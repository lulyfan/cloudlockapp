package com.ut.module_mine.fragment;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ut.module_mine.util.DialogUtil;
import com.ut.module_mine.R;
import com.ut.module_mine.customView.DateTimePicker;
import com.ut.module_mine.databinding.FragmentLimitTimeBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class LimitTimeFragment extends Fragment {

    private FragmentLimitTimeBinding binding;
    public LimitTimeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_limit_time, container, false);
        initUI();

        return binding.getRoot();
    }

    private void initUI() {
        binding.tvValidTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseTime(v, "生效时间");
            }
        });

        binding.tvInvalidTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseTime(v, "失效时间");
            }
        });
    }


    private void chooseTime(View v, String title) {
        DialogUtil.chooseDateTime(getContext(), title, new DateTimePicker.DateTimeSelectListener() {
            @Override
            public void onDateTimeSelected(int year, int month, int day, int hour, int minute) {
                TextView textView = (TextView) v;
                textView.setText(year + "/" + String.format("%02d", month) + "/" + String.format("%02d", day) + " " + hour + ":" + minute);
                textView.setTextColor(getResources().getColor(R.color.gray3));
            }
        });

    }

}
