package com.jiangzg.lovenote.controller.activity.settings;

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
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.common.TimeUnit;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.controller.adapter.settings.HelpContentAdapter;
import com.jiangzg.lovenote.controller.adapter.settings.HelpSubAdapter;
import com.jiangzg.lovenote.helper.common.SPHelper;
import com.jiangzg.lovenote.helper.view.RecyclerHelper;
import com.jiangzg.lovenote.helper.view.ViewHelper;
import com.jiangzg.lovenote.model.engine.Help;
import com.jiangzg.lovenote.model.entity.Limit;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;

public class HelpActivity extends BaseActivity<HelpActivity> {

    public static final int INDEX_HOME = 0;
    // couple
    public static final int INDEX_COUPLE_HOME = 100;
    public static final int INDEX_COUPLE_PAIR = 110;
    public static final int INDEX_COUPLE_INFO = 120;
    // note
    public static final int INDEX_NOTE_HOME = 200;
    public static final int INDEX_NOTE_LOCK = 210;
    public static final int INDEX_NOTE_SOUVENIR = 220;
    public static final int INDEX_NOTE_SHY = 221;
    public static final int INDEX_NOTE_MENSES = 222;
    public static final int INDEX_NOTE_SLEEP = 223;
    public static final int INDEX_NOTE_WORD = 230;
    public static final int INDEX_NOTE_WHISPER = 231;
    public static final int INDEX_NOTE_AWARD = 232;
    public static final int INDEX_NOTE_DREAM = 233;
    public static final int INDEX_NOTE_TRAVEL = 234;
    public static final int INDEX_NOTE_PROMISE = 235;
    // topic
    public static final int INDEX_TOPIC_HOME = 300;
    public static final int INDEX_TOPIC_POST = 310;
    // more
    public static final int INDEX_MORE_HOME = 400;
    public static final int INDEX_MORE_VIP = 410;
    public static final int INDEX_MORE_COIN = 420;
    public static final int INDEX_MORE_BILL = 430;
    public static final int INDEX_MORE_SIGN = 440;
    public static final int INDEX_MORE_MATCH = 450;
    // other
    public static final int INDEX_OTHER = 500;
    public static final int INDEX_USER_SUGGEST = 510;

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.rv)
    RecyclerView rv;

    private RecyclerHelper recyclerHelper;
    private RecyclerHelper recyclerHelperHead;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, HelpActivity.class);
        intent.putExtra("index", INDEX_HOME);
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
        int index = intent.getIntExtra("index", INDEX_HOME);
        Help help = getHelpByIndex(index);
        if (help == null) return;
        tb.setTitle(help.getTitle());
        initHead(help);
        recyclerHelper.dataNew(help.getSubList(), 0);
    }

    @Override
    protected void onFinish(Bundle state) {
        RecyclerHelper.release(recyclerHelper);
        RecyclerHelper.release(recyclerHelperHead);
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
            rv.setEnabled(false); // 不加一进来会抢焦点
            rv.setFocusable(false);
            rv.setFocusableInTouchMode(false);
            rv.setNestedScrollingEnabled(false);
            if (recyclerHelperHead == null) {
                recyclerHelperHead = new RecyclerHelper(rv)
                        .initLayoutManager(new LinearLayoutManager(mActivity))
                        .initAdapter(new HelpContentAdapter());
            }
            recyclerHelperHead.dataNew(contentList, 0);
        }
    }

    private Help getHelpByIndex(int index) {
        switch (index) {
            case INDEX_HOME:
            default:
                return getHelpHome();
            case INDEX_COUPLE_HOME:
                return getHelpCoupleHome(index);
            case INDEX_COUPLE_PAIR:
                return getHelpCouplePair(index);
            case INDEX_COUPLE_INFO:
                return getHelpCoupleInfo(index);
            case INDEX_NOTE_HOME:
                return getHelpNoteHome(index);
            case INDEX_NOTE_LOCK:
                return getHelpNoteLock(index);
            case INDEX_NOTE_SOUVENIR:
                return getHelpNoteSouvenir(index);
            case INDEX_NOTE_SHY:
                return getHelpNoteShy(index);
            case INDEX_NOTE_MENSES:
                return getHelpNoteMenses(index);
            case INDEX_NOTE_SLEEP:
                return getHelpNoteSleep(index);
            case INDEX_NOTE_WORD:
                return getHelpNoteWork(index);
            case INDEX_NOTE_WHISPER:
                return getHelpNoteWhisper(index);
            case INDEX_NOTE_AWARD:
                return getHelpNoteAward(index);
            case INDEX_NOTE_DREAM:
                return getHelpNoteDream(index);
            case INDEX_NOTE_TRAVEL:
                return getHelpNoteTravel(index);
            case INDEX_NOTE_PROMISE:
                return getHelpNotePromise(index);
            case INDEX_TOPIC_HOME:
                return getHelpTopicHome(index);
            case INDEX_TOPIC_POST:
                return getHelpTopicPost(index);
            case INDEX_MORE_HOME:
                return getHelpMoreHome(index);
            case INDEX_MORE_VIP:
                return getHelpMoreVip(index);
            case INDEX_MORE_COIN:
                return getHelpMoreCoin(index);
            case INDEX_MORE_BILL:
                return getHelpMoreBill(index);
            case INDEX_MORE_SIGN:
                return getHelpMoreSign(index);
            case INDEX_MORE_MATCH:
                return getHelpMoreMatch(index);
            case INDEX_OTHER:
                return getHelpOther(index);
            case INDEX_USER_SUGGEST:
                return getHelpSuggestHome(index);
        }
    }

    private Help getHelpHome() {
        Help help = new Help();
        help.setIndex(INDEX_HOME);
        help.setTitle(getString(R.string.help_document));
        help.setDesc(getString(R.string.help_home_d));
        // content
        List<Help.HelpContent> contentList = new ArrayList<>();
        Help.HelpContent c1 = new Help.HelpContent();
        c1.setQuestion(getString(R.string.help_home_c1_q));
        c1.setAnswer(getString(R.string.help_home_c1_a));
        contentList.add(c1);
        Help.HelpContent c2 = new Help.HelpContent();
        c2.setQuestion(getString(R.string.help_home_c2_q));
        c2.setAnswer(String.format(Locale.getDefault(), getString(R.string.help_home_c2_a), getString(R.string.nav_couple), getString(R.string.nav_note), getString(R.string.nav_topic), getString(R.string.nav_more)));
        contentList.add(c2);
        help.setContentList(contentList);
        // sub
        List<Help> subList = new ArrayList<>();
        Help s1 = new Help();
        s1.setIndex(INDEX_COUPLE_HOME);
        s1.setTitle(getString(R.string.nav_couple));
        subList.add(s1);
        Help s2 = new Help();
        s2.setIndex(INDEX_NOTE_HOME);
        s2.setTitle(getString(R.string.nav_note));
        subList.add(s2);
        Help s3 = new Help();
        s3.setIndex(INDEX_TOPIC_HOME);
        s3.setTitle(getString(R.string.nav_topic));
        subList.add(s3);
        Help s4 = new Help();
        s4.setIndex(INDEX_MORE_HOME);
        s4.setTitle(getString(R.string.nav_more));
        subList.add(s4);
        Help s5 = new Help();
        s5.setIndex(INDEX_OTHER);
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
        s1.setIndex(INDEX_COUPLE_PAIR);
        s1.setTitle(getString(R.string.pair));
        subList.add(s1);
        Help s2 = new Help();
        s2.setIndex(INDEX_COUPLE_INFO);
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
        long breakNeedDay = limit.getCoupleBreakNeedSec() / (TimeUnit.DAY / TimeUnit.SEC);
        long breakContinueHour = limit.getCoupleBreakSec() / (TimeUnit.HOUR / TimeUnit.SEC);
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
        Help.HelpContent c3 = new Help.HelpContent();
        c3.setQuestion(getString(R.string.help_note_home_c3_q));
        c3.setAnswer(getString(R.string.help_note_home_c3_a));
        contentList.add(c3);
        help.setContentList(contentList);
        // sub
        List<Help> subList = new ArrayList<>();
        Help s1 = new Help();
        s1.setIndex(INDEX_NOTE_LOCK);
        s1.setTitle(getString(R.string.pwd_lock));
        subList.add(s1);
        Help s11 = new Help();
        s11.setIndex(INDEX_NOTE_SHY);
        s11.setTitle(getString(R.string.shy));
        subList.add(s11);
        Help s12 = new Help();
        s12.setIndex(INDEX_NOTE_MENSES);
        s12.setTitle(getString(R.string.menses));
        subList.add(s12);
        Help s13 = new Help();
        s13.setIndex(INDEX_NOTE_SLEEP);
        s13.setTitle(getString(R.string.sleep));
        subList.add(s13);
        Help s2 = new Help();
        s2.setIndex(INDEX_NOTE_SOUVENIR);
        s2.setTitle(getString(R.string.souvenir));
        subList.add(s2);
        Help s3 = new Help();
        s3.setIndex(INDEX_NOTE_WORD);
        s3.setTitle(getString(R.string.word));
        subList.add(s3);
        Help s4 = new Help();
        s4.setIndex(INDEX_NOTE_WHISPER);
        s4.setTitle(getString(R.string.whisper));
        subList.add(s4);
        Help s6 = new Help();
        s6.setIndex(INDEX_NOTE_AWARD);
        s6.setTitle(getString(R.string.award));
        subList.add(s6);
        Help s7 = new Help();
        s7.setIndex(INDEX_NOTE_DREAM);
        s7.setTitle(getString(R.string.dream));
        subList.add(s7);
        Help s8 = new Help();
        s8.setIndex(INDEX_NOTE_TRAVEL);
        s8.setTitle(getString(R.string.travel));
        subList.add(s8);
        Help s9 = new Help();
        s9.setIndex(INDEX_NOTE_PROMISE);
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

    private Help getHelpNoteShy(int index) {
        Help help = new Help();
        help.setIndex(index);
        help.setTitle(getString(R.string.shy));
        // content
        List<Help.HelpContent> contentList = new ArrayList<>();
        Help.HelpContent c1 = new Help.HelpContent();
        c1.setQuestion(getString(R.string.help_note_shy_c1_q));
        c1.setAnswer(getString(R.string.help_note_shy_c1_a));
        contentList.add(c1);
        Help.HelpContent c2 = new Help.HelpContent();
        c2.setQuestion(getString(R.string.help_note_shy_c2_q));
        c2.setAnswer(getString(R.string.help_note_shy_c2_a));
        contentList.add(c2);
        help.setContentList(contentList);
        return help;
    }

    private Help getHelpNoteMenses(int index) {
        Help help = new Help();
        help.setIndex(index);
        help.setTitle(getString(R.string.menses));
        // content
        List<Help.HelpContent> contentList = new ArrayList<>();
        Help.HelpContent c1 = new Help.HelpContent();
        c1.setQuestion(getString(R.string.help_note_menses_c1_q));
        c1.setAnswer(getString(R.string.help_note_menses_c1_a));
        contentList.add(c1);
        Help.HelpContent c2 = new Help.HelpContent();
        c2.setQuestion(getString(R.string.help_note_menses_c2_q));
        c2.setAnswer(getString(R.string.help_note_menses_c2_a));
        contentList.add(c2);
        help.setContentList(contentList);
        return help;
    }

    private Help getHelpNoteSleep(int index) {
        Help help = new Help();
        help.setIndex(index);
        help.setTitle(getString(R.string.sleep));
        // content
        List<Help.HelpContent> contentList = new ArrayList<>();
        Help.HelpContent c1 = new Help.HelpContent();
        c1.setQuestion(getString(R.string.help_note_sleep_c1_q));
        c1.setAnswer(getString(R.string.help_note_sleep_c1_a));
        contentList.add(c1);
        Help.HelpContent c2 = new Help.HelpContent();
        c2.setQuestion(getString(R.string.help_note_sleep_c2_q));
        c2.setAnswer(getString(R.string.help_note_sleep_c2_a));
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
        Help.HelpContent c3 = new Help.HelpContent();
        c3.setQuestion(getString(R.string.help_note_whisper_c3_q));
        c3.setAnswer(getString(R.string.help_note_whisper_c3_a));
        contentList.add(c3);
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
        int movieCount = limit.getTravelMovieCount();
        int diaryCount = limit.getTravelDiaryCount();
        Help help = new Help();
        help.setIndex(index);
        help.setTitle(getString(R.string.travel));
        help.setDesc(getString(R.string.help_note_travel_d));
        // content
        List<Help.HelpContent> contentList = new ArrayList<>();
        Help.HelpContent c1 = new Help.HelpContent();
        c1.setQuestion(getString(R.string.help_note_travel_c1_q));
        c1.setAnswer(String.format(Locale.getDefault(), getString(R.string.help_note_travel_c1_a), placeCount, albumCount, videoCount, foodCount, movieCount, diaryCount));
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
        s1.setIndex(INDEX_TOPIC_POST);
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
        help.setDesc(getString(R.string.help_more_home_d));
        // sub
        List<Help> subList = new ArrayList<>();
        Help s1 = new Help();
        s1.setIndex(INDEX_MORE_VIP);
        s1.setTitle(getString(R.string.vip));
        subList.add(s1);
        Help s2 = new Help();
        s2.setIndex(INDEX_MORE_COIN);
        s2.setTitle(getString(R.string.coin));
        subList.add(s2);
        Help s3 = new Help();
        s3.setIndex(INDEX_MORE_SIGN);
        s3.setTitle(getString(R.string.sign));
        subList.add(s3);
        Help s4 = new Help();
        s4.setIndex(INDEX_MORE_MATCH);
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
        c1.setQuestion(getString(R.string.help_more_vip_c1_q));
        c1.setAnswer(getString(R.string.help_more_vip_c1_a));
        contentList.add(c1);
        Help.HelpContent c2 = new Help.HelpContent();
        c2.setQuestion(getString(R.string.help_more_vip_c2_q));
        c2.setAnswer(getString(R.string.help_more_vip_c2_a));
        contentList.add(c2);
        Help.HelpContent c3 = new Help.HelpContent();
        c3.setQuestion(getString(R.string.help_more_vip_c3_q));
        c3.setAnswer(getString(R.string.help_more_vip_c3_a));
        contentList.add(c3);
        help.setContentList(contentList);
        // sub
        List<Help> subList = new ArrayList<>();
        Help s1 = new Help();
        s1.setIndex(INDEX_MORE_BILL);
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
        c1.setQuestion(getString(R.string.help_more_coin_c1_q));
        c1.setAnswer(getString(R.string.help_more_coin_c1_a));
        contentList.add(c1);
        Help.HelpContent c2 = new Help.HelpContent();
        c2.setQuestion(getString(R.string.help_more_coin_c2_q));
        c2.setAnswer(getString(R.string.help_more_coin_c2_a));
        contentList.add(c2);
        help.setContentList(contentList);
        // sub
        List<Help> subList = new ArrayList<>();
        Help s1 = new Help();
        s1.setIndex(INDEX_MORE_BILL);
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
        c1.setQuestion(getString(R.string.help_more_bill_c1_q));
        c1.setAnswer(getString(R.string.help_more_bill_c1_a));
        contentList.add(c1);
        Help.HelpContent c2 = new Help.HelpContent();
        c2.setQuestion(getString(R.string.help_more_bill_c2_q));
        c2.setAnswer(getString(R.string.help_more_bill_c2_a));
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
        c1.setQuestion(getString(R.string.help_more_sign_c1_q));
        c1.setAnswer(getString(R.string.help_more_sign_c1_a));
        contentList.add(c1);
        Help.HelpContent c2 = new Help.HelpContent();
        c2.setQuestion(getString(R.string.help_more_sign_c2_q));
        c2.setAnswer(getString(R.string.help_more_sign_c2_a));
        contentList.add(c2);
        Help.HelpContent c3 = new Help.HelpContent();
        c3.setQuestion(getString(R.string.help_more_sign_c3_q));
        c3.setAnswer(String.format(Locale.getDefault(), getString(R.string.help_more_sign_c3_a), minCount, increaseCount, maxCount));
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
        c1.setQuestion(getString(R.string.help_more_match_c1_q));
        c1.setAnswer(getString(R.string.help_more_match_c1_a));
        contentList.add(c1);
        Help.HelpContent c2 = new Help.HelpContent();
        c2.setQuestion(getString(R.string.help_more_match_c2_q));
        c2.setAnswer(getString(R.string.help_more_match_c2_a));
        contentList.add(c2);
        Help.HelpContent c3 = new Help.HelpContent();
        c3.setQuestion(getString(R.string.help_more_match_c3_q));
        c3.setAnswer(getString(R.string.help_more_match_c3_a));
        contentList.add(c3);
        Help.HelpContent c4 = new Help.HelpContent();
        c4.setQuestion(getString(R.string.help_more_match_c4_q));
        c4.setAnswer(getString(R.string.help_more_match_c4_a));
        contentList.add(c4);
        Help.HelpContent c5 = new Help.HelpContent();
        c5.setQuestion(getString(R.string.help_more_match_c5_q));
        c5.setAnswer(getString(R.string.help_more_match_c5_a));
        contentList.add(c5);
        help.setContentList(contentList);
        return help;
    }

    private Help getHelpOther(int index) {
        Help help = new Help();
        help.setIndex(index);
        help.setTitle(getString(R.string.other));
        // sub
        List<Help> subList = new ArrayList<>();
        Help s1 = new Help();
        s1.setIndex(INDEX_USER_SUGGEST);
        s1.setTitle(getString(R.string.suggest_feedback));
        subList.add(s1);
        help.setSubList(subList);
        return help;
    }

    private Help getHelpSuggestHome(int index) {
        Help help = new Help();
        help.setIndex(index);
        help.setTitle(getString(R.string.suggest_feedback));
        help.setDesc(getString(R.string.help_suggest_home_d));
        // content
        List<Help.HelpContent> contentList = new ArrayList<>();
        Help.HelpContent c1 = new Help.HelpContent();
        c1.setQuestion(getString(R.string.help_suggest_home_c1_q));
        c1.setAnswer(getString(R.string.help_suggest_home_c1_a));
        contentList.add(c1);
        Help.HelpContent c2 = new Help.HelpContent();
        c2.setQuestion(getString(R.string.help_suggest_home_c2_q));
        c2.setAnswer(getString(R.string.help_suggest_home_c2_a));
        contentList.add(c2);
        help.setContentList(contentList);
        return help;
    }

}
