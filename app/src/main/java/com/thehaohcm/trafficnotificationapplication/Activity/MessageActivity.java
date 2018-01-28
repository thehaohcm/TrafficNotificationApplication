package com.thehaohcm.trafficnotificationapplication.Activity;

import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;
import com.thehaohcm.trafficnotificationapplication.Model.MessageReport;
import com.thehaohcm.trafficnotificationapplication.R;
import com.thehaohcm.trafficnotificationapplication.controller.Message;
import com.thehaohcm.trafficnotificationapplication.controller.SocketIO;

import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MessageActivity extends AppCompatActivity {

    ImageView imgMessageReport;
    TextView txtTypeMessage,txtInvalidDate,txtMessageContent;
    Button btnConfirmMessage,btnViolateMessage;
    private Socket mSocket;
    private String phoneNumber;
    private double latitude,longitude;
    private MessageReport messageReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        SocketIO app=(SocketIO)getApplication();
        mSocket=app.getSocket();
        mSocket.connect();

        mSocket.on("messageReport",messageReportListener);

        Bundle b=getIntent().getExtras();
        this.phoneNumber=b.getString("PHONENUMBER");
        this.latitude=b.getDouble("LAT");
        this.longitude=b.getDouble("LNG");

        LatLng latLng=new LatLng(latitude,longitude);
        messageReport= Message.getMessageReport(latLng);

        imgMessageReport= (ImageView) findViewById(R.id.imgReportMessage);
        txtTypeMessage= (TextView) findViewById(R.id.txtTypeMessage);
        txtInvalidDate= (TextView) findViewById(R.id.txtInvalidDate);
        txtMessageContent= (TextView) findViewById(R.id.txtMessageContent);
        btnConfirmMessage= (Button) findViewById(R.id.btnConfirmMessage);
        btnViolateMessage= (Button) findViewById(R.id.btnViolateMessage);

        if(this.phoneNumber==null){//||this.phoneNumber==messageReport.getPhoneNumber()){
            btnConfirmMessage.setEnabled(false);
            btnViolateMessage.setEnabled(false);
        }
        else if(messageReport.checkReportedConfirmPhoneNumber(this.phoneNumber)){
            btnConfirmMessage.setEnabled(false);
            btnConfirmMessage.setTypeface(btnConfirmMessage.getTypeface(), Typeface.BOLD_ITALIC);
            btnViolateMessage.setEnabled(false);
        }else if(messageReport.checkReportedViolatePhoneNumber(this.phoneNumber)){
            btnConfirmMessage.setEnabled(false);
            btnViolateMessage.setEnabled(false);
            btnViolateMessage.setTypeface(btnViolateMessage.getTypeface(),Typeface.BOLD_ITALIC);
        }

        txtMessageContent.setText(messageReport.getMessageContent());

        String imageURL=messageReport.getImageURL();
        if(!imageURL.equals(""))
            Picasso.with(getApplicationContext()).load(imageURL).into(imgMessageReport);
        txtTypeMessage.setText(messageReport.getMessageTypeStr());
        txtInvalidDate.setText(messageReport.getInvalidDateStr());


        btnConfirmMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject confirmInfo=new JSONObject();
                try {
                    confirmInfo.put("phoneNumber", phoneNumber);
                    confirmInfo.put("latitude", latitude);
                    confirmInfo.put("longitude",longitude);
                    mSocket.emit("confirm", confirmInfo);
                }catch (Exception ex){
                    Toast.makeText(MessageActivity.this,"Không thể gửi phản hồi đến server",Toast.LENGTH_LONG).show();
                }
            }
        });

        btnViolateMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MessageActivity.this);
                builder.setMessage("Bạn có thật sự muốn báo cáo vi phạm?").setPositiveButton("Có", dialogClickListener)
                        .setNegativeButton("Không", dialogClickListener).show();
            }
        });
    }

    private DialogInterface.OnClickListener dialogClickListener=new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    JSONObject violateInfo=new JSONObject();
                    try{
                        violateInfo.put("phoneNumber",phoneNumber);
                        violateInfo.put("latitude",latitude);
                        violateInfo.put("longitude",longitude);
                        mSocket.emit("violate",violateInfo);
                    }catch (Exception ex){
                        Toast.makeText(MessageActivity.this, "Không thể gửi phản hồi đến server", Toast.LENGTH_LONG).show();
                    }

                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    dialog.dismiss();
                    break;
            }
        }
    };

    private Emitter.Listener messageReportListener=new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (args[0].equals("true-confirm")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        messageReport.addReportConfirm(phoneNumber);
                        Toast.makeText(MessageActivity.this, "Cảm ơn bạn đã gửi phản hồi", Toast.LENGTH_LONG).show();
                        MessageActivity.this.finish();
                    }
                });
            } else if (args[0].equals("true-violate")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        messageReport.addReportViolate(phoneNumber);
                        Toast.makeText(MessageActivity.this, "Cảm ơn bạn đã gửi phản hồi", Toast.LENGTH_LONG).show();
                        MessageActivity.this.finish();
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MessageActivity.this, "Hệ thống không thể gửi phản hồi lên server", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    };
}
