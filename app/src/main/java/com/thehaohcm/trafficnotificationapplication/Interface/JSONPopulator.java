package com.thehaohcm.trafficnotificationapplication.Interface;

import org.json.JSONObject;

/**
 * Created by thehaohcm on 9/10/17.
 */

public interface JSONPopulator {
    void populate(JSONObject data);

    JSONObject toJSON();
}
