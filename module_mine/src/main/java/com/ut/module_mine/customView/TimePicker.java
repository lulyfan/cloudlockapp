package com.ut.module_mine.customView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.aigestudio.wheelpicker.WheelPicker;
import com.ut.module_mine.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TimePicker extends FrameLayout {

    private WheelPicker hourPicker;
    private WheelPicker minutePicker;
    private int selectedHour;
    private int selectedMinute;

    public TimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);

        View view = LayoutInflater.from(context).inflate(R.layout.timepicker_layout, null);
        hourPicker = view.findViewById(R.id.hourPicker);
        minutePicker = view.findViewById(R.id.minutePicker);
        addView(view);

        setData();
    }

    private void setData() {
        List<String> hours = new ArrayList<>();
        for (int i=0; i<24; i++) {
            hours.add(String.format("%02d", i));
        }
        hourPicker.setData(hours);

        List<String> minutes = new ArrayList<>();
        for (int i=0; i<60; i++) {
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
                selectedHour = Integer.parseInt((String) data);

                if (timeSelectListener != null) {
                    timeSelectListener.onTimeSelected(selectedHour, selectedMinute);
                }
            }
        });

        minutePicker.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
            @Override
            public void onItemSelected(WheelPicker picker, Object data, int position) {
                selectedMinute =  Integer.parseInt((String) data);

                if (timeSelectListener != null) {
                    timeSelectListener.onTimeSelected(selectedHour, selectedMinute);

                }
            }
        });
    }

    public void setTime(int hour, int minute) {

        if (hour < 0 || hour > 23) {
            return;
        }

        if (minute < 0 || minute > 59) {
            return;
        }

        hourPicker.setSelectedItemPosition(hour);
        minutePicker.setSelectedItemPosition(minute);
        selectedHour = hour;
        selectedMinute = minute;
    }

    public int getSelectedHour() {
        return selectedHour;
    }

    public int getSelectedMinute() {
        return selectedMinute;
    }

    public Calendar getTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
        calendar.set(Calendar.MINUTE, selectedMinute);
        return calendar;
    }

    private TimeSelectListener timeSelectListener;

    public void setTimeSelectListener(TimeSelectListener timeSelectListener) {
        this.timeSelectListener = timeSelectListener;
    }

    public interface TimeSelectListener {
        void onTimeSelected(int hour, int minute);
    }


}
