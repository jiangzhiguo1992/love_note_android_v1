package com.jiangzg.lovenote.controller.adapter.note;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.ConvertUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.view.ScreenUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.controller.activity.note.PictureListActivity;
import com.jiangzg.lovenote.helper.common.RxBus;
import com.jiangzg.lovenote.helper.common.TimeHelper;
import com.jiangzg.lovenote.helper.view.ViewHelper;
import com.jiangzg.lovenote.model.entity.Album;
import com.jiangzg.lovenote.view.FrescoView;

import java.util.Locale;

/**
 * Created by JZG on 2018/3/12.
 * 相册适配器
 */
public class AlbumAdapter extends BaseQuickAdapter<Album, BaseViewHolder> {

    private final BaseActivity mActivity;
    private final int imageWidth;
    private final int imageHeight;
    private final String formatTime;

    public AlbumAdapter(BaseActivity activity) {
        super(R.layout.list_item_album);
        mActivity = activity;
        imageWidth = ScreenUtils.getScreenRealWidth(activity);
        imageHeight = ConvertUtils.dp2px(170);
        formatTime = mActivity.getString(R.string.holder_space_line_space_holder);
    }

    @Override
    protected void convert(final BaseViewHolder helper, Album item) {
        // data
        String title = item.getTitle();
        String startTime = (item.getStartAt() == 0) ? "      " : TimeHelper.getTimeShowLocal_MD_YMD_ByGo(item.getStartAt());
        String endTime = (item.getEndAt() == 0) ? "      " : TimeHelper.getTimeShowLocal_MD_YMD_ByGo(item.getEndAt());
        String time = String.format(Locale.getDefault(), formatTime, startTime, endTime);
        String cover = item.getCover();
        // view
        helper.setText(R.id.tvTitle, title);
        helper.setText(R.id.tvTime, time);
        FrescoView ivAlbum = helper.getView(R.id.ivAlbum);
        ivAlbum.initHierarchy(null, true, false, false, false, false, false);
        ivAlbum.setWidthAndHeight(imageWidth, imageHeight);
        if (StringUtils.isEmpty(cover)) {
            // 没有封面时，随机给个颜色
            int randomColor = ViewHelper.getRandomThemePrimaryRes();
            ivAlbum.setDataRes(randomColor);
        } else {
            ivAlbum.setData(cover);
        }
    }

    public void selectAlbum(int position) {
        mActivity.finish(); // 必须先关闭
        Album item = getItem(position);
        RxBus.post(new RxBus.Event<>(RxBus.EVENT_ALBUM_SELECT, item));
    }

    public void goAlbumDetail(int position) {
        Album item = getItem(position);
        PictureListActivity.goActivity(mActivity, item);
    }

}
