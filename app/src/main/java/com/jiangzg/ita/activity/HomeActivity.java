package com.jiangzg.ita.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jiangzg.base.component.activity.ActivityTrans;
import com.jiangzg.base.component.fragment.FragmentTrans;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.base.view.BarUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.ita.R;
import com.jiangzg.ita.base.BaseActivity;
import com.jiangzg.ita.fragment.BookFragment;
import com.jiangzg.ita.fragment.ShowFragment;
import com.jiangzg.ita.fragment.WeFragment;
import com.jiangzg.ita.utils.ViewUtils;

import butterknife.BindView;

public class HomeActivity extends BaseActivity<HomeActivity> {

    @BindView(R.id.dl)
    DrawerLayout dl;
    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.rlContent)
    RelativeLayout rlContent;
    @BindView(R.id.rbBook)
    RadioButton rbBook;
    @BindView(R.id.rbWe)
    RadioButton rbWe;
    @BindView(R.id.rbShow)
    RadioButton rbShow;
    @BindView(R.id.rgBottom)
    RadioGroup rgBottom;

    private BookFragment bookFragment;
    private WeFragment weFragment;
    private ShowFragment showFragment;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, HomeActivity.class);
        // intent.putExtra();
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_home;
    }

    @Override
    protected void initView(Bundle state) {
        //沉浸式状态栏
        BarUtils.setStatusColor(mActivity, Color.TRANSPARENT);
        //toolbar + drawer
        ViewUtils.initToolbar(mActivity, tb, false);
        ViewUtils.initDrawerLayout(mActivity, dl, tb);

        bookFragment = BookFragment.newFragment();
        weFragment = WeFragment.newFragment();
        showFragment = ShowFragment.newFragment();

    }

    @Override
    protected void initData(Bundle state) {
        rgBottom.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbBook: // 首页
                        String textBook = rbBook.getText().toString();
                        tvTitle.setText(textBook);
                        if (bookFragment.isAdded()) {
                            FragmentTrans.show(mFragmentManager, bookFragment, false);
                        } else {
                            FragmentTrans.add(mFragmentManager, bookFragment, R.id.rlContent);
                        }
                        FragmentTrans.hide(mFragmentManager, weFragment, false);
                        FragmentTrans.hide(mFragmentManager, showFragment, false);
                        break;
                    case R.id.rbWe: // 分类
                        String textWe = rbWe.getText().toString();
                        tvTitle.setText(textWe);
                        if (weFragment.isAdded()) {
                            FragmentTrans.show(mFragmentManager, weFragment, false);
                        } else {
                            FragmentTrans.add(mFragmentManager, weFragment, R.id.rlContent);
                        }
                        FragmentTrans.hide(mFragmentManager, bookFragment, false);
                        FragmentTrans.hide(mFragmentManager, showFragment, false);
                        break;
                    case R.id.rbShow: // 购物
                        String textShow = rbShow.getText().toString();
                        tvTitle.setText(textShow);
                        if (showFragment.isAdded()) {
                            FragmentTrans.show(mFragmentManager, showFragment, false);
                        } else {
                            FragmentTrans.add(mFragmentManager, showFragment, R.id.rlContent);
                        }
                        FragmentTrans.hide(mFragmentManager, bookFragment, false);
                        FragmentTrans.hide(mFragmentManager, weFragment, false);
                        break;
                }
            }
        });
        rbWe.setChecked(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (dl.isDrawerOpen(GravityCompat.START)) {
            dl.closeDrawer(GravityCompat.START);
        } else {
            Long nowTime = DateUtils.getCurrentLong();
            if (nowTime - lastExitTime > 2000) { // 第一次按
                ToastUtils.show(R.string.press_again_exit);
            } else { // 返回键连按两次
                //AppUtils.appExit();
                super.onBackPressed();
            }
            lastExitTime = nowTime;
        }
    }

    //
    ///* 手机返回键 */
    //@Override
    //public boolean onKeyDown(int keyCode, KeyEvent event) {
    //    if (keyCode == KeyEvent.KEYCODE_BACK) {
    //        exit();
    //    }
    //    return true;
    //}

    private Long lastExitTime = 0L; //最后一次退出时间

    private void exit() {

    }

}
