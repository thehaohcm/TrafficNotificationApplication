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
import com.thehaohcm.trafficnotificationapplication.Model.News;
import com.thehaohcm.trafficnotificationapplication.R;

import java.util.ArrayList;

/**
 * Created by thehaohcm on 9/6/17.
 */

public class NewsAdapter extends ArrayAdapter<News> {
    private LayoutInflater inflater;

    public NewsAdapter(@NonNull Context context, @LayoutRes int resource,
                       @NonNull ArrayList<News> objects,LayoutInflater inflater) {
        super(context, resource, objects);
        this.inflater=inflater;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView==null){
            convertView=inflater.inflate(R.layout.item_row_news,null);
        }
        ImageView imgHinh= (ImageView) convertView.findViewById(R.id.imgHinh);
        TextView lblTitle= (TextView) convertView.findViewById(R.id.lblTitle);

        News news=getItem(position);
        lblTitle.setText(news.getTitle());

        //View Image
        Picasso.with(inflater.getContext()).load(news.getUrlImage()).into(imgHinh);

        return convertView;
    }
}
