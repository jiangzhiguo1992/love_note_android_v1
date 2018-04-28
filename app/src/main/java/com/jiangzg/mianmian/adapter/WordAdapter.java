package com.jiangzg.mianmian.adapter;

import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Couple;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.domain.RxEvent;
import com.jiangzg.mianmian.domain.Word;
import com.jiangzg.mianmian.helper.API;
import com.jiangzg.mianmian.helper.ApiHelper;
import com.jiangzg.mianmian.helper.ConsHelper;
import com.jiangzg.mianmian.helper.ConvertHelper;
import com.jiangzg.mianmian.helper.DialogHelper;
import com.jiangzg.mianmian.helper.RetrofitHelper;
import com.jiangzg.mianmian.helper.RxBus;
import com.jiangzg.mianmian.helper.SPHelper;
import com.jiangzg.mianmian.view.GImageAvatarView;

import retrofit2.Call;

/**
 * Created by JZG on 2018/3/12.
 * 留言
 */
public class WordAdapter extends BaseMultiItemQuickAdapter<Word, BaseViewHolder> {

    private BaseActivity mActivity;
    private final Couple couple;

    public WordAdapter(BaseActivity activity) {
        super(null);
        addItemType(ApiHelper.LIST_MY, R.layout.list_item_word_right);
        addItemType(ApiHelper.LIST_TA, R.layout.list_item_word_left);
        mActivity = activity;
        couple = SPHelper.getCouple();
    }

    @Override
    protected void convert(BaseViewHolder helper, Word item) {
        // data
        String avatar = ConvertHelper.getAvatarByCp(couple, item.getUserId());
        String createAt = ConvertHelper.getTimeShowLine_HM_MDHM_YMDHM_ByGo(item.getCreateAt());
        String content = item.getContent();
        // view
        GImageAvatarView ivAvatar = helper.getView(R.id.ivAvatar);
        ivAvatar.setDateAvatar(avatar);
        helper.setText(R.id.tvCreateAt, createAt);
        helper.setText(R.id.tvContent, content);
    }

    public void showDeleteDialog(final int position) {
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .content(R.string.confirm_del_this_word)
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

    public void deleteApi(int position) {
        final Word item = getItem(position);
        Call<Result> call = new RetrofitHelper().call(API.class).wordDel(item.getId());
        MaterialDialog loading = mActivity.getLoading(true);
        RetrofitHelper.enqueue(call, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxEvent<Word> event = new RxEvent<>(ConsHelper.EVENT_WORD_LIST_ITEM_DELETE, item);
                RxBus.post(event);
            }

            @Override
            public void onFailure(String errMsg) {
            }
        });
    }

}
