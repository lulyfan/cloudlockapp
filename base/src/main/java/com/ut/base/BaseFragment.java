package com.ut.base;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ut.base.Utils.UTLog;

/**
 * author : zhouyubin
 * time   : 2018/11/20
 * desc   :
 * version: 1.0
 */
public class BaseFragment extends Fragment {
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        UTLog.i(this.getClass().getSimpleName() + ":onAttach");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UTLog.i(this.getClass().getSimpleName() + ":onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        UTLog.i(this.getClass().getSimpleName() + ":onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        UTLog.i(this.getClass().getSimpleName() + ":onActivityCreated");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        UTLog.i(this.getClass().getSimpleName() + ":onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        UTLog.i(this.getClass().getSimpleName() + ":onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        UTLog.i(this.getClass().getSimpleName() + ":onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        UTLog.i(this.getClass().getSimpleName() + ":onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        UTLog.i(this.getClass().getSimpleName() + ":onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        UTLog.i(this.getClass().getSimpleName() + ":onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        UTLog.i(this.getClass().getSimpleName() + ":onDetach");
    }
}
