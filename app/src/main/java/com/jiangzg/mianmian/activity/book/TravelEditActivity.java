package com.jiangzg.mianmian.activity.book;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Travel;

public class TravelEditActivity extends BaseActivity<TravelEditActivity> {

    private static final int TYPE_ADD = 0;
    private static final int TYPE_UPDATE = 1;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, TravelEditActivity.class);
        intent.putExtra("type", TYPE_ADD);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from, Travel travel) {
        if (travel == null) {
            goActivity(from);
        } else if (!travel.isMine()) {
            ToastUtils.show(from.getString(R.string.can_operation_self_create_travel));
            return;
        }
        Intent intent = new Intent(from, TravelEditActivity.class);
        intent.putExtra("type", TYPE_UPDATE);
        intent.putExtra("travel", travel);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_travel_edit;
    }

    @Override
    protected void initView(Bundle state) {
        // TODO travel的所有修改都在edit里做，上传要带place才生成place的数据，不带不修改(其他的外键id也是)

    }

    @Override
    protected void initData(Bundle state) {

    }

}
