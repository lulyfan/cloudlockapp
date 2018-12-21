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
import com.ut.base.customView.DateTimePicker;
import com.ut.base.databinding.FragmentLimitTimeBinding;

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
                chooseTime(v, getString(R.string.validTime));
            }
        });

        binding.tvInvalidTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseTime(v, getString(R.string.invalidTime));
            }
        });

        ImageView iv_contact = binding.getRoot().findViewById(R.id.contact);
        iv_contact.setOnClickListener(v -> ((GrantPermissionActivity)getActivity()).selectContact());

        EditText et_phoneNum = binding.getRoot().findViewById(R.id.et_phoneNum);
        EditText et_name = binding.getRoot().findViewById(R.id.et_receiverName);
        ((GrantPermissionActivity)getActivity()).receiverPhoneNum.observe(this, s -> et_phoneNum.setText(s));
        ((GrantPermissionActivity)getActivity()).receiverName.observe(this, s -> et_name.setText(s));
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
