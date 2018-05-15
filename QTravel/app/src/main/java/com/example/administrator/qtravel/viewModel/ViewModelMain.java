package com.example.administrator.qtravel.viewModel;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.administrator.qtravel.databinding.ActivityMainBinding;
import com.example.administrator.qtravel.model.Main;

/**
 * Created by Administrator on 2018/4/21.
 */

public class ViewModelMain {
    public Main main;
    public Activity activity;
    public ActivityMainBinding activityMainBinding;

    public ViewModelMain(Activity activity, ActivityMainBinding activityMainBinding){
        this.activity = activity;
        this.activityMainBinding = activityMainBinding;
        init();
    }

    public void init(){
        main = new Main("dkj");
        activityMainBinding.setModel(this);
    }

    public void onClick(View view){
        Log.d("22222", "onClick: ");
    }

}
