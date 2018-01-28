package com.thehaohcm.trafficnotificationapplication.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.thehaohcm.trafficnotificationapplication.Interface.Retrofit.LoginRepository;
import com.thehaohcm.trafficnotificationapplication.Model.Retrofit.Account;
import com.thehaohcm.trafficnotificationapplication.Model.Retrofit.Result.LoginResult;
import com.thehaohcm.trafficnotificationapplication.R;
import com.thehaohcm.trafficnotificationapplication.controller.URLConnection;

import java.util.List;

import cz.msebera.android.httpclient.NameValuePair;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    TextView btnVerf;
    EditText txtPhoneNumberLogin,txtPasswordLogin;
    Button btnLogin, btnRegister;
    TextView lblLoginFail;
    List<NameValuePair> params;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setTitle("Đăng nhập");
        lblLoginFail = (TextView) findViewById(R.id.lblLoginFail);
        activity=this;

        txtPhoneNumberLogin = (EditText) findViewById(R.id.txtPhoneNumberLogin);
        txtPasswordLogin = (EditText) findViewById(R.id.txtPasswordLogin);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnVerf= (TextView) findViewById(R.id.btnVerf);

        SpannableString content = new SpannableString("Xác thực tài khoản");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        btnVerf.setText(content);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (txtPasswordLogin.getText().toString().trim().matches("") || txtPhoneNumberLogin.getText().toString().trim().matches("")) {
                    Toast.makeText(LoginActivity.this, "Bạn vui lòng nhập đầy đủ thông tin", Toast.LENGTH_LONG).show();
                    return;
                }

                String phoneNumber = txtPhoneNumberLogin.getText().toString();
                String password = txtPasswordLogin.getText().toString();

                initRetrofit(phoneNumber,password);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, FirstRegistrationActivity.class);
                startActivity(intent);
            }
        });

        btnVerf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,VerificationActivity.class));
            }
        });

        Bundle extras=getIntent().getExtras();
        if(extras!=null){
            txtPhoneNumberLogin.setText(extras.getString("phoneNumber"));
            txtPasswordLogin.requestFocus();
        }
    }

    private void initRetrofit(String phoneNumber,String password){
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
                    //SharedPreference
                    SharedPreferences sharedPreferences = getSharedPreferences("trafficNofiticationSharedPreferences", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("phoneNumber",account.getPhoneNumber());
                    editor.putString("password",txtPasswordLogin.getText().toString());
                    editor.apply();

                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    intent.putExtra("phoneNumber",account.getPhoneNumber());
                    intent.putExtra("name",account.getName());
                    intent.putExtra("score",account.getScore());
                    intent.putExtra("signupDate",account.getSignupDate());
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    setResult(Activity.RESULT_OK,intent);
                    finish();
                }else{
                    lblLoginFail.setText(repos.getDescription());
                    lblLoginFail.setVisibility(View.VISIBLE);
                    Toast.makeText(LoginActivity.this,"Không thể đăng nhập",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResult> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Không thể đăng nhập tài khoản, bạn vui lòng kiểm tra đường truyền Internet", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}