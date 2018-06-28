package com.jiangzg.mianmian.activity.settings;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.jiangzg.base.common.ConvertUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.system.InputUtils;
import com.jiangzg.base.view.ScreenUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.common.BigImageActivity;
import com.jiangzg.mianmian.adapter.SuggestCommentAdapter;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Help;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.domain.RxEvent;
import com.jiangzg.mianmian.domain.Suggest;
import com.jiangzg.mianmian.domain.SuggestComment;
import com.jiangzg.mianmian.domain.SuggestInfo;
import com.jiangzg.mianmian.helper.API;
import com.jiangzg.mianmian.helper.ApiHelper;
import com.jiangzg.mianmian.helper.ConsHelper;
import com.jiangzg.mianmian.helper.DialogHelper;
import com.jiangzg.mianmian.helper.RecyclerHelper;
import com.jiangzg.mianmian.helper.RetrofitHelper;
import com.jiangzg.mianmian.helper.RxBus;
import com.jiangzg.mianmian.helper.SPHelper;
import com.jiangzg.mianmian.helper.TimeHelper;
import com.jiangzg.mianmian.helper.ViewHelper;
import com.jiangzg.mianmian.view.FrescoView;
import com.jiangzg.mianmian.view.GSwipeRefreshLayout;
import com.jiangzg.mianmian.view.GWrapView;

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
    private Call<Result> callFollow;
    private Call<Result> callCommentGet;
    private Observable<Suggest> obDetailRefresh;
    private int page;
    private int limitCommentContentLength;

    public static void goActivity(Activity from, Suggest suggest) {
        Intent intent = new Intent(from, SuggestDetailActivity.class);
        intent.putExtra("suggest", suggest);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        page = 0;
        return R.layout.activity_suggest_detail;
    }

    @Override
    protected void initView(Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.suggest_feedback), true);
        // recycler
        recyclerHelper = new RecyclerHelper(mActivity)
                .initRecycler(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initRefresh(srl, false)
                .initAdapter(new SuggestCommentAdapter(mActivity))
                .viewHeader(R.layout.list_head_suggest_comment)
                .viewEmpty(R.layout.list_empty_grey, true, true)
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
        // comment
        commentShow(false);
        // comment 防止开始显示错误
        etComment.setText("");
    }

    @Override
    protected void initData(Bundle state) {
        suggest = getIntent().getParcelableExtra("suggest");
        // event
        obDetailRefresh = RxBus.register(ConsHelper.EVENT_SUGGEST_DETAIL_REFRESH, new Action1<Suggest>() {
            @Override
            public void call(Suggest suggest) {
                refreshSuggest();
            }
        });
        // head
        initHead();
        // follow
        initFollowView();
        // comment
        initCommentView();
        // api
        recyclerHelper.dataRefresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if (suggest != null && suggest.isMine()) {
            getMenuInflater().inflate(R.menu.help_del, menu);
        } else {
            getMenuInflater().inflate(R.menu.help, menu);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RetrofitHelper.cancel(callGet);
        RetrofitHelper.cancel(callDel);
        RetrofitHelper.cancel(callFollow);
        RetrofitHelper.cancel(callCommentGet);
        RetrofitHelper.cancel(callCommentAdd);
        RxBus.unregister(ConsHelper.EVENT_SUGGEST_DETAIL_REFRESH, obDetailRefresh);
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
            case R.id.menuHelp: // 帮助
                HelpActivity.goActivity(mActivity, Help.INDEX_SUGGEST_DETAIL);
                return true;
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

    private void initHead() {
        // data
        if (suggest == null) return;
        boolean top = suggest.isTop();
        boolean official = suggest.isOfficial();
        boolean mine = suggest.isMine();
        String statusShow = suggest.getStatus() > 0 ? SuggestInfo.getStatusShow(suggest.getStatus()) : "";
        String typeShow = SuggestInfo.getTypeShow(suggest.getContentType());
        String title = suggest.getTitle();
        String create = TimeHelper.getTimeShowLine_HM_MD_YMD_ByGo(suggest.getCreateAt());
        String createShow = String.format(Locale.getDefault(), getString(R.string.create_at_colon_space_holder), create);
        final String contentImgUrl = suggest.getContentImg();
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
            View tagTop = getTagView(mActivity.getString(R.string.top));
            wvTag.addChild(tagTop);
        }
        if (official) {
            View tagOfficial = getTagView(mActivity.getString(R.string.official));
            wvTag.addChild(tagOfficial);
        }
        if (mine) {
            View tagMine = getTagView(mActivity.getString(R.string.me_de));
            wvTag.addChild(tagMine);
        }
        View tagStatus = getTagView(statusShow);
        wvTag.addChild(tagStatus);
        View tagType = getTagView(typeShow);
        wvTag.addChild(tagType);
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

    private View getTagView(String show) {
        if (StringUtils.isEmpty(show)) return null;
        int dp7 = ConvertUtils.dp2px(7);
        int dp5 = ConvertUtils.dp2px(5);
        int dp2 = ConvertUtils.dp2px(2);
        FrameLayout.LayoutParams mTextLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mTextLayoutParams.setMarginEnd(dp7);

        TextView textView = new TextView(mActivity);
        textView.setLayoutParams(mTextLayoutParams);
        textView.setBackgroundResource(R.drawable.shape_solid_primary_r2);
        textView.setPadding(dp5, dp2, dp5, dp2);
        textView.setGravity(Gravity.CENTER);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textView.setTextAppearance(R.style.FontWhiteSmall);
        } else {
            textView.setTextAppearance(mActivity, R.style.FontWhiteSmall);
        }
        textView.setText(show);
        return textView;
    }

    private void getCommentData(final boolean more) {
        if (suggest == null) return;
        page = more ? page + 1 : 0;
        // api
        callCommentGet = new RetrofitHelper().call(API.class).suggestCommentListGet(suggest.getId(), page);
        RetrofitHelper.enqueue(callCommentGet, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                recyclerHelper.viewEmptyShow(data.getShow());
                long total = data.getTotal();
                List<SuggestComment> suggestCommentList = data.getSuggestCommentList();
                recyclerHelper.dataOk(suggestCommentList, total, more);
            }

            @Override
            public void onFailure(String errMsg) {
                recyclerHelper.dataFail(more, errMsg);
            }
        });
    }

    private void initFollowView() {
        if (suggest == null) return;
        boolean follow = suggest.isFollow();
        String followCount = String.valueOf(suggest.getFollowCount());
        tvFollow.setText(followCount);
        if (follow) {
            ivFollow.setImageResource(R.drawable.ic_visibility_on_primary);
        } else {
            ivFollow.setImageResource(R.drawable.ic_visibility_off_grey);
        }
    }

    private void initCommentView() {
        if (suggest == null) return;
        boolean isComment = suggest.isComment();
        String commentCount = String.valueOf(suggest.getCommentCount());

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
        callFollow = new RetrofitHelper().call(API.class).suggestFollowToggle(suggest.getId());
        RetrofitHelper.enqueue(callFollow, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // ListItemRefresh
                RxEvent<Suggest> event = new RxEvent<>(ConsHelper.EVENT_SUGGEST_LIST_ITEM_REFRESH, suggest);
                RxBus.post(event);
            }

            @Override
            public void onFailure(String errMsg) {
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
        callCommentAdd = new RetrofitHelper().call(API.class).suggestCommentAdd(body);
        RetrofitHelper.enqueue(callCommentAdd, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                etComment.setText("");
                commentShow(false);
                getCommentData(false);
                refreshSuggest();
            }

            @Override
            public void onFailure(String errMsg) {
            }
        });
    }

    private void showDelDialog() {
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .content(R.string.confirm_del_suggest)
                .cancelable(true)
                .canceledOnTouchOutside(false)
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
        callDel = new RetrofitHelper().call(API.class).suggestDel(suggest.getId());
        RetrofitHelper.enqueue(callDel, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // ListItemDelete
                RxEvent<Suggest> event = new RxEvent<>(ConsHelper.EVENT_SUGGEST_LIST_ITEM_DELETE, suggest);
                RxBus.post(event);
                mActivity.finish();
            }

            @Override
            public void onFailure(String errMsg) {
            }
        });
    }

    public void refreshSuggest() {
        if (suggest == null) return;
        callGet = new RetrofitHelper().call(API.class).suggestGet(suggest.getId());
        RetrofitHelper.enqueue(callGet, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                suggest = data.getSuggest();
                initFollowView();
                initCommentView();
                // ListItemRefresh
                RxEvent<Suggest> event = new RxEvent<>(ConsHelper.EVENT_SUGGEST_LIST_ITEM_REFRESH, suggest);
                RxBus.post(event);
                // menu
                mActivity.invalidateOptionsMenu();
            }

            @Override
            public void onFailure(String errMsg) {
            }
        });
    }

}
