package com.jiangzg.mianmian.activity.book;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
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
import com.jiangzg.mianmian.activity.common.HelpActivity;
import com.jiangzg.mianmian.adapter.ImgSquareEditAdapter;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Diary;
import com.jiangzg.mianmian.domain.Help;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.domain.RxEvent;
import com.jiangzg.mianmian.helper.API;
import com.jiangzg.mianmian.helper.ApiHelper;
import com.jiangzg.mianmian.helper.ConsHelper;
import com.jiangzg.mianmian.helper.ConvertHelper;
import com.jiangzg.mianmian.helper.OssHelper;
import com.jiangzg.mianmian.helper.PopHelper;
import com.jiangzg.mianmian.helper.RecyclerHelper;
import com.jiangzg.mianmian.helper.ResHelper;
import com.jiangzg.mianmian.helper.RetrofitHelper;
import com.jiangzg.mianmian.helper.RxBus;
import com.jiangzg.mianmian.helper.SPHelper;
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

    @BindView(R.id.root)
    LinearLayout root;
    @BindView(R.id.tb)
    Toolbar tb;
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

    private long happenAt;
    private RecyclerHelper recyclerHelper;
    private File cameraFile;
    private List<File> cameraFileList;
    private int limitContent;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, DiaryEditActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from, Diary diary) {
        Intent intent = new Intent(from, DiaryEditActivity.class);
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
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.small_book), true);
        // 外部和草稿，都检查一下
        Diary diary = getIntentDiary();
        if (diary == null) {
            diary = SPHelper.getDiary();
        }
        // date
        Calendar calendar = DateUtils.getCurrentCalendar();
        if (diary != null && diary.getHappenAt() != 0) {
            long happen = ConvertHelper.getJavaTimeByGo(diary.getHappenAt());
            calendar.setTimeInMillis(happen);
        }
        refreshDateView(calendar);
        // recycler
        int limitImages = SPHelper.getVipLimit().getBookDiaryImageCount();
        if (limitImages > 0) {
            rv.setVisibility(View.VISIBLE);
            int spanCount = limitImages > 3 ? 3 : limitImages;
            ImgSquareEditAdapter imgAdapter = new ImgSquareEditAdapter(mActivity, spanCount, limitImages);
            imgAdapter.setOnAddClick(new ImgSquareEditAdapter.OnAddClickListener() {
                @Override
                public void onAdd() {
                    showImgSelect();
                }
            });
            if (diary != null && diary.getImageList() != null && diary.getImageList().size() > 0) {
                imgAdapter.setOssData(diary.getImageList());
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
        String content = (diary != null) ? diary.getContent() : "";
        etContent.setText(content);
    }

    @Override
    protected void initData(Bundle state) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help, menu);
        return super.onCreateOptionsMenu(menu);
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
    protected void onDestroy() {
        super.onDestroy();
        // 创建成功的cameraFile都要删除
        ResHelper.deleteFileListInBackground(cameraFileList);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuHelp: // 帮助
                HelpActivity.goActivity(mActivity, Help.TYPE_DIARY_EDIT);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnTextChanged({R.id.etContent})
    public void afterTextChanged(Editable s) {
        onContentInput(s.toString());
    }

    @OnClick({R.id.tvDate, R.id.btnDraft, R.id.btnPublish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvDate: // 日期
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

    private Diary getIntentDiary() {
        return mActivity.getIntent().getParcelableExtra("diary");
    }

    private void showDatePicker() {
        Calendar calendar = DateUtils.getCalendar(happenAt);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog picker = new DatePickerDialog(mActivity, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar instance = DateUtils.getCurrentCalendar();
                instance.set(year, month, dayOfMonth);
                refreshDateView(instance);
            }
        }, year, month, day);
        picker.show();
    }

    private void refreshDateView(Calendar calendar) {
        happenAt = calendar.getTimeInMillis();
        String happen = ConvertHelper.getTimeShowCnSpace_HM_MD_YMD_ByJava(happenAt);
        tvDate.setText(happen);
    }

    private void showImgSelect() {
        if (SPHelper.getVipLimit().getBookDiaryImageCount() > 0) {
            cameraFile = ResHelper.newImageOutCache();
            PopupWindow popupWindow = PopHelper.createBookPictureCamera(mActivity, cameraFile);
            PopUtils.show(popupWindow, root, Gravity.CENTER);
        } else {
            ToastUtils.show(getString(R.string.now_status_cant_upload_img));
        }
    }

    private void onContentInput(String input) {
        if (limitContent <= 0) {
            limitContent = SPHelper.getLimit().getDiaryLimitContent();
        }
        int length = input.length();
        if (length > limitContent) {
            CharSequence charSequence = input.subSequence(0, limitContent);
            etContent.setText(charSequence);
            etContent.setSelection(charSequence.length());
            length = charSequence.length();
        }
        String limitShow = String.format(Locale.getDefault(), getString(R.string.holder_sprit_holder), length, limitContent);
        tvContentLimit.setText(limitShow);
    }

    private void saveDraft() {
        Diary diary = new Diary();
        diary.setHappenAt(ConvertHelper.getGoTimeByJava(happenAt));
        diary.setContent(etContent.getText().toString());
        SPHelper.setDiary(diary);
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
        long happenGo = ConvertHelper.getGoTimeByJava(happenAt);
        String content = etContent.getText().toString();
        if (getIntentDiary() != null) {
            Diary body = getIntentDiary();
            body.setHappenAt(happenGo);
            body.setContent(content);
            body.setImageList(ossPathList);
            updateApi(body);
        } else {
            Diary body = ApiHelper.getDiaryBody(happenGo, content, ossPathList);
            addApi(body);
        }
    }

    private void updateApi(Diary body) {
        MaterialDialog loading = getLoading(false);
        Call<Result> call = new RetrofitHelper().call(API.class).diaryUpdate(body);
        RetrofitHelper.enqueue(call, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                Diary diary = data.getDiary();
                RxEvent<Diary> eventList = new RxEvent<>(ConsHelper.EVENT_DIARY_LIST_ITEM_REFRESH, diary);
                RxBus.post(eventList);
                RxEvent<Diary> eventSingle = new RxEvent<>(ConsHelper.EVENT_DIARY_REFRESH, diary);
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
        Call<Result> call = new RetrofitHelper().call(API.class).diaryPost(body);
        RetrofitHelper.enqueue(call, loading, new RetrofitHelper.CallBack() {
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
