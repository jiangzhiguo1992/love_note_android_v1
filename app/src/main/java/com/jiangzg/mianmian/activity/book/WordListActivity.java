package com.jiangzg.mianmian.activity.book;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.system.InputUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.settings.HelpActivity;
import com.jiangzg.mianmian.adapter.WordAdapter;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Help;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.domain.Word;
import com.jiangzg.mianmian.helper.API;
import com.jiangzg.mianmian.helper.ApiHelper;
import com.jiangzg.mianmian.helper.ConsHelper;
import com.jiangzg.mianmian.helper.ListHelper;
import com.jiangzg.mianmian.helper.RecyclerHelper;
import com.jiangzg.mianmian.helper.RetrofitHelper;
import com.jiangzg.mianmian.helper.RxBus;
import com.jiangzg.mianmian.helper.SPHelper;
import com.jiangzg.mianmian.helper.ViewHelper;
import com.jiangzg.mianmian.view.GSwipeRefreshLayout;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import retrofit2.Call;
import rx.Observable;
import rx.functions.Action1;

public class WordListActivity extends BaseActivity<WordListActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.etContent)
    EditText etContent;
    @BindView(R.id.btnSend)
    Button btnSend;

    private RecyclerHelper recyclerHelper;
    private Call<Result> callGet;
    private Call<Result> callAdd;
    private Observable<Word> obListItemDelete;
    private int limitContentLength;
    private int page;
    private boolean canMore;

    public static void goActivity(Fragment from) {
        Intent intent = new Intent(from.getActivity(), WordListActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_word_list;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.word), true);
        // editTextHint
        limitContentLength = SPHelper.getLimit().getWordContentLength();
        String format = String.format(Locale.getDefault(), getString(R.string.please_input_content_no_over_holder_text), limitContentLength);
        etContent.setHint(format);
        etContent.setText("");
        // recycler
        recyclerHelper = new RecyclerHelper(rv) // 数据倒着来，显示也会倒着来
                .initLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, true))
                .initRefresh(srl, true)
                .initAdapter(new WordAdapter(mActivity))
                .viewEmpty(mActivity, R.layout.list_empty_white, true, true)
                .setAdapter()
                .listenerRefresh(new RecyclerHelper.RefreshListener() {
                    @Override
                    public void onRefresh() {
                        getDataMore();
                    }
                })
                .listenerClick(new OnItemLongClickListener() {
                    @Override
                    public void onSimpleItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                        WordAdapter wordAdapter = (WordAdapter) adapter;
                        wordAdapter.showDeleteDialog(position);
                    }
                });
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        page = -1;
        canMore = true;
        // event
        obListItemDelete = RxBus.register(ConsHelper.EVENT_WORD_LIST_ITEM_DELETE, new Action1<Word>() {
            @Override
            public void call(Word word) {
                if (recyclerHelper == null) return;
                ListHelper.removeObjInAdapter(recyclerHelper.getAdapter(), word);
            }
        });
        // refresh
        recyclerHelper.dataRefresh();
    }

    @Override
    protected void onFinish(Bundle state) {
        RecyclerHelper.release(recyclerHelper);
        RetrofitHelper.cancel(callGet);
        RetrofitHelper.cancel(callAdd);
        RxBus.unregister(ConsHelper.EVENT_WORD_LIST_ITEM_DELETE, obListItemDelete);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuHelp: // 帮助
                HelpActivity.goActivity(mActivity, Help.INDEX_BOOK_WORD_LIST);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnTextChanged({R.id.etContent})
    public void afterTextChanged(Editable s) {
        onContentInput(s.toString());
    }

    @OnClick({R.id.btnSend})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnSend: // 发送
                wordPush();
                break;
        }
    }

    private void onContentInput(String input) {
        if (StringUtils.isEmpty(input)) {
            btnSend.setEnabled(false);
        } else {
            btnSend.setEnabled(true);
        }
        int length = input.length();
        if (length > limitContentLength) {
            CharSequence charSequence = input.subSequence(0, limitContentLength);
            etContent.setText(charSequence);
            etContent.setSelection(charSequence.length());
        }
    }

    private void getDataMore() {
        if (!canMore) {
            srl.setRefreshing(false);
            ToastUtils.show(getString(R.string.already_arrive_top));
        }
        ++page;
        callGet = new RetrofitHelper().call(API.class).wordListGet(page);
        RetrofitHelper.enqueue(callGet, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.viewEmptyShow(data.getShow());
                List<Word> wordList = data.getWordList();
                recyclerHelper.dataOk(wordList, page != 0);
                canMore = (wordList != null && wordList.size() > 0);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.dataFail(page != 0, message);
            }
        });
    }

    private void wordPush() {
        String content = etContent.getText().toString();
        Word body = ApiHelper.getWordBody(content);
        MaterialDialog loading = mActivity.getLoading(true);
        callAdd = new RetrofitHelper().call(API.class).wordAdd(body);
        RetrofitHelper.enqueue(callAdd, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                // editText
                etContent.setText("");
                InputUtils.hideSoftInput(etContent);
                // adapter
                WordAdapter adapter = recyclerHelper.getAdapter();
                adapter.addData(0, data.getWord());
                // 下移，因为layoutManager是反转的，所以这里的最下方是0
                rv.smoothScrollToPosition(0);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

}
