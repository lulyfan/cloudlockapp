package com.ut.module_mine;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * 数据绑定适配器，结合RecyclerView和DataBinging一起使用
 * @param <T>数据类型
 * @param <V>ViewDataBinding类型
 */
public class DataBindingAdapter<T, V extends ViewDataBinding> extends RecyclerView.Adapter<DataBindingAdapter.BaseViewHolder>{

    private int layoutId;
    private Context context;
    private int variableId;
    private List<T> data;
    private V selectedBinding;

    /**
     *
     * @param context
     * @param layoutId RecyclerView Item 布局
     * @param variableId 布局中绑定的变量ID， 如果布局中变量ID为x, 则传入BR.x
     */
    public DataBindingAdapter(Context context, int layoutId, int variableId) {
        this.context = context;
        this.layoutId = layoutId;
        this.variableId = variableId;
    }

    public void setData(List<T> data) {
        this.data = data;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(context).inflate(layoutId, parent, false);
        final ViewDataBinding binding = DataBindingUtil.bind(root);

        if (initListener != null) {
            initListener.onInit(binding);
        }

        BaseViewHolder viewHolder = new BaseViewHolder(root, binding);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final BaseViewHolder holder, final int position) {
        holder.binding.setVariable(variableId, data.get(position));

        if (position == 0) {
            selectedBinding = (V) holder.binding;
            holder.itemView.setSelected(true);
        }

        if (bindListener != null) {
            bindListener.onBind(holder.binding, position);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                V oldSelectedBinding = selectedBinding;
                if (oldSelectedBinding != null) {
                    oldSelectedBinding.getRoot().setSelected(false);
                }
                holder.itemView.setSelected(true);

                if (onClickItemListener != null) {
                    onClickItemListener.onClick(holder.binding, position, oldSelectedBinding);
                }
                selectedBinding = (V) holder.binding;
            }
        });
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public List<T> getData() {
        return data;
    }

    public T getItemData(int position) {
        T t = data == null ? null : data.get(position);
        return t;
    }

    public V getSelectedBinding() {
        return selectedBinding;
    }

    public void setInitListener(initListener<V> initListener) {
        this.initListener = initListener;
    }

    private initListener initListener;

    interface initListener<R> {
        void onInit(R binding);
    }

    public void setBindListener(BindListener<V> bindListener) {
        this.bindListener = bindListener;
    }

    private BindListener bindListener;

    interface BindListener<S> {
        void onBind(S binding, int position);
    }

    public void setOnClickItemListener(OnClickItemListener<V> onClickItemListener) {
        this.onClickItemListener = onClickItemListener;
    }

    private OnClickItemListener onClickItemListener;

    public interface OnClickItemListener<U> {
        void onClick(U selectedbinding, int position, U lastSelectedBinding);
    }

    static class BaseViewHolder extends RecyclerView.ViewHolder {

        ViewDataBinding binding;

        public BaseViewHolder(View itemView, ViewDataBinding binding) {
            super(itemView);
            this.binding = binding;
        }
    }

}
