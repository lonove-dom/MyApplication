package com.example.note.justdo.TimeReminder;

import android.content.Context;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;

import com.contrarywind.adapter.WheelAdapter;
import com.contrarywind.view.WheelView;
import com.example.note.justdo.Event;
import com.example.note.justdo.R;
import com.example.note.justdo.ncalendar.calendar.NCalendar;
import com.example.note.justdo.ncalendar.listener.OnCalendarChangedListener;

import org.joda.time.LocalDate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;


/**
 * Created by Choz on 2018/6/5.
 * 时间选择器窗口
 * 主要进行窗口显示设置
 */

public class MyTimeWindow extends PopupWindow {
    private View view;
    private Context context;
    //确定按钮
    private Button addtime;
    //取消按钮
    private Button cancaltime;
    //按钮点击监听
    private Button daybtn;
    private Button weekbtn;
    private Button monthbtn;
    private Button workdaybtn;
    private Button custombtn;
    private View.OnClickListener onClickListener;
    private View.OnClickListener onClickListener1;
    //内部接口，用于外部重写确定、取消按钮的对应点击操作
    private MyTimeWindowButtonlistener myTimeWindowButtonlistener;
    private AppCompatActivity activity;
    private List<Integer> positionList;
    //时滚轮对应adapter位置
    final int Hourwheel=0;
    //分滚轮位置
    final int Minutewheel=1;
    //秒滚轮位置
    final int Secondwheel=2;
    //规定常量，用于标记日期的计算

    final int Single=0;
    final int EveryDay=1;
    final int EveryWeek=2;
    final int EveryMonth=3;
    final int WorkDay=4;
    final int CustomDay=5;
    final int CustomWeek=6;
    final int CustomMonth=7;
    final int CustomYear=8;
    private int State=Single;
    Boolean isTimeSelected=false;
    //时，分，秒对应滚轮数据列表
    List<Integer> Hours;
    List<Integer> Minutes;
    List<Integer> Seconds;
    List<Integer> Days;
    List<String> Types;
    private int Hour;
    private int Minute;
    private int Second;
    //时、分、秒滚轮
    WheelView HourwheelView;
    WheelView MinutewheelView;
    WheelView SecondwheelView;
    //滚轮父控件recyclerview
    RecyclerView mrecyclerView;
    //日历控件
    NCalendar nCalendar;
    //用于存储时、分、秒滚轮的集合
    List<WheelView> wheelViews;
    //recyclerview适配器
    MwheelAdapter mwheelAdapter;
    //recyclerview列布局设置
    MyGridlayoutmanger gridLayoutManager;
    //滚轮适配器，用于向滚轮中添加数据
    WheelAdapter<Integer> hourAdapter;
    WheelAdapter<Integer> minuteAdapter;
    WheelAdapter<Integer> secondAdapter;
    //暂定日历上限日期
    Calendar finalcalendar=new GregorianCalendar(2099,11,31);
    //需要向时间选择器传递event的信息
    Event currentEv;
    int intervel;
    long startmills;
    View.OnClickListener onClickListener2;
    Boolean customFinished=false;
    AppCompatTextView textView;
    //初始化
    public MyTimeWindow(Context context) {
        super(context);
        this.context=context;
        /**
         * 循环模式点击监听
         * 工作日，每天，每周，每月，自定义
         */
        onClickListener2=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(v.getId()){
                    case R.id.daybtn:
                        if(State!=EveryDay){
                        State=EveryDay;
                        intervel=1;
                        notifyButtonStateChanged();
                        if(startmills>0){
                        OnIntervelbuttonPressed(State);}}
                        else {
                            State=Single;
                            intervel=0;
                            notifyButtonStateChanged();
                            if(startmills>0){
                            OnIntervelbuttonCancalPressed();}
                        }
                    break;
                    case R.id.weekbtn:
                        if(State!=EveryWeek){
                        State=EveryWeek;
                            intervel=1;
                            notifyButtonStateChanged();
                            if(startmills>0){
                        OnIntervelbuttonPressed(State);}}
                    else {
                        State=Single;
                            intervel=0;
                            notifyButtonStateChanged();
                            if(startmills>0){
                        OnIntervelbuttonCancalPressed();}
                        }
                    break;
                    case R.id.monthbtn:
                        if(State!=EveryMonth){
                        State=EveryMonth;
                            intervel=1;
                            notifyButtonStateChanged();
                            if(startmills>0){
                        OnIntervelbuttonPressed(State);}}
                        else {
                        State=Single;
                            intervel=0;
                            notifyButtonStateChanged();
                            if(startmills>0){
                        OnIntervelbuttonCancalPressed();}
                    }
                    break;
                    case R.id.workdaybtn:
                        if(State!=WorkDay){
                            State=WorkDay;
                            intervel=1;//此处仅用于标记间隔大于0
                            notifyButtonStateChanged();
                            if(startmills>0){
                                OnIntervelbuttonPressed(State);}}
                        else {
                            State=Single;
                            intervel=0;
                            notifyButtonStateChanged();
                            if(startmills>0){
                                OnIntervelbuttonCancalPressed();}
                        }
                        break;
                    case R.id.custonbtn:
                          //此处需引入自定义表格

                            final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                            //    通过LayoutInflater来加载一个xml的布局文件作为一个View对象
                            final View Customview = LayoutInflater.from(activity).inflate(R.layout.customtimedialog, null);
                            //    设置我们自己定义的布局文件作为弹出框的Content
                            builder.setView(Customview);
                            WheelView stringwheelView=Customview.findViewById(R.id.every);
                            final WheelView intervalwheelView=Customview.findViewById(R.id.intevalSel);
                            final WheelView typewheelView=Customview.findViewById(R.id.typeSel);
                            final AlertDialog alertDialog;
                            stringwheelView.setCyclic(false);
                            intervalwheelView.setCyclic(false);
                            typewheelView.setCyclic(false);
                            final List<String> Everys=new ArrayList<>();
                            Everys.add("每");
                            Days=new ArrayList<>();
                            Types=new ArrayList<>();
                            for (int i=1;i<=30;i++){
                                Days.add(i);
                            }
                            Types.add("天");
                            Types.add("周");
                            Types.add("月");
                            Types.add("年");
                            stringwheelView.setAdapter(new WheelAdapter() {
                                @Override
                                public int getItemsCount() {
                                    return Everys.size();
                                }

                                @Override
                                public Object getItem(int index) {
                                    return Everys.get(index);
                                }

                                @Override
                                public int indexOf(Object o) {
                                    return Everys.indexOf(o);
                                }
                            });
                            intervalwheelView.setAdapter(new WheelAdapter() {
                                @Override
                                public int getItemsCount() {
                                    return Days.size();
                                }

                                @Override
                                public Object getItem(int index) {
                                    return Days.get(index);
                                }

                                @Override
                                public int indexOf(Object o) {
                                    return Days.indexOf(o);
                                }
                            });
                            typewheelView.setAdapter(new WheelAdapter() {
                                @Override
                                public int getItemsCount() {
                                    return Types.size();
                                }

                                @Override
                                public Object getItem(int index) {
                                    return Types.get(index);
                                }

                                @Override
                                public int indexOf(Object o) {
                                    return Types.indexOf(o);
                                }
                            });
                            alertDialog=builder.show();
                            onClickListener=new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    switch (v.getId()){
                                        case R.id.customaddbtn:
                                            intervel=intervalwheelView.getCurrentItem()+1;
                                            State=typewheelView.getCurrentItem()+5;
                                            Log.d("TAG","interval="+intervel);
                                            if(startmills>0){
                                                OnIntervelbuttonPressed(State);
                                                Log.d("TAG","66666666666");}
                                            notifyButtonStateChanged();
                                            alertDialog.dismiss();
                                            break;
                                        case R.id.customcancalbtn:
                                            State=Single;
                                            intervel=0;
                                            notifyButtonStateChanged();
                                            if(startmills>0){
                                                OnIntervelbuttonCancalPressed();}
                                            alertDialog.dismiss();
                                            break;
                                    }
                                }
                            };
                            Customview.findViewById(R.id.customcancalbtn).setOnClickListener(onClickListener);
                            Customview.findViewById(R.id.customaddbtn).setOnClickListener(onClickListener);
                    break;
                }
            }
        };
        //@layout--activity_text 为时间选择器根布局
        view= LayoutInflater.from(context).inflate(R.layout.activity_text,null,true);
        textView=view.findViewById(R.id.dateText);
        addtime=view.findViewById(R.id.addtime);
        cancaltime=view.findViewById(R.id.cancaltime);
        nCalendar = view.findViewById(R.id.mcalendar);
        (daybtn=view.findViewById(R.id.daybtn)).setOnClickListener(onClickListener2);
        (monthbtn=view.findViewById(R.id.monthbtn)).setOnClickListener(onClickListener2);
        (weekbtn=view.findViewById(R.id.weekbtn)).setOnClickListener(onClickListener2);
        (custombtn=view.findViewById(R.id.custonbtn)).setOnClickListener(onClickListener2);
        (workdaybtn=view.findViewById(R.id.workdaybtn)).setOnClickListener(onClickListener2);
        mrecyclerView =nCalendar.findViewById(R.id.mWheelView);
        onClickListener1=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.addtime:
                        myTimeWindowButtonlistener.OnSurebuttonClick(startmills,intervel,State,Hour,Minute,Second);
                        break;
                    case R.id.cancaltime:
                        myTimeWindowButtonlistener.OnCancalbuttonClick();
                        break;

                }
            }
        };
        nCalendar.setOnCalendarChangedListener(new OnCalendarChangedListener() {
            @Override
            public void onCalendarChanged(LocalDate date) {
                textView.setText(date.toString("yyyy.MM"));
                if(nCalendar.getDateClicked()){
                startmills=date.toDateTimeAtStartOfDay().toDate().getTime();
               Log.d("TAG", new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss").format(startmills));
                nCalendar.setDateClicked(false);
                switch (State){
                    case WorkDay:
                    case EveryDay:
                    case EveryWeek:
                    case EveryMonth:
                    case CustomDay:
                    case CustomWeek:
                    case CustomMonth:
                    case CustomYear:OnIntervelbuttonPressed(State);
                    break;
                    case Single:OnIntervelbuttonCancalPressed();
                    break;
                }
            }}
        });
       addtime.setOnClickListener(onClickListener1);
       cancaltime.setOnClickListener(onClickListener1);
        this.setContentView(this.view);
        // 设置弹出窗体的宽和高
        this.setHeight(ConstraintLayout.LayoutParams.WRAP_CONTENT);
        this.setWidth(ConstraintLayout.LayoutParams.MATCH_PARENT);
        // 设置弹出窗体可点击
        this.setFocusable(true);
        //设置出现与消失动画
        this.setAnimationStyle(R.style.Time_Window_anim);
    }
    public NCalendar getnCalendar(){
        return nCalendar;
    }

    public int getState() {
        return State;
    }

    public long getStartmills() {
        return startmills;
    }

    public int getIntervel() {
        return intervel;
    }

    public Boolean getTimeSelected() {
        return isTimeSelected;
    }

    public void setMyTimeWindowButtonlistener(MyTimeWindowButtonlistener myTimeWindowButtonlistener){
        this.myTimeWindowButtonlistener=myTimeWindowButtonlistener;
    }
    public void init(AppCompatActivity activity) {
        this.activity=activity;
        initWheelviews();
        //   wheelView = findViewById(R.id.wheelview);
        mwheelAdapter=new MwheelAdapter(wheelViews,context,activity.getLayoutInflater(),positionList);
        //监听滚轮操作
        mwheelAdapter.setMyTimeSelectToolListener(new MwheelAdapter.MyTimeSelectToolListener() {
            @Override
            public void OnTimeSelected(int index, int whichwheel) {
                switch (whichwheel){
                    case Hourwheel:
                        Hour=Hours.get(index);
                        Log.d("TAG","HOUR=="+Hour);
                        break;
                    case Minutewheel:
                        Minute=Minutes.get(index);
                        Log.d("TAG","MINUTE=="+Minute);
                        break;
                    case Secondwheel:
                        Second=Seconds.get(index);
                        Log.d("TAG","SECOND=="+Second);
                        break;
                }
                if(!nCalendar.getIsTimeSelected()){
                    isTimeSelected=true;
                nCalendar.setIsTimeSelected(isTimeSelected);}
                Boolean IsTimeBeforeNow=IsTimeBeforecurrent(Hour,Minute,Second);
                Boolean nCalendarIsTimeBeforeNow=nCalendar.getIsTimeBeforecurrent();
                if(!IsTimeBeforeNow.equals(nCalendarIsTimeBeforeNow)){
                    nCalendar.setIsTimeBeforecurrent(IsTimeBeforeNow);
                }
            }
        });
        //滚轮布局设置为三列
        gridLayoutManager =new MyGridlayoutmanger(context, 3, GridLayoutManager.VERTICAL, false);
        //*******************
        mrecyclerView.setAdapter(mwheelAdapter);
        mrecyclerView.setLayoutManager(gridLayoutManager);
    }
    //@设定滚轮初始位置，使滚轮位置处于event当前的设置时间
    public void initTime(int Hour,int Minute,int Second){
        positionList=new ArrayList<>();
        positionList.add(Hour);
        positionList.add(Minute);
        positionList.add(Second);
        mwheelAdapter.setPositionList(positionList);
    }
    //此方法用于连接MainActivity与MyTimeWindow
    //必须在init方法之前执行
    public void setEvent(Event event){
       // 如果是新添加的事件,则不做初始化
        if(event==null){
            return;
        }
        //确认已有事件是否具备时间提醒
        if(event.getStartmills()>0){
            this.currentEv=event;
            this.startmills=(new LocalDate(event.getStartmills())).toDateTimeAtStartOfDay().getMillis();
            this.intervel=event.getIntervel();
            this.State=event.getType();
            Calendar calendar = event.getCalendar();
            this.Hour=calendar.get(Calendar.HOUR_OF_DAY);
            this.Minute=calendar.get(Calendar.MINUTE);
            this.Second=calendar.get(Calendar.SECOND);
            initTime(Hour, Minute, Second);
            nCalendar.setIsTimeBeforecurrent(IsTimeBeforecurrent(Hour,Minute,Second));
            //有时间提醒的事件必然选中了时间
            isTimeSelected=true;
            nCalendar.setIsTimeSelected(true);
            notifyButtonStateChanged();
        //    mwheelAdapter.notifyDataSetChanged();
            nCalendar.setPoint(getPointList(State,intervel,startmills));
        }
    }
    public void initCalendar(long startmills,int interval,int type){
        Date d=new Date();
        d.setTime(startmills);
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(d);
        this.startmills=(new LocalDate(startmills)).toDateTimeAtStartOfDay().getMillis();
        this.intervel=interval;
        this.State=type;
        this.Hour=calendar.get(Calendar.HOUR_OF_DAY);
        this.Minute=calendar.get(Calendar.MINUTE);
        this.Second=calendar.get(Calendar.SECOND);
        initTime(Hour,Minute,Second);
        isTimeSelected=true;
        nCalendar.setIsTimeBeforecurrent(IsTimeBeforecurrent(Hour,Minute,Second));
        nCalendar.setIsTimeSelected(true);
        notifyButtonStateChanged();
      //  mwheelAdapter.notifyDataSetChanged();
        nCalendar.setPoint(getPointList(State,intervel,startmills));
    }
    private void initWheelviews(){
        HourwheelView=new WheelView(context);
        MinutewheelView=new WheelView(context);
        SecondwheelView=new WheelView(context);
        HourwheelView.setCyclic(false);
        MinutewheelView.setCyclic(false);
        SecondwheelView.setCyclic(false);
        wheelViews=new ArrayList<>();
        Hours = new ArrayList<>();
        Minutes = new ArrayList<>();
        Seconds = new ArrayList<>();
        //时
        hourAdapter=new WheelAdapter<Integer>() {
            @Override
            public int getItemsCount() {
                return Hours.size();
            }

            @Override
            public Integer getItem(int index) {
                return Hours.get(index);
            }

            @Override
            public int indexOf(Integer o) {
                return Hours.indexOf(o);
            }
        };
        //分
        minuteAdapter=new WheelAdapter<Integer>() {
            @Override
            public int getItemsCount() {
                return Minutes.size();
            }

            @Override
            public Integer getItem(int index) {
                return Minutes.get(index);
            }

            @Override
            public int indexOf(Integer o) {
                return Minutes.indexOf(o);
            }
        };
        //秒
        secondAdapter=new WheelAdapter<Integer>() {
            @Override
            public int getItemsCount() {
                return Seconds.size();
            }

            @Override
            public Integer getItem(int index) {
                return Seconds.get(index);
            }

            @Override
            public int indexOf(Integer o) {
                return Seconds.indexOf(o);
            }
        };
        for(int i=0;i<=23;i++){
            Hours.add(i);
        }
        for(int i=0;i<=59;i++){
            Minutes.add(i);
        }
        for(int i=0;i<=59;i++){
            Seconds.add(i);
        }
        HourwheelView.setAdapter(hourAdapter);
        MinutewheelView.setAdapter(minuteAdapter);
        SecondwheelView.setAdapter(secondAdapter);
        wheelViews.add(HourwheelView);
        wheelViews.add(MinutewheelView);
        wheelViews.add(SecondwheelView);
    }
    //此方法用于返回最终要被标记的日期列表
    public List<String> getPointList(int type,int interval,long startdaymills){
        Boolean stopped=false;
        List<String> pointList=new ArrayList<>();
        Date currentdate=new Date();
        Date startdate=new Date(startdaymills);
        long difftime=currentdate.getTime()-(new LocalDate().toDateTimeAtStartOfDay().getMillis());
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
        if(interval==0){
            pointList.add(format.format(startdate));
            return pointList;
        }
        else {
            finalcalendar = new GregorianCalendar(2099, 11, 31);
            Calendar currentcalendar = Calendar.getInstance();
            Calendar cusorCal;
            currentcalendar.setTime(startdate);
            int startYear = currentcalendar.get(Calendar.YEAR);
            int startMonth = currentcalendar.get(Calendar.MONTH);
            int startDay = currentcalendar.get(Calendar.DATE);
            // Date finaldata=calendar.getTime();
            switch (type) {
                case EveryDay:pointList=getEveryDayPointList(startdaymills);break;
                case EveryWeek:pointList=getEveryWeekPointList(startdaymills);break;
                case EveryMonth:pointList=getEveryMonthPointList(startdaymills);break;
                case WorkDay:int weekday=currentcalendar.get(Calendar.DAY_OF_WEEK);
                int cusor=0;
                switch (weekday){
                    case 1://星期日
                        startdate.setTime(startdate.getTime()+1000*24*60*60);
                    cusor=1;
                        break;
                    case 2:cusor=1;break;//星期一
                    case 3:cusor=2;break;//星期二
                    case 4:cusor=3;break;//星期三
                    case 5:cusor=4;break;//星期四
                    case 6:cusor=5;break;//星期五
                    case 7://星期六
                        startdate.setTime(startdate.getTime()+2*1000*24*60*60);
                    cusor=1;
                        break;
                }
                    if (startdate.getTime()+difftime < currentdate.getTime()) {
                        //关于是否始终显示过去时的问题？startmills不改变：startmills更新
                        //暂时令其更新
                        while (startdate.getTime()+difftime < currentdate.getTime()) {
                            pointList.add(format.format(startdate));
                            if (cusor < 5) {
                                startdate.setTime(startdate.getTime() + 1000 * 24 * 60 * 60);
                                cusor++;
                            } else if (cusor == 5) {
                                startdate.setTime(startdate.getTime() + 1000 * 3 * 24 * 60 * 60);
                                cusor = 1;
                            }
                        }
                    }
                        this.startmills=startdate.getTime();
                    while (!stopped) {
                        if (startdate.getTime() > finalcalendar.getTimeInMillis()) {
                            break;
                        }
                        pointList.add(format.format(startdate));
                        if(cusor<5){
                            startdate.setTime(startdate.getTime()+1000*24*60*60);
                            cusor++;
                        }
                        else if(cusor==5){
                            startdate.setTime(startdate.getTime()+1000*3*24*60*60);
                            cusor=1;
                        }
                    }
                    break;
                case CustomYear:
                    if (startdate.getTime()+difftime < currentdate.getTime()) {
                        while (startdate.getTime()+difftime < currentdate.getTime()) {
                            pointList.add(format.format(startdate));
                            startYear = startYear + interval;
                            cusorCal = new GregorianCalendar(startYear, startMonth, startDay);
                            startdate.setTime(cusorCal.getTimeInMillis());
                        }
                        this.startmills=startdate.getTime();
                    }
                    while (!stopped) {
                        if (startdate.getTime() > finalcalendar.getTimeInMillis()) {
                            break;
                        }
                        pointList.add(format.format(startdate));
                        startYear = startYear + interval;
                        cusorCal = new GregorianCalendar(startYear, startMonth, startDay);
                        startdate.setTime(cusorCal.getTimeInMillis());
                    }
                    break;
                case CustomMonth:
                    if (startdate.getTime()+difftime < currentdate.getTime()) {
                        while (startdate.getTime()+difftime < currentdate.getTime()) {
                            pointList.add(format.format(startdate));
                            startMonth = startMonth + interval;
                            if (startMonth > 11) {
                                startYear = startYear + startMonth / 12;
                                startMonth = startMonth % 12;
                            }
                            cusorCal = new GregorianCalendar(startYear, startMonth, startDay);
                            startdate.setTime(cusorCal.getTimeInMillis());
                        }
                        this.startmills=startdate.getTime();
                    }
                    while (!stopped) {
                        if (startdate.getTime() > finalcalendar.getTimeInMillis()) {
                            break;
                        }
                        pointList.add(format.format(startdate));
                        startMonth = startMonth + interval;
                        if (startMonth > 11) {
                            startYear = startYear + startMonth / 12;
                            startMonth = startMonth % 12;
                        }
                        cusorCal = new GregorianCalendar(startYear, startMonth, startDay);
                        startdate.setTime(cusorCal.getTimeInMillis());
                    }
                    break;
                case CustomDay:
                    if (startdate.getTime()+difftime < currentdate.getTime()) {
                        while (startdate.getTime()+difftime < currentdate.getTime()) {
                            pointList.add(format.format(startdate));
                            startdate.setTime(startdate.getTime() + interval * 1000 * 24 * 60 * 60);
                        }
                        this.startmills=startdate.getTime();
                    }
                    while (!stopped) {
                        if (startdate.getTime() > finalcalendar.getTimeInMillis()) {
                            break;
                        }
                        pointList.add(format.format(startdate));
                        startdate.setTime(startdate.getTime() + interval * 1000 * 24 * 60 * 60);
                    }
                    break;
                case CustomWeek: return getPointList(CustomDay,7*interval,startdaymills);
            }
            return pointList;
        }
    }
    //每年按钮按下时返回的标记日期列表
    private List<String> getEveryYearPointList(long startmills){
        return getPointList(CustomYear,1,startmills);
    }
    //每月
    private List<String> getEveryMonthPointList(long startmills){
        return getPointList(CustomMonth,1,startmills);
    }
    //每天
    private List<String> getEveryDayPointList(long startmills){
        return getPointList(CustomDay,1,startmills);
    }
    private List<String> getEveryWeekPointList(long startmills){
        return getPointList(CustomDay,7,startmills);
    }
    private List<String> getSingleDayPointList(){
        List<String> pointlist=new ArrayList<>();
        pointlist.add(new SimpleDateFormat("yyyy-MM-dd").format(new Date(startmills)));
        return pointlist;
    }
    private void notifyButtonStateChanged(){
      initUnsignedButton();
      switch (State){
          case 1:setSignedButton(daybtn);break;
          case 2:setSignedButton(weekbtn);break;
          case 3:setSignedButton(monthbtn);break;
          case 4:setSignedButton(workdaybtn);break;
          case 5|6|7|8:setSignedButton(custombtn);break;
          default:break;
      }
    }
    private void setSignedButton(Button button){
      button.setTextSize(dip2px(context,7));
      button.setTextColor(Color.parseColor("#000000"));
    }
    private void initUnsignedButton(){
        final float textsize=dip2px(context,5);
        final String colorstring="#808080";
        daybtn.setTextColor(Color.parseColor(colorstring));
        daybtn.setTextSize(textsize);
        weekbtn.setTextColor(Color.parseColor(colorstring));
        weekbtn.setTextSize(textsize);
        monthbtn.setTextColor(Color.parseColor(colorstring));
        monthbtn.setTextSize(textsize);
        custombtn.setTextColor(Color.parseColor(colorstring));
        custombtn.setTextSize(textsize);
        workdaybtn.setTextColor(Color.parseColor(colorstring));
        workdaybtn.setTextSize(textsize);
    }
    public static float dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (dpValue * scale + 0.5f);
    }

    private void OnIntervelbuttonPressed(int State){
      nCalendar.setPoint(getPointList(State,intervel,startmills));
    };
    private void OnIntervelbuttonCancalPressed(){
        nCalendar.setPoint(getSingleDayPointList());
    };
    private Boolean IsTimeBeforecurrent(int hour,int minute,int second){
        Calendar localTime=Calendar.getInstance();
        localTime.setTime(new Date());
       // int currenthour=(localTime.get(Calendar.HOUR)+8)>23?(localTime.get(Calendar.HOUR)-16):(localTime.get(Calendar.HOUR)+8);
        int currenthour=localTime.get(Calendar.HOUR_OF_DAY);
        int currentminute=localTime.get(Calendar.MINUTE);
        int currentsecond=localTime.get(Calendar.SECOND);
        Log.d("TAG","Hour=="+currenthour+"Minute=="+currentminute+"Second=="+currentsecond);
        if(hour<currenthour){
            return true;
        }
        else if(hour==currenthour){
            if(minute<currentminute){
                return true;
            }
            else if(minute==currentminute){
                if(second<currentsecond){
                    return true;
                }
                else {
                    return false;
                }
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }
   public interface MyTimeWindowButtonlistener{
        //确定按钮点击操作
         void OnSurebuttonClick(long startmills,int intevel,int Type,int Hour,int Minute,int Second);
        //取消按钮点击操作
         void OnCancalbuttonClick();
    }
}
