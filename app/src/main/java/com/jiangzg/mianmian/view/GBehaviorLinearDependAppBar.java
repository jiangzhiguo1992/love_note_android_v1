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
public class GBehaviorLinearDependAppBar extends CoordinatorLayout.Behavior<LinearLayout> {


    public GBehaviorLinearDependAppBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //确定所提供的子视图是否有另一个特定的同级视图作为布局从属。
    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, LinearLayout child, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, LinearLayout child, View dependency) {
        // 算出dependency移动的百分比
        int dependencyHeight = dependency.getHeight();
        int top = dependency.getTop();
        float topAbs = Math.abs(top);
        float percent = topAbs / dependencyHeight;
        // 算出child应该移动的距离
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
        int transYTotal = layoutParams.bottomMargin + child.getHeight();
        float transY = transYTotal * percent;
        transY = (top > 0) ? transY * -1 : transY;
        // 开始移动
        child.setTranslationY(transY);
        return true;
    }

}