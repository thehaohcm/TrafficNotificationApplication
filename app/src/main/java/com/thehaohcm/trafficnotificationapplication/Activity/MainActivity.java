package com.thehaohcm.trafficnotificationapplication.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import com.thehaohcm.trafficnotificationapplication.Interface.Retrofit.LoginRepository;
import com.thehaohcm.trafficnotificationapplication.Interface.Retrofit.ShortenURLRepository;
import com.thehaohcm.trafficnotificationapplication.Model.MessageReport;
import com.thehaohcm.trafficnotificationapplication.Model.Retrofit.Account;
import com.thehaohcm.trafficnotificationapplication.Model.Retrofit.Result.LoginResult;
import com.thehaohcm.trafficnotificationapplication.Model.Retrofit.Result.ShortenURLResult;
import com.thehaohcm.trafficnotificationapplication.R;
import com.thehaohcm.trafficnotificationapplication.controller.CalculateDistance;
import com.thehaohcm.trafficnotificationapplication.controller.Camera;
import com.thehaohcm.trafficnotificationapplication.controller.FetchUrl;
import com.thehaohcm.trafficnotificationapplication.controller.Flooding;
import com.thehaohcm.trafficnotificationapplication.controller.Message;
import com.thehaohcm.trafficnotificationapplication.controller.Radio;
import com.thehaohcm.trafficnotificationapplication.controller.SocketIO;
import com.thehaohcm.trafficnotificationapplication.controller.Text2Speech;
import com.thehaohcm.trafficnotificationapplication.controller.URLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback//, //method onMapReady
{

    private View header;
    private NavigationView navigationView;

    private String[] dinhtuyen;
    private int current;
    private TextView lblMaxSpeed, lblCurrentSpeed, lblNamePhone, lblScore, lblSignUpDate;
    private double currentSpeed, maxSpeed = 50; //, currentSpeed2
    private boolean cameraChangeFlag = false, startFlag = true;
    private boolean loginFlag = false;
    private boolean connectedServerFlag=false;
    private String phoneNumber, provider;
    private int countChangeStartLocation = 0;
    private int countToCheck = 0, countToSpeech = 0;

    private Socket mSocket = null;
    private Toolbar toolbarReport;
    private FloatingActionButton fabReport;
    private Snackbar snackbar;

    //    private GoogleApiClient mGoogleApiClient; //interact with API
    private GoogleMap mMap;
    private Location mLastLocation;//,mDestLocation;
    //    private LocationRequest mLocationRequest;
    private SupportMapFragment mapFragment;
    private Circle mCircle;
    private Marker startPointMarker, endPointMarker;
    private ArrayList<Location> trafficLocationList;
    private LatLng mDestLatLng;
    private LocationManager mLocationManager = null;

    private FetchUrl fetchUrl;

    private Switch switchRadio,switchCamera,switchFlooding;

//    private SharedPreferences mSharedPreferences;
    private JSONArray jsonArrayMessages;

    private String jsonCaremaStr;
    private Marker currentMarkerInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.loginFlag = extras.getBoolean("login");
            this.phoneNumber = extras.getString("phoneNumber");
        }

        setTitle("Traffic Notificaton Application");

        mapping();
        mappingAndInitMapFragment();
        initSocketIO();

        SharedPreferences sharedPreferences = getSharedPreferences("trafficNofiticationSharedPreferences", Context.MODE_PRIVATE);
        String phoneNumber=sharedPreferences.getString("phoneNumber",null);
        String password=sharedPreferences.getString("password",null);
        if(phoneNumber!=null&&password!=null){
            loginRetrofit(phoneNumber,password);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == RESULT_OK && data != null) {
            loginFlag = true;
            this.phoneNumber = data.getStringExtra("phoneNumber");
            URLConnection.setPhoneNumber(this.phoneNumber);
            lblNamePhone.setText("Xin chào: " + data.getStringExtra("name") + " - " + phoneNumber);
            lblScore.setVisibility(View.VISIBLE);
            lblScore.setText("Điểm tích lũy: " + data.getIntExtra("score",0) + " điểm");
            lblSignUpDate.setVisibility(View.VISIBLE);
            lblSignUpDate.setText("Ngày đăng ký: " + data.getStringExtra("signupDate"));

            Menu nav_Menu = navigationView.getMenu();
            nav_Menu.findItem(R.id.menuAccount).setVisible(true);
            nav_Menu.findItem(R.id.nav_login).setVisible(false);

        }else if(requestCode == 999 && resultCode == RESULT_OK && data != null){
            LatLng positionFavorite=new LatLng(data.getDoubleExtra("Lat",0),data.getDoubleExtra("Lng",0));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(positionFavorite));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(18));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
        //stop location updates when Activity is no longer active
//        if (mGoogleApiClient != null) {
//            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
//        }
        //mLocationManager.removeUpdates((android.location.LocationListener) this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerInternetCheckReceiver();
        //radio
//        if(mFilePath!=null||!mFilePath.trim().equals(""))
//            createPlayer(mFilePath);
        refreshScreen(getApplicationContext());
    }

    //region section check and request permission
    //start section check and request permission
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

//                        if (mGoogleApiClient == null) {
//                            buildGoogleApiClient();
//                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }
    //end section check and request permission
    //endregion

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_login) {
//            if(this.connectedServerFlag) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivityForResult(intent, 1000);
//            }else{
//                Toast.makeText(MainActivity.this,"Bạn vui lòng kết nối với server để có thể đăng nhập",Toast.LENGTH_LONG).show();
//            }
        } else if (id == R.id.nav_about) {

            Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            showDialogLogout();
        } else if (id == R.id.nav_rss) {
            Intent intent = new Intent(getApplicationContext(), NewsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_weather) {
            Intent intent = new Intent(getApplicationContext(), WeatherActivity.class);
            intent.putExtra("LOCATION", new String(String.valueOf(mLastLocation.getLatitude()) + "," + String.valueOf(mLastLocation.getLongitude())));
            startActivity(intent);
        } else if (id == R.id.nav_setting) {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_favoriteLocation) {
//            JSONObject tokenInfo = new JSONObject();
//            //Call Web Service
//            try {
//                Log.i("So dien thoai", phoneNumber);
//                tokenInfo.put("phoneNumber", phoneNumber);
//
////                    Toast.makeText(getApplicationContext(), "Tài khoản của bạn đã được xác thực", Toast.LENGTH_LONG).show();
//            } catch (Exception ex) {
//                Toast.makeText(getApplicationContext(), "Hệ thống không thể xác thực tài khoản của bạn", Toast.LENGTH_LONG).show();
//            }
//            mSocket.emit("getInfo", tokenInfo);

            Intent intent=new Intent(getApplicationContext(),FavLocationListActivity.class);
            intent.putExtra("phoneNumber",phoneNumber);
            startActivityForResult(intent, 999);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showDialogLogout() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        phoneNumber = null;
                        loginFlag = false;

//                        Intent intent = getIntent();
//                        finish();
//                        startActivity(intent);
                        SharedPreferences sharedPreferences = getSharedPreferences("trafficNofiticationSharedPreferences", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove("phoneNumber");
                        editor.remove("password");
                        editor.apply();

                        Intent i = getBaseContext().getPackageManager()
                                .getLaunchIntentForPackage( getBaseContext().getPackageName() );
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        finish();
                        startActivity(i);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        dialog.dismiss();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Bạn có thật sự muốn đăng xuất?").setPositiveButton("Có", dialogClickListener)
                .setNegativeButton("Không", dialogClickListener).show();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void initSocketIO() {
        SocketIO app = (SocketIO) getApplication();
        mSocket = app.getSocket();
        if (mSocket != null) {
            //có lỗi - chưa nhận được response từ responseConnectionListener

        }
        else {
            Toast.makeText(getApplicationContext(), "Không thể kết nối với server", Toast.LENGTH_LONG).show();
            finish();
            System.exit(0);
        }

        mSocket.on("responseConnection",responseConnectionListener);
        mSocket.on("responseDisconnect",responseDisconnectListener);

        mSocket.on("notificationReport", notificationReportListener);

        mSocket.on("messageReponseList",messageReponseListListener);

        mSocket.on("cameraResponse",cameraResponseListener);

        try{
            mSocket.emit("sendMessageList",new JSONObject());
        }catch (Exception ex){

        }
    }

    private void mapping() {
        toolbarReport = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbarReport);

        fabReport = (FloatingActionButton) findViewById(R.id.fab);
        fabReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Phản hồi về tuyến đường?", Snackbar.LENGTH_LONG)
                        .setAction("Gửi phản hồi", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (phoneNumber == null && loginFlag == false) {
                                    Toast.makeText(MainActivity.this, "Bạn vui lòng đăng nhập để sử dụng tính năng này", Toast.LENGTH_LONG).show();
                                    return;
                                }
                                startActivity(new Intent(MainActivity.this, SendReportPopup.class).putExtra("phoneNumber", phoneNumber).putExtra("latitude", mLastLocation.getLatitude()).putExtra("longitude", mLastLocation.getLongitude()));
                            }
                        }).show();
            }
        });

        FloatingActionButton fabStartVoice = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fabStartVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fetchUrl != null) {
                    Snackbar.make(view, "Bắt đầu đọc chỉ dẫn?", Snackbar.LENGTH_LONG)
                            .setAction("Đọc", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ArrayList<String> routes = fetchUrl.getListRoutesLegs();
                                    dinhtuyen = routes.toArray(new String[routes.size()]);
                                    //dinhtuyen = FetchUrl.getDinhtuyen();
                                    Log.e("length of dinhtuyen", String.valueOf(dinhtuyen.length));
                                    if (current < dinhtuyen.length) {
                                        Text2Speech t2s = new Text2Speech(MainActivity.this, fetchUrl,dinhtuyen);
                                        t2s.speechText();
                                        current++;
                                    } else {
                                        Toast.makeText(MainActivity.this, "Đã hết thông tin chỉ đường", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }).show();
                }
            }
        });

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbarReport, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        lblMaxSpeed = (TextView) findViewById(R.id.lblMaxSpeed);
        lblMaxSpeed.setText("Tốc độ tối đa: " + maxSpeed + " km/h");
        lblCurrentSpeed = (TextView) findViewById(R.id.lblCurrentSpeed);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        header = navigationView.getHeaderView(0);
        lblNamePhone = (TextView) header.findViewById(R.id.lblNamePhone);
        lblScore = (TextView) header.findViewById(R.id.lblScore);
        lblSignUpDate = (TextView) header.findViewById(R.id.lblSignUpDate);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.menuAccount).setVisible(false);
        nav_Menu.findItem(R.id.nav_login).setVisible(true);

        this.switchRadio = (Switch) nav_Menu.findItem(R.id.switchRadio).getActionView();
        switchRadio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true)
                    initRadio();
                else
                    Radio.stopRadio();
                drawer.closeDrawers();
            }
        });

        this.switchCamera = (Switch) nav_Menu.findItem(R.id.switchCamera).getActionView();
        switchCamera.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
//                    mSocket.on("cameraResponse",cameraResponseListener);
                    mSocket.emit("getCameras", new JSONObject());
                    Camera.showCamera(MainActivity.this, mMap);
                }
                else {
//                    mSocket.off("cameraResponse",cameraResponseListener);
                    Camera.removeCamera(true);

                }
                drawer.closeDrawers();
            }
        });

        this.switchFlooding=(Switch) nav_Menu.findItem(R.id.switchFlooding).getActionView();
        switchFlooding.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    Flooding.showTramDoMuaMarker(MainActivity.this,mMap);
                    Flooding.showTramDoTrieuMarker(MainActivity.this,mMap);
                }
                else {
                    Flooding.removeTramDoMuaMarkerList();
                    Flooding.removeTramDoTrieuMarkerList();
                }
                drawer.closeDrawers();
            }
        });

        if (lblNamePhone != null)
            lblNamePhone.setText("Xin chào: Khách");
        if (lblScore != null)
            lblScore.setVisibility(View.INVISIBLE);
        if (lblSignUpDate != null)
            lblSignUpDate.setVisibility(View.INVISIBLE);

    }

    private void mappingAndInitMapFragment() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        mapFragment.getMapAsync(this);

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("thành công", "Place: " + place.getName());

                LatLng latLng = place.getLatLng();
                //move map camera
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(18));
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("thất bại", "An error occurred: " + status);
            }
        });

        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabledGPS = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean enabledWiFi = service
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        // Check if enabled and if not send user to the GSP settings
        // Better solution would be to display a dialog and suggesting to
        // go to the settings
        if (!enabledGPS) {
            Toast.makeText(this, "GPS signal not found", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
        updateCurrentLocation();

        trafficLocationList = new ArrayList<>();
    }

    private Emitter.Listener responseConnectionListener=new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    connectedServerFlag=true;
                    Toast.makeText(getApplicationContext(), "Đã kết nối với server", Toast.LENGTH_LONG).show();
                    try{
                        mSocket.emit("sendMessageList",new JSONObject());
                    }catch (Exception ex){

                    }
//                    if(arg0.toString().equals("connected")) {
//                        connectedServerFlag=true;
//                        Toast.makeText(getApplicationContext(), "Đã kết nối với server", Toast.LENGTH_LONG).show();
//                        try {
//                            mSocket.emit("sendMessageList",new JSONObject());
//                        }catch (Exception ex){
//
//                        }
//                    }else if(arg0.toString().equals("disconnected")){
//                        connectedServerFlag=false;
//                        Toast.makeText(MainActivity.this, "Đã mất kết nối với server. Bạn vui lòng kiểm tra lại", Toast.LENGTH_SHORT).show();
//                    }
                }
            });

        }
    };

    private Emitter.Listener responseDisconnectListener=new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    connectedServerFlag=false;
                    Toast.makeText(getApplicationContext(), "Đã mất kết nối với server", Toast.LENGTH_LONG).show();
                }
            });
        }
    };

//    private Emitter.Listener notificationReportListener = new Emitter.Listener() {
//        @Override
//        public void call(Object... args) {
//            final Object arg0 = args[0];
//            runOnUiThread(new Runnable() {
//                public void run() {
//                    JSONObject jsonObject = null;
//                    try {
//                        jsonObject = new JSONObject(arg0.toString());
//                        String user = jsonObject.getString("phoneNumber");
//                        double latitude = jsonObject.getDouble("latitude");
//                        double longitude = jsonObject.getDouble("longitude");
//                        LatLng point = new LatLng(latitude, longitude);
//                        // Creating MarkerOptions
//                        MarkerOptions options = new MarkerOptions();
//                        options.position(point);
//                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
//
//                        // Setting the position of the marker
//                        options.position(point);
//                        options.title("Thông báo từ tài khoản " + user);
//                        options.snippet("Snippet");
//                        mMap.addMarker(options);
//                        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//
//                        // Vibrate for 300 milliseconds
//                        v.vibrate(300);
//
//                        //CircleOptions circle = new CircleOptions();
//                        //Location.distanceBetween(latitude,longitude,circle.get);
//                        if (CalculateDistance.checkDistanceRadius(mLastLocation, latitude, longitude)) {
//                            // Vibrate for 300 milliseconds
//                            v.vibrate(300);
//                        }
//                        showNotification("Phản hồi tuyến đường từ tài khoản " + user);
//
//                        Location location = new Location("report");
//                        location.setLatitude(latitude);
//                        location.setLongitude(longitude);
//                        trafficLocationList.add(location);
//                        if (fetchUrl != null) {
//                            //if (fetchUrl.checkLocationOnPolyline(mLastLocation, location)) { //điểm phản hồi có trùng với vị trí đi
//                            //removePointsMarker();
//                            mLastLocation = mMap.getMyLocation();
//                            //updateCurrentLocation();
//                            LatLng latlng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
//                            //fetchUrl.setRandomFlag(true);
//                            fetchUrl.changeRoute(new LatLng(location.getLatitude(), location.getLongitude()), endPointMarker.getPosition(), trafficLocationList);
//                            //}
//                        }
//                        Log.i("resultInfo", jsonObject.getString("Email"));
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                        Log.e("lỗi", "da có loi xay ra");
//                    }
//                }
//            });
//        }
//    };

    private Emitter.Listener notificationReportListener = new Emitter.Listener(){

        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(args[0].toString());
                        String user = jsonObject.getString("phoneNumber");
                        int type=jsonObject.getInt("type");
                        String messageContent=jsonObject.getString("messageContent");
                        double latitude = jsonObject.getDouble("latitude");
                        double longitude = jsonObject.getDouble("longitude");
                        String imageURL="";
                        if(jsonObject.has("imageURL"))
                            imageURL = jsonObject.getString("imageURL");
                        String createdDateTime=jsonObject.getString("createdDateTime");
                        String invalidDateTime=jsonObject.getString("invalidDateTime");
                        JSONArray jsonConfirmArray,jsonViolateArray;
                        ArrayList<String> confirmArray = null,violateArray=null;
                        if(jsonObject.has("confirm")) {
                            jsonConfirmArray = jsonObject.getJSONArray("confirm");
                            confirmArray = new ArrayList<String>();
                            for (int i = 0; i < jsonConfirmArray.length(); i++)
                                confirmArray.add(jsonConfirmArray.getJSONObject(i).getString("phoneNumber"));
                        }
                        if(jsonObject.has("violate")) {
                            jsonViolateArray = jsonObject.getJSONArray("violate");
                            violateArray = new ArrayList<String>();
                            for (int i = 0; i < jsonViolateArray.length(); i++)
                                violateArray.add(jsonViolateArray.getJSONObject(i).getString("phoneNumber"));
                        }

                        LatLng point = new LatLng(latitude, longitude);

                        Message.addMessageReport(new MessageReport(user,latitude,longitude,createdDateTime,invalidDateTime,type,imageURL,messageContent,confirmArray,violateArray));
                        Message.showMessageReportMarker(point,getApplicationContext(),mMap);
//                        // Creating MarkerOptions
//                        MarkerOptions options = new MarkerOptions();
//                        options.position(point);
//                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
//
//                        // Setting the position of the marker
//                        options.position(point);
//                        options.title("Thông báo từ tài khoản " + user);
//                        options.snippet("Snippet");
//                        mMap.addMarker(options);

//                        Message.addOneMessageReport(getApplicationContext(),mMap,new MessageReport(user,latitude,longitude,))
                        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                        // Vibrate for 300 milliseconds
                        v.vibrate(300);

                        //CircleOptions circle = new CircleOptions();
                        //Location.distanceBetween(latitude,longitude,circle.get);
                        if (CalculateDistance.checkDistanceRadius(mLastLocation, latitude, longitude)) {
                            // Vibrate for 300 milliseconds
                            v.vibrate(300);
                        }
                        showNotification("Phản hồi tuyến đường từ tài khoản " + user);
                        refreshScreen(MainActivity.this);

                        //30/10/2017
                        //tự động thay đổi tuyến đường nếu phát hiện vị trí kẹt
                        Location location = new Location("report");
                        location.setLatitude(latitude);
                        location.setLongitude(longitude);
                        trafficLocationList.add(location);
                        if (fetchUrl != null) {
                            //if (fetchUrl.checkLocationOnPolyline(mLastLocation, location)) { //điểm phản hồi có trùng với vị trí đi
                            //removePointsMarker();
                            mLastLocation = mMap.getMyLocation();
                            //updateCurrentLocation();
                            LatLng latlng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                            //fetchUrl.setRandomFlag(true);
                            fetchUrl.changeRoute(new LatLng(location.getLatitude(), location.getLongitude()), endPointMarker.getPosition(), trafficLocationList);
                            //}
                        }

                    }catch (Exception ex){

                    }
                }
            });
        }
    };

    private Emitter.Listener messageReponseListListener = new Emitter.Listener(){

        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i("messageReport",args[0].toString());
                    try {
                        HashMap<LatLng, MessageReport> messageReportList=new HashMap<LatLng, MessageReport>();
                        JSONArray jsonArray=new JSONArray(args[0].toString());
                        if(jsonArrayMessages==null||jsonArray!=jsonArrayMessages){
                            for(int i=0;i<jsonArray.length();i++){
                                JSONObject jsonObject=jsonArray.getJSONObject(i);
                                String user = jsonObject.getString("phoneNumber");
                                int type=jsonObject.getInt("type");
                                String messageContent=jsonObject.getString("messageContent");
                                double latitude = jsonObject.getDouble("latitude");
                                double longitude = jsonObject.getDouble("longitude");
                                String imageURL="";
                                if(jsonObject.has("imageURL"))
                                    imageURL = jsonObject.getString("imageURL");
                                String createdDateTime=jsonObject.getString("createdDateTime");
                                String invalidDateTime=jsonObject.getString("invalidDateTime");
                                JSONArray jsonConfirmArray,jsonViolateArray;
                                ArrayList<String> confirmArray = null,violateArray=null;
                                if(jsonObject.has("confirm")) {
                                    jsonConfirmArray = jsonObject.getJSONArray("confirm");
                                    confirmArray = new ArrayList<String>();
                                    for (int j = 0; j < jsonConfirmArray.length(); j++)
                                        confirmArray.add(jsonConfirmArray.getJSONObject(j).getString("phoneNumber"));
                                }
                                if(jsonObject.has("violate")) {
                                    jsonViolateArray = jsonObject.getJSONArray("violate");
                                    violateArray = new ArrayList<String>();
                                    for (int j = 0; j < jsonViolateArray.length(); j++)
                                        violateArray.add(jsonViolateArray.getJSONObject(j).getString("phoneNumber"));
                                }
                                LatLng point = new LatLng(latitude, longitude);


                                messageReportList.put(point,new MessageReport(user,latitude,longitude,createdDateTime,invalidDateTime,type,imageURL,messageContent,confirmArray,violateArray));

//                        // Creating MarkerOptions
//                        MarkerOptions options = new MarkerOptions();
//                        options.position(point);
//                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
//
//                        // Setting the position of the marker
//                        options.position(point);
//                        options.title("Thông báo từ tài khoản " + user);
//                        options.snippet("Snippet");
//                        mMap.addMarker(options);

//                        Message.addOneMessageReport(getApplicationContext(),mMap,new MessageReport(user,latitude,longitude,))


//                                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//                                // Vibrate for 300 milliseconds
//                                v.vibrate(300);
//
//                                //CircleOptions circle = new CircleOptions();
//                                //Location.distanceBetween(latitude,longitude,circle.get);
//                                if (CalculateDistance.checkDistanceRadius(mLastLocation, latitude, longitude)) {
//                                    // Vibrate for 300 milliseconds
//                                    v.vibrate(300);
//                                }
//                                showNotification("Phản hồi tuyến đường từ tài khoản " + user);
//
//                                Location location = new Location("report");
//                                location.setLatitude(latitude);
//                                location.setLongitude(longitude);
//                                trafficLocationList.add(location);
                            }

                            Message.addMessageReportList(messageReportList);
                            Message.showMessageReportMarkerList(getApplicationContext(),mMap);

                            if(Message.getSizeOfMessageMarkerList()>0) {
                                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                // Vibrate for 300 milliseconds
                                v.vibrate(300);
                            }

                            jsonArrayMessages=jsonArray;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener cameraResponseListener = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            final String str = args[0].toString();
            runOnUiThread(new Runnable() {
                public void run() {
//                    Toast.makeText(MainActivity.this,str,Toast.LENGTH_LONG).show();
                    if(!str.equals(jsonCaremaStr)) {
                        try {
                            JSONArray array = new JSONArray(str);
                            HashMap<LatLng, String> cameraListTemp = new HashMap<LatLng, String>();
                            for (int i = 0; i < array.length(); i++) {
                                String id = array.getJSONObject(i).getString("id");
                                double lat = array.getJSONObject(i).getDouble("lat");
                                double lng = array.getJSONObject(i).getDouble("lng");
                                cameraListTemp.put(new LatLng(lat, lng), id);
                            }
                            Camera.showCamerasListExtra(MainActivity.this, mMap, cameraListTemp);
                            jsonCaremaStr=str;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else{
                        Camera.showCamerasListExtra(MainActivity.this,mMap);
                    }

                }
            });
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        TabInterfaceActivity.resetMapStyle(MainActivity.this);
        //mSocket.disconnect();
        //mSocket.off("notificationReport", notificationReportListener);
    }

    android.location.LocationListener locationListener = new android.location.LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            mLastLocation = location;
            fetchUrl.drawMapPolylines(new LatLng(location.getLatitude(), location.getLongitude()));
            //version - 30/10/2017
//            if (fetchUrl != null && fetchUrl.getStopFlag() == false) {//&&CalculateDistance.getDistance(new LatLng(location.getLatitude(),location.getLongitude()),new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()))>=10) {
//                if (countToCheck == 10) {
//                    if (fetchUrl.checkDistanceAndSpeech(location) == false) {
//                        if (countToSpeech == 2) {
//                            LatLng origin = new LatLng(location.getLatitude(), location.getLongitude());
//                            LatLng dest = mDestLatLng;
//                            removePointsMarker();
//                            MarkerOptions options = new MarkerOptions();
//                            options.position(origin);
//                            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
//                            options.title("Điểm bắt đầu");
//                            startPointMarker = mMap.addMarker(options);
//
//                            options = new MarkerOptions();
//                            options.position(dest);
//                            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
//                            options.title("Điểm đến");
//                            endPointMarker = mMap.addMarker(options);
//                            fetchUrl = new FetchUrl(MainActivity.this, mMap, origin, dest, false);//, false);
//                            countToSpeech = 0;
//                        } else {
//                            countToSpeech++;
//                        }
//                    } else {
//                        countToSpeech = 0;
//                    }
//                    countToCheck = 0;
//                } else {
//                    countToCheck++;
//                }
//            }
            if (location.hasSpeed()) {
                currentSpeed = location.getSpeed() * 3.6;
            }
            lblCurrentSpeed.setText("Hiện tại: " + String.format("%.1f", currentSpeed) + " km/h");
            if (currentSpeed > maxSpeed) {
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 300 milliseconds
                v.vibrate(300);
                showNotification("Xin hãy giảm tốc độ");
                new Text2Speech(MainActivity.this, new String("Bạn đã chạy vượt quá tốc độ cho phép")).speechText();
            }
            if (cameraChangeFlag == false || startFlag == true) {
                //Place current location marker
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                //move map camera
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(18));
                if (countChangeStartLocation == 1 && startFlag)
                    startFlag = false;
                else
                    countChangeStartLocation++;
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;

        refreshScreen(getApplicationContext());

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
//                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//                mMap.animateCamera(CameraUpdateFactory.zoomTo(18));
//                cameraChangeFlag = false;
                if (startFlag)
                    startFlag = false;

                if(currentMarkerInfo!=null&&currentMarkerInfo.isInfoWindowShown())
                    currentMarkerInfo.hideInfoWindow();
            }
        });
        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                if (startFlag)
                    cameraChangeFlag = false;
                else
                    cameraChangeFlag = true;
            }
        });
//        mLastLocation = mLocationManager.getLastKnownLocation(provider);
        updateCurrentLocation();

        // Setting onclick event listener for the map
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            //mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapLongClick(LatLng point) {
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(300);
                //updateCurrentLocation();
                showDialogOptions(point);
            }
        });
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mLastLocation = mMap.getMyLocation();
            }
        });

        //set InfoWindow Google Map
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(final Marker marker) {
                switch (marker.getTitle()) {
                    case "camera":
                        View v = getLayoutInflater().inflate(R.layout.custom_camera_window, null);
                        currentMarkerInfo=marker;
                        final LatLng latLngCamera = marker.getPosition();
                        final ImageView ivThumbnail = (ImageView) v.findViewById(R.id.ivThumbnail);
                        Picasso.with(getApplicationContext()).load(Camera.getLinkCameraPreview(latLngCamera)).resize(512, 288).into(ivThumbnail);
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (marker.isInfoWindowShown()) {
                                    //Log.i("camera",Camera.getLinkCameraPreview(latLng,miliSecondStr));
                                    Picasso.with(getApplicationContext()).load(Camera.getLinkCameraPreview(latLngCamera)).resize(512, 288).into(ivThumbnail);
                                    marker.showInfoWindow();
                                    handler.removeCallbacks(this);
                                }
//                            handler.postDelayed(this,1500);
                            }
                        }, 2000);
                        return v;
                    case "report":
                        View view_message = getLayoutInflater().inflate(R.layout.custom_message_window, null);
                        final LatLng latLngMessage=marker.getPosition();
                        final ImageView imgThumbMessage= (ImageView) view_message.findViewById(R.id.imgThumbMessage);
                        TextView txtMessageContentThumb= (TextView) view_message.findViewById(R.id.txtMessageContentThumb);
                        txtMessageContentThumb.setText(Message.getMessageReport(latLngMessage).getMessageContent());
                        String imageURL=Message.getMessageReport(latLngMessage).getImageURL();
                        if(!imageURL.equals("")) {
                            Picasso.with(getApplicationContext()).load(imageURL).resize(512, 288).into(imgThumbMessage);
                            final Handler Messagehandler = new Handler();
                            Messagehandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (marker.isInfoWindowShown()) {
                                        //Log.i("camera",Camera.getLinkCameraPreview(latLng,miliSecondStr));
                                        String imageURL = Message.getMessageReport(latLngMessage).getImageURL();
                                        if (!imageURL.equals(""))
                                            Picasso.with(getApplicationContext()).load(imageURL).resize(512, 288).into(imgThumbMessage);
                                        marker.showInfoWindow();
                                        Messagehandler.removeCallbacks(this);
                                    }
//                            handler.postDelayed(this,1500);
                                }
                            }, 500);
                        }
                        return view_message;
                    case "Trạm Đo Mưa":
                        View view_tramdomua=getLayoutInflater().inflate(R.layout.custom_flooding_window, null);
                        TextView lblTitleTramDoMua= (TextView) view_tramdomua.findViewById(R.id.lblTitleFlooding);
                        lblTitleTramDoMua.setText("Trạm Đo Mưa");
                        final LatLng latLngTramDoMua=marker.getPosition();
                        TextView lblVitriTramDoMua= (TextView) view_tramdomua.findViewById(R.id.lblViTriFlooding);
                        lblVitriTramDoMua.setText(Flooding.getTramDoMuaItem(latLngTramDoMua).getTentram());
                        TextView lblNameMucNuoc=(TextView)view_tramdomua.findViewById(R.id.lblNameMucNuocTrieuFlooding);
                        lblNameMucNuoc.setText("Mực nước: ");
                        TextView lblMucNuoc=(TextView)view_tramdomua.findViewById(R.id.lblMucNuocTrieuFlooding);
                        lblMucNuoc.setText(String.valueOf(Flooding.getTramDoMuaItem(latLngTramDoMua).getMucnuoc())+" mét");
                        TextView lblTrangThaiMucNuoc=(TextView)view_tramdomua.findViewById(R.id.lblTrangThaiFlooding);
                        lblTrangThaiMucNuoc.setText(Flooding.getTramDoMuaItem(latLngTramDoMua).getTrangthai());
                        TextView lblThoiDiemMucNuoc= (TextView) view_tramdomua.findViewById(R.id.lblThoiDiemCapNhatFlooding);
                        lblThoiDiemMucNuoc.setText(Flooding.getTramDoMuaItem(latLngTramDoMua).getThoidiem());
                        return view_tramdomua;
                    case "Trạm Đo Triều":
                        View view_tramdotrieu=getLayoutInflater().inflate(R.layout.custom_flooding_window, null);
                        TextView lblTitleTramDoTrieu= (TextView) view_tramdotrieu.findViewById(R.id.lblTitleFlooding);
                        lblTitleTramDoTrieu.setText("Trạm Đo Triều Cường");
                        final LatLng latLngTramDoTrieu=marker.getPosition();
                        TextView lblVitriTramDoTrieu= (TextView) view_tramdotrieu.findViewById(R.id.lblViTriFlooding);
                        lblVitriTramDoTrieu.setText(Flooding.getTramDoTrieuItem(latLngTramDoTrieu).getTentramtrieu());
                        TextView lblNameMucTrieu=(TextView)view_tramdotrieu.findViewById(R.id.lblNameMucNuocTrieuFlooding);
                        lblNameMucTrieu.setText("Mức triều: ");
                        TextView lblMucTrieu=(TextView)view_tramdotrieu.findViewById(R.id.lblMucNuocTrieuFlooding);
                        lblMucTrieu.setText(String.valueOf(Flooding.getTramDoTrieuItem(latLngTramDoTrieu).getMuctrieu())+" mét");
                        TextView lblTrangThaiMucTrieu=(TextView)view_tramdotrieu.findViewById(R.id.lblTrangThaiFlooding);
                        lblTrangThaiMucTrieu.setText(Flooding.getTramDoTrieuItem(latLngTramDoTrieu).getTentrangthai());
                        TextView lblThoiDiemMucTrieu= (TextView) view_tramdotrieu.findViewById(R.id.lblThoiDiemCapNhatFlooding);
                        lblThoiDiemMucTrieu.setText(Flooding.getTramDoTrieuItem(latLngTramDoTrieu).getThoidiem());
                        return view_tramdotrieu;
                }
                return null;
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                switch (marker.getTitle()) {
                    case "camera":
                        Intent detailCamera = new Intent(MainActivity.this, NewsDetailActivity.class);
                        LatLng latLngCamera = marker.getPosition();
                        detailCamera.putExtra("LINK", Camera.getLinkCamera(latLngCamera));
                        startActivity(detailCamera);
                        break;
                    case "report":
                        Intent detailMessage=new Intent(MainActivity.this,MessageActivity.class);
                        LatLng latLngMessage=marker.getPosition();
                        Bundle b=new Bundle();
                        b.putString("PHONENUMBER",phoneNumber);
                        b.putDouble("LAT",latLngMessage.latitude);
                        b.putDouble("LNG",latLngMessage.longitude);
                        detailMessage.putExtras(b);
                        startActivity(detailMessage);
                        break;
                }
            }
        });
    }

    private void updateCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            // Define the criteria how to select the locatioin provider -> use
            // default
            Criteria criteria = new Criteria();
            provider = mLocationManager.getBestProvider(criteria, false);
        }
        mLastLocation = mLocationManager.getLastKnownLocation(provider);
    }

    private void showDialogOptions(final LatLng point) {
//        String cityName = null, StreetName = null, state = null, zip = null, country = null;
//        Geocoder gcd = new Geocoder(MainActivity.this);
//        List<Address> addresses;
//        try {
//            addresses = gcd.getFromLocation(point.latitude,point.longitude, 1);
//            if (addresses.size() > 0)
//                StreetName = addresses.get(0).getThoroughfare();
//            String a=addresses.get(0).getSubThoroughfare();
//            String b=addresses.get(0).getAddressLine(0);
//            String c=addresses.get(0).getCountryCode();
//            String d=addresses.get(0).getFeatureName();
//            String e=addresses.get(0).getLocality();
//            String f=addresses.get(0).getPhone();
//            String g=addresses.get(0).getPostalCode();
//            String h=addresses.get(0).getPremises();
//            String i=addresses.get(0).getSubAdminArea();
//            String k=addresses.get(0).getSubLocality();
//            Log.i("geocoder","a: "+a+ " - b: "+b+" - c: "+c+" - d: "+d+" - e: "+e+" - f: "+f+" - g: "+g+" - h: "+h+" - i: "+i+" - k: "+k );
//            cityName = addresses.get(0).getLocality();
//            state = addresses.get(0).getAdminArea();
//            zip = addresses.get(0).getPostalCode();
//            country = addresses.get(0).getCountryName();
//            String s = point.latitude + "\n" + point.longitude +
//                    "\n\nMy Currrent Street is: " + StreetName + " - " + cityName + " - " + state + " - " + zip + " - " + country;
//            Toast.makeText(MainActivity.this, s, Toast.LENGTH_LONG).show();
//            Log.i("abc","fdkafd");
//            Log.i("abc", addresses.get(0).getAddressLine(0) + " - " + addresses.get(0).getFeatureName() + " - " + addresses.get(0).getLocality() + " - " + addresses.get(0).getPhone() + " - " + addresses.get(0).getPremises() + " - " + addresses.get(0).getSubAdminArea() + " - " + addresses.get(0).getSubLocality() + " - " + addresses.get(0).getUrl() + " - " + addresses.get(0).getMaxAddressLineIndex());
//            Log.i("geocoder",StreetName + " - " + cityName + " - " + state + " - " + zip + " - " + country);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Lựa chọn");
        builder.setItems(new CharSequence[]
                        {"Lưu vị trí", "Thông tin thời tiết", "Đi đến đây", "Gửi phản hồi","Gửi tin nhắn SMS"},
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent intent=new Intent(MainActivity.this,FavLocationDetailActivity.class);
                                intent.putExtra("Lat",String.valueOf(point.latitude));
                                intent.putExtra("Lng",String.valueOf(point.longitude));
                                startActivity(intent);
                                break;
                            case 1:
                                startActivity(new Intent(MainActivity.this, WeatherActivity.class).putExtra("LOCATION", String.valueOf(point.latitude) + "," + String.valueOf(point.longitude)));
//                                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//                                v.vibrate(300);
                                break;
                            case 2:
                                removePointsMarker();

                                mLastLocation = mMap.getMyLocation();
                                //updateCurrentLocation();
                                LatLng origin = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                                LatLng dest = point;

                                MarkerOptions options = new MarkerOptions();
                                options.position(origin);
                                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                options.title("Điểm bắt đầu");
                                startPointMarker = mMap.addMarker(options);

                                options = new MarkerOptions();
                                options.position(point);
                                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                options.title("Điểm đến");
                                endPointMarker = mMap.addMarker(options);

                                mDestLatLng = endPointMarker.getPosition();

                                fetchUrl = new FetchUrl(MainActivity.this, mMap, origin, dest, true);//, false);

                                mMap.moveCamera(CameraUpdateFactory.newLatLng(origin));
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(18));

                                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                v.vibrate(300);
                                break;
                            case 3:
                                if (phoneNumber == null && loginFlag == false) {
                                    Toast.makeText(MainActivity.this, "Bạn vui lòng đăng nhập để sử dụng tính năng này", Toast.LENGTH_LONG).show();
                                } else {
                                    startActivity(new Intent(MainActivity.this, SendReportPopup.class).putExtra("phoneNumber", phoneNumber).putExtra("latitude", point.latitude).putExtra("longitude", point.longitude));
                                }
                                break;
                            case 4:
                                Retrofit.Builder builder=new Retrofit.Builder().baseUrl(URLConnection.getURLConnection()).addConverterFactory(GsonConverterFactory.create());

                                Retrofit retrofit=builder.build();

                                ShortenURLRepository client=retrofit.create(ShortenURLRepository.class);

                                Call<ShortenURLResult> call=client.getShortenURL(String.valueOf(point.latitude),String.valueOf(point.longitude));

                                call.enqueue(new Callback<ShortenURLResult>() {
                                    @Override
                                    public void onResponse(Call<ShortenURLResult> call, Response<ShortenURLResult> response) {
                                        ShortenURLResult repos=response.body();
                                        if(repos.getSuccess()){
                                            String url=URLConnection.getURLConnection()+"/"+repos.getCode();
                                            Log.i("code_url",repos.getCode()+" - "+url);
                                            Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"));
                                            smsIntent.putExtra("sms_body", "Xin chào, người bạn của bạn đã đánh dấu địa điểm này trên bản đồ và gửi đến bạn. Vui lòng truy cập vào đường dẫn: "+url);
                                            startActivity(smsIntent);
                                        }else{
                                            Toast.makeText(MainActivity.this,"Không thể lấy đường dẫn",Toast.LENGTH_LONG).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ShortenURLResult> call, Throwable t) {
                                        Toast.makeText(MainActivity.this, "Không thể kết nối với server", Toast.LENGTH_LONG).show();
                                    }
                                });
                                break;
                        }
                    }
                });
        builder.create().show();
    }

    private void registerInternetCheckReceiver() {
        IntentFilter internetFilter = new IntentFilter();
        internetFilter.addAction("android.net.wifi.STATE_CHANGE");
        internetFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(broadcastReceiver, internetFilter);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String status = null;
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            status = "Không có kết nối Internet";
            connectedServerFlag=false;
            if (null != activeNetwork) {
                if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                    status = "Đã kết nối với wifi";

                if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                    status = "Đã kết nối với mạng dữ liệu di động";
            }

            if (status.contains("Không")) {
                snackbar = Snackbar.make(findViewById(android.R.id.content), status, Snackbar.LENGTH_INDEFINITE).setAction("Vào Cài đặt", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                    }
                });
                snackbar.show();
            } else {
                if (snackbar != null)
                    snackbar.dismiss();
            }
        }
    };

    //Không được xóa - rất quan trọng
    //Sau này thêm vào chức năng: thêm latlng vào bảng history
//    @Override
//    public void onLocationChanged(Location location) {
//        Toast.makeText(getApplicationContext(),"Vị trí có thay đổi",Toast.LENGTH_LONG).show();
//
//        mLastLocation = location;
//
//        if (fetchUrl != null && fetchUrl.getStopFlag() == false){//&&CalculateDistance.getDistance(new LatLng(location.getLatitude(),location.getLongitude()),new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()))>=10) {
//            if (countToCheck == 3) {
//                if (fetchUrl.checkDistanceAndSpeech(location) == false) {
//                    if (countToSpeech == 2) {
//                        LatLng origin = new LatLng(location.getLatitude(), location.getLongitude());
//                        LatLng dest = mDestLatLng;
//                        removePointsMarker();
//                        MarkerOptions options = new MarkerOptions();
//                        options.position(origin);
//                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
//                        options.title("Điểm bắt đầu");
//                        startPointMarker = mMap.addMarker(options);
//
//                        options = new MarkerOptions();
//                        options.position(dest);
//                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
//                        options.title("Điểm đến");
//                        endPointMarker = mMap.addMarker(options);
//                        fetchUrl = new FetchUrl(MainActivity.this, mMap, origin, dest, false,false);
//                        countToSpeech = 0;
//                    } else {
//                        countToSpeech++;
//                    }
//                } else {
//                    countToSpeech = 0;
//                }
//                countToCheck = 0;
//            } else {
//                countToCheck++;
//            }
//        }
//        if (location.hasSpeed()) {
//            currentSpeed = location.getSpeed()*3.6;
//            Toast.makeText(MainActivity.this, "tốc độ: "+currentSpeed, Toast.LENGTH_SHORT).show();
//        }
////        lblMaxSpeed.setText("Tốc độ tối đa: " + maxSpeed + " km/h");
////                lblCurrentSpeed.setText("Tốc độ hiện tại: "+currentSpeed+" km/h");
//        lblCurrentSpeed.setText("Hiện tại: " + String.format("%.2f", currentSpeed)+" km/h");
//        if (currentSpeed > maxSpeed) {
//            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//            // Vibrate for 300 milliseconds
//            v.vibrate(300);
//            showNotification("Bạn vui lòng giảm tốc độ");
//            new Text2Speech(this,new String("Bạn đã chạy vượt quá tốc độ cho phép")).speechText();
//            //new Text2Speech(MainActivity.this,new String[]{"Bạn vui lòng giảm tốc độ. Tốc độ tối đa cho phép trên tuyến đường này là "+maxSpeed}).speechText();
//        }
//
//
//        if (cameraChangeFlag == false || startFlag == true) {
//            //Place current location marker
//            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//
//            //move map camera
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//            mMap.animateCamera(CameraUpdateFactory.zoomTo(18));
//
//            if (countChangeStartLocation == 1 && startFlag)
//                startFlag = false;
//            else
//                countChangeStartLocation++;
//        }
//    }

    public void removePointsMarker() {
        if (startPointMarker != null)
            startPointMarker.remove();
        if (endPointMarker != null)
            endPointMarker.remove();
    }

    private void showNotification(String str) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icon_app) // notification icon
                .setContentTitle("Chú ý") // title for notification
                .setContentText(str) // message for notification
                .setAutoCancel(true); // clear notification after click
        Intent intent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);
        NotificationManager NM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NM.notify(0, mBuilder.build());
    }

    //Init Radio
    private void initRadio() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Chọn kênh Radio");
        builder.setItems(new CharSequence[]
                        {"VOV Giao Thông Hà Nội", "VOV Giao Thông TP.Hồ Chí Minh"},
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Radio.playRadio(MainActivity.this, Radio.RADIO_HANOI);
                                break;
                            case 1:
                                Radio.playRadio(MainActivity.this, Radio.RADIO_TPHOCHIMINH);
                                break;
                            default:
                                switchRadio.setChecked(false);
                        }
                    }
                });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                switchRadio.setChecked(false);
            }
        });
        builder.create().show();
    }

    private void refreshScreen(Context view) {
        if(mMap!=null) {
            int numPanels = TabInterfaceActivity.getMapStyle(view);
            switch (numPanels){
                case 0:
                    try {
                        Calendar rightNow = Calendar.getInstance();
                        int currentHour = rightNow.get(Calendar.HOUR_OF_DAY);
                        if (currentHour >= 1 && currentHour <= 3)
                            mMap.setMapStyle(
                                    MapStyleOptions.loadRawResourceStyle(
                                            this, R.raw.style_maps_dark));
                        else if (currentHour >= 4 && currentHour <= 6)
                            mMap.setMapStyle(
                                    MapStyleOptions.loadRawResourceStyle(
                                            this, R.raw.style_maps_silver));
                        else if (currentHour >= 7 && currentHour <= 12)
                            mMap.setMapStyle(null);
                        else if(currentHour>=13&&currentHour<=17)
                            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,R.raw.style_maps_retro));
                        else if(currentHour>=18&&currentHour<=20)
                            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,R.raw.style_maps_aubergine));
                        else
                            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,R.raw.style_maps_night));
                    } catch (Resources.NotFoundException e) {

                    }
                    break;
                case 1:
                    mMap.setMapStyle(null);
                    break;
                case 2:
                    mMap.setMapStyle(
                            MapStyleOptions.loadRawResourceStyle(
                                    this, R.raw.style_maps_silver));
                    break;
                case 3:
                    mMap.setMapStyle(
                            MapStyleOptions.loadRawResourceStyle(
                                    this, R.raw.style_maps_retro));
                    break;
                case 4:
                    mMap.setMapStyle(
                            MapStyleOptions.loadRawResourceStyle(
                                    this, R.raw.style_maps_dark));
                    break;
                case 5:
                    mMap.setMapStyle(
                            MapStyleOptions.loadRawResourceStyle(
                                    this, R.raw.style_maps_night));
                    break;
                case 6:
                    mMap.setMapStyle(
                            MapStyleOptions.loadRawResourceStyle(
                                    this, R.raw.style_maps_aubergine));
                    break;
            }
//            Toast.makeText(MainActivity.this, "Đã thay đổi giao diện bản đồ", Toast.LENGTH_LONG).show();
        }
    }

    private void loginRetrofit(final String phoneNumber, final String password){
        Retrofit.Builder builder=new Retrofit.Builder().baseUrl(URLConnection.getURLConnection()).addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit=builder.build();

        LoginRepository client=retrofit.create(LoginRepository.class);

        Call<LoginResult> call=client.checkLogin(phoneNumber,password);

        call.enqueue(new Callback<LoginResult>() {
            @Override
            public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                LoginResult repos=response.body();
                if(repos.getSuccess()){
                    Account account=repos.getItems().get(0);
                    loginFlag = true;
                    MainActivity.this.phoneNumber = account.getPhoneNumber();
                    URLConnection.setPhoneNumber(MainActivity.this.phoneNumber);
                    lblNamePhone.setText("Xin chào: " + account.getName() + " - " + MainActivity.this.phoneNumber);
                    lblScore.setVisibility(View.VISIBLE);
                    lblScore.setText("Điểm tích lũy: " + account.getScore() + " điểm");
                    lblSignUpDate.setVisibility(View.VISIBLE);
                    lblSignUpDate.setText("Ngày đăng ký: " + account.getSignupDate());

                    Menu nav_Menu = navigationView.getMenu();
                    nav_Menu.findItem(R.id.menuAccount).setVisible(true);
                    nav_Menu.findItem(R.id.nav_login).setVisible(false);
//                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
//                    intent.putExtra("phoneNumber",account.getPhoneNumber());
//                    intent.putExtra("name",account.getName());
//                    intent.putExtra("score",account.getScore());
//                    intent.putExtra("signupDate",account.getSignupDate());
////                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
////                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    setResult(Activity.RESULT_OK,intent);
//                    finish();

                }else{
                    Toast.makeText(MainActivity.this,"Không thể đăng nhập - "+phoneNumber+" - "+password,Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivityForResult(intent, 1000);
                    //Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                    //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    //setResult(Activity.RESULT_OK,intent);
//                    finish();
                }
            }

            @Override
            public void onFailure(Call<LoginResult> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Không thể đăng nhập tài khoản, bạn vui lòng kiểm tra đường truyền Internet", Toast.LENGTH_LONG).show();
//                Intent intent=new Intent(MainActivity.this,LoginActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                setResult(Activity.RESULT_OK,intent);
//                finish();
            }
        });
    }
}