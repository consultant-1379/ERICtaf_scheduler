package com.ericsson.oss.axis.interfaces.scheduler;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Adapter for TAF scheduler REST service - see https://taf-scheduler.lmera.ericsson.se/api/swagger-ui.html
 */
public interface TafSchedulerAdapter {

    @POST("/api/login")
    Call<Void> authenticate(@Body AuthRequest authRequest);

    @GET("/api/isos/{isoId}/schedules")
    Call<TafScheduleInfo[]> getIsoSchedules(@Path("isoId") String isoName);

    @GET("/api/schedules/approved")
    Call<TafScheduleInfo[]> getApprovedSchedulesByType(@Query(value = "product") String product, @Query(value = "drop") String drop,
                                                       @Query(value = "type") String scheduleType);

    @POST("/api/schedules/executions")
    Call<Void> createScheduleExecution(@Query(value = "scheduleId") long scheduleId,
                                       @Query(value = "productIsoVersion") String productIsoVersion,
                                       @Query(value = "testwareIsoVersion") String testwareIsoVersion);

    @GET("/api/schedules/{scheduleId}")
    Call<TafScheduleInfo> getScheduleById(@Path("scheduleId") Long scheduleId);

    @GET("/api/schedules/approved/kgb")
    Call<TafScheduleInfo[]> getApprovedKGBSchedules(@Query(value = "team") String team);

}
