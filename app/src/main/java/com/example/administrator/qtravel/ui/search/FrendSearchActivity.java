package com.example.administrator.qtravel.ui.search;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.qtravel.R;

import io.crossbar.autobahn.websocket.WebSocketConnection;
import io.crossbar.autobahn.websocket.WebSocketConnectionHandler;
import io.crossbar.autobahn.websocket.exceptions.WebSocketException;

public class FrendSearchActivity extends AppCompatActivity {

    TextView textView;
    EditText editText;
    Button button;
    private StringBuffer text = new StringBuffer();

    private WebSocketConnection webSocketConnection =  new WebSocketConnection();
    private String TAG = "FrendSearchActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frend_search);
        onBind();
        textView = findViewById(R.id.textView);
        textView.setMovementMethod(ScrollingMovementMethod.getInstance());
        editText = findViewById(R.id.editText);
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!editText.getText().toString().trim().equals(""))
                    webSocketConnection.sendMessage(editText.getText().toString());
                else
                    Toast.makeText(FrendSearchActivity.this, "不能为空！", Toast.LENGTH_SHORT).show();
                editText.setText("");
            }
        });
        connect();
    }

    public void onBind(){
        Toolbar mTitleToolBar = findViewById(R.id.toolbar);
        setSupportActionBar(mTitleToolBar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.back);
        }
        mTitleToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void connect(){
        try {
            webSocketConnection.connect("ws://www.huang-ray.com:8080/QTravel/websocket/chat", new WebSocketConnectionHandler(){
                @Override
                public void onOpen() {
                    Log.d(TAG, "onOpen: ");
                }

                @Override
                public void onMessage(String payload) {
                    text.append(payload+"\n");
                    textView.setText(text);
                }

                @Override
                public void onClose(int code, String reason) {
                    Log.d(TAG, "onClose: "+reason);
                }

            });
        } catch (WebSocketException e) {
            //不能主动关闭
            //webSocketConnection.sendClose(0, e.toString());
            webSocketConnection=null;
            Log.d(TAG, "WebSocketException: "+e.toString());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //webSocketConnection.sendClose(1, "客户端关闭聊天activity");
        webSocketConnection = null;
    }
}
