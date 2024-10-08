package com.jiangzg.lovenote.controller.activity.main;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.jiangzg.base.application.AppUtils;
import com.jiangzg.base.common.DateUtils;
import com.jiangzg.base.common.FileUtils;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.common.TimeUnit;
import com.jiangzg.base.system.PermUtils;
import com.jiangzg.base.view.BarUtils;
import com.jiangzg.base.view.ScreenUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.helper.common.ApiHelper;
import com.jiangzg.lovenote.helper.common.ResHelper;
import com.jiangzg.lovenote.helper.common.SPHelper;
import com.jiangzg.lovenote.helper.common.UserHelper;
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.helper.view.DialogHelper;
import com.jiangzg.lovenote.main.MyApp;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.Entry;
import com.jiangzg.lovenote.view.FrescoNativeView;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import retrofit2.Call;

/**
 * Created by JiangZhiGuo on 2016/06/01
 * describe 启动界面
 */
public class WelcomeActivity extends BaseActivity<WelcomeActivity> {

    private static final long TransPageMillis = TimeUnit.SEC * 2;

    @BindView(R.id.ivBg)
    FrescoNativeView ivBg;
    @BindView(R.id.tvOnline)
    TextView tvOnline;
    @BindView(R.id.ivShouFa)
    ImageView ivShouFa;

    @Override
    protected int getView(Intent intent) {
        BarUtils.setStatusBarTrans(mActivity, true);
        BarUtils.setNavigationBarTrans(mActivity, true);
        return R.layout.activity_welcome;
    }

    @Override
    protected void initView(Intent intent, Bundle savedInstanceState) {
        // 底部提示的高度
        //int height = BarUtils.getNavigationBarHeight(mActivity);
        //RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) tvOnline.getLayoutParams();
        //layoutParams.bottomMargin += height;
        //tvOnline.setLayoutParams(layoutParams);
        // 首发
        initShouFa();
    }

    @Override
    protected void initData(Intent intent, Bundle savedInstanceState) {
        // wallPaper
        File wallPaper = getWallPaperRandom();
        if (!FileUtils.isFileEmpty(wallPaper)) {
            ivBg.setVisibility(View.VISIBLE);
            int width = ScreenUtils.getScreenRealWidth(mActivity);
            int height = ScreenUtils.getScreenRealHeight(mActivity);
            ivBg.setWidthAndHeight(width, height);
            ivBg.setDataFile(wallPaper);
            startAnim(); // 渐变动画
        }
        // 必要权限
        PermUtils.requestPermissions(mActivity, BaseActivity.REQUEST_DEVICE_INFO, PermUtils.deviceInfo, new PermUtils.OnPermissionListener() {
            @Override
            public void onPermissionGranted(int requestCode, String[] permissions) {
                // ...非网络性init操作
                checkUser();
            }

            @Override
            public void onPermissionDenied(int requestCode, String[] permissions) {
                DialogHelper.showGoPermDialog(mActivity, true);
            }
        });
    }

    @Override
    protected void onFinish(Bundle state) {
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish(); // 记得关闭欢迎页
    }

    // 获取随机的wp
    public File getWallPaperRandom() {
        List<String> imageList = SPHelper.getWallPaper().getContentImageList();
        if (imageList == null || imageList.size() <= 0) return null;
        List<File> fileList = new ArrayList<>();
        for (String path : imageList) {
            if (StringUtils.isEmpty(path)) continue;
            boolean exists = ResHelper.isKeyFileExists(path);
            if (!exists) continue;
            File file = ResHelper.newKeyFile(path);
            fileList.add(file);
        }
        if (fileList.size() <= 0) {
            LogUtils.i(WelcomeActivity.class, "getWallPaperRandom", "没有WallPaper文件");
            return null;
        }
        Random random = new Random();
        int nextInt = random.nextInt(fileList.size());
        return fileList.get(nextInt);
    }

    private void startAnim() {
        //ObjectAnimator scaleX = ObjectAnimator.ofFloat(ivBg, "scaleX", 1f, 1.2F, 1f);
        //ObjectAnimator scaleY = ObjectAnimator.ofFloat(ivBg, "scaleY", 1f, 1.2F, 1f);
        //AnimatorSet set = new AnimatorSet();
        //set.setDuration(10000);
        //set.playTogether(scaleX, scaleY);
        //set.setInterpolator(new AccelerateDecelerateInterpolator());
        //set.start();
        ObjectAnimator alpha = ObjectAnimator.ofFloat(ivBg, "alpha", 0, 1);
        alpha.setDuration(TimeUnit.SEC);
        alpha.setInterpolator(new AccelerateDecelerateInterpolator());
        alpha.start();
    }

    // 检查用户
    private void checkUser() {
        if (UserHelper.isEmpty(SPHelper.getMe())) {
            // 没有登录
            MyApp.get().getHandler().postDelayed(() -> SplashActivity.goActivity(mActivity), TransPageMillis);
        } else {
            // 有token
            final long startTime = DateUtils.getCurrentLong();
            Entry entry = ApiHelper.getEntryBody();
            // api
            Call<Result> api = new RetrofitHelper().call(API.class).entryPush(entry);
            RetrofitHelper.enqueue(api, null, new RetrofitHelper.CallBack() {
                @Override
                public void onResponse(int code, String message, Result.Data data) {
                    ApiHelper.onEntryFinish(startTime, TransPageMillis, mActivity, code, data);
                }

                @Override
                public void onFailure(int code, String message, Result.Data data) {
                }
            });
            pushApi(api);
        }
    }

    private void initShouFa() {
        ivShouFa.setVisibility(View.GONE);
        String publishEnd = AppUtils.getAppMetaDataString(MyApp.get(), "market_publish_end");
        Calendar now = DateUtils.getCurrentCal();
        Calendar end = DateUtils.getCal(publishEnd, DateUtils.FORMAT_LINE_Y_M_D);
        if (now.getTimeInMillis() > end.getTimeInMillis()) return;
        String marketChannel = AppUtils.getAppMetaDataString(MyApp.get(), "market_channel");
        switch (marketChannel) {
            case "google": // 谷歌(不用！)
                break;
            case "oppo": // oppo
                break;
            case "vivo": // vivo
                break;
            case "huawei": // 华为
                ivShouFa.setVisibility(View.VISIBLE);
                ivShouFa.setImageResource(R.mipmap.shoufa_huawei);
                break;
            case "xiaomi": // 小米
                ivShouFa.setVisibility(View.VISIBLE);
                ivShouFa.setImageResource(R.mipmap.shoufa_xiaomi);
                break;
            case "meizu": // 魅族
                ivShouFa.setVisibility(View.VISIBLE);
                ivShouFa.setImageResource(R.mipmap.shoufa_meizu);
                break;
            case "tencent": // 应用宝
                break;
            case "ali": // 阿里
                ivShouFa.setVisibility(View.VISIBLE);
                ivShouFa.setImageResource(R.mipmap.shoufa_ali);
                break;
            case "baidu": // 百度
                break;
            case "qh360": // 360
                ivShouFa.setVisibility(View.VISIBLE);
                ivShouFa.setImageResource(R.mipmap.shoufa_360);
                break;
            case "kuan": // 酷安
                break;
            default:
                break;
        }
    }

}
