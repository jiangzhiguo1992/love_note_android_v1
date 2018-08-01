package com.jiangzg.mianmian.activity.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.adapter.HelpContentAdapter;
import com.jiangzg.mianmian.adapter.HelpSubAdapter;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Help;
import com.jiangzg.mianmian.helper.RecyclerHelper;
import com.jiangzg.mianmian.helper.ViewHelper;

import java.util.List;

import butterknife.BindView;

public class HelpActivity extends BaseActivity<HelpActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.rv)
    RecyclerView rv;

    private RecyclerHelper recyclerHelper;
    private RecyclerHelper recyclerHelperHead;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, HelpActivity.class);
        intent.putExtra("index", Help.INDEX_ALL);
        //intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from, int index) {
        Intent intent = new Intent(from, HelpActivity.class);
        intent.putExtra("index", index);
        //intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Fragment from, int index) {
        Intent intent = new Intent(from.getActivity(), HelpActivity.class);
        intent.putExtra("index", index);
        //intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_help;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.help_document), true);
        // recycler
        recyclerHelper = new RecyclerHelper(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initAdapter(new HelpSubAdapter())
                .viewHeader(mActivity, R.layout.list_head_help)
                .listenerClick(new OnItemClickListener() {
                    @Override
                    public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                        HelpSubAdapter helpSubAdapter = (HelpSubAdapter) adapter;
                        helpSubAdapter.goSubHelp(mActivity, position);
                    }
                });
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        int index = intent.getIntExtra("index", Help.INDEX_ALL);
        Help help = getHelp(index);
        refreshView(help);
    }

    @Override
    protected void onFinish(Bundle state) {
        RecyclerHelper.release(recyclerHelper);
        RecyclerHelper.release(recyclerHelperHead);
    }

    private void refreshView(Help help) {
        if (recyclerHelper == null) return;
        if (help == null) {
            recyclerHelper.dataFail(false, "没有找到相关帮助文档");
            return;
        }
        tb.setTitle(help.getTitle());
        initHead(help);
        recyclerHelper.dataNew(help.getSubList(), 0);
    }

    private void initHead(Help help) {
        if (recyclerHelper == null) return;
        // data
        String desc = help.getDesc();
        List<Help.HelpContent> contentList = help.getContentList();
        // view
        View head = recyclerHelper.getViewHead();
        // desc
        TextView tvDesc = head.findViewById(R.id.tvDesc);
        tvDesc.setText(desc);
        // content
        RecyclerView rv = head.findViewById(R.id.rv);
        if (contentList == null || contentList.size() <= 0) {
            rv.setVisibility(View.GONE);
        } else {
            rv.setVisibility(View.VISIBLE);
            if (recyclerHelperHead == null) {
                recyclerHelperHead = new RecyclerHelper(rv)
                        .initLayoutManager(new LinearLayoutManager(mActivity))
                        .initAdapter(new HelpContentAdapter());
            }
            recyclerHelperHead.dataNew(contentList, 0);
        }
    }

    // TODO
    private Help getHelp(int index) {
        Help help = new Help();

        // no_data_help = 没有找到相关帮助文档
        //help_root_title = 帮助文档
        //help_root_desc = 帮助文档有助于帮助大家更快的理解这个app的玩法，有不明白的地方，就来这里找找答案吧
        //        help_root_q1 = 怎么注册账号？
        //help_root_a1 = 请跳转注册页面
        //help_root_q2 = 怎么登录账号？
        //help_root_a2 = 注册完之后才能登录哦\n具体请见登录页面
        //        help_root_q3 = 密码忘了怎么办？
        //help_root_a3 = 密码忘了没关系，只要手机还在，密码就能找回，账号就不会丢
        //        help_couple_home_title = 情侣配对
        //help_couple_home_desc = 快来和心仪的TA配对吧
        //help_note_home_title = 小本本
        //help_note_home_desc = 曾经的很多事我都没有忘记，只是暂时想不起来

        return help;
    }

}
