package com.jiangzg.mianmian.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.component.IntentFactory;
import com.jiangzg.base.system.PermUtils;
import com.jiangzg.base.view.PopUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.base.MyApp;

import java.io.File;
import java.util.Random;

/**
 * Created by JiangZhiGuo on 2016-10-31.
 * describe 符合项目样式的View管理类
 */
public class ViewHelper {

    /**
     * TODO 剪切view , 还有设置四大属性的方法，太多了 不封装了
     */
    public static void clipView(View view, ViewOutlineProvider provider) {
        // 设置Outline , provider在外部自己实现
        view.setOutlineProvider(provider);
        // 剔除Outline以外的view ,可以起裁剪作用
        view.setClipToOutline(true);
    }

    // TODO
    public static void setVectorTint(@DrawableRes int resId) {
        VectorDrawableCompat drawable = VectorDrawableCompat.create(MyApp.get().getResources(), resId, MyApp.get().getTheme());
        if (drawable == null) return;
        drawable.setTint(Color.RED);
    }

    public static void initTopBar(final AppCompatActivity activity, Toolbar tb, String title, boolean navBack) {
        if (activity == null || tb == null) return;
        tb.setTitle(title);
        activity.setSupportActionBar(tb);
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(!title.trim().isEmpty());
            actionBar.setDisplayHomeAsUpEnabled(navBack);
            actionBar.setHomeButtonEnabled(navBack);
            actionBar.setDisplayShowHomeEnabled(navBack);
        }
        if (navBack) {
            //tb.setNavigationIcon(R.drawable.ab_android);
            tb.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.finish();
                }
            });
        } else {
            tb.setTitleMarginStart(50);
        }
    }

    public static void initDrawerLayout(Activity activity, DrawerLayout dl, Toolbar tb) {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(activity, dl, tb,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        dl.addDrawerListener(toggle);
        toggle.syncState();
    }

    /**
     * textView.setCompoundDrawables(null, null, null, null);
     */
    public static Drawable getDrawable(Context context, int draResId) {
        if (draResId == 0) return null;
        Drawable icon = ContextCompat.getDrawable(context, draResId);
        if (icon == null) return null;
        icon.setBounds(0, 0, icon.getMinimumWidth(), icon.getMinimumHeight());
        return icon;
    }

    /**
     * 底划线
     */
    public static void setLineBottom(TextView view) {
        view.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        view.getPaint().setAntiAlias(true);
    }

    /**
     * 中划线
     */
    public static void setLineCenter(TextView view) {
        view.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        view.getPaint().setAntiAlias(true);
    }

    /**
     * 连接点击
     */
    public static void setLinkClick(TextView view) {
        view.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public static int getColorPrimary(Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        return typedValue.resourceId;
    }

    public static int getColorDark(Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorPrimaryDark, typedValue, true);
        return typedValue.resourceId;
    }

    public static int getColorAccent(Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorAccent, typedValue, true);
        return typedValue.resourceId;
    }

    public static int getColorLight(Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorControlNormal, typedValue, true);
        return typedValue.resourceId;
    }

    public static int getRandomThemePrimaryRes() {
        int colors[] = new int[]{R.color.theme_blue_primary, R.color.theme_brown_primary,
                R.color.theme_green_primary, R.color.theme_grey_primary, R.color.theme_indigo_primary,
                R.color.theme_lime_primary, R.color.theme_orange_primary, R.color.theme_pink_primary,
                R.color.theme_purple_primary, R.color.theme_red_primary, R.color.theme_teal_primary,
                R.color.theme_yellow_primary,
        };
        if (colors.length > 0) {
            Random random = new Random();
            int nextInt = random.nextInt(colors.length);
            return colors[nextInt];
        } else {
            return getColorPrimary(MyApp.get());
        }
    }

    public static PopupWindow createPictureCamera(final Activity activity, final File cameraFile) {
        View view = LayoutInflater.from(activity).inflate(R.layout.pop_select_img_from_picture_camera, null);
        final PopupWindow window = PopUtils.createWindow(view);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.root:
                        PopUtils.dismiss(window);
                        ResHelper.deleteFileInBackground(cameraFile);
                        break;
                    case R.id.llPicture:
                        PopUtils.dismiss(window);
                        ResHelper.deleteFileInBackground(cameraFile);
                        PermUtils.requestPermissions(activity, ConsHelper.REQUEST_APP_INFO, PermUtils.picture, new PermUtils.OnPermissionListener() {
                            @Override
                            public void onPermissionGranted(int requestCode, String[] permissions) {
                                Intent picture = IntentFactory.getPicture();
                                ActivityTrans.startResult(activity, picture, ConsHelper.REQUEST_PICTURE);
                            }

                            @Override
                            public void onPermissionDenied(int requestCode, String[] permissions) {
                                DialogHelper.showGoPermDialog(activity);
                            }
                        });
                        break;
                    case R.id.llCamera:
                        PopUtils.dismiss(window);
                        PermUtils.requestPermissions(activity, ConsHelper.REQUEST_CAMERA, PermUtils.camera, new PermUtils.OnPermissionListener() {
                            @Override
                            public void onPermissionGranted(int requestCode, String[] permissions) {
                                Intent camera = IntentFactory.getCamera(cameraFile);
                                ActivityTrans.startResult(activity, camera, ConsHelper.REQUEST_CAMERA);
                            }

                            @Override
                            public void onPermissionDenied(int requestCode, String[] permissions) {
                                DialogHelper.showGoPermDialog(activity);
                            }
                        });
                        break;
                    case R.id.llCancel:
                        PopUtils.dismiss(window);
                        ResHelper.deleteFileInBackground(cameraFile);
                        break;
                }
            }
        };
        RelativeLayout root = view.findViewById(R.id.root);
        LinearLayout llAlbum = view.findViewById(R.id.llPicture);
        LinearLayout llCamera = view.findViewById(R.id.llCamera);
        LinearLayout llCancel = view.findViewById(R.id.llCancel);
        root.setOnClickListener(listener);
        llAlbum.setOnClickListener(listener);
        llCamera.setOnClickListener(listener);
        llCancel.setOnClickListener(listener);
        return window;
    }

    public static PopupWindow createPictureCamera(final Activity activity, String show, final File cameraFile) {
        View view = LayoutInflater.from(activity).inflate(R.layout.pop_select_img_show_from_picture_camera, null);
        final PopupWindow window = PopUtils.createWindow(view);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.root:
                        PopUtils.dismiss(window);
                        ResHelper.deleteFileInBackground(cameraFile);
                        break;
                    case R.id.llPicture:
                        PopUtils.dismiss(window);
                        ResHelper.deleteFileInBackground(cameraFile);
                        PermUtils.requestPermissions(activity, ConsHelper.REQUEST_APP_INFO, PermUtils.picture, new PermUtils.OnPermissionListener() {
                            @Override
                            public void onPermissionGranted(int requestCode, String[] permissions) {
                                Intent picture = IntentFactory.getPicture();
                                ActivityTrans.startResult(activity, picture, ConsHelper.REQUEST_PICTURE);
                            }

                            @Override
                            public void onPermissionDenied(int requestCode, String[] permissions) {
                                DialogHelper.showGoPermDialog(activity);
                            }
                        });
                        break;
                    case R.id.llCamera:
                        PopUtils.dismiss(window);
                        PermUtils.requestPermissions(activity, ConsHelper.REQUEST_CAMERA, PermUtils.camera, new PermUtils.OnPermissionListener() {
                            @Override
                            public void onPermissionGranted(int requestCode, String[] permissions) {
                                Intent camera = IntentFactory.getCamera(cameraFile);
                                ActivityTrans.startResult(activity, camera, ConsHelper.REQUEST_CAMERA);
                            }

                            @Override
                            public void onPermissionDenied(int requestCode, String[] permissions) {
                                DialogHelper.showGoPermDialog(activity);
                            }
                        });
                        break;
                    case R.id.llCancel:
                        PopUtils.dismiss(window);
                        ResHelper.deleteFileInBackground(cameraFile);
                        break;
                }
            }
        };
        RelativeLayout root = view.findViewById(R.id.root);
        TextView tvShow = view.findViewById(R.id.tvShow);
        LinearLayout llAlbum = view.findViewById(R.id.llPicture);
        LinearLayout llCamera = view.findViewById(R.id.llCamera);
        LinearLayout llCancel = view.findViewById(R.id.llCancel);
        root.setOnClickListener(listener);
        tvShow.setText(show);
        llAlbum.setOnClickListener(listener);
        llCamera.setOnClickListener(listener);
        llCancel.setOnClickListener(listener);
        return window;
    }
}