package com.jiangzg.mianmian.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.base.BaseFragment;
import com.jiangzg.mianmian.domain.Souvenir;

public class SouvenirForeignFragment extends BaseFragment<SouvenirForeignFragment> {

    public static SouvenirForeignFragment newFragment(Souvenir souvenir, int year) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("souvenir", souvenir);
        bundle.putInt("year", year);
        return BaseFragment.newInstance(SouvenirForeignFragment.class, bundle);
    }

    @Override
    protected int getView(Bundle data) {
        return R.layout.fragment_souvenir_foreign;
    }

    @Override
    protected void initView(@Nullable Bundle state) {

    }

    @Override
    protected void initData(Bundle state) {

    }

    @Override
    protected void onFinish(Bundle state) {

    }

    public void edit() {
        // TODO edit：foreign的修改只带year和id(包括souvenir)

    }

}
