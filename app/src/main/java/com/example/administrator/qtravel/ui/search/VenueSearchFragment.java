package com.example.administrator.qtravel.ui.search;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.example.administrator.qtravel.R;
import com.example.administrator.qtravel.adapter.VenueSearchRecyclerViewAdapter;
import com.example.administrator.qtravel.databinding.FragmentVenueSearchBinding;
import com.example.administrator.qtravel.model.VenueSearchRecyclerView;
import com.example.administrator.qtravel.overlayutil.MyTempOverLay;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

public class VenueSearchFragment extends Fragment {

    //type从adapter接收 local接收从mainactivity传来的local参数
    private String type, local;
    private PoiSearch poiSearch;
    private VenueSearchRecyclerViewAdapter venueSearchRecyclerViewAdapter;
    private List<VenueSearchRecyclerView> list = new ArrayList<>();
    List<PoiInfo> poiList = new ArrayList<>(); //存储poi搜索场馆的信息实体；

    /**
     * 自定义方法 用来往fragment传入type参数
     * @param type
     * @return
     */
    public static VenueSearchFragment newInstance(String type){
        VenueSearchFragment venueSearchFragment = new VenueSearchFragment();
        Bundle bundle = new Bundle();
        bundle.putString("TYPE",type);
        venueSearchFragment.setArguments(bundle);
        return venueSearchFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //获取activity传来的type参数
        Bundle bundle = getArguments();
        type = bundle.getString("TYPE");
        Log.d("kk", "onCreateView: "+type);
        //发起poi检索 fragment预加载是从onCreateView到onDestroyView
        if(poiList.size()==0)
            select();
        //View view = inflater.inflate(R.layout.fragment_venue_search, container, false);
        FragmentVenueSearchBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_venue_search, container, false);
        RecyclerView recyclerView = binding.recyclerViewVenue;
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        venueSearchRecyclerViewAdapter = new VenueSearchRecyclerViewAdapter(list);
        recyclerView.setAdapter(venueSearchRecyclerViewAdapter);
        return binding.getRoot();
    }

    @Override
    public void onAttach(Context context) {
        local = ((VenueSearchActivity)getActivity()).local;
        Log.d("kk", "onAttach: "+local+"   "+type);
        super.onAttach(context);
    }

    /**
     * 下面全是和获取周边信息的函数
     */
    //POI检索的监听对象
    OnGetPoiSearchResultListener poiSearchResultListener = new OnGetPoiSearchResultListener() {
        //获得POI的检索结果，一般检索数据都是在这里获取
        @Override
        public void onGetPoiResult(PoiResult poiResult) {
            if (poiResult != null && poiResult.error == PoiResult.ERRORNO.NO_ERROR) {//如果没有错误
                // 会拖累程序速度
                poiList = poiResult.getAllPoi();
                for (int i = 0; i < poiList.size(); i++) {
                    VenueSearchRecyclerView venueSearchRecyclerView = new VenueSearchRecyclerView();
                    venueSearchRecyclerView.setTitle(poiList.get(i).name);
                    venueSearchRecyclerView.setLocal(poiList.get(i).address);
                    venueSearchRecyclerView.setTel(poiList.get(i).phoneNum);
                    venueSearchRecyclerView.setUid(poiList.get(i).uid);
                    list.add(venueSearchRecyclerView);
                }
                MyTempOverLay temp = new MyTempOverLay(poiSearch, poiResult);
                for (int i = 0; i < poiList.size(); i++) {
                    temp.getDetailPoi(i);
                }
            } else {
                Toast.makeText(getActivity().getApplication(), "搜索不到你需要的信息！", Toast.LENGTH_SHORT).show();
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    venueSearchRecyclerViewAdapter.notifyDataSetChanged();
                }
            });
        }

        //获得POI的详细检索结果，如果发起的是详细检索，这个方法会得到回调(需要uid)
        @Override
        public void onGetPoiDetailResult(final PoiDetailResult poiDetailResult) {
            if (poiDetailResult.error != SearchResult.ERRORNO.NO_ERROR) {
                Log.d("jjj", "onGetPoiDetailResult:  error");
            } else {// 正常返回结果的时候，此处可以获得很多相关信息
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            for (int i = 0; i < list.size(); i++) {
                                VenueSearchRecyclerView venueSearchRecyclerView = list.get(i);
                                if (venueSearchRecyclerView.getUid().equals(poiDetailResult.getUid())) {
                                    Document doc = Jsoup.connect(poiDetailResult.detailUrl).get();
                                    Element el = doc.select(".meta-img").first();
                                    Element el_img = el.select("img").first();
                                    venueSearchRecyclerView.setImage(el_img.attr("src"));
                                    venueSearchRecyclerView.setUrl(poiDetailResult.detailUrl);
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        }

        //获得POI室内检索结果
        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

        }
    };

    //POI搜索，城市检索
    public void select() {
        //获得Key
        String city = local;
        if(local==null){
            Toast.makeText(getActivity().getApplication(), "定位失败", Toast.LENGTH_SHORT).show();
            onDestroy();
        }else {
            String key = type + "馆";
            //实例化 poiSearch
            poiSearch = PoiSearch.newInstance();
            //设置Poi监听对象
            poiSearch.setOnGetPoiSearchResultListener(poiSearchResultListener);
            //发起检索
            PoiCitySearchOption poiCity = new PoiCitySearchOption();
            poiCity.keyword(key).city(city);
            poiSearch.searchInCity(poiCity);
        }

    }

}
