package com.ut.base.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.ut.base.R;
import com.ut.base.customView.DatePicker;
import com.ut.base.customView.DateTimePicker;
import com.ut.base.customView.TimePicker;

import javax.security.auth.callback.Callback;

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


}
