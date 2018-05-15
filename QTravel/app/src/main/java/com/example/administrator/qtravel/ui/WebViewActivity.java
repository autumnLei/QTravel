package com.example.administrator.qtravel.ui;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.administrator.qtravel.R;
import com.example.administrator.qtravel.databinding.ActivityWebViewBinding;
import com.example.administrator.qtravel.global.MyApplication;

public class WebViewActivity extends AppCompatActivity {

    ActivityWebViewBinding activityWebViewBinding;
    private String url = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityWebViewBinding = DataBindingUtil.setContentView(this, R.layout.activity_web_view);
        activityWebViewBinding.webView.getSettings().setJavaScriptEnabled(true);
        activityWebViewBinding.webView.setWebViewClient(new WebViewClient());
        initToolBar();
        Intent intent = getIntent();
        url = intent.getStringExtra("Url");
        if (url == null) {
            activityWebViewBinding.webView.loadUrl("https://www.baidu.com");
        }else {
            activityWebViewBinding.webView.loadUrl(url);
        }
    }

    public void initToolBar(){
        Toolbar mTitleToolBar = activityWebViewBinding.webViewToolBar;
        setSupportActionBar(mTitleToolBar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //去除默认Title显示
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.back);
        }
        mTitleToolBar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.more));
        mTitleToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mTitleToolBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.actionbar_share:// 分享到
                        share(WebViewActivity.this, url+"(分享自QTravel)");
                        break;
                    case R.id.actionbar_cope:// 复制链接
                        copy(url);
                        Toast.makeText(WebViewActivity.this, "复制成功", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.actionbar_open:// 打开链接
                        openLink(WebViewActivity.this, url);
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.webview_menu, menu);
        return true;
    }


    /**
     * 使用浏览器打开链接
     */
    public static void openLink(Context context, String content) {
        Uri issuesUrl = Uri.parse(content);
        Intent intent = new Intent(Intent.ACTION_VIEW, issuesUrl);
        context.startActivity(intent);
    }

    /**
     * 实现文本复制功能
     *
     * @param content 复制的文本
     */
    public static void copy(String content) {
        // 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager) MyApplication.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        if(content==null)
            return;
        cmb.setText(content.trim());
    }

    /**
     * 分享
     * @param context
     * @param extraText
     */
    public static void share(Context context, String extraText) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.action_share));
        intent.putExtra(Intent.EXTRA_TEXT, extraText);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(
                Intent.createChooser(intent, context.getString(R.string.action_share)));
    }
}
