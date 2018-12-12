package com.jiangzg.lovenote.activity.topic;

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
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.DialogUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.activity.couple.CouplePairActivity;
import com.jiangzg.lovenote.adapter.ImgSquareEditAdapter;
import com.jiangzg.lovenote.base.BaseActivity;
import com.jiangzg.lovenote.helper.ConsHelper;
import com.jiangzg.lovenote.helper.DialogHelper;
import com.jiangzg.lovenote.helper.ListHelper;
import com.jiangzg.lovenote.helper.MediaPickHelper;
import com.jiangzg.lovenote.helper.OssHelper;
import com.jiangzg.lovenote.helper.RecyclerHelper;
import com.jiangzg.lovenote.helper.RetrofitHelper;
import com.jiangzg.lovenote.helper.SPHelper;
import com.jiangzg.lovenote.helper.UserHelper;
import com.jiangzg.lovenote.helper.ViewHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.Post;
import com.jiangzg.lovenote.model.entity.PostKindInfo;
import com.jiangzg.lovenote.model.entity.PostSubKindInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import retrofit2.Call;

public class PostAddActivity extends BaseActivity<PostAddActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.cvKind)
    CardView cvKind;
    @BindView(R.id.tvKind)
    TextView tvKind;
    @BindView(R.id.cvSubKind)
    CardView cvSubKind;
    @BindView(R.id.tvSubKind)
    TextView tvSubKind;
    @BindView(R.id.etTitle)
    EditText etTitle;
    @BindView(R.id.etContent)
    EditText etContent;
    @BindView(R.id.tvContentLimit)
    TextView tvContentLimit;
    @BindView(R.id.rvImage)
    RecyclerView rvImage;

    private PostKindInfo kindInfo;
    private Post post;
    private RecyclerHelper recyclerHelper;
    private Call<Result> callAdd;
    private int limitContentLength;

    public static void goActivity(Activity from, PostKindInfo kindInfo, int subKind) {
        if (UserHelper.isCoupleBreak(SPHelper.getCouple())) {
            CouplePairActivity.goActivity(from);
            return;
        }
        Intent intent = new Intent(from, PostAddActivity.class);
        intent.putExtra("kindInfo", kindInfo);
        intent.putExtra("subKind", subKind);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_post_add;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.post), true);
        // init
        post = SPHelper.getDraftPost();
        if (post == null) {
            post = new Post();
        }
        // kindInfo
        kindInfo = intent.getParcelableExtra("kindInfo");
        refreshPostKind(intent.getIntExtra("subKind", 0));
        // etTitle
        String format = getString(R.string.please_input_title_no_over_holder_text);
        String hint = String.format(Locale.getDefault(), format, SPHelper.getLimit().getPostTitleLength());
        etTitle.setHint(hint);
        etTitle.setText(post.getTitle());
        // content
        etContent.setText(post.getContentText());
        // recycler
        int limitImagesCount = SPHelper.getVipLimit().getTopicPostImageCount();
        if (limitImagesCount <= 0) {
            rvImage.setVisibility(View.GONE);
        } else {
            rvImage.setVisibility(View.VISIBLE);
            int spanCount = limitImagesCount > 3 ? 3 : limitImagesCount;
            ImgSquareEditAdapter imgAdapter = new ImgSquareEditAdapter(mActivity, spanCount, limitImagesCount);
            imgAdapter.setOnAddClick(() -> {
                int maxCount = limitImagesCount - imgAdapter.getOssData().size() - imgAdapter.getFileData().size();
                MediaPickHelper.selectImage(mActivity, maxCount);
            });
            if (recyclerHelper == null) {
                recyclerHelper = new RecyclerHelper(rvImage)
                        .initLayoutManager(new GridLayoutManager(mActivity, spanCount))
                        .initAdapter(imgAdapter)
                        .setAdapter();
            }
        }
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
    }

    @Override
    protected void onFinish(Bundle state) {
        RetrofitHelper.cancel(callAdd);
        RecyclerHelper.release(recyclerHelper);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.draft_commit, menu);
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
            case R.id.menuDraft: // 草稿
                saveDraft();
                return true;
            case R.id.menuCommit: // 提交
                checkPush();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnTextChanged({R.id.etContent})
    public void afterTextChanged(Editable s) {
        onContentInput(s.toString());
    }

    @OnClick({R.id.cvSubKind})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cvSubKind: // 分类
                showSelectSubKindDialog();
                break;
        }
    }

    private void refreshPostKind(int subKind) {
        // kind
        if (kindInfo == null) {
            mActivity.finish();
            return;
        }
        // subKind
        if (!ListHelper.isPostSubKindInfoPush(kindInfo, subKind)) {
            List<PostSubKindInfo> subKindPushList = ListHelper.getPostSubKindInfoListPush(kindInfo);
            PostSubKindInfo subKindInfo = null;
            for (PostSubKindInfo subInfo : subKindPushList) {
                if (subInfo != null) {
                    subKindInfo = subInfo;
                    break;
                }
            }
            if (subKindInfo == null) {
                mActivity.finish();
                return;
            }
            subKind = subKindInfo.getKind();
        }
        post.setKind(kindInfo.getKind());
        post.setSubKind(subKind);
        // view
        PostSubKindInfo subKindInfo = ListHelper.getPostSubKindInfo(kindInfo, post.getSubKind());
        String kindShow = (kindInfo == null || StringUtils.isEmpty(kindInfo.getName())) ? getString(R.string.please_select_classify) : kindInfo.getName();
        String subKindShow = (subKindInfo == null || StringUtils.isEmpty(subKindInfo.getName())) ? getString(R.string.please_select_classify) : subKindInfo.getName();
        tvKind.setText(kindShow);
        tvSubKind.setText(subKindShow);
    }

    private void showSelectSubKindDialog() {
        if (post == null) return;
        List<String> subKindPushShowList = ListHelper.getPostSubKindInfoListPushShow(kindInfo);
        int selectIndex = ListHelper.getIndexInPostSubKindInfoListPush(kindInfo, post.getSubKind());
        if (selectIndex < 0) return;
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .title(R.string.please_select_classify)
                .items(subKindPushShowList)
                .itemsCallbackSingleChoice(selectIndex, (dialog1, view, which, text) -> {
                    if (post == null) return true;
                    List<PostSubKindInfo> subKindPushList = ListHelper.getPostSubKindInfoListPush(kindInfo);
                    PostSubKindInfo subKindInfo = subKindPushList.get(which);
                    if (subKindInfo != null) {
                        refreshPostKind(subKindInfo.getKind());
                    }
                    DialogUtils.dismiss(dialog1);
                    return true;
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void onContentInput(String input) {
        if (post == null) return;
        if (limitContentLength <= 0) {
            limitContentLength = SPHelper.getLimit().getPostContentLength();
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
        post.setContentText(etContent.getText().toString());
    }

    private void saveDraft() {
        if (post == null) return;
        post.setTitle(etTitle.getText().toString());
        SPHelper.setDraftPost(post);
        ToastUtils.show(getString(R.string.draft_save_success));
    }

    private void checkPush() {
        if (post == null) return;
        String title = etTitle.getText().toString();
        if (StringUtils.isEmpty(title)) {
            ToastUtils.show(etTitle.getHint().toString());
            return;
        } else if (title.length() > SPHelper.getLimit().getPostTitleLength()) {
            ToastUtils.show(etTitle.getHint().toString());
            return;
        } else if (StringUtils.isEmpty(post.getContentText())) {
            ToastUtils.show(etContent.getHint().toString());
            return;
        }
        post.setTitle(title);
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
            addApi(ossPaths);
        }
    }

    private void ossUploadImages(List<String> fileData) {
        if (post == null) return;
        OssHelper.uploadTopicPost(mActivity, fileData, new OssHelper.OssUploadsCallBack() {
            @Override
            public void success(List<File> sourceList, List<String> ossPathList) {
                if (recyclerHelper == null) return;
                ImgSquareEditAdapter adapter = recyclerHelper.getAdapter();
                if (adapter == null) return;
                List<String> ossData = adapter.getOssData();
                ossData.addAll(ossPathList == null ? new ArrayList<>() : ossPathList);
                addApi(ossData);
            }

            @Override
            public void failure(List<File> sourceList, String errMsg) {
            }
        });
    }

    private void addApi(List<String> ossPathList) {
        if (post == null) return;
        post.setContentImageList(ossPathList);
        MaterialDialog loading = getLoading(false);
        callAdd = new RetrofitHelper().call(API.class).topicPostAdd(post);
        RetrofitHelper.enqueue(callAdd, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event 先不刷新
                //Event<Integer> event = new Event<>(ConsHelper.EVENT_POST_LIST_REFRESH, post.getSubKind());
                //RxBus.post(event);
                // sp
                SPHelper.setDraftPost(null);
                // finish
                mActivity.finish();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

}
