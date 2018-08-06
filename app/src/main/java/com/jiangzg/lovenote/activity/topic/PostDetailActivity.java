package com.jiangzg.lovenote.activity.topic;

import android.app.Activity;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.system.InputUtils;
import com.jiangzg.base.view.DialogUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.adapter.ImgSquareShowAdapter;
import com.jiangzg.lovenote.adapter.PostCommentAdapter;
import com.jiangzg.lovenote.base.BaseActivity;
import com.jiangzg.lovenote.domain.Couple;
import com.jiangzg.lovenote.domain.Post;
import com.jiangzg.lovenote.domain.PostCollect;
import com.jiangzg.lovenote.domain.PostComment;
import com.jiangzg.lovenote.domain.PostPoint;
import com.jiangzg.lovenote.domain.PostReport;
import com.jiangzg.lovenote.domain.PostSubKindInfo;
import com.jiangzg.lovenote.domain.Result;
import com.jiangzg.lovenote.domain.RxEvent;
import com.jiangzg.lovenote.helper.API;
import com.jiangzg.lovenote.helper.ApiHelper;
import com.jiangzg.lovenote.helper.ConsHelper;
import com.jiangzg.lovenote.helper.CountHelper;
import com.jiangzg.lovenote.helper.DialogHelper;
import com.jiangzg.lovenote.helper.ListHelper;
import com.jiangzg.lovenote.helper.RecyclerHelper;
import com.jiangzg.lovenote.helper.RetrofitHelper;
import com.jiangzg.lovenote.helper.RxBus;
import com.jiangzg.lovenote.helper.SPHelper;
import com.jiangzg.lovenote.helper.ShareHelper;
import com.jiangzg.lovenote.helper.TimeHelper;
import com.jiangzg.lovenote.helper.ViewHelper;
import com.jiangzg.lovenote.view.FrescoAvatarView;
import com.jiangzg.lovenote.view.GSwipeRefreshLayout;
import com.jiangzg.lovenote.view.GWrapView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import retrofit2.Call;
import rx.Observable;
import rx.functions.Action1;

public class PostDetailActivity extends BaseActivity<PostDetailActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.rv)
    RecyclerView rv;

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
    @BindView(R.id.llJab)
    LinearLayout llJab;

    @BindView(R.id.rlComment)
    RelativeLayout rlComment;
    @BindView(R.id.ivCommentClose)
    ImageView ivCommentClose;
    @BindView(R.id.tvCommentLimit)
    TextView tvCommentLimit;
    @BindView(R.id.tvCommentCommit)
    TextView tvCommentCommit;
    @BindView(R.id.etComment)
    EditText etComment;

    private Post post;
    private RecyclerHelper recyclerHelper;
    private BottomSheetBehavior behaviorComment;
    private Observable<Long> obPostDetailRefresh;
    private Observable<PostComment> obPostCommentListRefresh;
    private Observable<PostComment> obPostCommentListItemDelete;
    private Observable<PostComment> obPostCommentListItemRefresh;
    private Call<Result> callGet;
    private Call<Result> callDel;
    private Call<Result> callCommentAdd;
    private Call<Result> callCommentListGet;
    private Call<Result> callReport;
    private Call<Result> callPoint;
    private Call<Result> callCollect;
    private int page, orderType, limitCommentContentLength;
    private long searchUserId;

    public static void goActivity(Activity from, Post post) {
        if (post == null) return;
        Intent intent = new Intent(from, PostDetailActivity.class);
        intent.putExtra("from", ConsHelper.ACT_DETAIL_FROM_OBJ);
        intent.putExtra("post", post);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from, long pid) {
        if (pid == 0) return;
        Intent intent = new Intent(from, PostDetailActivity.class);
        intent.putExtra("from", ConsHelper.ACT_DETAIL_FROM_ID);
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
                                commentAdapter.reportPush(position, true);
                                break;
                        }
                    }
                });
        // init
        int from = intent.getIntExtra("from", ConsHelper.ACT_DETAIL_FROM_ID);
        if (from == ConsHelper.ACT_DETAIL_FROM_OBJ) {
            post = intent.getParcelableExtra("post");
            // view
            initHead();
            recyclerHelper.dataRefresh();
        } else if (from == ConsHelper.ACT_DETAIL_FROM_ID) {
            long pid = intent.getLongExtra("pid", 0);
            refreshPost(pid, true);
        }
        // point
        initPointView();
        // collect
        initCollectView();
        // comment
        initCommentView();
        // comment
        commentShow(false);
        // content 防止开始显示错误
        etComment.setText("");
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        page = 0;
        orderType = ApiHelper.COMMENT_ORDER_POINT;
        searchUserId = 0;
        // event
        obPostDetailRefresh = RxBus.register(ConsHelper.EVENT_POST_DETAIL_REFRESH, new Action1<Long>() {
            @Override
            public void call(Long pid) {
                refreshPost(pid, false);
            }
        });
        obPostCommentListRefresh = RxBus.register(ConsHelper.EVENT_POST_COMMENT_LIST_REFRESH, new Action1<PostComment>() {
            @Override
            public void call(PostComment postComment) {
                if (recyclerHelper == null) return;
                recyclerHelper.dataRefresh();
            }
        });
        obPostCommentListItemDelete = RxBus.register(ConsHelper.EVENT_POST_COMMENT_LIST_ITEM_DELETE, new Action1<PostComment>() {
            @Override
            public void call(PostComment postComment) {
                if (recyclerHelper == null) return;
                ListHelper.removeObjInAdapter(recyclerHelper.getAdapter(), postComment);
            }
        });
        obPostCommentListItemRefresh = RxBus.register(ConsHelper.EVENT_POST_COMMENT_LIST_ITEM_REFRESH, new Action1<PostComment>() {
            @Override
            public void call(PostComment postComment) {
                if (recyclerHelper == null) return;
                ListHelper.refreshObjInAdapter(recyclerHelper.getAdapter(), postComment);
            }
        });
        // refresh
        recyclerHelper.dataRefresh();
    }

    @Override
    protected void onFinish(Bundle state) {
        RetrofitHelper.cancel(callGet);
        RetrofitHelper.cancel(callDel);
        RetrofitHelper.cancel(callCommentAdd);
        RetrofitHelper.cancel(callCommentListGet);
        RetrofitHelper.cancel(callReport);
        RetrofitHelper.cancel(callPoint);
        RetrofitHelper.cancel(callCollect);
        RxBus.unregister(ConsHelper.EVENT_POST_DETAIL_REFRESH, obPostDetailRefresh);
        RxBus.unregister(ConsHelper.EVENT_POST_COMMENT_LIST_REFRESH, obPostCommentListRefresh);
        RxBus.unregister(ConsHelper.EVENT_POST_COMMENT_LIST_ITEM_DELETE, obPostCommentListItemDelete);
        RxBus.unregister(ConsHelper.EVENT_POST_COMMENT_LIST_ITEM_REFRESH, obPostCommentListItemRefresh);
        RecyclerHelper.release(recyclerHelper);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if (post != null) {
            if (post.isOfficial()) {
                if (post.isMine()) { // 分享 + (删除 + 帮助)
                    getMenuInflater().inflate(R.menu.share_del, menu);
                } else { // 分享 + (帮助)
                    getMenuInflater().inflate(R.menu.share, menu);
                }
            } else {
                if (post.isMine()) { // 分享 + (举报 + 删除 + 帮助)
                    getMenuInflater().inflate(R.menu.share_report_del, menu);
                } else { // 分享 + (举报 + 帮助)
                    getMenuInflater().inflate(R.menu.share_report, menu);
                }
            }
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
            case R.id.menuShare: // 分享
                ShareHelper.shareTopicPost(post);
                return true;
            case R.id.menuReport: // 举报
                report();
                return true;
            case R.id.menuDel: // 删除
                showPostDelDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnTextChanged({R.id.etComment})
    public void afterTextChanged(Editable s) {
        onCommentInput(s.toString());
    }

    @OnClick({R.id.llPoint, R.id.llCollect, R.id.llComment, R.id.llJab,
            R.id.ivCommentClose, R.id.tvCommentCommit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.llPoint: // 点赞
                point(true);
                break;
            case R.id.llCollect: // 收藏
                collect(true);
                break;
            case R.id.llJab: // 戳ta
                jab();
                break;
            case R.id.llComment: // 评论打开
                commentShow(true);
                break;
            case R.id.ivCommentClose: // 评论关闭
                commentShow(false);
                break;
            case R.id.tvCommentCommit: // 评论提交
                commentPush();
                break;
        }
    }

    private void refreshPost(long pid, final boolean comment) {
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        callGet = new RetrofitHelper().call(API.class).topicPostGet(pid);
        RetrofitHelper.enqueue(callGet, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                srl.setRefreshing(false);
                post = data.getPost();
                // view
                initHead();
                initPointView();
                initCollectView();
                initCommentView();
                mActivity.invalidateOptionsMenu();
                // comment
                if (comment) recyclerHelper.dataRefresh();
                // event
                RxEvent<Post> event = new RxEvent<>(ConsHelper.EVENT_POST_LIST_ITEM_REFRESH, post);
                RxBus.post(event);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
            }
        });
    }

    private void initHead() {
        if (post == null || post.isScreen() || post.isDelete() || recyclerHelper == null) return;
        int colorPrimary = ContextCompat.getColor(mActivity, ViewHelper.getColorPrimary(mActivity));
        int colorFontGrey = ContextCompat.getColor(mActivity, R.color.font_grey);
        // data
        boolean isOur = post.isOur();
        Couple couple = post.getCouple();
        List<String> tagShowList = ListHelper.getPostTagShowList(post, true, true);
        String title = post.getTitle();
        String contentText = post.getContentText();
        List<String> imageList = post.getContentImageList();
        String address = post.getAddress();
        String create = TimeHelper.getTimeShowLine_HM_MD_YMD_ByGo(post.getCreateAt());
        String createShow = String.format(Locale.getDefault(), mActivity.getString(R.string.create_at_colon_space_holder), create);
        String update = TimeHelper.getTimeShowLine_HM_MD_YMD_ByGo(post.getUpdateAt());
        String updatedShow = String.format(Locale.getDefault(), mActivity.getString(R.string.update_at_colon_space_holder), update);
        // view
        View head = recyclerHelper.getViewHead();
        // couple
        LinearLayout llCouple = head.findViewById(R.id.llCouple);
        if (couple == null) {
            llCouple.setVisibility(View.GONE);
        } else {
            llCouple.setVisibility(View.VISIBLE);
            TextView tvNameLeft = head.findViewById(R.id.tvNameLeft);
            TextView tvNameRight = head.findViewById(R.id.tvNameRight);
            FrescoAvatarView ivAvatarLeft = head.findViewById(R.id.ivAvatarLeft);
            FrescoAvatarView ivAvatarRight = head.findViewById(R.id.ivAvatarRight);
            tvNameLeft.setText(couple.getCreatorName());
            tvNameRight.setText(couple.getInviteeName());
            if (isOur) {
                if (post.getUserId() == couple.getCreatorId()) {
                    tvNameLeft.setTextColor(colorPrimary);
                    tvNameRight.setTextColor(colorFontGrey);
                } else {
                    tvNameLeft.setTextColor(colorFontGrey);
                    tvNameRight.setTextColor(colorPrimary);
                }
            }
            ivAvatarLeft.setData(couple.getCreatorAvatar());
            ivAvatarRight.setData(couple.getInviteeAvatar());
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
        // title
        TextView tvTitle = head.findViewById(R.id.tvTitle);
        if (StringUtils.isEmpty(title)) {
            tvTitle.setVisibility(View.GONE);
        } else {
            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.setText(title);
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
                    .setAdapter()
                    .dataNew(imageList, 0);
        }
        // address
        TextView tvAddress = head.findViewById(R.id.tvAddress);
        if (StringUtils.isEmpty(address)) {
            tvAddress.setVisibility(View.GONE);
        } else {
            tvAddress.setVisibility(View.VISIBLE);
            tvAddress.setText(address);
        }
        // time
        TextView tvCreateAt = head.findViewById(R.id.tvCreateAt);
        TextView tvUpdateAt = head.findViewById(R.id.tvUpdateAt);
        tvCreateAt.setText(createShow);
        tvUpdateAt.setText(updatedShow);
        // comment
        TextView tvCommentUser = head.findViewById(R.id.tvCommentUser);
        TextView tvCommentSort = head.findViewById(R.id.tvCommentSort);
        initCommentUserView();
        initCommentOrderView();
        tvCommentUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCommentUserDialog();
            }
        });
        tvCommentSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCommentSortDialog();
            }
        });
    }

    private void showCommentUserDialog() {
        if (post == null) return;
        List<String> showList = new ArrayList<>();
        showList.add(getString(R.string.all));
        showList.add(getString(R.string.floor_master));
        if (!post.isMine()) {
            PostSubKindInfo subKindInfo = ListHelper.getPostSubKindInfoById(post.getKind(), post.getSubKind());
            if (subKindInfo == null || !subKindInfo.isAnonymous()) {
                showList.add(getString(R.string.me_de));
            }
        }
        int selectIndex = 0;
        if (searchUserId == post.getUserId()) {
            selectIndex = 1;
        } else if (!post.isMine() && searchUserId == SPHelper.getMe().getId()) {
            selectIndex = 2;
        }
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .title(R.string.select_search_type)
                .items(showList)
                .itemsCallbackSingleChoice(selectIndex, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        if (post == null || recyclerHelper == null) return true;
                        switch (which) {
                            case 1: // 楼主
                                searchUserId = post.getUserId();
                                break;
                            case 2: // 我的
                                searchUserId = SPHelper.getMe().getId();
                                break;
                            default: // 全部
                                searchUserId = 0;
                                break;
                        }
                        initCommentUserView();
                        recyclerHelper.dataRefresh();
                        DialogUtils.dismiss(dialog);
                        return true;
                    }
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
                .itemsCallbackSingleChoice(orderType, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        if (recyclerHelper == null) return true;
                        orderType = which;
                        initCommentOrderView();
                        recyclerHelper.dataRefresh();
                        DialogUtils.dismiss(dialog);
                        return true;
                    }
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void initCommentUserView() {
        if (recyclerHelper == null) return;
        View head = recyclerHelper.getViewHead();
        TextView tvCommentUser = head.findViewById(R.id.tvCommentUser);
        if (searchUserId == post.getUserId()) {
            tvCommentUser.setText(R.string.floor_master);
        } else if (searchUserId == SPHelper.getMe().getId()) {
            tvCommentUser.setText(R.string.me_de);
        } else {
            String commentAll;
            if (post == null) {
                commentAll = getString(R.string.all);
            } else {
                commentAll = String.format(Locale.getDefault(), getString(R.string.all_space_brackets_holder_brackets), post.getCommentCount());
            }
            tvCommentUser.setText(commentAll);
        }
    }

    private void initCommentOrderView() {
        if (recyclerHelper == null) return;
        View head = recyclerHelper.getViewHead();
        TextView tvCommentSort = head.findViewById(R.id.tvCommentSort);
        if (orderType >= 0 && orderType < ApiHelper.LIST_COMMENT_ORDER_SHOW.length) {
            tvCommentSort.setText(ApiHelper.LIST_COMMENT_ORDER_SHOW[orderType]);
        } else {
            tvCommentSort.setText("");
        }
    }

    private void getCommentData(final boolean more) {
        if (post == null) return;
        page = more ? page + 1 : 0;
        // api
        if (searchUserId > 0) {
            callCommentListGet = new RetrofitHelper().call(API.class).topicPostCommentUserListGet(post.getId(), searchUserId, orderType, page);
        } else {
            callCommentListGet = new RetrofitHelper().call(API.class).topicPostCommentListGet(post.getId(), orderType, page);
        }
        RetrofitHelper.enqueue(callCommentListGet, null, new RetrofitHelper.CallBack() {
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

    private void initPointView() {
        if (post == null) return;
        // data
        boolean point = post.isPoint();
        String pointCount = post.getPointCount() > 0 ? CountHelper.getShowCount2Thousand(post.getPointCount()) : mActivity.getString(R.string.point);
        // view
        tvPoint.setText(pointCount);
        if (point) {
            int colorPrimary = ContextCompat.getColor(mActivity, ViewHelper.getColorPrimary(mActivity));
            ivPoint.setImageTintList(ColorStateList.valueOf(colorPrimary));
        } else {
            int colorGrey = ContextCompat.getColor(mActivity, R.color.icon_grey);
            ivPoint.setImageTintList(ColorStateList.valueOf(colorGrey));
        }
    }

    private void initCollectView() {
        if (post == null) return;
        // data
        boolean collect = post.isCollect();
        String collectCount = post.getCollectCount() > 0 ? CountHelper.getShowCount2Thousand(post.getCollectCount()) : mActivity.getString(R.string.collect);
        // view
        tvCollect.setText(collectCount);
        if (collect) {
            int colorPrimary = ContextCompat.getColor(mActivity, ViewHelper.getColorPrimary(mActivity));
            ivCollect.setImageTintList(ColorStateList.valueOf(colorPrimary));
        } else {
            int colorGrey = ContextCompat.getColor(mActivity, R.color.icon_grey);
            ivCollect.setImageTintList(ColorStateList.valueOf(colorGrey));
        }
    }

    private void initCommentView() {
        if (post == null) return;
        // data
        boolean comment = post.isComment();
        String commentCount = post.getCommentCount() > 0 ? CountHelper.getShowCount2Thousand(post.getCommentCount()) : mActivity.getString(R.string.comment);
        // view
        tvComment.setText(commentCount);
        if (comment) {
            int colorPrimary = ContextCompat.getColor(mActivity, ViewHelper.getColorPrimary(mActivity));
            ivComment.setImageTintList(ColorStateList.valueOf(colorPrimary));
        } else {
            int colorGrey = ContextCompat.getColor(mActivity, R.color.icon_grey);
            ivComment.setImageTintList(ColorStateList.valueOf(colorGrey));
        }
    }

    private void point(boolean api) {
        if (post == null) return;
        boolean newPoint = !post.isPoint();
        int newPointCount = newPoint ? post.getPointCount() + 1 : post.getPointCount() - 1;
        if (newPointCount < 0) {
            newPointCount = 0;
        }
        post.setPoint(newPoint);
        post.setPointCount(newPointCount);
        initPointView();
        if (!api) return;
        PostPoint postPoint = new PostPoint();
        postPoint.setPostId(post.getId());
        callPoint = new RetrofitHelper().call(API.class).topicPostPointToggle(postPoint);
        RetrofitHelper.enqueue(callPoint, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxEvent<Post> event = new RxEvent<>(ConsHelper.EVENT_POST_LIST_ITEM_REFRESH, post);
                RxBus.post(event);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                point(false);
            }
        });
    }

    private void collect(boolean api) {
        if (post == null) return;
        boolean newCollect = !post.isCollect();
        int newCollectCount = newCollect ? post.getCollectCount() + 1 : post.getCollectCount() - 1;
        if (newCollectCount < 0) {
            newCollectCount = 0;
        }
        post.setCollect(newCollect);
        post.setCollectCount(newCollectCount);
        initCollectView();
        if (!api) return;
        PostCollect postCollect = new PostCollect();
        postCollect.setPostId(post.getId());
        callCollect = new RetrofitHelper().call(API.class).topicPostCollectToggle(postCollect);
        RetrofitHelper.enqueue(callCollect, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxEvent<Post> event = new RxEvent<>(ConsHelper.EVENT_POST_LIST_ITEM_REFRESH, post);
                RxBus.post(event);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                collect(false);
            }
        });
    }

    private void jab() {
        if (post == null) return;
        PostComment postComment = ApiHelper.getPostCommentJabBody(post.getId(), 0);
        MaterialDialog loading = mActivity.getLoading(true);
        callCommentAdd = new RetrofitHelper().call(API.class).topicPostCommentAdd(postComment);
        RetrofitHelper.enqueue(callCommentAdd, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                post.setComment(true);
                post.setCommentCount(post.getCommentCount() + 1);
                initCommentView();
                // refresh
                recyclerHelper.dataRefresh();
                // event
                RxEvent<Post> event = new RxEvent<>(ConsHelper.EVENT_POST_LIST_ITEM_REFRESH, post);
                RxBus.post(event);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

    private void commentShow(boolean show) {
        if (behaviorComment == null) {
            behaviorComment = BottomSheetBehavior.from(rlComment);
        }
        int state = show ? BottomSheetBehavior.STATE_COLLAPSED : BottomSheetBehavior.STATE_HIDDEN;
        behaviorComment.setState(state);
        if (!show) InputUtils.hideSoftInput(etComment);
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
        if (post == null) return;
        String content = etComment.getText().toString().trim();
        if (StringUtils.isEmpty(content)) {
            ToastUtils.show(etComment.getHint());
            return;
        }
        InputUtils.hideSoftInput(etComment);
        PostComment postComment = ApiHelper.getPostCommentTextBody(post.getId(), 0, content);
        MaterialDialog loading = getLoading(true);
        callCommentAdd = new RetrofitHelper().call(API.class).topicPostCommentAdd(postComment);
        RetrofitHelper.enqueue(callCommentAdd, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                etComment.setText("");
                commentShow(false);
                post.setComment(true);
                post.setCommentCount(post.getCommentCount() + 1);
                initCommentView();
                // refresh
                recyclerHelper.dataRefresh();
                // event
                RxEvent<Post> event = new RxEvent<>(ConsHelper.EVENT_POST_LIST_ITEM_REFRESH, post);
                RxBus.post(event);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

    private void showPostDelDialog() {
        if (post == null || !post.isMine()) {
            ToastUtils.show(mActivity.getString(R.string.can_delete_self_create_post));
            return;
        }
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .content(R.string.confirm_delete_this_post)
                .cancelable(true)
                .canceledOnTouchOutside(false)
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        delPost();
                    }
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void delPost() {
        if (post == null) return;
        MaterialDialog loading = getLoading(getString(R.string.are_deleting), true);
        callDel = new RetrofitHelper().call(API.class).topicPostDel(post.getId());
        RetrofitHelper.enqueue(callDel, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxEvent<Post> event = new RxEvent<>(ConsHelper.EVENT_POST_LIST_ITEM_DELETE, post);
                RxBus.post(event);
                mActivity.finish();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

    private void report() {
        if (post == null) return;
        MaterialDialog loading = getLoading(true);
        PostReport postReport = new PostReport();
        postReport.setPostId(post.getId());
        callReport = new RetrofitHelper().call(API.class).topicPostReportAdd(postReport);
        RetrofitHelper.enqueue(callReport, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxEvent<Post> event = new RxEvent<>(ConsHelper.EVENT_POST_LIST_ITEM_REFRESH, post);
                RxBus.post(event);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

}
