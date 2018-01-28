package com.thehaohcm.trafficnotificationapplication.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.thehaohcm.trafficnotificationapplication.R;
import com.thehaohcm.trafficnotificationapplication.controller.SocketIO;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SendReportPopup extends AppCompatActivity {

    private Button btnReport, btnTakeReportPicture, btnDeletePicture;
    private Button btnReportTrafficJam, btnReportPolice, btnReportCamera, btnReportConstruction, btnReportIntersection, btnReportAccident, btnReportWarning, btnReportFlooding;
    private TextView lblStatusReport,lblReportFail;
    private ImageView imgReport;
    private Uri uriFileImage;
    private File fileImage;
    private Socket mSocket;
    private int type;
    private String phoneNumber;
    private EditText txtMessageContent;
    private double latitude,longitude;
    private ProgressDialog pd;
    private String urlImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_report_popup);

        setTitle("Gửi phản hồi");

//        DisplayMetrics dm=new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(dm);
//
//        int width=dm.widthPixels;
//        int height=dm.heightPixels;
//
//        getWindow().setLayout((int)(width*.8),(int)(height*.6));
        this.type=-1;
        mapping();

    }

    private void mapping(){
        SocketIO app=(SocketIO)getApplication();
        mSocket=app.getSocket();
        mSocket.connect();

        Bundle extras=getIntent().getExtras();

        if(extras!=null){
            this.phoneNumber=extras.getString("phoneNumber");
            this.latitude=extras.getDouble("latitude");
            this.longitude=extras.getDouble("longitude");
        }else{
            Toast.makeText(SendReportPopup.this,"Không có số điện thoại",Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        pd=new ProgressDialog(this);
        pd.setCancelable(false);
        txtMessageContent=(EditText)findViewById(R.id.txtMessageContent);
        lblStatusReport = (TextView) findViewById(R.id.lblStatusReport);
        lblReportFail=(TextView)findViewById(R.id.lblReportFail);
        btnReportTrafficJam = (Button) findViewById(R.id.btnReportTrafficJam);
        btnReportTrafficJam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lblStatusReport.setText("Có va chạm xe");
                type=1;
            }
        });
        btnReportPolice = (Button) findViewById(R.id.btnReportPolice);
        btnReportPolice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lblStatusReport.setText("Có cảnh sát giao thông");
                type=2;
            }
        });
        btnReportCamera = (Button) findViewById(R.id.btnReportCamera);
        btnReportCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lblStatusReport.setText("Có camera giám sát");
                type=3;
            }
        });
        btnReportConstruction = (Button) findViewById(R.id.btnReportConstruction);
        btnReportConstruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lblStatusReport.setText("Có công trình xây dựng");
                type=4;
            }
        });
        btnReportAccident = (Button) findViewById(R.id.btnReportAccident);
        btnReportAccident.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lblStatusReport.setText("Có tai nạn");
                type=5;
            }
        });

        btnReportIntersection = (Button) findViewById(R.id.btnReportIntersection);
        btnReportIntersection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lblStatusReport.setText("Có ngã quẹo");
                type=6;
            }
        });

        btnReportWarning = (Button) findViewById(R.id.btnReportWarning);
        btnReportWarning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lblStatusReport.setText("Có Cảnh báo");
                type=7;
            }
        });
        btnReportFlooding = (Button) findViewById(R.id.btnReportFlooding);
        btnReportFlooding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lblStatusReport.setText("Có ngập lụt ");
                type=8;
            }
        });
        btnReport = (Button) findViewById(R.id.btnReport);
        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Call Web Service
                try {
                    if(fileImage!=null) {
                        urlImage = new UploadImage().execute(fileImage.getAbsolutePath()).get();
                    }else{
                        handler.sendEmptyMessage(0);
                    }
                } catch (Exception e){
                    Toast.makeText(SendReportPopup.this, "Không thể tải ảnh", Toast.LENGTH_SHORT).show();
                }
//                //Go back Main Activity
//                finish();
            }
        });

        btnTakeReportPicture = (Button) findViewById(R.id.btnTakeReportPicture);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            btnTakeReportPicture.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }

        btnDeletePicture = (Button) findViewById(R.id.btnDeletePicture);
        btnDeletePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imgReport.getDrawable() != null) {
                    imgReport.setImageDrawable(null);
                    btnDeletePicture.setVisibility(View.GONE);
                    btnTakeReportPicture.setEnabled(true);
                    fileImage=null;
                }
            }
        });

        btnDeletePicture.setVisibility(View.GONE);

        imgReport = (ImageView) findViewById(R.id.imgReport);

        mSocket.on("reportResult",report);
    }

    private Emitter.Listener report=new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if(args[0].equals("true")){
                Toast.makeText(getApplicationContext(), "Đã gửi phản hồi thành công", Toast.LENGTH_LONG).show();
                finish();
            }else if(args[0].equals("duplicated")){
                Toast.makeText(getApplicationContext(), "Nội dung phản hồi bị trùng lắp", Toast.LENGTH_LONG).show();
                finish();
            }else{
                runOnUiThread(new Runnable() {
                    public void run() {
                        //Toast.makeText(LoginActivity.this,"Sai thông tin đăng nhập. Bạn vui lòng đăng nhập lại",Toast.LENGTH_LONG).show();
                        lblReportFail.setText("Hệ thống không thể gửi phản hồi");
                        lblReportFail.setVisibility(View.VISIBLE);
                    }
                });
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                btnTakeReportPicture.setEnabled(true);
            }
        }
    }

    public void takePicture(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileImage=getOutputMediaFile();
        uriFileImage = Uri.fromFile(fileImage);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriFileImage);

        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                imgReport.setImageURI(uriFileImage);
                btnDeletePicture.setVisibility(View.VISIBLE);
                btnTakeReportPicture.setEnabled(false);
            }
        }
    }

    private File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), String.valueOf(R.string.app_name));

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
        mSocket.off("reportResult",report);
    }

    class UploadImage extends AsyncTask<String,Void,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd=new ProgressDialog(SendReportPopup.this);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Cloudinary cloudinary = new Cloudinary("cloudinary://782717635756553:krD723l2KIY6aU-BfO_jvy7EA_Y@dey0nbzee");
                Map uploadResult = cloudinary.uploader().upload(params[0], ObjectUtils.emptyMap());
                return uploadResult.get("url").toString();
            } catch (IOException e)
            {
                Log.i("uploadImage","đã có lỗi");
                e.printStackTrace();
            }
            Log.i("imageUpload","Da tai thanh cong");
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.i("uploadImage",s);
            handler.sendEmptyMessage(1);
        }
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    sendReport(true);
                    break;
                case 0:
                    sendReport(false);
                    break;
            }
        }
    };

    private void sendReport(boolean haveImage){
//        if(urlImage!=null&&!urlImage.trim().equals("")){
            JSONObject reportInfo=new JSONObject();
            try{
                if(type==-1){
                    Toast.makeText(getApplicationContext(), "Bạn vui lòng chọn tình trạng tuyến đường", Toast.LENGTH_LONG).show();
                    return;
                }
                reportInfo.put("phoneNumber",phoneNumber);
                reportInfo.put("type",type);
                reportInfo.put("messageContent",txtMessageContent.getText().toString());
                reportInfo.put("latitude",latitude);
                reportInfo.put("longitude",longitude);
                if(haveImage) {
                    reportInfo.put("imageURL", urlImage);
                }
//                String provider=null,cityName=null, StreetName=null, state=null, zip=null, country=null;
//                Geocoder gcd = new Geocoder(getApplicationContext(),Locale.getDefault());
//                List<Address> addresses;
//                try {
//                    addresses = gcd.getFromLocation(latitude, longitude, 1);
//                    if (addresses.size() > 0)
//                        StreetName = addresses.get(0).getThoroughfare();
//                    cityName = addresses.get(0).getLocality();
//                    state = addresses.get(0).getAdminArea();
//                    zip = addresses.get(0).getPostalCode();
//                    country = addresses.get(0).getCountryName();
//                } catch (Exception ex) {
//
//                }
//                reportInfo.put("country",country);
//                reportInfo.put("provider",provider);
//                reportInfo.put("district",district);
//                reportInfo.put("country",country);
//                reportInfo.put("country",country);

                mSocket.emit("report",reportInfo);
                finish();
            }catch(Exception ex){
                Toast.makeText(getApplicationContext(),"Đã có lỗi xảy ra, hệ thống không thể gửi phản hồi",Toast.LENGTH_LONG).show();
                return;
            }

            if(pd!=null)
                pd.dismiss();
//        }
    }
}
