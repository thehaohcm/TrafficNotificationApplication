package com.thehaohcm.trafficnotificationapplication.controller;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.thehaohcm.trafficnotificationapplication.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by thehaohcm on 9/18/17.
 */

public class Camera {
    private static HashMap<LatLng,String> cameraIDList;//=new HashMap<LatLng, String>();
    private static HashMap<LatLng,Marker> cameraMarkerList=null;
    private static String miliSecond=String.valueOf(System.currentTimeMillis()-1000);
    private static HashMap<LatLng,String> cameraIDListTemp;
    private static Handler handlerSecond;
    public static void showCamera(Context context,GoogleMap mMap){
        if(cameraIDList==null||(cameraIDList!=null&&cameraIDList.size()==0)){
            cameraIDList=new HashMap<>();
            cameraIDList.put(new LatLng(10.800973, 106.738145), "587c782db807da0011e33d3b");
            cameraIDList.put(new LatLng(10.799619, 106.717883), "587c79e9b807da0011e33d3d");
            cameraIDList.put(new LatLng(10.806175, 106.754060), "56de42f611f398ec0c48127e");
            cameraIDList.put(new LatLng(10.826496, 106.760967), "56df82d4c062921100c143e0");
            cameraIDList.put(new LatLng(10.826474, 106.761096), "56df8274c062921100c143df");
            cameraIDList.put(new LatLng(10.849258, 106.773722), "56df8198c062921100c143dd");
            cameraIDList.put(new LatLng(10.849110, 106.774033), "56df81d8c062921100c143de");
            cameraIDList.put(new LatLng(10.849026, 106.774269), "56df8159c062921100c143dc");
            cameraIDList.put(new LatLng(10.849686, 106.753875), "57062d62e407c81100f18ee1");
            cameraIDList.put(new LatLng(10.825310, 106.714315), "58b5510817139d0010f35d4e");
            cameraIDList.put(new LatLng(10.821107, 106.693642), "58af9670bd82540010390c34");
            cameraIDList.put(new LatLng(10.820913, 106.695443), "58d7c20ac1e33c00112b321c");
            cameraIDList.put(new LatLng(10.802460, 106.698039), "587ed91db807da0011e33d4e");

            //Camera.addMarker(context,mMap);
        }

        Camera.addMarker(context,mMap);

//        if(cameraIDListTemp!=null)
//            cameraIDList.putAll(cameraIDListTemp);
//        int failCount=0;
//        cameraMarkerList=new ArrayList<>();
//        for (LatLng latLng : cameraIDList.keySet()) {
//            try {
//                Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title("camera").icon(BitmapDescriptorFactory.fromResource(R.drawable.camera)));
//                marker.setTitle("camera");
//                marker.setSnippet("CAMERA");
//                cameraMarkerList.add(marker);
//            }catch (Exception ex){
//                failCount++;
//            }
//        }
//        if(failCount>0){
//            if(failCount==cameraIDList.size())
//                Toast.makeText(context, "Không thể lấy toàn bộ hình ảnh camera giao thông", Toast.LENGTH_LONG).show();
//            else
//                Toast.makeText(context,"Không thể lấy một vài hình ảnh camera giao thông",Toast.LENGTH_LONG).show();
//        }
//
//        final Handler handlerSecond=new Handler();
//        handlerSecond.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                miliSecond =String.valueOf(System.currentTimeMillis()-1000);
//                handlerSecond.postDelayed(this,6000);
//            }
//        },6000);
    }

    public static void showCamerasListExtra(Context context,GoogleMap mMap,HashMap<LatLng,String> cameraIDListTemp){
        if(cameraIDListTemp!=null&&Camera.cameraIDListTemp!=cameraIDListTemp){
            removeCamera(false);
            cameraIDList.putAll(cameraIDListTemp);
            Camera.cameraIDListTemp=cameraIDListTemp;
            addMarker(context,mMap);
        }
    }

    public static void showCamerasListExtra(Context context,GoogleMap mMap){
        addMarker(context,mMap);
    }

    private static void addMarker(Context context,GoogleMap mMap){
        int failCount=0;
        if(cameraMarkerList==null)
            cameraMarkerList=new HashMap<LatLng, Marker>();
        for (LatLng latLng : cameraIDList.keySet()) {
            try {
                Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title("camera").icon(BitmapDescriptorFactory.fromResource(R.drawable.camera)));
                marker.setTitle("camera");
                marker.setSnippet("CAMERA");
                cameraMarkerList.put(latLng,marker);
            }catch (Exception ex){
                failCount++;
            }
        }
        if(failCount>0){
            if(failCount==cameraIDList.size())
                Toast.makeText(context, "Không thể lấy toàn bộ hình ảnh camera giao thông", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(context,"Không thể lấy một vài hình ảnh camera giao thông",Toast.LENGTH_LONG).show();
        }

        if(Camera.handlerSecond==null) {
            Camera.handlerSecond = new Handler();
            Camera.handlerSecond.postDelayed(new Runnable() {
                @Override
                public void run() {
                    miliSecond = String.valueOf(System.currentTimeMillis() - 1000);
                    Camera.handlerSecond.postDelayed(this, 6000);
                }
            }, 6000);
        }
    }

    public static void removeCamera(boolean flag){
        if(cameraIDList!=null){
            for (LatLng latLng:cameraMarkerList.keySet()) {
                Marker marker=cameraMarkerList.get(latLng);
                marker.remove();
            }
            if(flag)
                cameraMarkerList.clear();
        }
    }

    public static String getIdCamera(LatLng latLng){
        return cameraIDList.get(latLng);
    }

    public static String getLinkCameraPreview(LatLng latLng){
        String id=cameraIDList.get(latLng);
        return new String("http://giaothong.hochiminhcity.gov.vn/render/ImageHandler.ashx?id=" + id + "&t=" + miliSecond);
    }

    public static String getLinkCamera(LatLng latLng){
        String id=cameraIDList.get(latLng);
        return new String("http://giaothong.hochiminhcity.gov.vn/expandcameraplayer/?camId=" + id);
    }
}
