package com.thehaohcm.trafficnotificationapplication.controller;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.widget.Toast;

import com.thehaohcm.trafficnotificationapplication.Activity.MainActivity;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import java.util.ArrayList;

/**
 * Created by thehaohcm on 9/18/17.
 */

public class Radio {

    public static String RADIO_HANOI="rtmp://117.6.129.102/live/vovgthn.sdp";
    public static String RADIO_TPHOCHIMINH="rtmp://117.6.129.102/live/vovgthcm.sdp";
    //Radio
    private static LibVLC libvlc;
    private static MediaPlayer mMediaPlayer = null;

    public static void playRadio(Context context,String linkRadio){
        boolean flag=true;
        if(mMediaPlayer==null||libvlc==null){
            releasePlayer();
            try {
                libvlc = new LibVLC(context);
                // Creating media player
                mMediaPlayer = new MediaPlayer(libvlc);
                Media m = new Media(libvlc, Uri.parse(linkRadio));
                mMediaPlayer.setMedia(m);
            } catch (Exception e) {
                Toast.makeText(context, "Không thể phát Radio", Toast
                        .LENGTH_LONG).show();
                flag=false;
            }
        }
        if(flag)
            mMediaPlayer.play();
        Toast.makeText(context,linkRadio,Toast.LENGTH_LONG).show();
    }

    public static void stopRadio(){
        releasePlayer();
    }

    private static void releasePlayer() {
        if (libvlc == null)
            return;
        mMediaPlayer.stop();
        libvlc.release();
        libvlc = null;
    }

    private static void setVolume(int value){
        if(mMediaPlayer!=null&&libvlc!=null&&(value>=0&&value<=100))
            mMediaPlayer.setVolume(value);
    }

    public static boolean isPlaying(){
        return mMediaPlayer.isPlaying();
    }
}
