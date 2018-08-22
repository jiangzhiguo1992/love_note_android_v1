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

    // TODO 抽到res-strings里
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

    private Help getHelpByIndex(int index) {
        Help help;
        switch (index) {
            case Help.INDEX_ALL:
            default:
                help = getHelpAll(Help.INDEX_ALL);
                break;
            case Help.INDEX_COUPLE_HOME:
                help = getHelpCoupleHome(index);
                break;
            case Help.INDEX_COUPLE_PAIR:
                help = getHelpCouplePair(index);
                break;
            case Help.INDEX_COUPLE_INFO:
                help = getHelpCoupleInfo(index);
                break;
            case Help.INDEX_NOTE_HOME:
                help = getHelpNoteHome(index);
                break;
            case Help.INDEX_NOTE_LOCK:
                help = getHelpNoteLock(index);
                break;
            case Help.INDEX_NOTE_SOUVENIR:
                help = getHelpNoteSouvenir(index);
                break;
            case Help.INDEX_NOTE_WORD:
                help = getHelpNoteWork(index);
                break;
            case Help.INDEX_NOTE_WHISPER:
                help = getHelpNoteWhisper(index);
                break;
            case Help.INDEX_NOTE_AWARD:
                help = getHelpNoteAward(index);
                break;
            case Help.INDEX_NOTE_DREAM:
                help = getHelpNoteDream(index);
                break;
            case Help.INDEX_NOTE_TRAVEL:
                help = getHelpNoteTravel(index);
                break;
            case Help.INDEX_NOTE_PROMISE:
                help = getHelpNotePromise(index);
                break;
            case Help.INDEX_TOPIC_HOME:
                help = getHelpTopicHome(index);
                break;
            case Help.INDEX_TOPIC_POST:
                help = getHelpTopicPost(index);
                break;
            case Help.INDEX_MORE_HOME:
                help = null;
                break;
            case Help.INDEX_OTHER:
                help = getHelpOther(index);
                break;
            case Help.INDEX_USER_SUGGEST:
                help = getHelpSuggestHome(index);
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
                "\n比如说《" + getString(R.string.nav_couple) + "》和《" + getString(R.string.nav_note) + "》模块，只有在本app配对之后，才能正常使用，但是像《" + getString(R.string.nav_topic) + "》和《" + getString(R.string.nav_more) + "》之类的分享模块，是可以正常浏览的。");
        contentList.add(c2);
        // TODO
        //Help.HelpContent c3 = new Help.HelpContent();
        //c3.setQuestion("这个app只有办会员才能玩吗？");
        //c3.setAnswer("并不是！！！" +
        //        "\napp中的硬收费项只有会员，而且就算不办会员，app也能正常使用。" +
        //        "\n相信大家可以感觉到，很多情侣恋爱app中的收费项，在我们这里都是免费的，毕竟多敲几行代码就能搞定。" +
        //        "\n而我们的会员一般是和存储空间挂钩的，当用户空间超出了免费的份额之后，就有必要办会员了。");
        //contentList.add(c3);
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
        Help s5 = new Help();
        s5.setIndex(Help.INDEX_OTHER);
        s5.setTitle(getString(R.string.other));
        subList.add(s5);
        help.setSubList(subList);
        return help;
    }

    private Help getHelpCoupleHome(int index) {
        Help help = new Help();
        help.setIndex(index);
        help.setTitle(getString(R.string.nav_couple));
        help.setDesc("这是一个记录双方信息的模块，包括但不限于头像、昵称、地理位置、天气信息。");
        // content
        List<Help.HelpContent> contentList = new ArrayList<>();
        Help.HelpContent c1 = new Help.HelpContent();
        c1.setQuestion("为什么我和TA配对了，还是显示没配对界面呢？");
        c1.setAnswer("亲，下拉刷试试看哦！前提是对方已经同意了邀请哦。");
        contentList.add(c1);
        Help.HelpContent c2 = new Help.HelpContent();
        c2.setQuestion("怎么设置背景图？");
        c2.setAnswer("点击右上角，即可进入相应界面进行设置，注意会员和非会员可上传的张数是不一样的哦。" +
                "\n另外，只上传一张的话，是没有动画效果的。");
        contentList.add(c2);
        Help.HelpContent c3 = new Help.HelpContent();
        c3.setQuestion("为什么TA的位置和天气信息没有数据？");
        c3.setAnswer("原因有以下几点：" +
                "\n1.对方没有登录过app。" +
                "\n2.没有开启地理位置获取权限，导致不能上传位置给服务器，以及获取不到位置相关的天气信息。" +
                "\n3.没有开启GPS，或者是GPS信号差，导致长时间获取不到位置信息。" +
                "\n4.部分地区的位置和天气暂时无法获取，如撒哈拉沙漠。" +
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
        c2.setAnswer("在被拒绝或者是配对解散后，有" + intervalSec + "秒的冷却时间，冷却时间内不能再邀请相同的人哦。" +
                "\n所以，还请大人们不要调皮了呢！");
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
        // content
        List<Help.HelpContent> contentList = new ArrayList<>();
        Help.HelpContent c1 = new Help.HelpContent();
        c1.setQuestion("为什么我不能修改自己的头像和昵称？");
        c1.setAnswer("没错！你只能修改对方的信息，不能修改自己的！" +
                "\n试想一下，恋爱过程中，你的形象完全取决与你的TA，多么有意思的一件事啊。");
        contentList.add(c1);
        Help.HelpContent c2 = new Help.HelpContent();
        c2.setQuestion("为什么我不能修改生日呢？");
        c2.setAnswer("小淘气，在你注册的时候已经告诉过你了，性别生日一旦设定，将不能修改！" +
                "\n如果是手误导致设置错了，请移步《关于我们》，联系客服人员解决。");
        contentList.add(c2);
        Help.HelpContent c3 = new Help.HelpContent();
        c3.setQuestion("《解散》是干嘛的？");
        c3.setAnswer("点击解散之后会出现两种状况:" +
                "\n1.配对持续时长小于" + breakNeedDay + "天时，直接解散。" +
                "\n2.配对持续时长大于" + breakNeedDay + "天时，会有" + breakContinueHour + "小时的倒计时。倒计时内没有复合，视为单方面分手。倒计时内对方也点击解散，视为双方面分手。" +
                "\n最后！注意！切记！如果是一些非原则性问题导致想不开的话，小编觉得还是多磨合一下的好。" +
                "\n再一次最后，如果在感情上有什么疑问或者委屈，请移步《关于我们》，找到并联系我们，我们会尽最大的努力来帮助你。");
        contentList.add(c3);
        help.setContentList(contentList);
        return help;
    }

    private Help getHelpNoteHome(int index) {
        Help help = new Help();
        help.setIndex(index);
        help.setTitle(getString(R.string.nav_note));
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
        s3.setIndex(Help.INDEX_NOTE_WORD);
        s3.setTitle(getString(R.string.word));
        subList.add(s3);
        Help s4 = new Help();
        s4.setIndex(Help.INDEX_NOTE_WHISPER);
        s4.setTitle(getString(R.string.whisper));
        subList.add(s4);
        Help s6 = new Help();
        s6.setIndex(Help.INDEX_NOTE_AWARD);
        s6.setTitle(getString(R.string.award));
        subList.add(s6);
        Help s7 = new Help();
        s7.setIndex(Help.INDEX_NOTE_DREAM);
        s7.setTitle(getString(R.string.dream));
        subList.add(s7);
        Help s8 = new Help();
        s8.setIndex(Help.INDEX_NOTE_TRAVEL);
        s8.setTitle(getString(R.string.travel));
        subList.add(s8);
        Help s9 = new Help();
        s9.setIndex(Help.INDEX_NOTE_PROMISE);
        s9.setTitle(getString(R.string.promise));
        subList.add(s9);
        help.setSubList(subList);
        return help;
    }

    private Help getHelpNoteLock(int index) {
        Help help = new Help();
        help.setIndex(index);
        help.setTitle(getString(R.string.pwd_lock));
        help.setDesc("给你的小本本上个锁吧，这样就不怕别人瞎戳戳戳了。");
        // content
        List<Help.HelpContent> contentList = new ArrayList<>();
        Help.HelpContent c1 = new Help.HelpContent();
        c1.setQuestion("为什么发送了验证码，我的手机缺收不到！");
        c1.setAnswer("前方高能！因为验证码是发到你的另一半手机上了啊。" +
                "\n为了避免手机丢失后，被不法分子通过验证码重置密码锁，我们决定验证码只能发到TA的手机上。" +
                "\n什么？两个人手机都丢了？还是被同一个小偷拿到？OMG！赶紧用其他手机登陆下，把那个账号顶下来吧！");
        contentList.add(c1);
        Help.HelpContent c2 = new Help.HelpContent();
        c2.setQuestion("为什么我退出app再进来后，没有自动上锁？");
        c2.setAnswer("值得注意的是，上锁和解锁都是用户去控制的，app不做任何操作，主要是为了避免频繁的解锁。" +
                "\n其实一般在出去玩耍，或者是家里还熊孩子的时候上锁就可以了，你说呢？");
        contentList.add(c2);
        help.setContentList(contentList);
        return help;
    }

    private Help getHelpNoteSouvenir(int index) {
        Limit limit = SPHelper.getLimit();
        int souvenirForeignYearCount = limit.getSouvenirForeignYearCount();
        Help help = new Help();
        help.setIndex(index);
        help.setTitle(getString(R.string.souvenir));
        help.setDesc("纪念日，最值得被记录的日子。可以关联每年当日发生的事哦。" +
                "\n每年的纪念日当天，记得过来看看，有彩蛋哟！");
        // content
        List<Help.HelpContent> contentList = new ArrayList<>();
        Help.HelpContent c1 = new Help.HelpContent();
        c1.setQuestion("纪念日和愿望清单有什么区别？");
        c1.setAnswer("当然有了，一个是已经实现的，一个是还没有实现的。" +
                "\n两种数据的展现方式也不一样，纪念日可以关联每年当天所发生的事，愿望清单就不行了。" +
                "\n还有一点，纪念日双方都可以看到，愿望清单只能看到自己发表的。毕竟总有一些不可告人的愿望！");
        contentList.add(c1);
        Help.HelpContent c2 = new Help.HelpContent();
        c2.setQuestion("为什么发表的时候，提示我时间不对？");
        c2.setAnswer("记住，纪念日选择的时间必须小于现在，大于你的生日。如果想记录TA的生日，但TA的生日比你早的话，还是请TA来发表吧。" +
                "\n愿望清单的话，时间必须大于现在，然后没有了。");
        contentList.add(c2);
        Help.HelpContent c3 = new Help.HelpContent();
        c3.setQuestion("纪念日的关联数据，每年能关联几个同类型数据？");
        c3.setAnswer(souvenirForeignYearCount + "个哦！");
        contentList.add(c3);
        help.setContentList(contentList);
        return help;
    }

    private Help getHelpNoteWork(int index) {
        Help help = new Help();
        help.setIndex(index);
        help.setTitle(getString(R.string.word));
        // content
        List<Help.HelpContent> contentList = new ArrayList<>();
        Help.HelpContent c1 = new Help.HelpContent();
        c1.setQuestion("为什么我发布了留言，对方却不能收到？");
        c1.setAnswer("这是留言板，不是聊天哦。不是实时发送的，需要下拉刷新才能看到哦。" +
                "\n本app和其他市面上情侣app还有一点不一样，就是没有实时聊天功能，因为我们只专注记录与分享！" +
                "\n而且某信的聊天功能已经做得非常完善了，我们就不献丑了呢！");
        contentList.add(c1);
        help.setContentList(contentList);
        return help;
    }

    private Help getHelpNoteWhisper(int index) {
        Help help = new Help();
        help.setIndex(index);
        help.setTitle(getString(R.string.whisper));
        help.setDesc("这是一个可以说悄悄话的地方。" +
                "\n是不是有些话憋在心里难受，说又没地方说？这里就是你发泄的天堂！" +
                "\n当然了，除了发泄，还有点点情趣呢，发表一句TA想听到的话，然后让TA使劲找吧，hiahiahia~");
        // content
        List<Help.HelpContent> contentList = new ArrayList<>();
        Help.HelpContent c1 = new Help.HelpContent();
        c1.setQuestion("这里的内容会被别人看到吗？");
        c1.setAnswer("不会的，整个小本本模块里的内容都只有你和TA可见，包括这里。" +
                "\n而且就算TA要看也要输对频道才行，是不是很有意思？");
        contentList.add(c1);
        Help.HelpContent c2 = new Help.HelpContent();
        c2.setQuestion("怎么删除？长按也不行！");
        c2.setAnswer("我想告诉你的是，这里的东西一旦发布，是不能删除的！" +
                "\n所以要谨慎啊，要是是怕被TA看到的话，就找个难写的频道发表内容。");
        contentList.add(c2);
        help.setContentList(contentList);
        return help;
    }

    private Help getHelpNoteAward(int index) {
        Help help = new Help();
        help.setIndex(index);
        help.setTitle(getString(R.string.award));
        help.setDesc("这是一个奖善罚恶的机制。" +
                "\n两个人的日常中，难免有人做错事，有人做对事。做错了就要惩罚，做对了就要奖励。" +
                "\n情侣们可以在这里定制一套自己的规则，按规则来奖励与惩罚，以分数来论英雄。");
        // content
        List<Help.HelpContent> contentList = new ArrayList<>();
        Help.HelpContent c1 = new Help.HelpContent();
        c1.setQuestion("怎么使用这个功能啊？");
        c1.setAnswer("1.首先无规矩不成方圆。首先双方可以添加一些规则，用来告诉双方哪些事会触发机制。" +
                "\n2.一旦触发了规则里事，就可以添加奖励，每个奖励必须依赖某个规则，不可以乱加哦！" +
                "\n3.奖励发表的多了，就能看出来谁分高，谁分低了。谁做的好，谁做的不好。");
        contentList.add(c1);
        Help.HelpContent c2 = new Help.HelpContent();
        c2.setQuestion("举个栗子！");
        c2.setAnswer("1.妹子讨厌男生抽烟，于是发布了一条规则：抽烟-10分。" +
                "\n2.发现男生抽烟了，发布奖励，依赖抽烟的规则，男生总分数-10。" +
                "\n3.分数总是负的不行啊，然后再发布一条规则：男生一天不打游戏的话，+10分。" +
                "\n4.大概就是这样子，要有扣分规则，也要有加分规则，这样才平衡。");
        contentList.add(c2);
        Help.HelpContent c3 = new Help.HelpContent();
        c3.setQuestion("为什么会有这个功能？");
        c3.setAnswer("题外话，因为我们BOSS和他女友相恋的时候，就是这么做的，感觉很有意思，然后强推这个功能。233333~");
        contentList.add(c3);
        help.setContentList(contentList);
        return help;
    }

    private Help getHelpNoteDream(int index) {
        Help help = new Help();
        help.setIndex(index);
        help.setTitle(getString(R.string.dream));
        help.setDesc("不妨在梦醒的那一刻，快速记录下来。" +
                "\n昨夜的梦，一定要记录下来哦！要不然会慢慢的消失掉，就像你的名字。" +
                "\n待到年老时刻，回过头来看看，是不是别有一番风味呢？");
        return help;
    }

    private Help getHelpNoteTravel(int index) {
        Limit limit = SPHelper.getLimit();
        int placeCount = limit.getTravelPlaceCount();
        int albumCount = limit.getTravelAlbumCount();
        int videoCount = limit.getTravelVideoCount();
        int foodCount = limit.getTravelFoodCount();
        int diaryCount = limit.getTravelDiaryCount();
        Help help = new Help();
        help.setIndex(index);
        help.setTitle(getString(R.string.travel));
        help.setDesc("一次约会，一次旅游，一次散步，一次踏足，甚至是异地恋的一次相见。都可以在这里记录哦！");
        // content
        List<Help.HelpContent> contentList = new ArrayList<>();
        Help.HelpContent c1 = new Help.HelpContent();
        c1.setQuestion("每个游记可以添加多少关联数据呢？");
        c1.setAnswer(placeCount + "个足迹，" + albumCount + "个相册，"
                + videoCount + "个视频，" + foodCount + "个美食，" + diaryCount + "个日记。");
        contentList.add(c1);
        help.setContentList(contentList);
        return help;
    }

    private Help getHelpNotePromise(int index) {
        Help help = new Help();
        help.setIndex(index);
        help.setTitle(getString(R.string.promise));
        help.setDesc("TA有没有给你做出什么承诺，哪怕是很随意的一句？" +
                "\n有的话就在这里记录下来吧！顺便还能记录违背承诺的情况。");
        return help;
    }

    private Help getHelpTopicHome(int index) {
        Help help = new Help();
        help.setIndex(index);
        help.setTitle(getString(R.string.nav_topic));
        help.setDesc("记录与分享，这个模块更专注于分享。" +
                "\n有什么想要分享给大家的、或者是探讨的话题，就来这里诉说吧，相信会有很多聆听者的。");
        // content 匿名
        List<Help.HelpContent> contentList = new ArrayList<>();
        Help.HelpContent c1 = new Help.HelpContent();
        c1.setQuestion("为什么有的类别我这里有，我的另一半没有呢？");
        c1.setAnswer("亲，我们这里部分模块是区分用户和地域的。" +
                "\n有的模块只对男性开放，有的只对女性开放，有的只对同性恋开放，有的只对异性恋开放。" +
                "\n有的模块只对区域内用户开发，类似于《附近的帖子》。");
        contentList.add(c1);
        Help.HelpContent c2 = new Help.HelpContent();
        c2.setQuestion("这个匿名发帖靠谱吗？");
        c2.setAnswer("考虑到部分内容可能不想让另一半看到，我们提供了匿名发布的模块，并且提供了相应的分类。" +
                "\n放心，匿名的话，发帖人的信息将会被抹除，包括在匿名下的所有评论者的信息也会被抹除。" +
                "\n因为不知道用户的信息，所以帖子和评论是不能删除的，而且在帖子更新的时候不会受到消息提醒。");
        contentList.add(c2);
        help.setContentList(contentList);
        // sub
        List<Help> subList = new ArrayList<>();
        Help s1 = new Help();
        s1.setIndex(Help.INDEX_TOPIC_POST);
        s1.setTitle(getString(R.string.post));
        subList.add(s1);
        help.setSubList(subList);
        return help;
    }

    private Help getHelpTopicPost(int index) {
        Help help = new Help();
        help.setIndex(index);
        help.setTitle(getString(R.string.post));
        // content
        List<Help.HelpContent> contentList = new ArrayList<>();
        Help.HelpContent c1 = new Help.HelpContent();
        c1.setQuestion("怎么删除？");
        c1.setAnswer("帖子：如果是自己的帖子，右上角会有删除的图标，点击删除。" +
                "\n评论：如果是自己的评论，长按即可删除哦。");
        contentList.add(c1);
        Help.HelpContent c2 = new Help.HelpContent();
        c2.setQuestion("点赞，收藏，戳TA，三者的区别是？");
        c2.setAnswer("点赞和收藏的区别，前者没有相关记录，后者可以在用户的收藏模块中找到收藏内容。" +
                "\n戳TA：如果觉得某个内容不错，想分享给TA，不妨戳TA一下，TA会在消息系统中收到消息，并可进入被戳的帖子或评论中来。");
        contentList.add(c2);
        Help.HelpContent c3 = new Help.HelpContent();
        c3.setQuestion("发现了不适的内容怎么办？");
        c3.setAnswer("举报！举报！举报！" +
                "\n社区是大家的，运营也需要靠大家的一份力量。欢迎大家勇决举报不适的内容，我们也会努力维护一个舒适的环境。");
        contentList.add(c3);
        help.setContentList(contentList);
        return help;
    }

    // TODO 操作习惯(长按删除)
    // TODO 设计风格(UI界面 md)
    // TODO 开发理念(图片少，apk小，android规范)
    // TODO 强迫症(界面排版，红点提醒(note没有，topic没有，more没有，set有)，更新升级)
    // TODO 图片处理(不限大小的压缩)
    private Help getHelpOther(int index) {
        return null;
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
        c2.setQuestion("我可以在这里发表什么内容？");
        c2.setAnswer("如状态所述的几个类型的内容都可以。" +
                "\n注意：如果app发生闪退等bug，记得标明手机型号等信息，以方便我们快速处理您的问题，最好截图(app崩溃但没退出去的情况下)。" +
                "\n再次注意：任何在本区域捣乱者，将会被给予警告处理，情节严重者，给予封号处理。");
        contentList.add(c2);
        help.setContentList(contentList);
        return help;
    }

}
