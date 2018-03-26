package com.jiangzg.ita.activity.couple;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.jiangzg.base.component.activity.ActivityTrans;
import com.jiangzg.ita.R;
import com.jiangzg.ita.adapter.WallPaperAdapter;
import com.jiangzg.ita.base.BaseActivity;
import com.jiangzg.ita.base.MyApp;
import com.jiangzg.ita.domain.WallPaper;
import com.jiangzg.ita.third.RecyclerManager;
import com.jiangzg.ita.utils.ViewUtils;
import com.jiangzg.ita.view.GImageView;
import com.jiangzg.ita.view.GSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class WallPaperActivity extends BaseActivity<WallPaperActivity> {

    @BindView(R.id.root)
    LinearLayout root;
    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.rv)
    RecyclerView rv;

    private RecyclerManager recyclerManager;

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
        ViewUtils.initTopBar(mActivity, tb, getString(R.string.wall_paper), true);
        // recycler
        recyclerManager = new RecyclerManager(mActivity)
                .initRecycler(rv)
                .initLayoutManager(new GridLayoutManager(mActivity, 3, LinearLayoutManager.VERTICAL, false))
                .initRefresh(srl, false)
                .initAdapter(new WallPaperAdapter(mActivity))
                .listenerRefresh(new RecyclerManager.RefreshListener() {
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
                        if (wallPaperAdapter.isAddItem(position)) {
                            wallPaperAdapter.showAddPop(root);
                        } else {
                            wallPaperAdapter.goImgScreen(position, ivWallPaper);
                        }
                    }
                })
                .listenerClick(new OnItemLongClickListener() {
                    @Override
                    public void onSimpleItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                        WallPaperAdapter wallPaperAdapter = (WallPaperAdapter) adapter;
                        if (wallPaperAdapter.isAddItem(position)) {
                            wallPaperAdapter.showDeleteDialog(position);
                        }
                    }
                });
    }

    @Override
    protected void initData(Bundle state) {
        recyclerManager.dataRefresh();
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
                //list.add("http://img3.imgtn.bdimg.com/it/u=419152308,4151393995&fm=27&gp=0.jpg");
                //list.add("http://img0.imgtn.bdimg.com/it/u=1745541687,3814417807&fm=200&gp=0.jpg");
                //list.add("http://img4.imgtn.bdimg.com/it/u=2818615775,2778183477&fm=27&gp=0.jpg");
                list.add(""); // 有一个占位的
                wallPaper.setImageList(list);

                recyclerManager.dataNew(list);
            }

        }, 1000);
    }
    //
    //private void initFoot() {
    //    foot = LayoutInflater.from(mActivity).inflate(R.layout.list_foot_wall_paper, rv, false);
    //    GImageView ivAdd = foot.findViewById(R.id.ivAdd);
    //    float screenWidth = ScreenUtils.getScreenWidth(mActivity);
    //    float screenHeight = ScreenUtils.getScreenHeight(mActivity);
    //    ivAdd.setAspectRatio(screenWidth / screenHeight);
    //    ivAdd.setDataRes(R.drawable.ic_add_grey);
    //}

}
