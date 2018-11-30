package com.ut.module_lock.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * author : chenjiajun
 * time   : 2018/11/29
 * desc   :
 */
public class ListViewHolder extends RecyclerView.ViewHolder {
    public ViewDataBinding binding = null;

    public ListViewHolder(@NonNull View itemView) {
        super(itemView);
        binding = DataBindingUtil.getBinding(itemView);
    }
}
