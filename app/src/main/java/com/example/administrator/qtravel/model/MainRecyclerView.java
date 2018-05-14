package com.example.administrator.qtravel.model;

import android.databinding.BaseObservable;
import android.databinding.ObservableField;

import com.example.administrator.qtravel.BR;

/**
 * Created by Administrator on 2018/4/26.
 */

public class MainRecyclerView extends BaseObservable {

    public ObservableField<String> image = new ObservableField<>();
    public ObservableField<String> title = new ObservableField<>();
    public ObservableField<String> text = new ObservableField<>();
    public ObservableField<String> content = new ObservableField<>();

    public MainRecyclerView(){

    }

    public MainRecyclerView(String image, String title, String text, String content){
        this.image.set(image);
        this.title.set(title);
        this.text.set(text);
        this.content.set(content);
    }

    public String getIamge() {
        return image.get();
    }

    public void setIamge(String iamge) {
        this.image.set(iamge);
        notifyPropertyChanged(BR._all);
    }

    public String getTitle() {
        return title.get();
    }

    public void setTitle(String title) {
        this.title.set(title);
        notifyPropertyChanged(BR._all);
    }

    public String getText() {
        return text.get();
    }

    public void setText(String text) {
        this.text.set(text);
        notifyPropertyChanged(BR._all);
    }

    public String getContent() {
        return content.get();
    }

    public void setContent(String content) {
        this.content.set(content);
        notifyPropertyChanged(BR._all);
    }
}
