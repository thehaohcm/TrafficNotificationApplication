package com.thehaohcm.trafficnotificationapplication.controller;

/**
 * Created by thehaohcm on 9/24/17.
 */

public class URLConnection {
    private static String URLConnection="http://192.168.43.125:5000";//"http://192.168.1.55:5000";
    public static String getURLConnection(){
        return URLConnection;
    }
    private static String phoneNumber="";
    public static void setPhoneNumber(String phone){phoneNumber=phone;}
    public static String getPhoneNumber(){return phoneNumber;}
}
