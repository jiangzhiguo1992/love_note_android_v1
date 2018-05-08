package com.jiangzg.mianmian.adapter;

import com.amap.api.services.core.PoiItem;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.base.BaseActivity;

/**
 * Created by JZG on 2018/3/12.
 * 帮助列表适配器
 */
public class MapSelectAdapter extends BaseQuickAdapter<PoiItem, BaseViewHolder> {

   private BaseActivity mActivity;

    public MapSelectAdapter(BaseActivity activity) {
        super(R.layout.list_item_map_select);
        mActivity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, PoiItem item) {
        String adName = item.getAdName();
        helper.setText(R.id.tvAddress, adName);
    }

}
