package com.example.note.justdo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.note.justdo.Amap.NewMap;
import com.example.note.justdo.MainLayoutTools.listrecyclerAdapter;
import com.example.note.justdo.MainLayoutTools.mLinearLayout;
import com.example.note.justdo.MainLayoutTools.mRecyclerview;
import com.example.note.justdo.TimeReminder.MyTimeWindow;
import com.example.note.justdo.TimeReminder.TimeManger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.support.v7.widget.helper.ItemTouchHelper.ACTION_STATE_DRAG;
import static android.support.v7.widget.helper.ItemTouchHelper.ACTION_STATE_SWIPE;

public class MainActivity extends AppCompatActivity  {
    //获取数据库
    Eventdaomanger eventdaomanger = getApp().getEventdaomanger();
    DaoSession daoSession = eventdaomanger.getDaoSession();
    //
    int listid = getApp().getListid();//获取当前列表id
    //软键盘高度获取，以确定时间（地点）提醒按钮显示位置
    int keybroadheight;//软键盘高度
    Boolean firstset=true;//第一次获取界面初始高度
    int voiddifferenceheight;//界面初始高度
    private int currentpos;//用于change时获取event
   // List<Event> eventList;//event列表，来源于数据库
    com.example.note.justdo.MainLayoutTools.mLinearLayout mLinearLayout;//子自定义布局，实现了下拉添加
    Button placebtn;//地点提醒按钮
    Button timebtn;//时间提醒按钮
    Boolean timeset=false;//是否进行了时间提醒窗口操作
    Bitmap bitmap;//用于绘制传递当前界面作为多列表的背景
    mRecyclerview listrecyclerview;//主界面列表管理
    com.example.note.justdo.MainLayoutTools.listrecyclerAdapter listrecyclerAdapter;//列表适配器
  //  ItemTouchHelper helper;//recyclerview手势辅助管理
    Event event;//记录当前选中事件
    InputMethodManager im;//此处用于管理软键盘
    ConstraintLayout constraintLayout;//主布局

    //时间提醒部分
    TimeManger timeManger;
    MyTimeWindow myTimeWindow;//时间提醒设置窗口
    long Cstartmills;//记录当前设置时间
    int Cintevel;//当前设置提醒间隔
    int CType;//当前设置提醒类型
    //
    Bitmap voidBitmap=getApp().getVoidviewbackground();
    EditText addedit;//添加栏
    ScaleAnimation missanim;


//撤销删除相关
    LinearLayout deletereminder;//是否撤销删除
    Button deleteback;//撤销删除按钮
    View.OnClickListener onClickListener;//撤销删除按钮的监听
    boolean lastdeal;//上次撤销完成是否处理完成
    boolean allowdelete;//是否允许删除

    //摇一摇监听
    private SensorManager sensorManager;
    private Vibrator vibrator;
    private static final int UPTATE_INTERVAL_TIME = 50;
    private static final int SPEED_SHRESHOLD = 30;//这个值调节灵敏度
    private long lastUpdateTime;
    private float lastX;
    private float lastY;
    private float lastZ;
    private Sensor sensor;

    private static final String TAG = "TestSensorActivity";
    private static final int SENSOR_SHAKE = 10;
    long time = System.currentTimeMillis();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
      //  getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timeManger=new TimeManger();//时间提醒管理类
        //用于获取软键盘高度的监听
        setEditkeybroadListener(getWindow().getDecorView());
        //
        constraintLayout=findViewById(R.id.mainlayout);//主布局
        im = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        List<Event> eventList = eventdaomanger.getfinalEventlist(listid);//获取列表
        initrecyclerview(eventList);
        placebtn=findViewById(R.id.placebtn);//地点提醒按钮

        placebtn.setVisibility(View.INVISIBLE);//初始不可见
        placebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // placebtn.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(MainActivity.this, NewMap.class);
                startActivity(intent);
            }
        });
        timebtn=findViewById(R.id.timebtn);//时间提醒按钮
        timebtn.setVisibility(View.INVISIBLE);//初始不可见
        timebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击时收起软键盘，隐藏时间提醒按钮
                im.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                timebtn.setVisibility(View.INVISIBLE);
                placebtn.setVisibility(View.INVISIBLE);
                //显示时间设置窗口
              showTimeWindowFromBottom();

            }
        });
        deletereminder=findViewById(R.id.deletelayout);//撤销删除布局
        deleteback=deletereminder.findViewById(R.id.deleteback);//撤销删除按钮
        onClickListener=new View.OnClickListener() {//撤销删除按钮监听
            @Override
            public void onClick(View v) {
                //     allowdelete=false;
                Log.d("TAG","CLICK...");
                lastdeal=true;//标记上次撤销删除选择界面已处理
                listrecyclerAdapter.recoverremove(event,currentpos);//撤销删除，恢复原位置event
                allowdelete=false;//不允许执行数据库删除（用于OnAnimationstop方法中的判断）
                deletereminder.clearAnimation();//清空原有动画（同时执行OnAnimationstop方法）
                deletereminder.setVisibility(View.INVISIBLE);//令该布局消失

            }
        };
        deleteback.setOnClickListener(onClickListener);

        mLinearLayout = findViewById(R.id.mlinlayout);//子主布局
        addedit=mLinearLayout.findViewById(R.id.medittext);
        //初始化
      //  mLinearLayout.setTransitionName(Integer.toString(listid));
      //  Log.d("TAG","transitionName="+mLinearLayout.getTransitionName());
        mLinearLayout.init(getApplicationContext(), MainActivity.this, timebtn,placebtn);
        mLinearLayout.setmeditOnActionListener(new mLinearLayout.MeditOnActionListener() {
            @Override
            public void editOnActionListener(int i,String flags) {
                if (i == EditorInfo.IME_ACTION_DONE) {//编辑回车键监听
                    switch (flags) {
                        case "add":onaddfinished();
                            break;
                        case "change":onchangefinished();
                            break;
                    }
                }
            }

            @Override
            public void OnactivityChanged() {//跳转到主列表时
                bitmap = getfinalBitmap(getApplicationContext(), listrecyclerview);//获取当前屏幕截图
                getApp().setViewbackground(bitmap);//借助Application暂存背景数据
                setvoidBitmap(MainActivity.this);//暂存默认带边框背景
               // constraintLayout.setTransitionName(Integer.toString(listid));
               // startActivity(new Intent(MainActivity.this, TActivity.class), ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, constraintLayout, Integer.toString(listid)).toBundle());
                startActivity(new Intent(MainActivity.this,TActivity.class));
                finish();
                overridePendingTransition(R.anim.tactivity_enter_anim,R.anim.mainactivity_miss_anim);
            }

            @Override
            public void OnTouchFinished(String flags) {//编辑状态时点击空白区域监听，与回车监听相同
                switch (flags) {
                    case "add":onaddfinished();
                        break;
                    case "change":onchangefinished();
                        break;
                }
            }

            @Override
            public void Onedittextshow() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ConstraintLayout.LayoutParams layoutParams=(ConstraintLayout.LayoutParams)timebtn.getLayoutParams();
                        ConstraintLayout.LayoutParams layoutParams1=(ConstraintLayout.LayoutParams)placebtn.getLayoutParams();
                        if(layoutParams.bottomMargin<keybroadheight){
                            layoutParams.bottomMargin=keybroadheight;
                            layoutParams1.bottomMargin=keybroadheight;
                            timebtn.setLayoutParams(layoutParams);
                            placebtn.setLayoutParams(layoutParams1);}
                        if(timebtn.getVisibility()==View.INVISIBLE&&keybroadheight>200){
                        timebtn.setVisibility(View.VISIBLE);
                        placebtn.setVisibility(View.VISIBLE);}
                        Log.d("TAG","BTNSETNOW");
                    }
                },200);
            }
        });

        //摇一摇
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        listrecyclerAdapter.notifyDataSetChanged();
        if (sensorManager != null) {
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        if (sensor != null) {
            sensorManager.registerListener(sensorEventListener,
                    sensor,
                    SensorManager.SENSOR_DELAY_GAME);//这里选择感应频率
        }
    }
    @Override
    protected  void onPause(){
        super.onPause();
        if (sensorManager != null) {// 取消监听器
            sensorManager.unregisterListener(sensorEventListener);
        }
    }

    @Override
    public void onBackPressed() {
        String flags=mLinearLayout.getFlags();
        switch (flags) {
            case "add":onaddfinished();
                break;
            case "change":onchangefinished();
                break;
            case "normal":super.onBackPressed();
                break;
        }
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event){
        //当时间提醒设置窗口显示时，令其他区域点击无效
        if(myTimeWindow!=null&&myTimeWindow.isShowing()){
            return false;
        }
        return super.dispatchTouchEvent(event);
    }

    /**
     * 添加完成回车/点击处理
     */
    private void onaddfinished(){
        if (TextUtils.isEmpty(addedit.getText().toString())) {
            mLinearLayout.finishedit();
            if (Cstartmills > 0) {
                initStartTime();
            }
        } else {
            Event newev = new Event(addedit.getText().toString(), getApp().getListid(), getApp().getListtitle());

            if(getApp().getRadius()!=0){
            }
            //时间提醒设置
            if (Cstartmills > 0) {
                newev.setStartmills(Cstartmills);
                newev.setIntervel(Cintevel);
                newev.setType(CType);
                if (CType > 0 && (Cstartmills < (new Date().getTime()))) {
                    newev.getNextstartmills();
                }
                initStartTime();

            }
            int startpos = listrecyclerAdapter.getItemCount();
            daoSession.getEventDao().insert(newev);
            if (startpos == 0) {
                listrecyclerAdapter.addfirstitem(newev);
            } else if (startpos > 0) {
                //原先有数据时，由于添加至首位，需进行数据库换位操作
                eventdaomanger.updateSwapedevents(listid, startpos, 0, false);
                //         Collections.swap(listrecyclerAdapter.mEvent,listrecyclerAdapter.getItemCount()-1,0);
                listrecyclerAdapter.additemToFirst();
              //  listrecyclerview.scrollToPosition(0);
            }
            Log.d("TAG", "size==" + listrecyclerAdapter.getItemCount());
            if (eventdaomanger.IsTimeRemindExist(listid)) {
                timeManger.refreshTimeJobInOneList(listid);
            }
            mLinearLayout.finishedit();
        }
    }

    /**
     * 编辑完成回车/点击处理
     * 如果编辑完成时编辑框为空，且无时间提醒，则删除该event（有时间提醒则删除时间通知）
     * 不为空时，若内容改变时，进行更新（进行了时间设置也进行更新）
     */
    private void onchangefinished(){
        Event event=listrecyclerAdapter.getEventBypos(currentpos);
        Boolean needupdate=false;
        String currentcontent=addedit.getText().toString();
        //编辑框空处理
        if(TextUtils.isEmpty(currentcontent)){
            if(timeset&&Cstartmills==0&&event.getStartmills()>0)
            {  timeManger.cancalJobByIdTAG(Long.toString(event.getId()));
                daoSession.getEventDao().deleteByKey(event.getId());
                listrecyclerAdapter.removeitem(currentpos);
                timeset=false;
                return;
            }
            else if((event.getStartmills()==0&&Cstartmills==0)){
                daoSession.getEventDao().deleteByKey(event.getId());
                listrecyclerAdapter.removeitem(currentpos);
                timeset=false;
                return;
            }
        }
        //内容不同时，需要更新
        if(!currentcontent.equals(event.getContext())) {
            event.setContext(currentcontent);
            needupdate=true;
        }
        //取消时间提醒时，需要更新
        if(timeset&&Cstartmills==0&&event.getStartmills()>0){
            event.setStartmills(Cstartmills);
            event.setIntervel(Cintevel);
            event.setType(CType);
            timeManger.cancalJobByIdTAG(Long.toString(event.getId()));
            needupdate=true;
        }
        //进行时间编辑，需要更新
        if(Cstartmills>0){
            event.setStartmills(Cstartmills);
            event.setIntervel(Cintevel);
            event.setType(CType);
            if(CType>0&&(Cstartmills<(new Date().getTime()))){
                event.getNextstartmills();
            }
            needupdate=true;
        }
        //编辑框收起动画，初始化数据
        mLinearLayout.finishedit();
        //刷新，更新
        if(needupdate){
        listrecyclerAdapter.notifyItemChanged(currentpos);
        daoSession.getEventDao().update(event);
        Log.d("TAG","id=="+event.getId());
        if((Cstartmills>0)&&(event.getStartmills()>(new Date().getTime()))){
            timeManger.changeTimeJob(event.getId(),event.getContext(),event.getStartmills(),event.getIntervel(),event.getType());
        }
        initStartTime();
        }
        timeset=false;
    }

    /**
     * 初始化列表（adapter，手势监听接口实现，init...）
     * @param eventlist 来源于数据库的列表list
     */
    private void initrecyclerview(List<Event> eventlist){
        final ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                //首先回调的方法 返回int表示是否监听该方向
                int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;//上下拖动
                int swipeFlags = ItemTouchHelper.LEFT;//左滑删除
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            /**
             * 拖拽操作逻辑处理，从拖拽开始到结束，移动过程中调用
             * @param recyclerView
             * @param viewHolder 拖动的item项
             * @param target 移动到的位置对应item项
             * @return
             */
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                //拖动事件
                if(!listrecyclerview.getDrag()){
                    listrecyclerview.setDrag(true);}
                Log.d("TAG", "has been moved from" + viewHolder.getAdapterPosition() + "to position" + target.getLayoutPosition());
                if (viewHolder != target) {//item对应viewholder位置改变时
                    /**
                     * ***********************
                     * 此处你无须作数据库内更新，因为你将在拖动完成后onclearview方法中更新数据库
                     * 你只需对adapter内部的eventlist作换位操作（先删除后添加）
                     */
                    eventdaomanger.updateSwapedevents(listid, viewHolder.getAdapterPosition(), target.getAdapterPosition());//数据库内交换位置
                    //************************
                    Event moveevent = listrecyclerAdapter.getEventBypos(viewHolder.getAdapterPosition());//
                    listrecyclerAdapter.removeitembeforeadd(viewHolder.getAdapterPosition());
                    listrecyclerAdapter.additemafterremove(moveevent,target.getAdapterPosition());
                    //   listrecyclerAdapter.updateeventlist();//更新adapter内部eventlist
                    Log.d("TAG", moveevent.getContext() + "has been moved from" + viewHolder.getLayoutPosition() + "to position" + target.getLayoutPosition());
                    listrecyclerAdapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                    Log.d("TAG", moveevent.getContext() + "has been moved from" + viewHolder.getAdapterPosition() + "to position" + target.getAdapterPosition());
                    return true;
                    //     listrecyclerAdapter.notifyItemRangeChanged(Math.min(target.getAdapterPosition(),viewHolder.getAdapterPosition()),Math.abs(viewHolder.getAdapterPosition()-target.getAdapterPosition())+1);
                }
                return false;
            }

            @Override
            public boolean isLongPressDragEnabled() {
                //是否可拖拽
                return false;
            }

            /**
             * 当且仅当item处于拖拽状态时
             * 需要进行item样式的更改（目前为通过改变背景方式）
             * @param viewHolder 所选item对应的holder
             * @param actionState 当前手势模式
             */

            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                if(actionState==ACTION_STATE_SWIPE){
                    listrecyclerview.setSwipe(true);
                    Log.d("TAG","swiping...");}
                if(!listrecyclerview.getRightSlip()&&actionState==ACTION_STATE_DRAG){//没有右滑且为拖动状态
                    if(viewHolder.itemView.getBackground()==null)//若未更改样式（默认背景为null）
                        viewHolder.itemView.setBackgroundResource(R.drawable.itemshadow);}//则更改样式
                super.onSelectedChanged(viewHolder, actionState);
            }

            /**
             * 滑动，拖拽操作结束时执行（右滑结束时也执行，因为判断选中了item）
             * 主要处理的是拖拽操作结束，因为滑动删除的处理已在onSwiped方法中执行
             * 作样式的更改，adapter内部eventlist刷新，以及一些布尔参数的初始化（有时间提醒存在时，刷新时间提醒）。
             * @param recyclerView
             * @param viewHolder
             */
            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                viewHolder.itemView.setBackground(null);
                if(listrecyclerview.getDrag()){
                    /**
                     * **********************
                     * 该方法为根据数据库更新adapter内的eventlist
                     * 你需要在此处将方法改为根据adapter内的eventlist按位更新数据库内容
                     */
                    listrecyclerAdapter.updateeventlist();
                    if (eventdaomanger.IsTimeRemindExist(listid)) {//如果这些事件中存在时间提醒，通知服务
                        timeManger.refreshTimeJobInOneList(listid);
                        Log.d("TAG", "on moved===");
                    }
                    //***********************
                    listrecyclerview.setDrag(false);
                }

                listrecyclerview.setSwipe(false);
                listrecyclerview.setRightSlip(false);
                //拖动事件完成后，通知服务作时间提醒变更,原因在于位置交换影响了原数据库内的id-event对应关系，而通知操作依赖于id的传入。
                // （详情见eventdaomanger类中的数据换位操作及TimeReminderService类）
            }

            @Override
            public boolean canDropOver(RecyclerView recyclerView, RecyclerView.ViewHolder current, RecyclerView.ViewHolder target) {
                //该函数用于控制当前事件能否移动到目标位置
                //当前设定为：未完成事件只能在未完成事件中移动，已完成事件只能在已完成事件中移动
                int eventsnum=listrecyclerAdapter.getEventsnum();//获取当前未完成事件数目，确定位置分界点
                if(current.getAdapterPosition()>eventsnum-1){
                    if(target.getAdapterPosition()>eventsnum-1){
                        return true;
                    }
                    else {
                        return false;}
                }
                else {if(target.getAdapterPosition()<eventsnum){
                    return true;
                }
                else {
                    return false;
                }
                }
                //  return super.canDropOver(recyclerView, current, target);
            }

            /**
             * 左滑删除结束的处理
             * @param viewHolder
             * @param direction
             */
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                if(!lastdeal&&event!=null){
                    deletereminder.clearAnimation();
                    deletereminder.setVisibility(View.VISIBLE);
                    Log.d("TAG","is in layout ");
                }
                allowdelete=true;

                //左滑删除
                //移除adapter，数据库内对应event
                event = listrecyclerAdapter.getEventBypos(viewHolder.getAdapterPosition());
                currentpos=viewHolder.getAdapterPosition();
                if (event != null) {listrecyclerAdapter.removeitem(viewHolder.getAdapterPosition());
                    deletereminder.setVisibility(View.VISIBLE);
                    delaydelete(event,currentpos);
                }
            }
        });
        // recyclerview adapter
        listrecyclerAdapter = new listrecyclerAdapter(this, getLayoutInflater(), eventlist);//设置适配器
        //实现adapter内部接口
        listrecyclerAdapter.setMlistviewOnClickListener(new listrecyclerAdapter.MlistviewOnClickListener() {
            @Override
            /**
             * 此处为点击item事件进行编辑跳转，实现你自己的跳转方式即可
             */
            public void OnitemClickListener(View v, int pos) {//事件点击响应
                mLinearLayout.editTurnChange();
                currentpos=pos;
                String currentString=listrecyclerAdapter.getEventBypos(pos).getContext();
                Log.d("TAG","content=="+currentString);
                addedit.setText(currentString.toCharArray(),0,currentString.length());
                addedit.setSelection(currentString.length());
            }

            @Override
            public void OnDragStart(RecyclerView.ViewHolder viewHolder) {
                helper.startDrag(viewHolder);//长按拖动时手动调用，itemtouchhelper提供的方法
            }
        });

        listrecyclerview =findViewById(R.id.mlistrecyclerview);
        listrecyclerview.setLayoutManager(new LinearLayoutManager(this));//线性布局管理
        listrecyclerview.setAdapter(listrecyclerAdapter);//适配器
        listrecyclerAdapter.setRecyclerView(listrecyclerview);//自定义方法，用于传递recyclerview至适配器内
        helper.attachToRecyclerView(listrecyclerview);
    }
    private void showTimeWindowFromBottom(){
        //自定义pupwindow用于设置时间提醒
        myTimeWindow=new MyTimeWindow(MainActivity.this);
        myTimeWindow.init(MainActivity.this);
        //设置其他区域点击无效
        myTimeWindow.setFocusable(false);
        myTimeWindow.setOutsideTouchable(false);
        //
        //出现在屏幕底部
        myTimeWindow.showAtLocation(findViewById(R.id.mainlayout), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
        delay1(myTimeWindow);//防止myTimeWindow绘制未完成，延迟进行日历初始化，
        myTimeWindow.setMyTimeWindowButtonlistener(new MyTimeWindow.MyTimeWindowButtonlistener() {
            @Override
            //确定按钮点击监听
            public void OnSurebuttonClick(long startmills, int intevel, int Type,int Hour,int Minute,int Second) {
                if(!myTimeWindow.getTimeSelected()){//如果未选择具体时间，作提示，窗口状态不变
                    Toast.makeText(MainActivity.this, "请选择具体时间", Toast.LENGTH_SHORT).show();
                }
                else {//否则，暂存当前设置时间，完成时间设置，窗口消失
                    timeset=true;
                    Cstartmills = startmills+((Hour*3600)+(Minute*60)+Second)*1000;
                    Cintevel = intevel;
                    CType = Type;
                    Toast.makeText(MainActivity.this, "确定", Toast.LENGTH_SHORT).show();
                    myTimeWindow.dismiss();//窗口消失
                         im.showSoftInput(addedit,0);//软键盘重新弹出
                    timebtn.setVisibility(View.VISIBLE);//时间提醒按钮重新显示
                    placebtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void OnCancalbuttonClick() {//取消，暂时理解为取消原有时间提醒
                timeset=true;//标记进行了时间设置
                initStartTime();
                Toast.makeText(MainActivity.this,"取消",Toast.LENGTH_SHORT).show();
                myTimeWindow.dismiss();//窗口消失
                im.showSoftInput(addedit,0);//软键盘重新跳出
                timebtn.setVisibility(View.VISIBLE);//时间提醒按钮重新显示
                placebtn.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * 延迟设置时间窗口的时间信息，因为初始化需等待窗口绘制完毕
     * @param myTimeWindow 时间提醒设置窗口
     */
    private void delay1(final MyTimeWindow myTimeWindow){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //“更改”状态时需判断原事件是否具备时间提醒
                if((mLinearLayout.getFlags().equals("change"))&&Cstartmills==0&&listrecyclerAdapter.getEventBypos(currentpos).getStartmills()>0){
                    //如果该事件有时间提醒，初始化窗口的时间信息
                    myTimeWindow.setEvent(listrecyclerAdapter.getEventBypos(currentpos));
                }
                //另外，如果之前开启过该窗口，重新开启时，应保留上一次窗口的时间设置（同一次“添加”/“更改”过程操作中）
                if(Cstartmills>0){
                myTimeWindow.initCalendar(Cstartmills,Cintevel,CType);}
               Log.d("TAG","waiting...");
            }
        },500);
    }

    /**
     * 每次添加/更改事件完成后调用，初始化暂存时间
     */
    private void initStartTime(){
        Cstartmills=0;
        Cintevel=0;
        CType=0;
    }

    /**
     * （暂不使用）
     * 该方法计算了向多列表跳转时的界面缩放位置，根据当前列表的listid决定
     * @param listid
     */
    private void caculateScalePosition(int listid){
        int px,py=0;
        int btnwidth=dip2px(this,100);
        int viewheight=dip2px(this,260);
        int fullheight=dip2px(this,62)+viewheight;
        int marginleft=dip2px(this,60);
        int halfscreenwidth=getDisplayMetrics(this)[0]/2;
        int halfscreenheight=getDisplayMetrics(this)[1]/2;
        int firstbtnpx=marginleft+halfscreenwidth-btnwidth/2;
        int firstbtnpy=dip2px(this,86);
        int fullscreenviewnum=2*2*halfscreenheight/fullheight;
        if(listid>fullscreenviewnum){
            //设置TActivity中的recyclerview初始化位置
        }
        int position=listid%fullscreenviewnum==0?fullscreenviewnum:(listid%fullscreenviewnum);
       if(position%2==1){//左边一列
           px=firstbtnpx;
           int i=(position+1)/2;
           if(i==1){
               py=firstbtnpy;
           }
           else if(i>1){
               py=firstbtnpy+(i-1)*fullheight;
           }
       }
       else {//右边一列
          px=halfscreenwidth*2-firstbtnpx-btnwidth;
          int i=position/2;
          if(i==1){
              py=firstbtnpy;
          }
          else {
              py=firstbtnpy+(i-1)*fullheight;
          }
       }
       float pivotX=(px/2)/halfscreenwidth;
       float pivotY=(py/2)/halfscreenheight;
        missanim=new ScaleAnimation(1,(float) (262.5 / halfscreenwidth)/2,1,(float) (446.25 / halfscreenheight)/2,pivotX,pivotY);
        missanim.setDuration(1000);
    }

    /**
     * 全局window状态的监听设置，用于确定软键盘高度
     * @param view
     */
    private void setEditkeybroadListener(View view) {
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            //当键盘弹出隐藏的时候会 调用此方法。
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                //获取当前界面可视部分
                MainActivity.this.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
                //获取屏幕的高度
                int screenHeight = MainActivity.this.getWindow().getDecorView().getRootView().getHeight();
                int heightDifference=0;
                //考虑到界面初始状态屏幕高度与可视高度存在的差值，该方法首次调用时先获取该初始差值
                if(voiddifferenceheight==0){
                heightDifference = screenHeight - r.bottom;}
                //仅执行一次
                if(firstset){
                    voiddifferenceheight=heightDifference;
                    firstset=false;
                }
                //软键盘弹出情况下，软键盘高度即为整个屏幕的高度-可视部分的高度-初始偏差
                //+10 是为了让时间按钮稍微向上显示的偏移量，可自定义
                if(voiddifferenceheight>0){
                    heightDifference=10+screenHeight-r.bottom-voiddifferenceheight;
                }
                //考虑到heightdifference在软键盘弹出与否值不同，应将软键盘高度keybroadheight取为其最大值
                if(heightDifference>keybroadheight){
                    keybroadheight=heightDifference;
                    //第一次软键盘弹出，高度计算完成后，按钮立即显示
                    if(mLinearLayout.isIseditShow()&&keybroadheight>200&&timebtn.getVisibility()==View.INVISIBLE){
                        ConstraintLayout.LayoutParams layoutParams=(ConstraintLayout.LayoutParams)timebtn.getLayoutParams();
                        ConstraintLayout.LayoutParams layoutParams1=(ConstraintLayout.LayoutParams)placebtn.getLayoutParams();
                        layoutParams1.bottomMargin=keybroadheight;
                        layoutParams.bottomMargin=keybroadheight;
                        timebtn.setLayoutParams(layoutParams);
                        timebtn.setVisibility(View.VISIBLE);
                        //第一次显示位置提醒
                        placebtn.setVisibility(View.VISIBLE);
                        placebtn.setLayoutParams(layoutParams1);
                    }
                }
                Log.d("Keyboard Size", "Size: " + heightDifference);
            }

        });
    }

    private App getApp() {
        return App.getInstance();
    }

    public static int dip2px(Context context, float dpValue) {//dp转px
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
//绘制当前屏幕图像，原理是将事件依次绘制到屏幕大小的画布上。
    public static Bitmap createBitmap(RecyclerView listView, Context context) {
        //if (listView.getLayoutManager().getItemCount() != 0) {
            int width, height = 0;
            Bitmap bitmap;
            int yPos = 0;
            int listItemNum;
            List<View> childViews = null;
            Canvas canvas;
            width = getDisplayMetrics(context)[0];//宽度等于屏幕宽
            height=getDisplayMetrics(context)[1];//高度等于屏幕高
            Log.d("TAG", "width=" + width + "  " + "height=" + getDisplayMetrics(context)[1]);

            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) listView.getLayoutManager();
            listItemNum = linearLayoutManager.findLastVisibleItemPosition()+1;
                    //linearLayoutManager.getChildCount();
            childViews = new ArrayList<View>(listItemNum);
            View itemView;
            //计算整体高度:
            for (int pos = 0; pos < listItemNum; ++pos) {
                itemView = linearLayoutManager.getChildAt(pos);
                //measure过程
                itemView.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                childViews.add(itemView);
              //  height = itemView.getMeasuredHeight();
            }

            // 创建对应大小的bitmap
            bitmap = Bitmap.createBitmap(listView.getWidth(), height,
                    Bitmap.Config.ARGB_8888);
            //bitmap = BitmapUtil.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            canvas = new Canvas(bitmap);

            Bitmap itemBitmap;
            int childHeight;
            //把每个ItemView生成图片，并画到背景画布上
            for (int pos = 0; pos < childViews.size(); ++pos) {
                itemView = childViews.get(pos);
                childHeight = itemView.getMeasuredHeight();
                itemBitmap = viewToBitmap(itemView, width, childHeight);
                if (itemBitmap != null) {
                    canvas.drawBitmap(itemBitmap, 0, yPos, null);
                }
                yPos = childHeight + yPos;
            }
            Paint paint=new Paint();
            paint.setColor(Color.parseColor("#000000"));
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2);
            paint.setShadowLayer(2,-2,0,Color.parseColor("#808080"));
            canvas.drawRect(1,1,width-1,height-1,paint);
            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();
            return bitmap;
    }
//将之前的全屏图像缩小，用于暂存作背景
    private static Bitmap getfinalBitmap(Context context, RecyclerView listView) {
        Bitmap bitmap = createBitmap(listView, context);
        int width=getDisplayMetrics(context)[0];
        int height=getDisplayMetrics(context)[1];
        if (bitmap != null) {
            Matrix matrix = new Matrix();
            matrix.postScale((float) 262.5 / width, (float) 446.25 / height);
            Bitmap finbitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix,
                    true);
            return finbitmap;
        }
        return null;
    }
    //绘制默认带边框背景
    private void setvoidBitmap(Context context){
        if(voidBitmap==null) {
            int width = getDisplayMetrics(context)[0];//宽度等于屏幕宽
            int height = getDisplayMetrics(context)[1];//高度等于屏幕高
            Bitmap bitmap1 = Bitmap.createBitmap(width, height,
                    Bitmap.Config.ARGB_8888);
            Canvas canvas=new Canvas(bitmap1);
            Paint paint=new Paint();
            paint.setColor(Color.parseColor("#000000"));
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2);
            paint.setShadowLayer(2,-2,0,Color.parseColor("#808080"));
            canvas.drawRect(1,1,width-1,height-1,paint);
            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();
            if (bitmap1!= null) {
                Matrix matrix = new Matrix();
                matrix.postScale((float) 262.5 / width, (float) 446.25 / height);
                Bitmap finbitmap = Bitmap.createBitmap(bitmap1, 0, 0, bitmap1.getWidth(), bitmap1.getHeight(), matrix,
                        true);
                getApp().setVoidviewbackground(finbitmap);
            }

        }
    }

    private static Bitmap viewToBitmap(View view, int viewWidth, int viewHeight) {
        view.layout(0, 0, viewWidth, viewHeight);
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }
//获取屏幕宽高数组
    public static final Integer[] getDisplayMetrics(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getApplicationContext().getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        return new Integer[]{screenWidth, screenHeight};
    }
    /**
     * 于onswiped方法中调用，滑动删除过后弹出是否撤销删除的布局
     * 该方法实现了该布局的动画效果（维持一段时间后逐渐消失）
     * @param event 传入删除的event，以便之后在数据库中删除
     * @param pos 被删除的event对应adapter中的位置，可不使用
     */
    private void delaydelete(final Event event, final int pos){
        //动画效果
        final Animation missanim= AnimationUtils.loadAnimation(MainActivity.this,R.anim.deleteback_miss);
        //设置动画监听，主要是end方法
        missanim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(allowdelete){//如果之前布局消失前未点击撤销删除
                    //则进行数据库，及时间提醒（若存在）的清除
                    lastdeal=true;//表示处理完成，该变量主要考虑到连续的删除情况
                    if(event.getStartmills()>(new Date().getTime())){//如果该事件存在时间提醒
                        timeManger.cancalJobByIdTAG(Long.toString(event.getId()));//移除其通知服务
                    }
                    //******************
                    /**
                     * *********************
                     * 此处为在数据库中删除该event，更改为sqlite的方法即可
                     */
                    eventdaomanger.deleteByid(event);
                    Log.d("TAG","delete"+event.getContext());
                    Toast.makeText(MainActivity.this, event.getContext() + "被删除了",
                            Toast.LENGTH_SHORT).show();
                    //**********************
                    Log.d("TAG","remove...");
                    //布局消失
                    deletereminder.setVisibility(View.INVISIBLE);
                }
                Log.d("TAG","end...");
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        /**
         * 之所以要延迟开始动画及初始化lastdeal
         * 原因在于考虑到连续删除时，需先清除原有布局动画（clearanimation方法），同时也会调用onAnimationEnd方法
         * 而onAnimationEnd占用的不是主线程，而是单独的线程，如果不进行延迟初始化
         * 则会造成初始化先执行，再执行onAnimationEnd中的方法（该方法中将lastdeal赋值为true）
         * 原本的结果应是初始化完成后lastdeal为false，此时初始化的最终结果则为true（false→true）
         * 造成下次连续删除时，无法执行在数据库中对上一个event事件的清除（详情将onswiped方法）
         * 于是需延迟一小段时间后进行初始化
         */
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                deletereminder.startAnimation(missanim);
                lastdeal=false;
            }
        },200);

        Log.d("TAG","waiting...");
    }


//监听器
    /**
     * 重力感应监听
     */
//
//    TimerTask task = new TimerTask() {
//        public void run() {
//            vibrator.vibrate(300);
//            Log.i(TAG, "检测到摇晃，执行操作！");
//            //每次需要执行的代码放到这里面。
//        }
//    };

    private SensorEventListener sensorEventListener = new SensorEventListener() {


        @Override
        public void onSensorChanged(SensorEvent event) {
            long currentUpdateTime = System.currentTimeMillis();
            long timeInterval = currentUpdateTime - lastUpdateTime;
            if (timeInterval < UPTATE_INTERVAL_TIME) {
                return;
            }
            lastUpdateTime = currentUpdateTime;
            long nTime=System.currentTimeMillis();
// 传感器信息改变时执行该方法
            float[] values = event.values;
            float x = values[0]; // x轴方向的重力加速度，向右为正
            float y = values[1]; // y轴方向的重力加速度，向前为正
            float z = values[2]; // z轴方向的重力加速度，向上为正
            float deltaX = x - lastX;
            float deltaY = y - lastY;
            float deltaZ = z - lastZ;


            lastX = x;
            lastY = y;
            lastZ = z;
            double speed = (Math.sqrt(deltaX * deltaX + deltaY * deltaY
                    + deltaZ * deltaZ) / timeInterval) * 100;
            if (speed >= SPEED_SHRESHOLD&&nTime-time>1000) {
                vibrator.vibrate(300);
                Log.i(TAG, "检测到摇晃，执行操作！");
                eventdaomanger.deletefinishedEvent(listid);
                listrecyclerAdapter.removefinishedevents();
                time=nTime;
            }
        }


        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

}
//           // Log.i(TAG, "x轴方向的重力加速度" + x + "；y轴方向的重力加速度" + y + "；z轴方向的重力加速度" + z);
//            // 一般在这三个方向的重力加速度达到40就达到了摇晃手机的状态。
//            int medumValue = 19;// 三星 i9250怎么晃都不会超过20，没办法，只设置19了
//            if (Math.abs(x) > medumValue || Math.abs(y) > medumValue || Math.abs(z) > medumValue) {
//                Log.i(TAG, "x轴方向的重力加速度" + x + "；y轴方向的重力加速度" + y + "；z轴方向的重力加速度" + z);
//                vibrator.vibrate(200);
//                Message msg = new Message();
//                msg.what = SENSOR_SHAKE;
//                handler.sendMessage(msg);
//            }
//        }
//
//        @Override
//        public void onAccuracyChanged(Sensor sensor, int accuracy) {
//
//        }
//    };
//
//    /**
//     * 动作执行
//     */
//    @SuppressLint("HandlerLeak")
//    Handler handler = new Handler() {
//
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case SENSOR_SHAKE:
//                    Toast.makeText(MainActivity.this, "检测到摇晃，执行操作！", Toast.LENGTH_SHORT).show();
//                    Log.i(TAG, "检测到摇晃，执行操作！");
//                    break;
//            }
//        }
//
//    };









