package com.example.administrator.qtravel.ui.search;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.example.administrator.qtravel.R;
import com.example.administrator.qtravel.adapter.ViewPagerAdapter;
import com.example.administrator.qtravel.databinding.ActivityVenueSearchBinding;

public class VenueSearchActivity extends AppCompatActivity {

    ActivityVenueSearchBinding activityVenueSearchBinding;
    ViewPagerAdapter viewPagerAdapter;
    public String local;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityVenueSearchBinding = DataBindingUtil.setContentView(this, R.layout.activity_venue_search);
        Intent intent = getIntent();
        local = intent.getStringExtra("local");
        if (local == null){
            Toast.makeText(VenueSearchActivity.this, "获取位置失败", Toast.LENGTH_SHORT).show();;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 1500);
        }
        onBindView();
    }

    public void onBindView(){
        Toolbar mTitleToolBar = activityVenueSearchBinding.toolbar;
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

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        activityVenueSearchBinding.viewPager.setAdapter(viewPagerAdapter);
        activityVenueSearchBinding.viewPager.setCurrentItem(0);
        activityVenueSearchBinding.tabLayout.setupWithViewPager(activityVenueSearchBinding.viewPager);
    }
}
