package com.jiangzg.lovenote.controller.adapter.settings;

import android.support.v4.app.FragmentActivity;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.common.BigImageActivity;
import com.jiangzg.lovenote.controller.activity.common.WebActivity;
import com.jiangzg.lovenote.controller.activity.settings.NoticeDetailActivity;
import com.jiangzg.lovenote.helper.RetrofitHelper;
import com.jiangzg.lovenote.helper.SPHelper;
import com.jiangzg.lovenote.helper.TimeHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.CommonCount;
import com.jiangzg.lovenote.model.entity.Notice;

import retrofit2.Call;

/**
 * Created by JZG on 2018/3/15.
 * 公告适配器
 */
public class NoticeAdapter extends BaseQuickAdapter<Notice, BaseViewHolder> {

    private FragmentActivity mActivity;

    public NoticeAdapter(FragmentActivity activity) {
        super(R.layout.list_item_notice);
        mActivity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, Notice item) {
        // data
        long createdAt = item.getCreateAt();
        String create = TimeHelper.getTimeShowLocal_HM_MD_YMD_ByGo(createdAt);
        String title = item.getTitle();
        boolean read = item.isRead();
        // view
        helper.setText(R.id.tvTime, create);
        helper.setText(R.id.tvTitle, title);
        helper.setVisible(R.id.tvNoRead, !read);
    }

    public void goNoticeDetail(int position) {
        Notice item = getItem(position);
        // read
        if (!item.isRead()) {
            CommonCount commonCount = SPHelper.getCommonCount();
            commonCount.setNoticeNewCount(commonCount.getNoticeNewCount() - 1);
            SPHelper.setCommonCount(commonCount);
            noticeRead(item.getId());
        }
        item.setRead(true);
        notifyItemChanged(position); // noReadCount
        switch (item.getContentType()) {
            case Notice.TYPE_URL: // 网页
                WebActivity.goActivity(mActivity, item.getTitle(), item.getContentText());
                break;
            case Notice.TYPE_IMAGE: // 图片
                BigImageActivity.goActivityByOss(mActivity, item.getContentText(), null);
                break;
            case Notice.TYPE_TEXT: // 文字
            default:
                NoticeDetailActivity.goActivity(mActivity, item);
                break;
        }
    }

    private void noticeRead(long nid) {
        Call<Result> call = new RetrofitHelper().call(API.class).setNoticeRead(nid);
        RetrofitHelper.enqueue(call, null, null);
    }

}
