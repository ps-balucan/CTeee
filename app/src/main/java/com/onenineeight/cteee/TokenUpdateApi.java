package com.onenineeight.cteee;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface TokenUpdateApi {
    @POST("fcmtoken")
    Call<Void> sendToken(@Body SubscribeToken subscribeToken);
}
