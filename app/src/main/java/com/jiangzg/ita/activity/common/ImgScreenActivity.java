package com.jiangzg.ita.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
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

// todo activity还是fragment
public class ImgScreenActivity extends BaseActivity<ImgScreenActivity> {

    private static final int SHOW_SINGLE = 0;
    private static final int SHOW_MULTI = 1;

    @BindView(R.id.ivScreen)
    GImageView ivScreen;

    private int showType;

    public static void goActivity(Activity from, Uri uri, GImageView view) {
        Intent intent = new Intent(from, ImgScreenActivity.class);
        intent.putExtra("imgSingle", uri);
        intent.putExtra("showType", SHOW_SINGLE);
        Pair<View, String> share = Pair.create((View) view, "imgAnim");
        ActivityTrans.start(from, intent, share);
    }

    public static void goActivity(Activity from, ArrayList<Uri> uri, int startIndex, GImageView view) {
        Intent intent = new Intent(from, ImgScreenActivity.class);
        intent.putParcelableArrayListExtra("imgMulti", uri);
        intent.putExtra("showIndex", startIndex);
        intent.putExtra("showType", SHOW_MULTI);
        Pair<View, String> share = Pair.create((View) view, "imgAnim");
        ActivityTrans.start(from, intent, share);
    }

    @Override
    protected int getView(Intent intent) {
        getWindow().setSharedElementEnterTransition(DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.CENTER_CROP, ScalingUtils.ScaleType.FIT_CENTER)); // 进入
        getWindow().setSharedElementReturnTransition(DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.FIT_CENTER, ScalingUtils.ScaleType.CENTER_CROP)); // 返回

        BarUtils.setStatusBarTrans(mActivity, true);
        BarUtils.setNavigationBarColor(mActivity, Color.BLACK);
        //BarUtils.setNavigationBarTrans(mActivity, true);
        return R.layout.activity_img_screen;
    }

    @Override
    protected void initView(Bundle state) {
        // todo 双击缩放 手指缩放 多图侧滑 图片下载
        ViewCompat.setTransitionName(ivScreen, "imgAnim");
        showType = getIntent().getIntExtra("showType", SHOW_SINGLE);
        if (showType == SHOW_MULTI) {
            initMulti();
        } else {
            initSingle();
        }
    }

    @Override
    protected void initData(Bundle state) {

    }

    private void initMulti() {
        ArrayList<Uri> imgMulti = getIntent().getParcelableArrayListExtra("imgMulti");
        int showIndex = getIntent().getIntExtra("showIndex", 0);

        Uri uri = imgMulti.get(showIndex);
        ivScreen.setDataUri(uri);
    }

    private void initSingle() {
        Uri imgSingle = getIntent().getParcelableExtra("imgSingle");
        ivScreen.setDataUri(imgSingle);
    }

}
