package com.jiangzg.lovenote.controller.activity.main;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.jiangzg.base.component.ActivityStack;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.BarUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.controller.activity.user.LoginActivity;
import com.jiangzg.lovenote.controller.activity.user.RegisterActivity;
import com.jiangzg.lovenote.controller.adapter.common.CommonPagerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import butterknife.BindView;
import butterknife.OnClick;

public class SplashActivity extends BaseActivity<SplashActivity> {

    @BindView(R.id.root)
    RelativeLayout root;
    @BindView(R.id.vp)
    ViewPager vp;
    @BindView(R.id.llBottom)
    LinearLayout llBottom;
    @BindView(R.id.btnLogin)
    Button btnLogin;
    @BindView(R.id.btnRegister)
    Button btnRegister;

    private int currentItem;

    public static void goActivity(Activity from) {
        // 顶部已经是SplashActivity时，不再跳转
        Activity top = ActivityStack.getTop();
        if (top != null) {
            ComponentName name = top.getComponentName();
            if (name.getClassName().equals(SplashActivity.class.getSimpleName())) {
                return;
            }
        }
        Intent intent = new Intent(from, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        BarUtils.setStatusBarTrans(mActivity, true);
        BarUtils.setNavigationBarTrans(mActivity, true);
        return R.layout.activity_splash;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        // 底部提示的高度
        int height = BarUtils.getNavigationBarHeight(mActivity);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) llBottom.getLayoutParams();
        layoutParams.bottomMargin += height;
        llBottom.setLayoutParams(layoutParams);
        // color
        final int[] colorList = new int[]{ContextCompat.getColor(mActivity, R.color.theme_red_dark),
                ContextCompat.getColor(mActivity, R.color.theme_teal_dark),
                ContextCompat.getColor(mActivity, R.color.theme_orange_dark),
                ContextCompat.getColor(mActivity, R.color.theme_blue_dark),
                ContextCompat.getColor(mActivity, R.color.theme_brown_dark)};
        // bg
        currentItem = 0;
        root.setBackgroundColor(colorList[currentItem]);
        btnLogin.setTextColor(colorList[currentItem]);
        btnRegister.setTextColor(colorList[currentItem]);
        // pager
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                int cLeft = colorList[(position < 0 || position >= (colorList.length - 1)) ? currentItem : position];
                int cRight = colorList[((position + 1) < 0 || (position + 1) >= (colorList.length - 1)) ? currentItem : position + 1];
                int color = getCurrentColor(positionOffset, cLeft, cRight);
                if (color == 0) return;
                root.setBackgroundColor(color);
                btnLogin.setTextColor(color);
                btnRegister.setTextColor(color);
            }

            @Override
            public void onPageSelected(int position) {
                currentItem = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        // adapter
        CommonPagerAdapter adapter = new CommonPagerAdapter(mActivity);
        vp.setAdapter(adapter);
        // data
        List<Integer> dataList = new ArrayList<>();
        dataList.add(R.layout.pager_item_splash_1);
        dataList.add(R.layout.pager_item_splash_2);
        dataList.add(R.layout.pager_item_splash_3);
        dataList.add(R.layout.pager_item_splash_4);
        adapter.setDataList(dataList);
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
    }

    @Override
    protected void onFinish(Bundle state) {
    }

    // 关闭其他activity
    @Override
    protected void onStart() {
        super.onStart();
        Stack<Activity> stack = ActivityStack.getStack();
        for (Activity activity : stack) {
            if (activity != mActivity) {
                activity.finish();
            }
        }
    }

    @OnClick({R.id.btnLogin, R.id.btnRegister})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                LoginActivity.goActivity(mActivity);
                break;
            case R.id.btnRegister:
                RegisterActivity.goActivity(mActivity);
                break;
        }
    }

    private int getCurrentColor(float fraction, int startColor, int endColor) {
        int redStart = Color.red(startColor);
        int blueStart = Color.blue(startColor);
        int greenStart = Color.green(startColor);
        int alphaStart = Color.alpha(startColor);

        int redEnd = Color.red(endColor);
        int blueEnd = Color.blue(endColor);
        int greenEnd = Color.green(endColor);
        int alphaEnd = Color.alpha(endColor);

        int redDifference = redEnd - redStart;
        int blueDifference = blueEnd - blueStart;
        int greenDifference = greenEnd - greenStart;
        int alphaDifference = alphaEnd - alphaStart;

        int redCurrent = (int) (redStart + fraction * redDifference);
        int blueCurrent = (int) (blueStart + fraction * blueDifference);
        int greenCurrent = (int) (greenStart + fraction * greenDifference);
        int alphaCurrent = (int) (alphaStart + fraction * alphaDifference);

        return Color.argb(alphaCurrent, redCurrent, greenCurrent, blueCurrent);
    }

}
