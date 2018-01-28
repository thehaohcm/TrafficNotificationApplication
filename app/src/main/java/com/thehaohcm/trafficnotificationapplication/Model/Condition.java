package com.thehaohcm.trafficnotificationapplication.Model;

import com.thehaohcm.trafficnotificationapplication.Interface.JSONPopulator;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by thehaohcm on 9/10/17.
 */

public class Condition implements JSONPopulator {
    private int code;
    private int temperature;
    private int highTemperature;
    private int lowTemperature;
    private String description;
    private String day;

    public int getCode() {
        return code;
    }

    public int getTemperature() {
        return temperature;
    }

    public int getHighTemperature() {
        return highTemperature;
    }

    public int getLowTemperature() {
        return lowTemperature;
    }

    public String getDescription() {
        return description;
    }

    public String getDay() {
        return day;
    }

    @Override
    public void populate(JSONObject data) {
        code = data.optInt("code");
        temperature = data.optInt("temp");
        highTemperature = data.optInt("high");
        lowTemperature = data.optInt("low");
        description = data.optString("text");
        day = data.optString("day");
        switch (day){
            case "Sun":
                day="Chủ Nhật";
                break;
            case "Mon":
                day="Thứ Hai";
                break;
            case "Tue":
                day="Thứ Ba";
                break;
            case "Wed":
                day="Thứ Tư";
                break;
            case "Thu":
                day="Thứ Năm";
                break;
            case "Fri":
                day="Thứ Sáu";
                break;
            case "Sat":
                day="Thứ Bảy";
                break;
        }
    }

    @Override
    public JSONObject toJSON() {
        JSONObject data = new JSONObject();

        try {
            data.put("code", code);
            data.put("temp", temperature);
            data.put("high", highTemperature);
            data.put("low", lowTemperature);
            data.put("text", description);
            data.put("day", day);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return data;
    }
}

