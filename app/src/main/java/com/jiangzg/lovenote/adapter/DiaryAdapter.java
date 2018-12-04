package com.jiangzg.lovenote.adapter;

import android.support.v4.app.FragmentActivity;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.activity.note.DiaryDetailActivity;
import com.jiangzg.lovenote.helper.ConsHelper;
import com.jiangzg.lovenote.helper.RxBus;
import com.jiangzg.lovenote.helper.SPHelper;
import com.jiangzg.lovenote.helper.TimeHelper;
import com.jiangzg.lovenote.helper.UserHelper;
import com.jiangzg.lovenote.model.entity.Couple;
import com.jiangzg.lovenote.model.entity.Diary;
import com.jiangzg.lovenote.model.entity.RxEvent;
import com.jiangzg.lovenote.view.FrescoAvatarView;

import java.util.Locale;

/**
 * Created by JZG on 2018/3/13.
 * 日记适配器
 */
public class DiaryAdapter extends BaseQuickAdapter<Diary, BaseViewHolder> {

    private final Couple couple;
    private final String textFormat;
    private final String readFormat;
    private FragmentActivity mActivity;

    public DiaryAdapter(FragmentActivity activity) {
        super(R.layout.list_item_diary);
        mActivity = activity;
        couple = SPHelper.getCouple();
        textFormat = mActivity.getString(R.string.text_number_space_colon_holder);
        readFormat = mActivity.getString(R.string.read_space_colon_holder);
    }

    @Override
    protected void convert(BaseViewHolder helper, Diary item) {
        String avatar = UserHelper.getAvatar(couple, item.getUserId());
        String happen = TimeHelper.getTimeShowLocal_HM_MD_YMD_ByGo(item.getHappenAt());
        String content = item.getContentText();
        String textCount = String.format(Locale.getDefault(), textFormat, content == null ? 0 : content.length());
        String readCount = String.format(Locale.getDefault(), readFormat, item.getReadCount());
        // view
        FrescoAvatarView ivAvatar = helper.getView(R.id.ivAvatar);
        ivAvatar.setData(avatar);
        helper.setText(R.id.tvHappenAt, happen);
        helper.setText(R.id.tvReadCount, readCount);
        helper.setText(R.id.tvTextCount, textCount);
        helper.setText(R.id.tvContent, content);
    }

    public void goDiaryDetail(int position) {
        Diary item = getItem(position);
        item.setReadCount(item.getReadCount() + 1);
        notifyItemChanged(position);
        DiaryDetailActivity.goActivity(mActivity, item);
    }

    public void selectDiary(int position) {
        mActivity.finish(); // 必须先关闭
        Diary item = getItem(position);
        RxEvent<Diary> event = new RxEvent<>(ConsHelper.EVENT_DIARY_SELECT, item);
        RxBus.post(event);
    }

}
