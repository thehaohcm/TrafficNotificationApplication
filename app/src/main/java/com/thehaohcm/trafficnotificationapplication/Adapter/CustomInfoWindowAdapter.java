package com.thehaohcm.trafficnotificationapplication.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;
import com.thehaohcm.trafficnotificationapplication.R;

/**
 * Created by thehaohcm on 9/15/17.
 */

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    LayoutInflater mInflater;
    String urlImage;
    Context context;

    public CustomInfoWindowAdapter(LayoutInflater i,  String urlImage, Context context ){
        mInflater = i;
        this.urlImage=urlImage;
        this.context=context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        View v = mInflater.inflate(R.layout.custom_camera_window, null);

        ImageView ivThumbnail = (ImageView) v.findViewById(R.id.ivThumbnail);
        Picasso.with(context).load(Uri.parse(urlImage)).resize(250,250).into(ivThumbnail);

        return v;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
