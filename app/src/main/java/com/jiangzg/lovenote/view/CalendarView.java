package com.jiangzg.lovenote.view;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.jiangzg.lovenote.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Created by JZG on 2018/8/6.
 * CalendarView
 */
public class CalendarView {

    // initMonthView
    public static void initMonthView(Activity activity, MaterialCalendarView view) {
        if (view == null) return;
        // text
        view.setHeaderTextAppearance(R.style.FontWhiteBig);
        view.setWeekDayTextAppearance(R.style.FontWhiteSmall);
        view.setDateTextAppearance(R.style.FontWhiteNormal);
        view.setWeekDayLabels(R.array.week_label);
        view.setTitleMonths(R.array.month_label);
        // decorator
        view.removeDecorators();
        //view.invalidateDecorators();
        // selection
        view.clearSelection();
        view.setSelected(false);
        view.setAllowClickDaysOutsideCurrentMonth(false);
        //int colorDark = ContextCompat.getColor(activity, ViewHelper.getColorDark(activity));
        //Date currentDate = DateUtils.getCurrentDate();
        //view.clearSelection();
        //view.setSelectionColor(colorDark);
        //view.setSelectedDate(currentDate);
        //view.setCurrentDate(currentDate);
        // arrow
        view.setLeftArrowMask(null);
        view.setRightArrowMask(null);
        // other
        view.setShowOtherDates(MaterialCalendarView.SHOW_OUT_OF_RANGE);
        view.setPagingEnabled(true);
        view.setAllowClickDaysOutsideCurrentMonth(false);
        view.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                //.setMaximumDate(DateUtils.getCurrentDate())
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();
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

        public List<Calendar> getSelectedList() {
            return selectedList;
        }

        public void setSelectedList(List<Calendar> selectedList) {
            this.selectedList = selectedList;
        }

    }

    // 点击视图
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

        public Calendar getClick() {
            return click;
        }

        public void setClick(Calendar click) {
            this.click = click;
        }

    }

    // 红点视图
    public class EventDecorator implements DayViewDecorator {

        private int color;
        private HashSet<CalendarDay> dates;

        public EventDecorator(int color, Collection<CalendarDay> dates) {
            this.color = color;
            this.dates = new HashSet<>(dates);
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return dates.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new DotSpan(5, color));
        }
    }

}
