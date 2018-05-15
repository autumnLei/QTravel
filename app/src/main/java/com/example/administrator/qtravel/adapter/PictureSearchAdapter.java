package com.example.administrator.qtravel.adapter;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.administrator.qtravel.R;
import com.example.administrator.qtravel.databinding.ItemPictureSearchBinding;
import com.example.administrator.qtravel.global.MyApplication;
import com.example.administrator.qtravel.model.PictureSearchRecyclerView;
import com.example.administrator.qtravel.ui.WebViewActivity;
import com.example.administrator.qtravel.ui.nav.NavHomePageActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/3.
 */

public class PictureSearchAdapter extends RecyclerView.Adapter<PictureSearchAdapter.ViewHolder> {

    private List<PictureSearchRecyclerView> mList = new ArrayList<>();
    private String searchText;

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            imageView = (ImageView)view.findViewById(R.id.image_view);
        }
    }

    public PictureSearchAdapter(List<PictureSearchRecyclerView> coachList, String searchText) {
        mList = coachList;
        this.searchText = searchText;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(MyApplication.getContext());
        ItemPictureSearchBinding binding = DataBindingUtil.inflate(inflater, R.layout.item_picture_search, parent, false);

        final ViewHolder holder = new ViewHolder(binding.getRoot());
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyApplication.getContext(), WebViewActivity.class);
                intent.putExtra("Url", mList.get(holder.getAdapterPosition()).getUrl());
                intent.putExtra("Title", searchText);
                parent.getContext().startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Glide.with(MyApplication.getContext()).load(mList.get(position).getHref()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
