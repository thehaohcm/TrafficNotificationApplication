package com.thehaohcm.trafficnotificationapplication.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.thehaohcm.trafficnotificationapplication.Interface.Retrofit.RegistrationRepository;
import com.thehaohcm.trafficnotificationapplication.Model.Retrofit.Account;
import com.thehaohcm.trafficnotificationapplication.Model.Retrofit.Result.RegistrationResult;
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

public class RegistrationActivity extends AppCompatActivity {

    Button btnRegister;
    EditText txtPhoneNumber,txtPassword,txtPasswordRe,txtName,txtEmail;
//    Socket mSocket;
    TextView lblRegistrationFail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        setTitle("Xác thực tài khoản");

        btnRegister=(Button)findViewById(R.id.btnRegister);
        txtPhoneNumber=(EditText)findViewById(R.id.txtPhoneNumber);
        txtPassword=(EditText)findViewById(R.id.txtPassword);
        txtPasswordRe=(EditText) findViewById(R.id.txtPasswordRe);
        txtName=(EditText)findViewById(R.id.txtName);
        txtEmail=(EditText)findViewById(R.id.txtEmail);
        lblRegistrationFail=(TextView)findViewById(R.id.lblRegistrationFail);

        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            txtPhoneNumber.setText(extras.getString("phoneNumber"));
        }
        else{
            Toast.makeText(RegistrationActivity.this,"Không có số điện thoại",Toast.LENGTH_LONG).show();
            finish();
            return;
        }

//        SocketIO app=(SocketIO)getApplication();
//        mSocket=app.getSocket();
//        mSocket.connect();

        final String phoneNumber=getIntent().getExtras().getString("phoneNumber");
        txtPhoneNumber.setText(phoneNumber);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate(new EditText[]{txtPhoneNumber,txtPassword,txtPasswordRe,txtName,txtEmail})==false){
                    Toast.makeText(RegistrationActivity.this,"Bạn vui lòng nhập vào đầy đủ thông tin",Toast.LENGTH_LONG).show();
                    return;
                }
                if(!android.util.Patterns.EMAIL_ADDRESS.matcher(txtEmail.getText()).matches()){
                    Toast.makeText(RegistrationActivity.this,"Bạn vui lòng nhập vào địa chỉ email hợp lệ",Toast.LENGTH_LONG).show();
                    return;
                }
                if(!txtPassword.getText().toString().matches(txtPasswordRe.getText().toString())){
                    Toast.makeText(RegistrationActivity.this,"Mật khẩu không trùng khớp, bạn vui lòng nhập lại",Toast.LENGTH_LONG).show();
                    return;
                }

                initRetrofit(txtPhoneNumber.getText().toString(),txtPassword.getText().toString(),txtName.getText().toString(),txtEmail.getText().toString());

                //Call Service
//                JSONObject registrationInfor=new JSONObject();
//                try{
//                    registrationInfor.put("phoneNumber",txtPhoneNumber.getText().toString());
//                    registrationInfor.put("password",txtPassword.getText().toString());
//                    registrationInfor.put("name",txtName.getText().toString());
//                    registrationInfor.put("email",txtEmail.getText().toString());
//
//                }catch(Exception ex){
//                    lblRegistrationFail.setText("Hệ thống không thể tạo tài khoản");
//                    lblRegistrationFail.setVisibility(View.VISIBLE);
//                    Toast.makeText(getApplicationContext(),"Đã có lỗi xảy ra, hệ thống không thể tạo tài khoản",Toast.LENGTH_LONG).show();
//                    return;
//                }
//
//                mSocket.emit("registration",registrationInfor);
                //Go to Verification Activity
//                Intent intent=new Intent(RegistrationActivity.this,VerificationActivity.class);
//                startActivity(intent);
            }
        });
//        mSocket.on("registresult",registrationUser);
    }

    private boolean validate(EditText[] fields){
        for(int i=0; i<fields.length; i++){
            EditText currentField=fields[i];
            if(currentField.getText().toString().length()<=0){
                return false;
            }
        }
        return true;
    }

    private void initRetrofit(String phoneNumber,String password,String name,String email){
        Retrofit.Builder builder=new Retrofit.Builder().baseUrl(URLConnection.getURLConnection()).addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit=builder.build();

        RegistrationRepository client=retrofit.create(RegistrationRepository.class);

        Call<RegistrationResult> call=client.registration(phoneNumber,password,name,email);

        call.enqueue(new Callback<RegistrationResult>() {
            @Override
            public void onResponse(Call<RegistrationResult> call, Response<RegistrationResult> response) {
                RegistrationResult repos=response.body();
                if(repos.getSuccess()){
//                    Intent intent=new Intent(RegistrationActivity.this,VerificationActivity.class);
//                    intent.putExtra("phoneNumber",txtPhoneNumber.getText().toString());
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(intent);

                    Toast.makeText(RegistrationActivity.this, "Bạn đã có thể đăng nhập vào phần mềm", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(RegistrationActivity.this,MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }else{
                    lblRegistrationFail.setText(repos.getDescription());
                    lblRegistrationFail.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<RegistrationResult> call, Throwable t) {
                lblRegistrationFail.setText("Không thể kết nối với server");
                lblRegistrationFail.setVisibility(View.VISIBLE);
            }
        });
    }

//    private Emitter.Listener registrationUser=new Emitter.Listener() {
//        @Override
//        public void call(Object... args) {
//            if(args[0].equals("true")){
//                Intent intent=new Intent(RegistrationActivity.this,VerificationActivity.class);
//                intent.putExtra("phoneNumber",txtPhoneNumber.getText().toString());
//                startActivity(intent);
//            }else{
//                runOnUiThread(new Runnable() {
//                    public void run() {
//                        //Toast.makeText(LoginActivity.this,"Sai thông tin đăng nhập. Bạn vui lòng đăng nhập lại",Toast.LENGTH_LONG).show();
//                        lblRegistrationFail.setText("Hệ thống không thể tạo tài khoản");
//                        lblRegistrationFail.setVisibility(View.VISIBLE);
//                    }
//                });
//            }
//        }
//    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mSocket.disconnect();
//        mSocket.off("registresult",registrationUser);
    }
}
