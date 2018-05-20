package com.example.administrator.qtravel.ui.bikenavi;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.mapapi.bikenavi.BikeNavigateHelper;
import com.baidu.mapapi.bikenavi.adapter.IBEngineInitListener;
import com.baidu.mapapi.bikenavi.adapter.IBRoutePlanListener;
import com.baidu.mapapi.bikenavi.model.BikeRoutePlanError;
import com.baidu.mapapi.bikenavi.params.BikeNaviLaunchParam;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.walknavi.WalkNavigateHelper;
import com.baidu.mapapi.walknavi.adapter.IWEngineInitListener;
import com.baidu.mapapi.walknavi.adapter.IWRoutePlanListener;
import com.baidu.mapapi.walknavi.model.WalkRoutePlanError;
import com.baidu.mapapi.walknavi.params.WalkNaviLaunchParam;
import com.example.administrator.qtravel.R;

import java.util.ArrayList;

public class BNaviMainActivity extends Activity {

    private MapView mMapView;
    private BaiduMap mBaiduMap;

    private Marker mMarkerA;
    private Marker mMarkerB;

    private LatLng startPt,endPt;

    private BikeNavigateHelper mNaviHelper;
    private WalkNavigateHelper mWNaviHelper;
    BikeNaviLaunchParam param;
    WalkNaviLaunchParam walkParam;
    private static boolean isPermissionRequested = false;

    BitmapDescriptor bdA = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_marka);
    BitmapDescriptor bdB = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_markb);


    ArrayList<String> poiList = new ArrayList<>();
    ArrayList<String> locX = new ArrayList<>();
    ArrayList<String> locY = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("BNaviMainActivity", "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_main);
        requestPermission();
        mMapView = (MapView) findViewById(R.id.mapview);

        Intent intent = getIntent();
        poiList = intent.getStringArrayListExtra("poiList");
        locX = intent.getStringArrayListExtra("locX");
        locY = intent.getStringArrayListExtra("locY");

        initMapStatus();
        initOverlay();

        Button bikeBtn = (Button) findViewById(R.id.button);
        bikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBikeNavi();
            }
        });

        Button walkBtn = (Button) findViewById(R.id.button1);
        walkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startWalkNavi();
            }
        });

        try {
            mNaviHelper = BikeNavigateHelper.getInstance();
            mWNaviHelper = WalkNavigateHelper.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(locX.size()<=1){

            finish();
            return;
        }

        startPt = new LatLng(Double.valueOf(locX.get(0)),Double.valueOf(locY.get(0)));
        endPt = new LatLng(Double.valueOf(locX.get(1)),Double.valueOf(locY.get(1)));

        param = new BikeNaviLaunchParam().stPt(startPt).endPt(endPt);
        walkParam = new WalkNaviLaunchParam().stPt(startPt).endPt(endPt);
        //不调用单车导航了 直接调起步行导航
        startWalkNavi();
        finish();
    }

    private void initMapStatus(){
        mBaiduMap = mMapView.getMap();
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(new LatLng(Double.valueOf(locX.get(0)), Double.valueOf(locY.get(0)))).zoom(19);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
    }

    public void initOverlay() {
        // add marker overlay
        Log.d("BNaviMainActivity", "initOverlay: "+locX.size());
        if(locX.size()<=1)
        {
            Toast.makeText(this, "导航结束！", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        LatLng llA = new LatLng(Double.valueOf(locX.get(0)),Double.valueOf(locY.get(0)));
        LatLng llB = new LatLng(Double.valueOf(locX.get(1)),Double.valueOf(locY.get(1)));

        MarkerOptions ooA = new MarkerOptions().position(llA).icon(bdA)
                .zIndex(9).draggable(true);

        mMarkerA = (Marker) (mBaiduMap.addOverlay(ooA));
        mMarkerA.setDraggable(true);
        MarkerOptions ooB = new MarkerOptions().position(llB).icon(bdB)
                .zIndex(5);
        mMarkerB = (Marker) (mBaiduMap.addOverlay(ooB));
        mMarkerB.setDraggable(true);


        mBaiduMap.setOnMarkerDragListener(new BaiduMap.OnMarkerDragListener() {
            public void onMarkerDrag(Marker marker) {
            }

            public void onMarkerDragEnd(Marker marker) {
                if(marker == mMarkerA){
                    startPt = marker.getPosition();
                }else if(marker == mMarkerB){
                    endPt = marker.getPosition();
                }
                param.stPt(startPt).endPt(endPt);
                walkParam.stPt(startPt).endPt(endPt);
            }

            public void onMarkerDragStart(Marker marker) {
            }
        });
    }

    private void startBikeNavi() {
        Log.d("View", "startBikeNavi");
        try {
            mNaviHelper.initNaviEngine(this, new IBEngineInitListener() {
                @Override
                public void engineInitSuccess() {
                    Log.d("View", "engineInitSuccess");
                    routePlanWithParam();
                }

                @Override
                public void engineInitFail() {
                    Log.d("View", "engineInitFail");
                }
            });
        } catch (Exception e) {
            Log.d("Exception", "startBikeNavi");
            e.printStackTrace();
        }
    }

    private void startWalkNavi() {
        Log.d("View", "startBikeNavi");
        try {
            mWNaviHelper.initNaviEngine(this, new IWEngineInitListener() {
                @Override
                public void engineInitSuccess() {
                    Log.d("View", "engineInitSuccess");
                    routePlanWithWalkParam();
                }

                @Override
                public void engineInitFail() {
                    Log.d("View", "engineInitFail");
                }
            });
        } catch (Exception e) {
            Log.d("Exception", "startBikeNavi");
            e.printStackTrace();
        }
    }

    private void routePlanWithParam() {
        mNaviHelper.routePlanWithParams(param, new IBRoutePlanListener() {
            @Override
            public void onRoutePlanStart() {
                Log.d("View", "onRoutePlanStart");
            }

            @Override
            public void onRoutePlanSuccess() {
                Log.d("View", "onRoutePlanSuccess");
                Intent intent = new Intent();
                intent.setClass(BNaviMainActivity.this, BNaviGuideActivity.class);
                startActivity(intent);
            }

            @Override
            public void onRoutePlanFail(BikeRoutePlanError error) {
                Log.d("View", "onRoutePlanFail");
            }

        });
    }
    private void routePlanWithWalkParam() {
        mWNaviHelper.routePlanWithParams(walkParam, new IWRoutePlanListener() {
            @Override
            public void onRoutePlanStart() {
                Log.d("View", "onRoutePlanStart");
            }

            @Override
            public void onRoutePlanSuccess() {
                Log.d("View", "onRoutePlanSuccess");
                Intent intent = new Intent();
                locX.remove(0);
                locY.remove(0);
                poiList.remove(0);
                intent.putStringArrayListExtra("poiList", poiList);
                intent.putStringArrayListExtra("locX", locX);
                intent.putStringArrayListExtra("locY", locY);
                intent.setClass(BNaviMainActivity.this, WNaviGuideActivity.class);
                startActivity(intent);
            }

            @Override
            public void onRoutePlanFail(WalkRoutePlanError error) {
                Log.d("View", "onRoutePlanFail");
            }

        });
    }


    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= 23 && !isPermissionRequested) {

            isPermissionRequested = true;

            ArrayList<String> permissions = new ArrayList<>();
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }

            if (permissions.size() == 0) {
                return;
            } else {
                requestPermissions(permissions.toArray(new String[permissions.size()]), 0);
            }
        }
    }

    protected void onPause() {
        Log.d("BNaviMainActivity", "onPause: ");
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onDestroy() {
        Log.d("BNaviMainActivity", "onDestroy: ");
        super.onDestroy();
        mMapView.onDestroy();
        bdA.recycle();
        bdB.recycle();
    }
}
