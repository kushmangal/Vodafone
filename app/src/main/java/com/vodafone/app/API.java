package com.vodafone.app;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface API {

    @FormUrlEncoded
    @POST("fetch/offers/")
    Call<API_RESPONSE> fetchOffers(@Field("uid") String uid, @Field("topic") String topic);
}
