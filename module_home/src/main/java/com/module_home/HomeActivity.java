package com.module_home;

import android.Manifest;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.arouterlib.SearchRoutePath;
import com.baseproject.bean.MainBean;
import com.baseproject.mvp_network.base.BaseActivity;
import com.baseproject.mvp_network.base.download.DownloadListener;
import com.baseproject.mvp_network.base.download.DownloadUtil;
import com.baseproject.mvp_network.base.mvp.BaseModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends BaseActivity<HomePresenter> implements HomeView {
    private TextView mTvText;
    private TextView tvProgress;
    private TextView tvFileLocation;

    private List<MainBean> mainBeans;


    @Override
    protected HomePresenter createPresenter() {
        return new HomePresenter(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    protected void initData() {
        mTvText = findViewById(R.id.tv_text);
        tvProgress = findViewById(R.id.tv_progess);
        tvFileLocation = findViewById(R.id.tv_file_location);
        mainBeans = new ArrayList<>();
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        final Button btn_first = findViewById(R.id.btn_first);
        btn_first.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.getMainApi3();
            }
        });
        final Button btn_second = findViewById(R.id.btn_second);
        btn_second.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downLoadFile();
            }
        });
        final Button btn_third = findViewById(R.id.btn_third);
        btn_third.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build(SearchRoutePath.ACTIVITY_SEARCH_URL_SIMPLE)
                        .withString("name", "android")
                        .withInt("age", 9)
                        .navigation();
            }
        });
    }

    @Override
    public void onMainSuccess(BaseModel<List<MainBean>> o) {
        Log.e(o.getErrmsg(), "");
        Log.e(o.getErrcode() + "", "");
        mainBeans.addAll(o.getData());
        Log.e("HomeActivity", mainBeans.toString() + "");
        mTvText.setText(o.getData().toString());
    }

    @Override
    public void onUpLoadImgSuccess(BaseModel<Object> o) {

    }


    /**
     * 下载文件
     */
    private void downLoadFile() {
        final String desFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/sstx.apk";
        final String baseUrl = "http://www.apk.anzhi.com/";
        final String url = "data4/apk/201809/06/f2a4dbd1b6cc2dca6567f42ae7a91f11_45629100.apk";
        DownloadUtil.getInstance()
                .downloadFile(baseUrl, url, desFilePath, new DownloadListener() {
                    @Override
                    public void onFinish(final File file) {
                        tvFileLocation.setText("下载的文件地址为：" + file.getAbsolutePath());
                        showToast("下载完成");
                    }

                    @Override
                    public void onProgress(int progress) {
                        tvProgress.setText(String.format("下载进度为：%s", progress));
                    }

                    @Override
                    public void onFailed(String errMsg) {
                        showToast("下载失败" + errMsg);
                    }
                });
    }
}
