package com.jiangzg.ita.activity.couple;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.jiangzg.base.component.activity.ActivityTrans;
import com.jiangzg.base.file.FileUtils;
import com.jiangzg.base.media.image.BitmapMedia;
import com.jiangzg.base.view.PopUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.ita.R;
import com.jiangzg.ita.activity.common.HelpActivity;
import com.jiangzg.ita.adapter.WallPaperAdapter;
import com.jiangzg.ita.base.BaseActivity;
import com.jiangzg.ita.base.MyApp;
import com.jiangzg.ita.domain.Help;
import com.jiangzg.ita.domain.WallPaper;
import com.jiangzg.ita.helper.ConsHelper;
import com.jiangzg.ita.helper.PopHelper;
import com.jiangzg.ita.helper.ResHelper;
import com.jiangzg.ita.helper.ViewHelper;
import com.jiangzg.ita.third.LuBanUtils;
import com.jiangzg.ita.third.RecyclerHelper;
import com.jiangzg.ita.view.GImageView;
import com.jiangzg.ita.view.GSwipeRefreshLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import top.zibin.luban.OnCompressListener;

public class WallPaperActivity extends BaseActivity<WallPaperActivity> {

    @BindView(R.id.root)
    LinearLayout root;
    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.rv)
    RecyclerView rv;

    private RecyclerHelper recyclerHelper;
    private File cameraFile;
    private PopupWindow popupWindow;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, WallPaperActivity.class);
        // intent.putExtra();
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_wall_paper;
    }

    @Override
    protected void initView(Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.wall_paper), true);
        // recycler
        recyclerHelper = new RecyclerHelper(mActivity)
                .initRecycler(rv)
                .initLayoutManager(new GridLayoutManager(mActivity, 3, LinearLayoutManager.VERTICAL, false))
                .initRefresh(srl, false)
                .initAdapter(new WallPaperAdapter(mActivity))
                .listenerRefresh(new RecyclerHelper.RefreshListener() {
                    @Override
                    public void onRefresh() {
                        getData();
                    }
                })
                .listenerClick(new OnItemClickListener() {
                    @Override
                    public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                        WallPaperAdapter wallPaperAdapter = (WallPaperAdapter) adapter;
                        GImageView ivWallPaper = view.findViewById(R.id.ivWallPaper);
                        wallPaperAdapter.goImgScreen(position, ivWallPaper);
                    }
                })
                .listenerClick(new OnItemLongClickListener() {
                    @Override
                    public void onSimpleItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                        WallPaperAdapter wallPaperAdapter = (WallPaperAdapter) adapter;
                        wallPaperAdapter.showDeleteDialog(position);
                    }
                });
        // menu
        tb.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menuHelp: // 评论
                        HelpActivity.goActivity(mActivity, Help.TYPE_WALL_PAPER_ADD);
                        break;
                    case R.id.menuAdd: // 添加
                        if (cameraFile == null) {
                            cameraFile = ResHelper.createJPEGInFiles();
                        }
                        showAddPop();
                        break;
                }
                return true;
            }
        });
    }

    @Override
    protected void initData(Bundle state) {
        recyclerHelper.dataRefresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help_add, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        if (requestCode == ConsHelper.REQUEST_BOOK_PICTURE) {
            pushData();
        } else if (requestCode == ConsHelper.REQUEST_PICTURE) {
            File pictureFile = BitmapMedia.getPictureFile(data);
            compressFile(pictureFile);
        } else if (requestCode == ConsHelper.REQUEST_CAMERA) {
            compressFile(cameraFile);
        }
    }

    private void getData() {
        // todo api
        MyApp.get().getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                WallPaper wallPaper = new WallPaper();
                List<String> list = new ArrayList<>();
                list.add("http://img4.imgtn.bdimg.com/it/u=169060927,1884911313&fm=27&gp=0.jpg");
                list.add("http://img0.imgtn.bdimg.com/it/u=1369711964,515430964&fm=200&gp=0.jpg");
                list.add("http://img4.imgtn.bdimg.com/it/u=2879331571,3181036945&fm=200&gp=0.jpg");
                list.add("http://img3.imgtn.bdimg.com/it/u=1489756078,3073908939&fm=27&gp=0.jpg");
                list.add("http://img1.imgtn.bdimg.com/it/u=2800834159,1959441958&fm=27&gp=0.jpg");
                list.add("http://img4.imgtn.bdimg.com/it/u=2324935939,3139070251&fm=27&gp=0.jpg");
                list.add("http://img3.imgtn.bdimg.com/it/u=419152308,4151393995&fm=27&gp=0.jpg");
                list.add("http://img0.imgtn.bdimg.com/it/u=1745541687,3814417807&fm=200&gp=0.jpg");
                list.add("http://img4.imgtn.bdimg.com/it/u=2818615775,2778183477&fm=27&gp=0.jpg");
                // 位的
                wallPaper.setImageList(list);

                recyclerHelper.dataNew(list);
            }

        }, 1000);
    }

    public void showAddPop() {
        if (popupWindow == null) {
            popupWindow = PopHelper.createBookAlbumCamera(mActivity, cameraFile);
        }
        PopUtils.show(popupWindow, root);
    }

    private void compressFile(final File original) {
        if (FileUtils.isFileEmpty(original)) {
            ToastUtils.show(getString(R.string.image_get_error));
            return;
        }
        LuBanUtils.compress(mActivity, original, new OnCompressListener() {
            @Override
            public void onStart() {
                getLoading(getString(R.string.image_is_compress), false, null).show();
            }

            @Override
            public void onSuccess(File file) {
                FileUtils.deleteFile(original);
                uploadOss(file);
            }

            @Override
            public void onError(Throwable e) {
                getLoading().dismiss();
                ToastUtils.show(getString(R.string.image_compress_fail));
                FileUtils.deleteFile(original);
            }
        });
    }

    private void uploadOss(File jpeg) {
        getLoading("正在上传中", true, null).show();
        // todo oss
        pushData();
    }

    private void pushData() {
        // todo api
        // todo refresh data
    }

}
