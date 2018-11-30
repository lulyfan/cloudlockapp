package com.ut.module_mine.customView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.aigestudio.wheelpicker.WheelPicker;
import com.aigestudio.wheelpicker.widgets.WheelYearPicker;
import com.ut.module_mine.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DatePicker extends FrameLayout {

    private WheelYearPicker yearPicker;
    private WheelPicker monthPicker;
    private WheelPicker dayPicker;
    private int selectedYear;
    private int selectedMonth;
    private int selectedDay;

    public DatePicker(Context context, AttributeSet attrs) {
        super(context, attrs);

        View view = LayoutInflater.from(context).inflate(R.layout.datepicker_layout, null);
        yearPicker = view.findViewById(R.id.wheelYearPicker);
        monthPicker = view.findViewById(R.id.wheelMonthPicker);
        dayPicker = view.findViewById(R.id.wheelDayPicker);
        addView(view);

        init();
    }

    private void init() {
        initYear();
        initMouth();
        initDay();
    }

    private void initYear() {
        selectedYear = yearPicker.getCurrentYear();
        yearPicker.setYearStart(selectedYear);
        yearPicker.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
            @Override
            public void onItemSelected(WheelPicker picker, Object data, int position) {
                selectedYear = (int) data;
                setYear(selectedYear);
                setYearAndMonth(selectedYear, selectedMonth);

                if (dateSelectListener != null) {
                    dateSelectListener.onDateSelected(selectedYear, selectedMonth + 1, selectedDay);
                }
            }
        });

    }

    private void initDay() {
        int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        setDayStart(currentDay);

        dayPicker.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
            @Override
            public void onItemSelected(WheelPicker picker, Object data, int position) {
                selectedDay = Integer.parseInt((String) data);

                if (dateSelectListener != null) {
                    dateSelectListener.onDateSelected(selectedYear, selectedMonth + 1, selectedDay);
                }
            }
        });
    }

    private void initMouth() {
        int currentMouth = Calendar.getInstance().get(Calendar.MONTH);
        setMouthStart(currentMouth);

        monthPicker.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
            @Override
            public void onItemSelected(WheelPicker picker, Object data, int position) {
                selectedMonth = Integer.parseInt((String) data) - 1;
                setYearAndMonth(selectedYear, selectedMonth);

                if (dateSelectListener != null) {
                    dateSelectListener.onDateSelected(selectedYear, selectedMonth + 1, selectedDay);
                }
            }
        });
    }

    private void setMouthStart(int start) {
        updateMouth(start);
        selectedMonth = start;
    }

    private void updateMouth(int start) {
        List<String> mouths = new ArrayList<>();
        int position = 0;
        for (int i=start; i<12; i++) {
            mouths.add(String.format("%02d", i + 1));

            if (i == selectedMonth) {
                position = i - start;
            }
        }
        monthPicker.setData(mouths);
        monthPicker.setSelectedItemPosition(position);
        selectedMonth = Integer.parseInt(mouths.get(position)) - 1;
    }

    private void setDayStart(int start) {
        updateDay(start, selectedYear, selectedMonth);
        selectedDay = start;
    }

    private void updateDay(int start, int year, int mouth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, mouth);

        int dayCount = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        selectedDay = Math.min(selectedDay, dayCount);

        List<String> days = new ArrayList<>();
        int position = 0;
        for (int i=start; i<=dayCount; i++) {
            days.add(String.format("%02d", i));

            if (i == selectedDay) {
                position = i - start;
            }
        }
        dayPicker.setData(days);
        dayPicker.setSelectedItemPosition(position);
        selectedDay = Integer.parseInt(days.get(position));
    }

    private void setYearAndMonth(int year, int mouth) {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        if (year == currentYear && mouth == currentMonth) {
            updateDay(currentDay, currentYear, currentMonth);
        } else {
            updateDay(1, selectedYear, selectedMonth);
        }
    }

    private void setYear(int year) {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);

        if (year == currentYear) {
            updateMouth(currentMonth);
        } else {
            updateMouth(0);
        }
    }

    public Calendar getDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, selectedYear);
        calendar.set(Calendar.MONTH, selectedMonth);
        calendar.set(Calendar.DAY_OF_MONTH, selectedDay);

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

    private DateSelectListener dateSelectListener;

    public void setDateSelectListener(DateSelectListener dateSelectListener) {
        this.dateSelectListener = dateSelectListener;
    }

    public interface DateSelectListener {
        void onDateSelected(int year, int month, int day);
    }


}
