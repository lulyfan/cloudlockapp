package com.ut.module_lock.adapter;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.ut.module_lock.databinding.ItemLockListBinding;
import com.ut.module_lock.databinding.ItemLockListEmptyBinding;

import java.util.List;

/**
 * author : zhouyubin
 * time   : 2018/11/30
 * desc   :
 * version: 1.0
 * <p>
 * T为数据类型，ET为空数据类型
 */
public class CommonRVAdapter<T, ET> extends RecyclerView.Adapter<CommonRVAdapter.CommonRVHolder> implements View.OnClickListener {

    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_EMPTY = 1;
    private static final int VIEW_TYPE_HEADER = 2;

//    private List<T>

    private int itemVariableId;
    private int emptyVariableId;

    private OnRcvItemClickListener mOnRcvItemClickListener = null;

    @NonNull
    @Override
    public CommonRVHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull CommonRVHolder commonRVHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public void onClick(View v) {

    }


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
