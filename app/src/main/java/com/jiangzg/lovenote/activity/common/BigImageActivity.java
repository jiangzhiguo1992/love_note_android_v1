package com.jiangzg.lovenote.activity.common;

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
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.BarUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.adapter.BigImagePagerAdapter;
import com.jiangzg.lovenote.base.BaseActivity;
import com.jiangzg.lovenote.helper.OssHelper;
import com.jiangzg.lovenote.view.TryCacheViewPager;

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
        Intent intent = new Intent(from, BigImageActivity.class);
        intent.putExtra("type", TYPE_OSS_SINGLE);
        intent.putExtra("imgOss", ossPath);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //Pair<View, String> share = Pair.create((View) view, "imgAnim");
        //ActivityTrans.start(from, intent, share);
        ActivityTrans.start(from, intent);
    }

    public static void goActivityByFile(Activity from, String filePath, SimpleDraweeView view) {
        Intent intent = new Intent(from, BigImageActivity.class);
        intent.putExtra("type", TYPE_FILE_SINGLE);
        intent.putExtra("imgFile", filePath);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //Pair<View, String> share = Pair.create((View) view, "imgAnim");
        //ActivityTrans.start(from, intent, share);
        ActivityTrans.start(from, intent);
    }

    public static void goActivityByOssList(Activity from, ArrayList<String> ossPathList, int startIndex, SimpleDraweeView view) {
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
        OssHelper.downloadBigImage(mActivity, objectKey);
    }

}
