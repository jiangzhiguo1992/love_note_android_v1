package com.jiangzg.ita.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jiangzg.base.component.activity.ActivityTrans;
import com.jiangzg.base.component.fragment.FragmentTrans;
import com.jiangzg.ita.R;
import com.jiangzg.ita.base.BaseActivity;
import com.jiangzg.ita.fragment.BookFragment;
import com.jiangzg.ita.fragment.ShowFragment;
import com.jiangzg.ita.fragment.WeFragment;
import com.jiangzg.ita.utils.UserPreference;
import com.jiangzg.ita.utils.ViewUtils;

import butterknife.BindView;

public class HomeActivity extends BaseActivity<HomeActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.rlContent)
    RelativeLayout rlContent;
    //@BindView(R.id.rbBook)
    //RadioButton rbBook;
    //@BindView(R.id.rbWe)
    //RadioButton rbWe;
    //@BindView(R.id.rbShow)
    //RadioButton rbShow;
    //@BindView(R.id.rgBottom)
    //RadioGroup rgBottom;


    private BookFragment bookFragment;
    private WeFragment weFragment;
    private ShowFragment showFragment;

    // todo 启动模式调一下(唯一的那种)
    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, HomeActivity.class);
        // intent.putExtra();
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        //BarUtils.setBarTrans(mActivity, false);
        return R.layout.activity_home;
    }

    @Override
    protected void initView(Bundle state) {
        //VectorDrawableCompat a=VectorDrawableCompat.create(getResources(), R.drawable.ic_group, getTheme());
        //a.setTintMode();
        //a.setTint(Color.RED);
        //ImageView imageview= (ImageView) findViewById(R.id.imageview);
        //imageview.setImageDrawable(a);

        ViewUtils.initToolbar(mActivity, tb, false);

        bookFragment = BookFragment.newFragment();
        weFragment = WeFragment.newFragment();
        showFragment = ShowFragment.newFragment();

        //rgBottom.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
        //    @Override
        //    public void onCheckedChanged(RadioGroup group, int checkedId) {
        //        switch (checkedId) {
        //            case R.id.rbBook: // 小本本
        //                String textBook = rbBook.getText().toString();
        //                tvTitle.setText(textBook);
        //                if (bookFragment.isAdded()) {
        //                    FragmentTrans.show(mFragmentManager, bookFragment, false);
        //                } else {
        //                    FragmentTrans.add(mFragmentManager, bookFragment, R.id.rlContent);
        //                }
        //                FragmentTrans.hide(mFragmentManager, weFragment, false);
        //                FragmentTrans.hide(mFragmentManager, showFragment, false);
        //                break;
        //            case R.id.rbWe: // 我和他
        //                String textWe = rbWe.getText().toString();
        //                tvTitle.setText(textWe);
        //                if (weFragment.isAdded()) {
        //                    FragmentTrans.show(mFragmentManager, weFragment, false);
        //                } else {
                            FragmentTrans.add(mFragmentManager, weFragment, R.id.rlContent);
        //                }
        //                FragmentTrans.hide(mFragmentManager, bookFragment, false);
        //                FragmentTrans.hide(mFragmentManager, showFragment, false);
        //                break;
        //            case R.id.rbShow: // 秀恩爱
        //                String textShow = rbShow.getText().toString();
        //                tvTitle.setText(textShow);
        //                if (showFragment.isAdded()) {
        //                    FragmentTrans.show(mFragmentManager, showFragment, false);
        //                } else {
        //                    FragmentTrans.add(mFragmentManager, showFragment, R.id.rlContent);
        //                }
        //                FragmentTrans.hide(mFragmentManager, bookFragment, false);
        //                FragmentTrans.hide(mFragmentManager, weFragment, false);
        //                break;
        //        }
        //    }
        //});
        //goInitTab();
    }

    @Override
    protected void initData(Bundle state) {
        initUser();
    }

    //@Override
    //protected void onRestart() {
    //    super.onRestart();
    //    initUser();
    //}

    //public void goInitTab() {
    //    rbWe.setChecked(true);
    //}

    private void initUser() {
        if (UserPreference.noLogin()) {
            //todo
        } else {
            if (UserPreference.noCouple()) {
                //todo
            } else {
                //todo
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuSettings: //设置

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
