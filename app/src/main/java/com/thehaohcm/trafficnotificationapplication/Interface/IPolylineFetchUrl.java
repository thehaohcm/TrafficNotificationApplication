package com.thehaohcm.trafficnotificationapplication.Interface;

import com.google.android.gms.maps.model.Polyline;

/**
 * Created by thehaohcm on 7/28/17.
 */

public interface IPolylineFetchUrl {

    public void setPolyline(Polyline polyline);
    public Polyline getPolyline();
    public void clearPolyline();
}
