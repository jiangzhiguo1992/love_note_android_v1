package com.jiangzg.ita.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.view.View;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.DraweeTransition;
import com.jiangzg.base.component.activity.ActivityTrans;
import com.jiangzg.base.view.BarUtils;
import com.jiangzg.ita.R;
import com.jiangzg.ita.base.BaseActivity;
import com.jiangzg.ita.view.GImageView;

import java.util.ArrayList;

import butterknife.BindView;

public class ImgScreenActivity extends BaseActivity<ImgScreenActivity> {

    @BindView(R.id.ivScreen)
    GImageView ivScreen;

    public static void goActivity(Activity from, Uri uri, GImageView view) {
        Intent intent = new Intent(from, ImgScreenActivity.class);
        intent.putExtra("imgSingle", uri);
        Pair<View, String> share = Pair.create((View) view, "imgScreen");
        ActivityTrans.start(from, intent, share);
    }

    public static void goActivity(Activity from, ArrayList<Uri> uri, int startIndex, GImageView view) {
        Intent intent = new Intent(from, ImgScreenActivity.class);
        intent.putParcelableArrayListExtra("imgList", uri);
        intent.putExtra("startIndex", startIndex);
        Pair<View, String> share = Pair.create((View) view, "imgScreen");
        ActivityTrans.start(from, intent, share);
    }

    @Override
    protected int getView(Intent intent) {
        initTransAnim();
        BarUtils.setStatusBarTrans(mActivity, true);
        BarUtils.setNavigationBarTrans(mActivity, true);
        return R.layout.activity_img_screen;
    }

    @Override
    protected void initView(Bundle state) {
        ViewCompat.setTransitionName(ivScreen, "imgScreen");
        Uri imgSingle = getIntent().getParcelableExtra("imgSingle");
        ivScreen.setUri(imgSingle);
    }

    @Override
    protected void initData(Bundle state) {

    }

    private void initTransAnim() {
        getWindow().setSharedElementEnterTransition(DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.CENTER_CROP, ScalingUtils.ScaleType.FIT_CENTER)); // 进入
        getWindow().setSharedElementReturnTransition(DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.FIT_CENTER, ScalingUtils.ScaleType.CENTER_CROP)); // 返回
    }

}
