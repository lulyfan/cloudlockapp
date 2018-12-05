package com.ut.module_lock.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * author : chenjiajun
 * time   : 2018/11/28
 * desc   :
 */
public class RecyclerListAdapter<T> extends RecyclerView.Adapter<ListViewHolder> {
    private List<T> itemsList = new ArrayList<>();
    private int itemResourceId;
    private int varId;
    private OnItemClickListener listener;

    public void setOnItemListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public RecyclerListAdapter(List<T> list, int itemResourceId, int varId) {
        if (list != null) {
            itemsList = list;
        }
        this.itemResourceId = itemResourceId;
        this.varId = varId;
    }

    public void addData(List<T> newData) {
        itemsList.addAll(newData);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ViewDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), itemResourceId, viewGroup, false);
        return new ListViewHolder(dataBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder listViewHolder, int i) {
        listViewHolder.binding.setVariable(varId, itemsList.get(i));
        listViewHolder.binding.getRoot().setOnClickListener(v -> {
            if (listener != null) {
                listener.onClicked(v, i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public interface OnItemClickListener {
        void onClicked(View view, int position);
    }
}

