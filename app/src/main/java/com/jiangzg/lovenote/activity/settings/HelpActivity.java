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
import com.jiangzg.base.common.StringUtils;
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
import java.util.Locale;

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
        if (StringUtils.isEmpty(desc)) {
            tvDesc.setVisibility(View.GONE);
        } else {
            tvDesc.setVisibility(View.VISIBLE);
            tvDesc.setText(desc);
        }
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
                help = getHelpMoreHome(index);
                break;
            case Help.INDEX_MORE_VIP:
                help = getHelpMoreVip(index);
                break;
            case Help.INDEX_MORE_COIN:
                help = getHelpMoreCoin(index);
                break;
            case Help.INDEX_MORE_BILL:
                help = getHelpMoreBill(index);
                break;
            case Help.INDEX_MORE_SIGN:
                help = getHelpMoreSign(index);
                break;
            case Help.INDEX_MORE_MATCH:
                help = getHelpMoreMatch(index);
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
        help.setDesc(getString(R.string.help_all_d));
        // content
        List<Help.HelpContent> contentList = new ArrayList<>();
        Help.HelpContent c1 = new Help.HelpContent();
        c1.setQuestion(getString(R.string.help_all_c1_q));
        c1.setAnswer(getString(R.string.help_all_c1_a));
        contentList.add(c1);
        Help.HelpContent c2 = new Help.HelpContent();
        c2.setQuestion(getString(R.string.help_all_c2_q));
        c2.setAnswer(String.format(Locale.getDefault(), getString(R.string.help_all_c2_a), getString(R.string.nav_couple), getString(R.string.nav_note), getString(R.string.nav_topic), getString(R.string.nav_more)));
        contentList.add(c2);
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
        help.setDesc(getString(R.string.help_couple_home_d));
        // content
        List<Help.HelpContent> contentList = new ArrayList<>();
        Help.HelpContent c1 = new Help.HelpContent();
        c1.setQuestion(getString(R.string.help_couple_home_c1_q));
        c1.setAnswer(getString(R.string.help_couple_home_c1_a));
        contentList.add(c1);
        Help.HelpContent c2 = new Help.HelpContent();
        c2.setQuestion(getString(R.string.help_couple_home_c2_q));
        c2.setAnswer(getString(R.string.help_couple_home_c2_a));
        contentList.add(c2);
        Help.HelpContent c3 = new Help.HelpContent();
        c3.setQuestion(getString(R.string.help_couple_home_c3_q));
        c3.setAnswer(getString(R.string.help_couple_home_c3_a));
        contentList.add(c3);
        Help.HelpContent c4 = new Help.HelpContent();
        c4.setQuestion(getString(R.string.help_couple_home_c4_q));
        c4.setAnswer(getString(R.string.help_couple_home_c4_a));
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
        c1.setQuestion(getString(R.string.help_couple_pair_c1_q));
        c1.setAnswer(getString(R.string.help_couple_pair_c1_a));
        contentList.add(c1);
        Help.HelpContent c2 = new Help.HelpContent();
        c2.setQuestion(getString(R.string.help_couple_pair_c2_q));
        c2.setAnswer(String.format(Locale.getDefault(), getString(R.string.help_couple_pair_c2_a), intervalSec));
        contentList.add(c2);
        Help.HelpContent c3 = new Help.HelpContent();
        c3.setQuestion(getString(R.string.help_couple_pair_c3_q));
        c3.setAnswer(getString(R.string.help_couple_pair_c3_a));
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
        c1.setQuestion(getString(R.string.help_couple_info_c1_q));
        c1.setAnswer(getString(R.string.help_couple_info_c1_a));
        contentList.add(c1);
        Help.HelpContent c2 = new Help.HelpContent();
        c2.setQuestion(getString(R.string.help_couple_info_c2_q));
        c2.setAnswer(getString(R.string.help_couple_info_c2_a));
        contentList.add(c2);
        Help.HelpContent c3 = new Help.HelpContent();
        c3.setQuestion(getString(R.string.help_couple_info_c3_q));
        c3.setAnswer(String.format(Locale.getDefault(), getString(R.string.help_couple_info_c3_a), breakNeedDay, breakNeedDay, breakContinueHour));
        contentList.add(c3);
        help.setContentList(contentList);
        return help;
    }

    private Help getHelpNoteHome(int index) {
        Help help = new Help();
        help.setIndex(index);
        help.setTitle(getString(R.string.nav_note));
        help.setDesc(getString(R.string.help_note_home_d));
        // content
        List<Help.HelpContent> contentList = new ArrayList<>();
        Help.HelpContent c1 = new Help.HelpContent();
        c1.setQuestion(getString(R.string.help_note_home_c1_q));
        c1.setAnswer(getString(R.string.help_note_home_c1_a));
        contentList.add(c1);
        Help.HelpContent c2 = new Help.HelpContent();
        c2.setQuestion(getString(R.string.help_note_home_c2_q));
        c2.setAnswer(getString(R.string.help_note_home_c2_a));
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
        help.setDesc(getString(R.string.help_note_lock_d));
        // content
        List<Help.HelpContent> contentList = new ArrayList<>();
        Help.HelpContent c1 = new Help.HelpContent();
        c1.setQuestion(getString(R.string.help_note_lock_c1_q));
        c1.setAnswer(getString(R.string.help_note_lock_c1_a));
        contentList.add(c1);
        Help.HelpContent c2 = new Help.HelpContent();
        c2.setQuestion(getString(R.string.help_note_lock_c2_q));
        c2.setAnswer(getString(R.string.help_note_lock_c2_a));
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
        help.setDesc(getString(R.string.help_note_souvenir_d));
        // content
        List<Help.HelpContent> contentList = new ArrayList<>();
        Help.HelpContent c1 = new Help.HelpContent();
        c1.setQuestion(getString(R.string.help_note_souvenir_c1_q));
        c1.setAnswer(getString(R.string.help_note_souvenir_c1_a));
        contentList.add(c1);
        Help.HelpContent c2 = new Help.HelpContent();
        c2.setQuestion(getString(R.string.help_note_souvenir_c2_q));
        c2.setAnswer(getString(R.string.help_note_souvenir_c2_a));
        contentList.add(c2);
        Help.HelpContent c3 = new Help.HelpContent();
        c3.setQuestion(getString(R.string.help_note_souvenir_c3_q));
        c3.setAnswer(String.format(Locale.getDefault(), getString(R.string.help_note_souvenir_c3_a), souvenirForeignYearCount));
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
        c1.setQuestion(getString(R.string.help_note_work_c1_q));
        c1.setAnswer(getString(R.string.help_note_work_c1_a));
        contentList.add(c1);
        help.setContentList(contentList);
        return help;
    }

    private Help getHelpNoteWhisper(int index) {
        Help help = new Help();
        help.setIndex(index);
        help.setTitle(getString(R.string.whisper));
        help.setDesc(getString(R.string.help_note_whisper_d));
        // content
        List<Help.HelpContent> contentList = new ArrayList<>();
        Help.HelpContent c1 = new Help.HelpContent();
        c1.setQuestion(getString(R.string.help_note_whisper_c1_q));
        c1.setAnswer(getString(R.string.help_note_whisper_c1_a));
        contentList.add(c1);
        Help.HelpContent c2 = new Help.HelpContent();
        c2.setQuestion(getString(R.string.help_note_whisper_c2_q));
        c2.setAnswer(getString(R.string.help_note_whisper_c2_a));
        contentList.add(c2);
        help.setContentList(contentList);
        return help;
    }

    private Help getHelpNoteAward(int index) {
        Help help = new Help();
        help.setIndex(index);
        help.setTitle(getString(R.string.award));
        help.setDesc(getString(R.string.help_note_award_d));
        // content
        List<Help.HelpContent> contentList = new ArrayList<>();
        Help.HelpContent c1 = new Help.HelpContent();
        c1.setQuestion(getString(R.string.help_note_award_c1_q));
        c1.setAnswer(getString(R.string.help_note_award_c1_a));
        contentList.add(c1);
        Help.HelpContent c2 = new Help.HelpContent();
        c2.setQuestion(getString(R.string.help_note_award_c2_q));
        c2.setAnswer(getString(R.string.help_note_award_c2_a));
        contentList.add(c2);
        Help.HelpContent c3 = new Help.HelpContent();
        c3.setQuestion(getString(R.string.help_note_award_c3_q));
        c3.setAnswer(getString(R.string.help_note_award_c3_a));
        contentList.add(c3);
        help.setContentList(contentList);
        return help;
    }

    private Help getHelpNoteDream(int index) {
        Help help = new Help();
        help.setIndex(index);
        help.setTitle(getString(R.string.dream));
        help.setDesc(getString(R.string.help_note_dream_d));
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
        help.setDesc(getString(R.string.help_note_travel_d));
        // content
        List<Help.HelpContent> contentList = new ArrayList<>();
        Help.HelpContent c1 = new Help.HelpContent();
        c1.setQuestion(getString(R.string.help_note_travel_c1_q));
        c1.setAnswer(String.format(Locale.getDefault(), getString(R.string.help_note_travel_c1_a), placeCount, albumCount, videoCount, foodCount, diaryCount));
        contentList.add(c1);
        help.setContentList(contentList);
        return help;
    }

    private Help getHelpNotePromise(int index) {
        Help help = new Help();
        help.setIndex(index);
        help.setTitle(getString(R.string.promise));
        help.setDesc(getString(R.string.help_note_promise_d));
        return help;
    }

    private Help getHelpTopicHome(int index) {
        Help help = new Help();
        help.setIndex(index);
        help.setTitle(getString(R.string.nav_topic));
        help.setDesc(getString(R.string.help_topic_home_d));
        // content 匿名
        List<Help.HelpContent> contentList = new ArrayList<>();
        Help.HelpContent c1 = new Help.HelpContent();
        c1.setQuestion(getString(R.string.help_topic_home_c1_q));
        c1.setAnswer(getString(R.string.help_topic_home_c1_a));
        contentList.add(c1);
        Help.HelpContent c2 = new Help.HelpContent();
        c2.setQuestion(getString(R.string.help_topic_home_c2_q));
        c2.setAnswer(getString(R.string.help_topic_home_c2_a));
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
        c1.setQuestion(getString(R.string.help_topic_post_c1_q));
        c1.setAnswer(getString(R.string.help_topic_post_c1_a));
        contentList.add(c1);
        Help.HelpContent c2 = new Help.HelpContent();
        c2.setQuestion(getString(R.string.help_topic_post_c2_q));
        c2.setAnswer(getString(R.string.help_topic_post_c2_a));
        contentList.add(c2);
        Help.HelpContent c3 = new Help.HelpContent();
        c3.setQuestion(getString(R.string.help_topic_post_c3_q));
        c3.setAnswer(getString(R.string.help_topic_post_c3_a));
        contentList.add(c3);
        help.setContentList(contentList);
        return help;
    }

    private Help getHelpMoreHome(int index) {
        Help help = new Help();
        help.setIndex(index);
        help.setTitle(getString(R.string.nav_more));
        help.setDesc("这是一个类似于《更多》的模块，在关注和分享里塞不下的功能，会全放到这里哦，比如广播系统，消费系统，活动系统等等。");
        // sub
        List<Help> subList = new ArrayList<>();
        Help s1 = new Help();
        s1.setIndex(Help.INDEX_MORE_VIP);
        s1.setTitle(getString(R.string.vip));
        subList.add(s1);
        Help s2 = new Help();
        s2.setIndex(Help.INDEX_MORE_COIN);
        s2.setTitle(getString(R.string.coin));
        subList.add(s2);
        Help s3 = new Help();
        s3.setIndex(Help.INDEX_MORE_SIGN);
        s3.setTitle(getString(R.string.sign));
        subList.add(s3);
        Help s4 = new Help();
        s4.setIndex(Help.INDEX_MORE_MATCH);
        s4.setTitle(getString(R.string.nav_match));
        subList.add(s4);
        help.setSubList(subList);
        return help;
    }

    private Help getHelpMoreVip(int index) {
        Help help = new Help();
        help.setIndex(index);
        help.setTitle(getString(R.string.vip));
        // content
        List<Help.HelpContent> contentList = new ArrayList<>();
        Help.HelpContent c1 = new Help.HelpContent();
        c1.setQuestion("这个app只有办会员才能玩吗？");
        c1.setAnswer("并不是！！！" +
                "\n相信大家可以感觉到，很多情侣恋爱app中的收费项，在我们这里都是免费的，能免费的我们一定不会收费。" +
                "\n而我们的会员一般是和存储空间挂钩的，当用户空间超出了免费的份额之后，就有必要办会员了。");
        contentList.add(c1);
        Help.HelpContent c2 = new Help.HelpContent();
        c2.setQuestion("我办了会员了，但是空间还是不够，怎么办？");
        c2.setAnswer("这个这个这个嘛。。。。。。" +
                "\n很多空间我们是算好了的，一般的情侣一辈子也用不完，但不排除需求特别特别特别大的情侣们。" +
                "\n如果真的有情侣们的存储空间不够用了，我们会在后期加大空间，请大家敬请期待！");
        contentList.add(c2);
        help.setContentList(contentList);
        // sub
        List<Help> subList = new ArrayList<>();
        Help s1 = new Help();
        s1.setIndex(Help.INDEX_MORE_BILL);
        s1.setTitle(getString(R.string.pay));
        subList.add(s1);
        help.setSubList(subList);
        return help;
    }

    private Help getHelpMoreCoin(int index) {
        Help help = new Help();
        help.setIndex(index);
        help.setTitle(getString(R.string.coin));
        // content
        List<Help.HelpContent> contentList = new ArrayList<>();
        Help.HelpContent c1 = new Help.HelpContent();
        c1.setQuestion("金币的来源都有哪些？");
        c1.setAnswer("1.用户购买。" +
                "\n2.每日签到，具体给予数量规则，请移步每日签到查看相关文档。" +
                "\n3.参加活动，部分活动参与会给予金币奖励哦。");
        contentList.add(c1);
        Help.HelpContent c2 = new Help.HelpContent();
        c2.setQuestion("金币有什么用？");
        c2.setAnswer("目前的话，基本是在活动里使用，用于投币，和金币榜挂钩。" +
                "\n其他的金币消费系统正在开发中，敬请期待。。。");
        contentList.add(c2);
        help.setContentList(contentList);
        // sub
        List<Help> subList = new ArrayList<>();
        Help s1 = new Help();
        s1.setIndex(Help.INDEX_MORE_BILL);
        s1.setTitle(getString(R.string.pay));
        subList.add(s1);
        help.setSubList(subList);
        return help;
    }

    private Help getHelpMoreBill(int index) {
        Help help = new Help();
        help.setIndex(index);
        help.setTitle(getString(R.string.pay));
        // content
        List<Help.HelpContent> contentList = new ArrayList<>();
        Help.HelpContent c1 = new Help.HelpContent();
        c1.setQuestion("支付失败了怎么办？");
        c1.setAnswer("亲，看看有没有什么提示信息，可以移步《意见反馈》中反馈给我们，或者是移步《关于我们》中，联系客服人员帮您解决问题。");
        contentList.add(c1);
        Help.HelpContent c2 = new Help.HelpContent();
        c2.setQuestion("支付成功了，但是买的东西没有给我？");
        c2.setAnswer("在支付按钮的下方会有提示，点击相关提示，可更新购买商品状况。" +
                "\n如果以上办法不能解决，请大人您一定要反馈给我们，任何方式都行(首推找客服人员)，我们会第一时间为您解决问题！" +
                "\n任何因我们的失误，导致用户服务购买失败的，我们会第一时间补救，请一定要相信我们的服务态度。");
        contentList.add(c2);
        help.setContentList(contentList);
        return help;
    }

    private Help getHelpMoreSign(int index) {
        Limit limit = SPHelper.getLimit();
        int minCount = limit.getCoinSignMinCount();
        int increaseCount = limit.getCoinSignIncreaseCount();
        int maxCount = limit.getCoinSignMaxCount();
        Help help = new Help();
        help.setIndex(index);
        help.setTitle(getString(R.string.sign));
        // content
        List<Help.HelpContent> contentList = new ArrayList<>();
        Help.HelpContent c1 = new Help.HelpContent();
        c1.setQuestion("签到有什么用？");
        c1.setAnswer("目前只有给予金币这个用途，不排除后续会开发其他的功能，如签到排行榜等！");
        contentList.add(c1);
        Help.HelpContent c2 = new Help.HelpContent();
        c2.setQuestion("为什么我今天没签到，缺提示我签到了？");
        c2.setAnswer("配对中只要有一个人签到就可以了呢。");
        contentList.add(c2);
        Help.HelpContent c3 = new Help.HelpContent();
        c3.setQuestion("签到给予的金币规则是？");
        c3.setAnswer("1.第一次签到或者是不连续签到，给予" + minCount + "个金币。" +
                "\n2.连续签到的话，根据连续天数的累计，额外给予(连续天数-1)*" + increaseCount + "个金币" +
                "\n3.签到给予的金币有封顶，最多每天获得" + maxCount + "个金币");
        contentList.add(c3);
        help.setContentList(contentList);
        return help;
    }

    private Help getHelpMoreMatch(int index) {
        Help help = new Help();
        help.setIndex(index);
        help.setTitle(getString(R.string.nav_match));
        // content
        List<Help.HelpContent> contentList = new ArrayList<>();
        Help.HelpContent c1 = new Help.HelpContent();
        c1.setQuestion("参与会得金币吗？");
        c1.setAnswer("是的呢？具体奖励额度请移步每一期活动的介绍查看。");
        contentList.add(c1);
        Help.HelpContent c2 = new Help.HelpContent();
        c2.setQuestion("为什么有的活动没有开启？");
        c2.setAnswer("活动的开启视运营情况和用户需求而定，不定期开启哦！");
        contentList.add(c2);
        Help.HelpContent c3 = new Help.HelpContent();
        c3.setQuestion("怎么查看往期的活动？");
        c3.setAnswer("如果活动没有开启的话，会默认进入往期活动列表页。" +
                "\n如果活动开启了，可以点击顶部本期介绍(也就是顶部那个渐变的视图)进入往期列表。");
        contentList.add(c3);
        Help.HelpContent c4 = new Help.HelpContent();
        c4.setQuestion("怎么查看自己的发表？");
        c4.setAnswer("如果是刚发表的，可以根据《新人榜》查看作品，毕竟是按发表时间倒叙排序的，找找看吧。" +
                "\n如果新人榜里找不到，请点击顶部本期介绍进入往期列表页，里面有《我们的》相关作品。");
        contentList.add(c4);
        Help.HelpContent c5 = new Help.HelpContent();
        c5.setQuestion("为什么往期的作品不能操作了？");
        c5.setAnswer("往期的作品除了删除，不能进行任何操作哦，包括点赞，投币，发表等。" +
                "\n另外，往期的作品是不能查看新人榜的！");
        contentList.add(c5);
        help.setContentList(contentList);
        return help;
    }

    private Help getHelpOther(int index) {
        Help help = new Help();
        help.setIndex(index);
        help.setTitle(getString(R.string.other));
        help.setDesc("我们的目的很简单，让用户看到我们的诚意。");
        // content
        List<Help.HelpContent> contentList = new ArrayList<>();
        Help.HelpContent c1 = new Help.HelpContent();
        c1.setQuestion("app的操作习惯是？");
        c1.setAnswer("我们的口号是：没事下拉刷新，有事长按看看！");
        contentList.add(c1);
        Help.HelpContent c2 = new Help.HelpContent();
        c2.setQuestion("app的设计风格是？");
        c2.setAnswer("完全参照流行的由Google提出的MaterialDesign的理念来设计的，主要实现方式为卡片式布局+沉浸式体验。" +
                "\n注：产品的开发和设计均有我们的BOSS来负责，BOSS是个苦逼程序猿(大家没有听到)，做出来的产品也是好坏参半。" +
                "\n优点是，能用代码解决的事情绝对不用美工，插画少，极简风格。" +
                "\n缺点是，太极简了，这里小的还请大家多多适应。" +
                "\n如果用户反响界面设计风格不好的话，我们会努力迭代新的界面，给大家一个亮瞎眼的app。");
        contentList.add(c2);
        Help.HelpContent c3 = new Help.HelpContent();
        c3.setQuestion("app的开发理念是？");
        c3.setAnswer("上面说过了，BOSS是程序猿，而且特别遵守行业规范，每天都要教导我们不要瞎搞事！" +
                "\n1.基本禁止后台活动，app基本不会在未获得用户允许的情况下开启后台活动，导致设备卡顿。" +
                "\n2.不会频繁的与后台做数据交互，这个权力都控制在用户手里，比如位置的下拉刷新。" +
                "\n3.尽量减小apk大小，压到10M以下！能不用框架就不用框架，能用矢量图就不要用位图。" +
                "\n4.一定不要卡顿，尽量提升界面的响应速度(设备性能也有影响)，线程处理要做好。" +
                "\n5.坚持原生开发！市面上虽然很多app都采用混合开发，但是我们不会！BOSS极力反对混合开发，虽然混合开发可以节约公司人力成本，缩短开发周期。但是！但是！导致的后续结果就是app性能差，我们宁愿多花点时间，多招点人来做好这个app，也不会选择让用户来承受卡卡西般的体验。");
        contentList.add(c3);
        Help.HelpContent c4 = new Help.HelpContent();
        c4.setQuestion("上传的图片会压缩吗？");
        c4.setAnswer("注意了，凡事有单张大小上限的是不会压缩的，没有上限的都会压缩。" +
                "\n压缩的话，可以放心，我们的方式很温柔，尽量减少对图片清晰度的丢失。");
        contentList.add(c4);
        Help.HelpContent c5 = new Help.HelpContent();
        c5.setQuestion("强迫症福音？");
        c5.setAnswer("相信很多来这个app的用户，都是记录狂魔，多多少少带点强迫症。我们这里也为强迫症修改了很多设计。" +
                "\n1.消息提醒：很多app采用小红点和通知来处理消息提醒。我们的小红点只在设置里有，事关公告和版本更新。所以发现小红点的时候，建议大家看一下，很重要哦。通知的话，我们没事不会给大人们推送的。" +
                "\n2.版本更新：就算有新版本也只是出个小红点，绝不会在app开启的时候弹对话框来打扰用户。(新版本最好更新一下，体积不大，速度很快哦，xiu的一下~)" +
                "\n3.界面排版：'往左点，再往左点，最后往右点，这下舒服了吧。'(对话来自BOSS和美工的日常交流)");
        contentList.add(c5);
        help.setContentList(contentList);
        // sub
        List<Help> subList = new ArrayList<>();
        Help s1 = new Help();
        s1.setIndex(Help.INDEX_USER_SUGGEST);
        s1.setTitle(getString(R.string.suggest_feedback));
        subList.add(s1);
        help.setSubList(subList);
        return help;
    }

    private Help getHelpSuggestHome(int index) {
        Help help = new Help();
        help.setIndex(index);
        help.setTitle(getString(R.string.suggest_feedback));
        help.setDesc("旨在让大家来提出app的bug和意见等，我们会在第一时间收到您的消息，并和相关负责部门商讨问题的解决方案等，并会在产出结果的第一时间以官方回复形式通知用户。" +
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
