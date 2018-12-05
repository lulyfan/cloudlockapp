package com.ut.base.UIUtils;

import android.text.Editable;
import android.text.TextWatcher;

public class SimpleTextWatcher implements TextWatcher {

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        beforeChanged(s, start, count, after);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        onChanged(s, start, before, count);
    }

    @Override
    public void afterTextChanged(Editable s) {
        afterChanged(s);
    }

    protected void beforeChanged(CharSequence s, int start, int count, int after) {

    }

    protected void onChanged(CharSequence s, int start, int before, int count) {

    }

    protected void afterChanged(Editable s) {

    }
}
