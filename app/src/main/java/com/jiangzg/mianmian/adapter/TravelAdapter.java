package com.jiangzg.mianmian.adapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.book.TravelDetailActivity;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Couple;
import com.jiangzg.mianmian.domain.RxEvent;
import com.jiangzg.mianmian.domain.Travel;
import com.jiangzg.mianmian.domain.TravelPlace;
import com.jiangzg.mianmian.helper.ConsHelper;
import com.jiangzg.mianmian.helper.RecyclerHelper;
import com.jiangzg.mianmian.helper.RxBus;
import com.jiangzg.mianmian.helper.SPHelper;
import com.jiangzg.mianmian.helper.TimeHelper;

import java.util.List;
import java.util.Locale;

/**
 * Created by JZG on 2018/3/13.
 * 礼物适配器
 */
public class TravelAdapter extends BaseQuickAdapter<Travel, BaseViewHolder> {

    private BaseActivity mActivity;
    private final Couple couple;

    public TravelAdapter(BaseActivity activity) {
        super(R.layout.list_item_travel);
        mActivity = activity;
        couple = SPHelper.getCouple();
    }

    @Override
    protected void convert(BaseViewHolder helper, Travel item) {
        String title = item.getTitle();
        String name = Couple.getName(couple, item.getUserId());
        String creator = String.format(Locale.getDefault(), mActivity.getString(R.string.creator_colon_space_holder), name);
        String happen = TimeHelper.getTimeShowCn_MD_YMD_ByGo(item.getHappenAt());
        String happenShow = String.format(Locale.getDefault(), mActivity.getString(R.string.time_colon_space_holder), happen);
        List<TravelPlace> placeList = item.getTravelPlaceList();
        // view
        RecyclerView rv = helper.getView(R.id.rv);
        helper.setText(R.id.tvTitle, title);
        helper.setText(R.id.tvHappenAt, happenShow);
        helper.setText(R.id.tvCreator, creator);
        if (placeList != null && placeList.size() > 0) {
            rv.setVisibility(View.VISIBLE);
            new RecyclerHelper(mActivity)
                    .initRecycler(rv)
                    .initLayoutManager(new LinearLayoutManager(mActivity))
                    .initAdapter() // TODO
                    .setAdapter()
                    .dataNew(placeList, 0);
        } else {
            rv.setVisibility(View.GONE);
        }
    }

    public void selectTravel(int position) {
        mActivity.finish(); // 必须先关闭
        Travel item = getItem(position);
        RxEvent<Travel> event = new RxEvent<>(ConsHelper.EVENT_TRAVEL_SELECT, item);
        RxBus.post(event);
    }

    public void goTravelDetail(int position) {
        Travel item = getItem(position);
        TravelDetailActivity.goActivity(mActivity, item);
    }

}
