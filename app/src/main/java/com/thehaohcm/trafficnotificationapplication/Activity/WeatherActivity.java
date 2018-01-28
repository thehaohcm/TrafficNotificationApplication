package com.thehaohcm.trafficnotificationapplication.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.thehaohcm.trafficnotificationapplication.Fragment.WeatherConditionFragment;
import com.thehaohcm.trafficnotificationapplication.Interface.WeatherServiceListener;
import com.thehaohcm.trafficnotificationapplication.Model.Channel;
import com.thehaohcm.trafficnotificationapplication.Model.Condition;
import com.thehaohcm.trafficnotificationapplication.Model.Units;
import com.thehaohcm.trafficnotificationapplication.R;
import com.thehaohcm.trafficnotificationapplication.service.YahooWeatherService;

public class WeatherActivity extends AppCompatActivity implements WeatherServiceListener{

    public static int GET_WEATHER_FROM_CURRENT_LOCATION = 0x00001;

    private ImageView weatherIconImageView;
    private TextView temperatureTextView;
    private TextView conditionTextView;
    private TextView locationTextView;
    private Button btnDestWeather;

    private YahooWeatherService weatherService;

    private ProgressDialog pd;

    private String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        this.setTitle("Thông tin thời tiết");

        weatherIconImageView = (ImageView) findViewById(R.id.weatherIconImageView);
        temperatureTextView = (TextView) findViewById(R.id.temperatureTextView);
        conditionTextView = (TextView) findViewById(R.id.conditionTextView);
        locationTextView = (TextView) findViewById(R.id.locationTextView);
        btnDestWeather=(Button)findViewById(R.id.btnDestWeather);

        this.location=getIntent().getStringExtra("LOCATION");
        if(this.location!=null){
            pd=new ProgressDialog(this);
            pd.setMessage("Đang tải...");
            pd.setCancelable(false);
            pd.show();
            weatherService = new YahooWeatherService(this,this.location);
            weatherService.setTemperatureUnit("C");//(preferences.getString("temperature_unit", null));
            weatherService.refreshWeather(this.location);
        }
    }

    @Override
    public void serviceSuccess(final Channel channel) {
        pd.dismiss();
        Condition condition = channel.getItem().getCondition();
        Units units = channel.getUnits();
        Condition[] forecast = channel.getItem().getForecast();

        int weatherIconImageResource = getResources().getIdentifier("icon_" + condition.getCode(), "drawable", getPackageName());

        weatherIconImageView.setImageResource(weatherIconImageResource);
        temperatureTextView.setText(getString(R.string.temperature_output, condition.getTemperature(), units.getTemperature()));
        conditionTextView.setText(condition.getDescription());
        locationTextView.setText(channel.getLocation());
        btnDestWeather.setVisibility(View.VISIBLE);
        btnDestWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(WeatherActivity.this,NewsDetailActivity.class);
                intent.putExtra("LINK",channel.getLink());
                startActivity(intent);
            }
        });

        for (int day = 0; day < forecast.length; day++) {
            if (day >= 3) {
                break;
            }

            Condition currentCondition = forecast[day];

            int viewId = getResources().getIdentifier("forecast_" + day, "id", getPackageName());
            WeatherConditionFragment fragment = (WeatherConditionFragment) getSupportFragmentManager().findFragmentById(viewId);

            if (fragment != null) {
                fragment.loadForecast(currentCondition, channel.getUnits());
            }
        }
    }

    @Override
    public void serviceFailure(Exception exception) {
        Toast.makeText(WeatherActivity.this,"Không thể lấy thông tin thời tiết",Toast.LENGTH_LONG).show();
        btnDestWeather.setVisibility(View.INVISIBLE);
    }
}
