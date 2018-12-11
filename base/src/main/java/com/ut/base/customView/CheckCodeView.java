package com.ut.base.customView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ut.base.R;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class CheckCodeView extends FrameLayout {

    private final int width;
    private int checkCodeLength;
    private float defaultTextSize;
    private int defaultTextColor;
    private float selectedTextSize;
    private int selectedTextColor;
    private Drawable defalultBackground;
    private Drawable selectedBackground;
    private float itemWidthPercent;
    private int itemWidth;
    private int itemMargin;

    private LinearLayout linearLayout;
    private EditText editText;

    public CheckCodeView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CheckCodeView);
        width = typedArray.getLayoutDimension(R.styleable.CheckCodeView_android_layout_width, -1);
        checkCodeLength = typedArray.getInt(R.styleable.CheckCodeView_length, 6);
        defalultBackground = typedArray.getDrawable(R.styleable.CheckCodeView_background);
        selectedBackground = typedArray.getDrawable(R.styleable.CheckCodeView_selectedBackground);
        defaultTextSize = typedArray.getDimension(R.styleable.CheckCodeView_textSize, 20f);
        selectedTextSize = typedArray.getDimension(R.styleable.CheckCodeView_selectedTextSize, -1f);
        defaultTextColor = typedArray.getColor(R.styleable.CheckCodeView_textColor, getResources().getColor(android.R.color.darker_gray));
        selectedTextColor = typedArray.getColor(R.styleable.CheckCodeView_selectedTextColor, -1);
        itemWidthPercent = typedArray.getFloat(R.styleable.CheckCodeView_itemWidthPercent, (float) (1.0 / (2 * checkCodeLength -1)));
        itemWidth = typedArray.getDimensionPixelSize(R.styleable.CheckCodeView_itemWidth, 100);
        itemMargin = typedArray.getDimensionPixelSize(R.styleable.CheckCodeView_itemMargin, 40);

        typedArray.recycle();

        initUI();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpec = widthMeasureSpec;
        int heightSpec = heightMeasureSpec;

        if (widthMode == MeasureSpec.AT_MOST) {
            int width = itemWidth * checkCodeLength + itemMargin * (checkCodeLength - 1);
            widthSpec = MeasureSpec.makeMeasureSpec(width, widthMode);
        }
        super.onMeasure(widthSpec, heightSpec);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                int pos = editText.getText().length();
                if (pos < checkCodeLength && selectedBackground != null) {
                    TextView textView = (TextView) linearLayout.getChildAt(pos);
                    textView.setBackground(selectedBackground);
                }
                break;

            default:
        }
        return super.dispatchTouchEvent(ev);
    }

    private void initUI() {
        linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        LayoutParams containerLayoutPara = new LayoutParams(MATCH_PARENT, MATCH_PARENT);

        editText = new EditText(getContext());
        editText.setCursorVisible(false);
        editText.setTextColor(Color.TRANSPARENT);
        editText.setBackgroundColor(Color.TRANSPARENT);
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(checkCodeLength)});
        addView(editText, containerLayoutPara);
        addView(linearLayout, containerLayoutPara);

//        int itemMargin = (int) ((parentWidth - checkCodeLength * itemWidthPercent * parentWidth) / (checkCodeLength - 1));
        int itemMargin = this.itemMargin;

        for (int i = 0; i < checkCodeLength; i++) {
            TextView textView = new TextView(getContext());
            textView.setTextSize(defaultTextSize);
            textView.setTextColor(defaultTextColor);
            textView.setGravity(Gravity.CENTER);

            if (defalultBackground != null) {
                textView.setBackground(defalultBackground);
            }

            LinearLayout.LayoutParams itemLayoutPara = new LinearLayout.LayoutParams(itemWidth, MATCH_PARENT);
            System.out.println("getMeasuredWidth:" + getMeasuredWidth());

            if (i != 0) {
                itemLayoutPara.leftMargin = itemMargin;
            }

            linearLayout.addView(textView, itemLayoutPara);
        }

        setTextListener();
    }

    private void setTextListener() {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString();
                if (str.length() > checkCodeLength) {
                    str = str.substring(0, checkCodeLength);
                }

                int length = Math.min(str.length(), checkCodeLength);

                for (int i=0; i<checkCodeLength; i++) {

                    TextView textView = (TextView) linearLayout.getChildAt(i);
                    if (i < length) {
                        String itemStr = s.toString().charAt(i) + "";
                        textView.setText(itemStr);
                    } else {
                        textView.setText("");
                    }

                    if (i == length) {
                        if (selectedBackground != null) {
                            textView.setBackground(selectedBackground);
                        }

                        if (selectedTextSize > 0) {
                            textView.setTextSize(selectedTextSize);
                        }

                        if (selectedTextColor > 0) {
                            textView.setTextColor(selectedTextColor);
                        }

                    } else {
                        if (defalultBackground != null) {
                            textView.setBackground(defalultBackground);
                        }
                        textView.setTextSize(defaultTextSize);
                        textView.setTextColor(defaultTextColor);
                    }
                }

                if (str.length() == checkCodeLength) {
                    if (inputListener != null) {
                        inputListener.onFinish(str);
                    }
                } else {
                    if (inputListener != null) {
                        inputListener.onInput(str);
                    }
                }
            }
        });
    }

    public void clear() {
        editText.setText("");
    }

    public String getInput() {
        return editText.getText().toString();
    }

    public void setInputListener(InputListener inputListener) {
        this.inputListener = inputListener;
    }

    private InputListener inputListener;

    public interface InputListener {
        void onInput(String checkCOde);
        void onFinish(String checkCode);
    }

}
