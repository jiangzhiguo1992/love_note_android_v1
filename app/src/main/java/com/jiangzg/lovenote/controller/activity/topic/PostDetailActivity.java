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
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.jiangzg.base.common.DateUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.DialogUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.base.view.ViewUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.controller.activity.settings.HelpActivity;
import com.jiangzg.lovenote.controller.adapter.common.ImgSquareShowAdapter;
import com.jiangzg.lovenote.controller.adapter.topic.PostCommentAdapter;
import com.jiangzg.lovenote.helper.common.ApiHelper;
import com.jiangzg.lovenote.helper.common.ListHelper;
import com.jiangzg.lovenote.helper.common.RxBus;
import com.jiangzg.lovenote.helper.common.SPHelper;
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
import com.jiangzg.lovenote.model.entity.Post;
import com.jiangzg.lovenote.model.entity.PostCollect;
import com.jiangzg.lovenote.model.entity.PostComment;
import com.jiangzg.lovenote.model.entity.PostKindInfo;
import com.jiangzg.lovenote.model.entity.PostPoint;
import com.jiangzg.lovenote.model.entity.PostReport;
import com.jiangzg.lovenote.model.entity.PostSubKindInfo;
import com.jiangzg.lovenote.model.entity.User;
import com.jiangzg.lovenote.view.FrescoAvatarView;
import com.jiangzg.lovenote.view.GSwipeRefreshLayout;
import com.jiangzg.lovenote.view.GWrapView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import rx.Observable;

public class PostDetailActivity extends BaseActivity<PostDetailActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.rv)
    RecyclerView rv;

    @BindView(R.id.llJab)
    LinearLayout llJab;
    @BindView(R.id.llPoint)
    LinearLayout llPoint;
    @BindView(R.id.ivPoint)
    ImageView ivPoint;
    @BindView(R.id.tvPoint)
    TextView tvPoint;
    @BindView(R.id.llCollect)
    LinearLayout llCollect;
    @BindView(R.id.ivCollect)
    ImageView ivCollect;
    @BindView(R.id.tvCollect)
    TextView tvCollect;
    @BindView(R.id.llComment)
    LinearLayout llComment;
    @BindView(R.id.ivComment)
    ImageView ivComment;
    @BindView(R.id.tvComment)
    TextView tvComment;

    private Post post;
    private RecyclerHelper recyclerHelper;
    private int page = 0;
    private int orderIndex;
    private long searchUserId;

    public static void goActivity(Activity from, Post post) {
        if (post == null) return;
        Intent intent = new Intent(from, PostDetailActivity.class);
        intent.putExtra("from", BaseActivity.ACT_DETAIL_FROM_OBJ);
        intent.putExtra("post", post);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from, long pid) {
        if (pid == 0) return;
        Intent intent = new Intent(from, PostDetailActivity.class);
        intent.putExtra("from", BaseActivity.ACT_DETAIL_FROM_ID);
        intent.putExtra("pid", pid);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Context from, long pid) {
        if (pid == 0) return;
        Intent intent = new Intent(from, PostDetailActivity.class);
        intent.putExtra("from", BaseActivity.ACT_DETAIL_FROM_ID);
        intent.putExtra("pid", pid);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_post_detail;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.post), true);
        // recycler
        recyclerHelper = new RecyclerHelper(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initRefresh(srl, true)
                .initAdapter(new PostCommentAdapter(mActivity, false))
                .viewHeader(mActivity, R.layout.list_head_post_comment)
                .viewEmpty(mActivity, R.layout.list_empty_grey, true, true)
                .viewLoadMore(new RecyclerHelper.MoreGreyView())
                .viewAnim()
                .setAdapter()
                .listenerRefresh(() -> refreshCommentData(false))
                .listenerMore(currentCount -> refreshCommentData(true))
                .listenerClick(new OnItemClickListener() {
                    @Override
                    public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                        PostCommentAdapter commentAdapter = (PostCommentAdapter) adapter;
                        commentAdapter.goSubCommentDetail(position);
                    }
                })
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
            post = intent.getParcelableExtra("post");
            // view
            initHead();
            if (post != null) {
                refreshPostData(post.getId(), true);
                // read
                postRead(post.getId());
            }
        } else if (from == BaseActivity.ACT_DETAIL_FROM_ID) {
            long pid = intent.getLongExtra("pid", 0);
            refreshPostData(pid, true);
            // read
            postRead(pid);
        } else {
            mActivity.finish();
        }
        // collect
        initCollectView();
        // point
        initPointView();
        // comment
        initCommentView();
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        orderIndex = 0;
        searchUserId = 0;
        // event
        Observable<Long> obPostDetailRefresh = RxBus.register(RxBus.EVENT_POST_DETAIL_REFRESH, pid -> refreshPostData(pid, false));
        pushBus(RxBus.EVENT_POST_DETAIL_REFRESH, obPostDetailRefresh);
        Observable<PostComment> obPostCommentListRefresh = RxBus.register(RxBus.EVENT_POST_COMMENT_LIST_REFRESH, postComment -> {
            if (recyclerHelper == null) return;
            recyclerHelper.dataRefresh();
        });
        pushBus(RxBus.EVENT_POST_COMMENT_LIST_REFRESH, obPostCommentListRefresh);
        Observable<PostComment> obPostCommentListItemDelete = RxBus.register(RxBus.EVENT_POST_COMMENT_LIST_ITEM_DELETE, postComment -> {
            if (recyclerHelper == null) return;
            ListHelper.removeObjInAdapter(recyclerHelper.getAdapter(), postComment);
        });
        pushBus(RxBus.EVENT_POST_COMMENT_LIST_ITEM_DELETE, obPostCommentListItemDelete);
        Observable<PostComment> obPostCommentListItemRefresh = RxBus.register(RxBus.EVENT_POST_COMMENT_LIST_ITEM_REFRESH, postComment -> {
            if (recyclerHelper == null) return;
            ListHelper.refreshObjInAdapter(recyclerHelper.getAdapter(), postComment);
        });
        pushBus(RxBus.EVENT_POST_COMMENT_LIST_ITEM_REFRESH, obPostCommentListItemRefresh);
    }

    @Override
    protected void onFinish(Bundle state) {
        RecyclerHelper.release(recyclerHelper);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if (post == null) {
            return super.onPrepareOptionsMenu(menu);
        }
        if (post.isOfficial()) {
            if (post.isMine()) { // 帮助 + 删除
                getMenuInflater().inflate(R.menu.help_del, menu);
            } else { // 帮助
                getMenuInflater().inflate(R.menu.help, menu);
            }
        } else {
            if (post.isMine()) { // 帮助 + 举报 + 删除
                getMenuInflater().inflate(R.menu.help_report_del, menu);
            } else { // 帮助 + 举报
                getMenuInflater().inflate(R.menu.help_report, menu);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuReport: // 举报
                showReportDialog();
                return true;
            case R.id.menuDel: // 删除
                showPostDelDialog();
                return true;
            case R.id.menuHelp:
                HelpActivity.goActivity(mActivity, HelpActivity.INDEX_TOPIC_POST);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.llJab, R.id.llPoint, R.id.llCollect, R.id.llComment})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.llJab: // 戳ta
                jab();
                break;
            case R.id.llPoint: // 点赞
                point(true);
                break;
            case R.id.llCollect: // 收藏
                collect(true);
                break;
            case R.id.llComment: // 评论打开
                if (post == null) return;
                PostCommentAddActivity.goActivity(mActivity, post.getId(), 0);
                break;
        }
    }

    private void refreshPostData(long pid, final boolean comment) {
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        Call<Result> api = new RetrofitHelper().call(API.class).topicPostGet(pid);
        RetrofitHelper.enqueue(api, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                srl.setRefreshing(false);
                post = data.getPost();
                // view
                initHead();
                initCollectView();
                initPointView();
                initCommentView();
                mActivity.invalidateOptionsMenu();
                // comment
                if (comment) recyclerHelper.dataRefresh();
                // event
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_POST_LIST_ITEM_REFRESH, post));
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
            }
        });
        pushApi(api);
    }

    private void initHead() {
        if (post == null || post.isScreen() || post.isDelete() || recyclerHelper == null) {
            mActivity.finish();
            return;
        }
        // data
        Couple couple = post.getCouple();
        String avatar = UserHelper.getAvatar(couple, post.getUserId());
        String name = UserHelper.getName(couple, post.getUserId(), true);
        String time = DateUtils.getStr(TimeHelper.getJavaTimeByGo(post.getCreateAt()), DateUtils.FORMAT_LINE_M_D_H_M);
        List<String> tagShowList = ShowHelper.getPostTagListShow(post, true, true);
        String title = post.getTitle();
        String contentText = post.getContentText();
        List<String> imageList = post.getContentImageList();
        // view
        View head = recyclerHelper.getViewHead();
        if (head == null) return;
        // couple
        RelativeLayout rlCouple = head.findViewById(R.id.rlCouple);
        if (couple == null) {
            rlCouple.setVisibility(View.GONE);
        } else {
            rlCouple.setVisibility(View.VISIBLE);
            FrescoAvatarView ivAvatar = head.findViewById(R.id.ivAvatar);
            ivAvatar.setData(avatar);
            TextView tvName = head.findViewById(R.id.tvName);
            tvName.setText(name);
            TextView tvTime = head.findViewById(R.id.tvTime);
            tvTime.setText(time);
        }
        // title
        TextView tvTitle = head.findViewById(R.id.tvTitle);
        if (StringUtils.isEmpty(title)) {
            tvTitle.setVisibility(View.GONE);
        } else {
            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.setText(title);
        }
        // tag
        GWrapView wvTag = head.findViewById(R.id.wvTag);
        if (tagShowList == null || tagShowList.size() <= 0) {
            wvTag.setVisibility(View.GONE);
        } else {
            wvTag.setVisibility(View.VISIBLE);
            wvTag.removeAllChild();
            for (String tag : tagShowList) {
                View tagView = ViewHelper.getWrapTextView(mActivity, tag);
                if (tagView == null) continue;
                wvTag.addChild(tagView);
            }
        }
        // content
        TextView tvContent = head.findViewById(R.id.tvContent);
        if (StringUtils.isEmpty(contentText)) {
            tvContent.setVisibility(View.GONE);
        } else {
            tvContent.setVisibility(View.VISIBLE);
            tvContent.setText(contentText);
        }
        // rvImage
        RecyclerView rvImage = head.findViewById(R.id.rvImage);
        if (imageList == null || imageList.size() <= 0) {
            rvImage.setVisibility(View.GONE);
        } else {
            rvImage.setVisibility(View.VISIBLE);
            rvImage.setEnabled(false); // 不加一进来会抢焦点
            rvImage.setFocusable(false);
            rvImage.setFocusableInTouchMode(false);
            rvImage.setNestedScrollingEnabled(false);
            new RecyclerHelper(rvImage)
                    .initLayoutManager(new LinearLayoutManager(mActivity) {
                        @Override
                        public boolean canScrollVertically() {
                            return false;
                        }
                    })
                    .initAdapter(new ImgSquareShowAdapter(mActivity, 1))
                    .viewAnim()
                    .setAdapter()
                    .dataNew(imageList, 0);
        }
        // comment
        initCommentUserView();
        initCommentOrderView();
        TextView tvCommentUser = head.findViewById(R.id.tvCommentUser);
        TextView tvCommentSort = head.findViewById(R.id.tvCommentSort);
        tvCommentUser.setOnClickListener(v -> showCommentUserDialog());
        tvCommentSort.setOnClickListener(v -> showCommentSortDialog());
    }

    private void initCommentUserView() {
        if (post == null || recyclerHelper == null) return;
        User me = SPHelper.getMe();
        View head = recyclerHelper.getViewHead();
        if (head == null) return;
        TextView tvCommentUser = head.findViewById(R.id.tvCommentUser);
        if (searchUserId == post.getUserId()) {
            tvCommentUser.setText(R.string.floor_master);
        } else if (me != null && searchUserId == me.getId()) {
            tvCommentUser.setText(R.string.me_de);
        } else {
            String commentAll = String.format(Locale.getDefault(), getString(R.string.all_space_brackets_holder_brackets), post.getCommentCount());
            tvCommentUser.setText(commentAll);
        }
    }

    private void initCommentOrderView() {
        if (post == null || recyclerHelper == null) return;
        View head = recyclerHelper.getViewHead();
        if (head == null) return;
        TextView tvCommentSort = head.findViewById(R.id.tvCommentSort);
        if (orderIndex >= 0 && orderIndex < ApiHelper.LIST_COMMENT_ORDER_SHOW.length) {
            tvCommentSort.setText(ApiHelper.LIST_COMMENT_ORDER_SHOW[orderIndex]);
        } else {
            tvCommentSort.setText("");
        }
    }

    private void refreshCommentData(final boolean more) {
        if (post == null) return;
        page = more ? page + 1 : 0;
        int orderType = ApiHelper.LIST_COMMENT_ORDER_TYPE[orderIndex];
        // api
        Call<Result> api;
        if (searchUserId > 0) {
            api = new RetrofitHelper().call(API.class).topicPostCommentUserListGet(post.getId(), searchUserId, orderType, page);
        } else {
            api = new RetrofitHelper().call(API.class).topicPostCommentListGet(post.getId(), orderType, page);
        }
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

    private void initCollectView() {
        if (post == null) return;
        String collectCount = post.getCollectCount() > 0 ? ShowHelper.getShowCount2Thousand(post.getCollectCount()) : mActivity.getString(R.string.collect);
        tvCollect.setText(collectCount);
        if (post.isCollect()) {
            int colorPrimary = ContextCompat.getColor(mActivity, ViewUtils.getColorPrimary(mActivity));
            ivCollect.setImageTintList(ColorStateList.valueOf(colorPrimary));
        } else {
            int colorGrey = ContextCompat.getColor(mActivity, R.color.icon_grey);
            ivCollect.setImageTintList(ColorStateList.valueOf(colorGrey));
        }
    }

    private void initPointView() {
        if (post == null) return;
        String pointCount = post.getPointCount() > 0 ? ShowHelper.getShowCount2Thousand(post.getPointCount()) : mActivity.getString(R.string.point);
        tvPoint.setText(pointCount);
        if (post.isPoint()) {
            int colorPrimary = ContextCompat.getColor(mActivity, ViewUtils.getColorPrimary(mActivity));
            ivPoint.setImageTintList(ColorStateList.valueOf(colorPrimary));
        } else {
            int colorGrey = ContextCompat.getColor(mActivity, R.color.icon_grey);
            ivPoint.setImageTintList(ColorStateList.valueOf(colorGrey));
        }
    }

    private void initCommentView() {
        if (post == null) return;
        initCommentUserView();
        String commentCount = post.getCommentCount() > 0 ? ShowHelper.getShowCount2Thousand(post.getCommentCount()) : mActivity.getString(R.string.comment);
        tvComment.setText(commentCount);
        if (post.isComment()) {
            int colorPrimary = ContextCompat.getColor(mActivity, ViewUtils.getColorPrimary(mActivity));
            ivComment.setImageTintList(ColorStateList.valueOf(colorPrimary));
        } else {
            int colorGrey = ContextCompat.getColor(mActivity, R.color.icon_grey);
            ivComment.setImageTintList(ColorStateList.valueOf(colorGrey));
        }
    }

    private void showCommentUserDialog() {
        if (post == null) return;
        List<String> showList = new ArrayList<>();
        showList.add(getString(R.string.all));
        showList.add(getString(R.string.floor_master));
        if (!post.isMine()) {
            PostKindInfo kindInfo = ListHelper.getPostKindInfo(post.getKind());
            PostSubKindInfo subKindInfo = ListHelper.getPostSubKindInfo(kindInfo, post.getSubKind());
            if (subKindInfo != null && !subKindInfo.isAnonymous()) {
                showList.add(getString(R.string.me_de));
            }
        }
        int selectIndex = 0;
        final User me = SPHelper.getMe();
        if (searchUserId == post.getUserId()) {
            selectIndex = 1;
        } else if (!post.isMine() && me != null && searchUserId == me.getId()) {
            selectIndex = 2;
        }
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .title(R.string.select_search_type)
                .items(showList)
                .itemsCallbackSingleChoice(selectIndex, (dialog1, view, which, text) -> {
                    if (post == null || recyclerHelper == null) return true;
                    switch (which) {
                        case 1: // 楼主
                            searchUserId = post.getUserId();
                            break;
                        case 2: // 我的
                            searchUserId = me == null ? 0 : me.getId();
                            break;
                        default: // 全部
                            searchUserId = 0;
                            break;
                    }
                    initCommentUserView();
                    recyclerHelper.dataRefresh();
                    DialogUtils.dismiss(dialog1);
                    return true;
                })
                .build();
        DialogHelper.showWithAnim(dialog);
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

    private void showReportDialog() {
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .content(R.string.confirm_report_this_post)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive((dialog1, which) -> report())
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void report() {
        if (post == null) return;
        PostReport postReport = new PostReport();
        postReport.setPostId(post.getId());
        // api
        Call<Result> api = new RetrofitHelper().call(API.class).topicPostReportAdd(postReport);
        RetrofitHelper.enqueue(api, getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                post.setReport(true);
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_POST_LIST_ITEM_REFRESH, post));
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
        pushApi(api);
    }

    private void jab() {
        if (post == null) return;
        PostComment postComment = ApiHelper.getPostCommentJabBody(post.getId(), 0);
        // api
        Call<Result> api = new RetrofitHelper().call(API.class).topicPostCommentAdd(postComment);
        RetrofitHelper.enqueue(api, getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                post.setComment(true);
                post.setCommentCount(post.getCommentCount() + 1);
                initCommentView();
                // refresh
                recyclerHelper.dataRefresh();
                // event
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_POST_LIST_ITEM_REFRESH, post));
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
        pushApi(api);
    }

    private void point(boolean isApi) {
        if (post == null) return;
        boolean newPoint = !post.isPoint();
        int newPointCount = newPoint ? post.getPointCount() + 1 : post.getPointCount() - 1;
        if (newPointCount < 0) {
            newPointCount = 0;
        }
        post.setPoint(newPoint);
        post.setPointCount(newPointCount);
        initPointView();
        if (!isApi) return;
        PostPoint postPoint = new PostPoint();
        postPoint.setPostId(post.getId());
        // api
        Call<Result> api = new RetrofitHelper().call(API.class).topicPostPointToggle(postPoint);
        RetrofitHelper.enqueue(api, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_POST_LIST_ITEM_REFRESH, post));
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                point(false);
            }
        });
        pushApi(api);
    }

    private void collect(boolean isApi) {
        if (post == null) return;
        boolean newCollect = !post.isCollect();
        int newCollectCount = newCollect ? post.getCollectCount() + 1 : post.getCollectCount() - 1;
        if (newCollectCount < 0) {
            newCollectCount = 0;
        }
        post.setCollect(newCollect);
        post.setCollectCount(newCollectCount);
        initCollectView();
        if (!isApi) return;
        PostCollect postCollect = new PostCollect();
        postCollect.setPostId(post.getId());
        // api
        Call<Result> api = new RetrofitHelper().call(API.class).topicPostCollectToggle(postCollect);
        RetrofitHelper.enqueue(api, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_POST_LIST_ITEM_REFRESH, post));
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                collect(false);
            }
        });
        pushApi(api);
    }

    private void showPostDelDialog() {
        if (post == null || !post.isMine()) {
            ToastUtils.show(mActivity.getString(R.string.can_delete_self_create_post));
            return;
        }
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .content(R.string.confirm_del_this_post)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive((dialog1, which) -> delPost())
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void delPost() {
        if (post == null) return;
        Call<Result> api = new RetrofitHelper().call(API.class).topicPostDel(post.getId());
        RetrofitHelper.enqueue(api, getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_POST_LIST_ITEM_DELETE, post));
                mActivity.finish();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
        pushApi(api);
    }

    private void postRead(long pid) {
        Call<Result> api = new RetrofitHelper().call(API.class).topicPostRead(pid);
        RetrofitHelper.enqueue(api, null, null);
        pushApi(api);
    }

}
