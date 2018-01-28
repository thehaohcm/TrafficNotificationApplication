package com.thehaohcm.trafficnotificationapplication.controller;

import android.app.Application;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

/**
 * Created by thehaohcm on 6/30/17.
 */

public class SocketIO extends Application {
//    private static final SocketIO ourInstance = new SocketIO();
    private Socket mSocket;
    {
        try {
            mSocket= IO.socket(URLConnection.getURLConnection());
            mSocket.connect();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

    }

    public Socket getSocket(){
        return mSocket;
    }

//    //private static DataSocketIO dataSocketIO;
//
//
////    public static SocketIO getInstance() {
////        return ourInstance;
////    }
//
////    private SocketIO(Activity activity) throws URISyntaxException {
////        mSocket= IO.socket("http://192.168.1.35:5000/");
////        mSocket.connect();
////        Toast.makeText(activity.getApplicationContext(),"Đã kết nối với server",Toast.LENGTH_LONG).show();
////    }
//
//    public static Socket getSocket(){
//        return mSocket;
//    }
//
//    public static void receiveData(){
//        mSocket.on("receiveFromServer",onRetrieveData);
//    }
//
//    private static Emitter.Listener onRetrieveData=new Emitter.Listener() {
//        @Override
//        public void call(final Object... args) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    JSONObject object=(JSONObject)args[0];
//                    //                        String ten=object.getString("noidung");
////                        Toast.makeText(MainActivity.this,ten,Toast.LENGTH_SHORT).show();
//                    SocketIO.dataSocketIO=new DataSocketIO(object);
//                }
//            });
//        }
//    };
//
//    public static DataSocketIO getDataSocketIO(){
//        return dataSocketIO;
//    }
//
//    public static void sendData(HashMap<String,String> values){
//        String commandType="sendToServer";
//        JSONObject jsonObject = new JSONObject();
//        for(Map.Entry<String, String> value:values.entrySet()){
//            try {
//                jsonObject.put(value.getKey(), value.getValue());
//            }catch (Exception ex){
//                ex.printStackTrace();
//            }
//        }
//        mSocket.emit(commandType,jsonObject);
//    }
}

class DataSocketIO{
    String commandType;
    String value;
    JSONObject jsonObject;

    public DataSocketIO(JSONObject jsonObject){
        this.jsonObject=jsonObject;

        parseJSONObject();
    }

    public DataSocketIO(String jsonString) throws JSONException {
        new DataSocketIO(new JSONObject(jsonString));
    }

    public void parseJSONObject(){
        if(this.jsonObject!=null) {
            this.commandType = this.jsonObject.optString("commandType");
            this.value = this.jsonObject.optString("value");
        }
    }

    public void setJSONObject(JSONObject jsonObject){
        this.jsonObject=jsonObject;
    }

    public void setJSONString(String jsonString) throws JSONException {
        this.jsonObject=new JSONObject(jsonString);
    }

    public void setAndParseJSONObject(JSONObject jsonObject){
        this.jsonObject=jsonObject;
        parseJSONObject();
    }

    public String getCommandType(){
        return commandType;
    }

    public String getValue(){
        return value;
    }

    public JSONObject getJSONObject(){
        return jsonObject;
    }

    public String getStringJSONObject(){
        return jsonObject.toString();
    }
}
