package com.ut.module_msg.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * author : chenjiajun
 * time   : 2018/11/26
 * desc   : 结合DataBinding的通用List Adapter
 */
public class ListAdapter<T> extends BaseAdapter {

    private Context context;
    private List<T> list;
    private int layoutId;
    private int variableId;

    public ListAdapter(Context context, int layoutId, List<T> list, int variableId) {
        this.context = context;
        if(list == null) {
            this.list = new ArrayList<>();
        } else {
            this.list = list;
        }
        this.layoutId = layoutId;
        this.variableId = variableId;
    }


    public void updateData(List<T> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewDataBinding binding = null;
        if (convertView == null) {
            binding = DataBindingUtil.inflate(LayoutInflater.from(context), layoutId, parent, false);
        } else {
            binding = DataBindingUtil.getBinding(convertView);
        }

        assert binding != null;
        binding.setVariable(variableId, list.get(position));
        addBadge(binding,position);
        return binding.getRoot();
    }

    public void addBadge(ViewDataBinding binding, int position){
    }
}


