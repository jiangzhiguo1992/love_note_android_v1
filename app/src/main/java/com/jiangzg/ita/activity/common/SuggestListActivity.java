package com.jiangzg.ita.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.jiangzg.base.component.activity.ActivityTrans;
import com.jiangzg.ita.R;
import com.jiangzg.ita.adapter.HelpAdapter;
import com.jiangzg.ita.adapter.SuggestAdapter;
import com.jiangzg.ita.base.BaseActivity;
import com.jiangzg.ita.domain.Suggest;
import com.jiangzg.ita.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class SuggestListActivity extends BaseActivity<SuggestListActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.rv)
    RecyclerView rv;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, SuggestListActivity.class);
        // intent.putExtra();
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_suggest_list;
    }

    @Override
    protected void initView(Bundle state) {
        ViewUtils.initTopBar(mActivity, tb, getString(R.string.suggest_feedback), true);
        // recycler
        rv.setLayoutManager(new LinearLayoutManager(mActivity));
        SuggestAdapter adapter = new SuggestAdapter(mActivity);
        rv.setAdapter(adapter);

        // todo behavior
    }

    @Override
    protected void initData(Bundle state) {
        getData();
    }


    public void getData() {
        List<Suggest> suggestList = new ArrayList<>();
        Suggest s1 = new Suggest();
        s1.setTitle("我发现了一个bug！");
        s1.setCreatedAt(1520866299);
        s1.setUpdatedAt(1520866299);
        //s1.setContent();
        s1.setWatch(false);
        s1.setComment(false);
        s1.setWatchCount(0);
        s1.setCommentCount(0);
        Suggest s2 = new Suggest();
        s2.setTitle("我发现了一个bug！我发现了一个bug！我发现了一个bug！我发现了一个bug！");
        s2.setCreatedAt(1520010299);
        s2.setUpdatedAt(1520866299);
        //s2.setContent();
        s2.setWatch(true);
        s2.setComment(false);
        s2.setWatchCount(111111111);
        s2.setCommentCount(0);
        Suggest s3 = new Suggest();
        s3.setTitle("我发现了一个bug！我发现了一个bug！我发现了一个bug！我发现了一个bug！我发现了一个bug！我发现了一个bug！我发现了一个bug！我发现了一个bug！我发现了一个bug！我发现了一个bug！我发现了一个bug！我发现了一个bug！我发现了一个bug！我发现了一个bug！");
        s3.setCreatedAt(1520010299);
        s3.setUpdatedAt(1520010299);
        //s3.setContent();
        s3.setWatch(true);
        s3.setComment(true);
        s3.setWatchCount(111111111);
        s3.setCommentCount(2);
        suggestList.add(s1);
        suggestList.add(s2);
        suggestList.add(s3);
        suggestList.add(s1);
        suggestList.add(s2);
        suggestList.add(s3);
        suggestList.add(s1);
        suggestList.add(s2);
        suggestList.add(s3);
        // todo api
        SuggestAdapter adapter = (SuggestAdapter) rv.getAdapter();
        adapter.setData(suggestList);
    }
}
