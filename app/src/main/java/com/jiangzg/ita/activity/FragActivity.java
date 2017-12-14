package com.jiangzg.ita.activity;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;

import com.jiangzg.base.component.activity.ActivityTrans;
import com.jiangzg.base.component.fragment.FragmentTrans;
import com.jiangzg.base.function.LocationInfo;
import com.jiangzg.base.function.PermUtils;
import com.jiangzg.ita.R;
import com.jiangzg.ita.base.BaseActivity;
import com.jiangzg.ita.fragment.BigFragment;
import com.jiangzg.ita.fragment.SmallFragment;

import butterknife.BindView;
import butterknife.OnClick;

public class FragActivity extends BaseActivity<FragActivity> {

    @BindView(R.id.rlFragment)
    RelativeLayout rlFragment;

    private BigFragment bigFragment;
    private SmallFragment smallFragment;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, FragActivity.class);
        // intent.putExtra();
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int initObj(Intent intent) {
        return R.layout.activity_frag;
    }

    @Override
    protected void initView(Bundle state) {

    }

    @Override
    protected void initData(Bundle state) {
        bigFragment = BigFragment.newFragment();
        smallFragment = SmallFragment.newFragment();
        FragmentTrans.add(mFragmentManager, smallFragment, R.id.rlFragment);
        FragmentTrans.add(mFragmentManager, bigFragment, R.id.rlFragment);

        //todo location
        Log.e("", "");

    }

    @OnClick(R.id.btnTrans)
    public void onViewClicked() {
        PermUtils.requestPermissions(mActivity, 11, PermUtils.location, new PermUtils.OnPermissionListener() {
            @Override
            public void onPermissionGranted(String[] permissions) {
                LocationInfo info = LocationInfo.getInfo();
                info.addListener(1000, 0, new LocationInfo.OnLocationChangeListener() {
                    @Override
                    public void onLocation(Location location) {
                        Log.e("", "");
                    }

                    @Override
                    public void onStatus(String provider, int status, Bundle extras) {
                        Log.e("", "");
                    }
                });
                Log.d("", "");
            }

            @Override
            public void onPermissionDenied(String[] permissions) {
                Log.d("", "");
            }
        });


        //FragmentManager m = getSupportFragmentManager();
        //if (bigFragment.isVisible()) {
        //    FragmentTrans.show(m, smallFragment, false);
        //    //FragmentTrans.hide(m, bigFragment, false);
        //    //smallFragment = bigFragment.replace();
        //} else {
        //    FragmentTrans.show(m, bigFragment, false);
        //    //FragmentTrans.hide(m, smallFragment, false);
        //    //bigFragment = smallFragment.replace();
        //}
    }
}
