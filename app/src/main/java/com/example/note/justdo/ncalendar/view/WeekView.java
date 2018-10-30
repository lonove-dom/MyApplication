package com.example.note.justdo.ncalendar.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.example.note.justdo.ncalendar.listener.OnClickWeekViewListener;
import com.example.note.justdo.ncalendar.utils.Attrs;
import com.example.note.justdo.ncalendar.utils.Utils;

import org.joda.time.LocalDate;

import java.util.List;


/**
 * Created by necer on 2017/8/25.
 */

public class WeekView extends CalendarView {


    private OnClickWeekViewListener mOnClickWeekViewListener;
    private List<String> lunarList;
    private Boolean isTimeSelected=false;
    private Boolean isTimeBeforeCurrent=false;

    public WeekView(Context context, LocalDate date, OnClickWeekViewListener onClickWeekViewListener) {
        super(context);

        this.mInitialDate = date;
        Utils.NCalendar weekCalendar2 = Utils.getWeekCalendar2(date, Attrs.firstDayOfWeek);

        dates = weekCalendar2.dateList;
        lunarList = weekCalendar2.lunarList;
        mOnClickWeekViewListener = onClickWeekViewListener;
    }
    public Boolean getTimeSelected() {
        return isTimeSelected;
    }

    public void setTimeSelected(Boolean timeSelected) {
        isTimeSelected = timeSelected;
    }

    public Boolean getTimeBeforeCurrent() {
        return isTimeBeforeCurrent;
    }

    public void setTimeBeforeCurrent(Boolean timeBeforeCurrent) {
        this.isTimeBeforeCurrent = timeBeforeCurrent;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mWidth = getWidth();
        //mHeight = getHeight();
        //为了与月日历保持一致，往上压缩一下,5倍的关系
        mHeight = (int) (getHeight() - Utils.dp2px(getContext(), 2));
        mRectList.clear();

        for (int i = 0; i < 7; i++) {
            Rect rect = new Rect(i * mWidth / 7, 0, i * mWidth / 7 + mWidth / 7, mHeight);
            mRectList.add(rect);
            LocalDate date = dates.get(i);
            Paint.FontMetricsInt fontMetrics = mSorlarPaint.getFontMetricsInt();
            int baseline = (rect.bottom + rect.top - fontMetrics.bottom - fontMetrics.top) / 2;
            if (Utils.isToday(date)) {
            mLunarPaint.setColor(mSelectCircleColor);
            canvas.drawCircle(rect.centerX(), baseline - getHeight() / 3, mPointSize, mLunarPaint);
            }
                drawPoint(canvas, rect, date, baseline);
                //       canvas.drawText(date.getDayOfMonth() + "", rect.centerX(), baseline, mSorlarPaint);
                drawLunar(canvas, rect, baseline,i);
                //绘制节假日
                drawHolidays(canvas, rect, date, baseline);
                //绘制圆点
                mSorlarPaint.setColor(mSolarTextColor);
                canvas.drawText(date.getDayOfMonth() + "", rect.centerX(), baseline, mSorlarPaint);
            }
        }


    private void drawLunar(Canvas canvas, Rect rect, int baseline, int i) {
        if (isShowLunar) {
            mLunarPaint.setColor(mLunarTextColor);
            String lunar = lunarList.get(i);
            canvas.drawText(lunar, rect.centerX(), baseline + getHeight() / 4, mLunarPaint);
        }
    }


    private void drawHolidays(Canvas canvas, Rect rect, LocalDate date, int baseline) {
        if (isShowHoliday) {
            if (holidayList.contains(date.toString())) {
                mLunarPaint.setColor(mHolidayColor);
                canvas.drawText("休", rect.centerX() + rect.width() / 4, baseline - getHeight() / 4, mLunarPaint);

            } else if (workdayList.contains(date.toString())) {
                mLunarPaint.setColor(mWorkdayColor);
                canvas.drawText("班", rect.centerX() + rect.width() / 4, baseline - getHeight() / 4, mLunarPaint);
            }
        }
    }
    public void drawPoint(Canvas canvas, Rect rect, LocalDate date, int baseline) {
        int centerY=rect.centerY();
        if (pointList != null && pointList.contains(date.toString())) {
            //过去的事件全部标记为红色
            if(date.toDateTimeAtStartOfDay().toDate().getTime()<(new LocalDate().toDateTimeAtStartOfDay().toDate().getTime())){
                mSorlarPaint.setColor(PastTimeCircleColor);
                canvas.drawCircle(rect.centerX(), centerY, mSelectCircleRadius-13, mSorlarPaint);
                mSorlarPaint.setColor(mHollowCircleColor);
                canvas.drawCircle(rect.centerX(), centerY, mSelectCircleRadius - mHollowCircleStroke-13, mSorlarPaint);}
            else {
                //今天及今后的日期，如果还未使用滚轮选择时间，先标记为灰色
                if(!getTimeSelected()){
                    mSorlarPaint.setColor(TimeNotSelectCircleColor);
                    canvas.drawCircle(rect.centerX(), centerY, mSelectCircleRadius-13, mSorlarPaint);
                    mSorlarPaint.setColor(mHollowCircleColor);
                    canvas.drawCircle(rect.centerX(), centerY, mSelectCircleRadius - mHollowCircleStroke-13, mSorlarPaint);
                }
                //选择时间后
                else {
                    //，对于今天，如果选择的时间在当前时间之前，则标记为过去的红色
                    if(Utils.isToday(date)&&getTimeBeforeCurrent()){
                        mSorlarPaint.setColor(PastTimeCircleColor);
                        canvas.drawCircle(rect.centerX(), centerY, mSelectCircleRadius-13, mSorlarPaint);
                        mSorlarPaint.setColor(mHollowCircleColor);
                        canvas.drawCircle(rect.centerX(), centerY, mSelectCircleRadius - mHollowCircleStroke-13, mSorlarPaint);
                        return;
                    }
                    //否则，标记为未来时绿色
                    mSorlarPaint.setColor(mSelectCircleColor);
                    canvas.drawCircle(rect.centerX(), centerY, mSelectCircleRadius-13, mSorlarPaint);
                    mSorlarPaint.setColor(mHollowCircleColor);
                    canvas.drawCircle(rect.centerX(), centerY, mSelectCircleRadius - mHollowCircleStroke-13, mSorlarPaint);
                }
            }
        }
        //        mLunarPaint.setColor(mPointColor);
        //        canvas.drawCircle(rect.centerX(), baseline - getMonthHeight() / 15, mPointSize, mLunarPaint);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    private GestureDetector mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            for (int i = 0; i < mRectList.size(); i++) {
                Rect rect = mRectList.get(i);
                if (rect.contains((int) e.getX(), (int) e.getY())) {
                    LocalDate selectDate = dates.get(i);
                    mOnClickWeekViewListener.onClickCurrentWeek(selectDate);
                    break;
                }
            }
            return true;
        }
    });


    public boolean contains(LocalDate date) {
        return dates.contains(date);
    }
}
