package com.thehaohcm.trafficnotificationapplication.Interface.Retrofit;

import com.thehaohcm.trafficnotificationapplication.Model.Retrofit.Result.RegistrationResult;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by thehaohcm on 9/28/17.
 */

public interface RegistrationRepository {
    @FormUrlEncoded
    @POST("/registration/addUser")
    Call<RegistrationResult> registration(@Field("phoneNumber") String phoneNumber, @Field("password") String password,
                                          @Field("name") String name, @Field("email") String email);
}
