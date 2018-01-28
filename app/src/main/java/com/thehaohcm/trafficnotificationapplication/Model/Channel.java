package com.thehaohcm.trafficnotificationapplication.Model;

import com.thehaohcm.trafficnotificationapplication.Interface.JSONPopulator;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by thehaohcm on 9/10/17.
 */

public class Channel implements JSONPopulator {
    private Units units;
    private Item item;
    private String location;
    private String link;

    public Units getUnits() {
        return units;
    }

    public Item getItem() {
        return item;
    }

    public String getLocation() {
        return location;
    }

    public String getLink(){
        return this.link;
    }

    @Override
    public void populate(JSONObject data) {

        units = new Units();
        units.populate(data.optJSONObject("units"));

        link= data.optString("link");
        link=link.substring(link.indexOf("*")+1);

        item = new Item();
        item.populate(data.optJSONObject("item"));

        JSONObject locationData = data.optJSONObject("location");

        String region = locationData.optString("region");
        String country = locationData.optString("country");

        location = String.format("%s, %s", locationData.optString("city"), (country.length() != 0 ? country:region));
    }

    @Override
    public JSONObject toJSON() {

        JSONObject data = new JSONObject();

        try {
            data.put("units", units.toJSON());
            data.put("item", item.toJSON());
            data.put("requestLocation", location);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return data;
    }

}
