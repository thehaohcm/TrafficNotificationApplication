package com.thehaohcm.trafficnotificationapplication.Interface.Retrofit;

import com.thehaohcm.trafficnotificationapplication.Model.Retrofit.Result.ShortenURLResult;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by thehaohcm on 10/13/17.
 */

public interface ShortenURLRepository {
    @FormUrlEncoded
    @POST("/shorten")
    Call<ShortenURLResult> getShortenURL(@Field("latitude") String latitude, @Field("longitude") String longitude);
}
