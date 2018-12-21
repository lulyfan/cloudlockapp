package com.ut.base.fragment;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.ut.base.R;
import com.ut.base.activity.GrantPermissionActivity;
import com.ut.base.databinding.FragmentOnceBinding;

public class OnceFragment extends Fragment {

    private FragmentOnceBinding binding;

    public OnceFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_once, container, false);
        binding = DataBindingUtil.bind(view);
        ImageView iv_contact = binding.getRoot().findViewById(R.id.contact);
        iv_contact.setOnClickListener(v -> ((GrantPermissionActivity)getActivity()).selectContact());

        EditText et_phoneNum = view.findViewById(R.id.et_phoneNum);
        EditText et_name = view.findViewById(R.id.et_receiverName);
        ((GrantPermissionActivity)getActivity()).receiverPhoneNum.observe(this, s -> et_phoneNum.setText(s));
        ((GrantPermissionActivity)getActivity()).receiverName.observe(this, s -> et_name.setText(s));
        return view;
    }


}
