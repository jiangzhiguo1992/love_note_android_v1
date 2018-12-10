package com.jiangzg.lovenote.helper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jiangzg.base.common.ConvertUtils;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.lovenote.R;

import java.util.Random;

/**
 * Created by JiangZhiGuo on 2016-10-31.
 * describe 符合项目样式的View管理类
 */
public class ViewHelper {

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
        int colors[] = new int[]{R.color.theme_red_primary, R.color.theme_pink_primary,
                R.color.theme_purple_primary, R.color.theme_indigo_primary, R.color.theme_blue_primary,
                R.color.theme_teal_primary, R.color.theme_green_primary, R.color.theme_yellow_primary,
                R.color.theme_orange_primary, R.color.theme_brown_primary, R.color.theme_grey_primary,
        };
        Random random = new Random();
        int nextInt = random.nextInt(colors.length);
        return colors[nextInt];
    }

    // getWrapTextView
    public static View getWrapTextView(Context context, String show) {
        if (context == null || StringUtils.isEmpty(show)) {
            LogUtils.w(ViewHelper.class, "getWrapTextView", "context == null || show == null");
            return null;
        }
        int dp7 = ConvertUtils.dp2px(7);
        int dp5 = ConvertUtils.dp2px(5);
        int dp2 = ConvertUtils.dp2px(2);
        FrameLayout.LayoutParams mTextLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mTextLayoutParams.setMarginEnd(dp7);
        TextView textView = new TextView(context);
        textView.setLayoutParams(mTextLayoutParams);
        textView.setBackgroundResource(R.drawable.shape_solid_primary_r2);
        //textView.setBackgroundResource(resId);
        textView.setPadding(dp5, dp2, dp5, dp2);
        textView.setGravity(Gravity.CENTER);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textView.setTextAppearance(R.style.FontWhiteSmall);
        } else {
            textView.setTextAppearance(context, R.style.FontWhiteSmall);
        }
        textView.setText(show);
        return textView;
    }

    //@SuppressLint("InflateParams")
    //public static PopupWindow createPictureCameraPop(final Activity activity, final File cameraFile) {
    //    View view = LayoutInflater.from(activity).inflate(R.layout.pop_select_img_from_picture_camera, null);
    //    final PopupWindow window = PopUtils.createWindow(view);
    //    View.OnClickListener listener = new View.OnClickListener() {
    //        @Override
    //        public void onClick(View v) {
    //            switch (v.getId()) {
    //                case R.id.root:
    //                    PopUtils.dismiss(window);
    //                    ResHelper.deleteFileInBackground(cameraFile);
    //                    break;
    //                case R.id.llPicture:
    //                    PopUtils.dismiss(window);
    //                    PermUtils.requestPermissions(activity, ConsHelper.REQUEST_APP_INFO, PermUtils.appInfo, new PermUtils.OnPermissionListener() {
    //                        @Override
    //                        public void onPermissionGranted(int requestCode, String[] permissions) {
    //                            ResHelper.deleteFileInBackground(cameraFile);
    //                            Intent picture = IntentFactory.getPicture();
    //                            ActivityTrans.startResult(activity, picture, ConsHelper.REQUEST_PICTURE);
    //                        }
    //
    //                        @Override
    //                        public void onPermissionDenied(int requestCode, String[] permissions) {
    //                            //ResHelper.deleteFileInBackground(cameraFile); 这里的权限也肯定不会同意
    //                            DialogHelper.showGoPermDialog(activity);
    //                        }
    //                    });
    //                    break;
    //                case R.id.llCamera:
    //                    PopUtils.dismiss(window);
    //                    PermUtils.requestPermissions(activity, ConsHelper.REQUEST_CAMERA, PermUtils.camera, new PermUtils.OnPermissionListener() {
    //                        @Override
    //                        public void onPermissionGranted(int requestCode, String[] permissions) {
    //                            Intent camera = IntentFactory.getCamera(ResHelper.PROVIDER_AUTH, cameraFile);
    //                            ActivityTrans.startResult(activity, camera, ConsHelper.REQUEST_CAMERA);
    //                        }
    //
    //                        @Override
    //                        public void onPermissionDenied(int requestCode, String[] permissions) {
    //                            DialogHelper.showGoPermDialog(activity);
    //                        }
    //                    });
    //                    break;
    //                case R.id.llCancel:
    //                    PopUtils.dismiss(window);
    //                    ResHelper.deleteFileInBackground(cameraFile);
    //                    break;
    //            }
    //        }
    //    };
    //    RelativeLayout root = view.findViewById(R.id.root);
    //    LinearLayout llAlbum = view.findViewById(R.id.llPicture);
    //    LinearLayout llCamera = view.findViewById(R.id.llCamera);
    //    LinearLayout llCancel = view.findViewById(R.id.llCancel);
    //    root.setOnClickListener(listener);
    //    llAlbum.setOnClickListener(listener);
    //    llCamera.setOnClickListener(listener);
    //    llCancel.setOnClickListener(listener);
    //    return window;
    //}
    //
    //@SuppressLint("InflateParams")
    //public static PopupWindow createPictureCameraPop(final Activity activity, String show, final File cameraFile) {
    //    View view = LayoutInflater.from(activity).inflate(R.layout.pop_select_img_show_from_picture_camera, null);
    //    final PopupWindow window = PopUtils.createWindow(view);
    //    View.OnClickListener listener = new View.OnClickListener() {
    //        @Override
    //        public void onClick(View v) {
    //            switch (v.getId()) {
    //                case R.id.root:
    //                    PopUtils.dismiss(window);
    //                    ResHelper.deleteFileInBackground(cameraFile);
    //                    break;
    //                case R.id.llPicture:
    //                    PopUtils.dismiss(window);
    //                    PermUtils.requestPermissions(activity, ConsHelper.REQUEST_APP_INFO, PermUtils.appInfo, new PermUtils.OnPermissionListener() {
    //                        @Override
    //                        public void onPermissionGranted(int requestCode, String[] permissions) {
    //                            ResHelper.deleteFileInBackground(cameraFile);
    //                            Intent picture = IntentFactory.getPicture();
    //                            ActivityTrans.startResult(activity, picture, ConsHelper.REQUEST_PICTURE);
    //                        }
    //
    //                        @Override
    //                        public void onPermissionDenied(int requestCode, String[] permissions) {
    //                            //ResHelper.deleteFileInBackground(cameraFile); 这里的权限也肯定不会同意
    //                            DialogHelper.showGoPermDialog(activity);
    //                        }
    //                    });
    //                    break;
    //                case R.id.llCamera:
    //                    PopUtils.dismiss(window);
    //                    PermUtils.requestPermissions(activity, ConsHelper.REQUEST_CAMERA, PermUtils.camera, new PermUtils.OnPermissionListener() {
    //                        @Override
    //                        public void onPermissionGranted(int requestCode, String[] permissions) {
    //                            Intent camera = IntentFactory.getCamera(ResHelper.PROVIDER_AUTH, cameraFile);
    //                            ActivityTrans.startResult(activity, camera, ConsHelper.REQUEST_CAMERA);
    //                        }
    //
    //                        @Override
    //                        public void onPermissionDenied(int requestCode, String[] permissions) {
    //                            DialogHelper.showGoPermDialog(activity);
    //                        }
    //                    });
    //                    break;
    //                case R.id.llCancel:
    //                    PopUtils.dismiss(window);
    //                    ResHelper.deleteFileInBackground(cameraFile);
    //                    break;
    //            }
    //        }
    //    };
    //    RelativeLayout root = view.findViewById(R.id.root);
    //    TextView tvShow = view.findViewById(R.id.tvShow);
    //    LinearLayout llAlbum = view.findViewById(R.id.llPicture);
    //    LinearLayout llCamera = view.findViewById(R.id.llCamera);
    //    LinearLayout llCancel = view.findViewById(R.id.llCancel);
    //    root.setOnClickListener(listener);
    //    tvShow.setText(show);
    //    llAlbum.setOnClickListener(listener);
    //    llCamera.setOnClickListener(listener);
    //    llCancel.setOnClickListener(listener);
    //    return window;
    //}

}