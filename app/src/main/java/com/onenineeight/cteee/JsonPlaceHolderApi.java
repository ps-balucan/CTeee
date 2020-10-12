package com.onenineeight.cteee;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface JsonPlaceHolderApi {
//    @GET("posts")
//    Call<List<Post>> getPosts();
//
//    @POST("helloworld")
//    //Call<Post> createPost(@Body Post post);
//    Call<LocationLogs> createPost(@Body LocationLogs locationLogs);
//    //Call<Post> createPost()


    @GET("checkinfectionreport")
    Call <Reports> getReport(@Query("last_update_time") String time);

    @GET("getlocationdata")
    Call <List<InfectedHistory>> getLocData(@Query("report_id") String id);

    @POST("sendlocationdata")
    Call <InfectedReport> postReport(@Body InfectedReport infectedReport);

    @Headers("InvocationType: Event")
    @POST("aggregatereport")
    Call <Void> postDPReport(@Header("Authorizer") String auth, @Body AggregateReport aggregateReport);

    @POST("fcmtoken")
    Call <Void> sendToken(@Body SubscribeToken subscribeToken);
}
