package com.thehaohcm.trafficnotificationapplication.controller;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.thehaohcm.trafficnotificationapplication.Interface.IPolylineFetchUrl;
import com.thehaohcm.trafficnotificationapplication.Activity.MainActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by thehaohcm on 6/27/17.
 */

// Fetches data from url passed
public class FetchUrl implements IPolylineFetchUrl {
    MainActivity mainActivity;
    private GoogleMap mMap;
    private List<List<HashMap<String, String>>> routes;
    private String url;
    private ProgressDialog dialog;
    private DataParser parser;
    private static ArrayList<LatLng> pointList=new ArrayList<>();
    private static Polyline polyline;
    private static int choosedItem;
    private boolean startFlag,stopFlag;//,randomFlag;
    private LatLng origin,dest;

    public FetchUrl(MainActivity mainActivity,GoogleMap mMap,LatLng origin,LatLng dest,boolean startFlag){//},boolean randomFlag) {
        this.mMap = mMap;
        this.mainActivity = mainActivity;
        this.stopFlag=false;
        this.startFlag=startFlag;
        if(startFlag==true)
            this.choosedItem=-1;
        else
            this.choosedItem=0;
        //this.randomFlag=randomFlag;
        this.origin=origin;
        this.dest=dest;
        this.url = getUrl(origin, dest);
        new FetchURL(this).execute(url);
    }

    public void setOrigin(LatLng origin){
        this.origin=origin;
    }

    public void setDest(LatLng dest){
        this.dest=dest;
    }

    public LatLng getOrigin(){
        return this.origin;
    }

    public LatLng getDest(){
        return this.dest;
    }

    private String getUrl(LatLng origin, LatLng dest) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        //Language
        String language = "language=vi";

        //Alternatives
        String alternatives = "alternatives=true";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&" + alternatives + "&" + language;

        return url;
    }

    @Override
    public void setPolyline(Polyline polyline){
        this.polyline=polyline;
        this.pointList.addAll(polyline.getPoints());
    }

    @Override
    public Polyline getPolyline(){
        return this.polyline;
    }

    @Override
    public void clearPolyline(){
        if(this.polyline!=null) {
            this.polyline.remove();
            this.polyline=null;
        }
    }

    public static void drawMapPolylines(LatLng tempLatLng){
        if(polyline!=null) {
            if (PolyUtil.containsLocation(tempLatLng, polyline.getPoints(), true)) {
                for (int i = 0; i < polyline.getPoints().size(); i++) {
                    LatLng latLng = polyline.getPoints().get(0);
                    if (latLng.equals(tempLatLng)) {
                        break;
                    }
                    polyline.getPoints().remove(i);
                }
            }
        }
    }

    public void clearPointMarker(){
        mainActivity.removePointsMarker();
    }

    public boolean checkDistanceAndSpeech(Location currentLocation){
        try {
            boolean checkFlag = true;
            //if(CalculateDistance.checkPointIntoTheLine(currentLocation,parser.getStartPointList(choosedItem).get(0),parser.getEndPointList(choosedItem).get(0))){
            int indexOfStreet = getIndexOfCurrentLocationInPolyLine(currentLocation, parser.getPointsStepsList(choosedItem));
            Log.i("index", String.valueOf(indexOfStreet));
            //if(indexOfStreet!=-1){
            int distance = 0;
            String strDistance = "";
            try {
                distance = CalculateDistance.getDistance(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), parser.getEndPointList(choosedItem).get(1));//.get(indexOfStreet));
                strDistance = String.valueOf(distance);
            } catch (Exception ex) {
                checkFlag = false;
            }

//            choosedItem=0; //có thể xóa - không cần quan tâm
            if (indexOfStreet >= parser.getRouteLegs(choosedItem).size() - 1) {
//                indexOfStreet=parser.getRouteLegs(choosedItem).size()-1;
                if (distance <= 100) {
                    if (checkFlag)
                        new Text2Speech(mainActivity, new String("Điểm đến của bạn cách " + distance + " mét nữa. Kết thúc hành trình")).speechText();
                    clearPolyline();
                    mainActivity.removePointsMarker();
                    this.stopFlag = true;
                } else {
                    if (checkFlag)
                        new Text2Speech(mainActivity, new String("Sau " + strDistance + " mét nữa, bạn sẽ đến điểm đến")).speechText();
                }
                return true;
            }
            //version cũ - sử dụng trên đường:
//            else
//                indexOfStreet++;
//            if (parser.getRouteLegs(choosedItem).size() > 1)
//                new Text2Speech(mainActivity, new String("Sau " + strDistance + " mét nữa, " + parser.getRouteLegs(choosedItem).get(indexOfStreet+1))).speechText();

            //cẩn thận
////                if(checkFlag)
////                    new Text2Speech(mainActivity, new String("Sau " + strDistance + " mét nữa, " + parser.getRouteLegs(choosedItem).get(indexOfStreet+1))).speechText();
////                else
////                    new Text2Speech(mainActivity, new String(parser.getRouteLegs(choosedItem).get(indexOfStreet+1))).speechText();
//
            //version mới - sử dụng trong nhà
            else {
                indexOfStreet++;
                if (parser.getRouteLegs(choosedItem).size() > 1)
                    if (checkFlag)
                        new Text2Speech(mainActivity, new String("Sau " + strDistance + " mét nữa, " + parser.getRouteLegs(choosedItem).get(indexOfStreet + 1))).speechText();
                    else
                        new Text2Speech(mainActivity, new String(parser.getRouteLegs(choosedItem).get(indexOfStreet + 1))).speechText();
            }
            return true;
//        }else{
//            new Text2Speech(mainActivity,new String("Bạn đang đi nhầm đường, vui lòng xem lại")).speechText();
//            return false;
//        }
        }catch (Exception ex){
            return false;
        }
    }

    private int getIndexOfCurrentLocationInPolyLine(Location currentLocation, ArrayList<ArrayList<LatLng>> pointsSteps){
        try {
            LatLng tempLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            for (int i = 0; i < pointsSteps.size(); i++) {
                ArrayList<LatLng> arrTemp = pointsSteps.get(i);
                for (LatLng latLng : arrTemp) {
                    Log.i("LatLngSteps", String.valueOf(latLng));
                    //if (CalculateDistance.getDistance(tempLatLng, latLng) <= 50) {
                    if (PolyUtil.containsLocation(tempLatLng, polyline.getPoints(), true)) {
                        return i;
                    }
                }
            }
        }catch (Exception ex){
            return -1;
        }
        return -1;
    }

    public boolean getStopFlag(){
        return this.stopFlag;
    }

    public ArrayList<String> getListRoutesLegs(){
        return parser.getRouteLegs(choosedItem);
    }

//    public void setRandomFlag(boolean randomFlag){
//        this.randomFlag=randomFlag;
//    }

    public boolean checkLocationOnPolyline(Location lastLocation,Location location){
        int indexLastLocation=getIndexOfCurrentLocationInPolyLine(lastLocation,parser.getPointsStepsList(choosedItem));
        LatLng latLngLoc=new LatLng(location.getLatitude(),location.getLongitude());
        if(indexLastLocation==-1)
            return false;

        for(int i=indexLastLocation;i<parser.getPointsStepsList().size();i++){
            ArrayList<ArrayList<LatLng>> arrTemp1=parser.getPointsStepsList(i);
            for(ArrayList<LatLng> arrTemp2:arrTemp1) {
                for (LatLng latLng : arrTemp2) {
                    if (CalculateDistance.getDistance(latLng, latLngLoc) <= 10) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void changeRoute(LatLng origin,LatLng dest,ArrayList<Location> trafficLocationList){
        this.stopFlag=true;
        try {
            if (parser.getDescriptionList().size() > 1) {//&&randomFlag){
//            Random r = new Random();
//            int choosed=r.nextInt(parser.getDescriptionList().size());
//            while(choosed==choosedItem){
//                choosed=r.nextInt(parser.getDescriptionList().size());
//            }
                this.origin = origin;
                this.dest = dest;


                for (int i = 0; i < parser.getDescriptionList().size(); i++) {
                    ArrayList<ArrayList<LatLng>> pointListTempList = parser.getPointsStepsList(i);
                    for (Location location : trafficLocationList) {
                        if (getIndexOfCurrentLocationInPolyLine(location, pointListTempList) != -1) {
                            choosedItem = i;
                            List<HashMap<String, String>> path = parser.getLatLngList(i);
                            clearPolyline();
                            ArrayList<LatLng> points;
                            PolylineOptions lineOptions = null;
                            points = new ArrayList<>();
                            lineOptions = new PolylineOptions();

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
                            lineOptions.color(Color.rgb(0, 139, 139));

                            // Drawing polyline in the Google Map for the i-th route
                            if (lineOptions != null) {
                                setPolyline(mMap.addPolyline(lineOptions));
                            } else {
                                Log.d("onPostExecute", "without Polylines drawn");
                            }

                            new Text2Speech(mainActivity, "Đã thay đổi tuyến đường").speechText();
                            this.stopFlag=false;
                            return;
                        }
                    }
                }

                //choosedItem=choosed;

                //check all route
                //...
            }
            new Text2Speech(mainActivity, "Không tìm thấy tuyến đường phù hợp").speechText();
            this.stopFlag=false;
            return;
        }catch (Exception ex) {
            new Text2Speech(mainActivity, "Không tìm thấy tuyến đường phù hợp").speechText();
        }
        this.stopFlag=false;
    }

    private class FetchURL extends AsyncTask<String, Void, Void> {
        IPolylineFetchUrl iPolylineFetchUrl;

        public FetchURL(IPolylineFetchUrl iPolylineFetchUrl){
            this.iPolylineFetchUrl = iPolylineFetchUrl;
        }

        private String downloadUrl(String strUrl) throws IOException {
            String data = "";
            InputStream iStream = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(strUrl);

                // Creating an http connection to communicate with url
                urlConnection = (HttpURLConnection) url.openConnection();

                // Connecting to url
                urlConnection.connect();

                // Reading data from url
                iStream = urlConnection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

                StringBuffer sb = new StringBuffer();

                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                data = sb.toString();
                Log.d("downloadUrl", data.toString());
                br.close();

            } catch (Exception e) {
                Log.d("Exception", e.toString());
            } finally {
                iStream.close();
                urlConnection.disconnect();
            }
            return data;
        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(mainActivity);
            dialog.setMessage("Vui lòng chờ");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected Void doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                JSONObject jObject;
                jObject = new JSONObject(data);
                parser = new DataParser();

                // Starts parsing data
                routes = parser.parse(jObject);

            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if(parser.getLength()==0){
                showToastNotFoundRoutes();
//            }
//            else if(parser.getLength()==1){
//                new ParserTask(mainActivity,mMap).execute();
            }else{
                ArrayList<String> descriptionList= parser.getDescriptionList();
                if(descriptionList.size()>1) {
                    if(choosedItem==-1)
                        showDialogRoute(descriptionList.toArray(new CharSequence[descriptionList.size()]));
                    else{
                        if(parser.getSizeAllLatLngList()>choosedItem)
                            drawMap(parser.getLatLngList(choosedItem),parser.getRouteLegs(choosedItem).get(0));
                        else{
                            drawMap(parser.getLatLngList(0),parser.getRouteLegs(0).get(0));
                            choosedItem=0;
                        }
                    }
                }else{
                    drawMap(parser.getLatLngList(0),parser.getRouteLegs(0).get(0));
                    choosedItem=0;
                }
            }

            if(dialog.isShowing())
                dialog.dismiss();
        }

        private void showToastNotFoundRoutes(){
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(mainActivity, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(mainActivity);
            }
            builder.setTitle("Không thể định tuyến")
                    .setMessage("Chương trình không thể tìm được tuyến đường đi đến vị trí đích")
                    .setNegativeButton("Bỏ qua", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                            dialog.dismiss();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

        private void showDialogRoute(final CharSequence[] items) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
            builder.setTitle("Chọn tuyến đường");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    dialog.dismiss();
                    choosedItem=item;
                    drawMap(parser.getLatLngList(item),parser.getRouteLegs(item).get(0));
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }

        private void drawMap(List<HashMap<String, String>> route,String instruction){
            iPolylineFetchUrl.clearPolyline();
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;
            points = new ArrayList<>();
            lineOptions = new PolylineOptions();

            // Fetching i-th route
            List<HashMap<String, String>> path = route;

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

            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null) {
                iPolylineFetchUrl.setPolyline(mMap.addPolyline(lineOptions));
            } else {
                Log.d("onPostExecute", "without Polylines drawn");
            }

            //new Text2Speech(mainActivity,instruction).speechText();
        }
    }
}