package com.jiangzg.lovenote.controller.activity.topic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.DialogUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.controller.activity.couple.CouplePairActivity;
import com.jiangzg.lovenote.controller.adapter.common.ImgSquareEditAdapter;
import com.jiangzg.lovenote.helper.common.ListHelper;
import com.jiangzg.lovenote.helper.common.OssHelper;
import com.jiangzg.lovenote.helper.common.RxBus;
import com.jiangzg.lovenote.helper.common.SPHelper;
import com.jiangzg.lovenote.helper.common.UserHelper;
import com.jiangzg.lovenote.helper.media.PickHelper;
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.helper.view.DialogHelper;
import com.jiangzg.lovenote.helper.view.RecyclerHelper;
import com.jiangzg.lovenote.helper.view.ViewHelper;
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

    @BindView(R.id.etTitle)
    EditText etTitle;
    @BindView(R.id.tvContentLimit)
    TextView tvContentLimit;
    @BindView(R.id.etContent)
    EditText etContent;
    @BindView(R.id.rvImage)
    RecyclerView rvImage;
    @BindView(R.id.llKind)
    LinearLayout llKind;
    @BindView(R.id.tvKind)
    TextView tvKind;

    private Post post;
    private RecyclerHelper recyclerHelper;
    private int limitContentLength;

    public static void goActivity(Fragment from) {
        if (UserHelper.isCoupleBreak(SPHelper.getCouple())) {
            CouplePairActivity.goActivity(from);
            return;
        }
        Intent intent = new Intent(from.getActivity(), PostAddActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from) {
        if (UserHelper.isCoupleBreak(SPHelper.getCouple())) {
            CouplePairActivity.goActivity(from);
            return;
        }
        Intent intent = new Intent(from, PostAddActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from, int kind, int subKind) {
        if (UserHelper.isCoupleBreak(SPHelper.getCouple())) {
            CouplePairActivity.goActivity(from);
            return;
        }
        Intent intent = new Intent(from, PostAddActivity.class);
        intent.putExtra("kind", kind);
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
            int spanCount = 3;
            ImgSquareEditAdapter imgAdapter = new ImgSquareEditAdapter(mActivity, spanCount, limitImagesCount);
            imgAdapter.setOnAddClick(() -> {
                int maxCount = limitImagesCount - imgAdapter.getOssData().size() - imgAdapter.getFileData().size();
                PickHelper.selectImage(mActivity, maxCount, true);
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
        // kind
        int kind = intent.getIntExtra("kind", 0);
        int subKind = intent.getIntExtra("subKind", 0);
        refreshPostKind(kind, subKind);
    }

    @Override
    protected void onFinish(Bundle state) {
        RecyclerHelper.release(recyclerHelper);
    }

    @Override
    public void onBackPressed() {
        // 没有数据
        if (post == null || StringUtils.isEmpty(post.getContentText())) {
            super.onBackPressed();
            return;
        }
        // 相同数据
        Post draft = SPHelper.getDraftPost();
        if (draft != null && draft.getContentText().equals(post.getContentText())) {
            super.onBackPressed();
            return;
        }
        // 草稿询问
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .content(R.string.is_save_draft)
                .positiveText(R.string.save_draft)
                .negativeText(R.string.cancel)
                .onPositive((dialog1, which) -> saveDraft(true))
                .onNegative((dialog12, which) -> super.onBackPressed())
                .build();
        DialogHelper.showWithAnim(dialog);
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
            case R.id.menuDraft: // 草稿
                saveDraft(false);
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

    @OnClick({R.id.llKind})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.llKind: // 分类
                showSelectKindDialog();
                break;
        }
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

    private void showSelectKindDialog() {
        if (post == null) return;
        List<String> nameList = ListHelper.getPostKindInfoListEnableShow();
        int selectIndex = ListHelper.getIndexInPostKindInfoListEnable(post.getKind());
        if (selectIndex < 0) {
            selectIndex = 0;
        }
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .title(R.string.please_select_classify)
                .items(nameList)
                .itemsCallbackSingleChoice(selectIndex, (dialog1, view, which, text) -> {
                    List<PostKindInfo> kindList = ListHelper.getPostKindInfoListEnable();
                    if (which < 0 || which >= kindList.size()) {
                        return true;
                    }
                    PostKindInfo postKindInfo = kindList.get(which);
                    if (postKindInfo == null) {
                        return true;
                    }
                    showSelectSubKindDialog(postKindInfo);
                    DialogUtils.dismiss(dialog1);
                    return true;
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void showSelectSubKindDialog(PostKindInfo kindInfo) {
        if (post == null || kindInfo == null) return;
        List<String> subKindPushShowList = ListHelper.getPostSubKindInfoListPushShow(kindInfo);
        int selectIndex = ListHelper.getIndexInPostSubKindInfoListPush(kindInfo, post.getSubKind());
        if (selectIndex < 0) {
            selectIndex = 0;
        }
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .title(R.string.please_select_classify)
                .items(subKindPushShowList)
                .itemsCallbackSingleChoice(selectIndex, (dialog1, view, which, text) -> {
                    List<PostSubKindInfo> subKindPushList = ListHelper.getPostSubKindInfoListPush(kindInfo);
                    if (which < 0 || which >= subKindPushList.size()) {
                        return true;
                    }
                    PostSubKindInfo subKindInfo = subKindPushList.get(which);
                    if (subKindInfo == null) {
                        return true;
                    }
                    refreshPostKind(kindInfo.getKind(), subKindInfo.getKind());
                    DialogUtils.dismiss(dialog1);
                    return true;
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void refreshPostKind(int kind, int subKind) {
        PostKindInfo kindInfo = ListHelper.getPostKindInfo(kind);
        PostSubKindInfo subKindInfo = ListHelper.getPostSubKindInfo(kindInfo, subKind);
        // kind
        if (kindInfo == null) {
            List<PostKindInfo> kindList = ListHelper.getPostKindInfoListEnable();
            if (kindList != null && kindList.size() > 0) {
                for (PostKindInfo kf : kindList) {
                    if (kf != null) {
                        // 默认第一个
                        kindInfo = kf;
                        break;
                    }
                }
            }
            if (kindInfo == null) {
                mActivity.finish();
                // 没有就退出
                return;
            }
            kind = kindInfo.getKind();
        }
        // subKind
        if (subKindInfo == null || !subKindInfo.isPush()) {
            List<PostSubKindInfo> subKindPushList = ListHelper.getPostSubKindInfoListPush(kindInfo);
            if (subKindPushList != null && subKindPushList.size() > 0) {
                for (PostSubKindInfo subInfo : subKindPushList) {
                    if (subInfo != null) {
                        // 默认第一个
                        subKindInfo = subInfo;
                        break;
                    }
                }
            }
            if (subKindInfo == null) {
                // 没有就退出
                mActivity.finish();
                return;
            }
            subKind = subKindInfo.getKind();
        }
        // data
        post.setKind(kind);
        post.setSubKind(subKind);
        // view
        String kindShow = String.format(Locale.getDefault(), getString(R.string.holder_space_line_space_holder), kindInfo.getName(), subKindInfo.getName());
        tvKind.setText(String.format(Locale.getDefault(), getString(R.string.type_colon_space_holder), kindShow));
    }

    private void saveDraft(boolean exit) {
        if (post == null) return;
        post.setTitle(etTitle.getText().toString());
        SPHelper.setDraftPost(post);
        ToastUtils.show(getString(R.string.draft_save_success));
        if (exit) mActivity.finish();
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
            public void success(List<File> sourceList, List<String> ossKeyList, List<String> successList) {
                if (recyclerHelper == null) return;
                ImgSquareEditAdapter adapter = recyclerHelper.getAdapter();
                if (adapter == null) return;
                List<String> ossData = adapter.getOssData();
                ossData.addAll(successList == null ? new ArrayList<>() : successList);
                addApi(ossData);
            }

            @Override
            public void failure(int index, List<File> sourceList, String errMsg) {
            }
        });
    }

    private void addApi(List<String> ossPathList) {
        if (post == null) return;
        post.setContentImageList(ossPathList);
        // api
        Call<Result> api = new RetrofitHelper().call(API.class).topicPostAdd(post);
        RetrofitHelper.enqueue(api, getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_POST_LIST_REFRESH, post));
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
