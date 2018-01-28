package com.thehaohcm.trafficnotificationapplication.Model.Retrofit.Result;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.thehaohcm.trafficnotificationapplication.Model.Retrofit.FavoriteLocation;

import java.util.List;

/**
 * Created by thehaohcm on 9/24/17.
 */

public class FavoriteLocationResult {
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("items")
    @Expose
    private List<FavoriteLocation> favoriteLocations = null;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<FavoriteLocation> getFavoriteLocations() {
        return favoriteLocations;
    }

    public void setFavoriteLocations(List<FavoriteLocation> favoriteLocations) {
        this.favoriteLocations = favoriteLocations;
    }
}
