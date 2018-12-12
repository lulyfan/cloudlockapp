package com.ut.module_lock.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ut.base.UIUtils.SystemUtils;
import com.ut.module_lock.R;
import com.ut.module_lock.entity.OperationRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * author : chenjiajun
 * time   : 2018/12/3
 * desc   :
 */
public class ORListAdapter extends BaseAdapter {

    private Context context;
    private List<OperationRecord> operationRecords = new ArrayList<>();

    public ORListAdapter(Context context, List<OperationRecord> operationRecords) {
        this.context = context;
        this.operationRecords = operationRecords;
    }

    @Override
    public int getCount() {
        return operationRecords.size();
    }

    @Override
    public Object getItem(int position) {
        return operationRecords.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.item_operation_record, null);
            holder.header = convertView.findViewById(R.id.time);
            holder.container = convertView.findViewById(R.id.container);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        OperationRecord operationRecord = operationRecords.get(position);
        holder.header.setText(operationRecord.getTime());
        holder.container.removeAllViews();
        List<OperationRecord.Record> records = operationRecord.getRecords();
        LinearLayout.LayoutParams lp = null;
        for (OperationRecord.Record r : records) {
            lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, SystemUtils.dp2px(context, 70));
            ViewGroup item = (ViewGroup) View.inflate(context, R.layout.item_record_body, null);
            TextView operatorTv = item.findViewById(R.id.operator);
            operatorTv.setText(r.getOperator());
            TextView descTv = item.findViewById(R.id.desc);
            descTv.setText(r.getDesc());
            ImageView icon = item.findViewById(R.id.icon);
            holder.container.addView(item, lp);
            if (records.indexOf(r) == records.size() - 1) {
                continue;
            }
            View view = new View(context);
            lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
            lp.leftMargin = SystemUtils.dp2px(context, 20);
            lp.rightMargin = SystemUtils.dp2px(context, 20);
            view.setBackgroundColor(Color.parseColor("#DFDFDF"));
            holder.container.addView(view, lp);
        }

        return convertView;
    }

    private class ViewHolder {
        TextView header;
        ViewGroup container;
    }
}
