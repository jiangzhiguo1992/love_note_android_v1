package com.jiangzg.lovenote.controller.adapter.note;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.helper.common.RxBus;
import com.jiangzg.lovenote.helper.common.TimeHelper;
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.helper.view.DialogHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.Promise;
import com.jiangzg.lovenote.model.entity.PromiseBreak;

import retrofit2.Call;

/**
 * Created by JZG on 2018/3/13.
 * 承诺违反适配器
 */
public class PromiseBreakAdapter extends BaseQuickAdapter<PromiseBreak, BaseViewHolder> {

    private BaseActivity mActivity;

    public PromiseBreakAdapter(BaseActivity activity) {
        super(R.layout.list_item_promise_break);
        mActivity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, PromiseBreak item) {
        String happen = TimeHelper.getTimeShowLine_HM_MDHM_YMDHM_ByGo(item.getHappenAt());
        String content = item.getContentText();
        // view
        helper.setText(R.id.tvHappenAt, happen);
        helper.setText(R.id.tvContent, content);
    }

    public void showDeleteDialog(final int position, final Promise promise) {
        PromiseBreak item = getItem(position);
        if (!item.isMine()) {
            ToastUtils.show(mActivity.getString(R.string.can_operation_self_create_note));
            return;
        }
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .content(R.string.confirm_delete_this_note)
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive((dialog1, which) -> delCommentApi(position, promise))
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void delCommentApi(final int position, final Promise promise) {
        final PromiseBreak item = getItem(position);
        Call<Result> api = new RetrofitHelper().call(API.class).notePromiseBreakDel(item.getId());
        RetrofitHelper.enqueue(api, mActivity.getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                remove(position);
                // event
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_PROMISE_DETAIL_REFRESH, promise));
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
        mActivity.pushApi(api);
    }

}
