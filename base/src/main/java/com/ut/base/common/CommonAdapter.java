package com.ut.base.common;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZYB on 2016/7/5.
 */
public abstract class CommonAdapter<T> extends BaseAdapter {
    private List<T> mDatas = null;
    private Context mContext = null;
    private int mLayoutId = 0;


    public CommonAdapter(Context context, List<T> datas, int layoutId) {
        this.mContext = context;
        this.mDatas = datas;
        this.mLayoutId = layoutId;
    }

    public void notifyData(List<T> list) {
//        this.mDatas.clear();
//        this.mDatas.addAll(list);
        this.mDatas = new ArrayList<>(list);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent) {
        CommonViewHolder commonViewHolder = CommonViewHolder.get(mContext, converView, mLayoutId);
        convert(commonViewHolder, position, mDatas.get(position));
        return commonViewHolder.getConvertView();
    }

    public abstract void convert(CommonViewHolder commonViewHolder, int position, T item);
}
