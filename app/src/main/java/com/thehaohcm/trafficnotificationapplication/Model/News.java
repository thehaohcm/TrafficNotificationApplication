package com.thehaohcm.trafficnotificationapplication.Model;

import android.graphics.Bitmap;


/**
 * Created by thehaohcm on 9/5/17.
 */

public class News {
    String title;
    String link;
    String urlImage;
    Bitmap Image;

    public News(String title, String link, String urlImage) {
        this.title = title;
        this.link = link;
        this.urlImage = urlImage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public Bitmap getImage() {
        return Image;
    }

    public void setImage(Bitmap image) {
        Image = image;
    }
}
