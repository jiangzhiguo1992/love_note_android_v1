package com.jiangzg.mianmian.view;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by JZG on 2018/3/18.
 * 跟随appBar一起消失
 */
public class GBehaviorFooterDependAppBar extends CoordinatorLayout.Behavior<LinearLayout> {


    public GBehaviorFooterDependAppBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //确定所提供的子视图是否有另一个特定的同级视图作为布局从属。
    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, LinearLayout child, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, LinearLayout child, View dependency) {
        int top = dependency.getTop();
        float translationY = Math.abs(top);
        child.setTranslationY(-top);
        return true;
    }

}