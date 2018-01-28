package com.thehaohcm.trafficnotificationapplication.Model;

/**
 * Created by thehaohcm on 10/25/17.
 */

public class TramDoTrieuItem {
    String idtramtrieu;
    String tentramtrieu;
    String viettat;
    double lat,lng;
    String vitri;
    int status;
    double muctrieu;
    String tentrangthai;
    int idtrangthai;
    String thoidiem;

    public TramDoTrieuItem(String idtramtrieu, String tentramtrieu, String viettat, double lat, double lng, String vitri, int status, double muctrieu, String tentrangthai, int idtrangthai, String thoidiem) {
        this.idtramtrieu = idtramtrieu;
        this.tentramtrieu = tentramtrieu;
        this.viettat = viettat;
        this.lat = lat;
        this.lng = lng;
        this.vitri = vitri;
        this.status = status;
        this.muctrieu = muctrieu;
        this.tentrangthai = tentrangthai;
        this.idtrangthai = idtrangthai;
        this.thoidiem = thoidiem;
    }

    public String getIdtramtrieu() {
        return idtramtrieu;
    }

    public void setIdtramtrieu(String idtramtrieu) {
        this.idtramtrieu = idtramtrieu;
    }

    public String getTentramtrieu() {
        return tentramtrieu;
    }

    public void setTentramtrieu(String tentramtrieu) {
        this.tentramtrieu = tentramtrieu;
    }

    public String getViettat() {
        return viettat;
    }

    public void setViettat(String viettat) {
        this.viettat = viettat;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getVitri() {
        return vitri;
    }

    public void setVitri(String vitri) {
        this.vitri = vitri;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public double getMuctrieu() {
        return muctrieu;
    }

    public void setMuctrieu(double muctrieu) {
        this.muctrieu = muctrieu;
    }

    public String getTentrangthai() {
        return tentrangthai;
    }

    public void setTentrangthai(String tentrangthai) {
        this.tentrangthai = tentrangthai;
    }

    public int getIdtrangthai() {
        return idtrangthai;
    }

    public void setIdtrangthai(int idtrangthai) {
        this.idtrangthai = idtrangthai;
    }

    public String getThoidiem() {
        return thoidiem;
    }

    public void setThoidiem(String thoidiem) {
        this.thoidiem = thoidiem;
    }
}
