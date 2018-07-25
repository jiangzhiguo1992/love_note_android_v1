package com.jiangzg.mianmian.activity.topic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.system.LocationInfo;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Post;
import com.jiangzg.mianmian.domain.PostKindInfo;
import com.jiangzg.mianmian.domain.PostSubKindInfo;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.fragment.TopicFragment;
import com.jiangzg.mianmian.helper.ListHelper;
import com.jiangzg.mianmian.helper.RecyclerHelper;
import com.jiangzg.mianmian.helper.SPHelper;
import com.jiangzg.mianmian.helper.ViewHelper;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import rx.Observable;

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

    }

    @Override
    protected void initData(Intent intent, Bundle state) {

    }

    @Override
    protected void onFinish(Bundle state) {

    }

    @OnClick({R.id.cvKind, R.id.cvSubKind, R.id.cvAddress, R.id.btnDraft, R.id.btnPublish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cvKind: // 大类
                // TODO
                break;
            case R.id.cvSubKind: // 小类
                // TODO
                break;
            case R.id.cvAddress: // 地址
                // TODO
                break;
            case R.id.btnDraft: // 草稿
                // TODO
                break;
            case R.id.btnPublish: // 发表
                // TODO
                break;
        }
    }

    private void refreshPostKind(int kindId, int subKindId) {
        if (TopicFragment.postKindInfoList == null || TopicFragment.postKindInfoList.size() <= 0) {
            mActivity.finish();
            return;
        }
        if (!ListHelper.isKindPushEnable(kindId, subKindId)) {
            List<PostSubKindInfo> subKindPushList = ListHelper.getSubKindPushList(kindId);
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
        PostKindInfo kindInfo = ListHelper.getKindInfoByList(kindId, subKindId);
        PostSubKindInfo subKindInfo = kindInfo.getPostSubKindInfoList().get(0);
        String kindShow = StringUtils.isEmpty(kindInfo.getName()) ? getString(R.string.please_select_classify) : kindInfo.getName();
        String subKindShow = StringUtils.isEmpty(subKindInfo.getName()) ? getString(R.string.please_select_classify) : subKindInfo.getName();
        tvKind.setText(kindShow);
        tvSubKind.setText(subKindShow);
    }

    public static void tee() {
        // TODO 发布保存草稿，area带地址
    }
}
