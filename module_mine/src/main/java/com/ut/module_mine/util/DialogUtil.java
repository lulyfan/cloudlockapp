package com.ut.module_mine.util;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.ut.module_mine.R;
import com.ut.module_mine.customView.DatePicker;
import com.ut.module_mine.customView.DateTimePicker;
import com.ut.module_mine.customView.TimePicker;

public class DialogUtil {

    public static void chooseDateTime(Context context, String title, DateTimePicker.DateTimeSelectListener dateTimeSelectListener) {
        View view = LayoutInflater.from(context).inflate(R.layout.choose_datetime, null);
        TextView tv_title = view.findViewById(R.id.title);
        tv_title.setText(title);
        DateTimePicker dateTimePicker = view.findViewById(R.id.dateTimePicker);
        View confirm = view.findViewById(R.id.confirm);
        View close = view.findViewById(R.id.close);

        DialogPlus dialog = DialogPlus.newDialog(context)
                .setContentHolder(new ViewHolder(view))
                .create();

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dateTimeSelectListener != null) {
                    dateTimeSelectListener.onDateTimeSelected(
                            dateTimePicker.getSelectedYear(),
                            dateTimePicker.getRealMonth(),
                            dateTimePicker.getSelectedDay(),
                            dateTimePicker.getSelectedHour(),
                            dateTimePicker.getSelectedMinute());
                }

                dialog.dismiss();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public static void chooseDate(Context context, String title, DatePicker.DateSelectListener dateSelectListener) {
        View view = LayoutInflater.from(context).inflate(R.layout.choose_date, null);
        TextView tv_title = view.findViewById(R.id.title);
        tv_title.setText(title);
        DatePicker datePicker = view.findViewById(R.id.datePicker);
        View confirm = view.findViewById(R.id.confirm);
        View close = view.findViewById(R.id.close);

        DialogPlus dialog = DialogPlus.newDialog(context)
                .setContentHolder(new ViewHolder(view))
                .create();

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dateSelectListener != null) {
                    dateSelectListener.onDateSelected(
                            datePicker.getSelectedYear(),
                            datePicker.getRealMonth(),
                            datePicker.getSelectedDay());
                }

                dialog.dismiss();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public static void chooseTime(Context context, String title, TimePicker.TimeSelectListener timeSelectListener) {
        View view = LayoutInflater.from(context).inflate(R.layout.choose_time, null);
        TextView tv_title = view.findViewById(R.id.title);
        tv_title.setText(title);
        TimePicker timePicker = view.findViewById(R.id.timePicker);
        View confirm = view.findViewById(R.id.confirm);
        View close = view.findViewById(R.id.close);

        DialogPlus dialog = DialogPlus.newDialog(context)
                .setContentHolder(new ViewHolder(view))
                .create();

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timeSelectListener != null) {
                    timeSelectListener.onTimeSelected(
                            timePicker.getSelectedHour(),
                            timePicker.getSelectedMinute());
                }

                dialog.dismiss();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public static void addLockGroup(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_addgroup, null);

        DialogPlus dialog = DialogPlus.newDialog(context)
                .setContentHolder(new ViewHolder(view))
                .setGravity(Gravity.CENTER)
                .setContentWidth(Util.getWidthPxByDisplayPercent(context, 0.8))
                .setContentBackgroundResource(R.drawable.arc_border)
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        int i = view.getId();
                        if (i == R.id.cancel) {
                            dialog.dismiss();

                        } else if (i == R.id.confirm) {
                            dialog.dismiss();

                        } else {
                        }
                    }
                })
                .create();

        dialog.show();
    }
}
