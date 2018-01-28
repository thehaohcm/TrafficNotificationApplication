package com.thehaohcm.trafficnotificationapplication.Interface.Retrofit;

import com.thehaohcm.trafficnotificationapplication.Model.Retrofit.Result.VerificationResult;
import com.thehaohcm.trafficnotificationapplication.Model.Retrofit.Result.VerificationTokenResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by thehaohcm on 9/28/17.
 */

public interface VerificationRepository {
    @GET("/registration/verification/{phoneNumber}/{token}")
    public Call<VerificationResult> checkToken(@Path("phoneNumber")String phoneNumber,@Path("token")String token);

    @GET("/registration/getToken/{phoneNumber}")
    public Call<VerificationTokenResult> getToken(@Path("phoneNumber")String phoneNumber);
}
