package com.thehaohcm.trafficnotificationapplication.Adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.thehaohcm.trafficnotificationapplication.Model.Retrofit.FavoriteLocation;
import com.thehaohcm.trafficnotificationapplication.R;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by thehaohcm on 9/22/17.
 */

public class FavoriteLocationAdapter extends ArrayAdapter<FavoriteLocation> {
    private LayoutInflater inflater;

    public FavoriteLocationAdapter(@NonNull Context context, @LayoutRes int resource,
                       @NonNull ArrayList<FavoriteLocation> objects, LayoutInflater inflater) {
        super(context, resource, objects);
        this.inflater=inflater;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView==null){
            convertView=inflater.inflate(R.layout.item_row_favlocation,null);
        }
        ImageView imgHinh= (ImageView) convertView.findViewById(R.id.imgHinhFavLocation);
        TextView txtTitle= (TextView) convertView.findViewById(R.id.txtTitleFavLocation);
        TextView txtDescription= (TextView) convertView.findViewById(R.id.txtDescriptionFavLocation);

        FavoriteLocation favoriteLocation=getItem(position);
        txtTitle.setText(favoriteLocation.getNameFavoriteLocation());
        txtDescription.setText(favoriteLocation.getNote());

        //View Image
        if(favoriteLocation.getImageURL()!=null&&!favoriteLocation.getImageURL().equals(""))
            Picasso.with(inflater.getContext()).load(favoriteLocation.getImageURL()).into(imgHinh);

        return convertView;
    }
}
