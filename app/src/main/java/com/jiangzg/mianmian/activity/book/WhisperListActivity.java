package com.jiangzg.mianmian.activity.book;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.system.InputUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.common.HelpActivity;
import com.jiangzg.mianmian.adapter.WhisperAdapter;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Help;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.domain.Whisper;
import com.jiangzg.mianmian.helper.API;
import com.jiangzg.mianmian.helper.ApiHelper;
import com.jiangzg.mianmian.helper.DialogHelper;
import com.jiangzg.mianmian.helper.RecyclerHelper;
import com.jiangzg.mianmian.helper.RetrofitHelper;
import com.jiangzg.mianmian.helper.SPHelper;
import com.jiangzg.mianmian.helper.ViewHelper;
import com.jiangzg.mianmian.view.GSwipeRefreshLayout;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import retrofit2.Call;

public class WhisperListActivity extends BaseActivity<WhisperListActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.tvCurrentChannel)
    TextView tvCurrentChannel;
    @BindView(R.id.etChannel)
    EditText etChannel;
    @BindView(R.id.btnChangeChannel)
    Button btnChangeChannel;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.llAddImage)
    LinearLayout llAddImage;
    @BindView(R.id.llAddText)
    LinearLayout llAddText;

    private int limitChannel;
    private RecyclerHelper recyclerHelper;
    private int page;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, WhisperListActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        page = 0;
        return R.layout.activity_whisper_list;
    }

    @Override
    protected void initView(Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.whisper), true);
        // recycler
        recyclerHelper = new RecyclerHelper(mActivity)
                .initRecycler(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initRefresh(srl, false)
                .initAdapter(new WhisperAdapter(mActivity))
                .viewEmpty(R.layout.list_empty_grey, true, true)
                .viewLoadMore(new RecyclerHelper.MoreGreyView())
                .listenerRefresh(new RecyclerHelper.RefreshListener() {
                    @Override
                    public void onRefresh() {
                        getData(false);
                    }
                })
                .listenerMore(new RecyclerHelper.MoreListener() {
                    @Override
                    public void onMore(int currentCount) {
                        getData(true);
                    }
                });
    }

    @Override
    protected void initData(Bundle state) {
        recyclerHelper.dataRefresh();
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
                HelpActivity.goActivity(mActivity, Help.TYPE_WHISPER_LIST);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnTextChanged({R.id.etChannel})
    public void afterTextChanged(Editable s) {
        onChannelInput(s.toString());
    }

    @OnClick({R.id.btnChangeChannel, R.id.llAddImage, R.id.llAddText})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnChangeChannel: // 切换频道
                getData(false);
                break;
            case R.id.llAddImage: // 添加图片
                showSelectImgPop();
                break;
            case R.id.llAddText: // 添加文字
                showEditDialog();
                break;
        }
    }

    private void getData(final boolean more) {
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        page = more ? page + 1 : 0;
        String channel = refreshCurrentChannelView();
        Call<Result> call = new RetrofitHelper().call(API.class).whisperListGet(channel, page);
        RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                recyclerHelper.viewEmptyShow(data.getShow());
                long total = data.getTotal();
                List<Whisper> whisperList = data.getWhisperList();
                recyclerHelper.dataOk(whisperList, total, more);
            }

            @Override
            public void onFailure(String errMsg) {
                recyclerHelper.dataFail(page != 0, errMsg);
            }
        });
    }

    private String refreshCurrentChannelView() {
        String channel = etChannel.getText().toString().trim();
        String currentChannel = String.format(Locale.getDefault(), getString(R.string.current_channel_colon_space_holder), channel);
        tvCurrentChannel.setText(currentChannel);
        return channel;
    }

    private void onChannelInput(String input) {
        if (StringUtils.isEmpty(input)) {
            btnChangeChannel.setEnabled(false);
        } else {
            btnChangeChannel.setEnabled(true);
        }
        if (limitChannel <= 0) {
            limitChannel = SPHelper.getLimit().getWhisperLimitChannel();
        }
        int length = input.length();
        if (length > limitChannel) {
            CharSequence charSequence = input.subSequence(0, limitChannel);
            etChannel.setText(charSequence);
            etChannel.setSelection(charSequence.length());
        }
    }

    // 文字添加
    private void showEditDialog() {
        int limitContent = SPHelper.getLimit().getWhisperLimitContent();
        final String channel = etChannel.getText().toString().trim();
        String currentChannel = String.format(Locale.getDefault(), getString(R.string.save_channel_colon_space_holder), channel);
        String hint = String.format(Locale.getDefault(), getString(R.string.please_input_content_dont_over_holder_text), limitContent);
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .content(currentChannel)
                .input(hint, "", false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                    }
                })
                .positiveText(R.string.send)
                .negativeText(R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        EditText editText = dialog.getInputEditText();
                        if (editText != null) {
                            String content = editText.getText().toString();
                            Whisper body = ApiHelper.getWhisperBody(channel, false, content);
                            api(body);
                        }
                    }
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    // 图片获取
    private void showSelectImgPop() {
        // TODO 频道显示出来
    }

    private void api(Whisper whisper) {
        Call<Result> call = new RetrofitHelper().call(API.class).whisperPost(whisper);
        MaterialDialog loading = getLoading(true);
        RetrofitHelper.enqueue(call, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // editText
                InputUtils.hideSoftInput(etChannel);
                // adapter
                WhisperAdapter adapter = recyclerHelper.getAdapter();
                adapter.addData(data.getWhisper());
                // 下移
                int position = adapter.getData().size() - 1;
                rv.smoothScrollToPosition(position);
            }

            @Override
            public void onFailure(String errMsg) {
            }
        });
    }

}
