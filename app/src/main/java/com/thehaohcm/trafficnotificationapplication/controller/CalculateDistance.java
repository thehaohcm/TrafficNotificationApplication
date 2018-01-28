package com.thehaohcm.trafficnotificationapplication.controller;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by thehaohcm on 7/29/17.
 */

public class CalculateDistance {

    public static boolean checkDistanceRadius(Location mLastLocation,double lat2, double lon2) {
        double lon1 = mLastLocation.getLongitude();
        double lat1 = mLastLocation.getLatitude();
        int R = 6378137;
        double dLat = deg2rad(lat2 - lat1);
        double dLon = deg2rad(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c;
        if (d <= 1)
            return true;
        return false;
    }

    public static boolean checkPointIntoTheLine(Location currentLocation, LatLng startPoint,LatLng endPoint){
        LatLng current=new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
        double distance=getDistance(startPoint,endPoint)+5.0;
        if(getDistance(current,startPoint)<distance&&getDistance(current,endPoint)<distance)
            return true;
        return false;
    }

    public static int getDistance(LatLng a,LatLng b){
//        Log.i("LatLng",String.valueOf("a: "+a.latitude+" - "+a.longitude));
//        Log.i("LatLng",String.valueOf("b: "+b.latitude+" - "+b.longitude));
//        return (int) Math.sqrt(Math.pow(a.longitude - b.longitude, 2)
//                        + Math.pow(a.latitude - b.latitude, 2))*1000;


//        double earthRadius = 3958.75;
//        double latDiff = Math.toRadians(b.latitude-a.latitude);
//        double lngDiff = Math.toRadians(b.longitude-a.longitude);
//        double a_t = Math.sin(latDiff /2) * Math.sin(latDiff /2) +
//                Math.cos(Math.toRadians(a.latitude)) * Math.cos(Math.toRadians(b.latitude)) *
//                        Math.sin(lngDiff /2) * Math.sin(lngDiff /2);
//        double c = 2 * Math.atan2(Math.sqrt(a_t), Math.sqrt(1-a_t));
//        double distance = earthRadius * c;
//
//        int meterConversion = 1609;
//
//        return new Integer((int) (distance * meterConversion)).intValue();
        float[] result=new float[1];
        Location.distanceBetween(a.latitude,a.longitude,b.latitude,b.longitude,result);
        Log.i("dodai",String.valueOf(result[0]));
        return (int)result[0];
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    //::  This function converts decimal degrees to radians             :::
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    public static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    //::  This function converts radians to decimal degrees             :::
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    public static double rad2deg(double rad) {
        return (rad / Math.PI * 180.0);
    }
}
