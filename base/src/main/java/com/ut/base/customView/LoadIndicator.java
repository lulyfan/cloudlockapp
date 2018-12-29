package com.ut.base.customView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import com.ut.base.R;

public class LoadIndicator extends View {
    public LoadIndicator(Context context) {
        this(context, null);
    }

    private int startColor = Color.argb(255, 255, 255, 255);
    private float strokeWidth = 0;
    private int startAngle = 0;

    public LoadIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LoadIndicator);
        for (int i = 0; i < typedArray.getIndexCount(); i++) {
            int i1 = typedArray.getIndex(i);
            if (i1 == R.styleable.LoadIndicator_startColor) {
                startColor = typedArray.getColor(R.styleable.LoadIndicator_startColor, startColor);

            } else if (i1 == R.styleable.LoadIndicator_startAngle) {
                startAngle = typedArray.getInt(R.styleable.LoadIndicator_startAngle, startAngle);

            } else if (i1 == R.styleable.LoadIndicator_strokeWidth) {
                strokeWidth = typedArray.getDimension(R.styleable.LoadIndicator_strokeWidth, strokeWidth);

            }
        }
        typedArray.recycle();
        initialize();
    }

    private final int LineCount = 12;
    private final int MinAlpha = 0;
    private final int AngleGradient = 360 / LineCount;
    private Paint paint;
    private int[] colors = new int[LineCount];

    private void initialize() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        int alpha = Color.alpha(startColor);
        int red = Color.red(startColor);
        int green = Color.green(startColor);
        int blue = Color.blue(startColor);
        int alpha_gradient = Math.abs(alpha - MinAlpha) / LineCount;
        for (int i = 0; i < colors.length; i++) {
            colors[i] = Color.argb(alpha - alpha_gradient * i, red, green, blue);
        }
        paint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        float radius = Math.min(getWidth() - getPaddingLeft() - getPaddingRight(), getHeight() - getPaddingTop() - getPaddingBottom()) * 0.5f;
        if (strokeWidth == 0) strokeWidth = pointX(AngleGradient / 2, radius / 2) / 2;
        paint.setStrokeWidth(strokeWidth);

        for (int i = 0; i < colors.length; i++) {
            paint.setColor(colors[i]);
            canvas.drawLine(
                    centerX + pointX(-AngleGradient * i + startAngle, radius / 2),
                    centerY + pointY(-AngleGradient * i + startAngle, radius / 2),
                    centerX + pointX(-AngleGradient * i + startAngle, radius - strokeWidth / 2),   //  这里计算Y值时, 之所以减去线宽/2, 是防止没有设置的Padding时,图像会超出View范围
                    centerY + pointY(-AngleGradient * i + startAngle, radius - strokeWidth / 2),   //  这里计算Y值时, 之所以减去线宽/2, 是防止没有设置的Padding时,图像会超出View范围
                    paint);
        }

        startAngle += AngleGradient;
        postInvalidateDelayed(100);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    private float pointX(int angle, float radius) {
        return (float) (radius * Math.cos(angle * Math.PI / 180));
    }

    private float pointY(int angle, float radius) {
        return (float) (radius * Math.sin(angle * Math.PI / 180));
    }

    private Handler animHandler = new Handler();
    private Runnable animRunnable = new Runnable() {
        @Override
        public void run() {
            startAngle += AngleGradient;
            invalidate();
            animHandler.postDelayed(animRunnable, 50);
        }
    };

    public void start() {
        animHandler.post(animRunnable);
    }

    public void stop() {
        animHandler.removeCallbacks(animRunnable);
    }

    public void setStartColor(int startColor) {
        this.startColor = startColor;
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public void setStartAngle(int startAngle) {
        this.startAngle = startAngle;
    }
}
