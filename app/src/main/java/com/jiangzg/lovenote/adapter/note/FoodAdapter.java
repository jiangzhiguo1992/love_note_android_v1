package com.jiangzg.lovenote.adapter.note;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.activity.base.BaseActivity;
import com.jiangzg.lovenote.activity.common.MapShowActivity;
import com.jiangzg.lovenote.activity.note.FoodEditActivity;
import com.jiangzg.lovenote.adapter.common.ImgSquareShowAdapter;
import com.jiangzg.lovenote.helper.ConsHelper;
import com.jiangzg.lovenote.helper.RecyclerHelper;
import com.jiangzg.lovenote.helper.RxBus;
import com.jiangzg.lovenote.helper.SPHelper;
import com.jiangzg.lovenote.helper.TimeHelper;
import com.jiangzg.lovenote.helper.UserHelper;
import com.jiangzg.lovenote.model.entity.Couple;
import com.jiangzg.lovenote.model.entity.Food;
import com.jiangzg.lovenote.view.FrescoAvatarView;

import java.util.List;

/**
 * Created by JZG on 2018/3/13.
 * 美食适配器
 */
public class FoodAdapter extends BaseQuickAdapter<Food, BaseViewHolder> {

    private final Couple couple;
    private BaseActivity mActivity;

    public FoodAdapter(BaseActivity activity) {
        super(R.layout.list_item_food);
        mActivity = activity;
        couple = SPHelper.getCouple();
    }

    @Override
    protected void convert(BaseViewHolder helper, Food item) {
        String avatar = UserHelper.getAvatar(couple, item.getUserId());
        String title = item.getTitle();
        String address = item.getAddress();
        String happen = TimeHelper.getTimeShowLine_HM_MDHM_YMDHM_ByGo(item.getHappenAt());
        List<String> imageList = item.getContentImageList();
        String contentText = item.getContentText();
        // view
        FrescoAvatarView ivAvatar = helper.getView(R.id.ivAvatar);
        ivAvatar.setData(avatar);
        helper.setText(R.id.tvTitle, title);
        helper.setVisible(R.id.tvAddress, !StringUtils.isEmpty(address));
        helper.setText(R.id.tvAddress, address);
        helper.setText(R.id.tvHappenAt, happen);
        RecyclerView rv = helper.getView(R.id.rv);
        if (imageList != null && imageList.size() > 0) {
            rv.setVisibility(View.VISIBLE);
            new RecyclerHelper(rv)
                    .initLayoutManager(new GridLayoutManager(mActivity, 3))
                    .initAdapter(new ImgSquareShowAdapter(mActivity, 3))
                    .setAdapter()
                    .dataNew(imageList, 0);
        } else {
            rv.setVisibility(View.GONE);
        }
        helper.setVisible(R.id.tvContent, !StringUtils.isEmpty(contentText));
        helper.setText(R.id.tvContent, contentText);
        // click
        helper.addOnClickListener(R.id.tvAddress);
        helper.addOnClickListener(R.id.rv);
    }

    public void selectFood(int position) {
        mActivity.finish(); // 必须先关闭
        Food item = getItem(position);
        RxBus.post(new RxBus.Event<>(ConsHelper.EVENT_FOOD_SELECT, item));
    }

    public void goEditActivity(int position) {
        Food item = getItem(position);
        FoodEditActivity.goActivity(mActivity, item);
    }

    public void goMapShow(int position) {
        Food item = getItem(position);
        String address = item.getAddress();
        double longitude = item.getLongitude();
        double latitude = item.getLatitude();
        MapShowActivity.goActivity(mActivity, address, longitude, latitude);
    }

}
