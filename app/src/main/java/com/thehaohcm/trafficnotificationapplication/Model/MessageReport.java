package com.thehaohcm.trafficnotificationapplication.Model;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by thehaohcm on 10/1/17.
 */

public class MessageReport {
    String phoneNumber;
    double latitude,longitude;
    Date createdDate,invalidDate;
    String createdDateStr,invalidDateStr;
    int type;
    String imageURL,messageContent;
    LatLng latLng;
    String messageTypeStr;
    ArrayList<String> confirmArray,violateArray;

    public MessageReport(String phoneNumber, double latitude, double longitude, String createdDate, String invalidDate, int type, String imageURL, String messageContent,ArrayList<String> confirmArray,ArrayList<String> violateArray) {
        this.phoneNumber = phoneNumber;
        this.latitude = latitude;
        this.longitude = longitude;
        this.changeLatLng();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat output = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        try {
            this.createdDate = sdf.parse(createdDate);
            this.createdDate.setTime(this.createdDate.getTime()+(7*3600000));
            this.invalidDate = sdf.parse(invalidDate);
            this.invalidDate.setTime(this.invalidDate.getTime()+(7*3600000));
            this.createdDateStr=output.format(this.createdDate);
            this.invalidDateStr=output.format(this.invalidDate);
        } catch (ParseException ex) {
            Log.i("loi_date", String.valueOf(ex));
        }
        this.type = type;
        this.changeMessageContentStr();
        this.imageURL = imageURL;
        this.messageContent = messageContent;
        this.confirmArray=confirmArray;
        this.violateArray=violateArray;
    }

    public MessageReport() {
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
        this.changeLatLng();
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
        this.changeLatLng();
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public void setCreatedDate(String createdDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat output = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        try {
            this.createdDate = sdf.parse(createdDate);
            this.createdDateStr=output.format(this.createdDate);
        } catch (ParseException ex) {
            Log.i("loi_date", String.valueOf(ex));
        }
    }

    public Date getInvalidDate() {
        return invalidDate;
    }

    public void setInvalidedDate(Date invalidDate) {
        this.invalidDate = invalidDate;
    }

    public void setInvalidedDate(String invalidedDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat output = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        try {
            this.invalidDate = sdf.parse(invalidedDate);
            this.invalidDateStr=output.format(this.invalidDate);
        } catch (ParseException ex) {
            Log.i("loi_date", String.valueOf(ex));
        }
    }

    public String getCreatedDateStr(){
        return this.createdDateStr;
    }

    public String getInvalidDateStr(){
        return this.invalidDateStr;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
        this.changeMessageContentStr();
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    private void changeLatLng(){
        this.latLng=new LatLng(this.latitude,this.longitude);
    }

    public LatLng getLatLng(){
        return this.latLng;
    }

    private void changeMessageContentStr(){
        switch(this.type){
            case 1:
                this.messageTypeStr="Va chạm xe";
                break;
            case 2:
                this.messageTypeStr="Cảnh sát giao thông";
                break;
            case 3:
                this.messageTypeStr="Camera giám sát";
                break;
            case 4:
                this.messageTypeStr="Công trình xây dựng";
                break;
            case 5:
                this.messageTypeStr="Tai nạn";
                break;
            case 6:
                this.messageTypeStr="Ngã quẹo";
                break;
            case 7:
                this.messageTypeStr="Cảnh báo";
                break;
            case 8:
                this.messageTypeStr="Ngập lụt";
                break;
        }
    }

    public String getMessageTypeStr(){
        return this.messageTypeStr;
    }

    public ArrayList<String> getConfirmArray() {
        return confirmArray;
    }

    public void setConfirmArray(ArrayList<String> confirmArray) {
        this.confirmArray = confirmArray;
    }

    public ArrayList<String> getViolateArray() {
        return violateArray;
    }

    public void setViolateArray(ArrayList<String> violateArray) {
        this.violateArray = violateArray;
    }

    public boolean checkReportedConfirmPhoneNumber(String phoneNumber){
        if(confirmArray!=null&&confirmArray.size()>0)
            for(String str:confirmArray)
                if(str.equals(phoneNumber))
                    return true;
        return false;
    }

    public boolean checkReportedViolatePhoneNumber(String phoneNumber){
        if(violateArray!=null&&violateArray.size()>0)
            for(String str:violateArray)
                if(str.equals(phoneNumber))
                    return true;
        return false;
    }

    public void addReportConfirm(String phoneNumber){
        if(confirmArray==null)
            confirmArray=new ArrayList<>();

        confirmArray.add(phoneNumber);
    }

    public void addReportViolate(String phoneNumber){
        if(violateArray==null)
            violateArray=new ArrayList<>();

        violateArray.add(phoneNumber);
    }
}
