package com.jiangzg.lovenote.controller.adapter.note;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.controller.activity.note.TravelDetailActivity;
import com.jiangzg.lovenote.helper.common.RxBus;
import com.jiangzg.lovenote.helper.common.SPHelper;
import com.jiangzg.lovenote.helper.common.TimeHelper;
import com.jiangzg.lovenote.helper.common.UserHelper;
import com.jiangzg.lovenote.helper.view.RecyclerHelper;
import com.jiangzg.lovenote.model.entity.Couple;
import com.jiangzg.lovenote.model.entity.Travel;
import com.jiangzg.lovenote.model.entity.TravelPlace;
import com.jiangzg.lovenote.view.FrescoAvatarView;

import java.util.List;

/**
 * Created by JZG on 2018/3/13.
 * 游记适配器
 */
public class TravelAdapter extends BaseQuickAdapter<Travel, BaseViewHolder> {

    private final Couple couple;
    private BaseActivity mActivity;

    public TravelAdapter(BaseActivity activity) {
        super(R.layout.list_item_travel);
        mActivity = activity;
        couple = SPHelper.getCouple();
    }

    @Override
    protected void convert(BaseViewHolder helper, Travel item) {
        String avatar = UserHelper.getAvatar(couple, item.getUserId());
        String title = item.getTitle();
        String happen = TimeHelper.getTimeShowLocal_HM_MD_YMD_ByGo(item.getHappenAt());
        List<TravelPlace> placeList = item.getTravelPlaceList();
        // view
        FrescoAvatarView ivAvatar = helper.getView(R.id.ivAvatar);
        ivAvatar.setData(avatar, item.getUserId());
        helper.setText(R.id.tvTitle, title);
        helper.setText(R.id.tvHappenAt, happen);
        RecyclerView rv = helper.getView(R.id.rv);
        if (placeList != null && placeList.size() > 0) {
            rv.setVisibility(View.VISIBLE);
            if (placeList.size() > 5) {
                // 都返回，但是app最多显示5个
                placeList = placeList.subList(0, 5);
            }
            new RecyclerHelper(rv)
                    .initLayoutManager(new LinearLayoutManager(mActivity))
                    .initAdapter(new TravelPlaceAdapter(mActivity))
                    .setAdapter()
                    .listenerClick(new com.chad.library.adapter.base.listener.OnItemClickListener() {
                        @Override
                        public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                            TravelPlaceAdapter placeAdapter = (TravelPlaceAdapter) adapter;
                            placeAdapter.goMapShow(position);
                        }
                    })
                    .dataNew(placeList, 0);
        } else {
            rv.setVisibility(View.GONE);
        }
        // click
        helper.addOnClickListener(R.id.rv);
    }

    public void selectTravel(int position) {
        mActivity.finish(); // 必须先关闭
        Travel item = getItem(position);
        RxBus.post(new RxBus.Event<>(RxBus.EVENT_TRAVEL_SELECT, item));
    }

    public void goTravelDetail(int position) {
        Travel item = getItem(position);
        TravelDetailActivity.goActivity(mActivity, item);
    }

}
