package com.example.note.justdo.ncalendar.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.example.note.justdo.ncalendar.listener.OnClickMonthViewListener;
import com.example.note.justdo.ncalendar.view.MonthView;

import org.joda.time.LocalDate;

/**
 * Created by necer on 2017/8/28.
 */

public class MonthAdapter extends CalendarAdapter {

    private OnClickMonthViewListener mOnClickMonthViewListener;

    public MonthAdapter(Context mContext, int count, int curr, LocalDate date, OnClickMonthViewListener onClickMonthViewListener) {
        super(mContext, count, curr, date);
        this.mOnClickMonthViewListener = onClickMonthViewListener;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        MonthView nMonthView = (MonthView) mCalendarViews.get(position);
        if (nMonthView == null) {
            int i = position - mCurr;
            LocalDate date = this.mDate.plusMonths(i);
            nMonthView = new MonthView(mContext, date, mOnClickMonthViewListener);
            mCalendarViews.put(position, nMonthView);
        }
        container.addView(nMonthView);
        return nMonthView;
    }
}
