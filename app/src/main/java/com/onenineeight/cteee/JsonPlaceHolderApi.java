package com.onenineeight.cteee;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
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
    Call <ReportID> getLocData(@Query("report_id") String id);

    @POST("sendlocationdata")
    Call <InfectedReport> postReport(@Body InfectedReport infectedReport);

    @POST("aggregatereport")
    Call <AggregateReport> postDPReport(@Body AggregateReport aggregateReport);
}
