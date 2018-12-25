package com.ut.module_lock.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.ut.base.BaseApplication;
import com.ut.base.BaseFragment;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.base.Utils.UTLog;
import com.ut.base.Utils.Util;
import com.ut.base.common.CommonAdapter;
import com.ut.base.common.CommonPopupWindow;
import com.ut.base.common.CommonViewHolder;
import com.ut.database.entity.EnumCollection;
import com.ut.database.entity.LockGroup;
import com.ut.database.entity.LockKey;
import com.ut.module_lock.R;
import com.ut.module_lock.activity.AddGuideActivity;
import com.ut.module_lock.activity.LockDetailActivity;
import com.ut.module_lock.activity.SearchLockActivity;
import com.ut.module_lock.adapter.LockListAdapter;
import com.ut.module_lock.adapter.OnRcvItemClickListener;
import com.ut.module_lock.databinding.FragmentLocklistBinding;
import com.ut.module_lock.viewmodel.LockListFragVM;

import java.util.ArrayList;
import java.util.List;

/**
 * author : zhouyubin
 * time   : 2018/11/20
 * desc   :
 * version: 1.0
 */
@Route(path = RouterUtil.LockModulePath.Fragment_Lock)
public class LockListFragment extends BaseFragment {
    private View mView = null;
    private FragmentLocklistBinding mFragmentLocklistBinding = null;
    private LockListAdapter mLockListAdapter = null;
    private LockListFragVM mLockListFragVM = null;
    private CommonAdapter<LockGroup> mLockGroupCommonAdapter = null;
    private CommonPopupWindow popupWindow = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mFragmentLocklistBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_locklist, container, false);
            mView = mFragmentLocklistBinding.getRoot();
        }
        return mView;
    }

    private void addPaddingTop() {
        View view = getView().findViewById(R.id.parent);
        view.setPadding(view.getPaddingLeft(), Util.getStatusBarHeight(getContext()), view.getPaddingRight(), view.getPaddingBottom());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        addPaddingTop();
        mFragmentLocklistBinding.setPresenter(new Present());
        initRecycleView();
        initPopupWindow();
        initViewModel();

    }

    private void initViewModel() {
        mLockListFragVM = ViewModelProviders.of(this).get(LockListFragVM.class);
        mLockListFragVM.getLockList().observe(this, lockKeys -> {
            refreshLockListData(lockKeys);
        });
        mLockListFragVM.getLockGroupList().observe(this, lockGroups -> {
            refreshGroupList(lockGroups);
        });
        mLockListFragVM.toGetLockAllList();
        mLockListFragVM.toGetAllGroupList();
    }

    private void initRecycleView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mFragmentLocklistBinding.lockRvLock.setLayoutManager(linearLayoutManager);
    }

    public void refreshLockListData(List<LockKey> datas) {
        if (mLockListAdapter == null) {
            mLockListAdapter = new LockListAdapter(getContext(), datas, BaseApplication.getUser());
            mFragmentLocklistBinding.lockRvLock.setAdapter(mLockListAdapter);
            mLockListAdapter.setOnRcvItemClickListener(new OnRcvItemClickListener() {
                @Override
                public void onItemClick(View view, List<?> datas, int position) {
                    LockKey lockKey = (LockKey) datas.get(position);
                    if (lockKey.getKeyStatus() == EnumCollection.KeyStatus.NORMAL.ordinal()) {
                        Intent intent = new Intent(getContext(), LockDetailActivity.class);
                        intent.putExtra(RouterUtil.LockModuleExtraKey.Extra_lock_detail, (Parcelable) datas.get(position));
                        startActivity(intent);
                    }
                }
            });
        } else {
            mLockListAdapter.notifyData(datas);
        }
    }

    private LockGroup allGroup = new LockGroup();
    int currentGroupPosition = -1;

    private void refreshGroupList(List<LockGroup> lockGroups) {
        if (popupWindow == null || lockGroups == null) return;
        if (mLockGroupCommonAdapter == null) {
            allGroup.setId(-1);
            allGroup.setCurrent(1);
            allGroup.setName(getString(R.string.lock_group_all));
            lockGroups.add(0, allGroup);
            ListView listView = popupWindow.getView(R.id.lv_group_list);

            mLockGroupCommonAdapter = new CommonAdapter<LockGroup>(getContext(), lockGroups, R.layout.item_goup_list) {
                @Override
                public void convert(CommonViewHolder commonViewHolder, int position, LockGroup item) {
                    ((TextView) commonViewHolder.getView(R.id.tv_group_name)).setText(item.getName());
                    if (item.getCurrent() == 1) {
                        currentGroupPosition = position;
                        ((TextView) commonViewHolder.getView(R.id.tv_group_name))
                                .setTextColor(getResources().getColor(R.color.color_tv_blue));
                        commonViewHolder.getView(R.id.iBtn_right_arrow).setVisibility(View.VISIBLE);
                    } else {
                        ((TextView) commonViewHolder.getView(R.id.tv_group_name))
                                .setTextColor(getResources().getColor(R.color.lock_color_tv_gray));
                        commonViewHolder.getView(R.id.iBtn_right_arrow).setVisibility(View.GONE);
                    }
                }
            };
            listView.setOnItemClickListener((parent, view, position, id) -> {
                LockGroup lockGroup = (LockGroup) parent.getAdapter().getItem(position);
                lockGroup.setCurrent(1);
                String currentGroupName = lockGroup.getName();
                ((TextView) popupWindow.getView(R.id.lock_tv_group)).setText(currentGroupName);
                mFragmentLocklistBinding.lockTvGroup.setText(currentGroupName);
                ((LockGroup) parent.getAdapter().getItem(currentGroupPosition)).setCurrent(0);
                mLockListFragVM.getGroupLockList(lockGroup).observe(LockListFragment.this,
                        LockListFragment.this::refreshLockListData);
                popupWindow.getPopupWindow().dismiss();
            });
            listView.setAdapter(mLockGroupCommonAdapter);
        } else {
            lockGroups.add(0, allGroup);
            mLockGroupCommonAdapter.notifyData(lockGroups);
        }

    }

    private void initPopupWindow() {
        popupWindow = new CommonPopupWindow(getContext(), R.layout.activity_lock_group_list,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT) {
            @Override
            protected void initView() {
            }

            @Override
            protected void initWindow() {
                super.initWindow();
                getPopupWindow().setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        setWindowAlpha(1.0f);
                        setDarkStatusBarFont();
                    }
                });
            }
        };
    }

    public class Present {
        public void onGroupClick(View view) {
            UTLog.i("onGroupClick");
            setLightStatusBarFont();
            setWindowAlpha(0.5f);
            popupWindow.showAtLocationWithAnim(getView().getRootView(), Gravity.TOP, 0, 0, R.style.animTranslate);
        }

        public void onSearchClick(View view) {
            UTLog.i("onSearchClick");
            Intent intent = new Intent(getContext(), SearchLockActivity.class);
            Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                    mFragmentLocklistBinding.lockTvSearch, getString(R.string.lock_share_string_search)).toBundle();
            getActivity().startActivity(intent, bundle);
        }

        public void onAddClick(View view) {
            getActivity().startActivity(new Intent(getContext(), AddGuideActivity.class));
            UTLog.i("onAddClick");
        }
    }

    private void setWindowAlpha(float alpha) {
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = alpha;
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getActivity().getWindow().setAttributes(lp);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mLockListAdapter = null;
        mLockGroupCommonAdapter = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mLockListFragVM != null) {
            mLockListFragVM.toGetLockAllList();
        }
    }
}
