package com.ut.module_login.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.ut.base.BaseActivity;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.module_login.R;

@Route(path = RouterUtil.LoginModulePath.SELECT_COUNTRY_AREA_CODE)
public class SelectCountryActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_select_country);

        findViewById(R.id.back).setOnClickListener(v -> finish());
        ListView listView = (ListView) findViewById(R.id.country_list);
        String[] arrays = new String[]{"中国/+86", "中国/+86", "中国/+89", "中国/+86", "中国/+86", "中国/+86", "中国/+86"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_country, arrays);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent();
            intent.putExtra("code", arrays[position]);
            setResult(RESULT_OK, intent);
            finish();
        });

    }
}
