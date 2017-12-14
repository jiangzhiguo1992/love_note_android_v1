package com.jiangzg.ita.view;

import android.app.Activity;
import android.app.ProgressDialog;

import com.jiangzg.base.view.DialogUtils;

/**
 * Created by gg on 2017/5/9.
 * 对话框
 */
public class GDialog {

    private ProgressDialog loading;
    private ProgressDialog progress;

    /**
     * @return 对话框(静态会混乱)
     */
    public ProgressDialog getLoading(Activity mActivity,String text) {
        if (loading != null) return loading;
        loading = DialogUtils.createLoading(mActivity,
                0, "", text, true);
        return loading;
    }

    /**
     * @return 进度框(静态会混乱)
     */
    public ProgressDialog getProgress(Activity mActivity,String text) {
        if (progress != null) return progress;
        progress = DialogUtils.createProgress(mActivity,
                0, text, true, 100, 0, null);
        return progress;
    }

}
