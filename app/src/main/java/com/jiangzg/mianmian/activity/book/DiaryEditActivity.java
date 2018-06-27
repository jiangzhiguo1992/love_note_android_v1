package com.jiangzg.mianmian.activity.book;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.common.FileUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.component.IntentResult;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.base.view.PopUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.settings.HelpActivity;
import com.jiangzg.mianmian.adapter.ImgSquareEditAdapter;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Diary;
import com.jiangzg.mianmian.domain.Help;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.domain.RxEvent;
import com.jiangzg.mianmian.helper.API;
import com.jiangzg.mianmian.helper.ConsHelper;
import com.jiangzg.mianmian.helper.OssHelper;
import com.jiangzg.mianmian.helper.RecyclerHelper;
import com.jiangzg.mianmian.helper.ResHelper;
import com.jiangzg.mianmian.helper.RetrofitHelper;
import com.jiangzg.mianmian.helper.RxBus;
import com.jiangzg.mianmian.helper.SPHelper;
import com.jiangzg.mianmian.helper.TimeHelper;
import com.jiangzg.mianmian.helper.ViewHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import retrofit2.Call;

public class DiaryEditActivity extends BaseActivity<DiaryEditActivity> {

    private static final int TYPE_ADD = 0;
    private static final int TYPE_UPDATE = 1;

    @BindView(R.id.root)
    LinearLayout root;
    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.cvDate)
    CardView cvDate;
    @BindView(R.id.tvDate)
    TextView tvDate;
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
    private File cameraFile;
    private List<File> cameraFileList;
    private int limitContentLength;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, DiaryEditActivity.class);
        intent.putExtra("type", TYPE_ADD);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from, Diary diary) {
        Intent intent = new Intent(from, DiaryEditActivity.class);
        intent.putExtra("type", TYPE_UPDATE);
        intent.putExtra("diary", diary);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_diary_edit;
    }

    @Override
    protected void initView(Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.diary), true);
    }

    @Override
    protected void initData(Bundle state) {
        if (isTypeUpdate()) {
            diary = getIntent().getParcelableExtra("diary");
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
        if (limitImagesCount > 0) {
            rv.setVisibility(View.VISIBLE);
            int spanCount = limitImagesCount > 3 ? 3 : limitImagesCount;
            ImgSquareEditAdapter imgAdapter = new ImgSquareEditAdapter(mActivity, spanCount, limitImagesCount);
            imgAdapter.setOnAddClick(new ImgSquareEditAdapter.OnAddClickListener() {
                @Override
                public void onAdd() {
                    showImgSelect();
                }
            });
            if (diary.getContentImageList() != null && diary.getContentImageList().size() > 0) {
                imgAdapter.setOssData(diary.getContentImageList());
            }
            recyclerHelper = new RecyclerHelper(mActivity)
                    .initRecycler(rv)
                    .initLayoutManager(new GridLayoutManager(mActivity, spanCount))
                    .initAdapter(imgAdapter)
                    .setAdapter();
        } else {
            rv.setVisibility(View.GONE);
        }
        // input
        etContent.setText(diary.getContentText());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RetrofitHelper.cancel(callAdd);
        RetrofitHelper.cancel(callUpdate);
        // 创建成功的cameraFile都要删除
        ResHelper.deleteFileListInBackground(cameraFileList);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            ResHelper.deleteFileInBackground(cameraFile);
            return;
        }
        if (recyclerHelper == null) return;
        ImgSquareEditAdapter adapter = recyclerHelper.getAdapter();
        if (adapter == null) return;
        if (requestCode == ConsHelper.REQUEST_CAMERA) {
            // 拍照
            if (FileUtils.isFileEmpty(cameraFile)) {
                ToastUtils.show(getString(R.string.file_no_exits));
                ResHelper.deleteFileInBackground(cameraFile);
                return;
            }
            adapter.addFileData(cameraFile.getAbsolutePath());
            if (cameraFileList == null) {
                cameraFileList = new ArrayList<>();
            }
            cameraFileList.add(FileUtils.getFileByPath(cameraFile.getAbsolutePath())); // 创建成功的cameraFile都要记录
            cameraFile = null; // 解除引用，防止误删
        } else if (requestCode == ConsHelper.REQUEST_PICTURE) {
            // 相册
            File pictureFile = IntentResult.getPictureFile(data);
            if (pictureFile == null || FileUtils.isFileEmpty(pictureFile)) {
                ToastUtils.show(getString(R.string.file_no_exits));
                return;
            }
            adapter.addFileData(pictureFile.getAbsolutePath());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuHelp: // 帮助
                HelpActivity.goActivity(mActivity, Help.INDEX_DIARY_EDIT);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnTextChanged({R.id.etContent})
    public void afterTextChanged(Editable s) {
        onContentInput(s.toString());
    }

    @OnClick({R.id.cvDate, R.id.btnDraft, R.id.btnPublish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cvDate: // 日期
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

    private boolean isTypeUpdate() {
        return getIntent().getIntExtra("type", TYPE_ADD) == TYPE_UPDATE;
    }

    private void showDatePicker() {
        Calendar calendar = DateUtils.getCalendar(TimeHelper.getJavaTimeByGo(diary.getHappenAt()));
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog picker = new DatePickerDialog(mActivity, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar instance = DateUtils.getCurrentCalendar();
                instance.set(year, month, dayOfMonth);
                diary.setHappenAt(TimeHelper.getGoTimeByJava(instance.getTimeInMillis()));
                refreshDateView();
            }
        }, year, month, day);
        picker.show();
    }

    private void refreshDateView() {
        String happen = TimeHelper.getTimeShowCnSpace_HM_MD_YMD_ByGo(diary.getHappenAt());
        tvDate.setText(happen);
    }

    private void showImgSelect() {
        if (SPHelper.getVipLimit().getDiaryImageCount() > 0) {
            cameraFile = ResHelper.newImageCacheFile();
            PopupWindow popupWindow = ViewHelper.createPictureCameraPop(mActivity, cameraFile);
            PopUtils.show(popupWindow, root, Gravity.CENTER);
        } else {
            ToastUtils.show(getString(R.string.now_status_cant_upload_img));
        }
    }

    private void onContentInput(String input) {
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
        String content = etContent.getText().toString();
        if (StringUtils.isEmpty(content)) {
            ToastUtils.show(etContent.getHint().toString());
            return;
        }
        List<String> fileData = null;
        List<String> ossPaths = null;
        if (recyclerHelper != null && recyclerHelper.getAdapter() != null) {
            ImgSquareEditAdapter adapter = recyclerHelper.getAdapter();
            fileData = adapter.getFileData();
            ossPaths = adapter.getOssData();
        }
        if (fileData != null && fileData.size() > 0) {
            ossUploadDiary(fileData);
        } else {
            api(ossPaths);
        }
    }

    private void ossUploadDiary(List<String> fileData) {
        OssHelper.uploadDiary(mActivity, fileData, new OssHelper.OssUploadsCallBack() {
            @Override
            public void success(List<File> sourceList, List<String> ossPathList) {
                if (recyclerHelper == null) return;
                ImgSquareEditAdapter adapter = recyclerHelper.getAdapter();
                if (adapter == null) return;
                List<String> ossData = adapter.getOssData();
                ossData.addAll(ossPathList);
                api(ossData);
            }

            @Override
            public void failure(List<File> sourceList, String errMsg) {
            }
        });
    }

    private void api(List<String> ossPathList) {
        diary.setContentImageList(ossPathList);
        if (isTypeUpdate()) {
            updateApi(diary);
        } else {
            addApi(diary);
        }
    }

    private void updateApi(Diary body) {
        MaterialDialog loading = getLoading(false);
        callUpdate = new RetrofitHelper().call(API.class).diaryUpdate(body);
        RetrofitHelper.enqueue(callUpdate, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                Diary diary = data.getDiary();
                RxEvent<Diary> eventList = new RxEvent<>(ConsHelper.EVENT_DIARY_LIST_ITEM_REFRESH, diary);
                RxBus.post(eventList);
                RxEvent<Diary> eventSingle = new RxEvent<>(ConsHelper.EVENT_DIARY_DETAIL_REFRESH, diary);
                RxBus.post(eventSingle);
                // finish
                mActivity.finish();
            }

            @Override
            public void onFailure(String errMsg) {
                // 上传失败不要删除，还可以继续上传
            }
        });
    }

    private void addApi(Diary body) {
        MaterialDialog loading = getLoading(false);
        callAdd = new RetrofitHelper().call(API.class).diaryAdd(body);
        RetrofitHelper.enqueue(callAdd, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxEvent<ArrayList<Diary>> event = new RxEvent<>(ConsHelper.EVENT_DIARY_LIST_REFRESH, new ArrayList<Diary>());
                RxBus.post(event);
                // finish
                mActivity.finish();
            }

            @Override
            public void onFailure(String errMsg) {
            }
        });
    }

}
