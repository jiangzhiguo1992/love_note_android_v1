package com.jiangzg.lovenote.controller.adapter.more;

import android.content.res.ColorStateList;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.widget.EditText;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.view.ViewUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.helper.common.ShowHelper;
import com.jiangzg.lovenote.helper.common.UserHelper;
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.helper.view.DialogHelper;
import com.jiangzg.lovenote.helper.view.ThemeHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.Couple;
import com.jiangzg.lovenote.model.entity.MatchCoin;
import com.jiangzg.lovenote.model.entity.MatchPoint;
import com.jiangzg.lovenote.model.entity.MatchReport;
import com.jiangzg.lovenote.model.entity.MatchWork;
import com.jiangzg.lovenote.view.FrescoAvatarView;

import retrofit2.Call;

/**
 * Created by JZG on 2018/3/13.
 * letter适配器
 */
public class MatchLetterAdapter extends BaseQuickAdapter<MatchWork, BaseViewHolder> {

    private BaseActivity mActivity;
    private final ColorStateList colorPrimaryStateList, colorHintStateList;

    public MatchLetterAdapter(BaseActivity activity) {
        super(R.layout.list_item_match_letter);
        mActivity = activity;
        // color
        int rId = ViewUtils.getColorPrimary(activity);
        int colorPrimary = ContextCompat.getColor(activity, rId);
        int colorHint = ContextCompat.getColor(activity, R.color.font_hint);
        colorPrimaryStateList = ColorStateList.valueOf(colorPrimary);
        colorHintStateList = ColorStateList.valueOf(colorHint);
    }

    @Override
    protected void convert(BaseViewHolder helper, MatchWork item) {
        // data
        String content = item.getTitle();
        Couple couple = item.getCouple();
        String avatar = UserHelper.getAvatar(couple, item.getUserId());
        String name = UserHelper.getName(couple, item.getUserId(), true);
        String coinCount = ShowHelper.getShowCount2Thousand(item.getCoinCount());
        String pointCount = ShowHelper.getShowCount2Thousand(item.getPointCount());
        boolean coin = item.isCoin();
        boolean point = item.isPoint();
        // view
        if (item.isScreen() || item.isDelete()) {
            helper.setVisible(R.id.tvContent, false);
            helper.setVisible(R.id.tvCover, true);
            helper.setText(R.id.tvCover, item.isScreen() ? R.string.work_already_be_screen : R.string.work_already_be_delete);
        } else {
            helper.setVisible(R.id.tvContent, true);
            helper.setVisible(R.id.tvCover, false);
            helper.setText(R.id.tvContent, content);
            helper.setBackgroundColor(R.id.tvContent, ContextCompat.getColor(mActivity, ThemeHelper.getThemePrimaryRandomRes()));
        }
        FrescoAvatarView ivAvatar = helper.getView(R.id.ivAvatar);
        ivAvatar.setData(avatar);
        helper.setText(R.id.tvName, name);
        helper.setText(R.id.tvCoinCount, coinCount);
        helper.setText(R.id.tvPointCount, pointCount);
        ImageView ivCoin = helper.getView(R.id.ivCoin);
        ivCoin.setImageTintList(coin ? colorPrimaryStateList : colorHintStateList);
        ImageView ivPoint = helper.getView(R.id.ivPoint);
        ivPoint.setImageTintList(point ? colorPrimaryStateList : colorHintStateList);
        // listener
        helper.addOnClickListener(R.id.tvContent);
        helper.addOnClickListener(R.id.llCoin);
        helper.addOnClickListener(R.id.llPoint);
        helper.addOnClickListener(R.id.ivMore);
    }

    public void showDeleteDialog(final int position) {
        MatchWork item = getItem(position);
        if (!item.isMine()) return;
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .content(R.string.confirm_del_this_work)
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive((dialog1, which) -> delWorks(position))
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void delWorks(final int position) {
        MatchWork item = getItem(position);
        Call<Result> api = new RetrofitHelper().call(API.class).moreMatchWorkDel(item.getId());
        RetrofitHelper.enqueue(api, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                remove(position);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
        mActivity.pushApi(api);
    }

    public void coinAdd(final int position) {
        String hint = mActivity.getString(R.string.input_coin_count);
        MaterialDialog dialogName = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .autoDismiss(true)
                .inputType(InputType.TYPE_CLASS_NUMBER)
                .input(hint, "", false, (dialog, input) -> LogUtils.i(MatchLetterAdapter.class, "onInput", input.toString()))
                .inputRange(1, 10)
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive((dialog, which) -> {
                    // api
                    EditText editText = dialog.getInputEditText();
                    if (editText != null) {
                        String input = editText.getText().toString();
                        coinApi(position, input);
                    }
                })
                .build();
        DialogHelper.showWithAnim(dialogName);
    }

    private void coinApi(final int position, String input) {
        final MatchWork item = getItem(position);
        if (!StringUtils.isNumber(input)) return;
        final int coinCount = Integer.parseInt(input);
        if (coinCount <= 0) return;
        MatchCoin body = new MatchCoin();
        body.setMatchWorkId(item.getId());
        body.setCoinCount(coinCount);
        // api
        Call<Result> api = new RetrofitHelper().call(API.class).moreMatchCoinAdd(body);
        RetrofitHelper.enqueue(api, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                item.setCoinCount(item.getCoinCount() + coinCount);
                notifyItemChanged(position + getHeaderLayoutCount());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
        mActivity.pushApi(api);
    }

    public void pointToggle(final int position, boolean isApi) {
        final MatchWork item = getItem(position);
        boolean newPoint = !item.isPoint();
        int newPointCount = newPoint ? item.getPointCount() + 1 : item.getPointCount() - 1;
        if (newPointCount < 0) {
            newPointCount = 0;
        }
        item.setPoint(newPoint);
        item.setPointCount(newPointCount);
        notifyItemChanged(position + getHeaderLayoutCount());
        if (!isApi) return;
        MatchPoint body = new MatchPoint();
        body.setMatchWorkId(item.getId());
        // api
        Call<Result> api = new RetrofitHelper().call(API.class).moreMatchPointAdd(body);
        RetrofitHelper.enqueue(api, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                pointToggle(position, false);
            }
        });
        mActivity.pushApi(api);
    }

    public void showReportDialog(final int position) {
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .content(R.string.confirm_report_this_work)
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive((dialog1, which) -> reportAdd(position, true))
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void reportAdd(final int position, boolean isApi) {
        final MatchWork item = getItem(position);
        if (item.isReport()) return;
        item.setReport(true);
        notifyItemChanged(position + getHeaderLayoutCount());
        if (!isApi) return;
        MatchReport body = new MatchReport();
        body.setMatchWorkId(item.getId());
        // api
        Call<Result> api = new RetrofitHelper().call(API.class).moreMatchReportAdd(body);
        RetrofitHelper.enqueue(api, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                reportAdd(position, false);
            }
        });
        mActivity.pushApi(api);
    }

}
