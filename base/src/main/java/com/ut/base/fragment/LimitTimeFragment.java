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

import com.ut.base.R;
import com.ut.base.Utils.DialogUtil;
import com.ut.base.activity.GrantPermissionActivity;
import com.ut.base.databinding.FragmentLimitTimeBinding;
import com.ut.base.viewModel.GrantPermisssionViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class LimitTimeFragment extends Fragment {

    private FragmentLimitTimeBinding binding;
    private GrantPermisssionViewModel viewModel;
    public LimitTimeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_limit_time, container, false);
        viewModel = ViewModelProviders.of(getActivity()).get(GrantPermisssionViewModel.class);
        initUI();
        initData();

        return binding.getRoot();
    }

    private void initData() {
        String limitStartTime = viewModel.limitStartTime.getValue();
        if (limitStartTime != null && !"".equals(limitStartTime)) {
            binding.tvValidTime.setText(limitStartTime);
            binding.tvValidTime.setTextColor(getResources().getColor(R.color.gray3));
        }

        String limitEndTime = viewModel.limitEndTime.getValue();
        if (limitEndTime != null && !"".equals(limitEndTime)) {
            binding.tvInvalidTime.setText(limitEndTime);
            binding.tvInvalidTime.setTextColor(getResources().getColor(R.color.gray3));
        }
    }

    private void initUI() {
        binding.tvValidTime.setOnClickListener(v -> chooseTime(v, getString(R.string.validTime)));

        binding.tvInvalidTime.setOnClickListener(v -> chooseTime(v, getString(R.string.invalidTime)));

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
    }


    private void chooseTime(View v, String title) {
        DialogUtil.chooseDateTime(getContext(), title, (year, month, day, hour, minute) -> {
            TextView textView = (TextView) v;
            textView.setText(year + "/" + String.format("%02d", month) + "/" + String.format("%02d", day) + " " + hour + ":" + minute);
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

}
