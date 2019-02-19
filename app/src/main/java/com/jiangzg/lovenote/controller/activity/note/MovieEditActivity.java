package com.jiangzg.lovenote.controller.activity.note;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.common.DateUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.system.LocationInfo;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.controller.activity.common.MapSelectActivity;
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
import com.jiangzg.lovenote.model.entity.Movie;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import retrofit2.Call;
import rx.Observable;

public class MovieEditActivity extends BaseActivity<MovieEditActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.etTitle)
    EditText etTitle;
    @BindView(R.id.cvHappenAt)
    CardView cvHappenAt;
    @BindView(R.id.tvHappenAt)
    TextView tvHappenAt;
    @BindView(R.id.cvAddress)
    CardView cvAddress;
    @BindView(R.id.tvAddress)
    TextView tvAddress;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.etContent)
    EditText etContent;
    @BindView(R.id.tvContentLimit)
    TextView tvContentLimit;

    private Movie movie;
    private RecyclerHelper recyclerHelper;
    private int limitContentLength;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, MovieEditActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from, Movie movie) {
        if (movie == null) {
            goActivity(from);
            return;
        } else if (!movie.isMine()) {
            ToastUtils.show(from.getString(R.string.can_operation_self_create_movie));
            return;
        }
        Intent intent = new Intent(from, MovieEditActivity.class);
        intent.putExtra("from", BaseActivity.ACT_EDIT_FROM_UPDATE);
        intent.putExtra("movie", movie);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_movie_edit;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.movie), true);
        // init
        if (isFromUpdate()) {
            movie = intent.getParcelableExtra("movie");
        }
        if (movie == null) {
            movie = new Movie();
        }
        if (movie.getHappenAt() == 0) {
            movie.setHappenAt(TimeHelper.getGoTimeByJava(DateUtils.getCurrentLong()));
        }
        // etTitle
        String format = getString(R.string.please_input_title_no_over_holder_text);
        String hint = String.format(Locale.getDefault(), format, SPHelper.getLimit().getMovieTitleLength());
        etTitle.setHint(hint);
        etTitle.setText(movie.getTitle());
        // date
        refreshDateView();
        // location
        refreshLocationView();
        // recycler
        int limitImagesCount = SPHelper.getVipLimit().getMovieImageCount();
        if (isFromUpdate()) {
            // 编辑
            if (movie.getContentImageList() == null || movie.getContentImageList().size() <= 0) {
                // 旧数据没有图片
                setRecyclerShow(limitImagesCount > 0, limitImagesCount);
            } else {
                // 旧数据有图片
                int imgCount = Math.max(limitImagesCount, movie.getContentImageList().size());
                setRecyclerShow(imgCount > 0, imgCount);
            }
        } else {
            // 添加
            setRecyclerShow(limitImagesCount > 0, limitImagesCount);
        }
        // content
        etContent.setText(movie.getContentText());
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        // event
        Observable<LocationInfo> obSelectMap = RxBus.register(RxBus.EVENT_MAP_SELECT, info -> {
            if (info == null || movie == null) return;
            movie.setLatitude(info.getLatitude());
            movie.setLongitude(info.getLongitude());
            movie.setAddress(info.getAddress());
            movie.setCityId(info.getCityId());
            refreshLocationView();
        });
        pushBus(RxBus.EVENT_MAP_SELECT, obSelectMap);
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

    @OnTextChanged({R.id.etContent})
    public void afterTextChanged(Editable s) {
        onContentInput(s.toString());
    }

    @OnClick({R.id.cvHappenAt, R.id.cvAddress})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cvHappenAt: // 日期
                showDatePicker();
                break;
            case R.id.cvAddress: // 地址
                if (movie == null) return;
                MapSelectActivity.goActivity(mActivity, movie.getAddress(), movie.getLongitude(), movie.getLatitude());
                break;
        }
    }

    private boolean isFromUpdate() {
        return getIntent().getIntExtra("from", BaseActivity.ACT_EDIT_FROM_ADD) == BaseActivity.ACT_EDIT_FROM_UPDATE;
    }

    private void setRecyclerShow(boolean show, int childCount) {
        if (movie == null) return;
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
        if (movie.getContentImageList() != null && movie.getContentImageList().size() > 0) {
            imgAdapter.setOssData(movie.getContentImageList());
        }
        if (recyclerHelper == null) {
            recyclerHelper = new RecyclerHelper(rv)
                    .initLayoutManager(new GridLayoutManager(mActivity, spanCount))
                    .initAdapter(imgAdapter)
                    .setAdapter();
        }
    }

    private void showDatePicker() {
        if (movie == null) return;
        DialogHelper.showDateTimePicker(mActivity, TimeHelper.getJavaTimeByGo(movie.getHappenAt()), time -> {
            movie.setHappenAt(TimeHelper.getGoTimeByJava(time));
            refreshDateView();
        });
    }

    private void refreshDateView() {
        if (movie == null) return;
        String happen = TimeHelper.getTimeShowLine_HM_MDHM_YMDHM_ByGo(movie.getHappenAt());
        tvHappenAt.setText(happen);
    }

    private void refreshLocationView() {
        if (movie == null) return;
        String location = StringUtils.isEmpty(movie.getAddress()) ? getString(R.string.now_no) : movie.getAddress();
        tvAddress.setText(location);
    }

    private void onContentInput(String input) {
        if (movie == null) return;
        if (limitContentLength <= 0) {
            limitContentLength = SPHelper.getLimit().getMovieContentLength();
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
        movie.setContentText(etContent.getText().toString());
    }

    private void checkPush() {
        if (movie == null) return;
        String title = etTitle.getText().toString();
        if (StringUtils.isEmpty(title)) {
            ToastUtils.show(etTitle.getHint().toString());
            return;
        } else if (title.length() > SPHelper.getLimit().getMovieTitleLength()) {
            ToastUtils.show(etTitle.getHint().toString());
            return;
        }
        movie.setTitle(title);
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
        if (movie == null) return;
        OssHelper.uploadMovie(mActivity, fileData, new OssHelper.OssUploadsCallBack() {
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
        if (movie == null) return;
        movie.setContentImageList(ossPathList);
        if (isFromUpdate()) {
            updateApi();
        } else {
            addApi();
        }
    }

    private void updateApi() {
        if (movie == null) return;
        Call<Result> api = new RetrofitHelper().call(API.class).noteMovieUpdate(movie);
        RetrofitHelper.enqueue(api, getLoading(false), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                Movie movie = data.getMovie();
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_MOVIE_LIST_ITEM_REFRESH, movie));
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
        if (movie == null) return;
        Call<Result> api = new RetrofitHelper().call(API.class).noteMovieAdd(movie);
        RetrofitHelper.enqueue(api, getLoading(false), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_MOVIE_LIST_REFRESH, new ArrayList<>()));
                // finish
                mActivity.finish();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
        pushApi(api);
    }

    public void showDeleteDialog() {
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .content(R.string.confirm_delete_this_movie)
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive((dialog1, which) -> deleteApi())
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void deleteApi() {
        if (movie == null) return;
        Call<Result> api = new RetrofitHelper().call(API.class).noteMovieDel(movie.getId());
        RetrofitHelper.enqueue(api, getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_MOVIE_LIST_ITEM_DELETE, movie));
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
