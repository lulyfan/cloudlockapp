package com.ut.base.customView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.aigestudio.wheelpicker.WheelPicker;
import com.aigestudio.wheelpicker.widgets.WheelYearPicker;
import com.ut.base.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DatePicker extends FrameLayout {

    private WheelYearPicker yearPicker;
    private WheelPicker monthPicker;
    private WheelPicker dayPicker;
    private boolean isInited;

    public DatePicker(Context context, AttributeSet attrs) {
        super(context, attrs);

        View view = LayoutInflater.from(context).inflate(R.layout.datepicker_layout, null);
        yearPicker = view.findViewById(R.id.wheelYearPicker);
        monthPicker = view.findViewById(R.id.wheelMonthPicker);
        dayPicker = view.findViewById(R.id.wheelDayPicker);
        addView(view);

        init();
        isInited = true;
    }

    private void init() {
        initYear();
        initMonth();
        initDay();
    }

    public void init(int startYear, int startMonth, int startDay) {
        initYear(startYear);
        initMonth(startMonth);
        initDay(startDay);
    }

    private void initYear() {
        initYear(yearPicker.getCurrentYear());
    }

    public void initYear(int startYear, int endYear) {
        yearPicker.setYearEnd(endYear);
        initYear(startYear);
    }

    public void initYear(int startYear) {
        yearPicker.setYearStart(startYear);
        yearPicker.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
            @Override
            public void onItemSelected(WheelPicker picker, Object data, int position) {
                adjustMouthByYear();
                adjustDayByYearAndMonth();

                if (dateSelectListener != null) {
                    dateSelectListener.onDateSelected(getSelectedYear(), getRealMonth(), getSelectedDay());
                }
            }
        });
    }

    public void setDate(int year, int month, int day) {
        yearPicker.setSelectedYear(year);
        adjustMouthByYear();
        adjustDayByYearAndMonth();

        setSelectedMonth(month);
        setSelectedDay(day);
    }

    public void setYearEnd(int endYear) {
        yearPicker.setYearEnd(endYear);
    }

    private void initDay() {
        int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        initDay(currentDay);
    }

    public void initDay(int day) {
        setDayStart(day);
        dayPicker.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
            @Override
            public void onItemSelected(WheelPicker picker, Object data, int position) {

                if (dateSelectListener != null) {
                    dateSelectListener.onDateSelected(getSelectedYear(), getRealMonth(), getSelectedDay());
                }
            }
        });
    }


    private void initMonth() {
        int currentMouth = Calendar.getInstance().get(Calendar.MONTH);
        initMonth(currentMouth);
    }

    public void initMonth(int month) {
        setMouthStart(month);

        monthPicker.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
            @Override
            public void onItemSelected(WheelPicker picker, Object data, int position) {
                adjustDayByYearAndMonth();

                if (dateSelectListener != null) {
                    dateSelectListener.onDateSelected(getSelectedYear(), getRealMonth(), getSelectedDay());
                }
            }
        });
    }

    private void setMouthStart(int start) {
        updateMouth(start);
    }

    private void setDayStart(int start) {
        updateDay(start, getSelectedYear(), getSelectedMonth());
    }

    private void updateMouth(int start) {
        String lastSelectedMonth = getSelectedMonthString();

        List<String> months = new ArrayList<>();
        for (int i = start; i < 12; i++) {
            months.add(String.format("%02d", i + 1));
        }
        monthPicker.setData(months);

        int monthPos = months.indexOf(lastSelectedMonth);
        if (monthPos != -1) {
            monthPicker.setSelectedItemPosition(monthPos);
        } else {
            monthPicker.setSelectedItemPosition(0);
        }
    }

    private void updateDay(int start, int year, int month) {

        String lastSelectedDay = getSelectedDayString();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);

        int dayCount = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        List<String> days = new ArrayList<>();
        for (int i = start; i <= dayCount; i++) {
            days.add(String.format("%02d", i));
        }
        dayPicker.setData(days);

        if (!isInited) {
            dayPicker.setSelectedItemPosition(0);
        } else {
            int dayPos = days.indexOf(lastSelectedDay);
            if (dayPos != -1) {
                dayPicker.setSelectedItemPosition(dayPos);
            } else {
                dayPicker.setSelectedItemPosition(days.size() - 1);
            }
        }
    }

    /**
     * 根据年份和月份调整日期
     */
    private void adjustDayByYearAndMonth() {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        if (getSelectedYear() == currentYear && getSelectedMonth() - 1 == currentMonth) {
            updateDay(currentDay, currentYear, currentMonth);
        } else {
            updateDay(1, getSelectedYear(), getSelectedMonth() - 1);
        }
    }

    /**
     * 根据年份变化调整月份
     */
    private void adjustMouthByYear() {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);

        if (getSelectedYear() == currentYear) {
            updateMouth(currentMonth);
        } else {
            updateMouth(0);
        }
    }

    public int getSelectedYear() {
        return (int) yearPicker.getData().get(yearPicker.getCurrentItemPosition());
    }

    public String getSelectedYearString() {
        return (String) yearPicker.getData().get(yearPicker.getCurrentItemPosition());
    }

    public void setSelectedMonth(int month) {
        setWheelData(monthPicker, month);
    }

    public int getSelectedMonth() {
        return Integer.parseInt((String) monthPicker.getData().get(monthPicker.getCurrentItemPosition()));
    }

    public String getSelectedMonthString() {
        return (String) monthPicker.getData().get(monthPicker.getCurrentItemPosition());
    }

    public int getRealMonth() {
        return getSelectedMonth();
    }

    public void setSelectedDay(int day) {
        setWheelData(dayPicker, day);
    }

    public int getSelectedDay() {
        return Integer.parseInt((String) dayPicker.getData().get(dayPicker.getCurrentItemPosition()));
    }

    public String getSelectedDayString() {
        return (String) dayPicker.getData().get(dayPicker.getCurrentItemPosition());
    }

    private void setWheelData(WheelPicker picker, int data) {
        String sData = String.format("%02d", data);
        int pos = picker.getData().indexOf(sData);
        if (pos != -1) {
            picker.setSelectedItemPosition(pos);
        }
    }

    private DateSelectListener dateSelectListener;

    public void setDateSelectListener(DateSelectListener dateSelectListener) {
        this.dateSelectListener = dateSelectListener;
    }

    public interface DateSelectListener {
        void onDateSelected(int year, int month, int day);
    }


}
