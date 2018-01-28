package com.thehaohcm.trafficnotificationapplication.controller;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.thehaohcm.trafficnotificationapplication.Enum.MarkerType;
import com.thehaohcm.trafficnotificationapplication.Model.TramDoMuaItem;
import com.thehaohcm.trafficnotificationapplication.Model.TramDoTrieuItem;
import com.thehaohcm.trafficnotificationapplication.R;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalFloat;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

/**
 * Created by thehaohcm on 10/24/17.
 */

public class Flooding {
    private static String URL = "http://112.213.95.151:81/WebService.asmx?WSDL";
    private static String NAMESPACE = "http://udchcmc.local/";
    private static String SOAP_ACTION_DOMUA = "http://udchcmc.local/getDSTramdomua";
    private static String SOAP_ACTION_DOTRIEU = "http://udchcmc.local/getDSTramdotrieu";
    private static String METHOD_NAME_DOMUA = "getDSTramdomua";
    private static String METHOD_NAME_DOTRIEU = "getDSTramdotrieu";
    private static String PARAMETER_NAME = "key";
    private static String PARAMETER_VALUE="jud8$%6jP#!hfCV";

    private static HashMap<LatLng,TramDoMuaItem> tramDoMuaList=null;
    private static HashMap<LatLng,Marker> tramDoMuaMarkerList=null;

    private static HashMap<LatLng,TramDoTrieuItem> tramDoTrieuList=null;
    private static HashMap<LatLng,Marker> tramDoTrieuMarkerList=null;

    public static void showTramDoMuaMarker(Context context,GoogleMap mMap) {
        if(Flooding.tramDoMuaMarkerList==null||(Flooding.tramDoMuaMarkerList!=null&&Flooding.tramDoMuaMarkerList.size()==0)||
                Flooding.tramDoMuaList==null||(Flooding.tramDoMuaList!=null&&Flooding.tramDoMuaList.size()==0)) {
            Flooding.tramDoMuaMarkerList = new HashMap<>();

            //Đo mưa
            try {
                CallWebService callWebServiceTramDoMua = new CallWebService(MarkerType.TramDoMua);
                String result = callWebServiceTramDoMua.execute().get();

                Flooding.tramDoMuaList = new HashMap<>();

                Flooding.tramDoMuaMarkerList = new HashMap<>();
                JSONArray jsonArray = new JSONArray(result);
                int idtram = 0, idtrangthai = 0, status = 0;
                double lat = 0, lng = 0, mucnuoc = 0;
                String tentram = null, vitri = null, trangthai = null, viettat = null, thoidiem = null;
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        if (jsonObject.has("idtram"))
                            idtram = jsonObject.getInt("idtram");
                        if (jsonObject.has("tentram"))
                            tentram = jsonObject.getString("tentram");
                        if (jsonObject.has("lat"))
                            lat = jsonObject.getDouble("lat");
                        if (jsonObject.has("lng"))
                            lng = jsonObject.getDouble("lng");
                        if (jsonObject.has("vitri"))
                            vitri = jsonObject.getString("vitri");
                        if (jsonObject.has("mucnuoc"))
                            mucnuoc = jsonObject.getDouble("mucnuoc");
                        if (jsonObject.has("idtrangthai"))
                            idtrangthai = jsonObject.getInt("idtrangthai");
                        if (jsonObject.has("status"))
                            status = jsonObject.getInt("status");
                        if (jsonObject.has("trangthai"))
                            trangthai = jsonObject.getString("trangthai");
                        if (jsonObject.has("viettat"))
                            viettat = jsonObject.getString("viettat");
                        if (jsonObject.has("thoidiem"))
                            thoidiem = jsonObject.getString("thoidiem");

                        LatLng latLng = new LatLng(lat, lng);
                        TramDoMuaItem tramDoMuaItem = new TramDoMuaItem(idtram, tentram, lat, lng, vitri, mucnuoc, idtrangthai, status, trangthai, viettat, thoidiem);
                        Flooding.tramDoMuaList.put(latLng, tramDoMuaItem);

                        BitmapDescriptor bitmapDescriptor = getImageMessageReport(MarkerType.TramDoMua, context);
                        Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title("Trạm Đo Mưa").icon(bitmapDescriptor));
                        marker.setTitle("Trạm Đo Mưa");
                        marker.setSnippet("Trạm Đo Mưa");
                        Flooding.tramDoMuaMarkerList.put(latLng, marker);
                    } catch (Exception ex) {

                    }
                }
            } catch (Exception ex) {

            }
        }else{
            for(LatLng latlng:Flooding.tramDoMuaList.keySet()){
                BitmapDescriptor bitmapDescriptor = getImageMessageReport(MarkerType.TramDoMua, context);
                Marker marker = mMap.addMarker(new MarkerOptions().position(latlng).title("Trạm Đo Mưa").icon(bitmapDescriptor));
                marker.setTitle("Trạm Đo Mưa");
                marker.setSnippet("Trạm Đo Mưa");
                Flooding.tramDoMuaMarkerList.put(latlng,marker);
            }
        }
    }

    public static void showTramDoTrieuMarker(Context context,GoogleMap mMap) {
        if(Flooding.tramDoTrieuList==null||(Flooding.tramDoTrieuList!=null&&Flooding.tramDoTrieuList.size()==0)||
                Flooding.tramDoTrieuMarkerList==null||(Flooding.tramDoTrieuMarkerList!=null&&Flooding.tramDoTrieuMarkerList.size()==0)) {
            Flooding.tramDoTrieuMarkerList = new HashMap<>();

            //Đo triều
            try {
                CallWebService callWebServiceTramDoTrieu = new CallWebService(MarkerType.TramDoTrieu);
                String result = callWebServiceTramDoTrieu.execute().get();

                Flooding.tramDoTrieuList = new HashMap<>();

                Flooding.tramDoTrieuMarkerList = new HashMap<>();
                JSONArray jsonArray = new JSONArray(result);
                String idtramtrieu = null, tentramtrieu = null, viettat = null, vitri = null, tentrangthai = null, thoidiem = null;
                double lat = 0, lng = 0, muctrieu = 0;
                int status = 0, idtrangthai = 0;
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        if (jsonObject.has("idtramtrieu"))
                            idtramtrieu = jsonObject.getString("idtramtrieu");
                        if (jsonObject.has("tentramtrieu"))
                            tentramtrieu = jsonObject.getString("tentramtrieu");
                        if (jsonObject.has("viettat"))
                            viettat = jsonObject.getString("viettat");
                        if (jsonObject.has("lat"))
                            lat = jsonObject.getDouble("lat");
                        if (jsonObject.has("lng"))
                            lng = jsonObject.getDouble("lng");
                        if (jsonObject.has("vitri"))
                            vitri = jsonObject.getString("vitri");
                        if (jsonObject.has("status"))
                            status = jsonObject.getInt("status");
                        if (jsonObject.has("muctrieu"))
                            muctrieu = jsonObject.getDouble("muctrieu");
                        if (jsonObject.has("tentrangthai"))
                            tentrangthai = jsonObject.getString("tentrangthai");
                        if (jsonObject.has("idtrangthai"))
                            idtrangthai = jsonObject.getInt("idtrangthai");
                        if (jsonObject.has("thoidiem"))
                            thoidiem = jsonObject.getString("thoidiem");

                        LatLng latLng = new LatLng(lat, lng);
                        TramDoTrieuItem tramDoTrieuItem = new TramDoTrieuItem(idtramtrieu, tentramtrieu, viettat, lat, lng, vitri, status, muctrieu, tentrangthai, idtrangthai, thoidiem);
                        Flooding.tramDoTrieuList.put(latLng, tramDoTrieuItem);

                        BitmapDescriptor bitmapDescriptor = getImageMessageReport(MarkerType.TramDoTrieu, context);
                        Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title("Trạm Đo Triều").icon(bitmapDescriptor));
                        marker.setTitle("Trạm Đo Triều");
                        marker.setSnippet("Trạm Đo Triều");
                        Flooding.tramDoTrieuMarkerList.put(latLng, marker);
                    } catch (Exception ex) {

                    }
                }
            } catch (Exception ex) {

            }
        }else{
            for(LatLng latlng:Flooding.tramDoTrieuMarkerList.keySet()){
                BitmapDescriptor bitmapDescriptor = getImageMessageReport(MarkerType.TramDoTrieu, context);
                Marker marker = mMap.addMarker(new MarkerOptions().position(latlng).title("Trạm Đo Triều").icon(bitmapDescriptor));
                marker.setTitle("Trạm Đo Triều");
                marker.setSnippet("Trạm Đo Triều");
            }
        }
    }


    public static void removeTramDoTrieuMarkerList(){
        if(Flooding.tramDoTrieuMarkerList!=null&&Flooding.tramDoTrieuMarkerList.size()>0){
            for(LatLng latlng:Flooding.tramDoTrieuMarkerList.keySet()){
                Marker marker=Flooding.tramDoTrieuMarkerList.get(latlng);
                marker.remove();
            }
            Flooding.tramDoTrieuMarkerList.clear();
        }
    }

    public static void removeTramDoMuaMarkerList(){
        if(Flooding.tramDoMuaMarkerList!=null&&tramDoMuaMarkerList.size()>0){
            for(LatLng latlng:Flooding.tramDoMuaMarkerList.keySet()){
                Marker marker=Flooding.tramDoMuaMarkerList.get(latlng);
                marker.remove();
            }
            Flooding.tramDoMuaMarkerList.clear();
        }
    }

    static class CallWebService extends AsyncTask<Void, Void, String> {
        MarkerType markerType;
        public CallWebService(MarkerType markerType){
            this.markerType=markerType;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(Void... v) {
            String result = "";

            SoapObject soapObject = null;
            switch (markerType){
                case TramDoMua:
                    soapObject= new SoapObject(NAMESPACE, METHOD_NAME_DOMUA);
                    break;
                case TramDoTrieu:
                    soapObject= new SoapObject(NAMESPACE, METHOD_NAME_DOTRIEU);
                    break;
            }

            PropertyInfo propertyInfo = new PropertyInfo();
            propertyInfo.setName(PARAMETER_NAME);
            propertyInfo.setValue(PARAMETER_VALUE);
            propertyInfo.setType(String.class);

            soapObject.addProperty(propertyInfo);

            SoapSerializationEnvelope envelope =  new SoapSerializationEnvelope(SoapEnvelope.VER10);
            envelope.dotNet=true;
            envelope.setOutputSoapObject(soapObject);

            MarshalFloat marshal=new MarshalFloat();
            marshal.register(envelope);

            HttpTransportSE httpTransportSE = new HttpTransportSE(URL);

            try {
                switch (markerType){
                    case TramDoMua:
                        httpTransportSE.call(SOAP_ACTION_DOMUA, envelope);
                        break;
                    case TramDoTrieu:
                        httpTransportSE.call(SOAP_ACTION_DOTRIEU, envelope);
                        break;
                }
                SoapPrimitive soapPrimitive = (SoapPrimitive)envelope.getResponse();
                result = soapPrimitive.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }
    }

    private static BitmapDescriptor getImageMessageReport(MarkerType markerType, Context context){
        int height = 100;
        int width = 100;
        BitmapDrawable bitmapdraw = null;

        switch(markerType){
            case TramDoMua:
                bitmapdraw=(BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.icon_waterdrop);
                break;
            case TramDoTrieu:
                bitmapdraw=(BitmapDrawable) ContextCompat.getDrawable(context,R.drawable.icon_sunsetland);
                break;
        }

        Bitmap b=bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        return BitmapDescriptorFactory.fromBitmap(smallMarker);
    }

    public static TramDoMuaItem getTramDoMuaItem(LatLng latlng){
        return tramDoMuaList.get(latlng);
    }

    public static TramDoTrieuItem getTramDoTrieuItem(LatLng latlng){
        return tramDoTrieuList.get(latlng);
    }
}
