package com.jiangzg.mianmian.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Notice;
import com.jiangzg.mianmian.helper.ConvertHelper;

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
        // TODO noReadCount

    }

}
