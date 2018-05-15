package com.example.administrator.qtravel.ui.nav;

import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.administrator.qtravel.MainActivity;
import com.example.administrator.qtravel.R;
import com.example.administrator.qtravel.databinding.ActivityLoginBinding;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreenByCode();
        final ActivityLoginBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        Glide.with(this)
                .load(R.drawable.splash)
                .into(binding.imageview);
        binding.btnLogin.setAlpha(0.7f);
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String account = binding.accountEdit.getText().toString();
                final String password = binding.passwordEdit.getText().toString();
                Log.d("k", "onClick: "+account+":"+password);
                if (account.equals("")){
                    Toast.makeText(LoginActivity.this, "请输入账号", Toast.LENGTH_SHORT).show();
                } else if(password.equals("")){
                    Toast.makeText(LoginActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                }else {
                    //调用Java后台登录接口
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                String path = "http://111.230.17.135:8080/QTravel/login?name=" + account + "&password=" + password;
                                URL url = new URL(path);
                                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                connection.connect();
                                int responseCode = connection.getResponseCode();
                                if (responseCode == 200) {
                                    InputStream is = connection.getInputStream();
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    byte[] buffer = new byte[1024];
                                    int len = -1;
                                    while ((len = is.read(buffer)) != -1) {
                                        baos.write(buffer, 0, len);
                                    }
                                    final String result = baos.toString();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (result.equals("1")) {
                                                Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                intent.putExtra("login", "ok");
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Toast.makeText(LoginActivity.this, "账号密码错误", Toast.LENGTH_SHORT).show();
                                            }
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
        });
        binding.traveler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.screen_zoom_in, R.anim.screen_zoom_out);
                finish();
            }
        });
        binding.linkSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.screen_zoom_in, R.anim.screen_zoom_out);
                finish();
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
