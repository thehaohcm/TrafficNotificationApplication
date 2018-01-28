package com.thehaohcm.trafficnotificationapplication.Interface.Retrofit;

import com.thehaohcm.trafficnotificationapplication.Model.Retrofit.FirstRegistration;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by thehaohcm on 9/24/17.
 */

public interface FirstRegistrationRepository {
    @GET("/registration/checkPhoneNumber/{phoneNumber}")
    public Call<FirstRegistration> checkphoneNumber(@Path("phoneNumber")String phoneNumber);
}
