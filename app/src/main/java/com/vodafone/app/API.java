package com.vodafone.app;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface API {

    @FormUrlEncoded
    @POST("fetch/offers/")
    Call<API_RESPONSE> fetchOffers(@Field("uid") String uid, @Field("topic") String topic);

    @FormUrlEncoded
    @POST("fetch/user/")
    Call<API_RESPONSE> fetchUser(@Field("uid") String uid);

    @FormUrlEncoded
    @POST("submit/data/")
    Call<API_RESPONSE> submitQuiz(@Field("uid") String uid,@Field("offer_id") String offer_id,@Field("offer_category") String offer_category,@Field("answer") String answer,@Field("type") String type);
}
