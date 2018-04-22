package com.jiangzg.mianmian.activity.book;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
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

    private long happenAt;
    private ImgSquareEditAdapter imgAdapter;
    private File cameraFile;
    private int limitContent;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, DiaryEditActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    // TODO 更新
    public static void goActivity(Activity from, Diary diary) {
        Intent intent = new Intent(from, DiaryEditActivity.class);
        // intent.putExtra();
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
        // date
        Calendar calendar = Calendar.getInstance();
        refreshDateView(calendar);
        // recycler
        int limitImages = SPHelper.getLimit().getDiaryLimitImages();
        rv.setLayoutManager(new GridLayoutManager(mActivity, limitImages));
        imgAdapter = new ImgSquareEditAdapter(mActivity, limitImages, limitImages);
        imgAdapter.setOnAddClick(new ImgSquareEditAdapter.OnAddClickListener() {
            @Override
            public void onAdd() {
                showImgSelect();
            }
        });
        rv.setAdapter(imgAdapter);
        // input
        etContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                onContentInput(s.toString());
            }
        });
        onContentInput("");
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuHelp: // 帮助
                HelpActivity.goActivity(mActivity, Help.TYPE_DIARY_EDIT);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            // 每次pop都会创建，所以这里必须删除
            ResHelper.deleteFileInBackground(cameraFile);
            return;
        }
        if (requestCode == ConsHelper.REQUEST_CAMERA) {
            // 拍照
            if (FileUtils.isFileEmpty(cameraFile)) {
                ResHelper.deleteFileInBackground(cameraFile);
                return;
            }
            imgAdapter.addFileData(cameraFile.getAbsolutePath());
            cameraFile = null; // 解除引用，防止误删
        } else if (requestCode == ConsHelper.REQUEST_PICTURE) {
            // 相册
            File pictureFile = IntentResult.getPictureFile(data);
            if (pictureFile == null || FileUtils.isFileEmpty(pictureFile)) {
                return;
            }
            ResHelper.deleteFileInBackground(cameraFile); // 每次pop都会创建，所以这里必须删除
            imgAdapter.addFileData(pictureFile.getAbsolutePath());
        }
    }

    @OnClick({R.id.tvDate, R.id.btnPublish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvDate: // 日期
                showDatePicker();
                break;
            case R.id.btnPublish: // 发表
                checkPush();
                break;
        }
    }

    private void showDatePicker() {
        Calendar calendar = DateUtils.getCalendar(happenAt);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog picker = new DatePickerDialog(mActivity, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar instance = Calendar.getInstance();
                instance.set(year, month, dayOfMonth);
                refreshDateView(instance);
            }
        }, year, month, day);
        picker.show();
    }

    private void refreshDateView(Calendar calendar) {
        happenAt = calendar.getTimeInMillis();
        String happen = ConvertHelper.ConvertTimeJava2DiaryShow(happenAt);
        tvDate.setText(happen);
    }

    private void showImgSelect() {
        if (SPHelper.getVipLimit().isBookDiaryImageEnable()) {
            cameraFile = ResHelper.createJPEGInCache();
            PopupWindow popupWindow = PopHelper.createBookPictureCamera(mActivity, cameraFile);
            PopUtils.show(popupWindow, root);
        } else {
            ToastUtils.show("非会员不能上传图片哦");
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

    private void checkPush() {
        String content = etContent.getText().toString();
        if (StringUtils.isEmpty(content)) {
            ToastUtils.show(getString(R.string.please_input_content));
            return;
        }
        List<String> fileData = imgAdapter.getFileData();
        if (fileData != null && fileData.size() > 0) {
            ossUploadDiary(fileData);
        } else {
            publish(new ArrayList<String>());
        }
    }

    private void ossUploadDiary(List<String> fileData) {
        OssHelper.uploadDiary(mActivity, fileData, new OssHelper.OssUploadsCallBack() {
            @Override
            public void success(List<String> ossPathList) {
                publish(ossPathList);
            }

            @Override
            public void failure(List<File> sourceList, String errMsg) {
            }
        });
    }

    private void publish(List<String> ossPathList) {
        long happenGo = ConvertHelper.convertTimeJava2Go(happenAt);
        String content = etContent.getText().toString();
        MaterialDialog loading = getLoading(false);
        Diary body = ApiHelper.getDiaryBody(happenGo, content, ossPathList);
        Call<Result> call = new RetrofitHelper().call(API.class).diaryPost(body);
        RetrofitHelper.enqueue(call, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                RxEvent<ArrayList<Diary>> event = new RxEvent<>(ConsHelper.EVENT_DIARY_LIST_REFRESH, new ArrayList<Diary>());
                RxBus.post(event);
                mActivity.finish();
            }

            @Override
            public void onFailure(String errMsg) {
            }
        });
    }

}
