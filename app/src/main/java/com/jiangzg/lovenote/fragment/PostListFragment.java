package com.jiangzg.lovenote.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.jiangzg.base.system.LocationInfo;
import com.jiangzg.base.system.PermUtils;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.adapter.PostAdapter;
import com.jiangzg.lovenote.base.BaseFragment;
import com.jiangzg.lovenote.base.BasePagerFragment;
import com.jiangzg.lovenote.domain.Post;
import com.jiangzg.lovenote.domain.PostKindInfo;
import com.jiangzg.lovenote.domain.PostSubKindInfo;
import com.jiangzg.lovenote.domain.Result;
import com.jiangzg.lovenote.helper.API;
import com.jiangzg.lovenote.helper.ApiHelper;
import com.jiangzg.lovenote.helper.ConsHelper;
import com.jiangzg.lovenote.helper.DialogHelper;
import com.jiangzg.lovenote.helper.ListHelper;
import com.jiangzg.lovenote.helper.LocationHelper;
import com.jiangzg.lovenote.helper.RecyclerHelper;
import com.jiangzg.lovenote.helper.RetrofitHelper;
import com.jiangzg.lovenote.helper.RxBus;
import com.jiangzg.lovenote.helper.TimeHelper;
import com.jiangzg.lovenote.view.GSwipeRefreshLayout;

import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import rx.Observable;
import rx.functions.Action1;

public class PostListFragment extends BasePagerFragment<PostListFragment> {

    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.rv)
    RecyclerView rv;

    private PostKindInfo kindInfo;
    private PostSubKindInfo subKindInfo;
    private RecyclerHelper recyclerHelper;
    private Call<Result> call;
    private long create;
    private double lon, lat;
    private boolean official, well;
    private int page;
    private Observable<Boolean> obGoTop;
    private Observable<Boolean> obSearchNormal;
    private Observable<Boolean> obSearchOfficial;
    private Observable<Boolean> obSearchWell;
    private Observable<Integer> obListRefresh;
    private Observable<Post> obListItemRefresh;
    private Observable<Post> obListItemDelete;

    public static PostListFragment newFragment(PostKindInfo kindInfo, PostSubKindInfo subKindInfo) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("kindInfo", kindInfo);
        bundle.putParcelable("subKindInfo", subKindInfo);
        return BaseFragment.newInstance(PostListFragment.class, bundle);
    }

    @Override
    protected int getView(Bundle data) {
        kindInfo = data.getParcelable("kindInfo");
        subKindInfo = data.getParcelable("subKindInfo");
        return R.layout.fragment_post_list;
    }

    @Override
    protected void initView(@Nullable Bundle state) {
        // recycler
        recyclerHelper = new RecyclerHelper(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initRefresh(srl, true)
                .initAdapter(new PostAdapter(mActivity, false, true))
                .viewEmpty(mActivity, R.layout.list_empty_white, true, true)
                .viewLoadMore(new RecyclerHelper.MoreGreyView())
                .setAdapter()
                .listenerRefresh(new RecyclerHelper.RefreshListener() {
                    @Override
                    public void onRefresh() {
                        getData(false);
                    }
                })
                .listenerMore(new RecyclerHelper.MoreListener() {
                    @Override
                    public void onMore(int currentCount) {
                        getData(true);
                    }
                })
                .listenerClick(new OnItemClickListener() {
                    @Override
                    public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                        PostAdapter postAdapter = (PostAdapter) adapter;
                        postAdapter.goPostDetail(position);
                    }
                });
    }

    @Override
    protected void loadData() {
        page = 0;
        official = false;
        well = false;
        // event
        obGoTop = RxBus.register(ConsHelper.EVENT_POST_GO_TOP, new Action1<Boolean>() {
            @Override
            public void call(Boolean isTrue) {
                if (!mFragment.getUserVisibleHint() || !isTrue || rv == null) return;
                rv.smoothScrollToPosition(0);
            }
        });
        obSearchNormal = RxBus.register(ConsHelper.EVENT_POST_SEARCH_NORMAL, new Action1<Boolean>() {
            @Override
            public void call(Boolean isTrue) {
                if (!mFragment.getUserVisibleHint() || !isTrue) return;
                official = false;
                well = false;
                recyclerHelper.dataRefresh();
            }
        });
        obSearchOfficial = RxBus.register(ConsHelper.EVENT_POST_SEARCH_OFFICIAL, new Action1<Boolean>() {
            @Override
            public void call(Boolean isTrue) {
                if (!mFragment.getUserVisibleHint() || !isTrue) return;
                official = true;
                well = false;
                recyclerHelper.dataRefresh();
            }
        });
        obSearchWell = RxBus.register(ConsHelper.EVENT_POST_SEARCH_WELL, new Action1<Boolean>() {
            @Override
            public void call(Boolean isTrue) {
                if (!mFragment.getUserVisibleHint() || !isTrue) return;
                official = false;
                well = true;
                recyclerHelper.dataRefresh();
            }
        });
        obListRefresh = RxBus.register(ConsHelper.EVENT_POST_LIST_REFRESH, new Action1<Integer>() {
            @Override
            public void call(Integer subKindId) {
                if (recyclerHelper == null || subKindInfo == null || subKindInfo.getKind() != subKindId)
                    return;
                recyclerHelper.dataRefresh();
            }
        });
        obListItemDelete = RxBus.register(ConsHelper.EVENT_POST_LIST_ITEM_DELETE, new Action1<Post>() {
            @Override
            public void call(Post post) {
                if (recyclerHelper == null) return;
                ListHelper.removeObjInAdapter(recyclerHelper.getAdapter(), post);
            }
        });
        obListItemRefresh = RxBus.register(ConsHelper.EVENT_POST_LIST_ITEM_REFRESH, new Action1<Post>() {
            @Override
            public void call(Post post) {
                if (recyclerHelper == null) return;
                ListHelper.refreshObjInAdapter(recyclerHelper.getAdapter(), post);
            }
        });
        // refresh
        recyclerHelper.dataRefresh();
    }

    @Override
    protected void onFinish(Bundle state) {
        RetrofitHelper.cancel(call);
        RxBus.unregister(ConsHelper.EVENT_POST_GO_TOP, obGoTop);
        RxBus.unregister(ConsHelper.EVENT_POST_SEARCH_NORMAL, obSearchNormal);
        RxBus.unregister(ConsHelper.EVENT_POST_SEARCH_OFFICIAL, obSearchOfficial);
        RxBus.unregister(ConsHelper.EVENT_POST_SEARCH_WELL, obSearchWell);
        RxBus.unregister(ConsHelper.EVENT_POST_LIST_REFRESH, obListRefresh);
        RxBus.unregister(ConsHelper.EVENT_POST_LIST_ITEM_DELETE, obListItemDelete);
        RxBus.unregister(ConsHelper.EVENT_POST_LIST_ITEM_REFRESH, obListItemRefresh);
        RecyclerHelper.release(recyclerHelper);
    }

    public PostSubKindInfo getSubKindInfo() {
        return subKindInfo;
    }

    public int getSearchType() {
        if (official) return ApiHelper.LIST_TOPIC_OFFICIAL;
        if (well) return ApiHelper.LIST_TOPIC_WELL;
        return ApiHelper.LIST_TOPIC_ALL;
    }

    private void getData(final boolean more) {
        if (subKindInfo == null) {
            if (srl.isRefreshing()) srl.setRefreshing(false);
            return;
        }
        if (subKindInfo.isLonLat()) {
            PermUtils.requestPermissions(mActivity, ConsHelper.REQUEST_LOCATION, PermUtils.location, new PermUtils.OnPermissionListener() {
                @Override
                public void onPermissionGranted(int requestCode, String[] permissions) {
                    LocationHelper.startLocation(mActivity, true, new LocationHelper.LocationCallBack() {
                        @Override
                        public void onSuccess(LocationInfo info) {
                            lon = info.getLongitude();
                            lat = info.getLatitude();
                            getDataWithLonLat(more);
                        }

                        @Override
                        public void onFailed(String errMsg) {
                            if (srl.isRefreshing()) srl.setRefreshing(false);
                            ToastUtils.show(getString(R.string.location_error));
                        }
                    });
                }

                @Override
                public void onPermissionDenied(int requestCode, String[] permissions) {
                    if (srl.isRefreshing()) srl.setRefreshing(false);
                    DialogHelper.showGoPermDialog(mActivity);
                }
            });
        } else {
            lon = 0;
            lat = 0;
            getDataWithLonLat(more);
        }
    }

    private void getDataWithLonLat(final boolean more) {
        if (subKindInfo == null) return;
        page = more ? page + 1 : 0;
        if (!more || create <= 0) {
            create = TimeHelper.getGoTimeByJava(DateUtils.getCurrentLong());
        }
        // api
        call = new RetrofitHelper().call(API.class).topicPostListGet(create, kindInfo.getKind(), subKindInfo.getKind(), "", lon, lat, official, well, page);
        RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.viewEmptyShow(data.getShow());
                List<Post> postList = data.getPostList();
                recyclerHelper.dataOk(postList, more);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.dataFail(more, message);
            }
        });
    }

}
