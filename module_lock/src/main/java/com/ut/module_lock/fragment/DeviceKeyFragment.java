package com.ut.module_lock.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ut.module_lock.R;

public class DeviceKeyFragment extends Fragment {
    private static final String EXTRA_KEY_TYPE = "extra_key_type";
    private static final String KEY_TYPE_FINGER = "key_type_finger";

    public DeviceKeyFragment() {
    }

    public static DeviceKeyFragment newInstance(int sectionNumber) {
        DeviceKeyFragment fragment = new DeviceKeyFragment();
        Bundle args = new Bundle();
        args.putInt(EXTRA_KEY_TYPE, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_device_key, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        textView.setText(getString(R.string.section_format, getArguments().getInt(EXTRA_KEY_TYPE)));
        return rootView;
    }
}