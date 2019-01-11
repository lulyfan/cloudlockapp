package com.ut.base.fragment;


import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ut.base.R;
import com.ut.base.UIUtils.SystemUtils;
import com.ut.base.Utils.DialogUtil;
import com.ut.base.activity.GrantPermissionActivity;
import com.ut.base.customView.DatePicker;
import com.ut.base.customView.TimePicker;
import com.ut.base.databinding.FragmentLoopBinding;
import com.ut.base.viewModel.GrantPermisssionViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoopFragment extends Fragment {
    private FragmentLoopBinding binding;
    private GrantPermisssionViewModel viewModel;

    public LoopFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_loop, container, false);
        viewModel = ViewModelProviders.of(getActivity()).get(GrantPermisssionViewModel.class);
        init();
        initData();

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
        binding.validTime.setOnClickListener(v -> chooseTime(v, getString(R.string.validTime)));
        binding.invalidTime.setOnClickListener(v -> chooseTime(v, getString(R.string.invalidTime)));
        binding.startDate.setOnClickListener(v -> chooseDate(v, getString(R.string.startDate)));
        binding.endDate.setOnClickListener(v -> chooseDate(v, getString(R.string.endDate)));

        ImageView iv_contact = binding.getRoot().findViewById(R.id.contact);
        iv_contact.setOnClickListener(v -> ((GrantPermissionActivity)getActivity()).selectContact());

        EditText et_phoneNum = binding.getRoot().findViewById(R.id.et_phoneNum);
        EditText et_name = binding.getRoot().findViewById(R.id.et_receiverName);
        et_phoneNum.addTextChangedListener(viewModel.receiverPhoneWatcher);
        et_name.addTextChangedListener(viewModel.keyNameWatcher);

        viewModel.receiverPhoneNum.observe(this, s -> {
            if (!et_phoneNum.getText().toString().equals(s)) {
                et_phoneNum.setText(s);
            }
        });
        viewModel.keyName.observe(this, s -> {
            if (!et_name.getText().toString().equals(s)) {
                et_name.setText(s);
            }
        });

        View root = binding.getRoot();
        CheckBox checkBox1 =  root.findViewById(R.id.monday);
        CheckBox checkBox2 =  root.findViewById(R.id.tuesday);
        CheckBox checkBox3 =  root.findViewById(R.id.wednessday);
        CheckBox checkBox4 =  root.findViewById(R.id.thursday);
        CheckBox checkBox5 =  root.findViewById(R.id.friday);
        CheckBox checkBox6 =  root.findViewById(R.id.saturday);
        CheckBox checkBox7 =  root.findViewById(R.id.sunday);

        checkBox1.setOnCheckedChangeListener(weekListener);
        checkBox2.setOnCheckedChangeListener(weekListener);
        checkBox3.setOnCheckedChangeListener(weekListener);
        checkBox4.setOnCheckedChangeListener(weekListener);
        checkBox5.setOnCheckedChangeListener(weekListener);
        checkBox6.setOnCheckedChangeListener(weekListener);
        checkBox7.setOnCheckedChangeListener(weekListener);
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

            } else if (getString(R.string.endDate).equals(title)) {
                String endTime = textView.getText().toString().replace("/", "-");
                viewModel.loopEndTime.setValue(endTime);
            }
        });
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
            } else if (getString(R.string.invalidTime).equals(title)) {
                String endTimeRange = textView.getText().toString().concat(":00");
                viewModel.endTimeRange.setValue(endTimeRange);
            }
        });
    }

    private CompoundButton.OnCheckedChangeListener weekListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            View root = binding.getRoot();
            String weeks = "";

            CheckBox checkBox1 =  root.findViewById(R.id.monday);
            CheckBox checkBox2 =  root.findViewById(R.id.tuesday);
            CheckBox checkBox3 =  root.findViewById(R.id.wednessday);
            CheckBox checkBox4 =  root.findViewById(R.id.thursday);
            CheckBox checkBox5 =  root.findViewById(R.id.friday);
            CheckBox checkBox6 =  root.findViewById(R.id.saturday);
            CheckBox checkBox7 =  root.findViewById(R.id.sunday);

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

}
