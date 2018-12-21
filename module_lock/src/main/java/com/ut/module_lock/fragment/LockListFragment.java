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

import android.arch.lifecycle.Observer;

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
            mFragmentLocklistBinding.setPresenter(new Present());
            addPaddingTop();
            initRecycleView();
            initViewModel();
        }
        return mView;
    }

    private void addPaddingTop() {
        View view = mView.findViewById(R.id.parent);
        view.setPadding(view.getPaddingLeft(), Util.getStatusBarHeight(getContext()), view.getPaddingRight(), view.getPaddingBottom());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLockListFragVM.toGetLockList();
    }

    private void initViewModel() {
        mLockListFragVM = ViewModelProviders.of(this).get(LockListFragVM.class);
        mLockListFragVM.getLockList().observe(this, new Observer<List<LockKey>>() {
            @Override
            public void onChanged(@Nullable final List<LockKey> lockKeys) {
                refreshListData(lockKeys);
            }
        });
    }

    private void initRecycleView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mFragmentLocklistBinding.lockRvLock.setLayoutManager(linearLayoutManager);
    }

    public void refreshListData(List<LockKey> datas) {
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

    public class Present {
        public void onGroupClick(View view) {
            UTLog.i("onGroupClick");
            setLightStatusBarFont();
            if(popupWindow == null){
                popupWindow = new CommonPopupWindow(getContext(), R.layout.activity_lock_group_list,
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT) {
                    @Override
                    protected void initView() {
                        setWindowAlpha(0.5f);
                    }

                    private void setWindowAlpha(float alpha) {
                        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
                        lp.alpha = alpha;
                        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                        getActivity().getWindow().setAttributes(lp);
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
            popupWindow.showAtLocationWithAnim(getView().getRootView(), Gravity.TOP, 0, 0, R.style.animTranslate);
        }

        private LockGroup allGroup = new LockGroup();

        private void refreshGroupList(List<LockGroup> lockGroups) {
            if (popupWindow==null) return;
            if (mLockGroupCommonAdapter == null) {
                ListView listView = popupWindow.getView(R.id.lv_group_list);
                mLockGroupCommonAdapter = new CommonAdapter<LockGroup>(getContext(), lockGroups, R.layout.item_goup_list) {
                    @Override
                    public void convert(CommonViewHolder commonViewHolder, int position, LockGroup item) {
                        ((TextView) commonViewHolder.getView(R.id.tv_group_name)).setText(item.getName());
                        if (item.getCurrent() == 1) {
                            ((TextView) commonViewHolder.getView(R.id.tv_group_name))
                                    .setTextColor(getResources().getColor(R.color.color_tv_blue));
                            commonViewHolder.getView(R.id.iBtn_right_arrow).setVisibility(View.VISIBLE);
                        }
                    }
                };
                listView.setOnItemClickListener((parent,view,position,id)-> {
                    LockGroup lockGroup = (LockGroup) parent.getAdapter().getItem(position);


                });
                listView.setAdapter(mLockGroupCommonAdapter);
            } else {
                mLockGroupCommonAdapter.notifyData(lockGroups);
            }

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


}
