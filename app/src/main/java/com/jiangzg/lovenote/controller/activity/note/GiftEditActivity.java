package com.jiangzg.lovenote.controller.activity.note;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.common.DateUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.DialogUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.controller.adapter.common.ImgSquareEditAdapter;
import com.jiangzg.lovenote.helper.common.OssHelper;
import com.jiangzg.lovenote.helper.common.RxBus;
import com.jiangzg.lovenote.helper.common.SPHelper;
import com.jiangzg.lovenote.helper.common.TimeHelper;
import com.jiangzg.lovenote.helper.media.PickHelper;
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.helper.view.DialogHelper;
import com.jiangzg.lovenote.helper.view.RecyclerHelper;
import com.jiangzg.lovenote.helper.view.ViewHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.Gift;
import com.jiangzg.lovenote.model.entity.User;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

public class GiftEditActivity extends BaseActivity<GiftEditActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.etTitle)
    EditText etTitle;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.llHappenAt)
    LinearLayout llHappenAt;
    @BindView(R.id.tvHappenAt)
    TextView tvHappenAt;
    @BindView(R.id.llHappenUser)
    LinearLayout llHappenUser;
    @BindView(R.id.tvHappenUser)
    TextView tvHappenUser;

    private Gift gift;
    private RecyclerHelper recyclerHelper;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, GiftEditActivity.class);
        intent.putExtra("from", BaseActivity.ACT_EDIT_FROM_ADD);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from, Gift gift) {
        if (gift == null) {
            goActivity(from);
            return;
        } else if (!gift.isMine()) {
            ToastUtils.show(from.getString(R.string.can_operation_self_create_note));
            return;
        }
        Intent intent = new Intent(from, GiftEditActivity.class);
        intent.putExtra("from", BaseActivity.ACT_EDIT_FROM_UPDATE);
        intent.putExtra("gift", gift);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_gift_edit;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.gift), true);
        // init
        if (isFromUpdate()) {
            gift = intent.getParcelableExtra("gift");
        }
        if (gift == null) {
            gift = new Gift();
        }
        if (gift.getHappenAt() == 0) {
            gift.setHappenAt(TimeHelper.getGoTimeByJava(DateUtils.getCurrentLong()));
        }
        if (gift.getReceiveId() == 0) {
            User me = SPHelper.getMe();
            if (me != null) {
                gift.setReceiveId(me.getId());
            }
        }
        // etTitle
        String format = getString(R.string.please_input_title_no_over_holder_text);
        String hint = String.format(Locale.getDefault(), format, SPHelper.getLimit().getGiftTitleLength());
        etTitle.setHint(hint);
        etTitle.setText(gift.getTitle());
        // recycler
        int limitImagesCount = SPHelper.getVipLimit().getGiftImageCount();
        if (isFromUpdate()) {
            // 编辑
            if (gift.getContentImageList() == null || gift.getContentImageList().size() <= 0) {
                // 旧数据没有图片
                setRecyclerShow(limitImagesCount > 0, limitImagesCount);
            } else {
                // 旧数据有图片
                int imgCount = Math.max(limitImagesCount, gift.getContentImageList().size());
                setRecyclerShow(imgCount > 0, imgCount);
            }
        } else {
            // 添加
            setRecyclerShow(limitImagesCount > 0, limitImagesCount);
        }
        // date
        refreshDateView();
        // receive
        refreshReceiveUser();
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
    }

    @Override
    protected void onFinish(Bundle state) {
        RecyclerHelper.release(recyclerHelper);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isFromUpdate()) {
            getMenuInflater().inflate(R.menu.del_commit, menu);
        } else {
            getMenuInflater().inflate(R.menu.commit, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        if (requestCode == BaseActivity.REQUEST_PICTURE) {
            // 相册
            List<String> pathList = PickHelper.getResultFilePathList(mActivity, data);
            if (pathList == null || pathList.size() <= 0) {
                ToastUtils.show(getString(R.string.file_no_exits));
                return;
            }
            if (recyclerHelper == null) return;
            ImgSquareEditAdapter adapter = recyclerHelper.getAdapter();
            if (adapter == null) return;
            adapter.addFileDataList(pathList);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuCommit: // 提交
                checkPush();
                return true;
            case R.id.menuDel: // 删除
                showDeleteDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.llHappenAt, R.id.llHappenUser})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.llHappenAt: // 日期
                showDatePicker();
                break;
            case R.id.llHappenUser: // 所属
                showUserDialog();
                break;
        }
    }

    private boolean isFromUpdate() {
        return getIntent().getIntExtra("from", BaseActivity.ACT_EDIT_FROM_ADD) == BaseActivity.ACT_EDIT_FROM_UPDATE;
    }

    private void setRecyclerShow(boolean show, int childCount) {
        if (gift == null) return;
        if (!show) {
            rv.setVisibility(View.GONE);
            return;
        }
        rv.setVisibility(View.VISIBLE);
        int spanCount = 3;
        ImgSquareEditAdapter imgAdapter = new ImgSquareEditAdapter(mActivity, spanCount, childCount);
        imgAdapter.setOnAddClick(() -> {
            int maxCount = childCount - imgAdapter.getOssData().size() - imgAdapter.getFileData().size();
            PickHelper.selectImage(mActivity, maxCount, true);
        });
        if (gift.getContentImageList() != null && gift.getContentImageList().size() > 0) {
            imgAdapter.setOssData(gift.getContentImageList());
        }
        if (recyclerHelper == null) {
            recyclerHelper = new RecyclerHelper(rv)
                    .initLayoutManager(new GridLayoutManager(mActivity, spanCount))
                    .initAdapter(imgAdapter)
                    .setAdapter();
        }
    }

    private void showDatePicker() {
        if (gift == null) return;
        DialogHelper.showDatePicker(mActivity, TimeHelper.getJavaTimeByGo(gift.getHappenAt()), time -> {
            gift.setHappenAt(TimeHelper.getGoTimeByJava(time));
            refreshDateView();
        });
    }

    private void refreshDateView() {
        if (gift == null) return;
        String happen = TimeHelper.getTimeShowLocal_HM_MD_YMD_ByGo(gift.getHappenAt());
        tvHappenAt.setText(String.format(Locale.getDefault(), getString(R.string.time_colon_space_holder), happen));
    }

    private void showUserDialog() {
        User me = SPHelper.getMe();
        User ta = SPHelper.getTa();
        if (gift == null || me == null || ta == null) return;
        int searchIndex = (gift.getReceiveId() == ta.getId()) ? 1 : 0;
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .title(R.string.select_user)
                .items(new String[]{getString(R.string.me_de), getString(R.string.ta_de)})
                .itemsCallbackSingleChoice(searchIndex, (dialog1, view, which, text) -> {
                    if (which < 0 || which > 1) {
                        return true;
                    }
                    gift.setReceiveId(which == 0 ? me.getId() : ta.getId());
                    refreshReceiveUser();
                    DialogUtils.dismiss(dialog1);
                    return true;
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void refreshReceiveUser() {
        User me = SPHelper.getMe();
        User ta = SPHelper.getTa();
        if (gift == null || me == null || ta == null) return;
        if (gift.getReceiveId() == ta.getId()) {
            tvHappenUser.setText(String.format(Locale.getDefault(), getString(R.string.belong_colon_space_holder), getString(R.string.ta_de)));
        } else {
            tvHappenUser.setText(String.format(Locale.getDefault(), getString(R.string.belong_colon_space_holder), getString(R.string.me_de)));
        }
    }

    private void checkPush() {
        if (gift == null) return;
        String title = etTitle.getText().toString();
        if (StringUtils.isEmpty(title)) {
            ToastUtils.show(etTitle.getHint().toString());
            return;
        } else if (title.length() > SPHelper.getLimit().getGiftTitleLength()) {
            ToastUtils.show(etTitle.getHint().toString());
            return;
        }
        gift.setTitle(title);
        List<String> fileData = null;
        List<String> ossPaths = null;
        if (recyclerHelper != null && recyclerHelper.getAdapter() != null) {
            ImgSquareEditAdapter adapter = recyclerHelper.getAdapter();
            fileData = adapter.getFileData();
            ossPaths = adapter.getOssData();
        }
        if (fileData != null && fileData.size() > 0) {
            ossUploadImages(fileData);
        } else {
            api(ossPaths);
        }
    }

    private void ossUploadImages(List<String> fileData) {
        if (gift == null) return;
        OssHelper.uploadGift(mActivity, fileData, new OssHelper.OssUploadsCallBack() {
            @Override
            public void success(List<File> sourceList, List<String> ossKeyList, List<String> successList) {
                if (recyclerHelper == null) return;
                ImgSquareEditAdapter adapter = recyclerHelper.getAdapter();
                if (adapter == null) return;
                List<String> ossData = adapter.getOssData();
                ossData.addAll(successList == null ? new ArrayList<>() : successList);
                api(ossData);
            }

            @Override
            public void failure(List<File> sourceList, String errMsg, int index) {

            }
        });
    }

    private void api(List<String> ossPathList) {
        if (gift == null) return;
        gift.setContentImageList(ossPathList);
        if (isFromUpdate()) {
            updateApi();
        } else {
            addApi();
        }
    }

    private void updateApi() {
        if (gift == null) return;
        Call<Result> api = new RetrofitHelper().call(API.class).noteGiftUpdate(gift);
        RetrofitHelper.enqueue(api, getLoading(false), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                Gift gift = data.getGift();
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_GIFT_LIST_ITEM_REFRESH, gift));
                // finish
                mActivity.finish();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                // 上传失败不要删除，还可以继续上传
            }
        });
        pushApi(api);
    }

    private void addApi() {
        if (gift == null) return;
        Call<Result> api = new RetrofitHelper().call(API.class).noteGiftAdd(gift);
        RetrofitHelper.enqueue(api, getLoading(false), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_GIFT_LIST_REFRESH, new ArrayList<>()));
                // finish
                mActivity.finish();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
        pushApi(api);
    }

    private void showDeleteDialog() {
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .content(R.string.confirm_delete_this_note)
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive((dialog1, which) -> deleteApi())
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void deleteApi() {
        if (gift == null) return;
        Call<Result> api = new RetrofitHelper().call(API.class).noteGiftDel(gift.getId());
        RetrofitHelper.enqueue(api, getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_GIFT_LIST_ITEM_DELETE, gift));
                // finish
                mActivity.finish();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
        pushApi(api);
    }

}
