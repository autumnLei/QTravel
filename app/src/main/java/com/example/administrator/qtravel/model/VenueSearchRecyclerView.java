package com.example.administrator.qtravel.model;

import android.databinding.BaseObservable;
import android.databinding.ObservableField;

import com.example.administrator.qtravel.ui.search.VenueSearchFragment;

/**
 * Created by Administrator on 2017/6/7.
 */

public class VenueSearchRecyclerView extends BaseObservable{

    public ObservableField<String> image = new ObservableField<>();
    public ObservableField<String> title = new ObservableField<>();
    public ObservableField<String> local = new ObservableField<>();
    public ObservableField<String> Tel = new ObservableField<>();
    public ObservableField<String> Url = new ObservableField<>();
    public ObservableField<String> uid = new ObservableField<>();

    public VenueSearchRecyclerView(){

    }

    public VenueSearchRecyclerView(String image, String title, String local, String Tel, String Url, String uid){
        this.image.set(image);
        this.title.set(title);
        this.local.set(local);
        this.Tel.set(Tel);
        this.Url.set(Url);
        this.uid.set(uid);
    }

    public String getImage() {
        return image.get();
    }

    public void setImage(String image) {
        this.image.set(image);
    }

    public String getTitle() {
        return title.get();
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public String getLocal() {
        return local.get();
    }

    public void setLocal(String local) {
        this.local.set(local);
    }

    public String getTel() {
        return Tel.get();
    }

    public void setTel(String tel) {
        Tel.set(tel);
    }

    public String getUrl() {
        return Url.get();
    }

    public void setUrl(String url) {
        Url.set(url);
    }

    public String getUid() {
        return uid.get();
    }

    public void setUid(String uid) {
        this.uid.set(uid);
    }
}
