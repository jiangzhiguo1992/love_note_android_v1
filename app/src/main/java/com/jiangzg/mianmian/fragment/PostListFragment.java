package com.jiangzg.mianmian.fragment;


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
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.adapter.PostAdapter;
import com.jiangzg.mianmian.base.BaseFragment;
import com.jiangzg.mianmian.base.BasePagerFragment;
import com.jiangzg.mianmian.domain.Post;
import com.jiangzg.mianmian.domain.PostKindInfo;
import com.jiangzg.mianmian.domain.PostSubKindInfo;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.helper.API;
import com.jiangzg.mianmian.helper.ApiHelper;
import com.jiangzg.mianmian.helper.ConsHelper;
import com.jiangzg.mianmian.helper.DialogHelper;
import com.jiangzg.mianmian.helper.ListHelper;
import com.jiangzg.mianmian.helper.LocationHelper;
import com.jiangzg.mianmian.helper.RecyclerHelper;
import com.jiangzg.mianmian.helper.RetrofitHelper;
import com.jiangzg.mianmian.helper.RxBus;
import com.jiangzg.mianmian.helper.TimeHelper;
import com.jiangzg.mianmian.view.GSwipeRefreshLayout;

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
                if (recyclerHelper == null || subKindInfo == null || subKindInfo.getId() != subKindId)
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

    public int getSearchType() {
        if (official) return ApiHelper.LIST_TOPIC_OFFICIAL;
        if (well) return ApiHelper.LIST_TOPIC_WELL;
        return ApiHelper.LIST_TOPIC_NORMAL;
    }

    private void getData(final boolean more) {
        if (subKindInfo == null) return;
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
                            ToastUtils.show(getString(R.string.location_error));
                        }
                    });
                }

                @Override
                public void onPermissionDenied(int requestCode, String[] permissions) {
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
        call = new RetrofitHelper().call(API.class).topicPostListGet(create, kindInfo.getId(), subKindInfo.getId(), "", lon, lat, official, well, page);
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
