package com.example.note.justdo.ncalendar.calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.widget.Toast;

import com.example.note.justdo.R;
import com.example.note.justdo.ncalendar.adapter.CalendarAdapter;
import com.example.note.justdo.ncalendar.adapter.WeekAdapter;
import com.example.note.justdo.ncalendar.listener.OnClickWeekViewListener;
import com.example.note.justdo.ncalendar.listener.OnWeekCalendarChangedListener;
import com.example.note.justdo.ncalendar.utils.Attrs;
import com.example.note.justdo.ncalendar.utils.Utils;
import com.example.note.justdo.ncalendar.view.CalendarView;
import com.example.note.justdo.ncalendar.view.WeekView;

import org.joda.time.LocalDate;

/**
 * Created by necer on 2017/8/30.
 */

public class WeekCalendar extends CalendarPager implements OnClickWeekViewListener {

    private OnWeekCalendarChangedListener onWeekCalendarChangedListener;
    private Boolean IsTimeSelected=false;
    private Boolean isTimeBeforecurrent=false;
    private Boolean IsDateClicked=false;

    public WeekCalendar(Context context) {
        super(context);
    }

    public WeekCalendar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected CalendarAdapter getCalendarAdapter() {

        mPageSize = Utils.getIntervalWeek(startDate, endDate, Attrs.firstDayOfWeek) + 1;
        mCurrPage = Utils.getIntervalWeek(startDate, mInitialDate, Attrs.firstDayOfWeek);

        return new WeekAdapter(getContext(), mPageSize, mCurrPage, mInitialDate, this);
    }


    private int lastPosition = -1;

    @Override
    protected void initCurrentCalendarView(int position) {

        WeekView currView = (WeekView) calendarAdapter.getCalendarViews().get(position);
        WeekView lastView = (WeekView) calendarAdapter.getCalendarViews().get(position - 1);
        WeekView nextView = (WeekView) calendarAdapter.getCalendarViews().get(position + 1);
        if (currView == null)
            return;

        if (lastView != null)
            lastView.clear();

        if (nextView != null)
            nextView.clear();

        //只处理翻页
        if (lastPosition == -1) {
            currView.setDateAndPoint(mInitialDate, pointList);
            mSelectDate = mInitialDate;
            lastSelectDate = mInitialDate;
            if (onWeekCalendarChangedListener != null) {
                onWeekCalendarChangedListener.onWeekCalendarChanged(mSelectDate);
            }
        } else if (isPagerChanged) {
            int i = position - lastPosition;
            mSelectDate = mSelectDate.plusWeeks(i);

            if (isDefaultSelect) {
                //日期越界
                if (mSelectDate.isAfter(endDate)) {
                    mSelectDate = endDate;
                } else if (mSelectDate.isBefore(startDate)) {
                    mSelectDate = startDate;
                }
                currView.setTimeBeforeCurrent(isTimeBeforecurrent);
                currView.setTimeSelected(IsTimeSelected);
                currView.setDateAndPoint(mSelectDate, pointList);
                if (onWeekCalendarChangedListener != null) {
                    onWeekCalendarChangedListener.onWeekCalendarChanged(mSelectDate);
                }
            } else {
                if (Utils.isEqualsMonth(lastSelectDate, mSelectDate)) {
                    currView.setDateAndPoint(lastSelectDate, pointList);
                }
            }

        }
        lastPosition = position;
    }

    public void setOnWeekCalendarChangedListener(OnWeekCalendarChangedListener onWeekCalendarChangedListener) {
        this.onWeekCalendarChangedListener = onWeekCalendarChangedListener;
    }


    @Override
    protected void setDate(LocalDate date) {

        if (date.isAfter(endDate) || date.isBefore(startDate)) {
            Toast.makeText(getContext(), R.string.illegal_date, Toast.LENGTH_SHORT).show();
            return;
        }

        SparseArray<CalendarView> calendarViews = calendarAdapter.getCalendarViews();
        if (calendarViews.size() == 0) {
            return;
        }

        isPagerChanged = false;

        WeekView currentWeekView = (WeekView) calendarViews.get(getCurrentItem());

        //不是当周
        if (!currentWeekView.contains(date)) {

            LocalDate initialDate = currentWeekView.getInitialDate();
            int weeks = Utils.getIntervalWeek(initialDate, date, Attrs.firstDayOfWeek);
            int i = getCurrentItem() + weeks;
            setCurrentItem(i, Math.abs(weeks) < 2);
            currentWeekView = (WeekView) calendarViews.get(getCurrentItem());
            currentWeekView.setTimeBeforeCurrent(isTimeBeforecurrent);
            currentWeekView.setTimeSelected(IsTimeSelected);
        }

        currentWeekView.setDateAndPoint(date, pointList);

        mSelectDate = date;
        lastSelectDate = date;

        isPagerChanged = true;

        if (onWeekCalendarChangedListener != null) {
            onWeekCalendarChangedListener.onWeekCalendarChanged(mSelectDate);
        }
    }
    public WeekView getCurrentWeekView() {
        return (WeekView) calendarAdapter.getCalendarViews().get(getCurrentItem());
    }


    @Override
    public void onClickCurrentWeek(LocalDate date) {

        if (date.isAfter(endDate) || date.isBefore(startDate)) {
            Toast.makeText(getContext(), R.string.illegal_date, Toast.LENGTH_SHORT).show();
            return;
        }

        WeekView weekView = (WeekView) calendarAdapter.getCalendarViews().get(getCurrentItem());
        weekView.setDateAndPoint(date, pointList);
        mSelectDate = date;
        lastSelectDate = date;
        this.IsDateClicked=true;
        if (onWeekCalendarChangedListener != null) {
            onWeekCalendarChangedListener.onWeekCalendarChanged(date);
        }

    }
    public void setIsTimeSelected(Boolean isTimeSelected){
        this.IsTimeSelected=isTimeSelected;
        getCurrentWeekView().setTimeSelected(isTimeSelected);
        getCurrentWeekView().invalidate();
    }
    public void setIsTimeBeforecurrent(Boolean isTimeBeforecurrent){
        this.isTimeBeforecurrent=isTimeBeforecurrent;
        getCurrentWeekView().setTimeBeforeCurrent(isTimeBeforecurrent);
        getCurrentWeekView().invalidate();
    }
    public Boolean getIsTimeSelected(){
        return this.IsTimeSelected;
    }
    public Boolean getIsTimeBeforecurrent(){
        return this.isTimeBeforecurrent;
    }
    public void setDateClicked(Boolean dateClicked){
        this.IsDateClicked=dateClicked;
    }
    public Boolean getDateClicked(){
        return this.IsDateClicked;
    }
}
