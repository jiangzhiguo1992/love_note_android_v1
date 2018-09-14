package com.jiangzg.lovenote_admin.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.lovenote_admin.R;
import com.jiangzg.lovenote_admin.activity.CoupleDetailActivity;
import com.jiangzg.lovenote_admin.base.BaseActivity;
import com.jiangzg.lovenote_admin.domain.Bill;
import com.jiangzg.lovenote_admin.domain.Result;
import com.jiangzg.lovenote_admin.helper.API;
import com.jiangzg.lovenote_admin.helper.RetrofitHelper;

import java.util.Locale;

import retrofit2.Call;

/**
 * Created by JZG on 2018/3/13.
 * bill适配器
 */
public class BillAdapter extends BaseQuickAdapter<Bill, BaseViewHolder> {

    private BaseActivity mActivity;

    public BillAdapter(BaseActivity activity) {
        super(R.layout.list_item_bill);
        mActivity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, Bill item) {
        // data
        String id = "id:" + item.getId();
        String uid = "uid:" + item.getUserId();
        String cid = "cid:" + item.getCoupleId();
        String create = DateUtils.getString(item.getCreateAt() * 1000, ConstantUtils.FORMAT_LINE_Y_M_D_H_M);
        String update = DateUtils.getString(item.getUpdateAt() * 1000, ConstantUtils.FORMAT_LINE_Y_M_D_H_M);
        String platformOs = item.getPlatformOs();
        String platformPay = Bill.getPlatformPayShow(item.getPlatformPay());
        String payType = Bill.getPayTypeShow(item.getPayType());
        String payAmount = String.format(Locale.getDefault(), "%.2f", item.getPayAmount());
        String tradeNo = item.getTradeNo();
        String tradePay = item.isTradePay() ? "已付款" : "未付款";
        String goodsType = Bill.getGoodsTypeShow(item.getGoodsType());
        String goodsOut = item.isGoodsOut() ? "已发货" : "未发货";
        // view
        helper.setText(R.id.tvId, id);
        helper.setText(R.id.tvUid, uid);
        helper.setText(R.id.tvCid, cid);
        helper.setText(R.id.tvCreate, "c:" + create);
        helper.setText(R.id.tvUpdate, "u:" + update);
        helper.setText(R.id.tvPlatformOs, platformOs);
        helper.setText(R.id.tvPlatformPay, platformPay);
        helper.setText(R.id.tvPayType, payType);
        helper.setText(R.id.tvPayAmount, payAmount);
        helper.setText(R.id.tvTradeNo, tradeNo);
        helper.setText(R.id.tvTradePay, tradePay);
        helper.setText(R.id.tvGoodsType, goodsType);
        helper.setText(R.id.tvGoodsOut, goodsOut);
    }

    public void goCouple(final int position) {
        Bill item = getItem(position);
        CoupleDetailActivity.goActivity(mActivity, item.getCoupleId());
    }

    public void checkBill(final int position) {
        Bill item = getItem(position);
        Call<Result> call = new RetrofitHelper().call(API.class).moreBillCheck(item.getId());
        RetrofitHelper.enqueue(call, mActivity.getLoading(true), null);
    }

}
