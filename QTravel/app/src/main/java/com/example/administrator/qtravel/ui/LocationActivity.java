package com.example.administrator.qtravel.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.busline.BusLineSearch;
import com.baidu.mapapi.search.busline.BusLineSearchOption;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.*;
import com.baidu.mapapi.utils.DistanceUtil;
import com.example.administrator.qtravel.R;
import com.example.administrator.qtravel.global.MyApplication;
import com.example.administrator.qtravel.overlayutil.MyOverLay;
import com.example.administrator.qtravel.service.LocationService;
import com.example.administrator.qtravel.service.Utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/***
 * 定位滤波demo，实际定位场景中，可能会存在很多的位置抖动，此示例展示了一种对定位结果进行的平滑优化处理
 * 实际测试下，该平滑策略在市区步行场景下，有明显平滑效果，有效减少了部分抖动，开放算法逻辑，希望能够对开发者提供帮助
 * 注意：该示例场景仅用于对定位结果优化处理的演示，里边相关的策略或算法并不一定适用于您的使用场景，请注意！！！
 *
 * @author baidu
 *
 */
public class LocationActivity extends Activity {
    //    @InjectView(R.id.text)
    private Button button_poi;
    private EditText et_key;
    private MapView mMapView = null;
    private BaiduMap mBaiduMap;
    private PoiSearch poiSearch;
    private BusLineSearch busLineSearch;//公交检索对象
    private LocationService locService;
    private LinkedList<LocationEntity> locationList = new LinkedList<LocationEntity>(); // 存放历史定位结果的链表，最大存放当前结果的前5次定位结果
    StringBuffer currentPosition = new StringBuffer(); //存放当前所在城市
    BDLocation location; //所在位置
    double latitude;//所在位置
    double longitude;//所在位置

    ArrayList<String> poiList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        button_poi = findViewById(R.id.button_poi);
        button_poi.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LocationActivity.this, RoutePlan.class);
                if (locationList.size()!=0){
                    poiList.add(0, locationList.getLast().location.getPoiList().get(0).getName());
                    Log.d("kkkkkkkk", "onClick: "+poiList.get(0));
                    intent.putStringArrayListExtra("poiList", poiList);
                    if (poiList.size() > 1) {
                        startActivity(intent);
                        finish();
                    }
                    else
                        Toast.makeText(LocationActivity.this, "请选择一个以上地点", Toast.LENGTH_SHORT).show();

                } else{
                    Toast.makeText(LocationActivity.this, "获取数据失败！", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
        et_key = (EditText) findViewById(R.id.editText);
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(18));
        //
        mBaiduMap.setMyLocationEnabled(true);
        //
        locService = ((MyApplication) getApplication()).locationService;
        LocationClientOption mOption = locService.getDefaultLocationClientOption();
        mOption.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
        mOption.setCoorType("bd09ll");
        locService.setLocationOption(mOption);
        locService.registerListener(listener);
        locService.start();
        initListener();
    }

    private void initListener(){
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            /**
             * 单击地图
             */
            public void onMapClick(LatLng point) {
            }

            /**
             * 单击地图中的POI点
             */
            public boolean onMapPoiClick(MapPoi poi) {
                if (poiList.size() < 5){
                    poiList.add(poi.getName());
                    Toast.makeText(LocationActivity.this, poi.getName()+"添加成功", Toast.LENGTH_SHORT).show();
                    Message message = new Message();
                    message.what = 0X1115;
                    locHander.sendMessage(message);
                } else {
                    Toast.makeText(LocationActivity.this, "添加地点已达到上限", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
    }

    /***
     * 定位结果回调，在此方法中处理定位结果
     */
    BDLocationListener listener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
            if (location != null && (location.getLocType() == 161 || location.getLocType() == 66)) {
                Message locMsg = locHander.obtainMessage();
                Bundle locData;
                locData = Algorithm(location);
                if (locData != null) {
                    locData.putParcelable("loc", location);
                    locMsg.setData(locData);
                    locHander.sendMessage(locMsg);
                }
            }
            if (currentPosition != null) {
                currentPosition = new StringBuffer();
            }
            currentPosition.append(location.getCity());
//            final BDLocation mLocation = location;

//            new Thread(
//        }new Runnable() {
//            @Override
//            public void run() {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
////                            StringBuffer currentPosition = new StringBuffer();
////                            currentPosition.append("定位城市：");
////                            currentPosition.append(mLocation.getProvince());
//                        if (currentPosition != null) {
//                            currentPosition = null;
//                        }
//                        currentPosition.append(mLocation.getCity());
////                            currentPosition.append(mLocation.getDistrict());
////                            currentPosition.append(mLocation.getStreet());
////                            if (mLocation.getLocType() == BDLocation.TypeNetWorkLocation) {
////                                currentPosition.append(" loctype:网络定位");
////                            } else if (mLocation.getLocType() == BDLocation.TypeGpsLocation) {
////                                currentPosition.append(" loctype:gps定位");
////                            } else if (mLocation.getLocType() == BDLocation.TypeOffLineLocation) {
////                                currentPosition.append(" loctype:离线定位");
////                            }
////                            text.setText(currentPosition);
//                    }
//                });
        }
        //        }).start();
        public void onConnectHotSpotMessage(String s, int i) {

        }
    };

    /***
     * 平滑策略代码实现方法，主要通过对新定位和历史定位结果进行速度评分，
     * 来判断新定位结果的抖动幅度，如果超过经验值，则判定为过大抖动，进行平滑处理,若速度过快，
     * 则推测有可能是由于运动速度本身造成的，则不进行低速平滑处理 ╭(●｀∀´●)╯
     *
     * @param BDLocation
     * @return Bundle
     */
    private Bundle Algorithm(BDLocation location) {
        Bundle locData = new Bundle();
        double curSpeed = 0;
        if (locationList.isEmpty() || locationList.size() < 2) {
            LocationEntity temp = new LocationEntity();
            temp.location = location;
            temp.time = System.currentTimeMillis();
            locData.putInt("iscalculate", 0);
            locationList.add(temp);
        } else {
            if (locationList.size() > 5)
                locationList.removeFirst();
            double score = 0;
            for (int i = 0; i < locationList.size(); ++i) {
                LatLng lastPoint = new LatLng(locationList.get(i).location.getLatitude(),
                        locationList.get(i).location.getLongitude());
                LatLng curPoint = new LatLng(location.getLatitude(), location.getLongitude());
                double distance = DistanceUtil.getDistance(lastPoint, curPoint);
                curSpeed = distance / (System.currentTimeMillis() - locationList.get(i).time) / 1000; //几秒回到原地一次
                score += curSpeed * Utils.EARTH_WEIGHT[i];
            }
            if (score > 0.00000999 && score < 0.00005) { // 经验值,开发者可根据业务自行调整，也可以不使用这种算法
                location.setLongitude(
                        (locationList.get(locationList.size() - 1).location.getLongitude() + location.getLongitude())
                                / 2);
                location.setLatitude(
                        (locationList.get(locationList.size() - 1).location.getLatitude() + location.getLatitude())
                                / 2);
                locData.putInt("iscalculate", 1);
            } else {
                locData.putInt("iscalculate", 0);
            }
            LocationEntity newLocation = new LocationEntity();
            newLocation.location = location;
            newLocation.time = System.currentTimeMillis();
            locationList.add(newLocation);
        }
        return locData;
    }

    /***
     * 接收定位结果消息，并显示在地图上
     */
    private Handler locHander = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case 0X1115:
                    if (poiList.size()==0)
                        break;
                    StringBuffer stringBuffer = new StringBuffer();
                    for (int i = 0; i < poiList.size(); i++){
                        if(i != 0){
                            stringBuffer.append(",");
                        }
                        stringBuffer.append(poiList.get(i));
                    }
                    button_poi.setText(stringBuffer);
                    break;
            }
            try {
                if (location != null) {
                    location = null;
                }
                location = msg.getData().getParcelable("loc");

//                int iscal = msg.getData().getInt("iscalculate");
                if (location != null) {
                    LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
//                    // 构建Marker图标
//                    BitmapDescriptor bitmap = null;
//                    if (iscal == 0) {
//                        bitmap = BitmapDescriptorFactory.fromResource(R.drawable.location); // 非推算结果
//                    } else {
//                        bitmap = BitmapDescriptorFactory.fromResource(R.drawable.location1); // 推算结果
//                    }

                    MyLocationData.Builder locationBuilder = new MyLocationData.Builder();
                    locationBuilder.latitude(location.getLatitude());
                    locationBuilder.longitude(location.getLongitude());
                    MyLocationData locationData = locationBuilder.build();
                    mBaiduMap.setMyLocationData(locationData);
                    // 构建MarkerOption，用于在地图上添加Marker
//                    OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);
                    // 在地图上添加Marker，并显示
//                    mBaiduMap.addOverlay(option);
                    mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(point));

                    //搜索对象的创建
                    poiSearch = PoiSearch.newInstance();
                    //设置Poi监听对象
                    poiSearch.setOnGetPoiSearchResultListener(poiSearchResultListener);

                }
            } catch (Exception e) {
                // TODO: handle exception
            }
        }

    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
//		WriteLog.getInstance().close();
        locService.unregisterListener(listener);
        locService.stop();
        mMapView.onDestroy();
        mBaiduMap.setMyLocationEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();mBaiduMap.clear();
//        reset.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                if (mBaiduMap != null)
//
//            }
//        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();

    }

    /**
     * 封装定位结果和时间的实体类
     *
     * @author baidu
     */
    class LocationEntity {
        BDLocation location;
        long time;
    }


    //POI检索的监听对象
    OnGetPoiSearchResultListener poiSearchResultListener = new OnGetPoiSearchResultListener() {
        //获得POI的检索结果，一般检索数据都是在这里获取
        @Override
        public void onGetPoiResult(PoiResult poiResult) {
            mBaiduMap.clear();
            if (poiResult != null && poiResult.error == PoiResult.ERRORNO.NO_ERROR) {//如果没有错误
                MyOverLay overlay = new MyOverLay(mBaiduMap, poiSearch);
                //设置数据,这里只需要一步，
                overlay.setData(poiResult);

                // 会严重拖累程序速度
//                List<PoiInfo> poiList =  poiResult.getAllPoi();
//                for (int i = 0; i < poiList.size(); i++){
//                    if(poiList.)
//                }

                //添加到地图
                overlay.addToMap();
                //将显示视图拉倒正好可以看到所有POI兴趣点的缩放等级
                overlay.zoomToSpan();//计算工具
//                mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(18));
                //设置标记物的点击监听事件
                mBaiduMap.setOnMarkerClickListener(overlay);
                return;
            } else {
                Toast.makeText(getApplication(), "搜索不到你需要的信息！", Toast.LENGTH_SHORT).show();
            }
        }

        //获得POI的详细检索结果，如果发起的是详细检索，这个方法会得到回调(需要uid)
        @Override
        public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
            if (poiDetailResult.error != SearchResult.ERRORNO.NO_ERROR) {
                Toast.makeText(getApplication(), "抱歉，未找到结果",
                        Toast.LENGTH_SHORT).show();
            } else {// 正常返回结果的时候，此处可以获得很多相关信息
                Toast.makeText(getApplication(), poiDetailResult.getName() + ": "
                                + poiDetailResult.getAddress(),
                        Toast.LENGTH_LONG).show();
                Log.d("666", "onGetPoiDetailResult: "+poiDetailResult.detailUrl);
                Log.d("666", "onGetPoiDetailResult: "+poiDetailResult.imageNum);
            }
        }

        //获得POI室内检索结果
        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

        }
    };
//
//    //公交路线的点的POI检索的监听对象
//    OnGetPoiSearchResultListener busPoiSearchResultListener = new OnGetPoiSearchResultListener() {
//        //获得POI的检索结果，一般检索数据都是在这里获取
//        @Override
//        public void onGetPoiResult(PoiResult poiResult) {
//            mBaiduMap.clear();//清除标志
//            if (poiResult != null && poiResult.error == PoiResult.ERRORNO.NO_ERROR) {//如果没有错误
//                //遍历所有数据
//                for (int i = 0; i < poiResult.getAllPoi().size(); i++) {
//                    //获取里面的数据对象
//                    PoiInfo poiInfo = poiResult.getAllPoi().get(i);
//                    //判断检索到的点的类型是不是公交路线或地铁路线
//                    if (poiInfo.type == PoiInfo.POITYPE.BUS_LINE || poiInfo.type == PoiInfo.POITYPE.SUBWAY_LINE) {
//                        //发起公交检索
//                        BusLineSearchOption busLineOptions = new BusLineSearchOption();
//                        busLineOptions.city("深圳").uid(poiInfo.uid);
//                        busLineSearch.searchBusLine(busLineOptions);
//                    }
//                }
//                return;
//            } else {
//                Toast.makeText(getApplication(), "搜索不到你需要的信息！", Toast.LENGTH_SHORT).show();
//            }
//        }

//        //获得POI的详细检索结果，如果发起的是详细检索，这个方法会得到回调(需要uid)
//        @Override
//        public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
//            if (poiDetailResult.error != SearchResult.ERRORNO.NO_ERROR) {
//                Toast.makeText(getApplication(), "抱歉，未找到结果",
//                        Toast.LENGTH_SHORT).show();
//            } else {// 正常返回结果的时候，此处可以获得很多相关信息
//                Toast.makeText(getApplication(), poiDetailResult.getName() + ": "
//                                + poiDetailResult.getAddress(),
//                        Toast.LENGTH_LONG).show();
//            }
//        }
//
//        //获得POI室内检索结果
//        @Override
//        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {
//
//        }
//    };

    //POI搜索，范围检索,
    public void selectAround(View view) {

        //定义Maker坐标点,当前坐标
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        //获得Key
        String key = et_key.getText().toString();
        //发起周边检索
        PoiBoundSearchOption boundSearchOption = new PoiBoundSearchOption();
        LatLng southwest = new LatLng(latitude - 0.01, longitude - 0.012);// 西南
        LatLng northeast = new LatLng(latitude + 0.01, longitude + 0.012);// 东北
        LatLngBounds bounds = new LatLngBounds.Builder().include(southwest)
                .include(northeast).build();// 得到一个地理范围对象
        boundSearchOption.bound(bounds);// 设置poi检索范围
        boundSearchOption.keyword(key);// 检索关键字
        boundSearchOption.pageNum(1);//搜索一页
        poiSearch.searchInBound(boundSearchOption);// 发起poi范围检索请求
    }

    //POI搜索，周边检索,
    public void selectPoint(View view) {
        //定义Maker坐标点,深圳大学经度和纬度113.943062,22.54069
        //设置的时候经纬度是反的 纬度在前，经度在后
        LatLng point = new LatLng(latitude, longitude);
        //获得Key
        String key = et_key.getText().toString();
        //周边检索
        PoiNearbySearchOption nearbySearchOption = new PoiNearbySearchOption();
        nearbySearchOption.location(point);
        nearbySearchOption.keyword(key);
        nearbySearchOption.radius(10000);// 检索半径，单位是米
        nearbySearchOption.pageNum(1);//搜索一页
        poiSearch.searchNearby(nearbySearchOption);// 发起附近检索请求
    }

    //POI搜索，城市检索u
    public void select(View view) {
        //获得Key
        String city = currentPosition.toString();
        String key = et_key.getText().toString();
        //发起检索
        PoiCitySearchOption poiCity = new PoiCitySearchOption();
        poiCity.keyword(key).city(city);
        poiSearch.searchInCity(poiCity);
        InputMethodManager imm = (InputMethodManager) getSystemService(LocationActivity.this.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
