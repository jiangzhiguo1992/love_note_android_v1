package com.jiangzg.lovenote.activity.settings;

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
import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.adapter.HelpContentAdapter;
import com.jiangzg.lovenote.adapter.HelpSubAdapter;
import com.jiangzg.lovenote.base.BaseActivity;
import com.jiangzg.lovenote.domain.Help;
import com.jiangzg.lovenote.domain.Limit;
import com.jiangzg.lovenote.helper.RecyclerHelper;
import com.jiangzg.lovenote.helper.SPHelper;
import com.jiangzg.lovenote.helper.ViewHelper;

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
        if (help == null) return;
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
            case Help.INDEX_USER_SUGGEST:
                help = getHelpSuggestHome(Help.INDEX_USER_SUGGEST);
                break;
            case Help.INDEX_COUPLE_HOME:
                help = getHelpCoupleHome(Help.INDEX_COUPLE_HOME);
                break;
            case Help.INDEX_COUPLE_PAIR:
                help = getHelpCouplePair(Help.INDEX_COUPLE_PAIR);
                break;
            case Help.INDEX_COUPLE_INFO:
                help = getHelpCoupleInfo(Help.INDEX_COUPLE_INFO);
                break;
            case Help.INDEX_NOTE_HOME:
                help = getHelpNoteHome(Help.INDEX_NOTE_HOME);
                break;
            case Help.INDEX_NOTE_LOCK:
                help = null;
                break;
            case Help.INDEX_NOTE_SOUVENIR:
                help = null;
                break;
            case Help.INDEX_NOTE_SHY:
                help = null;
                break;
            case Help.INDEX_NOTE_MENSES:
                help = null;
                break;
            case Help.INDEX_NOTE_SLEEP:
                help = null;
                break;
            case Help.INDEX_NOTE_AUDIO:
                help = null;
                break;
            case Help.INDEX_NOTE_VIDEO:
                help = null;
                break;
            case Help.INDEX_NOTE_ALBUM:
                help = null;
                break;
            case Help.INDEX_NOTE_WORD:
                help = null;
                break;
            case Help.INDEX_NOTE_WHISPER:
                help = null;
                break;
            case Help.INDEX_NOTE_DIARY:
                help = null;
                break;
            case Help.INDEX_NOTE_AWARD:
                help = null;
                break;
            case Help.INDEX_NOTE_DREAM:
                help = null;
                break;
            case Help.INDEX_NOTE_GIFT:
                help = null;
                break;
            case Help.INDEX_NOTE_FOOD:
                help = null;
                break;
            case Help.INDEX_NOTE_TRAVEL:
                help = null;
                break;
            case Help.INDEX_NOTE_ANGRY:
                help = null;
                break;
            case Help.INDEX_NOTE_PROMISE:
                help = null;
                break;
            case Help.INDEX_TOPIC_HOME:
                help = null;
                break;
            case Help.INDEX_MORE_HOME:
                help = null;
                break;
        }
        return help;
    }

    private Help getHelpAll(int index) {
        Help help = new Help();
        help.setIndex(index);
        help.setTitle(getString(R.string.help_document));
        help.setDesc("《帮助文档》旨在让大家快速理解这个app，如有不明白的地方，就来这里找找答案吧");
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
        Help help = new Help();
        help.setIndex(index);
        help.setTitle(getString(R.string.nav_couple));
        help.setDesc("《" + getString(R.string.nav_couple) + "》模块用来展示情侣双方的各项信息，包括但不仅限于双方头像和昵称，双方所在地和天气状况。");
        // content
        List<Help.HelpContent> contentList = new ArrayList<>();
        Help.HelpContent c1 = new Help.HelpContent();
        c1.setQuestion("为什么我和TA配对了，还是显示没配对界面呢？");
        c1.setAnswer("亲，下拉刷试试看哦！前提是对方已经同意了邀请哦。");
        contentList.add(c1);
        Help.HelpContent c2 = new Help.HelpContent();
        c2.setQuestion("怎么设置背景图？");
        c2.setAnswer("点击右上角，即可进入相应界面进行设置，注意会员和非会员可上传的张数是不一样的哦。\n另外，只上传一张的话，是没有动画效果的。");
        contentList.add(c2);
        Help.HelpContent c3 = new Help.HelpContent();
        c3.setQuestion("为什么TA的位置和天气信息没有数据？");
        c3.setAnswer("原因有以下几点：" +
                "\n1.对方没有登录过app。" +
                "\n2.没有开启地理位置获取权限，导致不能上传位置给服务器，以及获取不到位置相关的天气信息。" +
                "\n3.没有开启GPS，或者是GPS信号差，导致长时间获取不到位置信息。" +
                "\n4.部分地区的位置和天气暂时无法获取，如国外。" +
                "\n5.如果还是有问题，记得双方都下拉刷新试试能不能正常显示。如不能，请移步《意见反馈》中，反馈给我们。");
        contentList.add(c3);
        Help.HelpContent c4 = new Help.HelpContent();
        c4.setQuestion("位置和天气的更新逻辑是什么？");
        c4.setAnswer("为了确保用户设备的电量持久，以及为了遵守android高版本的开发准则。app不会在后台无限制的获取用户的地理位置，只有在app启动或者是下拉刷新的时候，才会获取用户的位置，进而更新天气信息。所以当位置和天气信息不准时，下拉刷新看看吧。");
        contentList.add(c4);
        help.setContentList(contentList);
        // sub
        List<Help> subList = new ArrayList<>();
        Help s1 = new Help();
        s1.setIndex(Help.INDEX_COUPLE_PAIR);
        s1.setTitle(getString(R.string.pair));
        subList.add(s1);
        Help s2 = new Help();
        s2.setIndex(Help.INDEX_COUPLE_INFO);
        s2.setTitle(getString(R.string.pair_info));
        subList.add(s2);
        help.setSubList(subList);
        return help;
    }

    private Help getHelpCouplePair(int index) {
        long intervalSec = SPHelper.getLimit().getCoupleInviteIntervalSec();
        Help help = new Help();
        help.setIndex(index);
        help.setTitle(getString(R.string.pair));
        // content
        List<Help.HelpContent> contentList = new ArrayList<>();
        Help.HelpContent c1 = new Help.HelpContent();
        c1.setQuestion("只能输手机号配对吗？");
        c1.setAnswer("是的，我们摒弃了邀请码的配对方式，采用更坦诚的手机号邀请方式。");
        contentList.add(c1);
        Help.HelpContent c2 = new Help.HelpContent();
        c2.setQuestion("为什么提示我邀请过于频繁？");
        c2.setAnswer("在被拒绝或者是配对解散后，有" + intervalSec + "秒的冷却时间，冷却时间内不能再邀请相同的人哦。\n所以，还请大人们不要调皮了呢！");
        contentList.add(c2);
        Help.HelpContent c3 = new Help.HelpContent();
        c3.setQuestion("我和TA原来配对过，再配对会怎么样？");
        c3.setAnswer("两个人有过配对经历，再次配对视为复合。配对之后，恢复解散之前的状态，数据也原封不动。");
        contentList.add(c3);
        help.setContentList(contentList);
        return help;
    }

    private Help getHelpCoupleInfo(int index) {
        Limit limit = SPHelper.getLimit();
        long breakNeedDay = limit.getCoupleBreakNeedSec() / (ConstantUtils.DAY / ConstantUtils.SEC);
        long breakContinueHour = limit.getCoupleBreakSec() / (ConstantUtils.HOUR / ConstantUtils.SEC);
        Help help = new Help();
        help.setIndex(index);
        help.setTitle(getString(R.string.pair_info));
        help.setDesc("用来展示和修改双方的信息，还可以打电话哦！");
        // content
        List<Help.HelpContent> contentList = new ArrayList<>();
        Help.HelpContent c1 = new Help.HelpContent();
        c1.setQuestion("为什么我不能修改自己的头像和昵称？");
        c1.setAnswer("没错！你只能修改对方的信息，不能修改自己的！" +
                "\n试想一下，恋爱过程中，你的形象完全取决与你的TA，多么有意思的一件事啊。");
        contentList.add(c1);
        Help.HelpContent c2 = new Help.HelpContent();
        c2.setQuestion("《解散》是干嘛的？");
        c2.setAnswer("点击解散之后会出现两种状况:" +
                "\n1.配对持续时长小于" + breakNeedDay + "天时，直接解散。" +
                "\n2.配对持续时长大于" + breakNeedDay + "天时，会有" + breakContinueHour + "小时的倒计时。倒计时内没有复合，视为单方面分手。倒计时内对方也点击解散，视为双方面分手。" +
                "\n最后！注意！切记！如果是一些非原则性问题导致想不开的话，小编觉得还是多磨合一下的好。" +
                "\n再一次最后，如果在感情上有什么疑问或者委屈，请移步《关于我们》，找到并联系我们，我们会尽最大的努力来帮助你。");
        contentList.add(c2);
        help.setContentList(contentList);
        return help;
    }

    private Help getHelpNoteHome(int index) {
        Help help = new Help();
        help.setIndex(index);
        help.setTitle(getString(R.string.nav_note));
        help.setDesc("《" + getString(R.string.nav_note) + "》是app最核心的模块，生活记录模块。" +
                "\n恋爱生活中的点点滴滴，都应该被记录下来，不管是多微不足道的事都值得被记录。");
        // content
        List<Help.HelpContent> contentList = new ArrayList<>();
        Help.HelpContent c1 = new Help.HelpContent();
        c1.setQuestion("数据放在这里安全吗？");
        c1.setAnswer("请放心！此模块中的数据全为私有数据，除了你和TA之外无人可以访问。如果双方解除配对了，那么此配对中的记录模块的数据将不见天日，除非复合。");
        contentList.add(c1);
        Help.HelpContent c2 = new Help.HelpContent();
        c2.setQuestion("为什么有的模块进不去？");
        c2.setAnswer("目前可能会有极个别小板块在未开会员的情况下是不允许进入的。不排除以后会消除这种限制。");
        contentList.add(c2);
        help.setContentList(contentList);
        // sub
        List<Help> subList = new ArrayList<>();
        Help s1 = new Help();
        s1.setIndex(Help.INDEX_NOTE_LOCK);
        s1.setTitle(getString(R.string.pwd_lock));
        subList.add(s1);
        Help s2 = new Help();
        s2.setIndex(Help.INDEX_NOTE_SOUVENIR);
        s2.setTitle(getString(R.string.souvenir));
        subList.add(s2);
        Help s3 = new Help();
        s3.setIndex(Help.INDEX_NOTE_SHY);
        s3.setTitle(getString(R.string.shy));
        subList.add(s3);
        Help s4 = new Help();
        s4.setIndex(Help.INDEX_NOTE_MENSES);
        s4.setTitle(getString(R.string.menses));
        subList.add(s4);
        Help s5 = new Help();
        s5.setIndex(Help.INDEX_NOTE_SLEEP);
        s5.setTitle(getString(R.string.sleep));
        subList.add(s5);
        Help s6 = new Help();
        s6.setIndex(Help.INDEX_NOTE_AUDIO);
        s6.setTitle(getString(R.string.audio));
        subList.add(s6);
        Help s7 = new Help();
        s7.setIndex(Help.INDEX_NOTE_VIDEO);
        s7.setTitle(getString(R.string.video));
        subList.add(s7);
        Help s8 = new Help();
        s8.setIndex(Help.INDEX_NOTE_ALBUM);
        s8.setTitle(getString(R.string.album));
        subList.add(s8);
        Help s9 = new Help();
        s9.setIndex(Help.INDEX_NOTE_WORD);
        s9.setTitle(getString(R.string.word));
        subList.add(s9);
        Help s10 = new Help();
        s10.setIndex(Help.INDEX_NOTE_WHISPER);
        s10.setTitle(getString(R.string.whisper));
        subList.add(s10);
        Help s11 = new Help();
        s11.setIndex(Help.INDEX_NOTE_DIARY);
        s11.setTitle(getString(R.string.diary));
        subList.add(s11);
        Help s12 = new Help();
        s12.setIndex(Help.INDEX_NOTE_AWARD);
        s12.setTitle(getString(R.string.award));
        subList.add(s12);
        Help s13 = new Help();
        s13.setIndex(Help.INDEX_NOTE_DREAM);
        s13.setTitle(getString(R.string.dream));
        subList.add(s13);
        Help s14 = new Help();
        s14.setIndex(Help.INDEX_NOTE_DREAM);
        s14.setTitle(getString(R.string.dream));
        subList.add(s14);
        Help s15 = new Help();
        s15.setIndex(Help.INDEX_NOTE_GIFT);
        s15.setTitle(getString(R.string.gift));
        subList.add(s15);
        Help s16 = new Help();
        s16.setIndex(Help.INDEX_NOTE_FOOD);
        s16.setTitle(getString(R.string.food));
        subList.add(s16);
        Help s17 = new Help();
        s17.setIndex(Help.INDEX_NOTE_TRAVEL);
        s17.setTitle(getString(R.string.travel));
        subList.add(s17);
        Help s18 = new Help();
        s18.setIndex(Help.INDEX_NOTE_ANGRY);
        s18.setTitle(getString(R.string.angry));
        subList.add(s18);
        Help s19 = new Help();
        s19.setIndex(Help.INDEX_NOTE_PROMISE);
        s19.setTitle(getString(R.string.promise));
        subList.add(s19);
        help.setSubList(subList);
        return help;
    }

}
