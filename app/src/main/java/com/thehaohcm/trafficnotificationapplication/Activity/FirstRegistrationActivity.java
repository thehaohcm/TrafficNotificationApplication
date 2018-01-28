package com.thehaohcm.trafficnotificationapplication.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hbb20.CountryCodePicker;
import com.thehaohcm.trafficnotificationapplication.Interface.Retrofit.FirstRegistrationRepository;
import com.thehaohcm.trafficnotificationapplication.Model.Retrofit.FirstRegistration;
import com.thehaohcm.trafficnotificationapplication.R;
import com.thehaohcm.trafficnotificationapplication.controller.URLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FirstRegistrationActivity extends AppCompatActivity {

    Button btnNext;
    CountryCodePicker ccp;
    EditText txtPhone;
    TextView lblCheckPhoneFail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_registration);

        setTitle("Đăng ký tài khoản");

        btnNext = (Button) findViewById(R.id.btnNext);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        txtPhone = (EditText) findViewById(R.id.txtPhone);
        lblCheckPhoneFail=(TextView)findViewById(R.id.lblCheckPhoneFail);

        ccp.setDefaultCountryUsingNameCode("VN");
        ccp.resetToDefaultCountry();

        try {
            TelephonyManager telemamanger = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            txtPhone.setText(telemamanger.getLine1Number());
        }catch(Exception ex){

        }

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtPhone.getText().toString().matches("")) {
                    Toast.makeText(getApplicationContext(), "Bạn vui lòng nhập vào số điện thoại!",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                if (txtPhone.getText().toString().length() < 9) {
                    Toast.makeText(getApplicationContext(), "Bạn vui lòng nhập vào số điện thoại hợp lệ!",
                            Toast.LENGTH_LONG).show();
                    lblCheckPhoneFail.setText("Bạn vui lòng nhập vào số điện thoại hợp lệ!");
                    lblCheckPhoneFail.setVisibility(View.VISIBLE);
                    return;
                }
                if (txtPhone.getText().toString().startsWith("0")) {
                    txtPhone.setText(txtPhone.getText().toString().substring(1));
                }
                ccp.registerCarrierNumberEditText(txtPhone);


//                Toast.makeText(FirstRegistrationActivity.this,"full number: "+ccp.getFullNumberWithPlus(),Toast.LENGTH_LONG).show();
                initRetrofit(ccp.getFullNumberWithPlus());
            }
        });
    }

    private void initRetrofit(final String phoneNumber){
        Retrofit.Builder builder=new Retrofit.Builder().baseUrl(URLConnection.getURLConnection()).addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit=builder.build();

        FirstRegistrationRepository client=retrofit.create(FirstRegistrationRepository.class);

        Call<FirstRegistration> call=client.checkphoneNumber(phoneNumber);

        call.enqueue(new Callback<FirstRegistration>() {
            @Override
            public void onResponse(Call<FirstRegistration> call, Response<FirstRegistration> response) {
                FirstRegistration repos=response.body();
                if(!repos.getSuccess()){
                    Intent intent = new Intent(FirstRegistrationActivity.this, RegistrationActivity.class);
                    intent.putExtra("phoneNumber",phoneNumber);
                    startActivity(intent);
                }else{
                    lblCheckPhoneFail.setText(repos.getDescription());
                    Toast.makeText(FirstRegistrationActivity.this,"Đã có số tài khoản này trong hệ thống.",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<FirstRegistration> call, Throwable t) {
                Toast.makeText(FirstRegistrationActivity.this, "Không thể kiểm tra số điện thoại của bạn", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
