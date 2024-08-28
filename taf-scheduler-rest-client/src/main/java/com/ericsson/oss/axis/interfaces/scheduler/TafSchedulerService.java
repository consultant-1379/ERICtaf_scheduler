package com.ericsson.oss.axis.interfaces.scheduler;

import com.ericsson.oss.axis.common.RestServiceUtils;
import com.ericsson.oss.axis.interfaces.scheduler.exceptions.TafSchedulerException;
import com.ericsson.oss.axis.interfaces.scheduler.exceptions.TafSchedulerItemNotFoundException;
import com.ericsson.oss.axis.types.ScheduleType;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.squareup.okhttp.OkHttpClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.Arrays;
import java.util.List;

public class TafSchedulerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TafSchedulerService.class);

    private final String username;
    private final String password;
    private final String endpointAddress;
    private boolean triedToAuthenticate = false;

    private TafSchedulerAdapter adapter;

    static {
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);
    }

    public TafSchedulerService(String baseEndpointAddress) {
        this(baseEndpointAddress, "", "");
    }

    public TafSchedulerService(String baseEndpointAddress, String username, String password) {
        Preconditions.checkArgument(StringUtils.isNotBlank(baseEndpointAddress), "TAF Scheduler base URL cannot be blank");

        this.username = username;
        this.password = password;
        this.endpointAddress = RestServiceUtils.cleanEndpointAddress(baseEndpointAddress);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(endpointAddress)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getSslProblemTolerantClient())
                .build();

        this.adapter = retrofit.create(TafSchedulerAdapter.class);
    }

    public List<TafScheduleInfo> getSchedules(String productName, String drop, ScheduleType scheduleType) throws TafSchedulerException {
        Call<TafScheduleInfo[]> scheduleRequest = getAdapter().getApprovedSchedulesByType(productName, drop, scheduleType.getTypeId());
        Response<TafScheduleInfo[]> response = processRequest(scheduleRequest,
                String.format("Failed to retrieve schedules for product '%s', drop '%s', schedule type '%s'",
                        productName, drop, scheduleType.getTypeId()));
        return Arrays.asList(response.body());
    }

    public TafScheduleInfo getSchedule(String productName, String drop, ScheduleType scheduleType,
                                       String scheduleName, String scheduleVersion) throws TafSchedulerException {
        List<TafScheduleInfo> schedules = getSchedules(productName, drop, scheduleType);
        Optional<TafScheduleInfo> scheduleHolder = findSchedule(schedules, scheduleName, scheduleVersion);
        if (scheduleHolder.isPresent()) {
            return scheduleHolder.get();
        } else {
            throw new TafSchedulerItemNotFoundException();
        }
    }

    public List<TafScheduleInfo> getKgbSchedules(String team) throws TafSchedulerException {
        Call<TafScheduleInfo[]> sceduleRequest = getAdapter().getApprovedKGBSchedules(team);
        Response<TafScheduleInfo[]> response = processRequest(sceduleRequest, String.format("Failed to retrieve KGB schedules for team %s", team));
        return Arrays.asList(response.body());
    }

    public TafScheduleInfo getKgbSchedule(String team, String scheduleName, String scheduleVersion) throws TafSchedulerException {
        List<TafScheduleInfo> schedules = getKgbSchedules(team);
        Optional<TafScheduleInfo> scheduleHolder = findSchedule(schedules, scheduleName, scheduleVersion);
        if (scheduleHolder.isPresent()) {
            return scheduleHolder.get();
        } else {
            throw new TafSchedulerItemNotFoundException();
        }
    }

    public void registerScheduleExecution(TafScheduleInfo tafScheduleInfo, String productIsoVersion, String testwareIsoVersion) throws TafSchedulerException {
        Call<Void> registryCall = getAdapter().createScheduleExecution(tafScheduleInfo.getId(), productIsoVersion, testwareIsoVersion);
        processRequest(registryCall, String.format("Failed to register schedule '%s' (ID=%s) execution for " +
                        "product ISO version '%s' and testware ISO version '%s'",
                tafScheduleInfo.getName(), tafScheduleInfo.getId(), productIsoVersion, testwareIsoVersion));
    }

    public TafScheduleInfo getScheduleById(Long scheduleId) throws TafSchedulerException {
        Call<TafScheduleInfo> scheduleByIdCall = getAdapter().getScheduleById(scheduleId);
        Response<TafScheduleInfo> response = processRequest(scheduleByIdCall,
                String.format("Failed to retrieve schedule by ID '%d'", scheduleId));
        return response.body();
    }

    @VisibleForTesting
    Optional<TafScheduleInfo> findSchedule(List<TafScheduleInfo> schedules, String scheduleName, String scheduleVersion)
            throws TafSchedulerItemNotFoundException {
        for (TafScheduleInfo schedule : schedules) {
            if (StringUtils.equals(scheduleName, schedule.getName())) {
                if ((StringUtils.isBlank(scheduleVersion) && schedule.isLastVersion())
                        || (StringUtils.equals(scheduleVersion, String.valueOf(schedule.getVersion())))) {
                    return Optional.of(schedule);
                }
            }
        }
        return Optional.absent();
    }

    protected void authenticateIfNeeded() throws TafSchedulerException {
        if (triedToAuthenticate) {
            return;
        } else if (StringUtils.isBlank(username)) {
            LOGGER.debug("Username is undefined for TAF Scheduler (global configuration), therefore bypassing authentication");
            triedToAuthenticate = true;
            return;
        }

        try {
            Call<Void> authCall = getAdapter().authenticate(AuthRequest.with(username, password));
            processRequest(authCall, String.format("Failed to authCall as '%s'", username));
        } finally {
            triedToAuthenticate = true;
        }
    }

    private <T> Response<T> processRequest(Call<T> call, String msgIfRequestFails) throws TafSchedulerException {
        try {
            Response<T> response = call.execute();
            if (!response.isSuccess()) {
                String fullErrorMsg = msgIfRequestFails + " - return code was " + response.code();
                LOGGER.error(fullErrorMsg);
                throw new TafSchedulerException(fullErrorMsg);
            }
            return response;
        } catch (IOException e) {
            LOGGER.error(msgIfRequestFails, e);
            throw new TafSchedulerException(msgIfRequestFails, e);
        }
    }

    private static OkHttpClient getSslProblemTolerantClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) { //NOSONAR
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) { //NOSONAR
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            java.security.cert.X509Certificate[] securityCerts = {};
                            return securityCerts;
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient okHttpClient = new OkHttpClient();
            okHttpClient.setSslSocketFactory(sslSocketFactory);
            okHttpClient.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    TafSchedulerAdapter getAdapter() {
        return adapter;
    }

}
