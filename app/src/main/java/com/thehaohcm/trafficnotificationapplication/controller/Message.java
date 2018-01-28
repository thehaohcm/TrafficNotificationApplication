package com.thehaohcm.trafficnotificationapplication.controller;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.thehaohcm.trafficnotificationapplication.Model.MessageReport;
import com.thehaohcm.trafficnotificationapplication.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by thehaohcm on 10/1/17.
 */

public class Message {
    private static HashMap<LatLng, MessageReport> messageReportList;
    private static HashMap<LatLng,Marker> messageMarkerList=null;

    public static void addMessageReportList(HashMap<LatLng, MessageReport> messageReportList){
        if(Message.messageReportList==null)
            Message.messageReportList=new HashMap<>();

        if(Message.messageReportList!=messageReportList)
            Message.messageReportList=messageReportList;
    }

    public static void addMessageReport(MessageReport messageReport){
        if(Message.messageReportList==null)
            Message.messageReportList=new HashMap<>();

        Message.messageReportList.put(messageReport.getLatLng(),messageReport);
    }

    public static void showMessageReportMarkerList(Context context,GoogleMap mMap){
        if(Message.messageReportList!=null&&Message.messageReportList.size()>0) {
            Message.removeMessageReportMarkerList(true);

            Message.messageMarkerList = new HashMap<>();
            for (Map.Entry<LatLng, MessageReport> entry : messageReportList.entrySet()) {
                LatLng latLng = entry.getKey();
                MessageReport messageReport = entry.getValue();

                showOneMessageReportMarker(latLng,messageReport,context,mMap);
            }
        }
    }

    public static void showMessageReportMarker(LatLng latLng,Context context,GoogleMap mMap){
        if(Message.messageMarkerList==null)
            Message.messageMarkerList=new HashMap<>();
        Marker marker=Message.messageMarkerList.get(latLng);
        if(marker==null){
            MessageReport messageReport=Message.messageReportList.get(latLng);
            showOneMessageReportMarker(latLng,messageReport,context,mMap);
        }
    }

    private static void showOneMessageReportMarker(LatLng latLng,MessageReport messageReport,Context context,GoogleMap mMap){
        BitmapDescriptor bitmapDescriptor = getImageMessageReport(context,messageReport);
        Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title("report").icon(bitmapDescriptor));
        marker.setTitle("report");
        marker.setSnippet("REPORT");
        messageMarkerList.put(latLng, marker);
    }

    public static void removeMessageReportMarkerList(boolean flag){
        if(Message.messageMarkerList!=null&&Message.messageMarkerList.size()>0){
            for (LatLng latLng:Message.messageMarkerList.keySet()) {
                Marker marker=Message.messageMarkerList.get(latLng);
                marker.remove();
            }
            if(flag)
                Message.messageMarkerList.clear();
        }
    }

    private static BitmapDescriptor getImageMessageReport(Context context,MessageReport messageReport){
        int height = 100;
        int width = 100;
        BitmapDrawable bitmapdraw = null;

        switch(messageReport.getType()){
            case 1:
                bitmapdraw=(BitmapDrawable) ContextCompat.getDrawable(context,R.drawable.icon_traffic_jam);
                break;
            case 2:
                bitmapdraw=(BitmapDrawable) ContextCompat.getDrawable(context,R.drawable.icon_police);
                break;
            case 3:
                bitmapdraw=(BitmapDrawable) ContextCompat.getDrawable(context,R.drawable.icon_camera);
                break;
            case 4:
                bitmapdraw=(BitmapDrawable) ContextCompat.getDrawable(context,R.drawable.icon_under_construction);
                break;
            case 5:
                bitmapdraw=(BitmapDrawable) ContextCompat.getDrawable(context,R.drawable.icon_accident);
                break;
            case 6:
                bitmapdraw=(BitmapDrawable) ContextCompat.getDrawable(context,R.drawable.icon_intersection);
                break;
            case 7:
                bitmapdraw=(BitmapDrawable) ContextCompat.getDrawable(context,R.drawable.icon_warning);
                break;
            case 8:
                bitmapdraw=(BitmapDrawable) ContextCompat.getDrawable(context,R.drawable.icon_flooding);
                break;
        }

        Bitmap b=bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        return BitmapDescriptorFactory.fromBitmap(smallMarker);
    }

    public static MessageReport getMessageReport(LatLng latLng){
        return messageReportList.get(latLng);
    }

    public static int getSizeOfMessageMarkerList(){
        return messageMarkerList.size();
    }

}
