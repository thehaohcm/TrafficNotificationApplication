package com.thehaohcm.trafficnotificationapplication.Model.Retrofit.Result;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.thehaohcm.trafficnotificationapplication.Model.Retrofit.VerificationToken;

import java.util.List;

/**
 * Created by thehaohcm on 9/28/17.
 */

public class VerificationTokenResult {
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("items")
    @Expose
    private List<VerificationToken> verificationTokenList = null;
    @SerializedName("typeError")
    @Expose
    private Integer typeError;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public List<VerificationToken> getverificationTokenList() {
        return verificationTokenList;
    }

    public void setverificationTokenList(List<VerificationToken> verificationTokenList) {
        this.verificationTokenList = verificationTokenList;
    }

    public Integer getTypeError() {
        return typeError;
    }

    public void setTypeError(Integer typeError) {
        this.typeError = typeError;
    }

    public void setDescription(String description){
        this.description=description;
    }

    public String getDescription(){return this.description;}
}
