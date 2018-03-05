package com.jiangzg.ita.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.ita.R;
import com.jiangzg.ita.base.BaseFragment;
import com.jiangzg.ita.base.BasePagerFragment;

public class BookFragment extends BasePagerFragment<BookFragment> {

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

    }

    protected void refreshData() {
        ToastUtils.show("Book加载数据");
    }

}
