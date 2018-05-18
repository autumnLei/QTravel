package com.example.administrator.qtravel.ui.nav;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.administrator.qtravel.MainActivity;
import com.example.administrator.qtravel.R;
import com.example.administrator.qtravel.databinding.ActivitySignUpBinding;
import com.example.administrator.qtravel.ui.SplashActivity;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreenByCode();
        final ActivitySignUpBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);
        Glide.with(this)
                .load(R.drawable.splash)
                .into(binding.imageview);
        binding.btnRegister.setAlpha(0.7f);
        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (true) {
                    final String name = binding.accountEdit.getText().toString().trim();
                    final String password = binding.passwordEdit.getText().toString().trim();
                    if (name.equals("")){
                        Toast.makeText(SignUpActivity.this, "请输入账号", Toast.LENGTH_SHORT).show();
                    } else if(password.equals("")){
                        Toast.makeText(SignUpActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                    }else {
                    //调用Java后台登录接口
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                String path = "http://111.230.17.135:8080/QTravel/register?name=" + name + "&password=" + password;
                                URL url = new URL(path);
                                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                connection.connect();
                                int responseCode = connection.getResponseCode();
                                if(responseCode == 200){
                                    InputStream is = connection.getInputStream();
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    byte[] buffer= new byte[1024];
                                    int len = -1;
                                    while((len=is.read(buffer))!=-1){
                                        baos.write(buffer,0,len);
                                    }
                                    final String result = baos.toString();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if(result.equals("1")){
                                                Toast.makeText(SignUpActivity.this,"注册成功！", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                                intent.putExtra("login", "ok");
                                                intent.putExtra("account", name);
                                                startActivity(intent);
                                                overridePendingTransition(R.anim.screen_zoom_in, R.anim.screen_zoom_out);
                                                finish();
                                            }else if (result.equals("2")){
                                                Toast.makeText(SignUpActivity.this,"已被注册！",Toast.LENGTH_SHORT).show();
                                            }else
                                                Toast.makeText(SignUpActivity.this,"注册失败！",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                    }
                }
            }
        });

    }


    private void setFullScreenByCode() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }
}
