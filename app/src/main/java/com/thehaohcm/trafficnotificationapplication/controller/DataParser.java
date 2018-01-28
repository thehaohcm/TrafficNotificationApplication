package com.thehaohcm.trafficnotificationapplication.controller;


import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by thehaohcm on 6/21/17.
 */

public class DataParser {
    /** Receives a JSONObject and returns a list of lists containing latitude and longitude */

    private ArrayList<ArrayList<String>> routesLegsList;
    private ArrayList<String> descriptionList;
    private int length;
    private List<List<HashMap<String, String>>> routes;
    private ArrayList<ArrayList<ArrayList<LatLng>>> pointsStepsList;
    private ArrayList<ArrayList<LatLng>> startPointList,endPointList;

    public int getLength(){
        return this.length;
    }

    public List<List<HashMap<String,String>>> parse(JSONObject jObject){
        JSONArray jRoutes,jLegs,jSteps;
        routesLegsList=new ArrayList<>();
        descriptionList=new ArrayList<>();
        pointsStepsList=new ArrayList<>();
        endPointList=new ArrayList<>();
        startPointList=new ArrayList<>();

        routes = new ArrayList<>() ;

        try {
            jRoutes = jObject.getJSONArray("routes");
            this.length=jRoutes.length();

            /** Traversing all routes */
            for(int i=0;i<jRoutes.length();i++){
                jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
                List path = new ArrayList<>();
                ArrayList<ArrayList<LatLng>> pointsStepsTemp2=new ArrayList<>();
                /** Traversing all legs */
                for(int j=0;j<jLegs.length();j++){
                    jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");
                    String distance=(String)((JSONObject)((JSONObject)jLegs.get(j)).get("distance")).get("text");
                    String duration=(String)((JSONObject)((JSONObject)jLegs.get(j)).get("duration")).get("text");
                    ArrayList<String> routesLegTemp=new ArrayList<String>();
                    ArrayList<LatLng> endPointTemp=new ArrayList<LatLng>();
                    ArrayList<LatLng> startPointTemp=new ArrayList<LatLng>();
                    String instrFirst="";
                    /** Traversing all steps */
                    for(int k=0;k<jSteps.length();k++){
                        ArrayList<LatLng> pointsStepsTemp=new ArrayList<LatLng>();
                        String polyline = "";
                        polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");

                        List<LatLng> list = decodePoly(polyline);

                        /** Traversing all points */
                        for(int l=0;l<list.size();l++){
                            HashMap<String, String> hm = new HashMap<>();
                            double lat=(list.get(l)).latitude;
                            double lng=(list.get(l)).longitude;
                            hm.put("lat", Double.toString(lat));
                            hm.put("lng", Double.toString(lng));
                            path.add(hm);
                            pointsStepsTemp.add(new LatLng(lat,lng));
                        }

                        String instr="";
                        instr=((JSONObject)jSteps.get(k)).getString("html_instructions");

                        instr = instr.replaceAll("<(.*?)\\>"," ");//Removes all items in brackets
                        instr = instr.replaceAll("<(.*?)\\\n"," ");//Must be undeneath
                        instr = instr.replaceFirst("(.*?)\\>", " ");//Removes any connected item to the last bracket
                        instr = instr.replaceAll("&nbsp;"," ");
                        instr = instr.replaceAll("&amp;"," ");
                        instr=instr.trim().replaceAll(" +", " ");

                        routesLegTemp.add(instr);
                        if(k==0) {
                            instrFirst = instr;
                        }

                        JSONObject startJSONLocation;
                        double startLat,startLng;
                        startJSONLocation=((JSONObject)((JSONObject)jSteps.get(k)).get("start_location"));
                        startLat=startJSONLocation.getDouble("lat");
                        startLng=startJSONLocation.getDouble("lng");
                        startPointTemp.add(new LatLng(startLat,startLng));

                        JSONObject endJSONLocation;
                        double endLat,endLng;
                        endJSONLocation=((JSONObject)((JSONObject)jSteps.get(k)).get("end_location"));
                        endLat=endJSONLocation.getDouble("lat");
                        endLng=endJSONLocation.getDouble("lng");
                        endPointTemp.add(new LatLng(endLat,endLng));
                        pointsStepsTemp2.add(pointsStepsTemp);
                    }
                    descriptionList.add(instrFirst+" ("+distance+" - "+duration+")");
                    routesLegsList.add(routesLegTemp);
                    routes.add(path);
                    startPointList.add(startPointTemp);
                    endPointList.add(endPointTemp);

                }
                pointsStepsList.add(pointsStepsTemp2);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }

        return routes;
    }

    public ArrayList<String> getRouteLegs(int index){
        if(index<routesLegsList.size())
            return routesLegsList.get(index);
        return null;
    }

    public ArrayList<String> getDescriptionList(){
        return this.descriptionList;
    }

    public List<HashMap<String, String>> getLatLngList(int index){
        return routes.get(index);
    }

    public int getSizeAllLatLngList(){
        return routes.size();
    }

    public ArrayList<LatLng> getStartPointList(int index){
        return this.startPointList.get(index);
    }

    public ArrayList<LatLng> getEndPointList(int index){
        return this.endPointList.get(index);
    }

    public ArrayList<ArrayList<LatLng>> getPointsStepsList(int index){
        return this.pointsStepsList.get(index);
    }

    public ArrayList<ArrayList<ArrayList<LatLng>>> getPointsStepsList(){
        return this.pointsStepsList;
    }

    /**
     * Method to decode polyline points
     * Courtesy : http://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
     * */
    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
}