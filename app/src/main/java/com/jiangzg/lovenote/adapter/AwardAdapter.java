package com.jiangzg.lovenote.adapter;

import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.base.BaseActivity;
import com.jiangzg.lovenote.domain.Award;
import com.jiangzg.lovenote.domain.Result;
import com.jiangzg.lovenote.domain.RxEvent;
import com.jiangzg.lovenote.domain.User;
import com.jiangzg.lovenote.helper.API;
import com.jiangzg.lovenote.helper.ConsHelper;
import com.jiangzg.lovenote.helper.DialogHelper;
import com.jiangzg.lovenote.helper.RetrofitHelper;
import com.jiangzg.lovenote.helper.RxBus;
import com.jiangzg.lovenote.helper.SPHelper;
import com.jiangzg.lovenote.helper.TimeHelper;
import com.jiangzg.lovenote.view.FrescoAvatarView;

import retrofit2.Call;

/**
 * Created by JZG on 2018/3/13.
 * 奖励适配器
 */
public class AwardAdapter extends BaseQuickAdapter<Award, BaseViewHolder> {

    private BaseActivity mActivity;
    private final User me;

    public AwardAdapter(BaseActivity activity) {
        super(R.layout.list_item_award);
        mActivity = activity;
        me = SPHelper.getMe();
    }

    @Override
    protected void convert(BaseViewHolder helper, Award item) {
        // data
        boolean isMine = (item.getHappenId() == me.getId());
        String avatar = me.getAvatarInCp(item.getHappenId());
        String happen = TimeHelper.getTimeShowLocal_HM_MDHM_YMDHM_ByGo(item.getHappenAt());
        String content = item.getContentText();
        String scoreShow = String.valueOf(item.getScoreChange());
        if (item.getScoreChange() > 0) {
            scoreShow = "+" + scoreShow;
        }
        // view
        FrescoAvatarView ivAvatar = helper.getView(R.id.ivAvatar);
        ivAvatar.setData(avatar);
        helper.setVisible(R.id.cvScoreMe, isMine);
        helper.setVisible(R.id.cvScoreTa, !isMine);
        helper.setText(isMine ? R.id.tvScoreMe : R.id.tvScoreTa, scoreShow);
        helper.setText(R.id.tvHappenAt, happen);
        helper.setText(R.id.tvContent, content);
    }

    public void showDeleteDialog(final int position) {
        Award item = getItem(position);
        if (!item.isMine()) {
            ToastUtils.show(mActivity.getString(R.string.can_operation_self_create_award));
            return;
        }
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .content(R.string.confirm_delete_this_award)
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
        final Award item = getItem(position);
        Call<Result> call = new RetrofitHelper().call(API.class).noteAwardDel(item.getId());
        MaterialDialog loading = mActivity.getLoading(true);
        RetrofitHelper.enqueue(call, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxEvent<Award> event = new RxEvent<>(ConsHelper.EVENT_AWARD_LIST_ITEM_DELETE, item);
                RxBus.post(event);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

}
