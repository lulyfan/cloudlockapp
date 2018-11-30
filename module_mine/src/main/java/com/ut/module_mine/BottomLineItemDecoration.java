package com.ut.module_mine;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class BottomLineItemDecoration extends RecyclerView.ItemDecoration {

    private Context context;
    private boolean isDrawLastLine;
    private int lastLineWidth;
    public final static int MATCH_ITEM = 0;
    public final static int MATCH_PARENT = 1;

    public BottomLineItemDecoration(Context context) {
        this(context, true, MATCH_ITEM);
    }

    public BottomLineItemDecoration(Context context, int lastLineWidth) {
        this(context, true, lastLineWidth);
    }

    /**
     *
     * @param context
     * @param isDrawLastLine 是否绘制最后一个item的下划线
     */
    public BottomLineItemDecoration(Context context, boolean isDrawLastLine, int lastLineWidth) {
        this.context = context;
        this.isDrawLastLine = isDrawLastLine;
        this.lastLineWidth = lastLineWidth;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {

        Paint paint = new Paint();
        paint.setColor(0xFFD2D2D2);
        paint.setStyle(Paint.Style.STROKE);
        int bottomLineWidth = dip2px(context, 1);
        paint.setStrokeWidth(bottomLineWidth);

        for (int i=0; i<parent.getChildCount(); i++) {

            if (i == parent.getChildCount() - 1 && !isDrawLastLine) {
                return;
            }

            View view = parent.getChildAt(i);
            int y = view.getBottom() - bottomLineWidth / 2;

            if (i == parent.getChildCount() - 1 && lastLineWidth == MATCH_PARENT) {
                c.drawLine(parent.getLeft(), y, parent.getRight(), y, paint);
                return;
            }

            c.drawLine(view.getLeft() + view.getPaddingLeft(), y, view.getRight() - view.getPaddingRight(), y, paint);
        }
    }

    /**
     * 根据手机分辨率从DP转成PX
     * @param context
     * @param dpValue
     * @return
     */
    public static int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
