package com.jiangzg.lovenote.adapter;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.activity.common.MapShowActivity;
import com.jiangzg.lovenote.activity.note.MovieEditActivity;
import com.jiangzg.lovenote.base.BaseActivity;
import com.jiangzg.lovenote.domain.Couple;
import com.jiangzg.lovenote.domain.Movie;
import com.jiangzg.lovenote.domain.RxEvent;
import com.jiangzg.lovenote.helper.ConsHelper;
import com.jiangzg.lovenote.helper.RecyclerHelper;
import com.jiangzg.lovenote.helper.RxBus;
import com.jiangzg.lovenote.helper.SPHelper;
import com.jiangzg.lovenote.helper.TimeHelper;

import java.util.List;
import java.util.Locale;

/**
 * Created by JZG on 2018/3/13.
 * 电影适配器
 */
public class MovieAdapter extends BaseQuickAdapter<Movie, BaseViewHolder> {

    private BaseActivity mActivity;
    private final Couple couple;
    private final String formatCreator;
    private final String formatTime;

    public MovieAdapter(BaseActivity activity) {
        super(R.layout.list_item_movie);
        mActivity = activity;
        couple = SPHelper.getCouple();
        formatCreator = mActivity.getString(R.string.creator_colon_space_holder);
        formatTime = mActivity.getString(R.string.time_colon_space_holder);
    }

    @Override
    protected void convert(BaseViewHolder helper, Movie item) {
        String title = item.getTitle();
        String address = item.getAddress();
        String name = Couple.getName(couple, item.getUserId());
        String creator = String.format(Locale.getDefault(), formatCreator, name);
        String happen = TimeHelper.getTimeShowLocal_HM_MDHM_YMDHM_ByGo(item.getHappenAt());
        String happenShow = String.format(Locale.getDefault(), formatTime, happen);
        List<String> imageList = item.getContentImageList();
        String contentText = item.getContentText();
        // view
        RecyclerView rv = helper.getView(R.id.rv);
        helper.setText(R.id.tvTitle, title);
        helper.setVisible(R.id.tvAddress, !StringUtils.isEmpty(address));
        helper.setText(R.id.tvAddress, address);
        helper.setText(R.id.tvHappenAt, happenShow);
        helper.setText(R.id.tvCreator, creator);
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

    public void selectMovie(int position) {
        mActivity.finish(); // 必须先关闭
        Movie item = getItem(position);
        RxEvent<Movie> event = new RxEvent<>(ConsHelper.EVENT_MOVIE_SELECT, item);
        RxBus.post(event);
    }

    public void goEditActivity(int position) {
        Movie item = getItem(position);
        MovieEditActivity.goActivity(mActivity, item);
    }

    public void goMapShow(int position) {
        Movie item = getItem(position);
        String address = item.getAddress();
        double longitude = item.getLongitude();
        double latitude = item.getLatitude();
        MapShowActivity.goActivity(mActivity, address, longitude, latitude);
    }

}
