package com.jiangzg.mianmian.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.common.MapShowActivity;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Food;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.domain.RxEvent;
import com.jiangzg.mianmian.helper.API;
import com.jiangzg.mianmian.helper.ConsHelper;
import com.jiangzg.mianmian.helper.DialogHelper;
import com.jiangzg.mianmian.helper.RecyclerHelper;
import com.jiangzg.mianmian.helper.RetrofitHelper;
import com.jiangzg.mianmian.helper.RxBus;
import com.jiangzg.mianmian.helper.TimeHelper;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;

/**
 * Created by JZG on 2018/3/13.
 * 礼物适配器
 */
public class FoodAdapter extends BaseQuickAdapter<Food, BaseViewHolder> {

    private BaseActivity mActivity;

    public FoodAdapter(BaseActivity activity) {
        super(R.layout.list_item_food);
        mActivity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, Food item) {
        String title = item.getTitle();
        String address = item.getAddress();
        String happen = TimeHelper.getTimeShowCn_MD_YMD_ByGo(item.getHappenAt());
        String happenShow = String.format(Locale.getDefault(), mActivity.getString(R.string.on_space_holder_space_send_to), happen);
        List<String> imageList = item.getContentImageList();
        // view
        RecyclerView rv = helper.getView(R.id.rv);
        helper.setText(R.id.tvTitle, title);
        helper.setVisible(R.id.tvLocation, !StringUtils.isEmpty(address));
        helper.setText(R.id.tvLocation, address);
        helper.setText(R.id.tvHappenAt, happenShow);
        if (imageList != null && imageList.size() > 0) {
            rv.setVisibility(View.VISIBLE);
            int spanCount = imageList.size() > 3 ? 3 : imageList.size();
            new RecyclerHelper(mActivity)
                    .initRecycler(rv)
                    .initLayoutManager(new GridLayoutManager(mActivity, spanCount))
                    .initAdapter(new ImgSquareShowAdapter(mActivity, spanCount))
                    .setAdapter()
                    .dataNew(imageList, 0);
        } else {
            rv.setVisibility(View.GONE);
        }
    }

    public void selectFood(int position) {
        mActivity.finish(); // 必须先关闭
        Food item = getItem(position);
        RxEvent<Food> event = new RxEvent<>(ConsHelper.EVENT_FOOD_SELECT, item);
        RxBus.post(event);
    }

    public void showDeleteDialog(final int position) {
        Food item = getItem(position);
        if (!item.isMine()) return;
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .content(R.string.confirm_delete_this_food)
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        deleteApi(position);
                    }
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void deleteApi(int position) {
        final Food item = getItem(position);
        Call<Result> call = new RetrofitHelper().call(API.class).foodDel(item.getId());
        MaterialDialog loading = mActivity.getLoading(true);
        RetrofitHelper.enqueue(call, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxEvent<Food> event = new RxEvent<>(ConsHelper.EVENT_FOOD_LIST_ITEM_DELETE, item);
                RxBus.post(event);
            }

            @Override
            public void onFailure(String errMsg) {
            }
        });
    }

    public void goMapShow(int position) {
        Food item = getItem(position);
        String address = item.getAddress();
        double longitude = item.getLongitude();
        double latitude = item.getLatitude();
        MapShowActivity.goActivity(mActivity, address, longitude, latitude);
    }

}
