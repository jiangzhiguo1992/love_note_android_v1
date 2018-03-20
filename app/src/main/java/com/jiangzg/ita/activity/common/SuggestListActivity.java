package com.jiangzg.ita.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.jiangzg.base.component.activity.ActivityTrans;
import com.jiangzg.ita.R;
import com.jiangzg.ita.base.BaseActivity;
import com.jiangzg.ita.domain.Help;

public class SuggestListActivity extends BaseActivity<SuggestListActivity> {

    public static final int ENTRY_MINE = 0;
    public static final int ENTRY_FOLLOW = 1;
    private int entry;

    public static void goActivity(Activity from, int entry) {
        Intent intent = new Intent(from, SuggestListActivity.class);
        intent.putExtra("entry", entry);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_suggest_list;
    }

    @Override
    protected void initView(Bundle state) {
        entry = getIntent().getIntExtra("entry", ENTRY_MINE);

        // menu
        //tb.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
        //    @Override
        //    public boolean onMenuItemClick(MenuItem item) {
        //        switch (item.getItemId()) {
        //            case R.id.menuHelp: // 评论
        //                HelpActivity.goActivity(mActivity, Help.TYPE_SUGGEST_ADD);
        //                break;
        //        }
        //        return true;
        //    }
        //});
    }

    @Override
    protected void initData(Bundle state) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help, menu);
        return super.onCreateOptionsMenu(menu);
    }

}
