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

import java.util.Calendar;

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

    /**
     * 在发送限时钥匙页面的选择时间
     *
     * @param context
     * @param title
     * @param dateTimeSelectListener
     */
    public static void chooseDateTimeInSendLimitKey(Context context, String title, DateTimePicker.DateTimeSelectListener dateTimeSelectListener,
                                                    int year, int month, int day, int hour, int minute) {
        View view = LayoutInflater.from(context).inflate(R.layout.choose_datetime, null);
        TextView tv_title = view.findViewById(R.id.title);
        tv_title.setText(title);
        DateTimePicker dateTimePicker = view.findViewById(R.id.dateTimePicker);
        dateTimePicker.setTimeCyclic(false, false);

        Calendar now = Calendar.getInstance();
        int currentYear = now.get(Calendar.YEAR);
        dateTimePicker.init(currentYear, now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH),
                now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE));

        if (context.getString(R.string.validTime).equals(title) ||
                context.getString(R.string.lock_key_vaild_time).equals(title)) {
            dateTimePicker.setYearEnd(currentYear + 1);
        } else {
            dateTimePicker.setYearEnd(currentYear + 60);
        }

        dateTimePicker.setDateTime(year, month, day, hour, minute);

        View confirm = view.findViewById(R.id.confirm);
        View close = view.findViewById(R.id.close);

        DialogPlus dialog = DialogPlus.newDialog(context)
                .setContentHolder(new ViewHolder(view))
                .create();

        confirm.setOnClickListener(v -> {
            if (dateTimeSelectListener != null) {
                dateTimeSelectListener.onDateTimeSelected(
                        dateTimePicker.getSelectedYear(),
                        dateTimePicker.getSelectedMonth(),
                        dateTimePicker.getSelectedDay(),
                        dateTimePicker.getSelectedHour(),
                        dateTimePicker.getSelectedMinute());
            }

            dialog.dismiss();
        });

        close.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    public static void chooseDateTime(Context context, String title, DateTimePicker.DateTimeSelectListener dateTimeSelectListener,
                                      boolean isReset, int year, int month, int day, int hour, int minute) {
        View view = LayoutInflater.from(context).inflate(R.layout.choose_datetime, null);
        TextView tv_title = view.findViewById(R.id.title);
        tv_title.setText(title);
        DateTimePicker dateTimePicker = view.findViewById(R.id.dateTimePicker);
        dateTimePicker.setTimeCyclic(false, false);
        if (isReset) {
            dateTimePicker.init(year, month, day, hour, minute);
        }
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


    public static void chooseDate(Context context, String title, DatePicker.DateSelectListener dateSelectListener, boolean isReset, int year, int month, int day) {
        View view = LayoutInflater.from(context).inflate(R.layout.choose_date, null);
        TextView tv_title = view.findViewById(R.id.title);
        tv_title.setText(title);

        DatePicker datePicker = view.findViewById(R.id.datePicker);
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        if (context.getString(R.string.startDate).equals(title)) {
            datePicker.initYear(currentYear, currentYear + 1);
        } else if (context.getString(R.string.endDate).equals(title)) {
            datePicker.initYear(currentYear, currentYear + 60);
        }

        if (isReset) {
            datePicker.init(year, month, day);
        }

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

    public static void chooseTime(Context context, String title, TimePicker.TimeSelectListener timeSelectListener, boolean isSetNo00_00) {
        View view = LayoutInflater.from(context).inflate(R.layout.choose_time, null);
        TextView tv_title = view.findViewById(R.id.title);
        tv_title.setText(title);
        TimePicker timePicker = view.findViewById(R.id.timePicker);
        timePicker.setCyclic(false, false);
        if (isSetNo00_00) {
            timePicker.setNo00_00();
        }
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

    public static void chooseTime(Context context, String title, TimePicker.TimeSelectListener timeSelectListener, boolean isReset, int hour, int minute) {
        View view = LayoutInflater.from(context).inflate(R.layout.choose_time, null);
        TextView tv_title = view.findViewById(R.id.title);
        tv_title.setText(title);
        TimePicker timePicker = view.findViewById(R.id.timePicker);

        if (isReset) {
            timePicker.init(hour, minute);
        }

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
