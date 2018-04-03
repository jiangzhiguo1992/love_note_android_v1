package com.jiangzg.ita.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.ita.R;
import com.jiangzg.ita.adapter.HelpAdapter;
import com.jiangzg.ita.adapter.HelpContentAdapter;
import com.jiangzg.ita.base.BaseActivity;
import com.jiangzg.ita.domain.Help;
import com.jiangzg.ita.helper.ViewHelper;
import com.jiangzg.ita.helper.RecyclerHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class HelpActivity extends BaseActivity<HelpActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.rv)
    RecyclerView rv;

    private int type;
    private RecyclerHelper recyclerHelper;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, HelpActivity.class);
        intent.putExtra("type", Help.TYPE_ALL);
        //intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from, int type) {
        Intent intent = new Intent(from, HelpActivity.class);
        intent.putExtra("type", type);
        //intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_help;
    }

    @Override
    protected void initView(Bundle state) {
        type = getIntent().getIntExtra("type", Help.TYPE_ALL);
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.help_document), true);
        // recycler
        recyclerHelper = new RecyclerHelper(mActivity)
                .initRecycler(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initAdapter(new HelpAdapter())
                .setAdapter()
                .listenerClick(new OnItemClickListener() {
                    @Override
                    public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                        HelpAdapter helpAdapter = (HelpAdapter) adapter;
                        helpAdapter.goSubHelp(mActivity, position);
                    }
                });
    }

    @Override
    protected void initData(Bundle state) {
        getHelpData();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        type = getIntent().getIntExtra("type", Help.TYPE_ALL);
        getHelpData();
    }

    public void getHelpData() {
        Help help = new Help();
        help.setTitle("帮助文档");
        help.setDesc("帮助文档有助于帮助大家更快的理解这个app的玩法，有不明白的地方，就来这里找找答案吧！");
        List<Help.Content> contentList = new ArrayList<>();
        contentList.add(new Help.Content("怎么注册账号？", "请跳转注册页面"));
        contentList.add(new Help.Content("怎么登录账号？", "注册完之后才能登录哦\n具体请见登录页面"));
        contentList.add(new Help.Content("密码忘了怎么办？", "密码忘了没关系，只要手机还在，密码就能找回，账号就不会丢"));
        contentList.add(new Help.Content("手机丢了怎么办？", "亲~可以更换手机的哦\n，赶紧去设置吧"));
        help.setContentList(contentList);
        // subList
        Help sub1 = new Help();
        sub1.setTitle("1情侣首页？？？？");
        Help sub2 = new Help();
        sub2.setTitle("2情侣首页？？？？");
        Help sub3 = new Help();
        sub3.setTitle("3情侣首页？？？？");
        List<Help> list = new ArrayList<>();
        list.add(sub1);
        list.add(sub2);
        list.add(sub3);
        help.setSubList(list);
        // todo 拿着type去请求api

        tb.setTitle(help.getTitle());
        recyclerHelper.viewHeader(R.layout.list_head_help);
        initHead(help);
        recyclerHelper.dataNew(help.getSubList());
    }

    private void initHead(Help help) {
        // data
        String desc = help.getDesc();
        List<Help.Content> contentList = help.getContentList();
        // view
        View head = this.recyclerHelper.getViewHead();
        TextView tvDesc = head.findViewById(R.id.tvDesc);
        tvDesc.setText(desc);
        RecyclerView rv = head.findViewById(R.id.rv);
        // list
        RecyclerHelper recyclerHelper = new RecyclerHelper(mActivity)
                .initRecycler(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initAdapter(new HelpContentAdapter());
        recyclerHelper.dataNew(contentList);
    }

}
