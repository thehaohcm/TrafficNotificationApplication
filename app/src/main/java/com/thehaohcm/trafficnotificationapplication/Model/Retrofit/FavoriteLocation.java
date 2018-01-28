package com.thehaohcm.trafficnotificationapplication.Model.Retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by thehaohcm on 9/24/17.
 */

public class FavoriteLocation implements Serializable {
    @SerializedName("FavoriteID")
    @Expose
    private Integer favoriteID;
    @SerializedName("PhoneNumber")
    @Expose
    private String phoneNumber;
    @SerializedName("NameFavoriteLocation")
    @Expose
    private String nameFavoriteLocation;
    @SerializedName("Latitude")
    @Expose
    private Double latitude;
    @SerializedName("Longitude")
    @Expose
    private Double longitude;
    @SerializedName("Note")
    @Expose
    private String note;
    @SerializedName("ImageURL")
    @Expose
    private String imageURL;
    @SerializedName("DateTime")
    @Expose
    private String dateTime;

    public Integer getFavoriteID() {
        return favoriteID;
    }

    public void setFavoriteID(Integer favoriteID) {
        this.favoriteID = favoriteID;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getNameFavoriteLocation() {
        return nameFavoriteLocation;
    }

    public void setNameFavoriteLocation(String nameFavoriteLocation) {
        this.nameFavoriteLocation = nameFavoriteLocation;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

}
