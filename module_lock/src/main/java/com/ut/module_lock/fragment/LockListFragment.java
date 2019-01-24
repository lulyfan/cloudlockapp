package com.ut.module_lock.fragment;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.ut.base.BaseApplication;
import com.ut.base.BaseFragment;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.base.UIUtils.SystemUtils;
import com.ut.base.Utils.UTLog;
import com.ut.base.Utils.Util;
import com.ut.base.common.CommonAdapter;
import com.ut.base.common.CommonPopupWindow;
import com.ut.base.common.CommonViewHolder;
import com.ut.commoncomponent.CLToast;
import com.ut.database.entity.EnumCollection;
import com.ut.database.entity.LockGroup;
import com.ut.database.entity.LockKey;
import com.ut.module_lock.AutoOpenLockService;
import com.ut.module_lock.R;
import com.ut.module_lock.activity.AddGuideActivity;
import com.ut.module_lock.activity.SearchLockActivity;
import com.ut.module_lock.adapter.LockListAdapter;
import com.ut.module_lock.databinding.FragmentLocklistBinding;
import com.ut.module_lock.viewmodel.LockListFragVM;
import com.ut.unilink.UnilinkManager;

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

    private AutoOpenLockService mService;
    private boolean requestPermissionFlag;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(getContext(), AutoOpenLockService.class);
        getContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mFragmentLocklistBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_locklist, container, false);
            mView = mFragmentLocklistBinding.getRoot();
            addPaddingTop(mView);
            mFragmentLocklistBinding.setPresenter(new Present());
            initView();
            initPopupWindow();
            initViewModel();
        }
        return mView;
    }

    private void addPaddingTop(View parent) {
        View view = parent.findViewById(R.id.parent);
        view.setPadding(view.getPaddingLeft(), Util.getStatusBarHeight(getContext()), view.getPaddingRight(), view.getPaddingBottom());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initViewModel() {
        mLockListFragVM = ViewModelProviders.of(this).get(LockListFragVM.class);
        mLockListFragVM.getLockList().observe(this, lockKeys -> {
            refreshLockListData(lockKeys);

            if (mService != null) {
                mService.setLockKeys(lockKeys);
            }
        });
        mLockListFragVM.getLockGroupList().observe(this, this::refreshGroupList);
        mLockListFragVM.getRefreshStatus().observe(this, isRefresh -> {
            if (mFragmentLocklistBinding.swfLockList.isRefreshing()) {
                mFragmentLocklistBinding.swfLockList.setRefreshing(false);
            }
            //TODO 提示刷新错误
//            if (isRefresh) {
//                CLToast.showAtBottom(getContext(), getString(R.string.refresh_success));
//            }
        });
    }

    private void initView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mFragmentLocklistBinding.lockRvLock.setLayoutManager(linearLayoutManager);
        mFragmentLocklistBinding.swfLockList.setColorSchemeResources(R.color.color_tv_blue);
        mFragmentLocklistBinding.swfLockList.setOnRefreshListener(() -> {
            mLockListFragVM.toGetLockAllList(true);
        });
    }

    public synchronized void refreshLockListData(List<LockKey> datas) {
        if (mLockListAdapter == null) {
            mLockListAdapter = new LockListAdapter(getContext(), datas, BaseApplication.getUser());
            mFragmentLocklistBinding.lockRvLock.setAdapter(mLockListAdapter);
            mLockListAdapter.setOnRcvItemClickListener((view, datas1, position) -> {
                LockKey lockKey = (LockKey) datas1.get(position);
                if (lockKey.getKeyStatus() == EnumCollection.KeyStatus.NORMAL.ordinal()) {
                    ARouter.getInstance()
                            .build(RouterUtil.LockModulePath.LOCK_DETAIL)
                            .withParcelable(RouterUtil.LockModuleExtraKey.EXTRA_LOCK_KEY, lockKey)
                            .navigation();

                    finish();
                } else if (lockKey.getKeyStatus() == EnumCollection.KeyStatus.HAS_INVALID.ordinal()){
                    CLToast.showAtCenter(getContext(), getString(R.string.lock_go_lock_detail_fail_has_invalid));
                } else if( lockKey.getKeyStatus() == EnumCollection.KeyStatus.HAS_FREEZE.ordinal()) {
                    CLToast.showAtCenter(getContext(), getString(R.string.lock_go_lock_detail_fail_has_freeze));
                } else if( lockKey.getKeyStatus() == EnumCollection.KeyStatus.HAS_OVERDUE.ordinal()) {
                    CLToast.showAtCenter(getContext(), getString(R.string.lock_go_lock_detail_fail_has_overdue));
                }
            });
        } else {
            mLockListAdapter.notifyData(datas);
        }
    }

    private LockGroup allGroup = new LockGroup();
    int currentGroupPosition = -1;

    private synchronized void refreshGroupList(List<LockGroup> lockGroups) {
        if (popupWindow == null || lockGroups == null) return;
        if (mLockGroupCommonAdapter == null) {
            allGroup.setId(-1);
            allGroup.setCurrent(1);
            allGroup.setName(getString(R.string.lock_group_all));
            lockGroups.add(0, allGroup);
            mFragmentLocklistBinding.lockTvGroup.setText(allGroup.getName());
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
                mLockListFragVM.getGroupLockList(lockGroup);
                popupWindow.getPopupWindow().dismiss();
            });
            listView.setAdapter(mLockGroupCommonAdapter);
        } else {
            lockGroups.add(0, allGroup);
            mLockGroupCommonAdapter.notifyData(lockGroups);
        }

    }

    private void initPopupWindow() {
        popupWindow = new CommonPopupWindow(getContext(), R.layout.popupwindow_lock_group_list,
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
                        SystemUtils.setWindowAlpha(getActivity(), 1.0f);
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
            popupWindow.showAtLocationWithAnim(getView().getRootView(), Gravity.TOP, 0, 0, R.style.animTranslate);
            SystemUtils.setWindowAlpha(getActivity(), 0.5f);
        }

        public void onSearchClick(View view) {
            UTLog.i("onSearchClick");
            Intent intent = new Intent(getContext(), SearchLockActivity.class);
//            Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
//                    mFragmentLocklistBinding.lockTvSearch, getString(R.string.lock_share_string_search)).toBundle();
//            getActivity().startActivity(intent, bundle);
            getActivity().startActivity(intent);

            finish();
        }

        public void onAddClick(View view) {
            getActivity().startActivity(new Intent(getContext(), AddGuideActivity.class));
            UTLog.i("onAddClick");

            finish();
        }
    }

    private void finish() {
        if (mService != null) {
            mService.stopAutoOpenLock();
        }
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
        if (mLockListFragVM != null) {
            mLockListFragVM.toGetLockAllList(false);
            mLockListFragVM.toGetAllGroupList(false);
        }

        autoOpenLock();
    }

    private void autoOpenLock() {
        if (!mLockListFragVM.isNeedAutoOpenLock()) {
            return;
        }

        if (!UnilinkManager.getInstance(getContext()).checkState() && !requestPermissionFlag) {
            requestPermissionFlag = true;
            UnilinkManager.getInstance(getContext()).enableBluetooth(getActivity(), 0);
            UnilinkManager.getInstance(getContext()).requestPermission(getActivity(), 1);
        }

        if (mService != null) {
            mService.startAutoOpenLock();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().unbindService(serviceConnection);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && mService != null) {
            autoOpenLock();
        }
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = ((AutoOpenLockService.LocalBinder)service).getService();
            mService.setLockKeys(mLockListFragVM.getLockList().getValue());
            autoOpenLock();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

}
