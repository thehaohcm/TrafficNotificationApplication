package com.thehaohcm.trafficnotificationapplication.Model.Retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by thehaohcm on 9/28/17.
 */

public class VerificationToken {
    @SerializedName("VerificationTokenID")
    @Expose
    private Integer verificationTokenID;
    @SerializedName("PhoneNumber")
    @Expose
    private String phoneNumber;
    @SerializedName("Token")
    @Expose
    private String token;
    @SerializedName("ExpireDate")
    @Expose
    private String expireDate;
    @SerializedName("Actived")
    @Expose
    private Boolean actived;

    public Integer getVerificationTokenID() {
        return verificationTokenID;
    }

    public void setVerificationTokenID(Integer verificationTokenID) {
        this.verificationTokenID = verificationTokenID;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public Boolean getActived() {
        return actived;
    }

    public void setActived(Boolean actived) {
        this.actived = actived;
    }
}
