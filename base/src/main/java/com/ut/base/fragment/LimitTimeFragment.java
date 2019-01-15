package com.ut.base.fragment;


import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ut.base.BaseFragment;
import com.ut.base.R;
import com.ut.base.UIUtils.SystemUtils;
import com.ut.base.Utils.DialogUtil;
import com.ut.base.activity.GrantPermissionActivity;
import com.ut.base.databinding.FragmentLimitTimeBinding;
import com.ut.base.viewModel.GrantPermisssionViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class LimitTimeFragment extends BaseFragment {

    private FragmentLimitTimeBinding binding;
    private GrantPermisssionViewModel viewModel;

    public LimitTimeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (binding == null) {
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_limit_time, container, false);
            viewModel = ViewModelProviders.of(getActivity()).get(GrantPermisssionViewModel.class);
            initUI();
            initData();
        }
        return binding.getRoot();
    }

    private void initData() {
        String limitStartTime = viewModel.limitStartTime.getValue();

        if (limitStartTime != null && !"".equals(limitStartTime)) {
            limitStartTime = limitStartTime.replace("-", "/");
            limitStartTime = limitStartTime.substring(0, limitStartTime.length() - 3);

            binding.tvValidTime.setText(limitStartTime);
            binding.tvValidTime.setTextColor(getResources().getColor(R.color.gray3));
        }

        String limitEndTime = viewModel.limitEndTime.getValue();

        if (limitEndTime != null && !"".equals(limitEndTime)) {
            limitEndTime = limitEndTime.replace("-", "/");
            limitEndTime = limitEndTime.substring(0, limitEndTime.length() - 3);

            binding.tvInvalidTime.setText(limitEndTime);
            binding.tvInvalidTime.setTextColor(getResources().getColor(R.color.gray3));
        }
    }

    private void initUI() {
        binding.tvValidTime.setOnClickListener(v -> chooseTime(v, getString(R.string.validTime)));

        binding.tvInvalidTime.setOnClickListener(v -> chooseTime(v, getString(R.string.invalidTime)));

        binding.getRoot().findViewById(R.id.contact).setOnClickListener(v -> ((GrantPermissionActivity) getActivity()).selectContact());

        EditText et_phoneNum = binding.getRoot().findViewById(R.id.et_phoneNum);
        EditText et_name = binding.getRoot().findViewById(R.id.et_receiverName);
        et_phoneNum.addTextChangedListener(viewModel.receiverPhoneWatcher);
        et_name.addTextChangedListener(viewModel.keyNameWatcher);

        viewModel.receiverPhoneNum.observe(this, s -> {
            if (!et_phoneNum.getText().toString().equals(s) && s != null) {
                et_phoneNum.setText(s);
                et_phoneNum.setSelection(s.length());
            }
        });
        viewModel.keyName.observe(this, s -> {
            if (!et_name.getText().toString().equals(s) && s != null) {
                et_name.setText(s);
                et_name.setSelection(s.length());
            }
        });
    }


    private void chooseTime(View v, String title) {
        SystemUtils.hideKeyboard(getContext(), v);
        DialogUtil.chooseDateTime(getContext(), title, (year, month, day, hour, minute) -> {
            TextView textView = (TextView) v;
            textView.setText(year + "/" + String.format("%02d", month) + "/" + String.format("%02d", day)
                    + " " + String.format("%02d", hour) + ":" + String.format("%02d", minute));
            textView.setTextColor(getResources().getColor(R.color.gray3));

            if (getString(R.string.validTime).equals(title)) {
                String startTime = textView.getText().toString().replace("/", "-").concat(":00");
                viewModel.limitStartTime.setValue(startTime);

            } else if (getString(R.string.invalidTime).equals(title)) {
                String endTime = textView.getText().toString().replace("/", "-").concat(":00");
                viewModel.limitEndTime.setValue(endTime);
            }
        });
    }

    @Override
    protected void onUserVisible() {
        super.onUserVisible();
    }
}
