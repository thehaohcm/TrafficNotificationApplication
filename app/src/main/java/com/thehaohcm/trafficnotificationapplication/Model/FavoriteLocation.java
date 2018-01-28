package com.thehaohcm.trafficnotificationapplication.Model;

import java.io.Serializable;

/**
 * Created by thehaohcm on 9/22/17.
 */

public class FavoriteLocation implements Serializable {
    int id;
    String name;
    double lat,lng;
    String description;
    String imageURL;

    public FavoriteLocation(int id,String name, double lat, double lng, String description, String imageURL) {
        this.id=id;
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.description = description;
        this.imageURL = imageURL;
    }

    public int getId(){
        return this.id;
    }

    public void setId(int id){
        this.id=id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
