package com.ut.module_mall;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ut.commoncomponent.Flowlayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Flowlayout flowlayout1 = findViewById(R.id.fly1);
//        ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        lp.leftMargin = 5;
//        lp.rightMargin = 5;
//        lp.topMargin = 5;
//        lp.bottomMargin = 5;
//        List<String> list = new ArrayList<>();
//        list.add("哈哈");
//        list.add("漂村脊 使用者");
//        list.add("哈哈");
//        list.add("哈哈1");
//        list.add("哈哈2");
//        list.add("哈哈3");
//        list.add("漂村脊 使用者 sdf");
//        list.add("哈哈栽华清嘉园会使地膛f");
//        list.add("漂村脊 使用者");
//        list.add("哈哈");
//        list.add("漂村脊 使用者");
//        for (int i = 0; i < list.size(); i++) {
//            TextView view = new TextView(this);
//            view.setText(list.get(i));
//            view.setTag(list.get(i));
//            view.setTextColor(Color.RED);
//            view.setPadding(10, 10, 10, 10);
//            view.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape));
//            view.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(MainActivity.this, "click:" + v.getTag(), Toast.LENGTH_SHORT).show();
//                }
//            });
//            flowlayout1.addView(view, lp);
//
//        }
    }
}