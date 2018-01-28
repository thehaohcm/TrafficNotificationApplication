package com.thehaohcm.trafficnotificationapplication.Interface.Retrofit;

import com.thehaohcm.trafficnotificationapplication.Model.Retrofit.Result.FavoriteLocationResult;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by thehaohcm on 9/24/17.
 */

public interface FavoriteLocationRepository {
    @GET("/{phoneNumber}/favoriteLocation")
    public Call<FavoriteLocationResult> getListFavoriteLocationResultByUser(@Path("phoneNumber") String phoneNumber);

    @FormUrlEncoded
    @POST("/{phoneNumber}/favoriteLocation")
    public Call<FavoriteLocationResult> createFavoriteLocationByUser(@Path("phoneNumber") String phoneNumber,
                                                                     @Field("name") String name,
                                                                     @Field("latitude") String latitude,
                                                                     @Field("longitude") String longitude,
                                                                     @Field("note") String note,
                                                                     @Field("imageURL") String imageURL);

    @FormUrlEncoded
    @PUT("/{phoneNumber}/favoriteLocation/{id}")
    public Call<FavoriteLocationResult> editFavoriteLocationById(@Path("phoneNumber") String phoneNumber,
                                                                 @Path("id") String id,
                                                                     @Field("name") String name,
                                                                     @Field("latitude") String latitude,
                                                                     @Field("longitude") String longitude,
                                                                     @Field("note") String note,
                                                                     @Field("imageURL") String imageURL);

    @DELETE("/{phoneNumber}/favoriteLocation/{id}")
    public Call<FavoriteLocationResult> deleteFavoriteLocationById(@Path("phoneNumber") String phoneNumber,
                                                                   @Path("id")String id);
}
