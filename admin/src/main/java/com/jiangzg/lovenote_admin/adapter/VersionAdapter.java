package com.jiangzg.lovenote_admin.adapter;

import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.lovenote_admin.R;
import com.jiangzg.lovenote_admin.base.BaseActivity;
import com.jiangzg.lovenote_admin.domain.Result;
import com.jiangzg.lovenote_admin.domain.Version;
import com.jiangzg.lovenote_admin.helper.API;
import com.jiangzg.lovenote_admin.helper.DialogHelper;
import com.jiangzg.lovenote_admin.helper.RetrofitHelper;

import retrofit2.Call;

/**
 * Created by JZG on 2018/3/13.
 * version适配器
 */
public class VersionAdapter extends BaseQuickAdapter<Version, BaseViewHolder> {

    private BaseActivity mActivity;

    public VersionAdapter(BaseActivity activity) {
        super(R.layout.list_item_version);
        mActivity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, Version item) {
        // data
        String id = "id:" + item.getId();
        String create = "时间:" + DateUtils.getString(item.getCreateAt() * 1000, ConstantUtils.FORMAT_LINE_Y_M_D_H_M);
        //String update = "u:" + DateUtils.getString(item.getUpdateAt() * 1000, ConstantUtils.FORMAT_LINE_Y_M_D_H_M);
        String platform = item.getPlatform();
        String versionName = "name:" + item.getVersionName();
        String versionCode = "code:" + String.valueOf(item.getVersionCode());
        String updateUrl = "url:" + item.getUpdateUrl();
        String updateLog = item.getUpdateLog().replace("\\n", "\n");
        // view
        helper.setText(R.id.tvId, id);
        helper.setText(R.id.tvCreate, create);
        //helper.setText(R.id.tvUpdate, update);
        helper.setText(R.id.tvPlatform, platform);
        helper.setText(R.id.tvVersionName, versionName);
        helper.setText(R.id.tvVersionCode, versionCode);
        helper.setText(R.id.tvVersionUrl, updateUrl);
        helper.setText(R.id.tvVersionLog, updateLog);
    }

    public void showDeleteDialog(final int position) {
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .content(R.string.confirm_delete_this)
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

    private void deleteApi(final int position) {
        final Version item = getItem(position);
        Call<Result> call = new RetrofitHelper().call(API.class).versionDel(item.getId());
        MaterialDialog loading = mActivity.getLoading(true);
        RetrofitHelper.enqueue(call, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                remove(position);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

}
