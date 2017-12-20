package com.jiangzg.ita.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.transition.Slide;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

import com.jiangzg.base.component.activity.ActivityTrans;
import com.jiangzg.base.function.InputUtils;
import com.jiangzg.base.function.PermUtils;
import com.jiangzg.base.view.BarUtils;
import com.jiangzg.base.view.DialogUtils;
import com.jiangzg.base.view.ScreenUtils;
import com.jiangzg.ita.R;
import com.jiangzg.ita.domain.Result;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;

/**
 * Created by JiangZhiGuo on 2016-12-2.
 * describe Activity的基类
 */
public abstract class BaseActivity<T> extends AppCompatActivity {

    public BaseActivity mActivity;
    public FragmentManager mFragmentManager;
    public View rootView;
    private Unbinder unbinder;
    private ProgressDialog loading;

    /* activity跳转demo */
    private static void goActivity(Activity from) {
        Intent intent = new Intent(from, BaseActivity.class);
        // intent.putExtra();
        ActivityTrans.start(from, intent);
    }

    /* 初始layout(setContent之前调用) */
    protected abstract int getView(Intent intent);

    /* 实例化View */
    protected abstract void initView(Bundle state);

    /* 初始Data */
    protected abstract void initData(Bundle state);

    public ProgressDialog getLoading(String msg, final Call call, final Activity activity) {
        if (loading != null) {
            loading.setMessage(msg);
        } else {
            loading = DialogUtils.createLoading(mActivity, 0, "", msg, true);
            DialogUtils.setAnim(loading, R.style.DialogAnim);
        }
        if (call == null && activity == null) return loading;
        loading.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (call != null && !call.isCanceled()) {
                    call.cancel();
                }
                if (activity != null) {
                    activity.finish();
                }
            }
        });
        return loading;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mActivity = this;
        InputUtils.initActivity(this); // 软键盘
        ScreenUtils.requestPortrait(this); // 竖屏
        BarUtils.requestNoTitle(this); // noTitle
        initTransAnim(this); //  过渡动画
        super.onCreate(savedInstanceState);
        mFragmentManager = getSupportFragmentManager();
        setContentView(getView(getIntent()));
        // 每次setContentView之后都要bind一下
        unbinder = ButterKnife.bind(this);
        initView(savedInstanceState);
        initData(savedInstanceState);
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
        rootView = getWindow().getDecorView();
        // setFinishOnTouchOutside(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            // 压栈
            window.setEnterTransition(new Slide()); // 下一个activity进场
            window.setExitTransition(new Slide()); // 当前activity向后时退场
            // 弹栈
            // window.setReenterTransition(new Fade()); // 上一个activity进场
            // window.setReturnTransition(new Fade()); // 当前activity向前时退场
        }
    }

}
