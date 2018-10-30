package com.example.note.justdo.ncalendar.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.example.note.justdo.ncalendar.listener.OnClickWeekViewListener;
import com.example.note.justdo.ncalendar.view.WeekView;

import org.joda.time.LocalDate;

/**
 * Created by necer on 2017/8/30.
 */

public class WeekAdapter extends CalendarAdapter {

    private OnClickWeekViewListener mOnClickWeekViewListener;

    public WeekAdapter(Context mContext, int count, int curr, LocalDate date, OnClickWeekViewListener onClickWeekViewListener) {
        super(mContext, count, curr, date);
        this.mOnClickWeekViewListener = onClickWeekViewListener;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        WeekView nWeekView = (WeekView) mCalendarViews.get(position);
        if (nWeekView == null) {
            nWeekView = new WeekView(mContext, mDate.plusDays((position - mCurr) * 7),mOnClickWeekViewListener);
            mCalendarViews.put(position, nWeekView);
        }
        container.addView(mCalendarViews.get(position));
        return mCalendarViews.get(position);
    }
}
