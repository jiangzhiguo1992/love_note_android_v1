package com.jiangzg.ita.adapter;

import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.ConvertUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.ita.R;
import com.jiangzg.ita.domain.SuggestComment;
import com.jiangzg.ita.helper.ConvertHelper;
import com.jiangzg.ita.view.GWrapView;

import java.util.List;

/**
 * Created by JZG on 2018/3/15.
 * 意见反馈评论适配器
 */
public class SuggestCommentAdapter extends BaseQuickAdapter<SuggestComment, BaseViewHolder> {

    private FragmentActivity mActivity;
    private final int dp7;
    private final int dp5;
    private final int dp2;

    public SuggestCommentAdapter(FragmentActivity activity) {
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
        String create = ConvertHelper.ConvertSecond2DiffDay(createdAt);
        String title = String.format(mActivity.getString(R.string.holder_storey_in_holder_say), layoutPosition, create);
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

    // todo 删除评论
    public void delComment(int position) {
        SuggestComment item = getItem(position);
        if (!item.isMine()) {
            return;
        }
        ToastUtils.show("删除");
    }

}
