/*
 * Copyright (C) 2016 Baidu, Inc. All Rights Reserved.
 */
package com.example.administrator.qtravel.ui.bikenavi;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.baidu.mapapi.bikenavi.BikeNavigateHelper;
import com.baidu.mapapi.bikenavi.adapter.IBRouteGuidanceListener;
import com.baidu.mapapi.bikenavi.adapter.IBTTSPlayer;
import com.baidu.mapapi.bikenavi.model.BikeRouteDetailInfo;
import com.baidu.mapapi.bikenavi.model.RouteGuideKind;
import com.baidu.mapapi.bikenavi.params.BikeNaviLaunchParam;
import com.example.administrator.qtravel.ui.LocationActivity;

import java.util.ArrayList;

public class BNaviGuideActivity extends Activity {

    private BikeNavigateHelper mNaviHelper;

    BikeNaviLaunchParam param;

    ArrayList<String> poiList = new ArrayList<>();
    ArrayList<String> locX = new ArrayList<>();
    ArrayList<String> locY = new ArrayList<>();


    @Override
    protected void onDestroy() {
        Log.d("BNaviGuideActivity", "onDestroy: ");
        super.onDestroy();
        mNaviHelper.quit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mNaviHelper.resume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("BNaviGuideActivity", "onCreate: ");
        Intent intent = getIntent();
        poiList = intent.getStringArrayListExtra("poiList");
        locX = intent.getStringArrayListExtra("locX");
        locY = intent.getStringArrayListExtra("locY");

        mNaviHelper = BikeNavigateHelper.getInstance();

        View view = mNaviHelper.onCreate(BNaviGuideActivity.this);
        if (view != null) {
            setContentView(view);
        }

        mNaviHelper.startBikeNavi(BNaviGuideActivity.this);

        mNaviHelper.setTTsPlayer(new IBTTSPlayer() {
            @Override
            public int playTTSText(String s, boolean b) {
                Log.d("tts", s);
                return 0;
            }
        });

        mNaviHelper.setRouteGuidanceListener(this, new IBRouteGuidanceListener() {
            @Override
            public void onRouteGuideIconUpdate(Drawable icon) {

            }

            @Override
            public void onRouteGuideKind(RouteGuideKind routeGuideKind) {

            }

            @Override
            public void onRoadGuideTextUpdate(CharSequence charSequence, CharSequence charSequence1) {

            }

            @Override
            public void onRemainDistanceUpdate(CharSequence charSequence) {

            }

            @Override
            public void onRemainTimeUpdate(CharSequence charSequence) {

            }

            @Override
            public void onGpsStatusChange(CharSequence charSequence, Drawable drawable) {

            }

            @Override
            public void onRouteFarAway(CharSequence charSequence, Drawable drawable) {

            }

            @Override
            public void onRoutePlanYawing(CharSequence charSequence, Drawable drawable) {

            }

            @Override
            public void onReRouteComplete() {

            }

            @Override
            public void onArriveDest() {
                Intent intent = new Intent(BNaviGuideActivity.this, BNaviMainActivity.class);
                intent.putStringArrayListExtra("poiList", poiList);
                intent.putStringArrayListExtra("locX", locX);
                intent.putStringArrayListExtra("locY", locY);
                startActivity(intent);
            }

            @Override
            public void onVibrate() {

            }

            @Override
            public void onGetRouteDetailInfo(BikeRouteDetailInfo bikeRouteDetailInfo) {

            }
        });
    }



}
