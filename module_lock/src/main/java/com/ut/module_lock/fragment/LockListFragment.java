package com.ut.module_lock.fragment;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.ut.base.BaseFragment;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.base.Utils.UTLog;
import com.ut.base.Utils.Util;
import com.ut.base.common.CommonAdapter;
import com.ut.base.common.CommonPopupWindow;
import com.ut.base.common.CommonViewHolder;
import com.ut.database.entity.User;
import com.ut.module_lock.R;
import com.ut.module_lock.activity.AddGuideActivity;
import com.ut.module_lock.activity.LockDetailActivity;
import com.ut.module_lock.adapter.LockListAdapter;
import com.ut.module_lock.adapter.OnRcvItemClickListener;
import com.ut.module_lock.databinding.*;
import com.ut.module_lock.entity.LockGroup;
import com.ut.module_lock.entity.LockKey;

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
        //TODO 测试数据
        List<LockKey> lockKeys = new ArrayList<>();
        lockKeys.add(new LockKey("小锁Chan的锁/超过12位字符就", 0, 0, 0, 0, 80));
        lockKeys.add(new LockKey("我是【授权用户】的门锁", 2, 0, 0, 1, 39));
        lockKeys.add(new LockKey("我是【普通用户】的门锁", 3, 0, 1, 2, 10));
        User user = new User();
        user.setAccount("13534673711");
        //
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mFragmentLocklistBinding.lockRvLock.setLayoutManager(linearLayoutManager);
        mLockListAdapter = new LockListAdapter(getContext(), lockKeys, user);
        mLockListAdapter.setOnRcvItemClickListener(new OnRcvItemClickListener() {
            @Override
            public void onItemClick(View view, List<?> datas, int position) {
                Intent intent = new Intent(getContext(), LockDetailActivity.class);
                intent.putExtra(LockDetailActivity.EXTRA_LOCK_KEY, (Parcelable) datas.get(position));
                startActivity(intent);
            }
        });
        mFragmentLocklistBinding.lockRvLock.setAdapter(mLockListAdapter);
        mFragmentLocklistBinding.setPresenter(new Present());
    }

    public class Present {
        public void onGroupClick(View view) {
            UTLog.i("onGroupClick");
//            mLockListAdapter.notifyData(new ArrayList<>());

            List<LockGroup> list = new ArrayList<>();
            list.add(new LockGroup("全部分组", 0));
            list.add(new LockGroup("Chan的家", 1));
            list.add(new LockGroup("优特公司", 1));
            setLightStatusBarFont();
            setWindowAlpha(0.5f);
            CommonPopupWindow popupWindow = new CommonPopupWindow(getContext(), R.layout.activity_lock_group_list,
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT) {
                @Override
                protected void initView() {
                    ListView listView = getView(R.id.lv_group_list);
                    listView.setAdapter(new CommonAdapter<LockGroup>(getContext(), list, R.layout.item_goup_list) {
                        @Override
                        public void convert(CommonViewHolder commonViewHolder, int position, LockGroup item) {
                            ((TextView) commonViewHolder.getView(R.id.tv_group_name)).setText(item.getName());
                            if (item.getType() == 0) {
                                commonViewHolder.getView(R.id.tv_current_group).setVisibility(View.VISIBLE);
                                commonViewHolder.getView(R.id.line_group_item).setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }

                ;

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
            popupWindow.showAtLocationWithAnim(getView().getRootView(), Gravity.TOP, 0, 0, R.style.animTranslate);
        }

        public void onSearchClick(View view) {
            UTLog.i("onSearchClick");
            List<LockGroup> list = new ArrayList<>();
            mLockListAdapter.notifyData(list);
//            ARouter.getInstance().build(RouterUtil.LockModulePath.KEY_MANAGER).navigation();
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
}
