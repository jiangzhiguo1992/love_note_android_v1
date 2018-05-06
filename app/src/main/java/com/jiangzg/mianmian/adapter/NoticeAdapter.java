package com.jiangzg.mianmian.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.common.WebActivity;
import com.jiangzg.mianmian.activity.settings.NoticeDetailActivity;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Notice;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.helper.API;
import com.jiangzg.mianmian.helper.ConvertHelper;
import com.jiangzg.mianmian.helper.RetrofitHelper;
import com.jiangzg.mianmian.helper.SPHelper;

import retrofit2.Call;

/**
 * Created by JZG on 2018/3/15.
 * 意见反馈评论适配器
 */
public class NoticeAdapter extends BaseQuickAdapter<Notice, BaseViewHolder> {

    private BaseActivity mActivity;

    public NoticeAdapter(BaseActivity activity) {
        super(R.layout.list_item_notice);
        mActivity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, Notice item) {
        // data
        long createdAt = item.getCreateAt();
        String create = ConvertHelper.getTimeShowCnSpace_HM_MD_YMD_ByGo(createdAt);
        String title = item.getTitle();
        boolean read = item.isRead();
        // view
        helper.setText(R.id.tvTime, create);
        helper.setText(R.id.tvTitle, title);
        helper.setVisible(R.id.tvNoRead, !read);
    }

    public void goDetail(int position) {
        Notice item = getItem(position);
        // read
        if (!item.isRead()) {
            long noticeNoReadCount = SPHelper.getNoticeNoReadCount();
            SPHelper.setNoticeNoReadCount(--noticeNoReadCount);
            noticeRead(item.getId());
        }
        item.setRead(true);
        notifyItemChanged(position); // noReadCount
        if (!StringUtils.isEmpty(item.getContentUrl())) {
            WebActivity.goActivity(mActivity, item.getContentUrl());
        } else {
            NoticeDetailActivity.goActivity(mActivity, item);
        }
    }

    private void noticeRead(long nid) {
        Call<Result> call = new RetrofitHelper().call(API.class).noticeRead(nid);
        RetrofitHelper.enqueue(call, null, null);
    }

}
