package com.jiangzg.mianmian.activity.topic;

import android.app.Activity;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.common.FileUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.component.IntentResult;
import com.jiangzg.base.system.LocationInfo;
import com.jiangzg.base.view.DialogUtils;
import com.jiangzg.base.view.PopUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.common.MapSelectActivity;
import com.jiangzg.mianmian.activity.settings.HelpActivity;
import com.jiangzg.mianmian.adapter.ImgSquareEditAdapter;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Help;
import com.jiangzg.mianmian.domain.Post;
import com.jiangzg.mianmian.domain.PostKindInfo;
import com.jiangzg.mianmian.domain.PostSubKindInfo;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.domain.RxEvent;
import com.jiangzg.mianmian.fragment.TopicFragment;
import com.jiangzg.mianmian.helper.API;
import com.jiangzg.mianmian.helper.ConsHelper;
import com.jiangzg.mianmian.helper.DialogHelper;
import com.jiangzg.mianmian.helper.ListHelper;
import com.jiangzg.mianmian.helper.OssHelper;
import com.jiangzg.mianmian.helper.RecyclerHelper;
import com.jiangzg.mianmian.helper.ResHelper;
import com.jiangzg.mianmian.helper.RetrofitHelper;
import com.jiangzg.mianmian.helper.RxBus;
import com.jiangzg.mianmian.helper.SPHelper;
import com.jiangzg.mianmian.helper.ViewHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import retrofit2.Call;
import rx.Observable;
import rx.functions.Action1;

public class PostAddActivity extends BaseActivity<PostAddActivity> {

    @BindView(R.id.root)
    LinearLayout root;
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
    @BindView(R.id.cvAddress)
    CardView cvAddress;
    @BindView(R.id.tvAddress)
    TextView tvAddress;
    @BindView(R.id.btnDraft)
    Button btnDraft;
    @BindView(R.id.btnPublish)
    Button btnPublish;

    private Post post;
    private RecyclerHelper recyclerHelper;
    private Call<Result> callAdd;
    private Observable<LocationInfo> obSelectMap;
    private File cameraFile;
    private List<File> cameraFileList;
    private int limitContentLength;

    public static void goActivity(Activity from, int kindId, int subKindId) {
        Intent intent = new Intent(from, PostAddActivity.class);
        intent.putExtra("kindId", kindId);
        intent.putExtra("subKindId", subKindId);
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
            refreshPostKind(intent.getIntExtra("kindId", 0), intent.getIntExtra("subKindId", 0));
        } else {
            refreshPostKind(post.getKind(), post.getSubKind());
        }
        // etTitle
        String format = getString(R.string.please_input_title_no_over_holder_text);
        String hint = String.format(Locale.getDefault(), format, SPHelper.getLimit().getPostTitleLength());
        etTitle.setHint(hint);
        etTitle.setText(post.getTitle());
        // content
        etContent.setText(post.getContentText());
        // recycler
        int limitImagesCount = SPHelper.getVipLimit().getTopicImageCount();
        if (limitImagesCount <= 0) {
            rvImage.setVisibility(View.GONE);
        } else {
            rvImage.setVisibility(View.VISIBLE);
            int spanCount = limitImagesCount > 3 ? 3 : limitImagesCount;
            ImgSquareEditAdapter imgAdapter = new ImgSquareEditAdapter(mActivity, spanCount, limitImagesCount);
            imgAdapter.setOnAddClick(new ImgSquareEditAdapter.OnAddClickListener() {
                @Override
                public void onAdd() {
                    showImgSelect();
                }
            });
            if (recyclerHelper == null) {
                recyclerHelper = new RecyclerHelper(rvImage)
                        .initLayoutManager(new GridLayoutManager(mActivity, spanCount))
                        .initAdapter(imgAdapter)
                        .setAdapter();
            }
        }
        // location
        refreshLocationView();
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        // event
        obSelectMap = RxBus.register(ConsHelper.EVENT_MAP_SELECT, new Action1<LocationInfo>() {
            @Override
            public void call(LocationInfo info) {
                if (info == null || post == null) return;
                post.setLatitude(info.getLatitude());
                post.setLongitude(info.getLongitude());
                post.setAddress(info.getAddress());
                post.setCityId(info.getCityId());
                refreshLocationView();
            }
        });
    }

    @Override
    protected void onFinish(Bundle state) {
        RetrofitHelper.cancel(callAdd);
        RxBus.unregister(ConsHelper.EVENT_MAP_SELECT, obSelectMap);
        RecyclerHelper.release(recyclerHelper);
        // 创建成功的cameraFile都要删除
        ResHelper.deleteFileListInBackground(cameraFileList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || post == null) {
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
                HelpActivity.goActivity(mActivity, Help.INDEX_POST_EDIT);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnTextChanged({R.id.etContent})
    public void afterTextChanged(Editable s) {
        onContentInput(s.toString());
    }

    @OnClick({R.id.cvKind, R.id.cvSubKind, R.id.cvAddress, R.id.btnDraft, R.id.btnPublish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cvKind: // 大类
                showSelectKindDialog();
                break;
            case R.id.cvSubKind: // 小类
                showSelectSubKindDialog();
                break;
            case R.id.cvAddress: // 地址
                if (post == null) return;
                MapSelectActivity.goActivity(mActivity, post.getAddress(), post.getLongitude(), post.getLatitude());
                break;
            case R.id.btnDraft: // 草稿
                saveDraft();
                break;
            case R.id.btnPublish: // 发表
                checkPush();
                break;
        }
    }

    private void refreshPostKind(int kindId, int subKindId) {
        if (TopicFragment.postKindInfoList == null || TopicFragment.postKindInfoList.size() <= 0) {
            mActivity.finish();
            return;
        }
        if (!ListHelper.isPostSubKindPushEnable(kindId, subKindId)) {
            List<PostSubKindInfo> subKindPushList = ListHelper.getPostSubKindPushList(kindId);
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
            subKindId = subKindInfo.getId();
        }
        post.setKind(kindId);
        post.setSubKind(subKindId);
        // view
        PostKindInfo kindInfo = ListHelper.getPostKindInfoById(kindId);
        PostSubKindInfo subKindInfo = ListHelper.getPostSubKindInfoById(kindId, subKindId);
        String kindShow = (kindInfo == null || StringUtils.isEmpty(kindInfo.getName())) ? getString(R.string.please_select_classify) : kindInfo.getName();
        String subKindShow = (subKindInfo == null || StringUtils.isEmpty(subKindInfo.getName())) ? getString(R.string.please_select_classify) : subKindInfo.getName();
        tvKind.setText(kindShow);
        tvSubKind.setText(subKindShow);
    }

    private void showSelectKindDialog() {
        List<String> kindShowList = ListHelper.getPostKindShowList();
        int selectIndex = ListHelper.getIndexInPostKindList(post.getKind());
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .title(R.string.please_select_classify)
                .items(kindShowList)
                .itemsCallbackSingleChoice(selectIndex, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        if (post == null) return true;
                        PostKindInfo kindInfo = TopicFragment.postKindInfoList.get(which);
                        if (kindInfo != null) {
                            List<PostSubKindInfo> subKindPushList = ListHelper.getPostSubKindPushList(kindInfo.getId());
                            if (subKindPushList != null && subKindPushList.size() > 0) {
                                PostSubKindInfo subKindInfo = subKindPushList.get(0);
                                refreshPostKind(kindInfo.getId(), subKindInfo.getId());
                            }
                        }
                        DialogUtils.dismiss(dialog);
                        return true;
                    }
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void showSelectSubKindDialog() {
        if (post == null) return;
        List<String> subKindPushShowList = ListHelper.getPostSubKindPushShowList(post.getKind());
        int selectIndex = ListHelper.getIndexInPostSubKindPushList(post.getKind(), post.getSubKind());
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .title(R.string.please_select_classify)
                .items(subKindPushShowList)
                .itemsCallbackSingleChoice(selectIndex, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        if (post == null) return true;
                        List<PostSubKindInfo> subKindPushList = ListHelper.getPostSubKindPushList(post.getKind());
                        PostSubKindInfo subKindInfo = subKindPushList.get(which);
                        if (subKindInfo != null) {
                            refreshPostKind(post.getKind(), subKindInfo.getId());
                        }
                        DialogUtils.dismiss(dialog);
                        return true;
                    }
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void refreshLocationView() {
        if (post == null) return;
        String location = StringUtils.isEmpty(post.getAddress()) ? getString(R.string.now_no) : post.getAddress();
        tvAddress.setText(location);
    }

    private void showImgSelect() {
        cameraFile = ResHelper.newImageCacheFile();
        PopupWindow popupWindow = ViewHelper.createPictureCameraPop(mActivity, cameraFile);
        PopUtils.show(popupWindow, root, Gravity.CENTER);
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
        } else if (title.length() > SPHelper.getLimit().getFoodTitleLength()) {
            ToastUtils.show(etTitle.getHint().toString());
            return;
        } else if (StringUtils.isEmpty(post.getContentText())) {
            ToastUtils.show(etContent.getHint().toString());
            return;
        } else if (post.getLongitude() == 0 && post.getLatitude() == 0) {
            PostSubKindInfo subKindInfo = ListHelper.getPostSubKindInfoById(post.getKind(), post.getSubKind());
            if (subKindInfo != null && subKindInfo.isLonLat()) {
                ToastUtils.show(getString(R.string.please_select_address));
                return;
            }
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
        OssHelper.uploadPost(mActivity, fileData, new OssHelper.OssUploadsCallBack() {
            @Override
            public void success(List<File> sourceList, List<String> ossPathList) {
                if (recyclerHelper == null) return;
                ImgSquareEditAdapter adapter = recyclerHelper.getAdapter();
                if (adapter == null) return;
                List<String> ossData = adapter.getOssData();
                ossData.addAll(ossPathList == null ? new ArrayList<String>() : ossPathList);
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
                // event
                RxEvent<Integer> event = new RxEvent<>(ConsHelper.EVENT_POST_LIST_REFRESH, post.getSubKind());
                RxBus.post(event);
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
