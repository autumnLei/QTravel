package com.example.administrator.qtravel.overlayutil;

import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;

/**
 * Created by Administrator on 2017/6/8.
 */

public class MyTempOverLay {

    PoiSearch mPoiSearch;
    PoiResult mPoiResult;
    PoiInfo poiInfo;

    public MyTempOverLay(PoiSearch mPoiSearch, PoiResult mPoiResult){
        this.mPoiSearch = mPoiSearch;
        this.mPoiResult = mPoiResult;
    }

    public void getDetailPoi(int i){
        poiInfo = mPoiResult.getAllPoi().get(i);
        //  发起一个详细检索,要使用uid
        mPoiSearch.searchPoiDetail(new PoiDetailSearchOption().poiUid(poiInfo.uid));
    }
}
