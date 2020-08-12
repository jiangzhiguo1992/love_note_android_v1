package com.jiangzg.lovenote.controller.activity.note;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.controller.activity.settings.HelpActivity;
import com.jiangzg.lovenote.controller.adapter.common.CommonFragmentAdapter;
import com.jiangzg.lovenote.controller.fragment.note.SouvenirListFragment;

import butterknife.BindView;
import butterknife.OnClick;

public class SouvenirListActivity extends BaseActivity<SouvenirListActivity> {


    @BindView(R.id.ivBack)
    ImageView ivBack;
    @BindView(R.id.ivHelp)
    ImageView ivHelp;

    @BindView(R.id.rgSouvenir)
    RadioGroup rgSouvenir;
    @BindView(R.id.rbSouvenir)
    RadioButton rbSouvenir;
    @BindView(R.id.rbWish)
    RadioButton rbWish;

    @BindView(R.id.vpFragment)
    ViewPager vpFragment;
    @BindView(R.id.fabAdd)
    FloatingActionButton fabAdd;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, SouvenirListActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Fragment from) {
        Intent intent = new Intent(from.getActivity(), SouvenirListActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Context from) {
        Intent intent = new Intent(from, SouvenirListActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_souvenir_list;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        // fragment
        SouvenirListFragment souvenirDoneFragment = SouvenirListFragment.newFragment(true);
        SouvenirListFragment souvenirWishFragment = SouvenirListFragment.newFragment(false);
        // adapter
        CommonFragmentAdapter<SouvenirListFragment> adapter = new CommonFragmentAdapter<>(getSupportFragmentManager());
        adapter.addData(rbSouvenir.getText().toString().trim(), souvenirDoneFragment);
        adapter.addData(rbWish.getText().toString().trim(), souvenirWishFragment);
        // group
        rbSouvenir.setChecked(true);
        rbWish.setChecked(false);
        rgSouvenir.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.rbSouvenir: // 纪念日
                    vpFragment.setCurrentItem(0, true);
                    break;
                case R.id.rbWish: // 愿望清单
                    vpFragment.setCurrentItem(1, true);
                    break;
            }
        });
        // viewPager
        vpFragment.setOffscreenPageLimit(1);
        vpFragment.setAdapter(adapter);
        vpFragment.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                boolean isSouvenir = position <= 0;
                rbSouvenir.setChecked(isSouvenir);
                rbWish.setChecked(!isSouvenir);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        rbSouvenir.measure(0, 0);
        rbWish.measure(0, 0);
        int souvenirWidth = rbSouvenir.getMeasuredWidth();
        int wishWidth = rbWish.getMeasuredWidth();
        RadioGroup.LayoutParams layoutParamsSouvenir = (RadioGroup.LayoutParams) rbSouvenir.getLayoutParams();
        RadioGroup.LayoutParams layoutParamsWidth = (RadioGroup.LayoutParams) rbWish.getLayoutParams();
        layoutParamsSouvenir.width = Math.max(souvenirWidth, wishWidth);
        layoutParamsWidth.width = Math.max(souvenirWidth, wishWidth);
        rbSouvenir.setLayoutParams(layoutParamsSouvenir);
        rbWish.setLayoutParams(layoutParamsWidth);
    }

    @Override
    protected void onFinish(Bundle state) {
    }

    @OnClick({R.id.ivBack, R.id.ivHelp, R.id.fabAdd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivBack: // 返回
                mActivity.finish();
                break;
            case R.id.ivHelp: // 帮助
                HelpActivity.goActivity(mActivity, HelpActivity.INDEX_NOTE_SOUVENIR);
                break;
            case R.id.fabAdd: // 添加
                SouvenirEditActivity.goActivity(mActivity);
                break;
        }
    }

}
