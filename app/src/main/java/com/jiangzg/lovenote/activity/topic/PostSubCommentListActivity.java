package com.jiangzg.lovenote.activity.topic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.system.InputUtils;
import com.jiangzg.base.view.DialogUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.base.view.ViewUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.adapter.PostCommentAdapter;
import com.jiangzg.lovenote.base.BaseActivity;
import com.jiangzg.lovenote.helper.ApiHelper;
import com.jiangzg.lovenote.helper.ConsHelper;
import com.jiangzg.lovenote.helper.CountHelper;
import com.jiangzg.lovenote.helper.DialogHelper;
import com.jiangzg.lovenote.helper.RecyclerHelper;
import com.jiangzg.lovenote.helper.RetrofitHelper;
import com.jiangzg.lovenote.helper.RxBus;
import com.jiangzg.lovenote.helper.SPHelper;
import com.jiangzg.lovenote.helper.TimeHelper;
import com.jiangzg.lovenote.helper.ViewHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.Couple;
import com.jiangzg.lovenote.model.entity.PostComment;
import com.jiangzg.lovenote.model.entity.PostCommentPoint;
import com.jiangzg.lovenote.model.entity.PostCommentReport;
import com.jiangzg.lovenote.view.FrescoAvatarView;
import com.jiangzg.lovenote.view.GSwipeRefreshLayout;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import retrofit2.Call;
import rx.Observable;

public class PostSubCommentListActivity extends BaseActivity<PostSubCommentListActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.llComment)
    LinearLayout llComment;
    @BindView(R.id.ivComment)
    ImageView ivComment;
    @BindView(R.id.llJab)
    LinearLayout llJab;
    @BindView(R.id.rlComment)
    RelativeLayout rlComment;
    @BindView(R.id.ivCommentClose)
    ImageView ivCommentClose;
    @BindView(R.id.tvCommentLimit)
    TextView tvCommentLimit;
    @BindView(R.id.ivAddCommit)
    ImageView ivAddCommit;
    @BindView(R.id.etComment)
    EditText etComment;

    private PostComment postComment;
    private RecyclerHelper recyclerHelper;
    private BottomSheetBehavior behaviorComment;
    private Observable<Long> obPostCommentDetail;
    private Call<Result> callGet;
    private Call<Result> callDel;
    private Call<Result> callCommentAdd;
    private Call<Result> callSubCommentListGet;
    private Call<Result> callReport;
    private Call<Result> callPoint;
    private int page, orderIndex, limitCommentContentLength;

    public static void goActivity(Activity from, PostComment postComment) {
        if (postComment == null) return;
        Intent intent = new Intent(from, PostSubCommentListActivity.class);
        intent.putExtra("from", ConsHelper.ACT_DETAIL_FROM_OBJ);
        intent.putExtra("postComment", postComment);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from, long pcid) {
        if (pcid == 0) return;
        Intent intent = new Intent(from, PostSubCommentListActivity.class);
        intent.putExtra("from", ConsHelper.ACT_DETAIL_FROM_ID);
        intent.putExtra("pcid", pcid);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Context from, long pcid) {
        if (pcid == 0) return;
        Intent intent = new Intent(from, PostSubCommentListActivity.class);
        intent.putExtra("from", ConsHelper.ACT_DETAIL_FROM_ID);
        intent.putExtra("pcid", pcid);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_post_sub_comment_list;
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
                                commentAdapter.reportPush(position, true);
                                break;
                        }
                    }
                });
        // init
        int from = intent.getIntExtra("from", ConsHelper.ACT_DETAIL_FROM_ID);
        if (from == ConsHelper.ACT_DETAIL_FROM_OBJ) {
            postComment = intent.getParcelableExtra("postComment");
            // view
            initHeadAndBottom();
            recyclerHelper.dataRefresh();
        } else if (from == ConsHelper.ACT_DETAIL_FROM_ID) {
            long pcid = intent.getLongExtra("pcid", 0);
            refreshPostComment(pcid, true);
        }
        // comment
        commentShow(false);
        // content 防止开始显示错误
        etComment.setText("");
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        page = 0;
        orderIndex = 0;
        // event
        obPostCommentDetail = RxBus.register(ConsHelper.EVENT_POST_COMMENT_DETAIL_REFRESH, pcid -> refreshPostComment(pcid, false));
        // refresh
        recyclerHelper.dataRefresh();
    }

    @Override
    protected void onFinish(Bundle state) {
        RetrofitHelper.cancel(callGet);
        RetrofitHelper.cancel(callDel);
        RetrofitHelper.cancel(callCommentAdd);
        RetrofitHelper.cancel(callSubCommentListGet);
        RetrofitHelper.cancel(callReport);
        RetrofitHelper.cancel(callPoint);
        RxBus.unregister(ConsHelper.EVENT_POST_COMMENT_DETAIL_REFRESH, obPostCommentDetail);
        RecyclerHelper.release(recyclerHelper);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if (postComment != null && postComment.isMine()) {
            getMenuInflater().inflate(R.menu.del, menu);
            getMenuInflater().inflate(R.menu.help, menu);
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
                showPostCommentDelDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnTextChanged({R.id.etComment})
    public void afterTextChanged(Editable s) {
        onCommentInput(s.toString());
    }

    @OnClick({R.id.llComment, R.id.llJab, R.id.ivCommentClose, R.id.ivAddCommit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.llJab: // 戳ta
                jab();
                break;
            case R.id.llComment: // 评论打开
                commentShow(true);
                break;
            case R.id.ivCommentClose: // 评论关闭
                commentShow(false);
                break;
            case R.id.ivAddCommit: // 评论提交
                commentPush();
                break;
        }
    }

    private void refreshPostComment(long pcid, final boolean subComment) {
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        callGet = new RetrofitHelper().call(API.class).topicPostCommentGet(pcid);
        RetrofitHelper.enqueue(callGet, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                srl.setRefreshing(false);
                postComment = data.getPostComment();
                // view
                initHeadAndBottom();
                mActivity.invalidateOptionsMenu();
                // subComment
                if (subComment) recyclerHelper.dataRefresh();
                // event
                RxBus.post(new RxBus.Event<>(ConsHelper.EVENT_POST_COMMENT_LIST_ITEM_REFRESH, postComment));
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
            }
        });
    }

    private void initHeadAndBottom() {
        if (postComment == null || recyclerHelper == null) return;
        // color
        int colorFontGrey = ContextCompat.getColor(mActivity, R.color.font_grey);
        int colorIconGrey = ContextCompat.getColor(mActivity, R.color.icon_grey);
        int colorPrimary = ContextCompat.getColor(mActivity, ViewUtils.getColorPrimary(mActivity));
        ColorStateList colorStateIconGrey = ColorStateList.valueOf(colorIconGrey);
        ColorStateList colorStatePrimary = ColorStateList.valueOf(colorPrimary);
        // data
        int kind = postComment.getKind();
        Couple couple = postComment.getCouple();
        String floor = String.format(Locale.getDefault(), mActivity.getString(R.string.holder_floor), postComment.getFloor());
        String create = TimeHelper.getTimeShowLine_HM_MD_YMD_ByGo(postComment.getCreateAt());
        String contentText = postComment.getContentText();
        String pointCount = postComment.getPointCount() > 0 ? CountHelper.getShowCount2Thousand(postComment.getPointCount()) : mActivity.getString(R.string.point);
        boolean official = postComment.isOfficial();
        boolean our = postComment.isOur();
        boolean subComment = postComment.isSubComment();
        final boolean point = postComment.isPoint();
        boolean report = postComment.isReport();
        // view
        View head = recyclerHelper.getViewHead();
        TextView tvOfficial = head.findViewById(R.id.tvOfficial);
        TextView tvFloor = head.findViewById(R.id.tvFloor);
        FrescoAvatarView ivAvatarLeft = head.findViewById(R.id.ivAvatarLeft);
        FrescoAvatarView ivAvatarRight = head.findViewById(R.id.ivAvatarRight);
        TextView tvTime = head.findViewById(R.id.tvTime);
        TextView tvContent = head.findViewById(R.id.tvContent);
        LinearLayout llOperate = head.findViewById(R.id.llOperate);
        LinearLayout llPoint = head.findViewById(R.id.llPoint);
        ImageView ivPoint = head.findViewById(R.id.ivPoint);
        TextView tvPointCount = head.findViewById(R.id.tvPointCount);
        LinearLayout llReport = head.findViewById(R.id.llReport);
        ImageView ivReport = head.findViewById(R.id.ivReport);
        LinearLayout llJab = head.findViewById(R.id.llJab);
        CardView cvAvatarJabLeft = head.findViewById(R.id.cvAvatarJabLeft);
        CardView cvAvatarJabRight = head.findViewById(R.id.cvAvatarJabRight);
        FrescoAvatarView ivAvatarJabLeft = head.findViewById(R.id.ivAvatarJabLeft);
        FrescoAvatarView ivAvatarJabRight = head.findViewById(R.id.ivAvatarJabRight);
        TextView tvCommentUser = head.findViewById(R.id.tvCommentUser);
        TextView tvCommentSort = head.findViewById(R.id.tvCommentSort);
        // set
        tvOfficial.setVisibility(official ? View.VISIBLE : View.GONE);
        tvFloor.setText(floor);
        tvFloor.setTextColor(our ? colorPrimary : colorFontGrey);
        tvTime.setText(create);
        if (kind == PostComment.KIND_JAB) {
            ivAvatarLeft.setVisibility(View.GONE);
            ivAvatarRight.setVisibility(View.GONE);
            tvContent.setVisibility(View.GONE);
            llOperate.setVisibility(View.GONE);
            llJab.setVisibility(View.VISIBLE);
            // 戳一戳
            if (couple == null) {
                cvAvatarJabLeft.setVisibility(View.GONE);
                cvAvatarJabRight.setVisibility(View.GONE);
            } else {
                cvAvatarJabLeft.setVisibility(View.VISIBLE);
                cvAvatarJabRight.setVisibility(View.VISIBLE);
                ivAvatarJabLeft.setData(couple.getCreatorAvatar());
                ivAvatarJabRight.setData(couple.getInviteeAvatar());
            }
        } else {
            // 正常文本
            tvContent.setVisibility(View.VISIBLE);
            llOperate.setVisibility(View.VISIBLE);
            llJab.setVisibility(View.GONE);
            if (couple == null) {
                ivAvatarLeft.setVisibility(View.GONE);
                ivAvatarRight.setVisibility(View.GONE);
            } else {
                ivAvatarLeft.setVisibility(View.VISIBLE);
                ivAvatarRight.setVisibility(View.VISIBLE);
                ivAvatarLeft.setData(couple.getCreatorAvatar());
                ivAvatarRight.setData(couple.getInviteeAvatar());
            }
            tvContent.setText(contentText);
            tvPointCount.setText(pointCount);
            ivPoint.setImageTintList(point ? colorStatePrimary : colorStateIconGrey);
            if (official) {
                llReport.setVisibility(View.GONE);
            } else {
                llReport.setVisibility(View.VISIBLE);
                ivReport.setImageTintList(report ? colorStatePrimary : colorStateIconGrey);
            }
            // listener
            llPoint.setOnClickListener(v -> point(true));
            llReport.setOnClickListener(v -> report(true));
        }
        // comment
        String commentUser = String.format(Locale.getDefault(), getString(R.string.all_space_brackets_holder_brackets), postComment.getSubCommentCount());
        tvCommentUser.setText(commentUser);
        initCommentOrderView();
        tvCommentSort.setOnClickListener(v -> showCommentSortDialog());
        // bottom
        ivComment.setImageTintList(subComment ? colorStatePrimary : colorStateIconGrey);
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

    private void initCommentOrderView() {
        if (recyclerHelper == null) return;
        View head = recyclerHelper.getViewHead();
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
        // api
        int orderType = ApiHelper.LIST_COMMENT_ORDER_TYPE[orderIndex];
        callSubCommentListGet = new RetrofitHelper().call(API.class).topicPostCommentSubListGet(postComment.getPostId(), postComment.getId(), orderType, page);
        RetrofitHelper.enqueue(callSubCommentListGet, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.viewEmptyShow(data.getShow());
                List<PostComment> postCommentList = data.getPostCommentList();
                recyclerHelper.dataOk(postCommentList, more);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.dataFail(more, message);
            }
        });
    }

    private void point(boolean api) {
        if (postComment == null) return;
        boolean newPoint = !postComment.isPoint();
        int newPointCount = newPoint ? postComment.getPointCount() + 1 : postComment.getPointCount() - 1;
        if (newPointCount < 0) {
            newPointCount = 0;
        }
        postComment.setPoint(newPoint);
        postComment.setPointCount(newPointCount);
        initHeadAndBottom();
        if (!api) return;
        PostCommentPoint postCommentPoint = new PostCommentPoint();
        postCommentPoint.setPostCommentId(postComment.getId());
        callPoint = new RetrofitHelper().call(API.class).topicPostCommentPointToggle(postCommentPoint);
        RetrofitHelper.enqueue(callPoint, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxBus.post(new RxBus.Event<>(ConsHelper.EVENT_POST_COMMENT_LIST_ITEM_REFRESH, postComment));
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                point(false);
            }
        });
    }

    private void report(boolean api) {
        if (postComment == null) return;
        // view
        postComment.setReport(true);
        postComment.setReportCount(postComment.getReportCount() + 1);
        initHeadAndBottom();
        if (!api) return;
        PostCommentReport postCommentReport = new PostCommentReport();
        postCommentReport.setPostCommentId(postComment.getId());
        callReport = new RetrofitHelper().call(API.class).topicPostCommentReportAdd(postCommentReport);
        RetrofitHelper.enqueue(callReport, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxBus.post(new RxBus.Event<>(ConsHelper.EVENT_POST_COMMENT_LIST_ITEM_REFRESH, postComment));
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                report(false);
            }
        });
    }

    private void jab() {
        if (postComment == null) return;
        PostComment body = ApiHelper.getPostCommentJabBody(postComment.getPostId(), postComment.getId());
        MaterialDialog loading = mActivity.getLoading(true);
        callCommentAdd = new RetrofitHelper().call(API.class).topicPostCommentAdd(body);
        RetrofitHelper.enqueue(callCommentAdd, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                postComment.setSubComment(true);
                postComment.setSubCommentCount(postComment.getSubCommentCount() + 1);
                initHeadAndBottom();
                // refresh
                recyclerHelper.dataRefresh();
                // event
                RxBus.post(new RxBus.Event<>(ConsHelper.EVENT_POST_DETAIL_REFRESH, postComment.getPostId()));
                RxBus.post(new RxBus.Event<>(ConsHelper.EVENT_POST_COMMENT_LIST_ITEM_REFRESH, postComment));
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

    private void commentShow(boolean show) {
        //if (!show) InputUtils.hideSoftInput(etComment);
        if (behaviorComment == null) {
            behaviorComment = BottomSheetBehavior.from(rlComment);
        }
        int state = show ? BottomSheetBehavior.STATE_COLLAPSED : BottomSheetBehavior.STATE_HIDDEN;
        behaviorComment.setState(state);
    }

    private void onCommentInput(String input) {
        if (limitCommentContentLength <= 0) {
            limitCommentContentLength = SPHelper.getLimit().getPostCommentContentLength();
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

    private void commentPush() {
        if (postComment == null) return;
        String content = etComment.getText().toString().trim();
        if (StringUtils.isEmpty(content)) {
            ToastUtils.show(etComment.getHint());
            return;
        }
        InputUtils.hideSoftInput(etComment);
        PostComment body = ApiHelper.getPostCommentTextBody(postComment.getPostId(), postComment.getId(), content);
        MaterialDialog loading = getLoading(true);
        callCommentAdd = new RetrofitHelper().call(API.class).topicPostCommentAdd(body);
        RetrofitHelper.enqueue(callCommentAdd, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                etComment.setText("");
                commentShow(false);
                postComment.setSubComment(true);
                postComment.setSubCommentCount(postComment.getSubCommentCount() + 1);
                initHeadAndBottom();
                // refresh
                recyclerHelper.dataRefresh();
                // event
                RxBus.post(new RxBus.Event<>(ConsHelper.EVENT_POST_DETAIL_REFRESH, postComment.getPostId()));
                RxBus.post(new RxBus.Event<>(ConsHelper.EVENT_POST_COMMENT_LIST_ITEM_REFRESH, postComment));
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

    private void showPostCommentDelDialog() {
        if (postComment == null || !postComment.isMine()) return;
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .content(R.string.confirm_delete_this_post)
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
        MaterialDialog loading = getLoading(getString(R.string.are_deleting), true);
        callDel = new RetrofitHelper().call(API.class).topicPostCommentDel(postComment.getId());
        RetrofitHelper.enqueue(callDel, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxBus.post(new RxBus.Event<>(ConsHelper.EVENT_POST_DETAIL_REFRESH, postComment.getPostId()));
                RxBus.post(new RxBus.Event<>(ConsHelper.EVENT_POST_COMMENT_LIST_ITEM_DELETE, postComment));
                mActivity.finish();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

}
