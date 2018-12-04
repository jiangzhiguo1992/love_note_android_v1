package com.jiangzg.lovenote.activity.note;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
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
import com.jiangzg.lovenote.helper.ViewHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.Diary;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import retrofit2.Call;

public class DiaryEditActivity extends BaseActivity<DiaryEditActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.cvHappenAt)
    CardView cvHappenAt;
    @BindView(R.id.tvHappenAt)
    TextView tvHappenAt;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.etContent)
    EditText etContent;
    @BindView(R.id.tvContentLimit)
    TextView tvContentLimit;
    @BindView(R.id.btnPublish)
    Button btnPublish;
    @BindView(R.id.btnDraft)
    Button btnDraft;

    private Diary diary;
    private RecyclerHelper recyclerHelper;
    private Call<Result> callUpdate;
    private Call<Result> callAdd;
    private int limitContentLength;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, DiaryEditActivity.class);
        intent.putExtra("from", ConsHelper.ACT_EDIT_FROM_ADD);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from, Diary diary) {
        if (diary == null) {
            goActivity(from);
            return;
        } else if (!diary.isMine()) {
            ToastUtils.show(from.getString(R.string.can_operation_self_create_diary));
            return;
        }
        Intent intent = new Intent(from, DiaryEditActivity.class);
        intent.putExtra("from", ConsHelper.ACT_EDIT_FROM_UPDATE);
        intent.putExtra("diary", diary);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_diary_edit;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.diary), true);
        // init
        if (isFromUpdate()) {
            diary = intent.getParcelableExtra("diary");
        } else {
            diary = SPHelper.getDraftDiary();
        }
        if (diary == null) {
            diary = new Diary();
        }
        if (diary.getHappenAt() == 0) {
            diary.setHappenAt(TimeHelper.getGoTimeByJava(DateUtils.getCurrentLong()));
        }
        // date
        refreshDateView();
        // recycler
        int limitImagesCount = SPHelper.getVipLimit().getDiaryImageCount();
        if (isFromUpdate()) {
            // 编辑
            if (diary.getContentImageList() == null || diary.getContentImageList().size() <= 0) {
                // 旧数据没有图片
                setRecyclerShow(limitImagesCount > 0, limitImagesCount);
            } else {
                // 旧数据有图片
                int imgCount = Math.max(limitImagesCount, diary.getContentImageList().size());
                setRecyclerShow(imgCount > 0, imgCount);
            }
        } else {
            // 添加
            setRecyclerShow(limitImagesCount > 0, limitImagesCount);
        }
        // content
        etContent.setText(diary.getContentText());
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
    }

    @Override
    protected void onFinish(Bundle state) {
        RetrofitHelper.cancel(callAdd);
        RetrofitHelper.cancel(callUpdate);
        RecyclerHelper.release(recyclerHelper);
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

    @OnTextChanged({R.id.etContent})
    public void afterTextChanged(Editable s) {
        onContentInput(s.toString());
    }

    @OnClick({R.id.cvHappenAt, R.id.btnDraft, R.id.btnPublish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cvHappenAt: // 日期
                showDatePicker();
                break;
            case R.id.btnDraft: // 保存草稿
                saveDraft();
                break;
            case R.id.btnPublish: // 发表
                checkPush();
                break;
        }
    }

    private boolean isFromUpdate() {
        return getIntent().getIntExtra("from", ConsHelper.ACT_EDIT_FROM_ADD) == ConsHelper.ACT_EDIT_FROM_UPDATE;
    }

    private void setRecyclerShow(boolean show, final int childCount) {
        if (diary == null) return;
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
        if (diary.getContentImageList() != null && diary.getContentImageList().size() > 0) {
            imgAdapter.setOssData(diary.getContentImageList());
        }
        if (recyclerHelper == null) {
            recyclerHelper = new RecyclerHelper(rv)
                    .initLayoutManager(new GridLayoutManager(mActivity, spanCount))
                    .initAdapter(imgAdapter)
                    .setAdapter();
        }
    }

    private void showDatePicker() {
        if (diary == null) return;
        DialogHelper.showDateTimePicker(mActivity, TimeHelper.getJavaTimeByGo(diary.getHappenAt()), new DialogHelper.OnPickListener() {
            @Override
            public void onPick(long time) {
                diary.setHappenAt(TimeHelper.getGoTimeByJava(time));
                refreshDateView();
            }
        });
    }

    private void refreshDateView() {
        if (diary == null) return;
        String happen = TimeHelper.getTimeShowLocal_HM_MD_YMD_ByGo(diary.getHappenAt());
        tvHappenAt.setText(happen);
    }

    private void onContentInput(String input) {
        if (diary == null) return;
        if (limitContentLength <= 0) {
            limitContentLength = SPHelper.getLimit().getDiaryContentLength();
        }
        int length = input.length();
        if (length > limitContentLength) {
            CharSequence charSequence = input.subSequence(0, limitContentLength);
            etContent.setText(charSequence);
            etContent.setSelection(charSequence.length());
            length = charSequence.length();
        }
        String limitShow = String.format(Locale.getDefault(), getString(R.string.holder_sprit_holder), length, limitContentLength);
        tvContentLimit.setText(limitShow);
        // 设置进去
        diary.setContentText(etContent.getText().toString());
    }

    private void saveDraft() {
        SPHelper.setDraftDiary(diary);
        ToastUtils.show(getString(R.string.draft_save_success));
    }

    private void checkPush() {
        if (diary == null) return;
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
        if (diary == null) return;
        OssHelper.uploadDiary(mActivity, fileData, new OssHelper.OssUploadsCallBack() {
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
        if (diary == null) return;
        diary.setContentImageList(ossPathList);
        if (isFromUpdate()) {
            updateApi();
        } else {
            addApi();
        }
    }

    private void updateApi() {
        if (diary == null) return;
        MaterialDialog loading = getLoading(false);
        callUpdate = new RetrofitHelper().call(API.class).noteDiaryUpdate(diary);
        RetrofitHelper.enqueue(callUpdate, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                Diary diary = data.getDiary();
                RxBus.Event<Diary> eventList = new RxBus.Event<>(ConsHelper.EVENT_DIARY_LIST_ITEM_REFRESH, diary);
                RxBus.post(eventList);
                RxBus.Event<Diary> eventSingle = new RxBus.Event<>(ConsHelper.EVENT_DIARY_DETAIL_REFRESH, diary);
                RxBus.post(eventSingle);
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
        if (diary == null) return;
        MaterialDialog loading = getLoading(false);
        callAdd = new RetrofitHelper().call(API.class).noteDiaryAdd(diary);
        RetrofitHelper.enqueue(callAdd, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxBus.Event<ArrayList<Diary>> event = new RxBus.Event<>(ConsHelper.EVENT_DIARY_LIST_REFRESH, new ArrayList<Diary>());
                RxBus.post(event);
                // sp
                SPHelper.setDraftDiary(null);
                // finish
                mActivity.finish();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

}
