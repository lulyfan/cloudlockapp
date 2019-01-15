package com.ut.base.fragment;


import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;

import com.ut.base.BaseFragment;
import com.ut.base.R;
import com.ut.base.activity.GrantPermissionActivity;
import com.ut.base.databinding.FragmentForeverBinding;
import com.ut.base.viewModel.GrantPermisssionViewModel;
import com.ut.database.entity.EnumCollection;


/**
 * A simple {@link Fragment} subclass.
 */
public class ForeverFragment extends BaseFragment {

    private FragmentForeverBinding binding;
    private GrantPermisssionViewModel viewModel;

    public ForeverFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(binding == null) {
            View view = inflater.inflate(R.layout.fragment_forever, container, false);
            binding = DataBindingUtil.bind(view);
            viewModel = ViewModelProviders.of(getActivity()).get(GrantPermisssionViewModel.class);
            initUI();
        }
        return binding.getRoot();
    }

    private void initUI() {
        if (binding == null) return;
        View view = binding.getRoot();
        view.findViewById(R.id.contact).setOnClickListener(v -> ((GrantPermissionActivity) getActivity()).selectContact());

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

        binding.swIsAdmin.setOnCheckedChangeListener((buttonView, isChecked) -> viewModel.isAdmin = isChecked);
        if (viewModel.userType != EnumCollection.UserType.ADMIN.ordinal()) {
            binding.swIsAdmin.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onUserVisible() {
        super.onUserVisible();
    }
}
