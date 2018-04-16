package com.jiangzg.ita.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.BarUtils;
import com.jiangzg.ita.R;
import com.jiangzg.ita.adapter.ImgScreenPagerAdapter;
import com.jiangzg.ita.base.BaseActivity;
import com.jiangzg.ita.view.GImageView;

import java.util.ArrayList;

import butterknife.BindView;

// todo activity还是fragment
public class ImgScreenActivity extends BaseActivity<ImgScreenActivity> {

    @BindView(R.id.vpImage)
    ViewPager vpImage;

    public static void goActivity(Activity from, String ossPath, GImageView view) {
        Intent intent = new Intent(from, ImgScreenActivity.class);
        ArrayList<String> list = new ArrayList<>();
        list.add(ossPath);
        intent.putStringArrayListExtra("imgList", list);
        intent.putExtra("showIndex", 0);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        Pair<View, String> share = Pair.create((View) view, "imgAnim");
        ActivityTrans.start(from, intent, share);
    }

    public static void goActivity(Activity from, ArrayList<String> ossPathList, int startIndex, GImageView view) {
        Intent intent = new Intent(from, ImgScreenActivity.class);
        intent.putStringArrayListExtra("imgList", ossPathList);
        intent.putExtra("showIndex", startIndex);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        Pair<View, String> share = Pair.create((View) view, "imgAnim");
        ActivityTrans.start(from, intent, share);
    }

    @Override
    protected int getView(Intent intent) {
        BarUtils.setStatusBarTrans(mActivity, true);
        BarUtils.setNavigationBarTrans(mActivity, true);
        return R.layout.activity_img_screen;
    }

    @Override
    protected void initView(Bundle state) {
        // todo 双击缩放 手指缩放 多图侧滑 图片下载
        // 获取数据
        ArrayList<String> imgList = getIntent().getStringArrayListExtra("imgList");
        int showIndex = getIntent().getIntExtra("showIndex", 0);
        // 设置pager
        ImgScreenPagerAdapter adapter = new ImgScreenPagerAdapter(mActivity);
        adapter.setData(imgList);
        vpImage.setAdapter(adapter);
        vpImage.setCurrentItem(showIndex, false);

        ViewCompat.setTransitionName(vpImage, "imgAnim");

    }

    @Override
    protected void initData(Bundle state) {

    }

}
