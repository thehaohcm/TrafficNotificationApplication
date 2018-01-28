package com.thehaohcm.trafficnotificationapplication.Model.Retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by thehaohcm on 9/24/17.
 */

public class FirstRegistration {
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("description")
    @Expose
    private String description;

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

}
