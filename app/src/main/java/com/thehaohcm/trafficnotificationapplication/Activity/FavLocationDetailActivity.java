package com.thehaohcm.trafficnotificationapplication.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.squareup.picasso.Picasso;
import com.thehaohcm.trafficnotificationapplication.Interface.Retrofit.FavoriteLocationRepository;
import com.thehaohcm.trafficnotificationapplication.Model.Retrofit.FavoriteLocation;
import com.thehaohcm.trafficnotificationapplication.Model.Retrofit.Result.FavoriteLocationResult;
import com.thehaohcm.trafficnotificationapplication.R;
import com.thehaohcm.trafficnotificationapplication.controller.URLConnection;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by thehaohcm on 9/22/17.
 */

public class FavLocationDetailActivity extends AppCompatActivity {

    private int id;
    private FavoriteLocation favoriteLocation;
    private ImageView imgFavLocation;
    private EditText txtNameLocation;
    private EditText txtDescLocation;
    private EditText txtLatFavLocation, txtLngFavLocation;
    private Button btnCreateEditLocation, btnDeleteLocation;
    private Uri uriFileImage;
    private File fileImage;
    private ProgressDialog pd;
    private Retrofit retrofit;
    private String imageURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favlocation_detail);

        try {
            Intent intent = this.getIntent();
            Bundle bundle = intent.getExtras();
            this.favoriteLocation = (FavoriteLocation) bundle.getSerializable("FAVLOCATION");
        } catch (Exception ex) {
            favoriteLocation = null;
        }

        //mapping
        imgFavLocation = (ImageView) findViewById(R.id.imgFavLocation);
        txtNameLocation = (EditText) findViewById(R.id.txtNameLocation);
        txtLatFavLocation = (EditText) findViewById(R.id.txtLatFavLocation);
        txtLngFavLocation = (EditText) findViewById(R.id.txtLngFavLocation);
        txtDescLocation = (EditText) findViewById(R.id.txtDescLocation);
        btnCreateEditLocation = (Button) findViewById(R.id.btnCreateEditLocation);
        btnDeleteLocation = (Button) findViewById(R.id.btnDeleteLocation);
        pd = new ProgressDialog(this);
        pd.setCancelable(false);

        Retrofit.Builder builder = new Retrofit.Builder().baseUrl(URLConnection.getURLConnection()).addConverterFactory(GsonConverterFactory.create());

        this.retrofit = builder.build();

        txtNameLocation.addTextChangedListener(textChangeListener);
        txtLatFavLocation.addTextChangedListener(textChangeListener);
        txtLngFavLocation.addTextChangedListener(textChangeListener);
        txtDescLocation.addTextChangedListener(textChangeListener);

        imgFavLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imgFavLocation.getDrawable() != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(FavLocationDetailActivity.this);
                    builder.setTitle("Lựa chọn");
                    builder.setItems(new CharSequence[]
                                    {"Chụp ảnh khác", "Xóa ảnh"},
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 0:
                                            takePicture();
                                            break;
                                        case 1:
                                            imgFavLocation.setImageDrawable(null);
                                            break;
                                    }
                                }
                            });
                    builder.create().show();
                } else {
                    takePicture();
                }
            }
        });

        if (favoriteLocation == null) {
            Intent intent = getIntent();
            txtLatFavLocation.setText(intent.getStringExtra("Lat"));
            txtLngFavLocation.setText(intent.getStringExtra("Lng"));

            btnCreateEditLocation.setText("Thêm địa điểm");
            btnDeleteLocation.setVisibility(View.INVISIBLE);
            btnCreateEditLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //gửi lên server thêm địa điểm

//                    Toast.makeText(FavLocationDetailActivity.this, "Đã thêm địa điểm", Toast.LENGTH_LONG).show();
//                    FavLocationDetailActivity.this.finish();
                    try {
                        if (fileImage != null) {
                            new UploadImage().execute(fileImage.getAbsolutePath()).get();
                        } else {
                            imageURL = "";
                            handler.sendEmptyMessage(1);
                        }
                    } catch (Exception e) {
                        Toast.makeText(FavLocationDetailActivity.this, "Không thể tải ảnh", Toast.LENGTH_SHORT).show();
                        imageURL="";
                    }
                }
            });
        } else {
            this.id = favoriteLocation.getFavoriteID();
            btnCreateEditLocation.setText("Chỉnh sửa địa điểm");
            btnCreateEditLocation.setEnabled(false);
            btnCreateEditLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //gửi lên server chỉnh sửa location
                    try {
                        if (fileImage != null) {
                            new UploadImage().execute(fileImage.getAbsolutePath()).get();
                        } else {
                            imageURL = "";
                            handler.sendEmptyMessage(0);
                        }
                    } catch (Exception e) {
                        Toast.makeText(FavLocationDetailActivity.this, "Không thể tải ảnh", Toast.LENGTH_SHORT).show();
                        imageURL="";
                    }
//                    Toast.makeText(FavLocationDetailActivity.this, "Đã chỉnh sửa địa điểm", Toast.LENGTH_LONG).show();
//                    FavLocationDetailActivity.this.finish();
                }
            });
            btnDeleteLocation = (Button) findViewById(R.id.btnDeleteLocation);
            btnDeleteLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //gửi lên server xóa location

//                    Toast.makeText(FavLocationDetailActivity.this, "Đã xóa địa điểm", Toast.LENGTH_LONG).show();
//                    FavLocationDetailActivity.this.finish();
                    if (id != 0)
                        deleteFavoriteLocation();
                }
            });

            txtNameLocation.setText(favoriteLocation.getNameFavoriteLocation());

            txtLatFavLocation.setText(String.valueOf(favoriteLocation.getLatitude()));

            txtLngFavLocation.setText(String.valueOf(favoriteLocation.getLongitude()));

            txtDescLocation.setText(favoriteLocation.getNote());

            if(favoriteLocation.getImageURL()!=null&&!favoriteLocation.getImageURL().equals("")) {
                Picasso.with(getApplicationContext()).load(favoriteLocation.getImageURL()).resize(512, 288).into(imgFavLocation);
                this.imageURL=favoriteLocation.getImageURL();
            }

        }
    }

    private TextWatcher textChangeListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (txtNameLocation.getText().toString().trim().length() > 0 &&
                    txtLatFavLocation.getText().toString().trim().length() > 0 &&
                    txtLngFavLocation.getText().toString().trim().length() > 0 &&
                    txtDescLocation.getText().toString().trim().length() > 0) {
                btnCreateEditLocation.setEnabled(true);
            } else {
                btnCreateEditLocation.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 99) {
            if (resultCode == RESULT_OK) {
                imgFavLocation.setImageURI(uriFileImage);
            }
        }
    }

    private void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileImage = getOutputMediaFile();
        uriFileImage = Uri.fromFile(fileImage);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriFileImage);

        startActivityForResult(intent, 99);
    }

    private void deleteFavoriteLocation() {
        Retrofit.Builder builder = new Retrofit.Builder().baseUrl(URLConnection.getURLConnection()).addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        FavoriteLocationRepository client = retrofit.create(FavoriteLocationRepository.class);

        Call<FavoriteLocationResult> call = client.deleteFavoriteLocationById(URLConnection.getPhoneNumber(), String.valueOf(this.id));

        call.enqueue(new Callback<FavoriteLocationResult>() {
            @Override
            public void onResponse(Call<FavoriteLocationResult> call, Response<FavoriteLocationResult> response) {
                Toast.makeText(FavLocationDetailActivity.this, "Đã xóa địa điểm thành công", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(Call<FavoriteLocationResult> call, Throwable t) {
                Toast.makeText(FavLocationDetailActivity.this, "Không thể xóa địa điểm", Toast.LENGTH_SHORT).show();
            }
        });
    }

    class UploadImage extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd=new ProgressDialog(FavLocationDetailActivity.this);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Cloudinary cloudinary = new Cloudinary("cloudinary://782717635756553:krD723l2KIY6aU-BfO_jvy7EA_Y@dey0nbzee");
                Map uploadResult = cloudinary.uploader().upload(params[0], ObjectUtils.emptyMap());
                return uploadResult.get("url").toString();
            } catch (IOException e) {
                Log.i("uploadImage", "đã có lỗi");
                e.printStackTrace();
            }
            Log.i("imageUpload", "Da tai thanh cong");
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            imageURL = s;
            if(pd!=null)
                pd.dismiss();

            if (favoriteLocation != null)
                handler.sendEmptyMessage(0);
            else
                handler.sendEmptyMessage(1);
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            FavoriteLocationRepository client = retrofit.create(FavoriteLocationRepository.class);
            Call<FavoriteLocationResult> call;
            final String successStr,failureStr;
            if (msg.what == 0) {
                call = client.editFavoriteLocationById(URLConnection.getPhoneNumber(), String.valueOf(id), txtNameLocation.getText().toString(), txtLatFavLocation.getText().toString(), txtLngFavLocation.getText().toString(), txtDescLocation.getText().toString(), imageURL);
                successStr="Đã chỉnh sửa địa điểm thành công";
                failureStr="Không thể chỉnh sửa địa điểm";

            } else {
                call = client.createFavoriteLocationByUser(URLConnection.getPhoneNumber(), txtNameLocation.getText().toString(), txtLatFavLocation.getText().toString(), txtLngFavLocation.getText().toString(), txtDescLocation.getText().toString(), imageURL);
                successStr="Đã thêm địa điểm thành công";
                failureStr="Không thể thêm địa điểm";
            }

            call.enqueue(new retrofit2.Callback<FavoriteLocationResult>() {
                @Override
                public void onResponse(Call<FavoriteLocationResult> call, Response<FavoriteLocationResult> response) {
                    FavoriteLocationResult res=response.body();
                    if(res.getSuccess()) {
                        Toast.makeText(FavLocationDetailActivity.this, successStr, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else
                        Toast.makeText(FavLocationDetailActivity.this, failureStr, Toast.LENGTH_SHORT).show();

                    fileImage.delete();
                }

                @Override
                public void onFailure(Call<FavoriteLocationResult> call, Throwable t) {
                    Toast.makeText(FavLocationDetailActivity.this, "Không thể kết nối với server, bạn vui lòng kiểm tra lại", Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

}
