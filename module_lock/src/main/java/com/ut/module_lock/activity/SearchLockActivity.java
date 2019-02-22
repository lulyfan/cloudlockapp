package com.ut.module_lock.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.ut.base.BaseActivity;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.base.UIUtils.SystemUtils;
import com.ut.base.Utils.PreferenceUtil;
import com.ut.base.common.CommonAdapter;
import com.ut.base.common.CommonViewHolder;
import com.ut.database.entity.LockKey;
import com.ut.module_lock.R;
import com.ut.module_lock.databinding.ActivitySearchLockBinding;
import com.ut.module_lock.viewmodel.SearchLockVM;

import java.util.ArrayList;
import java.util.List;

public class SearchLockActivity extends BaseActivity {

    private static final String EXTRA_SEARCH_DATA = "extra_search_data";

    private ActivitySearchLockBinding mBinding = null;
    private CommonAdapter<LockKey> mLockKeyCommonAdapter = null;
    private SearchLockVM mSearchLockVM = null;
    private long currentGroupId = 0;
    private List<String> mSearchRecords = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_search_lock);
        initTitle();
        initView();
        initViewModel();
        initData();
    }

    private void initData() {
        currentGroupId = getIntent().getLongExtra(RouterUtil.LockModuleExtraKey.EXTRA_LOCK_CURRENT_GROUPID, 0);

        String json = PreferenceUtil.getInstance(getBaseContext()).getString(EXTRA_SEARCH_DATA);
        if (!TextUtils.isEmpty(json)) {
            mSearchRecords = JSON.parseArray(json, String.class);
            mSearchRecords = mSearchRecords.subList(0, mSearchRecords.size() > 9 ? 10 : mSearchRecords.size());
            mSearchLockVM.getSearchRecords().postValue(mSearchRecords);
        }
    }

    private void initViewModel() {
        mSearchLockVM = ViewModelProviders.of(this).get(SearchLockVM.class);
        mSearchLockVM.getSearchRecords().observe(this, searchRecords -> {
            assert searchRecords != null;
            searchRecords = searchRecords.subList(0, searchRecords.size() > 9 ? 10 : searchRecords.size());
            refreshSearchRecord(searchRecords);
        });

    }

    private void initView() {
        mBinding.lvSearchLock.setEmptyView(mBinding.llySerarchRecord);
        mBinding.edtSearchWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    refreshLockListData(new ArrayList<>());
                } else {
                    mSearchLockVM.getLockKeys(s.toString(), currentGroupId).observe(SearchLockActivity.this, lockKeys -> {
                        refreshLockListData(lockKeys);
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                mSearchRecords.add(0, s.toString());
                mSearchLockVM.getSearchRecords().postValue(mSearchRecords);
                PreferenceUtil.getInstance(getBaseContext()).setString(EXTRA_SEARCH_DATA, JSON.toJSONString(mSearchRecords));
            }
        });
        mBinding.lvSearchLock.setOnItemClickListener((parent, view, position, id) -> {
            LockKey lockKey = (LockKey) parent.getAdapter().getItem(position);
            boolean ifHad = false;
            for (String record : mSearchRecords) {
                if (record.equals(lockKey.getName())) {
                    ifHad = true;
                    break;
                }
            }
            if (!ifHad) {
                mSearchRecords.add(lockKey.getName());
                mSearchLockVM.getSearchRecords().postValue(mSearchRecords);
                PreferenceUtil.getInstance(getBaseContext()).setString(EXTRA_SEARCH_DATA, JSON.toJSONString(mSearchRecords));
            }
            ARouter.getInstance().build(RouterUtil.LockModulePath.LOCK_DETAIL)
                    .withParcelable(RouterUtil.LockModuleExtraKey.EXTRA_LOCK_KEY, lockKey)
                    .navigation();
        });
    }

    ViewGroup.MarginLayoutParams searchWordlp = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

    private void refreshSearchRecord(List<String> records) {
        searchWordlp.rightMargin = SystemUtils.dp2px(getBaseContext(), 8);
        searchWordlp.bottomMargin = SystemUtils.dp2px(getBaseContext(), 4);
        mBinding.flySearchHistory.removeAllViews();
        for (String record : records) {
            TextView view = new TextView(this);
            view.setText(record);
            view.setTag(record);
            view.setTextColor(getResources().getColor(R.color.gray3));
            view.setPadding(20, 8, 20, 8);
            view.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_serarch_word));
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBinding.edtSearchWord.setText((String) v.getTag());
                }
            });
            mBinding.flySearchHistory.addView(view, searchWordlp);
        }
    }

    private void refreshLockListData(List<LockKey> lockKeys) {
        if (mLockKeyCommonAdapter == null) {
            mLockKeyCommonAdapter = new CommonAdapter<LockKey>(this, lockKeys, R.layout.item_history_list) {
                @Override
                public void convert(CommonViewHolder commonViewHolder, int position, LockKey item) {
                    ((TextView) commonViewHolder.getView(R.id.tv_lock_name)).setText(item.getName());
                }
            };
            mBinding.lvSearchLock.setAdapter(mLockKeyCommonAdapter);
        } else {
            mLockKeyCommonAdapter.notifyData(lockKeys);
        }
    }

    private void initTitle() {
        enableImmersive(R.color.white, true);
    }

    @Override
    public void onXmlClick(View view) {
        super.onXmlClick(view);
        int i = view.getId();
        if (i == R.id.tv_cancel) {
            onBackPressed();
        }
    }
}
