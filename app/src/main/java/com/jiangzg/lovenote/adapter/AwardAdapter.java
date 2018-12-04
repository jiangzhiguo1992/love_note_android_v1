package com.jiangzg.lovenote.adapter;

import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.base.BaseActivity;
import com.jiangzg.lovenote.helper.ConsHelper;
import com.jiangzg.lovenote.helper.DialogHelper;
import com.jiangzg.lovenote.helper.RetrofitHelper;
import com.jiangzg.lovenote.helper.RxBus;
import com.jiangzg.lovenote.helper.SPHelper;
import com.jiangzg.lovenote.helper.TimeHelper;
import com.jiangzg.lovenote.helper.UserHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.Award;
import com.jiangzg.lovenote.model.entity.Couple;
import com.jiangzg.lovenote.model.entity.User;
import com.jiangzg.lovenote.view.FrescoAvatarView;

import retrofit2.Call;

/**
 * Created by JZG on 2018/3/13.
 * 奖励适配器
 */
public class AwardAdapter extends BaseQuickAdapter<Award, BaseViewHolder> {

    private final User me;
    private final Couple couple;
    private BaseActivity mActivity;

    public AwardAdapter(BaseActivity activity) {
        super(R.layout.list_item_award);
        mActivity = activity;
        me = SPHelper.getMe();
        couple = SPHelper.getCouple();
    }

    @Override
    protected void convert(BaseViewHolder helper, Award item) {
        // data
        boolean isMine = (item.getHappenId() == me.getId());
        String avatar = UserHelper.getAvatar(me, item.getHappenId());
        String content = item.getContentText();
        String happen = TimeHelper.getTimeShowLine_HM_MDHM_YMDHM_ByGo(item.getHappenAt());
        String creator = UserHelper.getName(couple, item.getUserId());
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
        helper.setText(R.id.tvCreator, creator);
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
                RxBus.Event<Award> event = new RxBus.Event<>(ConsHelper.EVENT_AWARD_LIST_ITEM_DELETE, item);
                RxBus.post(event);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

}
