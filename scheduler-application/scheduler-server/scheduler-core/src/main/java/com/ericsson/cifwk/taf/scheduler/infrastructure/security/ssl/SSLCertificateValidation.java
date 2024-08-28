package com.ericsson.cifwk.taf.scheduler.infrastructure.security.ssl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public final class SSLCertificateValidation {

    private static final Logger LOGGER = LoggerFactory.getLogger(SSLCertificateValidation.class);

    private SSLCertificateValidation() {
    }

    public static void disable() {
        try {
            SSLContext sslc = SSLContext.getInstance("TLS");
            TrustManager[] trustManagerArray = {new NullX509TrustManager()};
            sslc.init(null, trustManagerArray, null);
            HttpsURLConnection.setDefaultSSLSocketFactory(sslc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new NullHostnameVerifier());
            LOGGER.info("Validation of SSL certificates disabled");
        } catch (Exception e) {
            LOGGER.error("Failed to disable validation of SSL certificates", e);
        }
    }

    private static class NullX509TrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            // always trust
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            // always trust
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    private static class NullHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

}
