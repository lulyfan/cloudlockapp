package com.ut.module_lock.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ut.module_lock.databinding.ItemLockListBinding;
import com.ut.module_lock.databinding.ItemLockListEmptyBinding;

/**
 * author : zhouyubin
 * time   : 2018/11/30
 * desc   :
 * version: 1.0
 */
public class CommonRVAdapter<T, ET> {

    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_EMPTY = 1;
    private static final int VIEW_TYPE_HEADER = 2;

    private int itemVariableId;
    private int emptyVariableId;


    public static class CommonRVHolder<T, ET> extends RecyclerView.ViewHolder {
        private ItemLockListBinding mBinding;
        private ItemLockListEmptyBinding mEmptyBinding;
        public int viewType = 0;

        public CommonRVHolder(View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;
            if (viewType == VIEW_TYPE_EMPTY) {
                mEmptyBinding = DataBindingUtil.bind(itemView);
            } else {
                mBinding = DataBindingUtil.bind(itemView);
            }
        }

        public void bind(T item, int itemVariableId) {
            if (mBinding != null && item != null) {
                mBinding.setVariable(itemVariableId, item);
            }
        }

        public void bindEmpty(ET emptyData, int emptyVariableId) {
            if (mEmptyBinding != null && emptyData != null) {
                mEmptyBinding.setVariable(emptyVariableId, emptyData);
            }
        }
    }
}
