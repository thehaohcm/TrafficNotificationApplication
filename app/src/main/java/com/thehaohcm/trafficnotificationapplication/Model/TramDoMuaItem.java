package com.thehaohcm.trafficnotificationapplication.Model;

/**
 * Created by thehaohcm on 10/24/17.
 */

public class TramDoMuaItem {
    int idtram;
    String tentram;
    double lat,lng;
    String vitri;
    double mucnuoc;
    int idtrangthai;
    int status;
    String trangthai;
    String viettat;
    String thoidiem;

    public TramDoMuaItem(int idtram, String tentram, double lat, double lng, String vitri, double mucnuoc, int idtrangthai, int status, String trangthai, String viettat, String thoidiem) {
        this.idtram = idtram;
        this.tentram = tentram;
        this.lat = lat;
        this.lng = lng;
        this.vitri = vitri;
        this.mucnuoc = mucnuoc;
        this.idtrangthai = idtrangthai;
        this.status = status;
        this.trangthai = trangthai;
        this.viettat = viettat;
        this.thoidiem = thoidiem;
    }

    public int getIdtram() {
        return idtram;
    }

    public void setIdtram(int idtram) {
        this.idtram = idtram;
    }

    public String getTentram() {
        return tentram;
    }

    public void setTentram(String tentram) {
        this.tentram = tentram;
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

    public double getMucnuoc() {
        return mucnuoc;
    }

    public void setMucnuoc(double mucnuoc) {
        this.mucnuoc = mucnuoc;
    }

    public int getIdtrangthai() {
        return idtrangthai;
    }

    public void setIdtrangthai(int idtrangthai) {
        this.idtrangthai = idtrangthai;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTrangthai() {
        return trangthai;
    }

    public void setTrangthai(String trangthai) {
        this.trangthai = trangthai;
    }

    public String getViettat() {
        return viettat;
    }

    public void setViettat(String viettat) {
        this.viettat = viettat;
    }

    public String getThoidiem() {
        return thoidiem;
    }

    public void setThoidiem(String thoidiem) {
        this.thoidiem = thoidiem;
    }
}
