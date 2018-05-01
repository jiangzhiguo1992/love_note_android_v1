package com.jiangzg.mianmian.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.BarUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.adapter.BigImagePagerAdapter;
import com.jiangzg.mianmian.base.BaseActivity;

import java.util.ArrayList;

import butterknife.BindView;

public class BigImageActivity extends BaseActivity<BigImageActivity> {

    public static final int TYPE_OSS_SINGLE = 0;
    public static final int TYPE_FILE_SINGLE = 1;
    public static final int TYPE_OSS_LIST = 2;
    public static final int TYPE_FILE_LIST = 3;

    @BindView(R.id.vpImage)
    ViewPager vpImage;

    private int type;

    public static void goActivityByOss(Activity from, String ossPath, SimpleDraweeView view) {
        Intent intent = new Intent(from, BigImageActivity.class);
        intent.putExtra("type", TYPE_OSS_SINGLE);
        intent.putExtra("imgOss", ossPath);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        Pair<View, String> share = Pair.create((View) view, "imgAnim");
        ActivityTrans.start(from, intent, share);
    }

    public static void goActivityByFile(Activity from, String filePath, SimpleDraweeView view) {
        Intent intent = new Intent(from, BigImageActivity.class);
        intent.putExtra("type", TYPE_FILE_SINGLE);
        intent.putExtra("imgFile", filePath);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        Pair<View, String> share = Pair.create((View) view, "imgAnim");
        ActivityTrans.start(from, intent, share);
    }

    public static void goActivityByOssList(Activity from, ArrayList<String> ossPathList, int startIndex, SimpleDraweeView view) {
        Intent intent = new Intent(from, BigImageActivity.class);
        intent.putExtra("type", TYPE_OSS_LIST);
        intent.putStringArrayListExtra("imgOssList", ossPathList);
        intent.putExtra("showIndex", startIndex);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        Pair<View, String> share = Pair.create((View) view, "imgAnim");
        ActivityTrans.start(from, intent, share);
    }

    public static void goActivityByFileList(Activity from, ArrayList<String> filePathList, int startIndex, SimpleDraweeView view) {
        Intent intent = new Intent(from, BigImageActivity.class);
        intent.putExtra("type", TYPE_OSS_LIST);
        intent.putStringArrayListExtra("imgFileList", filePathList);
        intent.putExtra("showIndex", startIndex);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        Pair<View, String> share = Pair.create((View) view, "imgAnim");
        ActivityTrans.start(from, intent, share);
    }

    @Override
    protected int getView(Intent intent) {
        BarUtils.setStatusBarTrans(mActivity, true);
        BarUtils.setNavigationBarTrans(mActivity, true);
        return R.layout.activity_big_image;
    }

    @Override
    protected void initView(Bundle state) {
        // TODO 多页下标 1/9
        // TODO Oss下载(会员可无水印?)

        ViewCompat.setTransitionName(vpImage, "imgAnim");
        // type
        type = getIntent().getIntExtra("type", TYPE_OSS_SINGLE);
        int showIndex = getIntent().getIntExtra("showIndex", 0);
        // adapter
        BigImagePagerAdapter adapter = new BigImagePagerAdapter(mActivity);
        adapter.setType(type);
        switch (type) {
            case TYPE_FILE_LIST:
                ArrayList<String> imgFileList = getIntent().getStringArrayListExtra("imgFileList");
                adapter.setData(imgFileList);
                break;
            case TYPE_OSS_LIST:
                ArrayList<String> imgOssList = getIntent().getStringArrayListExtra("imgOssList");
                adapter.setData(imgOssList);
                break;
            case TYPE_FILE_SINGLE:
                String imgFile = getIntent().getStringExtra("imgFile");
                adapter.setData(imgFile);
                break;
            case TYPE_OSS_SINGLE:
            default:
                String imgOss = getIntent().getStringExtra("imgOss");
                adapter.setData(imgOss);
                break;
        }
        vpImage.setAdapter(adapter);
        vpImage.setCurrentItem(showIndex, false);
    }

    @Override
    protected void initData(Bundle state) {

    }

}
