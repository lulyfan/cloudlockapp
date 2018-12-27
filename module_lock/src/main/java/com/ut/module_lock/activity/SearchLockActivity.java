package com.ut.module_lock.activity;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.ut.base.BaseActivity;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.base.common.CommonAdapter;
import com.ut.base.common.CommonViewHolder;
import com.ut.database.entity.LockKey;
import com.ut.database.entity.SearchRecord;
import com.ut.module_lock.R;
import com.ut.module_lock.databinding.ActivitySearchLockBinding;
import com.ut.module_lock.viewmodel.SearchLockVM;

import java.util.ArrayList;
import java.util.List;

public class SearchLockActivity extends BaseActivity {
    private ActivitySearchLockBinding mBinding = null;
    private CommonAdapter<LockKey> mLockKeyCommonAdapter = null;
    private SearchLockVM mSearchLockVM = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_search_lock);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_search_lock);
        initTitle();
        initView();
        initViewModel();
    }

    private void initViewModel() {
        mSearchLockVM = ViewModelProviders.of(this).get(SearchLockVM.class);
        mSearchLockVM.getSearchRecords().observe(this, searchRecords -> {
            assert searchRecords != null;
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
                    mSearchLockVM.getLockKeys(s.toString()).observe(SearchLockActivity.this, lockKeys -> {
                        refreshLockListData(lockKeys);
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mBinding.lvSearchLock.setOnItemClickListener((parent, view, position, id) -> {
            LockKey lockKey = (LockKey) parent.getAdapter().getItem(position);
            mSearchLockVM.insertSearchRecord(lockKey.getName());
            ARouter.getInstance().build(RouterUtil.LockModulePath.LOCK_DETAIL)
                    .withParcelable(RouterUtil.LockModuleExtraKey.Extra_lock_detail, lockKey)
                    .navigation();
            this.finish();
        });
    }

    ViewGroup.MarginLayoutParams searchWordlp = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

    private void refreshSearchRecord(List<SearchRecord> records) {
        mBinding.flySearchHistory.removeAllViews();
        for (SearchRecord record : records) {
            TextView view = new TextView(this);
            view.setText(record.getWord());
            view.setTag(record.getWord());
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
