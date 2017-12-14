package com.jiangzg.ita.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.jiangzg.base.component.activity.ActivityTrans;
import com.jiangzg.base.component.intent.IntentUtils;
import com.jiangzg.base.function.PermUtils;
import com.jiangzg.base.media.image.BitmapMedia;
import com.jiangzg.base.common.ConvertUtils;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.ita.third.GlideUtils;
import com.jiangzg.ita.third.LogUtils;
import com.jiangzg.ita.third.LuBanUtils;
import com.jiangzg.ita.R;
import com.jiangzg.ita.base.BaseActivity;
import com.jiangzg.ita.utils.ViewUtils;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import top.zibin.luban.OnCompressListener;

/**
 * Created by JiangZhiGuo on 2016/06/01
 * describe 主界面
 */
public class HomeActivity extends BaseActivity<HomeActivity> {

    @BindView(R.id.btn1)
    Button btn1;
    @BindView(R.id.btn2)
    Button btn2;
    @BindView(R.id.btn3)
    Button btn3;
    @BindView(R.id.ivMain)
    ImageView ivMain;

    private File jpgInRes;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, HomeActivity.class);
        // TODO: 2017/5/27  clearTop???
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int initObj(Intent intent) {
        return R.layout.activity_home;
    }

    @Override
    protected void initView(Bundle state) {
        ViewUtils.initTop(mActivity, "主页面");
    }

    @Override
    protected void initData(Bundle state) {
        LogUtils.e("----------------");
    }

    @OnClick({R.id.btn1, R.id.btn2, R.id.btn3})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn1:
                //PermUtils.requestPermissions(mActivity, 11, PermUtils.camera, new PermUtils.OnPermissionListener() {
                //    @Override
                //    public void onPermissionGranted(int permissions) {
                //        //jpgInRes = ResUtils.createJPGInRes();
                //        //Intent camera = IntentUtils.getCamera(jpgInRes);
                //        Intent camera = IntentUtils.getCamera(null);
                //        ActivityTrans.startResult(mActivity, camera, 11);
                //    }
                //
                //    @Override
                //    public void onPermissionDenied(int permissions) {
                //
                //    }
                //});
                break;
            case R.id.btn2:
                Intent picture = IntentUtils.getPicture();
                ActivityTrans.startResult(mActivity, picture, 22);
                break;
            case R.id.btn3:
                FragActivity.goActivity(mActivity);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) return;
        switch (requestCode) {
            case 11:
                compress();
                break;
            case 22:
                Uri pictureUri = BitmapMedia.getPictureUri(data);
                jpgInRes = ConvertUtils.URI2File(pictureUri);
                compress();
                break;
        }
    }

    private void compress() {
        LuBanUtils.compress(mActivity, jpgInRes, new OnCompressListener() {
            @Override
            public void onStart() {
//                getLoading().show();
            }

            @Override
            public void onSuccess(File file) {
//                getLoading().dismiss();
                ToastUtils.show(file.getAbsolutePath());
                GlideUtils.load(mActivity, file, ivMain);
            }

            @Override
            public void onError(Throwable e) {
//                getLoading().dismiss();
            }
        });
    }

    private Long lastExitTime = 0L; //最后一次退出时间

    /* 手机返回键 */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Long nowTime = DateUtils.getCurrentLong();
            if (nowTime - lastExitTime > 2000) { // 第一次按
                ToastUtils.show(R.string.press_again_exit);
            } else { // 返回键连按两次
                System.exit(0); // 真正退出程序
            }
            lastExitTime = nowTime;
        }
        return true;
    }

}
