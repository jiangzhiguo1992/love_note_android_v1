package com.jiangzg.lovenote.controller.adapter.note;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.DateUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.helper.common.RxBus;
import com.jiangzg.lovenote.helper.common.TimeHelper;
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.helper.view.DialogHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.Sleep;

import java.util.Locale;

import retrofit2.Call;

/**
 * Created by JZG on 2018/3/12.
 * 睡眠适配器
 */
public class SleepAdapter extends BaseQuickAdapter<Sleep, BaseViewHolder> {

    private BaseActivity mActivity;

    public SleepAdapter(BaseActivity activity) {
        super(R.layout.list_item_sleep);
        mActivity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, Sleep item) {
        String time = DateUtils.getStr(TimeHelper.getJavaTimeByGo(item.getCreateAt()), DateUtils.FORMAT_H_M);
        String format = item.isSleep() ? mActivity.getString(R.string.holder_colon_sleep) : mActivity.getString(R.string.holder_colon_wake);
        String show = String.format(Locale.getDefault(), format, time);
        // view
        helper.setText(R.id.tvTime, show);
    }

    public void showDeleteDialog(final int position) {
        Sleep item = getItem(position);
        if (!item.isMine()) {
            ToastUtils.show(mActivity.getString(R.string.can_operation_self_create_sleep));
            return;
        }
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .content(R.string.confirm_del_this_sleep)
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive((dialog1, which) -> deleteApi(position))
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void deleteApi(int position) {
        final Sleep item = getItem(position);
        Call<Result> api = new RetrofitHelper().call(API.class).noteSleepDel(item.getId());
        RetrofitHelper.enqueue(api, mActivity.getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_SLEEP_LIST_ITEM_DELETE, item));
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
        mActivity.pushApi(api);
    }

}
