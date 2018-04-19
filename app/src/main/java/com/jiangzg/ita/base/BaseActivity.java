package com.jiangzg.ita.base;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.DraweeTransition;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityStack;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.system.InputUtils;
import com.jiangzg.base.system.PermUtils;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.base.view.BarUtils;
import com.jiangzg.base.view.ScreenUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.ita.R;
import com.jiangzg.ita.activity.HomeActivity;
import com.jiangzg.ita.helper.DialogHelper;
import com.jiangzg.ita.helper.ThemeHelper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Stack;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by JiangZhiGuo on 2016-12-2.
 * describe Activity的基类
 */
public abstract class BaseActivity<T> extends AppCompatActivity {

    public final String LOG_TAG = getCls().getSimpleName();

    public BaseActivity mActivity;
    public FragmentManager mFragmentManager;
    public View mRootView;
    public int mRootViewId;
    private Unbinder mUnbinder;
    private MaterialDialog mLoading;
    private Long mLastExitTime = 0L; //最后一次退出时间
    private boolean isLoad = false; // 是否加载过数据，主要用于换肤

    /* activity跳转demo */
    private static void goActivity(Activity from) {
        Intent intent = new Intent(from, BaseActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    /* 初始layout(setContent之前调用) */
    protected abstract int getView(Intent intent);

    /* 实例化View */
    protected abstract void initView(Bundle state);

    /* 初始Data */
    protected abstract void initData(Bundle state);

    public MaterialDialog getLoading(String msg, boolean cancelable, DialogInterface.OnDismissListener listener) {
        MaterialDialog loading = getLoading(msg, cancelable);
        loading.setOnDismissListener(listener);
        return loading;
    }

    public MaterialDialog getLoading(String msg, boolean cancelable) {
        if (StringUtils.isEmpty(msg)) {
            msg = getString(R.string.please_wait);
        }
        MaterialDialog loading = getLoading(cancelable);
        loading.setContent(msg);
        return loading;
    }

    public MaterialDialog getLoading(boolean cancelable) {
        if (mLoading == null) {
            MaterialDialog.Builder builder = DialogHelper.getBuild(mActivity)
                    .cancelable(cancelable)
                    .canceledOnTouchOutside(false)
                    .content(R.string.please_wait)
                    .progress(true, 0)
                    .progressIndeterminateStyle(false);
            mLoading = builder.build();
            DialogHelper.setAnim(mLoading);
        }
        mLoading.setContent(R.string.please_wait);
        return mLoading;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mActivity = this;
        InputUtils.initActivity(this); // 软键盘
        ScreenUtils.requestPortrait(this); // 竖屏
        BarUtils.requestNoTitle(this); // noTitle
        initTransAnim(this); //  过渡动画
        ThemeHelper.initTheme(this);
        super.onCreate(savedInstanceState);
        mFragmentManager = getSupportFragmentManager();
        mRootViewId = getView(getIntent());
        setContentView(mRootViewId);
    }

    /* setContentView()或addContentView()后调用,view只是加载出来，没有实例化.
       为了页面的加载速度，不要在setContentView前做过多的操作 */
    @Override
    public void onContentChanged() {
        super.onContentChanged();
        // 每次setContentView之后都要bind一下
        mUnbinder = ButterKnife.bind(this);
        // 二次setContentView之后控件是以前view的，所以要重新实例化一次
        initView(null);
        // 二次setContentView的话，可以不用获取数据 只加载数据
        if (!isLoad) {
            isLoad = true;
            initData(null);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /*
     * DecorView在这里才会有params,viewGroup在这里才能add
     * Window是WindowManager最顶层的视图，PhoneWindow是Window的唯一实现类
     * DecorView是window下的子视图,是所有应用窗口(Activity界面)的根View
     */
    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        // 控制DecorView的大小来控制activity的大小，可做窗口activity
        mRootView = getWindow().getDecorView();
        // setFinishOnTouchOutside(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
    }

    /* 触摸事件 */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        View focus = this.getCurrentFocus();
        if (null != focus) { // 点击屏幕空白区域隐藏软键盘
            return InputUtils.hideSoftInput(focus);
        }
        return super.onTouchEvent(event);
    }

    /* 手机返回键 */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
        }
        return true;
    }

    /* 菜单事件 */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // 返回键
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Stack<Activity> stack = ActivityStack.getStack();
        if (stack.size() <= 1 || this.getCls() == HomeActivity.class) {
            long nowTime = DateUtils.getCurrentLong();
            if (nowTime - mLastExitTime > 2000) { // 第一次按
                ToastUtils.show(getString(R.string.press_again_exit));
            } else { // 返回键连按两次
                //AppUtils.appExit();
                super.onBackPressed();
            }
            mLastExitTime = nowTime;
        } else {
            super.onBackPressed();
        }
    }

    /**
     * @return 获取当前类
     */
    @SuppressWarnings("unchecked")
    public Class<T> getCls() {
        Type type = this.getClass().getGenericSuperclass();
        return (Class<T>) (((ParameterizedType) (type)).getActualTypeArguments()[0]);
    }

    /*
     * activity过渡动画初始化, 要在setContentView之前调用
     * 这里设置的是5.0以上的过渡动画,需要ActivityTrans中的跳转方式才会起效
     * 5.0以下的过渡动画已在themes里添加
     */
    private void initTransAnim(Activity activity) {
        Window window = activity.getWindow();
        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        // 压栈
        window.setEnterTransition(new Fade(0)); // 下一个activity进场
        window.setExitTransition(new Fade(0)); // 当前activity向后时退场
        // 弹栈
        //window.setReenterTransition(new Fade(0)); // 上一个activity进场
        //window.setReturnTransition(new Fade(0)); // 当前activity向前时退场
        // fresco动画
        getWindow().setSharedElementEnterTransition(DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.CENTER_CROP, ScalingUtils.ScaleType.FIT_CENTER)); // 进入
        getWindow().setSharedElementReturnTransition(DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.FIT_CENTER, ScalingUtils.ScaleType.CENTER_CROP)); // 返回
    }

}
