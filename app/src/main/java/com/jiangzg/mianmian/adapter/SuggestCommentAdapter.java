package com.jiangzg.mianmian.adapter;

import android.os.Build;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.ConvertUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.common.SuggestDetailActivity;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.domain.SuggestComment;
import com.jiangzg.mianmian.helper.API;
import com.jiangzg.mianmian.helper.ConvertHelper;
import com.jiangzg.mianmian.helper.DialogHelper;
import com.jiangzg.mianmian.helper.RetrofitHelper;
import com.jiangzg.mianmian.view.GWrapView;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;

/**
 * Created by JZG on 2018/3/15.
 * 意见反馈评论适配器
 */
public class SuggestCommentAdapter extends BaseQuickAdapter<SuggestComment, BaseViewHolder> {

    private BaseActivity mActivity;
    private final int dp7;
    private final int dp5;
    private final int dp2;

    public SuggestCommentAdapter(BaseActivity activity) {
        super(R.layout.list_item_suggest_comment);
        mActivity = activity;
        dp7 = ConvertUtils.dp2px(7);
        dp5 = ConvertUtils.dp2px(5);
        dp2 = ConvertUtils.dp2px(2);
    }

    @Override
    protected void convert(BaseViewHolder helper, SuggestComment item) {
        // data
        int layoutPosition = helper.getLayoutPosition();
        long createdAt = item.getCreateAt();
        String create = ConvertHelper.getTimeShowLine_HM_MD_YMD_ByGo(createdAt);
        String title = String.format(Locale.getDefault(), mActivity.getString(R.string.holder_storey_in_holder_say), layoutPosition, create);
        String contentText = item.getContentText();
        List<String> tagList = item.getTagList();
        // view
        helper.setText(R.id.tvTitle, title);
        helper.setText(R.id.tvContent, contentText);
        GWrapView wvTag = helper.getView(R.id.wvTag);
        wvTag.removeAllChild();
        for (String tag : tagList) {
            View tagView = getTagView(tag);
            wvTag.addChild(tagView);
        }
    }

    private View getTagView(String show) {
        FrameLayout.LayoutParams mTextLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mTextLayoutParams.setMarginEnd(dp7);

        TextView textView = new TextView(mActivity);
        textView.setLayoutParams(mTextLayoutParams);
        textView.setBackgroundResource(R.drawable.shape_r2_solid_primary);
        textView.setPadding(dp5, dp2, dp5, dp2);
        textView.setGravity(Gravity.CENTER);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textView.setTextAppearance(R.style.FontWhiteSmall);
        } else {
            textView.setTextAppearance(mActivity, R.style.FontWhiteSmall);
        }
        textView.setText(show);
        return textView;
    }

    // 删除评论
    public void delComment(final int position) {
        SuggestComment item = getItem(position);
        if (!item.isMine()) {
            return;
        }
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .content(R.string.confirm_del_comment)
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        delCommentApi(position);
                    }
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    public void delCommentApi(final int position) {
        final SuggestComment item = getItem(position);
        Call<Result> call = new RetrofitHelper().call(API.class).suggestCommentDel(item.getId());
        MaterialDialog loading = mActivity.getLoading(true);
        RetrofitHelper.enqueue(call, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                remove(position);
                if (mActivity instanceof SuggestDetailActivity) {
                    SuggestDetailActivity activity = (SuggestDetailActivity) mActivity;
                    activity.refreshSuggest();
                }
            }

            @Override
            public void onFailure(String errMsg) {
            }
        });
    }


}
