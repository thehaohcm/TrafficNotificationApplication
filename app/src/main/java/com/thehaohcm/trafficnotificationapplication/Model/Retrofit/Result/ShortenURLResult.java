package com.thehaohcm.trafficnotificationapplication.Model.Retrofit.Result;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by thehaohcm on 10/13/17.
 */

public class ShortenURLResult {
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("typeError")
    @Expose
    private Integer typeError;
    @SerializedName("code")
    @Expose
    private String code;

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

    public Integer getTypeError() {
        return typeError;
    }

    public void setTypeError(Integer typeError) {
        this.typeError = typeError;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
