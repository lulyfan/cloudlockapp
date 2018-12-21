package com.ut.base.fragment;


import android.arch.lifecycle.Observer;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ut.base.R;
import com.ut.base.Utils.DialogUtil;
import com.ut.base.activity.GrantPermissionActivity;
import com.ut.base.customView.DatePicker;
import com.ut.base.customView.TimePicker;
import com.ut.base.databinding.FragmentLoopBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoopFragment extends Fragment {
    private FragmentLoopBinding binding;

    public LoopFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_loop, container, false);
        init();
        return binding.getRoot();
    }

    private void init() {
        binding.validTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseTime(v, getString(R.string.validTime));
            }
        });

        binding.invalidTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseTime(v, getString(R.string.invalidTime));
            }
        });

        binding.startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               chooseDate(v, getString(R.string.startDate));
            }
        });

        binding.endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseDate(v, getString(R.string.endDate));
            }
        });

        ImageView iv_contact = binding.getRoot().findViewById(R.id.contact);
        iv_contact.setOnClickListener(v -> ((GrantPermissionActivity)getActivity()).selectContact());

        EditText et_phoneNum = binding.getRoot().findViewById(R.id.et_phoneNum);
        EditText et_name = binding.getRoot().findViewById(R.id.et_receiverName);
        ((GrantPermissionActivity)getActivity()).receiverPhoneNum.observe(this, s -> et_phoneNum.setText(s));
        ((GrantPermissionActivity)getActivity()).receiverName.observe(this, s -> et_name.setText(s));
    }

    private void chooseDate(View v, String title) {
        DialogUtil.chooseDate(getContext(), title, new DatePicker.DateSelectListener() {
            @Override
            public void onDateSelected(int year, int month, int day) {
                TextView textView = (TextView) v;
                textView.setText(year + "/" + String.format("%02d", month) + "/" + String.format("%02d", day));
                textView.setTextColor(getResources().getColor(R.color.gray3));
            }
        });
    }

    private void chooseTime(View v, String title) {
        DialogUtil.chooseTime(getContext(), title, new TimePicker.TimeSelectListener() {
            @Override
            public void onTimeSelected(int hour, int minute) {
                TextView textView = (TextView) v;
                textView.setText(String.format("%02d", hour) + ":" + String.format("%02d", minute));
                textView.setTextColor(getResources().getColor(R.color.gray3));
            }
        });
    }

}
