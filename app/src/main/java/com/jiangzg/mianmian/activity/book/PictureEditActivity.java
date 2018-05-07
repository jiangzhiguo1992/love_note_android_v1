package com.jiangzg.mianmian.activity.book;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Picture;

import butterknife.BindView;
import butterknife.OnClick;

public class PictureEditActivity extends BaseActivity<PictureEditActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.cvAlbum)
    CardView cvAlbum;
    @BindView(R.id.tvAlbum)
    TextView tvAlbum;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.cvHappenAt)
    CardView cvHappenAt;
    @BindView(R.id.tvHappenAt)
    TextView tvHappenAt;
    @BindView(R.id.cvLocation)
    CardView cvLocation;
    @BindView(R.id.tvLocation)
    TextView tvLocation;
    @BindView(R.id.btnCommit)
    Button btnCommit;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, PictureEditActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from, Picture picture) {
        Intent intent = new Intent(from, PictureEditActivity.class);
        intent.putExtra("picture", picture);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_picture_edit;
    }

    @Override
    protected void initView(Bundle state) {

    }

    @Override
    protected void initData(Bundle state) {

    }

    @OnClick({R.id.cvAlbum, R.id.cvHappenAt, R.id.cvLocation, R.id.btnCommit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cvAlbum:
                break;
            case R.id.cvHappenAt:
                break;
            case R.id.cvLocation:
                break;
            case R.id.btnCommit:
                break;
        }
    }
}
