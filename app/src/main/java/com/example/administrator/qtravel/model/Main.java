package com.example.administrator.qtravel.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.ObservableField;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;

import com.example.administrator.qtravel.BR;


/**
 * Created by Administrator on 2018/4/21.
 */

public class Main extends BaseObservable{

    public ObservableField<String> name = new ObservableField<>();

    public Main(String string){
        this.name.set(string);
    }

    @Bindable
    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
        notifyPropertyChanged(BR.name);
    }
}
