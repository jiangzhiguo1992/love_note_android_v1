package com.jiangzg.lovenote.controller.adapter.note;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.helper.ApiHelper;
import com.jiangzg.lovenote.helper.ConsHelper;
import com.jiangzg.lovenote.helper.DialogHelper;
import com.jiangzg.lovenote.helper.RetrofitHelper;
import com.jiangzg.lovenote.helper.RxBus;
import com.jiangzg.lovenote.helper.TimeHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.Word;

import retrofit2.Call;

/**
 * Created by JZG on 2018/3/12.
 * 留言
 */
public class WordAdapter extends BaseMultiItemQuickAdapter<Word, BaseViewHolder> {

    private BaseActivity mActivity;

    public WordAdapter(BaseActivity activity) {
        super(null);
        addItemType(ApiHelper.LIST_NOTE_MY, R.layout.list_item_word_right);
        addItemType(ApiHelper.LIST_NOTE_TA, R.layout.list_item_word_left);
        mActivity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, Word item) {
        // data
        String timeDay = TimeHelper.getTimeShowLine_MD_YMD_ByGo(item.getCreateAt());
        String timeClock = DateUtils.getStr(TimeHelper.getJavaTimeByGo(item.getCreateAt()), ConstantUtils.FORMAT_H_M);
        String content = item.getContentText();
        // view
        helper.setText(R.id.tvCreateAt, timeDay);
        helper.setText(R.id.tvClock, timeClock);
        helper.setText(R.id.tvContent, content);
        // click
        helper.addOnLongClickListener(R.id.cvContent);
    }

    public void showDeleteDialog(final int position) {
        Word item = getItem(position);
        if (!item.isMine()) {
            ToastUtils.show(mActivity.getString(R.string.can_operation_self_create_word));
            return;
        }
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .content(R.string.confirm_del_this_word)
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive((dialog1, which) -> deleteApi(position))
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void deleteApi(int position) {
        final Word item = getItem(position);
        Call<Result> call = new RetrofitHelper().call(API.class).noteWordDel(item.getId());
        MaterialDialog loading = mActivity.getLoading(true);
        RetrofitHelper.enqueue(call, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxBus.post(new RxBus.Event<>(ConsHelper.EVENT_WORD_LIST_ITEM_DELETE, item));
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

}
