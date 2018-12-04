package com.jiangzg.lovenote.activity.note;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.adapter.ImgSquareEditAdapter;
import com.jiangzg.lovenote.base.BaseActivity;
import com.jiangzg.lovenote.helper.ConsHelper;
import com.jiangzg.lovenote.helper.DialogHelper;
import com.jiangzg.lovenote.helper.MediaPickHelper;
import com.jiangzg.lovenote.helper.OssHelper;
import com.jiangzg.lovenote.helper.RecyclerHelper;
import com.jiangzg.lovenote.helper.RetrofitHelper;
import com.jiangzg.lovenote.helper.RxBus;
import com.jiangzg.lovenote.helper.SPHelper;
import com.jiangzg.lovenote.helper.TimeHelper;
import com.jiangzg.lovenote.helper.UserHelper;
import com.jiangzg.lovenote.helper.ViewHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.entity.Diary;
import com.jiangzg.lovenote.model.entity.Gift;
import com.jiangzg.lovenote.model.entity.Result;
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
    @BindView(R.id.cvHappenAt)
    CardView cvHappenAt;
    @BindView(R.id.tvHappenAt)
    TextView tvHappenAt;
    @BindView(R.id.rgReceive)
    RadioGroup rgReceive;
    @BindView(R.id.rbReceiveMe)
    RadioButton rbReceiveMe;
    @BindView(R.id.rbReceiveTa)
    RadioButton rbReceiveTa;
    @BindView(R.id.rv)
    RecyclerView rv;

    private Gift gift;
    private RecyclerHelper recyclerHelper;
    private Call<Result> callUpdate;
    private Call<Result> callAdd;
    private Call<Result> callDel;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, GiftEditActivity.class);
        intent.putExtra("from", ConsHelper.ACT_EDIT_FROM_ADD);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from, Gift gift) {
        if (gift == null) {
            goActivity(from);
            return;
        } else if (!gift.isMine()) {
            ToastUtils.show(from.getString(R.string.can_operation_self_create_gift));
            return;
        }
        Intent intent = new Intent(from, GiftEditActivity.class);
        intent.putExtra("from", ConsHelper.ACT_EDIT_FROM_UPDATE);
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
        // etTitle
        String format = getString(R.string.please_input_title_no_over_holder_text);
        String hint = String.format(Locale.getDefault(), format, SPHelper.getLimit().getGiftTitleLength());
        etTitle.setHint(hint);
        etTitle.setText(gift.getTitle());
        // date
        refreshDateView();
        // receive
        initReceiveCheck();
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
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
    }

    @Override
    protected void onFinish(Bundle state) {
        RetrofitHelper.cancel(callAdd);
        RetrofitHelper.cancel(callUpdate);
        RetrofitHelper.cancel(callDel);
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
        if (requestCode == ConsHelper.REQUEST_PICTURE) {
            // 相册
            List<String> pathList = MediaPickHelper.getResultFilePathList(data);
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

    @OnClick({R.id.cvHappenAt})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cvHappenAt: // 日期
                showDatePicker();
                break;
        }
    }

    private boolean isFromUpdate() {
        return getIntent().getIntExtra("from", ConsHelper.ACT_EDIT_FROM_ADD) == ConsHelper.ACT_EDIT_FROM_UPDATE;
    }

    private void initReceiveCheck() {
        if (gift == null) return;
        final User user = SPHelper.getMe();
        rgReceive.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (gift == null) return;
                switch (checkedId) {
                    case R.id.rbReceiveMe: // 送给我
                        gift.setReceiveId(UserHelper.getMyId(user));
                        break;
                    case R.id.rbReceiveTa: // 送给ta
                        gift.setReceiveId(UserHelper.getTaId(user));
                        break;
                }
            }
        });
        long receiveId = gift.getReceiveId();
        if (receiveId == 0 || receiveId == user.getId()) {
            rbReceiveMe.setChecked(true);
        } else {
            rbReceiveTa.setChecked(true);
        }
    }

    private void setRecyclerShow(boolean show, int childCount) {
        if (gift == null) return;
        if (!show) {
            rv.setVisibility(View.GONE);
            return;
        }
        rv.setVisibility(View.VISIBLE);
        int spanCount = childCount > 3 ? 3 : childCount;
        ImgSquareEditAdapter imgAdapter = new ImgSquareEditAdapter(mActivity, spanCount, childCount);
        imgAdapter.setOnAddClick(new ImgSquareEditAdapter.OnAddClickListener() {
            @Override
            public void onAdd() {
                int maxCount = childCount - imgAdapter.getOssData().size() - imgAdapter.getFileData().size();
                MediaPickHelper.selectImage(mActivity, maxCount);
            }
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
        DialogHelper.showDatePicker(mActivity, TimeHelper.getJavaTimeByGo(gift.getHappenAt()), new DialogHelper.OnPickListener() {
            @Override
            public void onPick(long time) {
                gift.setHappenAt(TimeHelper.getGoTimeByJava(time));
                refreshDateView();
            }
        });
    }

    private void refreshDateView() {
        if (gift == null) return;
        String happen = TimeHelper.getTimeShowLocal_HM_MD_YMD_ByGo(gift.getHappenAt());
        tvHappenAt.setText(happen);
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
            public void success(List<File> sourceList, List<String> ossPathList) {
                if (recyclerHelper == null) return;
                ImgSquareEditAdapter adapter = recyclerHelper.getAdapter();
                if (adapter == null) return;
                List<String> ossData = adapter.getOssData();
                ossData.addAll(ossPathList == null ? new ArrayList<String>() : ossPathList);
                api(ossData);
            }

            @Override
            public void failure(List<File> sourceList, String errMsg) {
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
        MaterialDialog loading = getLoading(false);
        callUpdate = new RetrofitHelper().call(API.class).noteGiftUpdate(gift);
        RetrofitHelper.enqueue(callUpdate, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                Gift gift = data.getGift();
                RxBus.Event<Gift> eventList = new RxBus.Event<>(ConsHelper.EVENT_GIFT_LIST_ITEM_REFRESH, gift);
                RxBus.post(eventList);
                // finish
                mActivity.finish();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                // 上传失败不要删除，还可以继续上传
            }
        });
    }

    private void addApi() {
        if (gift == null) return;
        MaterialDialog loading = getLoading(false);
        callAdd = new RetrofitHelper().call(API.class).noteGiftAdd(gift);
        RetrofitHelper.enqueue(callAdd, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxBus.Event<ArrayList<Diary>> event = new RxBus.Event<>(ConsHelper.EVENT_GIFT_LIST_REFRESH, new ArrayList<Diary>());
                RxBus.post(event);
                // finish
                mActivity.finish();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

    private void showDeleteDialog() {
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .content(R.string.confirm_delete_this_gift)
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        deleteApi();
                    }
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void deleteApi() {
        if (gift == null) return;
        MaterialDialog loading = getLoading(true);
        callDel = new RetrofitHelper().call(API.class).noteGiftDel(gift.getId());
        RetrofitHelper.enqueue(callDel, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxBus.Event<Gift> event = new RxBus.Event<>(ConsHelper.EVENT_GIFT_LIST_ITEM_DELETE, gift);
                RxBus.post(event);
                // finish
                mActivity.finish();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

}
