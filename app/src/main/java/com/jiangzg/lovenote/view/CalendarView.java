package com.jiangzg.lovenote.view;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.jiangzg.base.time.DateUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.domain.Menses;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.Calendar;
import java.util.List;

/**
 * Created by JZG on 2018/8/6.
 * CalendarView
 */
public class CalendarView {

    // initMonthView
    public static void initMonthView(Activity activity, MaterialCalendarView view, Calendar current) {
        if (view == null) return;
        // text
        view.setHeaderTextAppearance(R.style.FontWhiteHugeBold);
        view.setWeekDayTextAppearance(R.style.FontWhiteSmallBold);
        view.setDateTextAppearance(R.style.FontWhiteNormalBold);
        view.setWeekDayLabels(R.array.week_label);
        view.setTitleMonths(R.array.month_label);
        // decorator
        view.removeDecorators();
        view.addDecorator(new PointDecorator(ContextCompat.getColor(activity, R.color.white), current));
        //view.invalidateDecorators();
        // selection
        view.clearSelection();
        view.setSelected(false);
        view.setAllowClickDaysOutsideCurrentMonth(false);
        //Date currentDate = DateUtils.getCurrentDate();
        //view.clearSelection();
        //view.setSelectionColor(ContextCompat.getColor(activity, ViewHelper.getColorDark(activity)));
        //view.setSelectedDate(currentDate);
        // arrow
        view.setLeftArrowMask(null);
        view.setRightArrowMask(null);
        // other
        view.setCurrentDate(current);
        view.setShowOtherDates(MaterialCalendarView.SHOW_OUT_OF_RANGE);
        view.setPagingEnabled(true);
        view.setAllowClickDaysOutsideCurrentMonth(false);
        view.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                //.setMaximumDate(DateUtils.getCurrentDate())
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();
    }

    // 点点视图
    public static class PointDecorator implements DayViewDecorator {

        private int color;
        private Calendar calendar;

        PointDecorator(int color, Calendar calendar) {
            this.color = color;
            this.calendar = calendar;
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            Calendar cal = day.getCalendar();
            return (calendar != null && calendar.get(Calendar.DAY_OF_YEAR) == cal.get(Calendar.DAY_OF_YEAR));
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new DotSpan(8, color));
        }
    }

    // 点击视图(不加会有默认的accent圆圈)
    public static class ClickDecorator implements DayViewDecorator {

        private Calendar click;
        private Drawable drawable;

        public ClickDecorator(Activity activity, Calendar click) {
            drawable = ContextCompat.getDrawable(activity, R.drawable.ic_circle_primary_dark);
            this.click = click;
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            Calendar cal = day.getCalendar();
            return (click != null && click.get(Calendar.DAY_OF_YEAR) == cal.get(Calendar.DAY_OF_YEAR));
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.setSelectionDrawable(drawable);
        }

        public void setClick(Calendar click) {
            this.click = click;
        }
    }

    // 选中视图
    public static class SelectedDecorator implements DayViewDecorator {

        private List<Calendar> selectedList;
        private Drawable drawable;

        public SelectedDecorator(Activity activity, List<Calendar> selectedList) {
            drawable = ContextCompat.getDrawable(activity, R.drawable.ic_heart_solid_primary);
            this.selectedList = selectedList;
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            Calendar cal = day.getCalendar();
            int fayOfYear = cal.get(Calendar.DAY_OF_YEAR);
            if (selectedList != null && selectedList.size() > 0) {
                for (Calendar date : selectedList) {
                    if (date.get(Calendar.DAY_OF_YEAR) == fayOfYear) {
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.setSelectionDrawable(drawable);
        }

        public void setSelectedList(List<Calendar> selectedList) {
            this.selectedList = selectedList;
        }
    }

    // 选中视图(姨妈)
    public static class MensesDecorator implements DayViewDecorator {

        private List<Menses> mensesList;
        private Drawable drawable;

        public MensesDecorator(Activity activity, List<Menses> mensesList) {
            drawable = ContextCompat.getDrawable(activity, R.drawable.ic_heart_solid_primary);
            this.mensesList = mensesList;
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            Calendar cal = day.getCalendar();
            Calendar calendar = DateUtils.getCurrentCalendar();
            if (cal.get(Calendar.DAY_OF_YEAR) > calendar.get(Calendar.DAY_OF_YEAR) && cal.get(Calendar.YEAR) >= calendar.get(Calendar.YEAR)) {
                // 没过的日子
                return false;
            }
            if (mensesList != null && mensesList.size() > 0) {
                int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
                // 检查开始状态
                Menses first = mensesList.get(0);
                boolean isBlood = !first.isStart();
                // 循环mensesList(升序的)，防止一天多次
                for (int i = 0; i < mensesList.size(); i++) {
                    Menses menses = mensesList.get(i);
                    if (menses == null) continue;
                    if (menses.getDayOfMonth() < dayOfMonth) {
                        // 之前的天数
                        isBlood = menses.isStart();
                    } else if (menses.getDayOfMonth() == dayOfMonth) {
                        // 当天有情况，不管来还是去，都blood
                        isBlood = true;
                        break;
                    } else {
                        // 之后的不管，依照之前的来做
                        break;
                    }
                }
                return isBlood;
            }
            return false;
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.setSelectionDrawable(drawable);
        }

        public void setMensesList(List<Menses> mensesList) {
            this.mensesList = mensesList;
        }
    }

}
