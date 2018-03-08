package com.jiangzg.ita.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.jiangzg.base.view.BarUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.ita.R;
import com.jiangzg.ita.base.BaseFragment;
import com.jiangzg.ita.base.BasePagerFragment;
import com.jiangzg.ita.utils.ViewUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

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
        ViewUtils.initTopBar(mActivity, tb, "小本本", false);
        fitToolBar(tb);
    }

    protected void refreshData() {
        ToastUtils.show("Book加载数据");
    }

}
