package com.ut.base.common;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;

/**
 * Created by ZYB on 2016/7/5.
 */
public class CommonViewHolder {

    private SparseArray<View> mViews = null;

    private View mConvertView = null;

    public CommonViewHolder(Context context, View contentView, int layoutId) {
        mViews = new SparseArray<View>();
        mConvertView = View.inflate(context, layoutId, null);
        mConvertView.setTag(this);
    }

    public static CommonViewHolder get(Context context, View convertView, int layoutId) {
        if (convertView == null) {
            CommonViewHolder baseViewHolder = new CommonViewHolder(context, convertView, layoutId);
            return baseViewHolder;
        }
        return (CommonViewHolder) convertView.getTag();
    }

    public View getConvertView() {
        return mConvertView;
    }

    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }
}
