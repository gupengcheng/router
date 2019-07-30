package com.module_home;

import com.baseproject.bean.MainBean;
import com.baseproject.mvp_network.base.mvp.BaseModel;
import com.baseproject.mvp_network.base.mvp.BaseView;

import java.util.List;

/**
 * @ProjectName: MVP-Retrofit2-okhttp3-Rxjava2
 * @Package: com.willkong.mvp_network.core.main
 * @Author: willkong
 * @CreateDate: 2019/7/26 9:32
 * @Description: MainActivity的接口数据回调类
 */
public interface HomeView extends BaseView {
    /**
     * 数据请求成功
     *
     * @param o
     */
    void onMainSuccess(BaseModel<List<MainBean>> o);

    /**
     * 图片上传成功
     *
     * @param o
     */
    void onUpLoadImgSuccess(BaseModel<Object> o);
}
