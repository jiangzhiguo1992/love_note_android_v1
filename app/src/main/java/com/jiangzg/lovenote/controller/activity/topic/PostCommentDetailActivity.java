package com.jiangzg.lovenote.controller.activity.topic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.jiangzg.base.common.DateUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.DialogUtils;
import com.jiangzg.base.view.ViewUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.controller.adapter.topic.PostCommentAdapter;
import com.jiangzg.lovenote.helper.common.ApiHelper;
import com.jiangzg.lovenote.helper.common.RxBus;
import com.jiangzg.lovenote.helper.common.ShowHelper;
import com.jiangzg.lovenote.helper.common.TimeHelper;
import com.jiangzg.lovenote.helper.common.UserHelper;
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.helper.view.DialogHelper;
import com.jiangzg.lovenote.helper.view.RecyclerHelper;
import com.jiangzg.lovenote.helper.view.ViewHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.Couple;
import com.jiangzg.lovenote.model.entity.PostComment;
import com.jiangzg.lovenote.model.entity.PostCommentPoint;
import com.jiangzg.lovenote.model.entity.PostCommentReport;
import com.jiangzg.lovenote.view.FrescoAvatarView;
import com.jiangzg.lovenote.view.GSwipeRefreshLayout;

import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import rx.Observable;

public class PostCommentDetailActivity extends BaseActivity<PostCommentDetailActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.llJab)
    LinearLayout llJab;
    @BindView(R.id.llComment)
    LinearLayout llComment;
    @BindView(R.id.ivComment)
    ImageView ivComment;

    private PostComment postComment;
    private RecyclerHelper recyclerHelper;
    private int page = 0;
    private int orderIndex;

    public static void goActivity(Activity from, PostComment postComment) {
        if (postComment == null) return;
        Intent intent = new Intent(from, PostCommentDetailActivity.class);
        intent.putExtra("from", BaseActivity.ACT_DETAIL_FROM_OBJ);
        intent.putExtra("postComment", postComment);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from, long pcid) {
        if (pcid == 0) return;
        Intent intent = new Intent(from, PostCommentDetailActivity.class);
        intent.putExtra("from", BaseActivity.ACT_DETAIL_FROM_ID);
        intent.putExtra("pcid", pcid);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Context from, long pcid) {
        if (pcid == 0) return;
        Intent intent = new Intent(from, PostCommentDetailActivity.class);
        intent.putExtra("from", BaseActivity.ACT_DETAIL_FROM_ID);
        intent.putExtra("pcid", pcid);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_post_comment_detail;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.comment), true);
        // recycler
        recyclerHelper = new RecyclerHelper(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initRefresh(srl, true)
                .initAdapter(new PostCommentAdapter(mActivity, true))
                .viewHeader(mActivity, R.layout.list_head_post_sub_comment)
                .viewEmpty(mActivity, R.layout.list_empty_grey, true, true)
                .viewLoadMore(new RecyclerHelper.MoreGreyView())
                .viewAnim()
                .setAdapter()
                .listenerRefresh(() -> getSubCommentData(false))
                .listenerMore(currentCount -> getSubCommentData(true))
                .listenerClick(new OnItemLongClickListener() {
                    @Override
                    public void onSimpleItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                        PostCommentAdapter commentAdapter = (PostCommentAdapter) adapter;
                        commentAdapter.showDeleteDialog(position);
                    }
                })
                .listenerClick(new OnItemChildClickListener() {
                    @Override
                    public void onSimpleItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                        PostCommentAdapter commentAdapter = (PostCommentAdapter) adapter;
                        switch (view.getId()) {
                            case R.id.llPoint: // 点赞
                                commentAdapter.pointPush(position, true);
                                break;
                            case R.id.llReport: // 举报
                                commentAdapter.showReportDialog(position);
                                break;
                        }
                    }
                });
        // init
        int from = intent.getIntExtra("from", BaseActivity.ACT_DETAIL_FROM_ID);
        if (from == BaseActivity.ACT_DETAIL_FROM_OBJ) {
            postComment = intent.getParcelableExtra("postComment");
            // view
            initHead();
            recyclerHelper.dataRefresh();
        } else if (from == BaseActivity.ACT_DETAIL_FROM_ID) {
            long pcid = intent.getLongExtra("pcid", 0);
            refreshPostComment(pcid, true);
        } else {
            mActivity.finish();
        }
        // comment
        initCommentView();
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        orderIndex = 0;
        // event
        Observable<PostComment> obPostCommentListRefresh = RxBus.register(RxBus.EVENT_POST_COMMENT_LIST_REFRESH, postComment -> {
            if (recyclerHelper == null) return;
            recyclerHelper.dataRefresh();
        });
        pushBus(RxBus.EVENT_POST_COMMENT_LIST_REFRESH, obPostCommentListRefresh);
        Observable<Long> obPostCommentDetail = RxBus.register(RxBus.EVENT_POST_COMMENT_DETAIL_REFRESH, pcid -> refreshPostComment(pcid, false));
        pushBus(RxBus.EVENT_POST_COMMENT_DETAIL_REFRESH, obPostCommentDetail);
    }

    @Override
    protected void onFinish(Bundle state) {
        RecyclerHelper.release(recyclerHelper);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.original, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if (postComment != null && postComment.isMine()) {
            getMenuInflater().inflate(R.menu.del_original, menu);
        } else {
            getMenuInflater().inflate(R.menu.original, menu);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuOriginal: // 原帖
                goOriginalPost();
                return true;
            case R.id.menuDel: // 删除
                showPostCommentDelDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.llJab, R.id.llComment})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.llJab: // 戳ta
                jab();
                break;
            case R.id.llComment: // 评论提交
                if (postComment == null || postComment.getPostId() == 0) return;
                PostCommentAddActivity.goActivity(mActivity, postComment.getPostId(), postComment.getId());
                break;
        }
    }

    private void refreshPostComment(long pcid, final boolean subComment) {
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        Call<Result> api = new RetrofitHelper().call(API.class).topicPostCommentGet(pcid);
        RetrofitHelper.enqueue(api, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                srl.setRefreshing(false);
                postComment = data.getPostComment();
                // view
                initHead();
                initCommentView();
                mActivity.invalidateOptionsMenu();
                // subComment
                if (subComment) recyclerHelper.dataRefresh();
                // event
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_POST_COMMENT_LIST_ITEM_REFRESH, postComment));
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
            }
        });
        pushApi(api);
    }

    private void initHead() {
        if (postComment == null || recyclerHelper == null) return;
        // color
        int colorFontBlack = ContextCompat.getColor(mActivity, R.color.font_black);
        int colorHint = ContextCompat.getColor(mActivity, R.color.font_hint);
        int colorPrimary = ContextCompat.getColor(mActivity, ViewUtils.getColorPrimary(mActivity));
        ColorStateList colorStateHint = ColorStateList.valueOf(colorHint);
        ColorStateList colorStatePrimary = ColorStateList.valueOf(colorPrimary);
        // data
        Couple couple = postComment.getCouple();
        String avatar = UserHelper.getAvatar(couple, postComment.getUserId());
        String name = postComment.isOfficial() ? mActivity.getString(R.string.administrators) : UserHelper.getName(couple, postComment.getUserId(), true);
        String floor = String.format(Locale.getDefault(), mActivity.getString(R.string.holder_floor), postComment.getFloor());
        String time = DateUtils.getStr(TimeHelper.getJavaTimeByGo(postComment.getCreateAt()), DateUtils.FORMAT_LINE_M_D_H_M);
        String contentText = postComment.getContentText();
        String jabAvatar;
        String pointCount = ShowHelper.getShowCount2Thousand(postComment.getPointCount());
        boolean report = postComment.isReport();
        boolean point = postComment.isPoint();
        // view
        View head = recyclerHelper.getViewHead();
        if (head == null) return;
        RelativeLayout rlTop = head.findViewById(R.id.rlTop);
        FrescoAvatarView ivAvatar = head.findViewById(R.id.ivAvatar);
        TextView tvName = head.findViewById(R.id.tvName);
        TextView tvFloor = head.findViewById(R.id.tvFloor);
        TextView tvTime = head.findViewById(R.id.tvTime);
        TextView tvContent = head.findViewById(R.id.tvContent);
        FrescoAvatarView ivJab = head.findViewById(R.id.ivJab);
        LinearLayout llReport = head.findViewById(R.id.llReport);
        ImageView ivReport = head.findViewById(R.id.ivReport);
        LinearLayout llPoint = head.findViewById(R.id.llPoint);
        ImageView ivPoint = head.findViewById(R.id.ivPoint);
        TextView tvPointCount = head.findViewById(R.id.tvPointCount);
        TextView tvCommentUser = head.findViewById(R.id.tvCommentUser);
        TextView tvCommentSort = head.findViewById(R.id.tvCommentSort);
        // top
        if (couple == null) {
            rlTop.setVisibility(View.GONE);
            jabAvatar = "";
        } else {
            rlTop.setVisibility(View.VISIBLE);
            long jabId = (couple.getCreatorId() == postComment.getUserId()) ? couple.getInviteeId() : couple.getCreatorId();
            jabAvatar = UserHelper.getAvatar(couple, jabId);
            ivAvatar.setData(avatar);
            tvName.setText(name);
            tvFloor.setText(floor);
            tvTime.setText(time);
        }
        // center
        if (postComment.getKind() != PostComment.KIND_JAB) {
            tvContent.setText(contentText);
            tvContent.setTextColor(colorFontBlack);
            ivJab.setVisibility(View.GONE);
        } else {
            tvContent.setText(mActivity.getString(R.string.jab_a_little));
            tvContent.setTextColor(colorPrimary);
            ivJab.setVisibility(View.VISIBLE);
            ivJab.setData(jabAvatar);
        }
        // bottom
        ivReport.setImageTintList(report ? colorStatePrimary : colorStateHint);
        ivPoint.setImageTintList(point ? colorStatePrimary : colorStateHint);
        tvPointCount.setText(pointCount);
        // comment
        String commentUser = String.format(Locale.getDefault(), getString(R.string.all_space_brackets_holder_brackets), postComment.getSubCommentCount());
        tvCommentUser.setText(commentUser);
        initCommentOrderView();
        // listener
        llReport.setOnClickListener(v -> showReportDialog());
        llPoint.setOnClickListener(v -> point(true));
        tvCommentSort.setOnClickListener(v -> showCommentSortDialog());
    }

    private void initCommentOrderView() {
        if (recyclerHelper == null) return;
        View head = recyclerHelper.getViewHead();
        if (head == null) return;
        TextView tvCommentSort = head.findViewById(R.id.tvCommentSort);
        if (orderIndex >= 0 && orderIndex < ApiHelper.LIST_COMMENT_ORDER_SHOW.length) {
            tvCommentSort.setText(ApiHelper.LIST_COMMENT_ORDER_SHOW[orderIndex]);
        } else {
            tvCommentSort.setText("");
        }
    }

    private void getSubCommentData(final boolean more) {
        if (postComment == null) return;
        page = more ? page + 1 : 0;
        int orderType = ApiHelper.LIST_COMMENT_ORDER_TYPE[orderIndex];
        // api
        Call<Result> api = new RetrofitHelper().call(API.class).topicPostCommentSubListGet(postComment.getPostId(), postComment.getId(), orderType, page);
        RetrofitHelper.enqueue(api, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.dataOk(data.getShow(), data.getPostCommentList(), more);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.dataFail(more, message);
            }
        });
        pushApi(api);
    }

    private void initCommentView() {
        if (postComment == null) return;
        int colorIconGrey = ContextCompat.getColor(mActivity, R.color.icon_grey);
        int colorPrimary = ContextCompat.getColor(mActivity, ViewUtils.getColorPrimary(mActivity));
        ColorStateList colorStateIconGrey = ColorStateList.valueOf(colorIconGrey);
        ColorStateList colorStatePrimary = ColorStateList.valueOf(colorPrimary);
        ivComment.setImageTintList(postComment.isSubComment() ? colorStatePrimary : colorStateIconGrey);
    }

    private void showReportDialog() {
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .content(R.string.confirm_report_this_comment)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive((dialog1, which) -> report(true))
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void report(boolean isApi) {
        if (postComment == null) return;
        // view
        postComment.setReport(true);
        postComment.setReportCount(postComment.getReportCount() + 1);
        initHead();
        if (!isApi) return;
        PostCommentReport postCommentReport = new PostCommentReport();
        postCommentReport.setPostCommentId(postComment.getId());
        // api
        Call<Result> api = new RetrofitHelper().call(API.class).topicPostCommentReportAdd(postCommentReport);
        RetrofitHelper.enqueue(api, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_POST_COMMENT_LIST_ITEM_REFRESH, postComment));
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                report(false);
            }
        });
        pushApi(api);
    }

    private void point(boolean isApi) {
        if (postComment == null) return;
        boolean newPoint = !postComment.isPoint();
        int newPointCount = newPoint ? postComment.getPointCount() + 1 : postComment.getPointCount() - 1;
        if (newPointCount < 0) {
            newPointCount = 0;
        }
        postComment.setPoint(newPoint);
        postComment.setPointCount(newPointCount);
        initHead();
        if (!isApi) return;
        PostCommentPoint postCommentPoint = new PostCommentPoint();
        postCommentPoint.setPostCommentId(postComment.getId());
        // api
        Call<Result> api = new RetrofitHelper().call(API.class).topicPostCommentPointToggle(postCommentPoint);
        RetrofitHelper.enqueue(api, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_POST_COMMENT_LIST_ITEM_REFRESH, postComment));
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                point(false);
            }
        });
        pushApi(api);
    }

    private void showCommentSortDialog() {
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .title(R.string.select_order_type)
                .items(ApiHelper.LIST_COMMENT_ORDER_SHOW)
                .itemsCallbackSingleChoice(orderIndex, (dialog1, view, which, text) -> {
                    if (recyclerHelper == null) return true;
                    if (which < 0 || which >= ApiHelper.LIST_COMMENT_ORDER_TYPE.length) {
                        return true;
                    }
                    orderIndex = which;
                    initCommentOrderView();
                    recyclerHelper.dataRefresh();
                    DialogUtils.dismiss(dialog1);
                    return true;
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void jab() {
        if (postComment == null) return;
        PostComment body = ApiHelper.getPostCommentJabBody(postComment.getPostId(), postComment.getId());
        // api
        Call<Result> api = new RetrofitHelper().call(API.class).topicPostCommentAdd(body);
        RetrofitHelper.enqueue(api, getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                postComment.setSubComment(true);
                postComment.setSubCommentCount(postComment.getSubCommentCount() + 1);
                initHead();
                initCommentView();
                // refresh
                recyclerHelper.dataRefresh();
                // event
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_POST_DETAIL_REFRESH, postComment.getPostId()));
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_POST_COMMENT_LIST_ITEM_REFRESH, postComment));
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
        pushApi(api);
    }

    private void goOriginalPost() {
        if (postComment == null || postComment.getPostId() == 0) return;
        PostDetailActivity.goActivity(mActivity, postComment.getPostId());
    }

    private void showPostCommentDelDialog() {
        if (postComment == null || !postComment.isMine()) return;
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .content(R.string.confirm_del_this_comment)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive((dialog1, which) -> delPostComment())
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void delPostComment() {
        if (postComment == null) return;
        Call<Result> api = new RetrofitHelper().call(API.class).topicPostCommentDel(postComment.getId());
        RetrofitHelper.enqueue(api, getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_POST_DETAIL_REFRESH, postComment.getPostId()));
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_POST_COMMENT_LIST_ITEM_DELETE, postComment));
                mActivity.finish();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
        pushApi(api);
    }

}
