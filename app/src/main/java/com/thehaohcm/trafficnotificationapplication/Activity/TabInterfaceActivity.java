package com.thehaohcm.trafficnotificationapplication.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.thehaohcm.trafficnotificationapplication.Enum.MapStyle;
import com.thehaohcm.trafficnotificationapplication.R;

/**
 * Created by thehaohcm on 9/21/17.
 */

public class TabInterfaceActivity extends Fragment {

    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.rootView=inflater.inflate(R.layout.activity_tabinterface,container,false);

        Button button= (Button) rootView.findViewById(R.id.button3);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog();
            }
        });
        return rootView;
    }

    private void dialog(){
        final String grp[]=new String[]{"Default","Standard","Silver","Retro","Dark","Night","Aubergine"};
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(rootView.getContext());
        //alt_bld.setIcon(R.drawable.icon);
//        alt_bld.setTitle("Select a Group Name");
        int choosedItem=TabInterfaceActivity.getMapStyle(rootView.getContext());

        alt_bld.setTitle("Chọn giao diện");
        alt_bld.setSingleChoiceItems(grp, choosedItem, new DialogInterface
                .OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                Toast.makeText(rootView.getContext(),
                        "Group Name = "+grp[item], Toast.LENGTH_SHORT).show();

                saveMapStyle(item);
                dialog.dismiss();// dismiss the alertbox after chose option

            }
        });
        AlertDialog alert = alt_bld.create();
        alert.show();

    }

    private void saveMapStyle(int choosedStyle){
        SharedPreferences pref= this.getActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=pref.edit();
        editor.putInt("Num installed panels",choosedStyle);
        editor.apply();
    }

    public static int getMapStyle(Context context){

        SharedPreferences prefs=context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        return prefs.getInt("Num installed panels",0);
    }

    public static void resetMapStyle(Context context){
        SharedPreferences pref= context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=pref.edit();
        editor.putInt("Num installed panels",0);
        editor.apply();
    }

}