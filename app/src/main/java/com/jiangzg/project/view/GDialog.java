package com.jiangzg.project.view;

import android.app.Activity;
import android.app.ProgressDialog;

import com.android.base.view.widget.DialogUtils;
import com.jiangzg.project.R;

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
    public ProgressDialog getLoading(Activity mActivity) {
        if (loading != null) return loading;
        loading = DialogUtils.createLoading(mActivity,
                0, "", mActivity.getString(R.string.wait), true);
        return loading;
    }

    /**
     * @return 进度框(静态会混乱)
     */
    public ProgressDialog getProgress(Activity mActivity) {
        if (progress != null) return progress;
        progress = DialogUtils.createProgress(mActivity,
                0, mActivity.getString(R.string.wait), true, 100, 0, null);
        return progress;
    }

}
