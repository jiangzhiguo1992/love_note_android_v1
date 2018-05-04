package com.jiangzg.mianmian.activity.book;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Album;

public class PictureListActivity extends BaseActivity<PictureListActivity> {

    public static void goActivity(Activity from, Album album) {
        Intent intent = new Intent(from, PictureListActivity.class);
        intent.putExtra("album", album);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_picture_list;
    }

    @Override
    protected void initView(Bundle state) {
        // TODO 相册详情，顶部封面+详细信息(创建+更新+张数+我的+你的)，可压缩toolbar，照片列表背景是封面的虚化，且是瀑布流
    }

    @Override
    protected void initData(Bundle state) {

    }

}
