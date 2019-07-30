package com.module_search;

import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.arouterlib.SearchRoutePath;
import com.baseproject.mvp_network.base.BaseActivity;
import com.baseproject.mvp_network.base.mvp.BasePresenter;

@Route(path = SearchRoutePath.ACTIVITY_SEARCH_URL_SIMPLE)
public class SimpleActivity extends BaseActivity {
    @Autowired
    String name;
    @Autowired
    int age;
    private TextView textView;

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_simple;
    }

    @Override
    protected void initData() {
        textView = findViewById(R.id.tv);
        textView.setText("名称：" + name + "年龄：" + age);
    }
}
