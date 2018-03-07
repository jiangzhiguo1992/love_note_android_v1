package com.jiangzg.ita.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

import com.jiangzg.ita.R;
import com.jiangzg.ita.utils.ViewUtils;

import java.lang.reflect.Field;

/**
 * Created by JZG on 2018/3/3.
 * 数字选择器
 */

public class GNumberPicker extends NumberPicker {

    private int mTextColor;
    private int mDividerColor;

    public GNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public GNumberPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GNumberPicker);
        int color = ViewUtils.getColorPrimary(context);
        // 获取参数
        mTextColor = a.getColor(R.styleable.GNumberPicker_text_color, color);
        mDividerColor = a.getColor(R.styleable.GNumberPicker_divider_color, color);
        a.recycle();
        // 设置属性
        setCustomStyle();
        // 设置不可获取焦点
        setFocusable(false);
        setFocusableInTouchMode(false);
        // 取消点击可设置
        setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        // 设置不循环
        setWrapSelectorWheel(false);
    }

    public void setCustomStyle() {
        Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (Field field : pickerFields) {
            try {
                field.setAccessible(true);
                String name = field.getName();
                switch (name) {
                    case "mSelectionDivider":  // 分割线的颜色
                        field.set(this, new ColorDrawable(mDividerColor));
                        break;
                    case "mSelectorWheelPaint":  // 字体的颜色
                        ((Paint) field.get(this)).setColor(mTextColor);
                        break;
                    case "mInputText":  // 防止字体在首次展示和点击时显示不正确
                        ((EditText) field.get(this)).setFilters(new InputFilter[]{});
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 字体开始显示的颜色
        final int count = this.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = this.getChildAt(i);
            if (child instanceof EditText) {
                ((EditText) child).setTextColor(mTextColor);
                //this.invalidate();
            }
        }
    }

}
