package com.ut.base.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * author : chenjiajun
 * time   : 2018/11/26
 * desc   : 结合DataBinding的通用List Adapter
 */
public class ListAdapter<T> extends BaseAdapter {

    private Context context;
    private List<T> dataSource;
    private int layoutId;
    private int variableId;

    public ListAdapter(Context context, int layoutId, List<T> list, int variableId) {
        this.context = context;
        if (list == null) {
            dataSource = new ArrayList<>();
        } else {
            dataSource = list;
        }
        this.layoutId = layoutId;
        this.variableId = variableId;
    }

    public void updateDate(List<T> list) {
        if (list == null) return;
        dataSource.clear();
        dataSource.addAll(list);
        notifyDataSetChanged();
    }

    public void loadDate(List<T> list) {
        if (list == null) return;
        dataSource.addAll(list);
        HashSet<T> set = new HashSet<>(dataSource);
        dataSource.clear();
        dataSource.addAll(set);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return dataSource.size();
    }

    @Override
    public Object getItem(int position) {
        return dataSource.get(position);
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
        handleItem(binding, position);
        binding.setVariable(variableId, dataSource.get(position));
        return binding.getRoot();
    }

    public void handleItem(ViewDataBinding binding, int position) {
    }
}


