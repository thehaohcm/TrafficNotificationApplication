package com.thehaohcm.trafficnotificationapplication.Interface.Retrofit;

import com.thehaohcm.trafficnotificationapplication.Model.Retrofit.Result.LoginResult;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by thehaohcm on 9/24/17.
 */

public interface LoginRepository {
    @FormUrlEncoded
    @POST("/login")
    Call<LoginResult> checkLogin(@Field("phoneNumber") String phoneNumber,@Field("password") String password);
}
