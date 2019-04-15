package com.example.administrator.qtravel.ui.search;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;


import com.example.administrator.qtravel.R;
import com.example.administrator.qtravel.adapter.PictureSearchAdapter;
import com.example.administrator.qtravel.databinding.ActivityPictureSearchBinding;
import com.example.administrator.qtravel.model.PictureSearchRecyclerView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;


public class PictureSearchActivity extends AppCompatActivity {


    private List<PictureSearchRecyclerView> mList = new ArrayList<>();
    String searchText;
    SearchView searchView;
    PictureSearchAdapter adapter;
    ActivityPictureSearchBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_picture_search);
        setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.back);
        }
        showProgressBar();
        initCoach();
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        binding.recycler.setLayoutManager(layoutManager);
        adapter = new PictureSearchAdapter(mList, searchText);
        binding.recycler.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_picture_search, menu);
        final MenuItem item = menu.findItem(R.id.search_contact);
        searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchText = newText;
                init();
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
            default:
                break;
        }
        return true;
    }

    private void initCoach() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Document doc_add = Jsoup.connect("http://www.ivsky.com/search.php?q=" + "体育明星").get();
                    Element els_temp2 = doc_add.select("ul.pli").first();
                    Elements els_add = els_temp2.select("li");
                    for (int i = 0; i < els_add.size(); i++) {
                        Log.d("00", "run: "+"ddd");
                        Element el_add = els_add.get(i).select("a").first();
                        Document doc_detail = Jsoup.connect("http://www.ivsky.com/" + el_add.attr("href")).get();
                        Element el_detail = doc_detail.select("div.left").first();
                        PictureSearchRecyclerView pic = new PictureSearchRecyclerView();
                        pic.setHref(els_add.get(i).select("img").first().attr("src"));
                        pic.setUrl("http:"+el_detail.select("img").first().attr("src"));
                        mList.add(pic);
                    }
                } catch (Exception e) {
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                        closeProgressBar();
                    }
                });
            }
        }.start();

    }

    public void init() {
        mList.clear();
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Document doc_add = Jsoup.connect("http://www.ivsky.com/search.php?q=" + searchText).get();
                    Element els_temp2 = doc_add.select("ul.pli").first();
                    Elements els_add = els_temp2.select("li");
                    for (int i = 0; i < els_add.size(); i++) {
                        Element el_add = els_add.get(i).select("a").first();
                        Document doc_detail = Jsoup.connect("http://www.ivsky.com/" + el_add.attr("href")).get();
                        Element el_detail = doc_detail.select("div.left").first();
                        PictureSearchRecyclerView pic = new PictureSearchRecyclerView();
                        pic.setHref(els_add.get(i).select("img").first().attr("src"));
                        pic.setUrl("http:"+el_detail.select("img").first().attr("src"));
                        mList.add(pic);
                    }
                } catch (Exception e) {
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }.start();

    }

    /**
     * 显示进度条
     */
    private void showProgressBar(){
        if (binding.progressBar.getVisibility() == View.GONE){
            binding.progressBar.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    /**
     * 关闭进度条
     */
    private void closeProgressBar(){
        if(binding.progressBar.getVisibility() == View.VISIBLE){
            binding.progressBar.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

}
