package com.jiangzg.lovenote.activity.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.system.InputUtils;
import com.jiangzg.base.view.ScreenUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.activity.common.BigImageActivity;
import com.jiangzg.lovenote.adapter.SuggestCommentAdapter;
import com.jiangzg.lovenote.base.BaseActivity;
import com.jiangzg.lovenote.domain.Result;
import com.jiangzg.lovenote.domain.RxEvent;
import com.jiangzg.lovenote.domain.Suggest;
import com.jiangzg.lovenote.domain.SuggestComment;
import com.jiangzg.lovenote.domain.SuggestFollow;
import com.jiangzg.lovenote.domain.SuggestInfo;
import com.jiangzg.lovenote.helper.API;
import com.jiangzg.lovenote.helper.ApiHelper;
import com.jiangzg.lovenote.helper.ConsHelper;
import com.jiangzg.lovenote.helper.DialogHelper;
import com.jiangzg.lovenote.helper.RecyclerHelper;
import com.jiangzg.lovenote.helper.RetrofitHelper;
import com.jiangzg.lovenote.helper.RxBus;
import com.jiangzg.lovenote.helper.SPHelper;
import com.jiangzg.lovenote.helper.TimeHelper;
import com.jiangzg.lovenote.helper.ViewHelper;
import com.jiangzg.lovenote.view.FrescoView;
import com.jiangzg.lovenote.view.GSwipeRefreshLayout;
import com.jiangzg.lovenote.view.GWrapView;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import retrofit2.Call;
import rx.Observable;
import rx.functions.Action1;

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
    @BindView(R.id.tvCommentLimit)
    TextView tvCommentLimit;
    @BindView(R.id.tvComment)
    TextView tvComment;
    @BindView(R.id.rlComment)
    RelativeLayout rlComment;
    @BindView(R.id.etComment)
    EditText etComment;

    private Suggest suggest;
    private RecyclerHelper recyclerHelper;
    private BottomSheetBehavior behaviorComment;
    private Call<Result> callGet;
    private Call<Result> callDel;
    private Call<Result> callCommentAdd;
    private Call<Result> callCommentListGet;
    private Call<Result> callFollow;
    private Observable<Suggest> obDetailRefresh;
    private int page, limitCommentContentLength;

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
                .initRefresh(srl, false)
                .initAdapter(new SuggestCommentAdapter(mActivity))
                .viewHeader(mActivity, R.layout.list_head_suggest_comment)
                .viewEmpty(mActivity, R.layout.list_empty_grey, true, true)
                .viewLoadMore(new RecyclerHelper.MoreGreyView())
                .setAdapter()
                .listenerRefresh(new RecyclerHelper.RefreshListener() {
                    @Override
                    public void onRefresh() {
                        getCommentData(false);
                    }
                })
                .listenerMore(new RecyclerHelper.MoreListener() {
                    @Override
                    public void onMore(int currentCount) {
                        getCommentData(true);
                    }
                })
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
        // comment
        commentShow(false);
        // comment 防止开始显示错误
        etComment.setText("");
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        page = 0;
        // event
        obDetailRefresh = RxBus.register(ConsHelper.EVENT_SUGGEST_DETAIL_REFRESH, new Action1<Suggest>() {
            @Override
            public void call(Suggest suggest) {
                refreshSuggest();
            }
        });
        // refresh
        //recyclerHelper.dataRefresh();
        refreshSuggest();
    }

    @Override
    protected void onFinish(Bundle state) {
        RetrofitHelper.cancel(callGet);
        RetrofitHelper.cancel(callDel);
        RetrofitHelper.cancel(callFollow);
        RetrofitHelper.cancel(callCommentListGet);
        RetrofitHelper.cancel(callCommentAdd);
        RxBus.unregister(ConsHelper.EVENT_SUGGEST_DETAIL_REFRESH, obDetailRefresh);
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
    public void onBackPressed() {
        if (behaviorComment.getState() != BottomSheetBehavior.STATE_HIDDEN) {
            behaviorComment.setState(BottomSheetBehavior.STATE_HIDDEN);
        } else {
            super.onBackPressed();
        }
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

    @OnTextChanged({R.id.etComment})
    public void afterTextChanged(Editable s) {
        onCommentInput(s.toString());
    }

    @OnClick({R.id.llFollow, R.id.llComment, R.id.ivCommentClose, R.id.tvCommentCommit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.llFollow: // 关注
                follow(true);
                break;
            case R.id.llComment: // 评论打开
                commentShow(true);
                break;
            case R.id.ivCommentClose: // 评论关闭
                commentShow(false);
                break;
            case R.id.tvCommentCommit: // 评论提交
                comment();
                break;
        }
    }

    public void refreshSuggest() {
        if (suggest == null) return;
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        callGet = new RetrofitHelper().call(API.class).setSuggestGet(suggest.getId());
        RetrofitHelper.enqueue(callGet, null, new RetrofitHelper.CallBack() {
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
                RxEvent<Suggest> event = new RxEvent<>(ConsHelper.EVENT_SUGGEST_LIST_ITEM_REFRESH, suggest);
                RxBus.post(event);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
            }
        });
    }

    private void initHead() {
        // data
        if (suggest == null || recyclerHelper == null) return;
        boolean top = suggest.isTop();
        boolean official = suggest.isOfficial();
        boolean mine = suggest.isMine();
        String statusShow = SuggestInfo.getStatusShow(suggest.getStatus());
        String kindShow = SuggestInfo.getKindShow(suggest.getKind());
        String title = suggest.getTitle();
        String create = TimeHelper.getTimeShowLine_HM_MD_YMD_ByGo(suggest.getCreateAt());
        String createShow = String.format(Locale.getDefault(), getString(R.string.create_at_colon_space_holder), create);
        final String contentImgUrl = suggest.getContentImage();
        String contentText = suggest.getContentText();
        // view
        View head = recyclerHelper.getViewHead();
        TextView tvTitle = head.findViewById(R.id.tvTitle);
        TextView tvCreateAt = head.findViewById(R.id.tvCreateAt);
        GWrapView wvTag = head.findViewById(R.id.wvTag);
        TextView tvContent = head.findViewById(R.id.tvContent);
        // imageView
        FrescoView ivContent = head.findViewById(R.id.ivContent);
        ViewGroup.LayoutParams layoutParams = ivContent.getLayoutParams();
        ivContent.setWidthAndHeight(ScreenUtils.getScreenWidth(mActivity), layoutParams.height);
        // tagView
        wvTag.removeAllChild();
        if (top) {
            View tagTop = ViewHelper.getWrapTextView(mActivity, mActivity.getString(R.string.top));
            wvTag.addChild(tagTop);
        }
        if (official) {
            View tagOfficial = ViewHelper.getWrapTextView(mActivity, mActivity.getString(R.string.official));
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
        // otherView
        tvTitle.setText(title);
        tvCreateAt.setText(createShow);
        tvContent.setText(contentText);
        if (StringUtils.isEmpty(contentImgUrl)) {
            ivContent.setVisibility(View.GONE);
            ivContent.setClickListener(null);
        } else {
            ivContent.setVisibility(View.VISIBLE);
            ivContent.setData(contentImgUrl);
            ivContent.setClickListener(new FrescoView.ClickListener() {
                @Override
                public void onSuccessClick(FrescoView iv) {
                    BigImageActivity.goActivityByOss(mActivity, contentImgUrl, iv);
                }
            });
        }
    }

    private void getCommentData(final boolean more) {
        if (suggest == null) return;
        page = more ? page + 1 : 0;
        // api
        callCommentListGet = new RetrofitHelper().call(API.class).setSuggestCommentListGet(suggest.getId(), page);
        RetrofitHelper.enqueue(callCommentListGet, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.viewEmptyShow(data.getShow());
                List<SuggestComment> suggestCommentList = data.getSuggestCommentList();
                recyclerHelper.dataOk(suggestCommentList, more);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.dataFail(more, message);
            }
        });
    }

    private void initFollowView() {
        if (suggest == null) return;
        // data
        boolean follow = suggest.isFollow();
        String followCount = String.valueOf(suggest.getFollowCount());
        // view
        tvFollow.setText(followCount);
        if (follow) {
            ivFollow.setImageResource(R.drawable.ic_visibility_on_primary);
        } else {
            ivFollow.setImageResource(R.drawable.ic_visibility_off_grey);
        }
    }

    private void initCommentView() {
        if (suggest == null) return;
        // data
        boolean isComment = suggest.isComment();
        String commentCount = String.valueOf(suggest.getCommentCount());
        // view
        tvComment.setText(commentCount);
        if (isComment) {
            int rId = ViewHelper.getColorPrimary(mActivity);
            int colorPrimary = ContextCompat.getColor(mActivity, rId);
            ivComment.setImageTintList(ColorStateList.valueOf(colorPrimary));
        } else {
            int colorGrey = ContextCompat.getColor(mActivity, R.color.icon_grey);
            ivComment.setImageTintList(ColorStateList.valueOf(colorGrey));
        }
    }

    private void onCommentInput(String input) {
        if (limitCommentContentLength <= 0) {
            limitCommentContentLength = SPHelper.getLimit().getSuggestCommentContentLength();
        }
        int length = input.length();
        if (length > limitCommentContentLength) {
            CharSequence charSequence = input.subSequence(0, limitCommentContentLength);
            etComment.setText(charSequence);
            etComment.setSelection(charSequence.length());
            length = charSequence.length();
        }
        String limitShow = String.format(Locale.getDefault(), getString(R.string.holder_sprit_holder), length, limitCommentContentLength);
        tvCommentLimit.setText(limitShow);
    }

    // 评论视图
    private void commentShow(boolean show) {
        if (behaviorComment == null) {
            behaviorComment = BottomSheetBehavior.from(rlComment);
        }
        int state = show ? BottomSheetBehavior.STATE_COLLAPSED : BottomSheetBehavior.STATE_HIDDEN;
        behaviorComment.setState(state);
        if (!show) InputUtils.hideSoftInput(etComment);
    }

    // 关注
    private void follow(boolean api) {
        boolean newFollow = !suggest.isFollow();
        long newFollowCount = newFollow ? suggest.getFollowCount() + 1 : suggest.getFollowCount() - 1;
        if (newFollowCount < 0) {
            newFollowCount = 0;
        }
        suggest.setFollow(newFollow);
        suggest.setFollowCount(newFollowCount);
        initFollowView();
        if (!api) return;
        SuggestFollow suggestFollow = new SuggestFollow(suggest.getId());
        callFollow = new RetrofitHelper().call(API.class).setSuggestFollowToggle(suggestFollow);
        RetrofitHelper.enqueue(callFollow, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxEvent<Suggest> event = new RxEvent<>(ConsHelper.EVENT_SUGGEST_LIST_ITEM_REFRESH, suggest);
                RxBus.post(event);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                follow(false);
            }
        });
    }

    // 评论
    private void comment() {
        if (suggest == null) return;
        InputUtils.hideSoftInput(etComment);
        MaterialDialog loading = getLoading(true);
        String content = etComment.getText().toString();
        SuggestComment body = ApiHelper.getSuggestCommentAddBody(suggest.getId(), content);
        callCommentAdd = new RetrofitHelper().call(API.class).setSuggestCommentAdd(body);
        RetrofitHelper.enqueue(callCommentAdd, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                etComment.setText("");
                commentShow(false);
                getCommentData(false);
                refreshSuggest();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

    private void showDelDialog() {
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .content(R.string.confirm_del_suggest)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        delSuggest();
                    }
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    // 删除意见
    private void delSuggest() {
        if (suggest == null) return;
        MaterialDialog loading = getLoading(getString(R.string.are_deleting), true);
        callDel = new RetrofitHelper().call(API.class).setSuggestDel(suggest.getId());
        RetrofitHelper.enqueue(callDel, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxEvent<Suggest> event = new RxEvent<>(ConsHelper.EVENT_SUGGEST_LIST_ITEM_DELETE, suggest);
                RxBus.post(event);
                mActivity.finish();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

}
