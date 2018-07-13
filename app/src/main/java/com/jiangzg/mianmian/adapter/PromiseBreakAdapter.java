package com.jiangzg.mianmian.adapter;

import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Promise;
import com.jiangzg.mianmian.domain.PromiseBreak;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.domain.RxEvent;
import com.jiangzg.mianmian.helper.API;
import com.jiangzg.mianmian.helper.ConsHelper;
import com.jiangzg.mianmian.helper.DialogHelper;
import com.jiangzg.mianmian.helper.RetrofitHelper;
import com.jiangzg.mianmian.helper.RxBus;
import com.jiangzg.mianmian.helper.TimeHelper;

import retrofit2.Call;

/**
 * Created by JZG on 2018/3/13.
 * 梦境适配器
 */
public class PromiseBreakAdapter extends BaseQuickAdapter<PromiseBreak, BaseViewHolder> {

    private BaseActivity mActivity;

    public PromiseBreakAdapter(BaseActivity activity) {
        super(R.layout.list_item_promise_break);
        mActivity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, PromiseBreak item) {
        String happen = TimeHelper.getTimeShowCn_HM_MDHM_YMDHM_ByGo(item.getHappenAt());
        String content = item.getContentText();
        // view
        helper.setText(R.id.tvHappenAt, happen);
        helper.setText(R.id.tvContent, content);
    }

    public void showDeleteDialog(final int position, final Promise promise) {
        PromiseBreak item = getItem(position);
        if (!item.isMine()) {
            ToastUtils.show(mActivity.getString(R.string.can_operation_self_create_promise_break));
            return;
        }
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .content(R.string.confirm_delete_this_promise_break)
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        delCommentApi(position, promise);
                    }
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void delCommentApi(final int position, final Promise promise) {
        final PromiseBreak item = getItem(position);
        Call<Result> call = new RetrofitHelper().call(API.class).bookPromiseBreakDel(item.getId());
        MaterialDialog loading = mActivity.getLoading(true);
        RetrofitHelper.enqueue(call, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                remove(position);
                // event
                RxEvent<Promise> eventDetail = new RxEvent<>(ConsHelper.EVENT_PROMISE_DETAIL_REFRESH, new Promise());
                RxBus.post(eventDetail);
                if (promise != null) {
                    int breakCount = promise.getBreakCount() - 1;
                    promise.setBreakCount(breakCount < 0 ? 0 : breakCount);
                    RxEvent<Promise> eventListItem = new RxEvent<>(ConsHelper.EVENT_PROMISE_LIST_ITEM_REFRESH, promise);
                    RxBus.post(eventListItem);
                }
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

}
