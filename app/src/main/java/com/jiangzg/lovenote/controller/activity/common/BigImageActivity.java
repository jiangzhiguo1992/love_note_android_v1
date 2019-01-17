package com.jiangzg.lovenote.controller.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jiangzg.base.common.FileUtils;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.component.BroadcastUtils;
import com.jiangzg.base.system.PermUtils;
import com.jiangzg.base.view.BarUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.controller.adapter.common.BigImagePagerAdapter;
import com.jiangzg.lovenote.helper.common.OssHelper;
import com.jiangzg.lovenote.helper.common.OssResHelper;
import com.jiangzg.lovenote.helper.common.ResHelper;
import com.jiangzg.lovenote.helper.view.DialogHelper;
import com.jiangzg.lovenote.main.MyApp;
import com.jiangzg.lovenote.view.TryCacheViewPager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;

public class BigImageActivity extends BaseActivity<BigImageActivity> {

    public static final int TYPE_OSS_SINGLE = 0;
    public static final int TYPE_FILE_SINGLE = 1;
    public static final int TYPE_OSS_LIST = 2;
    public static final int TYPE_FILE_LIST = 3;

    @BindView(R.id.root)
    RelativeLayout root;
    @BindView(R.id.vpImage)
    TryCacheViewPager vpImage;
    @BindView(R.id.vTop)
    View vTop;
    @BindView(R.id.llBottom)
    LinearLayout llBottom;
    @BindView(R.id.vBottom)
    View vBottom;
    @BindView(R.id.tvIndex)
    TextView tvIndex;
    @BindView(R.id.ivShare)
    ImageView ivShare;
    @BindView(R.id.ivDownload)
    ImageView ivDownload;

    private boolean screenShow = false;
    private int type;
    private List<String> dataList;

    public static void goActivityByOss(Activity from, String ossPath, SimpleDraweeView view) {
        if (StringUtils.isEmpty(ossPath)) return;
        Intent intent = new Intent(from, BigImageActivity.class);
        intent.putExtra("type", TYPE_OSS_SINGLE);
        intent.putExtra("imgOss", ossPath);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //Pair<View, String> share = Pair.create((View) view, "imgAnim");
        //ActivityTrans.start(from, intent, share);
        ActivityTrans.start(from, intent);
    }

    public static void goActivityByFile(Activity from, String filePath, SimpleDraweeView view) {
        if (StringUtils.isEmpty(filePath)) return;
        Intent intent = new Intent(from, BigImageActivity.class);
        intent.putExtra("type", TYPE_FILE_SINGLE);
        intent.putExtra("imgFile", filePath);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //Pair<View, String> share = Pair.create((View) view, "imgAnim");
        //ActivityTrans.start(from, intent, share);
        ActivityTrans.start(from, intent);
    }

    public static void goActivityByOssList(Activity from, ArrayList<String> ossPathList, int startIndex, SimpleDraweeView view) {
        if (ossPathList == null || ossPathList.size() <= 0) return;
        Intent intent = new Intent(from, BigImageActivity.class);
        intent.putExtra("type", TYPE_OSS_LIST);
        intent.putStringArrayListExtra("imgOssList", ossPathList);
        intent.putExtra("showIndex", startIndex);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //Pair<View, String> share = Pair.create((View) view, "imgAnim");
        //ActivityTrans.start(from, intent, share);
        ActivityTrans.start(from, intent);
    }

    public static void goActivityByFileList(Activity from, ArrayList<String> filePathList, int startIndex, SimpleDraweeView view) {
        if (filePathList == null || filePathList.size() <= 0) return;
        Intent intent = new Intent(from, BigImageActivity.class);
        intent.putExtra("type", TYPE_OSS_LIST);
        intent.putStringArrayListExtra("imgFileList", filePathList);
        intent.putExtra("showIndex", startIndex);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //Pair<View, String> share = Pair.create((View) view, "imgAnim");
        //ActivityTrans.start(from, intent, share);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        dataList = new ArrayList<>();
        BarUtils.setStatusBarTrans(mActivity, true);
        BarUtils.setNavigationBarTrans(mActivity, true);
        return R.layout.activity_big_image;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        // type
        type = intent.getIntExtra("type", TYPE_OSS_SINGLE);
        // anim
        //ViewCompat.setTransitionName(vpImage, "imgAnim");
        // navigation
        initBottom();
        // adapter
        initViewPager();
        // index
        changeIndex();
        // view
        toggleScreenView();
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        screenShow = false;
    }

    @Override
    protected void onFinish(Bundle state) {
    }

    @OnClick({R.id.ivDownload})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivDownload: // 下载
                downLoad();
                break;
        }
    }

    private void initBottom() {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) vBottom.getLayoutParams();
        layoutParams.height = BarUtils.getNavigationBarHeight(mActivity);
        vBottom.setLayoutParams(layoutParams);
    }

    private void initViewPager() {
        int showIndex = getIntent().getIntExtra("showIndex", 0);
        BigImagePagerAdapter adapter = new BigImagePagerAdapter(mActivity, type, (view, x, y) -> toggleScreenView());
        // 根据type设置数据
        switch (type) {
            case TYPE_FILE_LIST:
                List<String> imgFileList = getIntent().getStringArrayListExtra("imgFileList");
                dataList.addAll(imgFileList == null ? new ArrayList<>() : imgFileList);
                adapter.setData(dataList);
                break;
            case TYPE_OSS_LIST:
                List<String> imgOssList = getIntent().getStringArrayListExtra("imgOssList");
                dataList.addAll(imgOssList == null ? new ArrayList<>() : imgOssList);
                adapter.setData(dataList);
                break;
            case TYPE_FILE_SINGLE:
                String imgFile = getIntent().getStringExtra("imgFile");
                dataList.add(imgFile);
                adapter.setData(imgFile);
                break;
            case TYPE_OSS_SINGLE:
            default:
                String imgOss = getIntent().getStringExtra("imgOss");
                dataList.add(imgOss);
                adapter.setData(imgOss);
                break;
        }
        vpImage.setAdapter(adapter);
        vpImage.setCurrentItem(showIndex, false);
        // 下标变换监听
        vpImage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                changeIndex();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    // 下标切换
    private void changeIndex() {
        int currentItem = vpImage.getCurrentItem();
        switch (type) {
            case TYPE_FILE_LIST:
            case TYPE_OSS_LIST:
                String format = String.format(Locale.getDefault(), mActivity.getString(R.string.holder_sprit_holder), currentItem + 1, dataList.size());
                tvIndex.setText(format);
                break;
            case TYPE_FILE_SINGLE:
            case TYPE_OSS_SINGLE:
                // 不显示
            default:
                break;
        }
    }

    // 点击切换界面
    private void toggleScreenView() {
        screenShow = !screenShow;
        vTop.setVisibility(screenShow ? View.VISIBLE : View.GONE);
        llBottom.setVisibility(screenShow ? View.VISIBLE : View.GONE);
        // 本地文件没有下载和分享
        if (screenShow && (type == TYPE_FILE_LIST || type == TYPE_FILE_SINGLE)) {
            ivShare.setVisibility(View.GONE);
            ivDownload.setVisibility(View.GONE);
        }
        // statusBar
        if (screenShow) {
            BarUtils.showStatus(root);
        } else {
            BarUtils.hideStatus(root);
        }
    }

    // 图片下载
    private void downLoad() {
        // 本地文件不下载
        if (type == TYPE_FILE_SINGLE || type == TYPE_FILE_LIST) return;
        // 获取地址并下载
        String objectKey = dataList.get(vpImage.getCurrentItem());
        downloadBigImage(mActivity, objectKey);
    }

    // 下载任务
    private void downloadBigImage(final Activity activity, final String objectKey) {
        if (StringUtils.isEmpty(objectKey)) {
            LogUtils.w(BigImageActivity.class, "downloadBigImage", "下载文件不存在！");
            ToastUtils.show(MyApp.get().getString(R.string.download_file_no_exists));
            return;
        }
        // 权限检查
        PermUtils.requestPermissions(activity, BaseActivity.REQUEST_APP_INFO, PermUtils.appInfo, new PermUtils.OnPermissionListener() {
            @Override
            public void onPermissionGranted(int requestCode, String[] permissions) {
                final File target = ResHelper.newImageDownLoadFile(objectKey);
                // 目标文件创建检查
                if (target == null) {
                    ToastUtils.show(MyApp.get().getString(R.string.file_create_fail));
                    return;
                }
                // 目标文件重复检查
                String format = MyApp.get().getString(R.string.already_download_to_colon_holder);
                final String sucToast = String.format(Locale.getDefault(), format, target.getAbsoluteFile());
                if (FileUtils.isFileExists(target) && target.length() > 0) {
                    LogUtils.d(BigImageActivity.class, "downloadBigImage", "下载文件已存在！");
                    ToastUtils.show(sucToast);
                    return;
                } else {
                    FileUtils.deleteFile(target); // 清除空文件
                }
                // 在OssRes中是否存在(note之类的缓存)
                if (OssResHelper.isKeyFileExists(objectKey)) {
                    File source = OssResHelper.newKeyFile(objectKey);
                    boolean copy = FileUtils.copyOrMoveFile(source, target, false);
                    if (copy) {
                        LogUtils.d(BigImageActivity.class, "downloadBigImage", "文件复制成功！");
                        BroadcastUtils.refreshMediaImageInsert(ResHelper.getFileProviderAuth(), target);
                        ToastUtils.show(sucToast);
                        return;
                    }
                    LogUtils.w(BigImageActivity.class, "downloadBigImage", "文件复制失败！");
                } else {
                    LogUtils.d(BigImageActivity.class, "downloadBigImage", "下载本地没有的文件");
                }
                // 开始下载
                OssHelper.downloadFileInBackground(objectKey, target, true, new OssHelper.OssDownloadCallBack() {
                    @Override
                    public void success(String ossKey, File target) {
                        ToastUtils.show(sucToast);
                        // 下载完通知图库媒体
                        BroadcastUtils.refreshMediaImageInsert(ResHelper.getFileProviderAuth(), target);
                    }

                    @Override
                    public void failure(String ossKey, String errMsg) {
                    }
                });
            }

            @Override
            public void onPermissionDenied(int requestCode, String[] permissions) {
                DialogHelper.showGoPermDialog(activity);
            }
        });
    }

}
