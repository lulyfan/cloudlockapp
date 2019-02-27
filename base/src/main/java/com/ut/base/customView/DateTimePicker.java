package com.ut.base.customView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.ut.base.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DateTimePicker extends FrameLayout {

    private DatePicker datePicker;
    private TimePicker timePicker;

    public DateTimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);

        View view = LayoutInflater.from(context).inflate(R.layout.date_time_picker_layout, null);
        addView(view);

        datePicker = view.findViewById(R.id.datePicker);
        timePicker = view.findViewById(R.id.timePicker);

        init();
    }

    private void init() {

        datePicker.setDateSelectListener(new DatePicker.DateSelectListener() {
            @Override
            public void onDateSelected(int year, int month, int day) {

                adjustHour();

                if (dateTimeSelectListener != null) {
                    dateTimeSelectListener.onDateTimeSelected(getSelectedYear(), month, getSelectedDay(),
                            getSelectedHour(), getSelectedMinute());
                }
            }
        });

        timePicker.setTimeSelectListener(new TimePicker.TimeSelectListener() {
            @Override
            public void onTimeSelected(int hour, int minute) {

                adjustMinute();

                if (dateTimeSelectListener != null) {
                    dateTimeSelectListener.onDateTimeSelected(getSelectedYear(), getSelectedMonth(), getSelectedDay(),
                            getSelectedHour(), getSelectedMinute());
                }
            }
        });
    }

    public void init(int startYear, int startMonth, int startDay, int startHour, int startMinute) {
        datePicker.init(startYear, startMonth, startDay);
        timePicker.init(startHour, startMinute);
        init();
    }

    private void adjustHour() {
        Calendar calendar = Calendar.getInstance();

        boolean isFromCurrentHour = getSelectedYear() == calendar.get(Calendar.YEAR) &&
                                    getSelectedMonth() - 1 == calendar.get(Calendar.MONTH) &&
                                    getSelectedDay() == calendar.get(Calendar.DAY_OF_MONTH);

        timePicker.adjustHour(isFromCurrentHour);
        adjustMinute();
    }

    private void adjustMinute() {
        adjustMinuteByHour(getSelectedHour());
    }

    private void adjustMinuteByHour(int hour) {
        Calendar calendar = Calendar.getInstance();

        boolean isFromCurrentMinute =   getSelectedYear() == calendar.get(Calendar.YEAR) &&
                                        getSelectedMonth() - 1 == calendar.get(Calendar.MONTH) &&
                                        getSelectedDay() == calendar.get(Calendar.DAY_OF_MONTH) &&
                                        hour == calendar.get(Calendar.HOUR_OF_DAY);

        timePicker.adjustMinute(isFromCurrentMinute);
    }

    /**
     * 设置时间轮子是否循环
     * @param isHourCyclic    时钟轮子是否循环
     * @param isMinuteCyclic  分钟轮子是否循环
     */
    public void setTimeCyclic(boolean isHourCyclic, boolean isMinuteCyclic) {
        timePicker.setCyclic(isHourCyclic, isMinuteCyclic);
    }

    public void setDateTime(int year, int month, int day, int hour, int minute) {

        setDate(year, month, day);
        adjustHour();
        adjustMinuteByHour(hour);
        setTime(hour, minute);
    }

    public void setDate(int year, int month, int day) {
        datePicker.setDate(year, month, day);
    }

    public void setTime(int hour, int minute) {
        timePicker.setTime(hour, minute);
    }

    public void setYearEnd(int endYear) {
        datePicker.setYearEnd(endYear);
    }

    public int getSelectedYear() {
        return datePicker.getSelectedYear();
    }

    public int getSelectedMonth() {
        return datePicker.getSelectedMonth();
    }

    public int getRealMonth() {
        return datePicker.getRealMonth();
    }

    public int getSelectedDay() {
        return datePicker.getSelectedDay();
    }

    public int getSelectedHour() {
        return timePicker.getSelectedHour();
    }

    public int getSelectedMinute() {
        return timePicker.getSelectedMinute();
    }

    private DateTimeSelectListener dateTimeSelectListener;

    public void setDateTimeSelectListener(DateTimeSelectListener dateTimeSelectListener) {
        this.dateTimeSelectListener = dateTimeSelectListener;
    }

    public interface DateTimeSelectListener {
        void onDateTimeSelected(int year, int month, int day, int hour, int minute);
    }
}
