package com.ut.module_lock.adapter;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.ut.base.Utils.TxtUtils;
import com.ut.base.Utils.UTLog;
import com.ut.database.entity.User;
import com.ut.module_lock.R;
import com.ut.module_lock.databinding.ItemLockListBinding;
import com.ut.module_lock.databinding.ItemLockListEmptyBinding;
import com.ut.module_lock.entity.LockKey;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZYB on 2017-03-24.
 */

public class LockListAdapter<T> extends RecyclerView.Adapter<LockListAdapter.LockKeyViewHolder> implements View.OnClickListener {
    private LayoutInflater mLayoutInflater = null;
    private List<LockKey> datas = new ArrayList<>();
    private OnRcvItemClickListener mOnRcvItemClickListener = null;
    private User mUser = null;
    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_EMPTY = 1;

    public LockListAdapter(Context context, List<LockKey> datas, User user) {
        mLayoutInflater = LayoutInflater.from(context);
        mUser = user;
        if (datas != null) {
            this.datas.addAll(datas);
        }
    }

    public void notifyData(List<LockKey> datas) {
        if (datas != null) {
            this.datas.clear();
            this.datas.addAll(datas);
        }
        this.datas = datas;
        notifyDataSetChanged();
    }


    public void setOnRcvItemClickListener(OnRcvItemClickListener listener) {
        this.mOnRcvItemClickListener = listener;
    }

    @Override
    public LockKeyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == VIEW_TYPE_ITEM) {
            view = mLayoutInflater.inflate(R.layout.item_lock_list, parent, false);
            view.setOnClickListener(this);
        } else {
            view = mLayoutInflater.inflate(R.layout.item_lock_list_empty, parent, false);
        }
        return new LockKeyViewHolder(view, viewType);

    }

    @Override
    public void onBindViewHolder(LockKeyViewHolder holder, int position) {
        if (holder.viewType == VIEW_TYPE_ITEM) {
            LockKey lockKey = datas.get(position);
            lockKey.init();
            holder.bind(lockKey);
            holder.mView.setTag(position);
        } else {
            holder.bindEmpty(mUser);
        }
    }

    @Override
    public int getItemCount() {
        if (this.datas.size() == 0) {
            return 1;
        }
        return this.datas.size();
    }


    @Override
    public int getItemViewType(int position) {
        if (this.datas.size() > 0) {
            return VIEW_TYPE_ITEM;
        } else {
            return VIEW_TYPE_EMPTY;
        }
    }

//    @Override
//    public boolean onTouch(View v, MotionEvent event) {
//        if (event.getAction() == MotionEvent.ACTION_UP && mOnRcvItemClickListener != null) {
//            mOnRcvItemClickListener.onItemClick(v, datas, (Integer) v.getTag());
//            return true;
//        }
//        return false;
//    }


    @BindingAdapter("imgSrc")
    public static void loadImage(ImageView imageView, int userType) {
        if (userType == 0) {
            imageView.setImageResource(R.mipmap.icon_user_manager);
        } else if (userType == 1) {
            imageView.setImageResource(R.mipmap.icon_user_auth);
        } else {
            imageView.setImageResource(R.mipmap.icon_user_normal);
        }
    }

    @BindingAdapter("encryptText")
    public static void loadText(TextView textView, String account) {
        textView.setText(TxtUtils.toEncryptAccount(account));
    }

    @Override
    public void onClick(View v) {
        mOnRcvItemClickListener.onItemClick(v, datas, (Integer) v.getTag());
    }

    public static class LockKeyViewHolder extends RecyclerView.ViewHolder {
        private ItemLockListBinding mBinding;
        private ItemLockListEmptyBinding mEmptyBinding;
        public View mView = null;
        public int viewType = 0;

        public LockKeyViewHolder(View itemView, int viewType) {
            super(itemView);
            mView = itemView;
            this.viewType = viewType;
            if (viewType == VIEW_TYPE_ITEM) {
                mBinding = DataBindingUtil.bind(itemView);
            } else {
                mEmptyBinding = DataBindingUtil.bind(itemView);
            }
        }

        public void bind(LockKey lockKey) {
            mBinding.setLockKey(lockKey);
        }

        public void bindEmpty(User user) {
            mEmptyBinding.setUser(user);
        }
    }

    private void log(String msg) {
        UTLog.i(LockListAdapter.class.getSimpleName(), msg);
    }
}
