package com.thehaohcm.trafficnotificationapplication.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.thehaohcm.trafficnotificationapplication.Interface.Retrofit.VerificationRepository;
import com.thehaohcm.trafficnotificationapplication.Model.Retrofit.Result.LoginResult;
import com.thehaohcm.trafficnotificationapplication.Model.Retrofit.Result.VerificationResult;
import com.thehaohcm.trafficnotificationapplication.Model.Retrofit.Result.VerificationTokenResult;
import com.thehaohcm.trafficnotificationapplication.Model.Retrofit.VerificationToken;
import com.thehaohcm.trafficnotificationapplication.R;
import com.thehaohcm.trafficnotificationapplication.controller.SocketIO;
import com.thehaohcm.trafficnotificationapplication.controller.URLConnection;

import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VerificationActivity extends AppCompatActivity {

    Button btnVerification;
    EditText txtToken,txtPhoneVerifyToken;
    TextView lblVerifyFail,btnSendVerf;
    String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        setTitle("Xác thực tài khoản");

        btnVerification = (Button) findViewById(R.id.btnVerification);
        btnVerification.setEnabled(false);
        txtToken = (EditText) findViewById(R.id.txtToken);
        txtToken.addTextChangedListener(textWatcherListener);
        txtPhoneVerifyToken=(EditText)findViewById(R.id.txtPhoneVerifyToken);
        txtPhoneVerifyToken.addTextChangedListener(textWatcherListener);
        lblVerifyFail=(TextView)findViewById(R.id.lblVerifyFail);
        lblVerifyFail.setVisibility(View.INVISIBLE);
        btnSendVerf= (TextView) findViewById(R.id.btnSendVerf);

        SpannableString content = new SpannableString("Gửi lại mã xác thực");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        btnSendVerf.setText(content);
        btnSendVerf.setEnabled(false);

        btnSendVerf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumber=txtPhoneVerifyToken.getText().toString();
                AlertDialog.Builder builder = new AlertDialog.Builder(VerificationActivity.this);
                builder.setMessage("Gửi lại mã xác thực?").setPositiveButton("Có", dialogClickListener)
                        .setNegativeButton("Không", dialogClickListener).show();
            }
        });

        Bundle extras=getIntent().getExtras();
        if(extras!=null){
            this.phoneNumber=extras.getString("phoneNumber");
            txtPhoneVerifyToken.setText(phoneNumber);
            txtPhoneVerifyToken.setEnabled(false);
        }

        btnVerification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtToken.getText().toString().trim().matches("")) {
                    Toast.makeText(getApplicationContext(), "Vui lòng nhập vào mã xác thực", Toast.LENGTH_LONG).show();
                    return;
                }
                if(txtPhoneVerifyToken.toString().trim().equals("")){
                    Toast.makeText(getApplicationContext(), "Vui lòng nhập vào số điện thoại cần xác thực", Toast.LENGTH_LONG).show();
                    return;
                }

                initRetrofit(txtPhoneVerifyToken.getText().toString(),txtToken.getText().toString());

            }
        });
    }

    private void initRetrofit(final String phoneNumber, String token){
        Retrofit.Builder builder=new Retrofit.Builder().baseUrl(URLConnection.getURLConnection()).addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit=builder.build();

        VerificationRepository client=retrofit.create(VerificationRepository.class);

        Call<VerificationResult> call=client.checkToken(phoneNumber,token);

        call.enqueue(new Callback<VerificationResult>() {
            @Override
            public void onResponse(Call<VerificationResult> call, Response<VerificationResult> response) {
                VerificationResult repos=response.body();
                if(repos.getSuccess()){
                    Toast.makeText(VerificationActivity.this,"Đã xác thực thành công tài khoản "+phoneNumber,Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(VerificationActivity.this, LoginActivity.class);
                    intent.putExtra("phoneNumber",txtPhoneVerifyToken.getText().toString());
                    startActivity(intent);
                }else{
                    lblVerifyFail.setText(repos.getDescription());
                    lblVerifyFail.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<VerificationResult> call, Throwable t) {
                lblVerifyFail.setText("Không thể kết nối với server");
                lblVerifyFail.setVisibility(View.VISIBLE);
            }
        });
    }

    TextWatcher textWatcherListener=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(txtToken.getText().length()==5 &&(txtPhoneVerifyToken.getText().toString().length() >= 9&&txtPhoneVerifyToken.getText().toString().length() <=12)){
                btnVerification.setEnabled(true);
                btnSendVerf.setEnabled(true);
            }else if(txtPhoneVerifyToken.getText().toString().length() >= 9&&txtPhoneVerifyToken.getText().toString().length() <=12){
                btnVerification.setEnabled(false);
                btnSendVerf.setEnabled(true);
            }else{
                btnVerification.setEnabled(false);
                btnSendVerf.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    //Yes button clicked
                    sendVerificationToken(phoneNumber);
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    dialog.dismiss();
                    break;
            }
        }
    };

    private void sendVerificationToken(final String phoneNumber){
        Retrofit.Builder builder=new Retrofit.Builder().baseUrl(URLConnection.getURLConnection()).addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit=builder.build();

        VerificationRepository client=retrofit.create(VerificationRepository.class);

        Call<VerificationTokenResult> call=client.getToken(phoneNumber);

        call.enqueue(new Callback<VerificationTokenResult>() {
            @Override
            public void onResponse(Call<VerificationTokenResult> call, Response<VerificationTokenResult> response) {
                VerificationTokenResult repos=response.body();
                if(repos.getSuccess()){
                    Toast.makeText(VerificationActivity.this, "Đã gửi mã xác thực dến điện thoại của bạn", Toast.LENGTH_LONG).show();
                    lblVerifyFail.setVisibility(View.INVISIBLE);
                }else{
                    lblVerifyFail.setText(repos.getDescription());
                    lblVerifyFail.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<VerificationTokenResult> call, Throwable t) {
                Toast.makeText(VerificationActivity.this, "Không thể gửi mã xác thực, bạn vui lòng kiểm tra đường truyền Internet", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }
}
