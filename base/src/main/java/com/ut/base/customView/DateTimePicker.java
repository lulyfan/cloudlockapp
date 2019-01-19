package com.ut.base.customView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.ut.base.R;

import java.util.Calendar;

public class DateTimePicker extends FrameLayout {

    private DatePicker datePicker;
    private TimePicker timePicker;
    private int selectedYear;
    private int selectedMonth;
    private int selectedDay;
    private int selectedHour;
    private int selectedMinute;

    public DateTimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);

        View view = LayoutInflater.from(context).inflate(R.layout.date_time_picker_layout, null);
        addView(view);

        datePicker = view.findViewById(R.id.datePicker);
        timePicker = view.findViewById(R.id.timePicker);

        init();
    }

    private void init() {
        selectedYear = datePicker.getSelectedYear();
        selectedMonth = datePicker.getSelectedMonth();
        selectedDay = datePicker.getSelectedDay();
        selectedHour = timePicker.getSelectedHour();
        selectedMinute = timePicker.getSelectedMinute();

        datePicker.setDateSelectListener(new DatePicker.DateSelectListener() {
            @Override
            public void onDateSelected(int year, int month, int day) {
                selectedYear = year;
                selectedMonth = month - 1;
                selectedDay = day;

                if (dateTimeSelectListener != null) {
                    dateTimeSelectListener.onDateTimeSelected(selectedYear, month, selectedDay, selectedHour, selectedMinute);
                }
            }
        });

        timePicker.setTimeSelectListener(new TimePicker.TimeSelectListener() {
            @Override
            public void onTimeSelected(int hour, int minute) {
                selectedHour = hour;
                selectedMinute = minute;

                if (dateTimeSelectListener != null) {
                    dateTimeSelectListener.onDateTimeSelected(selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute);
                }
            }
        });
    }

    public void reset(int year, int month, int day, int hour, int minute) {
        selectedYear = year;
        selectedMonth = month;
        selectedDay = day;
        selectedHour = hour;
        selectedMinute = minute;

        datePicker.reset(year, month, day);
        timePicker.reset(hour, minute);

        datePicker.setDateSelectListener(new DatePicker.DateSelectListener() {
            @Override
            public void onDateSelected(int year, int month, int day) {
                selectedYear = year;
                selectedMonth = month - 1;
                selectedDay = day;

                if (dateTimeSelectListener != null) {
                    dateTimeSelectListener.onDateTimeSelected(selectedYear, month, selectedDay, selectedHour, selectedMinute);
                }
            }
        });

        timePicker.setTimeSelectListener(new TimePicker.TimeSelectListener() {
            @Override
            public void onTimeSelected(int hour, int minute) {
                selectedHour = hour;
                selectedMinute = minute;

                if (dateTimeSelectListener != null) {
                    dateTimeSelectListener.onDateTimeSelected(selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute);
                }
            }
        });
    }

    public Calendar getDateTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, selectedYear);
        calendar.set(Calendar.MONTH, selectedMonth);
        calendar.set(Calendar.DAY_OF_MONTH, selectedDay);
        calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
        calendar.set(Calendar.MINUTE, selectedMinute);

        return calendar;
    }

    public int getSelectedYear() {
        return selectedYear;
    }

    public int getSelectedMonth() {
        return selectedMonth;
    }

    public int getRealMonth() {
        return selectedMonth + 1;
    }

    public int getSelectedDay() {
        return selectedDay;
    }

    public int getSelectedHour() {
        return selectedHour;
    }

    public int getSelectedMinute() {
        return selectedMinute;
    }

    private DateTimeSelectListener dateTimeSelectListener;

    public void setDateTimeSelectListener(DateTimeSelectListener dateTimeSelectListener) {
        this.dateTimeSelectListener = dateTimeSelectListener;
    }

    public interface DateTimeSelectListener {
        void onDateTimeSelected(int year, int month, int day, int hour, int minute);
    }
}
