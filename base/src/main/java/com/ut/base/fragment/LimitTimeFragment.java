package com.ut.base.fragment;


import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.ut.base.BaseFragment;
import com.ut.base.R;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.base.UIUtils.SystemUtils;
import com.ut.base.Utils.DialogUtil;
import com.ut.base.activity.SendKeyActivity;
import com.ut.base.databinding.FragmentLimitTimeBinding;
import com.ut.base.viewModel.SendKeyViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class LimitTimeFragment extends BaseFragment {

    private FragmentLimitTimeBinding binding;
    private SendKeyViewModel viewModel;

    private int sYear, sMonth, sDay, sHour, sMinute;
    private int eYear, eMonth, eDay, eHour, eMinute;
    private int mYear, mMonth, mDay, mHour, mMinute;

    public LimitTimeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (binding == null) {
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_limit_time, container, false);
            viewModel = ViewModelProviders.of(getActivity()).get(SendKeyViewModel.class);
            initUI();
            initData();
        }
        return binding.getRoot();
    }

    private void initData() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        String limitStartTime = viewModel.limitStartTime.getValue();

        if (!TextUtils.isEmpty(limitStartTime)) {
            limitStartTime = limitStartTime.replace("-", "/");
            limitStartTime = limitStartTime.substring(0, limitStartTime.length() - 3);
        } else {
            Calendar calendar = Calendar.getInstance();
            limitStartTime = sdf.format(new Date(calendar.getTimeInMillis()));
            sYear = calendar.get(Calendar.YEAR);
            sMonth = calendar.get(Calendar.MONTH);
            sDay = calendar.get(Calendar.DATE);
            sHour = calendar.get(Calendar.HOUR_OF_DAY);
            sMinute = calendar.get(Calendar.MINUTE);

            viewModel.limitStartTime.setValue(limitStartTime.concat(":00"));
        }

        binding.tvValidTime.setText(limitStartTime);
        binding.tvValidTime.setTextColor(getResources().getColor(R.color.gray3));

        String limitEndTime = viewModel.limitEndTime.getValue();

        if (!TextUtils.isEmpty(limitEndTime)) {
            limitEndTime = limitEndTime.replace("-", "/");
            limitEndTime = limitEndTime.substring(0, limitEndTime.length() - 3);
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.HOUR_OF_DAY, 1);
            limitEndTime = sdf.format(new Date(calendar.getTimeInMillis()));
            viewModel.limitEndTime.setValue(limitEndTime.concat(":00"));
        }

        binding.tvInvalidTime.setText(limitEndTime);
        binding.tvInvalidTime.setTextColor(getResources().getColor(R.color.gray3));
    }

    private void initUI() {
        binding.tvValidTime.setOnClickListener(v -> {
            mYear = sYear;
            mMonth = sMonth;
            mDay = sDay;
            mHour = sHour;
            mMinute = sMinute;
            chooseTime(v, getString(R.string.validTime));
        });

        binding.tvInvalidTime.setOnClickListener(v -> {
            handleInvalidTime();
            chooseTime(v, getString(R.string.invalidTime));
        });

        binding.getRoot().findViewById(R.id.contact).setOnClickListener(v -> ((SendKeyActivity) getActivity()).selectContact());

        EditText et_phoneNum = binding.getRoot().findViewById(R.id.et_phoneNum);
        EditText et_name = binding.getRoot().findViewById(R.id.et_receiverName);
        et_phoneNum.addTextChangedListener(viewModel.receiverPhoneWatcher);
        et_name.addTextChangedListener(viewModel.keyNameWatcher);

        viewModel.receiverPhoneNum.observe(this, s -> {
            if (!et_phoneNum.getText().toString().equals(s) && s != null) {
                et_phoneNum.setText(s);
                et_phoneNum.setSelection(s.length());


                if(getActivity()!= null) {
                    String extraPhone = getActivity().getIntent().getStringExtra(RouterUtil.LockModuleExtraKey.EXTRA_LOCK_SENDKEY_MOBILE);
                    boolean cantEdit = getActivity().getIntent().getBooleanExtra(RouterUtil.LockModuleExtraKey.EXTRA_CANT_EDIT_PHONE,false);
                    if(s.equals(extraPhone) && cantEdit) {
                        et_phoneNum.setEnabled(false);
                    }
                }
            }
        });
        viewModel.keyName.observe(this, s -> {
            if (!et_name.getText().toString().equals(s) && s != null) {
                et_name.setText(s);
                et_name.setSelection(s.length());
            }
        });


        viewModel.sendingKey.observe(this, isSending-> {
            binding.getRoot().findViewById(R.id.contact).setEnabled(!isSending);
        });
    }

    private void handleInvalidTime() {
         if (sYear > 0) {
            mYear = sYear;
            mMonth = sMonth;
            mDay = sDay;
            mHour = sHour;
            mMinute = sMinute;
            Calendar calendar = Calendar.getInstance();
            calendar.set(mYear, mMonth, mDay, mHour, mMinute);
            calendar.add(Calendar.HOUR_OF_DAY, 1);
            mYear = calendar.get(Calendar.YEAR);
            mMonth = calendar.get(Calendar.MONTH);
            mDay = calendar.get(Calendar.DAY_OF_MONTH);
            mHour = calendar.get(Calendar.HOUR_OF_DAY);
            mMinute = calendar.get(Calendar.MINUTE);
        }
    }

    private void chooseTime(View v, String title) {
        SystemUtils.hideKeyboard(getContext(), v);
        DialogUtil.chooseDateTime(getContext(), title, (year, month, day, hour, minute) -> {
            TextView textView = (TextView) v;
            textView.setText(year + "-" + String.format("%02d", month) + "-" + String.format("%02d", day)
                    + " " + String.format("%02d", hour) + ":" + String.format("%02d", minute));

            if (getString(R.string.validTime).equals(title)) {
                String startTime = textView.getText().toString().concat(":00");
                viewModel.limitStartTime.setValue(startTime);

                sYear = year;
                sMonth = month - 1;
                sDay = day;
                sHour = hour;
                sMinute = minute;

            } else if (getString(R.string.invalidTime).equals(title)) {
                String endTime = textView.getText().toString().concat(":00");
                viewModel.limitEndTime.setValue(endTime);

                eYear = year;
                eMonth = month - 1;
                eDay = day;
                eHour = hour;
                eMinute = minute;
            }
        }, mYear != mMonth, mYear, mMonth, mDay, mHour, mMinute);
    }
}
