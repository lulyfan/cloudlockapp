package com.ut.module_lock.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.aigestudio.wheelpicker.WheelPicker;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.ut.module_lock.R;

import java.util.List;

/**
 * author : zhouyubin
 * time   : 2019/01/14
 * desc   :
 * version: 1.0
 */
public class PickerDialogUtils {
    public static void chooseSingle(Context context, String title, List<String> data, int defaultIndex, SinglePickListener listener) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_permission_picker, null);
        TextView tv_title = view.findViewById(R.id.title);
        tv_title.setText(title);
        View confirm = view.findViewById(R.id.confirm);
        View close = view.findViewById(R.id.close);
        WheelPicker wheelPicker = view.findViewById(R.id.wheel_content);
        wheelPicker.setData(data);
        if (defaultIndex >= 0 && defaultIndex < data.size()) {
            wheelPicker.setSelectedItemPosition(defaultIndex);
        }

        DialogPlus dialog = DialogPlus.newDialog(context)
                .setContentHolder(new ViewHolder(view))
                .create();
        final int[] selectIndex = {defaultIndex};
        final String[] selectData = {data.get(defaultIndex)};
        wheelPicker.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
            @Override
            public void onItemSelected(WheelPicker picker, Object data, int position) {
                selectIndex[0] = position;
                selectData[0] = (String) data;
            }
        });
        confirm.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSelected(selectIndex[0], selectData[0]);
            }
            dialog.dismiss();
        });

        close.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    public interface SinglePickListener {
        void onSelected(int selectIndex, String data);
    }
}
