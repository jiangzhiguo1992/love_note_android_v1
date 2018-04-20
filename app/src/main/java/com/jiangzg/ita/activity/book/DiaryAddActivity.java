package com.jiangzg.ita.activity.book;

import android.app.Activity;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.component.IntentResult;
import com.jiangzg.ita.R;
import com.jiangzg.ita.activity.common.HelpActivity;
import com.jiangzg.ita.adapter.ImgSquareAddAdapter;
import com.jiangzg.ita.base.BaseActivity;
import com.jiangzg.ita.domain.Help;
import com.jiangzg.ita.helper.ConsHelper;
import com.jiangzg.ita.helper.RecyclerHelper;
import com.jiangzg.ita.helper.ResHelper;
import com.jiangzg.ita.helper.SPHelper;
import com.jiangzg.ita.helper.ViewHelper;

import java.io.File;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DiaryAddActivity extends BaseActivity<DiaryAddActivity> {

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

    private int limitContent;
    private ImgSquareAddAdapter addAdapter;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, DiaryAddActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_diary_add;
    }

    @Override
    protected void initView(Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.small_book), true);
        // recycler
        int limitImages = SPHelper.getLimit().getDiaryLimitImages();
        rv.setLayoutManager(new GridLayoutManager(mActivity, limitImages));
        addAdapter = new ImgSquareAddAdapter(mActivity, root, limitImages, limitImages);
        rv.setAdapter(addAdapter);
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
        // menu
        tb.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menuHelp: // 帮助
                        HelpActivity.goActivity(mActivity, Help.TYPE_SUGGEST_ADD);
                        break;
                }
                return true;
            }
        });
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
        File cameraFile = addAdapter.getCameraFile();
        if (resultCode != RESULT_OK) {
            ResHelper.deleteFileInBackground(cameraFile);
            //pictureFile = null;
            return;
        }
        if (requestCode == ConsHelper.REQUEST_CAMERA) {
            // 拍照
            if (cameraFile == null) return;
            addAdapter.addFileData(cameraFile.getAbsolutePath());
            // 解除相册文件引用
            //pictureFile = null;
        } else if (requestCode == ConsHelper.REQUEST_PICTURE) {
            // 相册
            File pictureFile = IntentResult.getPictureFile(data);
            if (pictureFile == null) return;
            addAdapter.addFileData(pictureFile.getAbsolutePath());
            // 删除拍照文件
            ResHelper.deleteFileInBackground(cameraFile);
        }
    }

    @OnClick({R.id.tvDate, R.id.btnPublish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvDate: // 日期
                // TODO
                break;
            case R.id.btnPublish: // 发表
                // TODO
                break;
        }
    }

    private void onContentInput(String input) {
        if (limitContent <= 0) {
            limitContent = SPHelper.getLimit().getSuggestLimitContentText();
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

}
