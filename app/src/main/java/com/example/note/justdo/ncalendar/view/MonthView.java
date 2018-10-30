package com.example.note.justdo.ncalendar.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.example.note.justdo.ncalendar.listener.OnClickMonthViewListener;
import com.example.note.justdo.ncalendar.utils.Attrs;
import com.example.note.justdo.ncalendar.utils.Utils;

import org.joda.time.LocalDate;

import java.util.List;


/**
 * Created by necer on 2017/8/25.
 */

public class MonthView extends CalendarView {

    private List<String> lunarList;
    private int mRowNum;
    private OnClickMonthViewListener mOnClickMonthViewListener;
    private Boolean isTimeSelected=false;
    private Boolean isTimeBeforeCurrent=false;

    public MonthView(Context context, LocalDate date, OnClickMonthViewListener onClickMonthViewListener) {
        super(context);
        this.mInitialDate = date;

        //0周日，1周一
        Utils.NCalendar nCalendar2 = Utils.getMonthCalendar2(date, Attrs.firstDayOfWeek);
        mOnClickMonthViewListener = onClickMonthViewListener;

        lunarList = nCalendar2.lunarList;
        dates = nCalendar2.dateList;

        mRowNum = dates.size() / 7;
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
        //绘制高度
        mHeight = getDrawHeight();
        mRectList.clear();
        for (int i = 0; i < mRowNum; i++) {
            for (int j = 0; j < 7; j++) {
                Rect rect = new Rect(j * mWidth / 7, i * mHeight / mRowNum, j * mWidth / 7 + mWidth / 7, i * mHeight / mRowNum + mHeight / mRowNum);
                mRectList.add(rect);
                LocalDate date = dates.get(i * 7 + j);
                Paint.FontMetricsInt fontMetrics = mSorlarPaint.getFontMetricsInt();

                int baseline;//让6行的第一行和5行的第一行在同一直线上，处理选中第一行的滑动
                if (mRowNum == 5) {
                    baseline = (rect.bottom + rect.top - fontMetrics.bottom - fontMetrics.top) / 2;
                } else {
                    baseline = (rect.bottom + rect.top - fontMetrics.bottom - fontMetrics.top) / 2 + (mHeight / 5 - mHeight / 6) / 2;
                }

                //当月和上下月的颜色不同
                if (Utils.isEqualsMonth(date, mInitialDate)) {
                    //当天和选中的日期不绘制农历
                   if (Utils.isToday(date)) {
                       mLunarPaint.setColor(mSelectCircleColor);
                       canvas.drawCircle(rect.centerX(), baseline - getMonthHeight() / 15, mPointSize, mLunarPaint);
                   }


                  //  } //else if (mSelectDate != null && date.equals(mSelectDate)) {

                      //  mSorlarPaint.setColor(mSelectCircleColor);
                       // int centerY = mRowNum == 5 ? rect.centerY() : (rect.centerY() + (mHeight / 5 - mHeight / 6) / 2);
                      //  canvas.drawCircle(rect.centerX(), centerY, mSelectCircleRadius, mSorlarPaint);
                      //  mSorlarPaint.setColor(mHollowCircleColor);
                      //  canvas.drawCircle(rect.centerX(), centerY, mSelectCircleRadius - mHollowCircleStroke, mSorlarPaint);

                     //   mSorlarPaint.setColor(mSolarTextColor);
                    //    canvas.drawText(date.getDayOfMonth() + "", rect.centerX(), baseline, mSorlarPaint);}

                        drawPoint(canvas, rect, date, baseline);
                 //       canvas.drawText(date.getDayOfMonth() + "", rect.centerX(), baseline, mSorlarPaint);
                        drawLunar(canvas, rect, baseline, mLunarTextColor, i, j);
                        //绘制节假日
                        drawHolidays(canvas, rect, date, baseline);
                        //绘制圆点
                        mSorlarPaint.setColor(mSolarTextColor);
                        canvas.drawText(date.getDayOfMonth() + "", rect.centerX(), baseline, mSorlarPaint);

                } else {
                    drawPoint(canvas, rect, date, baseline);
                    mSorlarPaint.setColor(mHintColor);
                    canvas.drawText(date.getDayOfMonth() + "", rect.centerX(), baseline, mSorlarPaint);
                    drawLunar(canvas, rect, baseline, mHintColor, i, j);
                    //绘制节假日
                    drawHolidays(canvas, rect, date, baseline);
                    //绘制圆点
                }
            }
        }
    }

    /**
     * 月日历高度
     *
     * @return
     */
    public int getMonthHeight() {
        return Attrs.monthCalendarHeight;
    }

    /**
     * 月日历的绘制高度，
     * 为了月日历6行时，绘制农历不至于太靠下，绘制区域网上压缩一下
     *
     * @return
     */
    public int getDrawHeight() {
        return (int) (getMonthHeight() - Utils.dp2px(getContext(), 10));
    }


    private void drawLunar(Canvas canvas, Rect rect, int baseline, int color, int i, int j) {
        if (isShowLunar) {
            mLunarPaint.setColor(color);
            String lunar = lunarList.get(i * 7 + j);
            canvas.drawText(lunar, rect.centerX(), 5+baseline + getMonthHeight() / 20, mLunarPaint);
        }
    }

    private void drawHolidays(Canvas canvas, Rect rect, LocalDate date, int baseline) {
        if (isShowHoliday) {
            if (holidayList.contains(date.toString())) {
                mLunarPaint.setColor(mHolidayColor);
                canvas.drawText("休", rect.centerX() + rect.width() / 4, baseline - getMonthHeight() / 20, mLunarPaint);

            } else if (workdayList.contains(date.toString())) {
                mLunarPaint.setColor(mWorkdayColor);
                canvas.drawText("班", rect.centerX() + rect.width() / 4, baseline - getMonthHeight() / 20, mLunarPaint);
            }
        }
    }

    //绘制圆点
    public void drawPoint(Canvas canvas, Rect rect, LocalDate date, int baseline) {
        int centerY = mRowNum == 5 ? rect.centerY() : (rect.centerY() + (mHeight / 5 - mHeight / 6) / 2);
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
                    if (Utils.isLastMonth(selectDate, mInitialDate)) {
                        mOnClickMonthViewListener.onClickLastMonth(selectDate);
                    } else if (Utils.isNextMonth(selectDate, mInitialDate)) {
                        mOnClickMonthViewListener.onClickNextMonth(selectDate);
                    } else {
                        mOnClickMonthViewListener.onClickCurrentMonth(selectDate);
                    }
                    break;
                }
            }
            return true;
        }
    });

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    public int getRowNum() {
        return mRowNum;
    }

    public int getSelectRowIndex() {
        if (mSelectDate == null) {
            return 0;
        }
        int indexOf = dates.indexOf(mSelectDate);
        return indexOf / 7;
    }


}
