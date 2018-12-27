package com.ut.commoncomponent;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * author : zhouyubin
 * time   : 2018/12/13
 * desc   :
 * version: 1.0
 */
public class Flowlayout extends ViewGroup {
    public Flowlayout(Context context) {
        super(context);
    }

    public Flowlayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Flowlayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public Flowlayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width = 0;
        int height = 0;

        int lineWidth = 0;
        int lineHeight = 0;

        int childCount = getChildCount();

        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);

            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;

            if (lineWidth + childWidth <= widthSize) {
                lineWidth += childWidth;
                lineHeight = Math.max(lineHeight, childHeight);
            } else {//下一行
                width = Math.max(lineWidth, childWidth);
                height += lineHeight;//加上上一行行高
                lineHeight = childHeight;//下一行初始行高
                lineWidth = childWidth;
            }
            if (i == childCount - 1) {
                width = Math.max(width, lineWidth);
                height += lineHeight;
            }
        }
        int finalWidth = (widthMode == MeasureSpec.EXACTLY) ? widthSize : width;
        int finalHeight = (heightMode == MeasureSpec.EXACTLY) ? heightSize : height;
        setMeasuredDimension(finalWidth, finalHeight);
    }

    List<Integer> lineHeights = new ArrayList<>();
    List<List<View>> listLineViews = new ArrayList<>();

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        lineHeights.clear();
        listLineViews.clear();
        initLineHeightsAndListLineViews(lineHeights, listLineViews);
        int lineNums = listLineViews.size();
        int top = 0;
        for (int i = 0; i < lineNums; i++) {
            List<View> lineViews = listLineViews.get(i);
            int lineHeight = lineHeights.get(i);

            int lineChildCounts = lineViews.size();
            int left = 0;
            for (int j = 0; j < lineChildCounts; j++) {
                View child = lineViews.get(j);
                if (child.getVisibility() == View.GONE) {
                    continue;
                }
                MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
                int cl = left + lp.leftMargin;
                int cr = cl + child.getMeasuredWidth();
                int ct = top + lp.topMargin;
                int cb = ct + child.getMeasuredHeight();


                child.layout(cl, ct, cr, cb);

                left = cr + lp.rightMargin;
            }
            top += lineHeight;
        }
    }

    private void initLineHeightsAndListLineViews(List<Integer> lineHeights, List<List<View>> listLineViews) {
        int childCount = getChildCount();
        int lineWidth = 0;
        int lineHeight = 0;
        int parentWidth = getWidth();
        List<View> lineViews = new ArrayList<>();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            int childAllWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            int childAllHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            if (lineWidth + childAllWidth <= parentWidth) {
                lineWidth += childAllWidth;
                lineHeight = Math.max(lineHeight, childAllHeight);
            } else {
                lineHeights.add(lineHeight);
                listLineViews.add(lineViews);
                lineViews = new ArrayList<>();
                lineWidth = childAllWidth;
                lineHeight = childAllHeight;
            }
            lineViews.add(child);
            if (i == childCount - 1) {
                lineHeights.add(lineHeight);
                listLineViews.add(lineViews);
            }
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }
}
