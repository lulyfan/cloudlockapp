package com.ut.base.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.ut.base.R;
import com.ut.base.activity.GrantPermissionActivity;
import com.ut.base.databinding.FragmentOnceBinding;
import com.ut.base.viewModel.GrantPermisssionViewModel;

public class OnceFragment extends Fragment {

    private FragmentOnceBinding binding;
    private GrantPermisssionViewModel viewModel;

    public OnceFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_once, container, false);
        binding = DataBindingUtil.bind(view);
        viewModel = ViewModelProviders.of(getActivity()).get(GrantPermisssionViewModel.class);
        initUI(view);

        return view;
    }

    private void initUI(View view) {
        ImageView iv_contact = binding.getRoot().findViewById(R.id.contact);
        iv_contact.setOnClickListener(v -> ((GrantPermissionActivity)getActivity()).selectContact());

        EditText et_phoneNum = view.findViewById(R.id.et_phoneNum);
        EditText et_name = view.findViewById(R.id.et_receiverName);
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
}
