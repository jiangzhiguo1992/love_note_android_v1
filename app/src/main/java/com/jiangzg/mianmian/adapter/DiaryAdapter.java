package com.jiangzg.mianmian.adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.book.DiaryDetailActivity;
import com.jiangzg.mianmian.domain.Couple;
import com.jiangzg.mianmian.domain.Diary;
import com.jiangzg.mianmian.domain.RxEvent;
import com.jiangzg.mianmian.helper.ConsHelper;
import com.jiangzg.mianmian.helper.DialogHelper;
import com.jiangzg.mianmian.helper.RxBus;
import com.jiangzg.mianmian.helper.SPHelper;
import com.jiangzg.mianmian.helper.TimeHelper;
import com.jiangzg.mianmian.view.FrescoAvatarView;

import java.util.Locale;

/**
 * Created by JZG on 2018/3/13.
 * 日记适配器
 */
public class DiaryAdapter extends BaseQuickAdapter<Diary, BaseViewHolder> {

    private FragmentActivity mActivity;
    private final Couple couple;

    public DiaryAdapter(FragmentActivity activity) {
        super(R.layout.list_item_diary);
        mActivity = activity;
        couple = SPHelper.getCouple();
    }

    @Override
    protected void convert(BaseViewHolder helper, Diary item) {
        String avatar = Couple.getAvatar(couple, item.getUserId());
        String happen = TimeHelper.getTimeShowCnSpace_HM_MD_YMD_ByGo(item.getHappenAt());
        String content = item.getContentText();
        String textFormat = mActivity.getString(R.string.text_number_space_colon_holder);
        String textCount = String.format(Locale.getDefault(), textFormat, content == null ? 0 : content.length());
        String readFormat = mActivity.getString(R.string.read_space_colon_holder);
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

    public void showDeleteDialogNoApi(final int position) {
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .content(R.string.confirm_remove_this_diary)
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        remove(position);
                    }
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

}
