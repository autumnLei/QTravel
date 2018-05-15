package com.example.administrator.qtravel.model;

import android.databinding.ObservableField;

/**
 * Created by Administrator on 2018/5/2.
 */

public class PictureSearchRecyclerView {
    public ObservableField<String> url = new ObservableField<>();
    public ObservableField<String> href = new ObservableField<>();

    public PictureSearchRecyclerView(){

    }

    public PictureSearchRecyclerView(String url, String href){
        this.url.set(url);
        this.href.set(href);
    }

    public String getUrl() {
        return url.get();
    }

    public void setUrl(String url) {
        this.url.set(url);
    }

    public String getHref() {
        return href.get();
    }

    public void setHref(String href) {
        this.href.set(href);
    }
}
