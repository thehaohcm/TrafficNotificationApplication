package com.thehaohcm.trafficnotificationapplication.controller;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by thehaohcm on 7/27/17.
 */

public class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
    // Parsing the data in non-ui thread
    private ProgressDialog dialog;
    private Activity activity;
    private  GoogleMap mMap;

    public ParserTask(Activity activity,GoogleMap mMap){
        this.activity=activity;
        this.mMap=mMap;
    }

    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(activity);
        this.dialog.setMessage("Please wait");
        this.dialog.setCanceledOnTouchOutside(false);
        this.dialog.show();
    }


    @Override
    protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

        JSONObject jObject;
        List<List<HashMap<String, String>>> routes = null;

        try {
            jObject = new JSONObject(jsonData[0]);
            Log.d("ParserTask", jsonData[0].toString());
            DataParser parser = new DataParser();
            Log.d("ParserTask", parser.toString());

            // Starts parsing data
            routes = parser.parse(jObject);
            Log.d("ParserTask", "Executing routes");
            Log.d("ParserTask", routes.toString());
            List<String> arrTmp = new ArrayList<String>();
//            arrTmp = parser.getDinhTuyen();
//            dinhtuyen = new String[arrTmp.size()];
//            dinhtuyen = arrTmp.toArray(dinhtuyen);
//            Log.i("Gia tri", String.valueOf(dinhtuyen.length));
//            for (String sp : dinhtuyen) {
//                Log.i("Gia tri", sp);
//            }

        } catch (Exception e) {
            Log.d("ParserTask", e.toString());
            e.printStackTrace();
        }
        return routes;
    }

    // Executes in UI thread, after the parsing process
    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> result) {
        ArrayList<LatLng> points;
        PolylineOptions lineOptions = null;

        Log.i("sizeofresult", String.valueOf(result.size()));

        // Traversing through all the routes
        for (int i = 0; i < result.size(); i++) {
            Log.i("resultstr", String.valueOf(result.get(i)));
            points = new ArrayList<>();
            lineOptions = new PolylineOptions();

            // Fetching i-th route
            List<HashMap<String, String>> path = result.get(i);

            // Fetching all the points in i-th route
            for (int j = 0; j < path.size(); j++) {
                HashMap<String, String> point = path.get(j);

                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);

                points.add(position);
            }

            // Adding all the points in the route to LineOptions
            lineOptions.addAll(points);
            lineOptions.width(10);
            lineOptions.color(Color.rgb(0,139,139));

            Log.d("onPostExecute", "onPostExecute lineoptions decoded");

        }

        // Drawing polyline in the Google Map for the i-th route
        if (lineOptions != null) {
            mMap.addPolyline(lineOptions);
        } else {
            Log.d("onPostExecute", "without Polylines drawn");
        }

        if (dialog.isShowing())
            dialog.dismiss();
    }

}
