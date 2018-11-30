package com.ut.module_msg.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

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
    private int itemVariableId;
    private int headerVariableId;

    public Section(String headerTitle, List<T> list, int headerResourceId, int itemResourceId, int headerVariableId , int itemVariableId) {
        super(SectionParameters.builder().headerResourceId(headerResourceId).itemResourceId(itemResourceId).build());
        if (list != null) {
            itemsList = list;
        }
        this.headerTitle = headerTitle;
        this.headerVariableId = headerVariableId;
        this.itemVariableId = itemVariableId;
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
      HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
      headerViewHolder.dataBinding.setVariable(headerVariableId, headerTitle);
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
        ((ItemViewHolder) holder).dataBinding.setVariable(itemVariableId, itemsList.get(position));
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        ViewDataBinding dataBinding = null;
        public HeaderViewHolder(@NonNull View headerView) {
            super(headerView);
            dataBinding = DataBindingUtil.getBinding(headerView);
        }
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder {
        ViewDataBinding dataBinding = null;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            dataBinding = DataBindingUtil.getBinding(itemView);
        }
    }
}
