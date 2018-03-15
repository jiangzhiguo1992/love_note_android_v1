package com.jiangzg.ita.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.jiangzg.base.component.activity.ActivityTrans;
import com.jiangzg.ita.R;
import com.jiangzg.ita.base.BaseActivity;
import com.jiangzg.ita.domain.Suggest;
import com.jiangzg.ita.domain.SuggestComment;
import com.jiangzg.ita.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class SuggestDetailActivity extends BaseActivity<SuggestDetailActivity> {

    @BindView(R.id.tb)
    Toolbar tb;

    private Suggest suggest;

    public static void goActivity(Activity from, Suggest suggest) {
        Intent intent = new Intent(from, SuggestDetailActivity.class);
        intent.putExtra("suggest", suggest);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_suggest_detail;
    }

    @Override
    protected void initView(Bundle state) {
        suggest = (Suggest) getIntent().getSerializableExtra("suggest");
        String title = (suggest == null) ? getString(R.string.suggest_feedback) : suggest.getTitle();
        ViewUtils.initTopBar(mActivity, tb, title, true);

        // todo
    }

    @Override
    protected void initData(Bundle state) {
        getData();
    }

    private void getData() {
        suggest.setContentText("这是一个很不好的消息，你们的产品太差了，真的不好。这是一个很不好的消息，你们的产品太差了，真的不好。这是一个很不好的消息，你们的产品太差了，真的不好。这是一个很不好的消息，你们的产品太差了，真的不好。");
        suggest.setContentImgUrl("https://timgsa.baidu.com/timg?image");
        List<SuggestComment> commentList = new ArrayList<>();
        SuggestComment c1 = new SuggestComment();
        //c1.setCreatedAt();
        //c1.setOfficial();
        //c1.setMine();
        //c1.setContentText();
        suggest.setCommentList(commentList);
        // todo tpi
    }

}
