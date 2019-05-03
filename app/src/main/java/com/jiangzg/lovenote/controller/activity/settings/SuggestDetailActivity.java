package com.jiangzg.lovenote.controller.activity.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.jiangzg.base.common.DateUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.ScreenUtils;
import com.jiangzg.base.view.ViewUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.controller.activity.common.BigImageActivity;
import com.jiangzg.lovenote.controller.adapter.settings.SuggestCommentAdapter;
import com.jiangzg.lovenote.helper.common.ListHelper;
import com.jiangzg.lovenote.helper.common.RxBus;
import com.jiangzg.lovenote.helper.common.TimeHelper;
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.helper.view.DialogHelper;
import com.jiangzg.lovenote.helper.view.RecyclerHelper;
import com.jiangzg.lovenote.helper.view.ViewHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.Suggest;
import com.jiangzg.lovenote.model.entity.SuggestFollow;
import com.jiangzg.lovenote.view.FrescoView;
import com.jiangzg.lovenote.view.GSwipeRefreshLayout;
import com.jiangzg.lovenote.view.GWrapView;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import rx.Observable;

public class SuggestDetailActivity extends BaseActivity<SuggestDetailActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.ivFollow)
    ImageView ivFollow;
    @BindView(R.id.tvFollow)
    TextView tvFollow;
    @BindView(R.id.ivComment)
    ImageView ivComment;
    @BindView(R.id.tvComment)
    TextView tvComment;

    private Suggest suggest;
    private RecyclerHelper recyclerHelper;
    private int page = 0;

    public static void goActivity(Activity from, Suggest suggest) {
        Intent intent = new Intent(from, SuggestDetailActivity.class);
        intent.putExtra("suggest", suggest);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Context from, long sid) {
        if (sid <= 0) return;
        Suggest suggest = new Suggest();
        suggest.setId(sid);
        Intent intent = new Intent(from, SuggestDetailActivity.class);
        intent.putExtra("suggest", suggest);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_suggest_detail;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.suggest_feedback), true);
        // init
        suggest = intent.getParcelableExtra("suggest");
        // recycler
        recyclerHelper = new RecyclerHelper(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initRefresh(srl, true)
                .initAdapter(new SuggestCommentAdapter(mActivity))
                .viewHeader(mActivity, R.layout.list_head_suggest_comment)
                .viewEmpty(mActivity, R.layout.list_empty_grey, true, true)
                .viewLoadMore(new RecyclerHelper.MoreGreyView())
                .viewAnim()
                .setAdapter()
                .listenerRefresh(() -> getCommentData(false))
                .listenerMore(currentCount -> getCommentData(true))
                .listenerClick(new OnItemLongClickListener() {
                    @Override
                    public void onSimpleItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                        SuggestCommentAdapter commentAdapter = (SuggestCommentAdapter) adapter;
                        commentAdapter.showDeleteDialog(position);
                    }
                });
        // head
        initHead();
        // follow
        initFollowView();
        // comment
        initCommentView();
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        // event
        Observable<Suggest> obDetailRefresh = RxBus.register(RxBus.EVENT_SUGGEST_DETAIL_REFRESH, suggest -> refreshSuggest());
        pushBus(RxBus.EVENT_SUGGEST_DETAIL_REFRESH, obDetailRefresh);
        // refresh
        //recyclerHelper.dataRefresh();
        refreshSuggest();
    }

    @Override
    protected void onFinish(Bundle state) {
        RecyclerHelper.release(recyclerHelper);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if (suggest != null && suggest.isMine()) {
            getMenuInflater().inflate(R.menu.del, menu);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuDel: // 删除
                showDelDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.llFollow, R.id.llComment})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.llFollow: // 关注
                follow(true);
                break;
            case R.id.llComment: // 评论
                if (suggest == null || suggest.getId() == 0) return;
                SuggestCommentAddActivity.goActivity(mActivity, suggest.getId());
                break;
        }
    }

    public void refreshSuggest() {
        if (suggest == null) return;
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        Call<Result> api = new RetrofitHelper().call(API.class).setSuggestGet(suggest.getId());
        RetrofitHelper.enqueue(api, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
                suggest = data.getSuggest();
                // view
                initHead();
                initFollowView();
                initCommentView();
                mActivity.invalidateOptionsMenu();
                // data
                if (recyclerHelper != null) recyclerHelper.dataRefresh();
                // event
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_SUGGEST_LIST_ITEM_REFRESH, suggest));
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
            }
        });
        pushApi(api);
    }

    private void initHead() {
        // data
        if (suggest == null || recyclerHelper == null) return;
        boolean top = suggest.isTop();
        boolean official = suggest.isOfficial();
        boolean mine = suggest.isMine();
        String statusShow = ListHelper.getSuggestStatusShow(suggest.getStatus());
        String kindShow = ListHelper.getSuggestKindShow(suggest.getKind());
        String title = suggest.getTitle();
        String create = DateUtils.getStr(TimeHelper.getJavaTimeByGo(suggest.getCreateAt()), DateUtils.FORMAT_LINE_M_D_H_M);
        final String contentImgUrl = suggest.getContentImage();
        String contentText = suggest.getContentText();
        // view
        View head = recyclerHelper.getViewHead();
        if (head == null) return;
        TextView tvTitle = head.findViewById(R.id.tvTitle);
        TextView tvCreateAt = head.findViewById(R.id.tvCreateAt);
        GWrapView wvTag = head.findViewById(R.id.wvTag);
        TextView tvContent = head.findViewById(R.id.tvContent);
        FrescoView ivContent = head.findViewById(R.id.ivContent);
        ViewGroup.LayoutParams layoutParams = ivContent.getLayoutParams();
        ivContent.setWidthAndHeight(ScreenUtils.getScreenWidth(mActivity), layoutParams.height);
        // data
        wvTag.removeAllChild();
        if (top) {
            View tagTop = ViewHelper.getWrapTextView(mActivity, mActivity.getString(R.string.top));
            wvTag.addChild(tagTop);
        }
        if (official) {
            View tagOfficial = ViewHelper.getWrapTextView(mActivity, mActivity.getString(R.string.administrators));
            wvTag.addChild(tagOfficial);
        }
        View tagStatus = ViewHelper.getWrapTextView(mActivity, statusShow);
        wvTag.addChild(tagStatus);
        View tagKind = ViewHelper.getWrapTextView(mActivity, kindShow);
        wvTag.addChild(tagKind);
        if (mine) {
            View tagMine = ViewHelper.getWrapTextView(mActivity, mActivity.getString(R.string.me_de));
            wvTag.addChild(tagMine);
        }
        tvTitle.setText(title);
        tvCreateAt.setText(create);
        tvContent.setText(contentText);
        if (StringUtils.isEmpty(contentImgUrl)) {
            ivContent.setVisibility(View.GONE);
            ivContent.setClickListener(null);
        } else {
            ivContent.setVisibility(View.VISIBLE);
            ivContent.setData(contentImgUrl);
            ivContent.setClickListener(iv -> BigImageActivity.goActivityByOss(mActivity, contentImgUrl, iv));
        }
    }

    private void getCommentData(final boolean more) {
        if (suggest == null) return;
        page = more ? page + 1 : 0;
        // api
        Call<Result> api = new RetrofitHelper().call(API.class).setSuggestCommentListGet(suggest.getId(), page);
        RetrofitHelper.enqueue(api, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.dataOk(data.getShow(), data.getSuggestCommentList(), more);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.dataFail(more, message);
            }
        });
        pushApi(api);
    }

    private void initFollowView() {
        if (suggest == null) return;
        tvFollow.setText(String.valueOf(suggest.getFollowCount()));
        if (suggest.isFollow()) {
            Drawable visibility = ContextCompat.getDrawable(mActivity, R.mipmap.ic_visibility_grey_18dp);
            if (visibility != null) {
                visibility.setTint(ContextCompat.getColor(mActivity, ViewUtils.getColorPrimary(mActivity)));
                ivFollow.setImageDrawable(visibility);
            }
        } else {
            ivFollow.setImageResource(R.mipmap.ic_visibility_off_grey_18dp);
        }
    }

    private void initCommentView() {
        if (suggest == null) return;
        tvComment.setText(String.valueOf(suggest.getCommentCount()));
        if (suggest.isComment()) {
            int colorPrimary = ContextCompat.getColor(mActivity, ViewUtils.getColorPrimary(mActivity));
            ivComment.setImageTintList(ColorStateList.valueOf(colorPrimary));
        } else {
            int colorGrey = ContextCompat.getColor(mActivity, R.color.icon_grey);
            ivComment.setImageTintList(ColorStateList.valueOf(colorGrey));
        }
    }

    // 关注
    private void follow(boolean isApi) {
        boolean newFollow = !suggest.isFollow();
        int newFollowCount = newFollow ? suggest.getFollowCount() + 1 : suggest.getFollowCount() - 1;
        if (newFollowCount < 0) {
            newFollowCount = 0;
        }
        suggest.setFollow(newFollow);
        suggest.setFollowCount(newFollowCount);
        initFollowView();
        if (!isApi) return;
        SuggestFollow suggestFollow = new SuggestFollow(suggest.getId());
        // api
        Call<Result> api = new RetrofitHelper().call(API.class).setSuggestFollowToggle(suggestFollow);
        RetrofitHelper.enqueue(api, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_SUGGEST_LIST_ITEM_REFRESH, suggest));
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                follow(false);
            }
        });
        pushApi(api);
    }

    private void showDelDialog() {
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .content(R.string.confirm_del_this_suggest)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive((dialog1, which) -> delSuggest())
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    // 删除意见
    private void delSuggest() {
        if (suggest == null) return;
        Call<Result> api = new RetrofitHelper().call(API.class).setSuggestDel(suggest.getId());
        RetrofitHelper.enqueue(api, getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_SUGGEST_LIST_ITEM_DELETE, suggest));
                mActivity.finish();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
        pushApi(api);
    }

}
