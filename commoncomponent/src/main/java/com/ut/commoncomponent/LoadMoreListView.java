package com.ut.commoncomponent;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * Created by RaphetS on 2016/10/14.
 */

public class LoadMoreListView extends ListView implements AbsListView.OnScrollListener {
    private Context mContext;
    private View mFootView;
    private int mTotalItemCount;
    private OnLoadMoreListener mLoadMoreListener;
    private boolean mIsLoading = false;

    public LoadMoreListView(Context context) {
        super(context);
        init(context);
    }

    public LoadMoreListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public LoadMoreListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private int footViewHeight = 0;
    private void init(Context context) {
        this.mContext = context;
        mFootView = LayoutInflater.from(context).inflate(R.layout.view_loading, null);
        mFootView.measure(0,0);
        footViewHeight = mFootView.getMeasuredHeight();
        hideFootView();
        addFooterView(mFootView);
        setOnScrollListener(this);
    }



    @Override
    public void onScrollStateChanged(AbsListView listView, int scrollState) {
        // 滑到底部后自动加载，判断listview已经停止滚动并且最后可视的条目等于adapter的条目
        int lastVisibleIndex = listView.getLastVisiblePosition();
        View lastView = getChildAt(lastVisibleIndex);
        if (!mIsLoading && scrollState == OnScrollListener.SCROLL_STATE_IDLE
                && lastVisibleIndex == mTotalItemCount - 1 &&  lastView != null && lastView.getBottom() == getBottom()) {
            mIsLoading = true;
            showFootView();
            setSelection(getCount() - 1);
            if (mLoadMoreListener != null) {
                mLoadMoreListener.onloadMore();
            }
        }
    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        mTotalItemCount = totalItemCount;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        mLoadMoreListener = listener;
    }


    public interface OnLoadMoreListener {
        void onloadMore();
    }

    public void setLoadCompleted() {
        mIsLoading = false;
        hideFootView();
    }

    public boolean isLoading() {
        return mIsLoading;
    }

    private void hideFootView(){
        mFootView.setPadding(0, -footViewHeight, 0 , 0);
    }

    private void showFootView(){
        mFootView.setPadding(0, 0, 0 , 0);
    }
}