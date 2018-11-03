package com.example.note.justdo.MainLayoutTools;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.note.justdo.App;
import com.example.note.justdo.Event;
import com.example.note.justdo.Eventdaomanger;
import com.example.note.justdo.R;
import com.example.note.justdo.TimeReminder.TimeManger;

import org.joda.time.LocalDate;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Choz on 2018/4/18.
 */

public class listrecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context mContext;
    LayoutInflater mInflate;
    List<Event> mEvent;
    Eventdaomanger eventdaomanger = getApp().getEventdaomanger();
    MlistviewOnClickListener mlistviewOnClickListener;
    TimeManger timeManger = new TimeManger();
    Event event;
    int mDownX;
    int mDownY;
    int nowX;
    int nowY;
    int pos;
    float textwidth;
    float timetextwidth;
    int textSize;
    int timetextsize;
    float wordwidth;
    float timewordwidth;
    int movewidth;
    int includedsize;
    int pastcolor;
    Paint wordPaint;
    Boolean ProcessMove = false;
    String contentString;
    String timeString;
    TextView content;
    AppCompatTextView timeinformation;
    LinearLayout focusedchildview;
    mRecyclerview recyclerView;
    listViewHolder listViewholder;
    listTimeViewHolder listTimeViewHolder;
    int eventsnum;
    final int NormalType = 1;
    final int TimeType = 2;
    private SimpleDateFormat HmssimpleDateFormat;
    private SimpleDateFormat YmdsimpleDateFormat;
    private SimpleDateFormat AllsimpleDateFormat;
    private final String yesterday = "昨天";
    private final String today = "今天";
    private final String tomorrow = "明天";
    private final String EveryDay = "每天";
    private final String EveryWeek = "每周";
    private final String EveryMonth = "每月";
    private final String WorkDay = "工作日";
    private final String space = "  ";
    private final String NextDate = "下一日程:";
    private final String Every = "每";
    private final String Day = "天";
    private final String Week = "周";
    private final String Month = "月";
    private final String Year = "年";

    public listrecyclerAdapter(Context context, LayoutInflater mInflate, List<Event> mData) {
        this.mContext = context;
        this.mInflate = mInflate;
        this.mEvent = mData;
        this.eventsnum = getLineareventsNum(mEvent);
        HmssimpleDateFormat = new SimpleDateFormat("  HH:mm:ss");
        HmssimpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        YmdsimpleDateFormat = new SimpleDateFormat(" yyyy年MM月dd日");
        YmdsimpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        AllsimpleDateFormat = new SimpleDateFormat(" yyyy年MM月dd日  HH:mm:ss");
        AllsimpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
    }

    public void setRecyclerView(mRecyclerview recyclerView) {
        this.recyclerView = recyclerView;
    }

    @Override
    public int getItemViewType(int position) {
        return mEvent.get(position).getStartmills() > 0 ? TimeType : NormalType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == NormalType) {
            View view = mInflate.inflate(R.layout.item, parent, false);
            listViewHolder mylistViewHolder = new listViewHolder(view);
            return mylistViewHolder;
        } else {
            View view = mInflate.inflate(R.layout.item_time, parent, false);
            listTimeViewHolder mylistViewHolder = new listTimeViewHolder(view);
            return mylistViewHolder;
        }
    }


    public void setMlistviewOnClickListener(MlistviewOnClickListener mlistviewOnClickListener) {
        this.mlistviewOnClickListener = mlistviewOnClickListener;
    }

    public int getEventsnum() {
        return eventsnum;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof listViewHolder) {
            content = ((listViewHolder) holder).content;
            content.setText(mEvent.get(position).toString());
            Log.d("TAG", "holderposition==" + "===+" + position);
            if (mEvent.get(position).getIsLinearShow()) {//第一次绘制时，如果对应位置事件是完成事件
                content.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);//对应位置Textview绘制完整中划线
                content.setTextColor(Color.parseColor("#808080"));
            } else if (!mEvent.get(position).getIsLinearShow()) {//否则
                content.getPaint().setFlags(0);//不做中划线绘制或者清空对应位置中划线
                content.setTextColor(Color.parseColor("#000000"));
            }
            content.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    recyclerView.setDrag(true);
                    mlistviewOnClickListener.OnDragStart(holder);
                    return true;
                }
            });
        /*在recyclerview的绘制主函数中对事件内容所在TextView进行手势监听。
          好处之一在于该函数提供了准确的position
         */
            content.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent ev) {//当前content处于最底层优先级手势处理，意味着父级View总是先执行手势操作或拦截。
                    switch (ev.getAction()) {
                        case MotionEvent.ACTION_DOWN://按下时
                            pos = holder.getAdapterPosition();//防止错位，重新找位置。
                            event = mEvent.get(pos);//获取当前位置事件
                            listViewholder = (listViewHolder) recyclerView.findViewHolderForAdapterPosition(pos);//重新定位当前ViewHolder
                            Log.d("TAG", "viewposition==" + "===+" + pos + "   ++");//----------检查错位bug时用到----------
                            content = listViewholder.content;//重定位content
                            mDownX = (int) ev.getX();//手势触摸点位置获取
                            mDownY = (int) ev.getY();//
                            break;
                        case MotionEvent.ACTION_MOVE://移动时
                            nowX = (int) ev.getX();//当前触摸点位置
                            nowY = (int) ev.getY();//
                            if ((nowX > mDownX) && (nowX - mDownX > 2)) {//如果右移超过2个单位
                                ProcessMove = true;
                                recyclerView.setRightSlip(true);
                            }
                            //   content.getParent().requestDisallowInterceptTouchEvent(true);//手势拦截:不允许父级继续对该手势处理，后续手势将由该content完成
                            if (ProcessMove == true) {
                                content.getParent().requestDisallowInterceptTouchEvent(true);//手势拦截:不允许父级继续对该手势处理，后续手势将由该content完成
                                Log.d("TAG", "time to processmove");
                                if (mDownX == 0) {
                                    mDownX = nowX;
                                }
                                int dy = nowX - mDownX;
                                mDownX = nowX;
                                contentString = content.getText().toString();//获取内容字符串
                                textSize = contentString.length();//字符数
                                wordPaint = new Paint();
                                textwidth = wordPaint.measureText(contentString);//计算字符串长度
                                wordwidth = textwidth / textSize;//计算每个字符的长度
                                movewidth = movewidth + dy;//手势移动距离
                                if (movewidth < 0) {
                                    movewidth = 0;
                                }
                                if (movewidth > textwidth) {
                                    movewidth = (int) textwidth;
                                }
                                includedsize = (int) (movewidth / wordwidth);//标记移动距离所包含的字符个数
                                if (event.getIsLinearShow()) {//如果之前中划线完全显示，即事件已标记完成，随右移应逐渐消除中划线
                                    if ((int) (movewidth / wordwidth) >= textSize) {//如果移动距离超过字符串长度
                                        includedsize = textSize;//包含范围最大限定为字符串长度
                                    }
                                    changetextColorToBlack(content);
                                    content.getPaint().setFlags(0);//先清空当前TextView上的中划线（因为此中划线与下面过程所用的SpannableString中划线是两个东西，不清除的话两者会重合）
                                    String subcontent = content.getText().toString();//用于copy原字符串
                                    StringBuilder stringBuilder = new StringBuilder(subcontent);//下面进行字符串编辑
                                    SpannableString spannableString = new SpannableString(stringBuilder);
                                    //添加中划线
                                    spannableString.setSpan(new StrikethroughSpan(), includedsize, textSize, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);//从includesize开始到字符串末添加中划线，进行加工。
                                    content.setText(spannableString);//设置加工后的字符串。
                                } else {//如果之前无中划线，即事件未标记完成，随右移应逐渐显示中划线
                                    if ((int) (movewidth / wordwidth) >= textSize - 1) {//此处与之前不同仅出于消除中划线显示不全bug的考虑
                                        includedsize = textSize;
                                        if (includedsize < 0) {//此时includedsize是末尾位置，不能小于0；
                                            includedsize = 0;
                                        }
                                    }
                                    changetextColorToGray(content);
                                    StringBuilder stringBuilder = new StringBuilder(contentString);//相同的操作
                                    SpannableString spannableString = new SpannableString(stringBuilder);
                                    //添加中划线
                                    spannableString.setSpan(new StrikethroughSpan(), 0, includedsize, Spanned.SPAN_INCLUSIVE_INCLUSIVE);//从0开始至includesize作中划线。
                                    content.setText(spannableString);
                                    //  return true;//表示移动手势事件被处理
                                }
                                content.getParent().requestDisallowInterceptTouchEvent(true);//手势拦截:不允许父级继续对该手势处理，后续手势将由该content完成
                                return true;//表示移动手势事件被处理
                            }
                            return false;

                        case MotionEvent.ACTION_UP:
                            if (ProcessMove) {//如果之前经过了移动，
                                movewidth = 0;
                                if (!event.getIsLinearShow()) {//之前无中划线显示时，
                                    if (includedsize < textSize / 2) {//移动距离小于原长度一半则保持原状，清空中划线；
                                        //     content.getPaint().setFlags(0);
                                        notifyItemChanged(pos);//刷新该位置状态。
                                    } else {//大于一半则作完全中划线，将事件标记为已完成
                                        event.setIsLinearShow(true);
                                        Boolean islinearshow = event.getIsLinearShow();
                                        eventsnum--;
                                        Log.d("TAG", "" + eventsnum);
                                        eventdaomanger.updateSwapedevents(event.getListid(), pos, eventsnum, islinearshow);//数据库中将该事件移至未完成事件下端
                                        updateeventlist();
                                        refreshTimeReminderSevice();
                                        Log.d("TAG", mEvent.get(pos).getIsLinearShow() + "===" + mEvent.get(mEvent.size() - 1).getIsLinearShow());
                                        //        listViewholder.setLinearShow(true);
                                        if (eventsnum > ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition()) {
                                            //    notifyItemRemoved(pos);//（如果事件标记完成后移动到的末尾位置处于当前屏幕视图之下，两步局部刷新增加连贯性）
                                            notifyItemRangeChanged(pos, 1 + ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition() - pos);
                                        } else {//
                                            //     notifyItemMoved(pos, eventsnum);
                                            //   notifyDataSetChanged();

                                            //如果事件标记完成后移动到的末尾位置处于当前屏幕视图之中，直接刷新
                                            notifyItemRangeChanged(pos, 1 + eventsnum - pos);
                                            //  notifyItemRangeChanged(position+1,getItemCount()-position-1);
                                            //    mEvent.add(mEvent.size()-1,event);

                                            Log.d("TAG", "SIZE==" + getItemCount());

                          /*               listViewHolder viewholder = ((listViewHolder) recyclerView.findViewHolderForLayoutPosition(mEvent.size() - 1));
                                         //     viewholder.content.setText(spannableString);
                                         viewholder.content.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG); //中划线
                                         Log.d("TAG", viewholder.content.getText().toString());*/
                                        }
                                    }
                                } else {//如果之前已标记完成
                                    if (includedsize < textSize / 2) {//小于一半时保持原状
                                        //  StringBuilder stringBuilder = new StringBuilder(contentString);
                                        //  SpannableString spannableString = new SpannableString(stringBuilder);
                                        //添加中划线
                                        // spannableString.setSpan(new StrikethroughSpan(), 0, textSize, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                                        // content.setText(spannableString);
                                        notifyItemChanged(pos);//刷新该位置状态。
                                    } else {//否则清空完成标记
                                        content.getPaint().setFlags(0);
                                        event.setLinearShow(false);
                                        //      eventdaomanger.updateSingleEvent(event);
                                        eventsnum++;
                                        eventdaomanger.updateSwapedevents(event.getListid(), pos, eventsnum - 1, event.getIsLinearShow());
                                        updateeventlist();
                                        refreshTimeReminderSevice();
                                        if ((eventsnum - 1) < ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition()) {
                                            // notifyItemRemoved(pos);
                                            notifyItemRangeChanged(((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition(), 1 + (pos - ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition()));
                                        } else {
                                            notifyItemRangeChanged(eventsnum - 1, 2 + pos - eventsnum);
                                        }
                                    }
                                }
                                ProcessMove = false;
                                Log.d("TAG", "processmove reach");
                                return true;

                            } else {//如果标记已完成，则令点击无效
                                if (event.getIsLinearShow()) {
                                    //    content.performClick();//
                                    return true;
                                }
                                break;
                            }
                        case MotionEvent.ACTION_CANCEL:
                            content.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                    return false;
                }
            });
            ((listViewHolder) holder).content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pos = holder.getLayoutPosition();
                    mlistviewOnClickListener.OnitemClickListener(v, pos);
                }
            });
        } else {//如果标记了时间提醒
            final Date nowDate = new Date();
            String TimeString;
            String ExtraTimeString;
            String FinalString;
            String intervalString;
            content = ((listTimeViewHolder) holder).content;
            timeinformation = ((listTimeViewHolder) holder).timeinformation;
            focusedchildview = ((listTimeViewHolder) holder).linearLayout;
            Event CusorEvent = mEvent.get(position);
            if ((CusorEvent.getType() > 0) && (new Date().getTime() > CusorEvent.getStartmills())) {
                //有时间间隔的时间提醒，则更新当前event
                CusorEvent.getNextstartmills();
                Event updateev = eventdaomanger.getDaoSession().getEventDao().load(CusorEvent.getId());
                updateev.setStartmills(CusorEvent.getStartmills());
                eventdaomanger.getDaoSession().getEventDao().update(updateev);
            }
            content.setText(CusorEvent.toString());
            if (CusorEvent.getStartmills() < (nowDate.getTime())) {//在当前时间之前
                long Timelag = nowDate.getTime() - new LocalDate().toDateTimeAtStartOfDay().getMillis();
                if (CusorEvent.getIsLinearShow()) {
                    timeinformation.setTextColor(Color.parseColor("#808080"));
                } else {
                    timeinformation.setTextColor(Color.parseColor("#ff0000"));
                }//时间提醒为红色
                if ((CusorEvent.getStartmills() + Timelag) > nowDate.getTime()) {//今天
                    TimeString = HmssimpleDateFormat.format(CusorEvent.getDate());
                    FinalString = today + TimeString;
                    timeinformation.setText(FinalString);
                } else if ((CusorEvent.getStartmills() + Timelag + 1000 * 24 * 60 * 60) > nowDate.getTime()) {//昨天
                    TimeString = HmssimpleDateFormat.format(CusorEvent.getDate());
                    FinalString = yesterday + TimeString;
                    timeinformation.setText(FinalString);
                } else {
                    //昨天之前
                    TimeString = AllsimpleDateFormat.format(CusorEvent.getDate());
                    timeinformation.setText(TimeString);
                }
            } else {//未来
                int dayofweek = CusorEvent.getCalendar().get(Calendar.DAY_OF_WEEK);
                String DayofWeek = getDayofWeek(dayofweek);
                timeinformation.setTextColor(Color.parseColor("#808080"));
                /**
                 * 先根据type确定timeinformation的内容
                 * 非循环提醒，需对提醒时间为昨天之前，昨天，今天，明天，明天之后做判断，以确定不同的文字内容
                 * 循环提醒，目前内容为类型+时间+下一日程日期（如：每天 9：00  下一日程：X年X月X日）
                 */
                switch (CusorEvent.getType()) {
                    case 0:
                        long Timelag = 24 * 60 * 60 * 1000 + new LocalDate().toDateTimeAtStartOfDay().getMillis() - nowDate.getTime();
                        if ((nowDate.getTime() + Timelag) > CusorEvent.getStartmills()) {//今天
                            TimeString = HmssimpleDateFormat.format(CusorEvent.getDate());
                            FinalString = today + TimeString;
                            timeinformation.setText(FinalString);
                        } else if ((nowDate.getTime() + Timelag + 24 * 60 * 60 * 1000) > CusorEvent.getStartmills()) {//明天
                            TimeString = HmssimpleDateFormat.format(CusorEvent.getDate());
                            FinalString = tomorrow + TimeString;
                            timeinformation.setText(FinalString);
                        } else {//明天之后的时间
                            TimeString = AllsimpleDateFormat.format(CusorEvent.getDate());
                            timeinformation.setText(TimeString);
                        }
                        break;
                    case 1:
                        TimeString = HmssimpleDateFormat.format(CusorEvent.getDate());
                        ExtraTimeString = YmdsimpleDateFormat.format(CusorEvent.getDate());
                        FinalString = EveryDay + TimeString + space + NextDate + ExtraTimeString;
                        timeinformation.setText(FinalString);
                        break;
                    case 2:
                        TimeString = HmssimpleDateFormat.format(CusorEvent.getDate());
                        ExtraTimeString = YmdsimpleDateFormat.format(CusorEvent.getDate());
                        FinalString = EveryWeek + DayofWeek + TimeString + space + NextDate + ExtraTimeString;
                        timeinformation.setText(FinalString);
                        break;
                    case 3:
                        TimeString = HmssimpleDateFormat.format(CusorEvent.getDate());
                        ExtraTimeString = YmdsimpleDateFormat.format(CusorEvent.getDate());
                        FinalString = EveryMonth + TimeString + space + NextDate + ExtraTimeString;
                        timeinformation.setText(FinalString);
                        break;
                    case 4:
                        TimeString = HmssimpleDateFormat.format(CusorEvent.getDate());
                        FinalString = WorkDay + TimeString;
                        timeinformation.setText(FinalString);
                        break;
                    case 5:
                        TimeString = HmssimpleDateFormat.format(CusorEvent.getDate());
                        ExtraTimeString = YmdsimpleDateFormat.format(CusorEvent.getDate());
                        intervalString = Integer.toString(CusorEvent.getIntervel());
                        FinalString = Every + intervalString + Day + TimeString + space + NextDate + ExtraTimeString;
                        timeinformation.setText(FinalString);
                        break;
                    case 6:
                        TimeString = HmssimpleDateFormat.format(CusorEvent.getDate());
                        ExtraTimeString = YmdsimpleDateFormat.format(CusorEvent.getDate());
                        intervalString = Integer.toString(CusorEvent.getIntervel());
                        FinalString = Every + intervalString + Week + TimeString + space + NextDate + ExtraTimeString;
                        timeinformation.setText(FinalString);
                        break;
                    case 7:
                        TimeString = HmssimpleDateFormat.format(CusorEvent.getDate());
                        ExtraTimeString = YmdsimpleDateFormat.format(CusorEvent.getDate());
                        intervalString = Integer.toString(CusorEvent.getIntervel());
                        FinalString = Every + intervalString + Month + TimeString + space + NextDate + ExtraTimeString;
                        timeinformation.setText(FinalString);
                        break;
                    case 8:
                        TimeString = HmssimpleDateFormat.format(CusorEvent.getDate());
                        ExtraTimeString = YmdsimpleDateFormat.format(CusorEvent.getDate());
                        intervalString = Integer.toString(CusorEvent.getIntervel());
                        FinalString = Every + intervalString + Year + TimeString + space + NextDate + ExtraTimeString;
                        timeinformation.setText(FinalString);
                        break;
                }
            }
            if (CusorEvent.getIsLinearShow()) {//第一次绘制时，如果对应位置事件是完成事件
                content.setTextColor(Color.parseColor("#808080"));
                content.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);//对应位置Textview绘制完整中划线
                timeinformation.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            } else if (!CusorEvent.getIsLinearShow()) {//否则
                content.setTextColor(Color.parseColor("#000000"));
                content.getPaint().setFlags(0);//不做中划线绘制或者清空对应位置中划线
                timeinformation.getPaint().setFlags(0);
            }
            focusedchildview.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    recyclerView.setDrag(true);
                    mlistviewOnClickListener.OnDragStart(holder);
                    return true;
                }
            });
            focusedchildview.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent ev) {//当前content处于最底层优先级手势处理，意味着父级View总是先执行手势操作或拦截。
                    switch (ev.getAction()) {
                        case MotionEvent.ACTION_DOWN://按下时
                            pos = holder.getAdapterPosition();//防止错位，重新找位置。
                            event = mEvent.get(pos);//获取当前位置事件
                            listTimeViewHolder = (listTimeViewHolder) recyclerView.findViewHolderForAdapterPosition(pos);//重新定位当前ViewHolder
                            Log.d("TAG", "viewposition==" + "===+" + pos + "   ++");//----------检查错位bug时用到----------
                            content = listTimeViewHolder.content;//重定位content
                            timeinformation = listTimeViewHolder.timeinformation;
                            pastcolor = timeinformation.getCurrentTextColor();
                            mDownX = (int) ev.getX();//手势触摸点位置获取
                            mDownY = (int) ev.getY();//
                            break;
                        case MotionEvent.ACTION_MOVE://移动时
                            nowX = (int) ev.getX();//当前触摸点位置
                            nowY = (int) ev.getY();//
                            if ((nowX > mDownX) && (nowX - mDownX > 2)) {//如果右移超过2个单位
                                focusedchildview.requestDisallowInterceptTouchEvent(true);//手势拦截:不允许父级继续对该手势处理，后续手势将由该content完成
                                ProcessMove = true;
                                recyclerView.setRightSlip(true);
                            }
                            if (ProcessMove) {
                                if (mDownX == 0) {
                                    mDownX = nowX;
                                }
                                int dy = nowX - mDownX;
                                mDownX = nowX;
                                Log.d("TAG", "time to processmove");
                                contentString = content.getText().toString();//获取内容字符串
                                timeString = timeinformation.getText().toString();
                                textSize = contentString.length();//字符数
                                timetextsize = timeString.length();
                                wordPaint = new Paint();
                                textwidth = wordPaint.measureText(contentString);//计算字符串长度
                                timetextwidth = wordPaint.measureText(timeString);
                                wordwidth = textwidth / textSize;//计算每个字符的长度
                                timewordwidth = timetextwidth / timetextsize;
                                movewidth = movewidth + dy;//手势移动距离
                                if (movewidth < 0) {
                                    movewidth = 0;
                                }
                                if (movewidth > textwidth) {
                                    movewidth = (int) textwidth;
                                }
                                includedsize = (int) (movewidth / wordwidth);//标记移动距离所包含的字符个数
                                if (event.getIsLinearShow()) {//如果之前中划线完全显示，即事件已标记完成，随右移应逐渐消除中划线
                               /*     if ((int) (movewidth / wordwidth) >= textSize) {//如果移动距离超过字符串长度
                                        includedsize = textSize;//包含范围最大限定为字符串长度
                                    }*/
                                    if (includedsize == 0) {
                                        changetextColorToGray(content);
                                        changetextColorToGray(timeinformation);
                                    } else {
                                        changetextColorToBlack(content);
                                        if (event.getStartmills() < nowDate.getTime()) {
                                            timeinformation.setTextColor(Color.parseColor("#ff0000"));
                                        }
                                    }
                                    content.getPaint().setFlags(0);//先清空当前TextView上的中划线（因为此中划线与下面过程所用的SpannableString中划线是两个东西，不清除的话两者会重合）
                                    timeinformation.getPaint().setFlags(0);
                                    String subcontent = content.getText().toString();//用于copy原字符串
                                    String timesubcontent = timeinformation.getText().toString();
                                    StringBuilder stringBuilder = new StringBuilder(subcontent);//下面进行字符串编辑
                                    SpannableString spannableString = new SpannableString(stringBuilder);
                                    //添加中划线
                                    spannableString.setSpan(new StrikethroughSpan(), includedsize, textSize, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);//从includesize开始到字符串末添加中划线，进行加工。
                                    content.setText(spannableString);//设置加工后的字符串。
                                    stringBuilder = new StringBuilder(timesubcontent);//下面进行字符串编辑
                                    spannableString = new SpannableString(stringBuilder);
                                    //添加中划线
                                    spannableString.setSpan(new StrikethroughSpan(), includedsize * timetextsize / textSize, timetextsize, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);//从includesize开始到字符串末添加中划线，进行加工。
                                    timeinformation.setText(spannableString);//设置加工后的字符串。
                                } else {//如果之前无中划线，即事件未标记完成，随右移应逐渐显示中划线
                                 /*   if ((int) (movewidth / wordwidth) >= textSize - 1) {//此处与之前不同仅出于消除中划线显示不全bug的考虑
                                        includedsize = textSize;
                                        if (includedsize < 0) {//此时includedsize是末尾位置，不能小于0；
                                            includedsize = 0;
                                        }
                                    }*/
                                    if (includedsize == 0) {
                                        Recovertextcolor(pastcolor, timeinformation);
                                        changetextColorToBlack(content);
                                    } else {
                                        changetextColorToGray(content);
                                        changetextColorToGray(timeinformation);
                                    }
                                    StringBuilder stringBuilder = new StringBuilder(contentString);//相同的操作
                                    SpannableString spannableString = new SpannableString(stringBuilder);
                                    //添加中划线
                                    spannableString.setSpan(new StrikethroughSpan(), 0, includedsize, Spanned.SPAN_INCLUSIVE_INCLUSIVE);//从0开始至includesize作中划线。
                                    content.setText(spannableString);
                                    stringBuilder = new StringBuilder(timeString);//相同的操作
                                    spannableString = new SpannableString(stringBuilder);
                                    //添加中划线
                                    spannableString.setSpan(new StrikethroughSpan(), 0, includedsize * timetextsize / textSize, Spanned.SPAN_INCLUSIVE_INCLUSIVE);//从0开始至includesize作中划线。
                                    timeinformation.setText(spannableString);
                                }
                                return true;//表示移动手势事件被处理
                            }
                            return false;

                        case MotionEvent.ACTION_UP:
                            if (ProcessMove) {//如果之前经过了移动，
                                if (!event.getIsLinearShow()) {//之前无中划线显示时，
                                    if (includedsize < textSize / 2) {//移动距离小于原长度一半则保持原状，清空中划线；
                                        notifyItemChanged(pos);//刷新该位置状态。
                                    } else {//大于一半则作完全中划线，将事件标记为已完成
                                        event.setIsLinearShow(true);
                                        Boolean islinearshow = event.getIsLinearShow();
                                        eventsnum--;
                                        eventdaomanger.updateSwapedevents(event.getListid(), pos, eventsnum, islinearshow);//数据库中将该事件移至未完成事件下端
                                        updateeventlist();
                                        refreshTimeReminderSevice();
                                        Log.d("TAG", mEvent.get(pos).getIsLinearShow() + "===" + mEvent.get(mEvent.size() - 1).getIsLinearShow());
                                        if (eventsnum > ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition()) {
                                            //  notifyItemRemoved(pos);//（如果事件标记完成后移动到的末尾位置处于当前屏幕视图之下，两步局部刷新增加连贯性）
                                            notifyItemRangeChanged(pos, 1 + ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition() - pos);
                                        } else {//
                                            //如果事件标记完成后移动到的末尾位置处于当前屏幕视图之中，直接刷新
                                            notifyItemRangeChanged(pos, 1 + eventsnum - pos);
                                            Log.d("TAG", "SIZE==" + getItemCount());


                                        }
                                    }
                                } else {//如果之前已标记完成
                                    if (includedsize < textSize / 2) {//小于一半时保持原状
                                        notifyItemChanged(pos);
                                    } else {//否则清空完成标记
                                        content.getPaint().setFlags(0);
                                        timeinformation.getPaint().setFlags(0);
                                        event.setLinearShow(false);
                                        eventsnum++;
                                        eventdaomanger.updateSwapedevents(event.getListid(), pos, eventsnum - 1, event.getIsLinearShow());
                                        updateeventlist();
                                        refreshTimeReminderSevice();
                                        if ((eventsnum - 1) < ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition()) {
                                            //   notifyItemRemoved(pos);
                                            notifyItemRangeChanged(((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition(), 1 + (pos - ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition()));
                                        } else {
                                            notifyItemRangeChanged(eventsnum - 1, 2 + pos - eventsnum);
                                        }
                                    }
                                }
                                movewidth = 0;
                                ProcessMove = false;
                                return true;

                            } else {//如果标记已完成，则令点击无效
                                if (event.getIsLinearShow()) {
                                    //    content.performClick();//
                                    return true;
                                }
                                break;
                            }
                        case MotionEvent.ACTION_CANCEL:
                            focusedchildview.requestDisallowInterceptTouchEvent(false);
                            break;

                    }
                    return false;
                }
            });
            ((listTimeViewHolder) holder).linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pos = holder.getLayoutPosition();
                    mlistviewOnClickListener.OnitemClickListener(v, pos);
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return mEvent.size();
    }

    /**
     * 不带有提醒的默认item viewholder
     * 布局详见item.xml
     */
    class listViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayout;
        TextView content;

        private listViewHolder(View itemView) {
            super(itemView);
            linearLayout = (LinearLayout) itemView;//item总布局
            content = linearLayout.findViewById(R.id.textView);//内部textview
        }
    }

    /**
     * 带有时间提醒的item viewholder
     * 布局详见item_time.xml
     */
    class listTimeViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayout;
        AppCompatTextView content;
        AppCompatTextView timeinformation;

        private listTimeViewHolder(View itemView) {
            super(itemView);
            linearLayout = (LinearLayout) itemView;//item总布局
            content = linearLayout.findViewById(R.id.item_time1);//内部内容textview
            timeinformation = linearLayout.findViewById(R.id.item_time2);//时间部分textview
        }
    }

    /**
     * 内部接口，Mainactivity中实现
     * 分别为每个item点击事件监听
     * 与开始拖动排序时的监听
     */

    public interface MlistviewOnClickListener {
        void OnitemClickListener(View v, int pos);

        void OnDragStart(RecyclerView.ViewHolder viewHolder);
    }

    /**
     * 根据dayofweek，获取对应字符串
     *
     * @param dayofweek
     * @return
     */
    private String getDayofWeek(int dayofweek) {
        switch (dayofweek) {
            case 1:
                return "日";
            case 2:
                return "一";
            case 3:
                return "二";
            case 4:
                return "三";
            case 5:
                return "四";
            case 6:
                return "五";
            case 7:
                return "六";
            default:
                return null;
        }
    }

    /**
     * 根据位置移除对应item
     *
     * @param position
     */
    public void removeitem(int position) {
        if (!mEvent.get(position).getIsLinearShow()) {
            eventsnum--;
        }
        mEvent.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount() - position);
    }

    /**
     * 拖动换位时，先移除，后添加item
     *
     * @param startpos，
     */
    public void removeitembeforeadd(int startpos) {
        mEvent.remove(startpos);
    }

    /**
     * @param event
     * @param finalpos
     */
    public void additemafterremove(Event event, int finalpos) {
        mEvent.add(finalpos, event);

    }

    /**
     * 添加事件时，将事件移至首位
     */

    public void additemToFirst() {
        updateeventlist();
        eventsnum++;
        notifyItemInserted(0);
        notifyItemRangeChanged(1, mEvent.size() > 13 ? 12 : (mEvent.size() - 1));
    }

    /**
     * 第一次添加事件时，不用刷新adapter内events
     *
     * @param event
     */
    public void addfirstitem(Event event) {
        mEvent.add(event);
        notifyItemInserted(0);
        eventsnum++;
    }

    /**
     * 根据数据库内容，更新eventlist，你当前可直接注释
     */
    public void updateeventlist() {
        if (mEvent.size() > 0) {//只有当前listid对应event数大于1时才会产生数据库对应event数据移位事件
            int listid = mEvent.get(0).getListid();//取第一个event获取listid
            mEvent = eventdaomanger.getfinalEventlist(listid);
        }
    }

    /**
     * 将textview的文字颜色变为黑色
     *
     * @param textView
     */
    private void changetextColorToBlack(TextView textView) {
        int color = textView.getCurrentTextColor();
        int graycolor = Color.parseColor("#808080");
        int blackcolor = Color.parseColor("#000000");
        if (color != blackcolor) {
            textView.setTextColor(blackcolor);
        }
    }

    /**
     * 将textview的文字颜色改为灰色
     *
     * @param textView
     */
    private void changetextColorToGray(TextView textView) {
        int color = textView.getCurrentTextColor();
        int graycolor = Color.parseColor("#808080");
        int blackcolor = Color.parseColor("#000000");
        if (color != graycolor) {
            textView.setTextColor(graycolor);
        }
    }

    /**
     * 恢复textview之前的文字颜色
     *
     * @param pastcolor
     * @param textView
     */
    private void Recovertextcolor(int pastcolor, TextView textView) {
        textView.setTextColor(pastcolor);
    }


    private App getApp() {
        return App.getInstance();
    }

    /**
     * 刷新时间提醒服务,你当前可直接注释
     */
    private void refreshTimeReminderSevice() {
        if (eventdaomanger.IsTimeRemindExist(mEvent.get(0).getListid())) {
            timeManger.refreshTimeJobInOneList(mEvent.get(0).getListid());
        }
    }

    private int getLineareventsNum(List<Event> Events) {
        int num = 0;
        for (Event event : Events) {
            if (!event.getIsLinearShow()) {
                num++;
            }
        }
        return num;
    }

    public Event getEventBypos(int pos) {
        return mEvent.get(pos);
    }

    public void recoverremove(Event event, int finalpos) {
        mEvent.add(finalpos, event);
        if (!event.getIsLinearShow()) {
            eventsnum++;
        }
        Log.d("TAG", "event" + event.getContext());
        notifyItemInserted(finalpos);
        notifyItemRangeChanged(finalpos + 1, getItemCount() - finalpos - 1);

    }
    public void removefinishedevents(){
        if(eventsnum!=getItemCount()-1){
           final int finalpos=getItemCount();
            for(int i=getItemCount()-1;i>=eventsnum;i--){
                mEvent.remove(i);
            }
            notifyItemRangeChanged(eventsnum-1,finalpos-eventsnum);
        }
        }
}

