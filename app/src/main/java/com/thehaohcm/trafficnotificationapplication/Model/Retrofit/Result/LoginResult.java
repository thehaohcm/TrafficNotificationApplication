package com.thehaohcm.trafficnotificationapplication.Model.Retrofit.Result;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.thehaohcm.trafficnotificationapplication.Model.Retrofit.Account;

import java.util.List;

/**
 * Created by thehaohcm on 9/24/17.
 */

public class LoginResult {
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("items")
    @Expose
    private List<Account> accountList = null;
    @SerializedName("typeError")
    @Expose
    private Integer typeError;

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

    public List<Account> getItems() {
        return accountList;
    }

    public void setItems(List<Account> accountList) {
        this.accountList=accountList;
    }

    public Integer getTypeError() {
        return typeError;
    }

    public void setTypeError(Integer typeError) {
        this.typeError = typeError;
    }
}
