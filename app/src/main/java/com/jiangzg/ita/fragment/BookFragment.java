package com.jiangzg.ita.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;

import com.jiangzg.ita.R;
import com.jiangzg.ita.base.BaseFragment;
import com.jiangzg.ita.base.BasePagerFragment;
import com.jiangzg.ita.helper.ViewHelper;

import butterknife.BindView;

public class BookFragment extends BasePagerFragment<BookFragment> {

    @BindView(R.id.tb)
    Toolbar tb;

    public static BookFragment newFragment() {
        Bundle bundle = new Bundle();
        // bundle.putData();
        return BaseFragment.newInstance(BookFragment.class, bundle);
    }

    @Override
    protected int getView(Bundle data) {
        return R.layout.fragment_book;
    }

    @Override
    protected void initView(@Nullable Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.small_book), false);
        fitToolBar(tb);
    }

    protected void loadData() {

    }

}
