package com.example.administrator.qtravel.adapter;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.administrator.qtravel.R;
import com.example.administrator.qtravel.databinding.ItemMainBinding;
import com.example.administrator.qtravel.model.MainRecyclerView;
import com.example.administrator.qtravel.global.MyApplication;
import com.example.administrator.qtravel.ui.WebViewActivity;
import com.example.administrator.qtravel.ui.nav.NavHomePageActivity;

import java.util.List;

/**
 * Created by Administrator on 2018/4/26.
 */

public class MainRecyclerViewAdapter extends RecyclerView.Adapter {

    private List<MainRecyclerView> list;

    public static final int TYPE_HEADER = 0;  //说明是带有Header的
    public static final int TYPE_FOOTER = 1;  //说明是带有Footer的
    public static final int TYPE_NORMAL = 2;  //说明是不带有header和footer的
    public static final int TYPE_BUTTON = 3;  //说明是button

    private static View mHeaderView;
    private static View mFooterView;
    private static View mButtonView;

    public MainRecyclerViewAdapter(List<MainRecyclerView> list){
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        if(mHeaderView != null && viewType == TYPE_HEADER) {
            return new ViewHolder(mHeaderView);
        } else if(mFooterView != null && viewType == TYPE_FOOTER){
            return new ViewHolder(mFooterView);
        } else if (mButtonView != null && viewType == TYPE_BUTTON) {
            return new ViewHolder(mButtonView);
        }
        LayoutInflater inflater = LayoutInflater.from(MyApplication.getContext());
        ItemMainBinding binding = DataBindingUtil.inflate(inflater, R.layout.item_main, parent, false);
        final ViewHolder holder = new ViewHolder(binding.getRoot());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyApplication.getContext(), WebViewActivity.class);
                intent.putExtra("Url", list.get(holder.getAdapterPosition()-2).getContent());
                parent.getContext().startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position) == TYPE_HEADER || getItemViewType(position) == TYPE_FOOTER || getItemViewType(position) == TYPE_BUTTON)
            return;
        ItemMainBinding binding = DataBindingUtil.getBinding(holder.itemView);
        binding.setMainRecyclerView(list.get(position-2));
        MainRecyclerView message = list.get(position - 2);
        if (message.getIamge() != null){
            Glide.with(MyApplication.getContext()).load(message.getIamge()).into(binding.imageview);
        }
        binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        if(mHeaderView == null && mFooterView == null){
            return list.size();
        }else if(mHeaderView == null){
            return list.size() + 1;
        }else if (mFooterView == null){
            return list.size() + 1;
        }else {
            return list.size() + 2;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mHeaderView == null && mFooterView == null){
            return TYPE_NORMAL;
        }
        if (position == 0){
            //第一个item应该加载Header
            return TYPE_HEADER;
        }
        if (position == getItemCount()){
            //最后一个,应该加载Footer
            return TYPE_FOOTER;
        }
        if (position == 1){
            //第二个 加载button
            return TYPE_BUTTON;
        }
        return TYPE_NORMAL;
    }

    public void setmHeaderView(View mHeaderView) {
        this.mHeaderView = mHeaderView;
        notifyItemInserted(0);
    }

    public void setmFooterView(View mFooterView) {
        this.mFooterView = mFooterView;
        notifyItemInserted(getItemCount()-1);
    }

    public static View getmButtonView() {
        return mButtonView;
    }

    public void setmButtonView(View mButtonView) {
        this.mButtonView = mButtonView;
        notifyItemInserted(1);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ViewHolder(View itemView){
            super(itemView);
            if (itemView == mHeaderView|| itemView == mFooterView || itemView == mButtonView){
                return;
            }
        }
    }
}
