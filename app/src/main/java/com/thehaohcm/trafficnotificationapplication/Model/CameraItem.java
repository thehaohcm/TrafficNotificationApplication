package com.thehaohcm.trafficnotificationapplication.Model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by thehaohcm on 9/16/17.
 */

public class CameraItem {
    String id;
    double latitude;
    double longitude;
    LatLng latLng;

    public CameraItem(String id, double latitude, double longitude) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.latLng=new LatLng(latitude,longitude);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public LatLng getLatLng(){
        return this.latLng;
    }
}
