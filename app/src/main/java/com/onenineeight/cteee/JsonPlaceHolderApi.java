package com.onenineeight.cteee;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface JsonPlaceHolderApi {

    @GET("checkinfectionreport")
    Call <Reports> getReport(@Query("last_update_time") String time);

    @GET("getlocationdata")
    Call <List<InfectedHistory>> getLocData(@Header("Authorizer") String auth, @Query("report_id") String id);

    @POST("sendlocationdata")
    Call <Void> postHistory(@Header("Authorizer") String auth, @Body List<BluetoothLog> infectedHistory);

    @Headers("InvocationType: Event")

    @POST("aggregatereport")
    Call <Void> postDPReport(@Header("Authorizer") String auth, @Body AggregateReport aggregateReport);

    @POST("fcmtoken")
    Call <Void> sendToken(@Body SubscribeToken subscribeToken);

    @POST("delete-user")
    Call <Void> deleteUser(@Header("Authorizer") String auth, @Body DeleteUserRequest deleteUserRequest);

    @POST("securitytest")
    Call <Void> testEncrypt(@Header("Authorizer") String auth, @Body AggregateReport aggregateReport);
}
