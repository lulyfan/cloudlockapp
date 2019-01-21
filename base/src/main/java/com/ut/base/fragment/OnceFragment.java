package com.ut.base.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.ut.base.BaseFragment;
import com.ut.base.R;
import com.ut.base.activity.SendKeyActivity;
import com.ut.base.databinding.FragmentOnceBinding;
import com.ut.base.viewModel.SendKeyViewModel;

public class OnceFragment extends BaseFragment {

    private FragmentOnceBinding binding;
    private SendKeyViewModel viewModel;

    public OnceFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (binding == null) {
            View view = inflater.inflate(R.layout.fragment_once, container, false);
            binding = DataBindingUtil.bind(view);
            viewModel = ViewModelProviders.of(getActivity()).get(SendKeyViewModel.class);
            initUI();
        }
        return binding.getRoot();
    }

    private void initUI() {
        if (binding == null) return;
        View view = binding.getRoot();
        ImageView iv_contact = binding.getRoot().findViewById(R.id.contact);
        iv_contact.setOnClickListener(v -> ((SendKeyActivity) getActivity()).selectContact());

        EditText et_phoneNum = view.findViewById(R.id.et_phoneNum);
        EditText et_name = view.findViewById(R.id.et_receiverName);
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

    @Override
    protected void onUserVisible() {
        super.onUserVisible();
    }
}
