package com.ut.base.fragment;


import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.ut.base.BaseFragment;
import com.ut.base.R;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.base.UIUtils.SystemUtils;
import com.ut.base.Utils.DialogUtil;
import com.ut.base.activity.SendKeyActivity;
import com.ut.base.databinding.FragmentLoopBinding;
import com.ut.base.viewModel.SendKeyViewModel;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoopFragment extends BaseFragment {
    private FragmentLoopBinding binding;
    private SendKeyViewModel viewModel;
    private int mY, mM, mD;
    private int sY, sM, sD, eY, eM, eD;
    private int mHour, mMin;
    private int sHour, sMin;

    public LoopFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (binding == null) {
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_loop, container, false);
            viewModel = ViewModelProviders.of(getActivity()).get(SendKeyViewModel.class);
            init();
            initData();
        }
        return binding.getRoot();
    }

    private void initData() {
        String loopStartTime = viewModel.loopStartTime.getValue();
        if (loopStartTime != null && !"".equals(loopStartTime)) {
            binding.startDate.setText(loopStartTime.replace("-", "/"));
            binding.startDate.setTextColor(getResources().getColor(R.color.gray3));
        }

        String loopEndTime = viewModel.loopEndTime.getValue();
        if (loopEndTime != null && !"".equals(loopEndTime)) {
            binding.endDate.setText(loopEndTime.replace("-", "/"));
            binding.endDate.setTextColor(getResources().getColor(R.color.gray3));
        }

        String startTimeRange = viewModel.startTimeRange.getValue();
        if (startTimeRange != null && !"".equals(startTimeRange)) {
            binding.validTime.setText(startTimeRange.replace(":00", ""));
            binding.validTime.setTextColor(getResources().getColor(R.color.gray3));
        }

        String endTimeRange = viewModel.endTimeRange.getValue();
        if (endTimeRange != null && !"".equals(endTimeRange)) {
            binding.invalidTime.setText(endTimeRange.replace(":00", ""));
            binding.invalidTime.setTextColor(getResources().getColor(R.color.gray3));
        }
    }

    private void init() {
        if (binding == null) return;
        binding.validTime.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            mHour = calendar.get(Calendar.HOUR_OF_DAY);
            mMin = calendar.get(Calendar.MINUTE);
            chooseTime(v, getString(R.string.validTime));
        });
        binding.invalidTime.setOnClickListener(v -> {

            mHour = sHour;
            mMin = sMin;

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, mHour);
            calendar.set(Calendar.MINUTE, mMin);
            calendar.add(Calendar.MINUTE, 15);

            mHour = calendar.get(Calendar.HOUR_OF_DAY);
            mMin = calendar.get(Calendar.MINUTE);

            chooseTime(v, getString(R.string.invalidTime));
        });
        binding.startDate.setOnClickListener(v -> {
            mY = sY;
            mM = sM;
            mD = sD;
            chooseDate(v, getString(R.string.startDate));
        });
        binding.endDate.setOnClickListener(v -> {
            handleEnDate();
            chooseDate(v, getString(R.string.endDate));
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

                if (getActivity() != null) {
                    String extraPhone = getActivity().getIntent().getStringExtra(RouterUtil.LockModuleExtraKey.EXTRA_LOCK_SENDKEY_MOBILE);
                    boolean cantEdit = getActivity().getIntent().getBooleanExtra(RouterUtil.LockModuleExtraKey.EXTRA_CANT_EDIT_PHONE, false);
                    if (s.equals(extraPhone) && cantEdit) {
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

        View root = binding.getRoot();
        CheckBox checkBox1 = root.findViewById(R.id.monday);
        CheckBox checkBox2 = root.findViewById(R.id.tuesday);
        CheckBox checkBox3 = root.findViewById(R.id.wednessday);
        CheckBox checkBox4 = root.findViewById(R.id.thursday);
        CheckBox checkBox5 = root.findViewById(R.id.friday);
        CheckBox checkBox6 = root.findViewById(R.id.saturday);
        CheckBox checkBox7 = root.findViewById(R.id.sunday);

        checkBox1.setOnCheckedChangeListener(weekListener);
        checkBox2.setOnCheckedChangeListener(weekListener);
        checkBox3.setOnCheckedChangeListener(weekListener);
        checkBox4.setOnCheckedChangeListener(weekListener);
        checkBox5.setOnCheckedChangeListener(weekListener);
        checkBox6.setOnCheckedChangeListener(weekListener);
        checkBox7.setOnCheckedChangeListener(weekListener);


        viewModel.sendingKey.observe(this, isSending -> binding.getRoot().findViewById(R.id.contact).setEnabled(!isSending));
    }

    private void handleEnDate() {
        if (sY > 0) {
            mY = sY;
            mM = sM;
            mD = sD;
            Calendar calendar = Calendar.getInstance();
            calendar.set(mY, mM, mD);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            mY = calendar.get(Calendar.YEAR);
            mM = calendar.get(Calendar.MONTH);
            mD = calendar.get(Calendar.DAY_OF_MONTH);
        }
    }

    private void chooseDate(View v, String title) {
        SystemUtils.hideKeyboard(getContext(), v);
        DialogUtil.chooseDate(getContext(), title, (year, month, day) -> {
            TextView textView = (TextView) v;
            textView.setText(year + "/" + String.format("%02d", month) + "/" + String.format("%02d", day));
            textView.setTextColor(getResources().getColor(R.color.gray3));

            if (getString(R.string.startDate).equals(title)) {
                String startTime = textView.getText().toString().replace("/", "-");
                viewModel.loopStartTime.setValue(startTime);
                sY = year;
                sM = month - 1;
                sD = day;
            } else if (getString(R.string.endDate).equals(title)) {
                eY = year;
                eM = month - 1;
                eD = day;
                String endTime = textView.getText().toString().replace("/", "-");
                viewModel.loopEndTime.setValue(endTime);
            }
        }, mY != mM, mY, mM, mD);
    }

    private void chooseTime(View v, String title) {
        SystemUtils.hideKeyboard(getContext(), v);
        DialogUtil.chooseTime(getContext(), title, (hour, minute) -> {
            TextView textView = (TextView) v;
            textView.setText(String.format("%02d", hour) + ":" + String.format("%02d", minute));
            textView.setTextColor(getResources().getColor(R.color.gray3));

            if (getString(R.string.validTime).equals(title)) {
                String startTimeRange = textView.getText().toString().concat(":00");
                viewModel.startTimeRange.setValue(startTimeRange);

                mHour = sHour = hour;
                mMin = sMin = minute;

            } else if (getString(R.string.invalidTime).equals(title)) {
                String endTimeRange = textView.getText().toString().concat(":00");
                viewModel.endTimeRange.setValue(endTimeRange);
            }
        }, getString(R.string.invalidTime).equals(title));
    }

    private CompoundButton.OnCheckedChangeListener weekListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            View root = binding.getRoot();
            String weeks = "";

            CheckBox checkBox1 = root.findViewById(R.id.monday);
            CheckBox checkBox2 = root.findViewById(R.id.tuesday);
            CheckBox checkBox3 = root.findViewById(R.id.wednessday);
            CheckBox checkBox4 = root.findViewById(R.id.thursday);
            CheckBox checkBox5 = root.findViewById(R.id.friday);
            CheckBox checkBox6 = root.findViewById(R.id.saturday);
            CheckBox checkBox7 = root.findViewById(R.id.sunday);

            weeks += checkBox1.isChecked() ? "1," : "";
            weeks += checkBox2.isChecked() ? "2," : "";
            weeks += checkBox3.isChecked() ? "3," : "";
            weeks += checkBox4.isChecked() ? "4," : "";
            weeks += checkBox5.isChecked() ? "5," : "";
            weeks += checkBox6.isChecked() ? "6," : "";
            weeks += checkBox7.isChecked() ? "7," : "";
            viewModel.weeks.setValue(weeks);
        }
    };

    @Override
    protected void onUserVisible() {
        super.onUserVisible();
    }
}
