package com.ut.module_lock.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ut.base.UIUtils.SystemUtils;
import com.ut.module_lock.R;
import com.ut.module_lock.entity.OperationRecord;
import com.ut.database.entity.Record;

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
        List<Record> records = operationRecord.getRecords();
        LinearLayout.LayoutParams lp = null;
        for (Record r : records) {
            lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, SystemUtils.dp2px(context, 70));
            ViewGroup item = (ViewGroup) View.inflate(context, R.layout.item_record_body, null);
            TextView operatorTv = item.findViewById(R.id.operator);
            //TODO 适配门锁钥匙
            String describe = r.getDescription();
            if (describe.contains(",")) {
                String[] strings = describe.split(",");
                if (strings.length >= 2) {
                    if (TextUtils.isEmpty(r.getKeyName()))
                        r.setKeyName(strings[0]);
                    r.setDescription(strings[1]);
                }
            }
            operatorTv.setText(r.getKeyName());
            TextView descTv = item.findViewById(R.id.desc);
            descTv.setText(new StringBuffer(r.getTime() + "   " + r.getDescription()));
            ImageView icon = item.findViewById(R.id.icon);
            Glide.with(context).load(getRecordIcon(r.getOpenLockType())).apply(RequestOptions.circleCropTransform()).into(icon);
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

    private Drawable getRecordIcon(int openType) {
//                BLEMANUAL(0,"蓝牙手动开锁"),
//                BLEAUTO(1,"蓝牙无感开锁"),
//                FINGERPRINT( 2,"指纹"),
//                PASSWORD(3,"密码"),
//                ELECTRONICKEY(4,"电子钥匙"),
//                BLUETOOTH(5,"手机蓝牙"),
//                ICCARD(6,"卡片");

        int rid = 0;
        switch (openType) {
            case 0:
                rid = R.mipmap.open_bluetooth;
                break;
            case 1:
                rid = R.mipmap.open_touch;
                break;
            case 2:
                rid = R.mipmap.open_fingerprint;
                break;
            case 3:
                rid = R.mipmap.open_password;
                break;
            case 4:
                rid = R.mipmap.open_key;
                break;
            case 5:
//                rid = R.mipmap.open_bluetooth;
                break;
            case 6:
                rid = R.mipmap.open_door_card;
                break;
        }
        if (rid > 0) {
            return context.getResources().getDrawable(rid);
        }
        return null;
    }
}
