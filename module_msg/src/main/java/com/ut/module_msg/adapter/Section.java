package com.ut.module_msg.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ut.module_msg.R;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

/**
 * author : chenjiajun
 * time   : 2018/11/28
 * desc   :
 */
public class Section<T> extends StatelessSection {

    private List<T> itemsList = new ArrayList<>();
    private String headerTitle;
    private int itemResourceId;

    public Section(String headerTitle, List<T> list, int itemResourceId) {
        super(SectionParameters.builder().headerResourceId(R.layout.item_section_header).itemResourceId(R.layout.item_section_child).build());
        if (list != null) {
            itemsList = list;
        }
        this.headerTitle = headerTitle;
        this.itemResourceId = itemResourceId;
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
        headerViewHolder.headerTv.setText(headerTitle);
    }

    @Override
    public int getContentItemsTotal() {
        return itemsList.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView headerTv = null;
        public HeaderViewHolder(@NonNull View headerView) {
            super(headerView);
            headerTv = headerView.findViewById(R.id.tv_header);
        }
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder {
        ViewGroup container = null;
        ViewDataBinding dataBinding = null;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.container);
            dataBinding = DataBindingUtil.inflate(LayoutInflater.from(itemView.getContext()), itemResourceId, container, true);
        }
    }
}
