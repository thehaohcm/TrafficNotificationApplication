package com.thehaohcm.trafficnotificationapplication.controller;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import org.json.JSONObject;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.protocol.HTTP;
import cz.msebera.android.httpclient.util.EntityUtils;

/**
 * Created by thehaohcm on 6/29/17.
 */
public class Text2Speech {

    private Activity mainActivity;
    private String[] arrayText;
    private static int current=0;
    private FetchUrl fetchUrl;

    public Text2Speech(Activity mainActivity, String... arrayText) {
        this.mainActivity=mainActivity;
        this.arrayText=arrayText;
    }

    public Text2Speech(Activity mainActivity,FetchUrl fetchUrl, String... arrayText) {
        this.mainActivity=mainActivity;
        this.fetchUrl=fetchUrl;
        this.arrayText=arrayText;
    }

    public void speechText(){
//        for(String text:arrayText) {
//            new SendTask(mainActivity).execute(text);
//        }
        new SendTask(mainActivity).execute(arrayText);
    }

    private class SendTask extends AsyncTask<String, Integer, String> {

        //private ProgressDialog dialog;
        private Activity activity;

        public SendTask(Activity activity){
            this.activity=activity;
        }

        @Override
        protected void onPreExecute() {
//            dialog = new ProgressDialog(activity);
//            this.dialog.setTitle("Đang xử lý...");
//            this.dialog.setMessage("Vui lòng đợi...");
//            this.dialog.setCanceledOnTouchOutside(false);
//            this.dialog.show();
        }

//        @Override
//        protected void onPostExecute(String... arr) {
//            super.onPostExecute(arr);
//            if(dialog.isShowing())
//                dialog.dismiss();
//        }

        @Override
        protected String doInBackground(String... speakText) {
            final boolean[] flag = {false};
            for (String text : speakText) {
                if(flag[0])
                    break;
                try {
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost("http://api.openfpt.vn/text2speech/v4");
                    httppost.setHeader(HTTP.CONTENT_TYPE,
                            "text/plain;charset=UTF-8");

                    httppost.setHeader("api_key", "9dc9d2b239584f08bc81ced8df28df1f");
                    httppost.setHeader("speed", "-1");
                    httppost.setHeader("voice", "hatieumai");
                    httppost.setHeader("prosody", "1");
                    httppost.setHeader("Cache-Control", "no-cache");
                    httppost.setHeader(HTTP.CONTENT_TYPE, "text/plain");

                    httppost.setEntity(new ByteArrayEntity(text.getBytes("UTF-8")));
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();

                    // Read the contents of an entity and return it as a String.
                    String content = EntityUtils.toString(entity);
                    Log.e("json", content);
                    JSONObject obj = new JSONObject(content);
                    String assignVoice = obj.getString("async");
//                    assignVoiceArr.add(assignVoice);
                    final MediaPlayer player = new MediaPlayer();
                    player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    player.setDataSource(assignVoice);
                    player.prepare();
                    player.start();

                    if(speakText.length>1) {
                        Snackbar.make(activity.findViewById(android.R.id.content), (text), player.getDuration() + 1000)
                                .setAction("Hủy", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        SendTask.this.cancel(true);
                                        fetchUrl.clearPolyline();
                                        fetchUrl.clearPointMarker();
                                        flag[0] = true;
                                    }
                                })
                                .show();
                    }
                    else if(speakText.length==1){
                        Snackbar.make(activity.findViewById(android.R.id.content), (text), player.getDuration() + 1000)
                                .show();
                    }
                    if(flag[0])
                        break;
                    Thread.sleep(player.getDuration()+1500);
                } catch (Exception ex) {
                    Log.e("HTTP", String.valueOf(ex));
                }
                //}

                Log.i("HTTP", "Success ");

//                if (dialog.isShowing())
//                    dialog.dismiss();

            }
            return null;
        }

    }
}
