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

import java.util.ArrayList;
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
        Help help = getHelpByIndex(index);
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

    // TODO 数据本地化 + strings整理
    private Help getHelpByIndex(int index) {
        Help help;
        switch (index) {
            case Help.INDEX_ALL:
            default:
                help = getHelpAll(Help.INDEX_ALL);
                break;
            case Help.INDEX_USER_SUGGEST_HOME:
                help = getHelpSuggestHome(Help.INDEX_USER_SUGGEST_HOME);
                break;
            case Help.INDEX_COUPLE_HOME:
                help = getHelpCoupleHome(Help.INDEX_COUPLE_HOME);
                break;
        }
        return help;
    }

    private Help getHelpAll(int index) {
        Help help = new Help();
        help.setIndex(index);
        help.setTitle(getString(R.string.help_document));
        help.setDesc("《" + getString(R.string.help_document) + "》旨在让大家快速理解这个app，如有不明白的地方，就来这里找找答案吧");
        // content
        List<Help.HelpContent> contentList = new ArrayList<>();
        Help.HelpContent c1 = new Help.HelpContent();
        c1.setQuestion("这是一个什么样的app？");
        c1.setAnswer("一款专注于情侣的生活记录和恋爱分享app，没有商城！没有商城！没有游戏！没有游戏！");
        contentList.add(c1);
        Help.HelpContent c2 = new Help.HelpContent();
        c2.setQuestion("我没有情侣，可以玩这个app吗？");
        c2.setAnswer("没有情侣的话，大半功能会受到限制，毕竟这款app是量身为情侣们打造的。" +
                "\n比如说《" + getString(R.string.nav_couple) + "》和《" + getString(R.string.nav_note) + "》模块，只有在本app配对之后，才能正常使用，但是像《" + getString(R.string.nav_topic) + "》之类的分享模块，是可以正常浏览的。");
        contentList.add(c2);
        Help.HelpContent c3 = new Help.HelpContent();
        c3.setQuestion("这个app只有办会员才能玩吗？");
        c3.setAnswer("并不是！！！" +
                "\napp中的硬收费项只有会员，而且就算不办会员，app也能正常使用。" +
                "\n相信大家可以感觉到，很多情侣恋爱app中的收费项，在我们这里都是免费的，毕竟多敲几行代码就能搞定。" +
                "\n而我们的会员一般是和存储空间挂钩的，当用户空间超出了免费的份额之后，就有必要办会员了。");
        contentList.add(c3);
        help.setContentList(contentList);
        // sub
        List<Help> subList = new ArrayList<>();
        Help s1 = new Help();
        s1.setIndex(Help.INDEX_COUPLE_HOME);
        s1.setTitle(getString(R.string.nav_couple));
        subList.add(s1);
        Help s2 = new Help();
        s2.setIndex(Help.INDEX_NOTE_HOME);
        s2.setTitle(getString(R.string.nav_note));
        subList.add(s2);
        Help s3 = new Help();
        s3.setIndex(Help.INDEX_TOPIC_HOME);
        s3.setTitle(getString(R.string.nav_topic));
        subList.add(s3);
        Help s4 = new Help();
        s4.setIndex(Help.INDEX_MORE_HOME);
        s4.setTitle(getString(R.string.nav_more));
        subList.add(s4);
        help.setSubList(subList);
        return help;
    }

    private Help getHelpSuggestHome(int index) {
        Help help = new Help();
        help.setIndex(index);
        help.setTitle(getString(R.string.suggest_feedback));
        help.setDesc("《意见反馈》旨在让大家来提出app的bug和意见等，我们会在第一时间收到您的消息，并和相关负责部门商讨问题的解决方案等，并会在产出结果的第一时间以官方回复形式通知用户。" +
                "\n(注：如果没有第一时间回复，那可能是管理员在偷懒。)");
        // content
        List<Help.HelpContent> contentList = new ArrayList<>();
        Help.HelpContent c1 = new Help.HelpContent();
        c1.setQuestion("类型和状态是什么？");
        c1.setAnswer("类型：意见反馈的种类，在提交意见反馈时选择，这样分类的话，可以加快管理员的审核速度和方便用户浏览。" +
                "\n状态：意见反馈的处理进度，一般为管理员回复并设置。");
        contentList.add(c1);
        Help.HelpContent c2 = new Help.HelpContent();
        c2.setQuestion("《关注》有什么用？");
        c2.setAnswer("当您很想知道一个意见反馈的后续处理时，可以点击关注，这样在管理员回复的时候，就能收到推送消息了。");
        contentList.add(c2);
        Help.HelpContent c3 = new Help.HelpContent();
        c3.setQuestion("我可以在这里发表什么内容？");
        c3.setAnswer("如状态所述的几个类型的内容都可以。" +
                "\n注意：如果app发生闪退等bug，记得标明手机型号等信息，以方便我们快速处理您的问题，最好截图(app崩溃但没退出去的情况下)。" +
                "\n再次注意：任何在本区域捣乱者，将会被给予警告处理，情节严重者，给予封号处理。");
        contentList.add(c3);
        help.setContentList(contentList);
        return help;
    }

    private Help getHelpCoupleHome(int index) {
        return null;
    }

}
