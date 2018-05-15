package com.example.administrator.qtravel.adapter;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.administrator.qtravel.R;
import com.example.administrator.qtravel.databinding.ItemVenueSearchBinding;
import com.example.administrator.qtravel.global.MyApplication;
import com.example.administrator.qtravel.model.VenueSearchRecyclerView;
import com.example.administrator.qtravel.ui.WebViewActivity;
import com.example.administrator.qtravel.ui.search.VenueSearchFragment;

import java.util.List;

/**
 * Created by Administrator on 2018/5/2.
 */

public class VenueSearchRecyclerViewAdapter extends RecyclerView.Adapter {

    private List<VenueSearchRecyclerView> list;

    public VenueSearchRecyclerViewAdapter(List<VenueSearchRecyclerView> list){
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(MyApplication.getContext());
        ItemVenueSearchBinding binding = DataBindingUtil.inflate(inflater, R.layout.item_venue_search, parent, false);
        final ViewHolder holder = new ViewHolder(binding.getRoot());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyApplication.getContext(), WebViewActivity.class);
                intent.putExtra("Url", list.get(holder.getAdapterPosition()).getUrl());
                intent.putExtra("Title", list.get(holder.getAdapterPosition()).getTitle());
                parent.getContext().startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemVenueSearchBinding binding = DataBindingUtil.getBinding(holder.itemView);
        binding.setVenueSearchRecyclerView(list.get(position));
        VenueSearchRecyclerView item = list.get(position);
        if (item.getImage() != null){
            Glide.with(MyApplication.getContext()).load(item.getImage()).into(binding.imageViewSports);
        }
        binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ViewHolder(View itemView){
            super(itemView);
        }
    }
}
