package com.example.note.justdo.ncalendar.calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.widget.Toast;

import com.example.note.justdo.R;
import com.example.note.justdo.ncalendar.adapter.CalendarAdapter;
import com.example.note.justdo.ncalendar.adapter.MonthAdapter;
import com.example.note.justdo.ncalendar.listener.OnClickMonthViewListener;
import com.example.note.justdo.ncalendar.listener.OnMonthCalendarChangedListener;
import com.example.note.justdo.ncalendar.utils.Utils;
import com.example.note.justdo.ncalendar.view.CalendarView;
import com.example.note.justdo.ncalendar.view.MonthView;

import org.joda.time.LocalDate;

/**
 * Created by necer on 2017/8/28.
 */

public class MonthCalendar extends CalendarPager implements OnClickMonthViewListener {
    private Boolean isDateClicked=false;
    private OnMonthCalendarChangedListener onMonthCalendarChangedListener;
    private int lastPosition = -1;
    private Boolean IsTimeSelected=false;
    private Boolean isTimeBeforecurrent=false;
    public MonthCalendar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected CalendarAdapter getCalendarAdapter() {

        mPageSize = Utils.getIntervalMonths(startDate, endDate) + 1;
        mCurrPage = Utils.getIntervalMonths(startDate, mInitialDate);

        return new MonthAdapter(getContext(), mPageSize, mCurrPage, mInitialDate, this);
    }

    public Boolean getDateClicked() {
        return isDateClicked;
    }

    public void setDateClicked(Boolean dateClicked) {
        isDateClicked = dateClicked;
    }

    @Override
    protected void initCurrentCalendarView(int position) {

        MonthView currView = (MonthView) calendarAdapter.getCalendarViews().get(position);
        MonthView lastView = (MonthView) calendarAdapter.getCalendarViews().get(position - 1);
        MonthView nextView = (MonthView) calendarAdapter.getCalendarViews().get(position + 1);


        if (currView == null) {
            return;
        }

        if (lastView != null)
            lastView.clear();

        if (nextView != null)
            nextView.clear();


        //只处理翻页
        if (lastPosition == -1) {
            currView.setDateAndPoint(mInitialDate, pointList);
            mSelectDate = mInitialDate;
            lastSelectDate = mInitialDate;
            if (onMonthCalendarChangedListener != null) {
                onMonthCalendarChangedListener.onMonthCalendarChanged(mSelectDate);
            }
        } else if (isPagerChanged) {
            int i = position - lastPosition;
            mSelectDate = mSelectDate.plusMonths(i);

            if (isDefaultSelect) {
                //日期越界
                if (mSelectDate.isAfter(endDate)) {
                    mSelectDate = endDate;
                } else if (mSelectDate.isBefore(startDate)) {
                    mSelectDate= startDate;
                }
                currView.setTimeBeforeCurrent(isTimeBeforecurrent);
                currView.setTimeSelected(IsTimeSelected);
                currView.setDateAndPoint(mSelectDate, pointList);
         //       currView.setDateAndPoint(mSelectDate, pointList);
               if (onMonthCalendarChangedListener != null) {
                    onMonthCalendarChangedListener.onMonthCalendarChanged(mSelectDate);
               }
            } else {
                if (Utils.isEqualsMonth(lastSelectDate, mSelectDate)) {
                    currView.setDateAndPoint(lastSelectDate, pointList);
                }
            }

        }
        lastPosition = position;
    }

    public void setOnMonthCalendarChangedListener(OnMonthCalendarChangedListener onMonthCalendarChangedListener) {
        this.onMonthCalendarChangedListener = onMonthCalendarChangedListener;
    }

    @Override
    protected void setDate(LocalDate date) {
        if (date.isAfter(endDate)  || date.isBefore(startDate)) {
            Toast.makeText(getContext(), R.string.illegal_date, Toast.LENGTH_SHORT).show();
            return;
        }

        SparseArray<CalendarView> calendarViews = calendarAdapter.getCalendarViews();
        if (calendarViews.size() == 0) {
            return;
        }

        isPagerChanged = false;

        MonthView currectMonthView = getCurrentMonthView();
        LocalDate initialDate = currectMonthView.getInitialDate();

        //不是当月
        if (!Utils.isEqualsMonth(initialDate, date)) {
            int months = Utils.getIntervalMonths(initialDate, date);
            int i = getCurrentItem() + months;
            setCurrentItem(i, Math.abs(months) < 2);
            currectMonthView = getCurrentMonthView();
        }
        currectMonthView.setTimeBeforeCurrent(isTimeBeforecurrent);
        currectMonthView.setTimeSelected(IsTimeSelected);
        currectMonthView.setDateAndPoint(date, pointList);

        mSelectDate = date;
        lastSelectDate = date;

        isPagerChanged = true;

        if (onMonthCalendarChangedListener != null) {
            onMonthCalendarChangedListener.onMonthCalendarChanged(mSelectDate);
        }


    }

    @Override
    public void onClickCurrentMonth(LocalDate date) {
        setDateClicked(true);
        dealClickEvent(date, getCurrentItem());
    }

    @Override
    public void onClickLastMonth(LocalDate date) {
        int currentItem = getCurrentItem() - 1;
        setDateClicked(true);
        dealClickEvent(date, currentItem);
    }

    @Override
    public void onClickNextMonth(LocalDate date) {
        int currentItem = getCurrentItem() + 1;
        setDateClicked(true);
        dealClickEvent(date, currentItem);
    }

    private void dealClickEvent(LocalDate date, int currentItem) {
        if (date.isAfter(endDate)  || date.isBefore(startDate)) {
            Toast.makeText(getContext(), R.string.illegal_date, Toast.LENGTH_SHORT).show();
            return;
        }
        isPagerChanged = false;
        setCurrentItem(currentItem, true);
        MonthView nMonthView = getCurrentMonthView();
        nMonthView.setTimeBeforeCurrent(isTimeBeforecurrent);
        nMonthView.setTimeSelected(IsTimeSelected);
        nMonthView.setDateAndPoint(date, pointList);
        mSelectDate = date;
        lastSelectDate = date;
        isDateClicked=true;
        isPagerChanged = true;
        if (onMonthCalendarChangedListener != null) {
            onMonthCalendarChangedListener.onMonthCalendarChanged(date);
        }
    }


    public MonthView getCurrentMonthView() {
        return (MonthView) calendarAdapter.getCalendarViews().get(getCurrentItem());
    }
    public void setIsTimeSelected(Boolean isTimeSelected){
        this.IsTimeSelected=isTimeSelected;
        getCurrentMonthView().setTimeSelected(isTimeSelected);
        getCurrentMonthView().invalidate();
    }
    public void setIsTimeBeforecurrent(Boolean isTimeBeforecurrent){
        this.isTimeBeforecurrent=isTimeBeforecurrent;
        getCurrentMonthView().setTimeBeforeCurrent(isTimeBeforecurrent);
        getCurrentMonthView().invalidate();
    }
    public Boolean getIsTimeSelected(){
        return this.IsTimeSelected;
    }
    public Boolean getIsTimeBeforecurrent(){
        return this.isTimeBeforecurrent;
    }
    //此方法用于返回最终要被标记的日期列表
/*    public List<String> getPointList(int type, int interval, long startdaymills){
        Boolean stopped=false;
        List<String> pointList=new ArrayList<>();
        Date currentdate=new Date();
        Date startdate=new Date(startdaymills);
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
        finalcalendar=new GregorianCalendar(2099,11,31);
        Calendar currentcalendar=Calendar.getInstance();
        Calendar cusorCal;
        currentcalendar.setTime(startdate);
        int startYear=currentcalendar.get(Calendar.YEAR);
        int startMonth=currentcalendar.get(Calendar.MONTH);
        int startDay=currentcalendar.get(Calendar.DATE);
        // Date finaldata=calendar.getTime();
        switch (type){
            case Year:if(startdate.getTime()<currentdate.getTime()){
                while (startdate.getTime()<currentdate.getTime()){
                    startYear=startYear+interval;
                    cusorCal=new GregorianCalendar(startYear,startMonth,startDay);
                    startdate.setTime(cusorCal.getTimeInMillis());
                }
            }
                while (!stopped){
                    if(startdate.getTime()>finalcalendar.getTimeInMillis()){
                        break;
                    }
                    pointList.add(format.format(startdate));
                    startYear=startYear+interval;
                    cusorCal=new GregorianCalendar(startYear,startMonth,startDay);
                    startdate.setTime(cusorCal.getTimeInMillis());
                }
                break;
            case Month:if(startdate.getTime()<currentdate.getTime()){
                while (startdate.getTime()<currentdate.getTime()){
                    startMonth=startMonth+interval;
                    if(startMonth>11){
                        startYear=startYear+startMonth/12;
                        startMonth=startMonth%12;
                    }
                    cusorCal=new GregorianCalendar(startYear,startMonth,startDay);
                    startdate.setTime(cusorCal.getTimeInMillis());
                }
            }
                while (!stopped){
                    if(startdate.getTime()>finalcalendar.getTimeInMillis()){
                        break;
                    }
                    pointList.add(format.format(startdate));
                    startMonth=startMonth+interval;
                    if(startMonth>11){
                        startYear=startYear+startMonth/12;
                        startMonth=startMonth%12;
                    }
                    cusorCal=new GregorianCalendar(startYear,startMonth,startDay);
                    startdate.setTime(cusorCal.getTimeInMillis());
                }
                break;
            case Day:
                if(startdate.getTime()<currentdate.getTime()){
                    while (startdate.getTime()<currentdate.getTime()){
                        startdate.setTime(startdate.getTime()+interval*1000*24*60*60);
                    }
                }
                while (!stopped){
                    if(startdate.getTime()>finalcalendar.getTimeInMillis()){
                        break;
                    }
                    pointList.add(format.format(startdate));
                    startdate.setTime(startdate.getTime()+interval*1000*24*60*60);
                }
                break;
        }
        return pointList;
    }
*/
}
