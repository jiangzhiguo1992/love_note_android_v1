package com.jiangzg.ita.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
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

    //@BindView(R.id.ivScreen)
    //GImageView ivScreen;
    @BindView(R.id.vpImage)
    ViewPager vpImage;

    public static void goActivity(Activity from, Uri uri, GImageView view) {
        Intent intent = new Intent(from, ImgScreenActivity.class);
        ArrayList<Uri> uris = new ArrayList<>();
        uris.add(uri);
        intent.putParcelableArrayListExtra("imgList", uris);
        intent.putExtra("showIndex", 0);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        Pair<View, String> share = Pair.create((View) view, "imgAnim");
        ActivityTrans.start(from, intent, share);
    }

    public static void goActivity(Activity from, ArrayList<Uri> uri, int startIndex, GImageView view) {
        Intent intent = new Intent(from, ImgScreenActivity.class);
        intent.putParcelableArrayListExtra("imgList", uri);
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
        ArrayList<Uri> imgList = getIntent().getParcelableArrayListExtra("imgList");
        int showIndex = getIntent().getIntExtra("showIndex", 0);
        // 设置pager
        ImgScreenPagerAdapter adapter = new ImgScreenPagerAdapter(mActivity);
        adapter.setData(imgList);
        vpImage.setAdapter(adapter);
        vpImage.setCurrentItem(showIndex, false);

        ViewCompat.setTransitionName(vpImage, "imgAnim");
        //
        //ViewCompat.setTransitionName(ivScreen, "imgAnim");
        //showType = getIntent().getIntExtra("showType", SHOW_SINGLE);
        //if (showType == SHOW_MULTI) {
        //    initMulti();
        //} else {
        //    initSingle();
        //}
        //adapter.setData();

    }

    @Override
    protected void initData(Bundle state) {

    }

    //private void initMulti() {
    //    ArrayList<Uri> imgMulti = getIntent().getParcelableArrayListExtra("imgMulti");
    //    int showIndex = getIntent().getIntExtra("showIndex", 0);
    //    Uri uri = imgMulti.get(showIndex);
    //    ivScreen.setDataUri(uri);
    //}
    //
    //private void initSingle() {
    //    Uri imgSingle = getIntent().getParcelableExtra("imgSingle");
    //    ivScreen.setDataUri(imgSingle);
    //}

}
