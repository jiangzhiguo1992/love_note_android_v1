package com.jiangzg.lovenote.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.haibin.calendarview.MonthView;
import com.jiangzg.base.common.ConvertUtils;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.lovenote.helper.TimeHelper;

import java.util.Calendar;

/**
 * Created by JZG on 2018/11/22.
 * CalendarMonthView
 */
public class CalendarMonthView extends MonthView {

    private Paint mTextPaint = new Paint();
    private Paint mSchemeBasicPaint = new Paint();
    private float mRadio;
    private int mPadding;
    private float mSchemeBaseLine;

    public CalendarMonthView(Context context) {
        super(context);
        // 绘制scheme文字的
        mTextPaint.setTextSize(ConvertUtils.dp2px(8));
        mTextPaint.setColor(0xffFFFFFF);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setFakeBoldText(true);
        // 绘制scheme圆圈的
        mSchemeBasicPaint.setAntiAlias(true);
        mSchemeBasicPaint.setStyle(Paint.Style.FILL);
        mSchemeBasicPaint.setTextAlign(Paint.Align.CENTER);
        mSchemeBasicPaint.setColor(0xffed5353);
        mSchemeBasicPaint.setFakeBoldText(true);
        mRadio = ConvertUtils.dp2px(7);
        mPadding = ConvertUtils.dp2px(4);
        Paint.FontMetrics metrics = mSchemeBasicPaint.getFontMetrics();
        mSchemeBaseLine = mRadio - metrics.descent + (metrics.bottom - metrics.top) / 2 + ConvertUtils.dp2px(1);
    }

    public static com.haibin.calendarview.Calendar getCalendarView(long goTime) {
        Calendar calendar = DateUtils.getCalendar(TimeHelper.getJavaTimeByGo(goTime));
        com.haibin.calendarview.Calendar c = new com.haibin.calendarview.Calendar();
        c.setYear(calendar.get(Calendar.YEAR));
        c.setMonth(calendar.get(Calendar.MONTH) + 1);
        c.setDay(calendar.get(Calendar.DAY_OF_MONTH));
        return c;
    }

    /**
     * 绘制选中的日子
     *
     * @param canvas    canvas
     * @param calendar  日历日历calendar
     * @param x         日历Card x起点坐标
     * @param y         日历Card y起点坐标
     * @param hasScheme hasScheme 非标记的日期
     * @return 返回true 则绘制onDrawScheme，因为这里背景色不是是互斥的，所以返回true
     */
    @Override
    protected boolean onDrawSelected(Canvas canvas, com.haibin.calendarview.Calendar calendar, int x, int y, boolean hasScheme) {
        // 这里绘制选中的日子样式，看需求需不需要继续调用onDrawScheme
        mSelectedPaint.setStyle(Paint.Style.FILL);
        // 不要矩形
        //canvas.drawRect(x + mPadding, y + mPadding, x + mItemWidth - mPadding, y + mItemHeight - mPadding, mSelectedPaint);
        canvas.drawCircle(x + mItemWidth / 2, y + (mItemWidth - mPadding) / 2, (mItemHeight - mPadding) / 2, mSelectedPaint);
        return true;
    }

    /**
     * 绘制标记的事件日子
     *
     * @param canvas   canvas
     * @param calendar 日历calendar
     * @param x        日历Card x起点坐标
     * @param y        日历Card y起点坐标
     */
    @Override
    protected void onDrawScheme(Canvas canvas, com.haibin.calendarview.Calendar calendar, int x, int y) {
        // 这里绘制标记的日期样式，想怎么操作就怎么操作
        mSchemeBasicPaint.setColor(calendar.getSchemeColor());
        // 绘制圆点
        canvas.drawCircle(x + mItemWidth - mPadding - mRadio / 2, y + mPadding + mRadio, mRadio, mSchemeBasicPaint);
        // 绘制文字
        canvas.drawText(calendar.getScheme(),
                x + mItemWidth - mPadding - mRadio / 2 - getTextWidth(calendar.getScheme()) / 2,
                y + mPadding + mSchemeBaseLine, mTextPaint);
    }

    /**
     * 获取字体的宽
     *
     * @param text text
     * @return return
     */
    private float getTextWidth(String text) {
        return mTextPaint.measureText(text);
    }

    /**
     * 绘制文本
     *
     * @param canvas     canvas
     * @param calendar   日历calendar
     * @param x          日历Card x起点坐标
     * @param y          日历Card y起点坐标
     * @param hasScheme  是否是标记的日期
     * @param isSelected 是否选中
     */
    @Override
    protected void onDrawText(Canvas canvas, com.haibin.calendarview.Calendar calendar, int x, int y, boolean hasScheme, boolean isSelected) {
        // 这里绘制文本，不要再问我怎么隐藏农历了，不要再问我怎么把某个日期换成特殊字符串了，要怎么显示你就在这里怎么画，你不画就不显示，是看你想怎么显示日历的，而不是看框架
        int cx = x + mItemWidth / 2;
        int top = y - mItemHeight / 6;
        if (isSelected) {
            // 绘制选中样式
            canvas.drawText(String.valueOf(calendar.getDay()), cx, mTextBaseLine + top,
                    mSelectTextPaint);
            canvas.drawText(calendar.getLunar(), cx, mTextBaseLine + y + mItemHeight / 10, mSelectedLunarTextPaint);
        } else if (hasScheme) {
            canvas.drawText(String.valueOf(calendar.getDay()), cx, mTextBaseLine + top,
                    calendar.isCurrentDay() ? mCurDayTextPaint :
                            calendar.isCurrentMonth() ? mSchemeTextPaint : mOtherMonthTextPaint);
            canvas.drawText(calendar.getLunar(), cx, mTextBaseLine + y + mItemHeight / 10,
                    calendar.isCurrentDay() ? mCurDayLunarTextPaint : mSchemeLunarTextPaint);
        } else {
            // 绘制未选中样式
            canvas.drawText(String.valueOf(calendar.getDay()), cx, mTextBaseLine + top,
                    calendar.isCurrentDay() ? mCurDayTextPaint :
                            calendar.isCurrentMonth() ? mCurMonthTextPaint : mOtherMonthTextPaint);
            canvas.drawText(calendar.getLunar(), cx, mTextBaseLine + y + mItemHeight / 10,
                    calendar.isCurrentDay() ? mCurDayLunarTextPaint :
                            calendar.isCurrentMonth() ? mCurMonthLunarTextPaint : mOtherMonthLunarTextPaint);
        }
    }
}
