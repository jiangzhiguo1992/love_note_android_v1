package com.jiangzg.ita.view;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by JZG on 2018/3/18.
 * 跟随appBar一起消失
 */
public class FooterBehaviorDependAppBar extends CoordinatorLayout.Behavior<View> {


    public FooterBehaviorDependAppBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //确定所提供的子视图是否有另一个特定的同级视图作为布局从属。
    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        int visibility = dependency.getVisibility();
        float translationY1 = dependency.getTranslationY();
        int top = dependency.getTop();
        int paddingTop = dependency.getPaddingTop();
        float translationY = Math.abs(translationY1);//获取更随布局的顶部位置
        //float translationY = dependency.getTranslationY();
        child.setTranslationY(translationY);
        return true;
    }

}