package com.jiangzg.lovenote.fragment.more;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.base.BaseFragment;
import com.jiangzg.lovenote.base.BasePagerFragment;
import com.jiangzg.lovenote.view.GSwipeRefreshLayout;

import butterknife.BindView;

public class MatchWifeListFragment extends BasePagerFragment<MatchWifeListFragment> {

    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.rv)
    RecyclerView rv;

    public static MatchWifeListFragment newFragment() {
        Bundle bundle = new Bundle();
        return BaseFragment.newInstance(MatchWifeListFragment.class, bundle);
    }

    @Override
    protected int getView(Bundle data) {
        return R.layout.fragment_match_wife_list;
    }

    @Override
    protected void initView(@Nullable Bundle state) {

    }

    @Override
    protected void onFinish(Bundle state) {

    }

    @Override
    protected void loadData() {

    }
}
