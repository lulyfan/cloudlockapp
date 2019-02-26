package com.ut.base.customView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.aigestudio.wheelpicker.WheelPicker;
import com.ut.base.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TimePicker extends FrameLayout {

    private WheelPicker hourPicker;
    private WheelPicker minutePicker;

    public TimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);

        View view = LayoutInflater.from(context).inflate(R.layout.timepicker_layout, null);
        hourPicker = view.findViewById(R.id.hourPicker);
        minutePicker = view.findViewById(R.id.minutePicker);
        addView(view);

        init();
    }

    private void init() {
        init(0, 0);
    }

    public void init(int startHour, int startMinute) {
        List<String> hours = new ArrayList<>();
        for (int i=startHour; i<24; i++) {
            hours.add(String.format("%02d", i));
        }
        hourPicker.setData(hours);

        List<String> minutes = new ArrayList<>();
        for (int i=startMinute; i<60; i++) {
            minutes.add(String.format("%02d", i));
        }
        minutePicker.setData(minutes);

        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);
        setTime(currentHour, currentMinute);

        hourPicker.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
            @Override
            public void onItemSelected(WheelPicker picker, Object data, int position) {

                if (timeSelectListener != null) {
                    timeSelectListener.onTimeSelected(getSelectedHour(), getSelectedMinute());
                }
            }
        });

        minutePicker.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
            @Override
            public void onItemSelected(WheelPicker picker, Object data, int position) {

                if (timeSelectListener != null) {
                    timeSelectListener.onTimeSelected(getSelectedHour(), getSelectedMinute());

                }
            }
        });
    }

    public void adjustHour(boolean isStartFromCurrentHour) {

        String lastSelectedHour = getSelectedHourString();

        Calendar now = Calendar.getInstance();
        int start = 0;

        if (isStartFromCurrentHour) {
            start = now.get(Calendar.HOUR_OF_DAY);
        }

        List<String> hours = new ArrayList<>();
        for (int i = start; i<24; i++) {
            hours.add(String.format("%02d", i));
        }
        hourPicker.setData(hours);

        int hourPos = hours.indexOf(lastSelectedHour);
        if (hourPos != -1) {
            hourPicker.setSelectedItemPosition(hourPos);
        } else {
            hourPicker.setSelectedItemPosition(0);
        }
    }

    public void adjustMinute(boolean isStartFromCurrentMinute) {

        String lastSelectedMinute = getSelectedMinuteString();

        Calendar now = Calendar.getInstance();
        int start = 0;

        if (isStartFromCurrentMinute) {
            start = now.get(Calendar.MINUTE);
        }

        List<String> minutes = new ArrayList<>();
        for (int i = start; i<60; i++) {
            minutes.add(String.format("%02d", i));
        }
        minutePicker.setData(minutes);

        int minutePos = minutes.indexOf(lastSelectedMinute);
        if (minutePos != -1) {
            minutePicker.setSelectedItemPosition(minutePos);
        } else {
            minutePicker.setSelectedItemPosition(0);
        }
    }

    /**
     * 设置小时轮子是否循环
     * @param isCyclic
     */
    public void setHourCyclic(boolean isCyclic) {
        hourPicker.setCyclic(isCyclic);
    }

    /**
     * 设置分钟轮子是否循环
     * @param isCyclic
     */
    public void setMinutePicker(boolean isCyclic) {
        minutePicker.setCyclic(isCyclic);
    }

    /**
     * 设置时间轮子是否循环
     * @param isHourCyclic
     * @param isMinuteCyclic
     */
    public void setCyclic(boolean isHourCyclic, boolean isMinuteCyclic) {
        hourPicker.setCyclic(isHourCyclic);
        minutePicker.setCyclic(isMinuteCyclic);
    }

    public void setTime(int hour, int minute) {

        if (hour < 0 || hour > 23) {
            return;
        }

        if (minute < 0 || minute > 59) {
            return;
        }

        String sHour = String.format("%02d", hour);
        String sMinute = String.format("%02d", minute);
        List hourDatas = hourPicker.getData();
        List minuteDatas = minutePicker.getData();

        int hourPos = hourDatas.indexOf(sHour);
        if (hourPos != -1) {
            hourPicker.setSelectedItemPosition(hourPos);
        }

        int minutePos = minuteDatas.indexOf(sMinute);
        if (minutePos != -1) {
            minutePicker.setSelectedItemPosition(minutePos);
        }
    }

    public int getSelectedHour() {
        return Integer.parseInt((String) hourPicker.getData().get(hourPicker.getCurrentItemPosition()));
    }

    public String getSelectedHourString() {
        return (String) hourPicker.getData().get(hourPicker.getCurrentItemPosition());
    }

    public int getSelectedMinute() {
        return Integer.parseInt((String) minutePicker.getData().get(minutePicker.getCurrentItemPosition()));
    }

    public String getSelectedMinuteString() {
        return (String) minutePicker.getData().get(minutePicker.getCurrentItemPosition());
    }

    private TimeSelectListener timeSelectListener;

    public void setTimeSelectListener(TimeSelectListener timeSelectListener) {
        this.timeSelectListener = timeSelectListener;
    }

    public interface TimeSelectListener {
        void onTimeSelected(int hour, int minute);
    }


}
