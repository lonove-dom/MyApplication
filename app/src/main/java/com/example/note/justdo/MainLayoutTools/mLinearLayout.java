package com.example.note.justdo.MainLayoutTools;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.NestedScrollingParent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.note.justdo.R;

import static android.content.ContentValues.TAG;
import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by Choz on 2018/4/1.
 */

public class mLinearLayout extends LinearLayout implements NestedScrollingParent {
    private Context cont;
    private AppCompatActivity appcomp;
    private boolean iseditShow;//删除组件是否显示
    private boolean allowintercept = true;
    private int mEditHeight; //编辑组件的宽度
    private int msetlayoutHeight=(int)dp2px(180); //设置跳转组件的宽度
    private LinearLayout.LayoutParams editLayoutParams;//edittext的布局参数
    private LinearLayout.LayoutParams settingLayoutParams;//setting的布局参数
    private ConstraintLayout.LayoutParams shadowLayoutParams;//阴影蒙层的布局参数
    private int mDownX;//手指初次按下的X坐标 初始化为0
    private int mDownY;//手指初次按下的Y坐标 初始化为0
    //用于回调的内部接口
    private MeditOnActionListener meditOnActionListener;

    /**用于判断edittext当前状态为添加还是更改
     * @params "add" / "change"/"normal"
     */
    private String flags="normal"; //默认为主界面状态
    private String State=null;//上拉/下拉状态标识（PULLUP/PULLDOWN)
    private final String Upstate="PULLUP";
    private final String Downstate="PULLDOWN";

    //布局内基本控件
    private EditText editText;//嵌套于editll内，两者等大
    private LinearLayout editll;
    private LinearLayout setting;
    mRecyclerview mlistrecyclerview;
    //用于实现软键盘手动收弹操作
    InputMethodManager im;

    //主界面布局中的控件
    Button textbtn;//时间提醒按钮
    Button placebtn;

    //调控手势操作过程的判断参量，初始都为false
    private Boolean processmove=false;//是否拦截了移动
    private Boolean cancalintercept=false;//是否需要取消移动拦截（嵌套滑动时调用）
    private Boolean allowchange=false;

    //手势操作中需要的参量
    private int pointnumber;
    private int mDownX0;
    private int mDownX1;
    private int mDownY0;
    private int mDownY1;
    private int mNowX0;
    private int mNowX1;
    private int mNowY0;
    private int mNowY1;
    private int lastY;
    private ValueAnimator editshowanim;//更改事件时，edittext的跳出动画
    private ValueAnimator editmissanim;//完成编辑时，edittext的属性动画
    private ValueAnimator upanim;//up动作完成时，edittext的属性动画

    /**
     *
     * @param @可实现下拉添加的linearlayout
     * @param “顶端为edittext，正常情况下为隐藏状态
     *        “主要部分为recyclerview
     */

    public mLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //绑定布局
        View view = LayoutInflater.from(context).inflate(R.layout.mlayout, this, true);

        //根据id获取
        editText = view.findViewById(R.id.medittext);
        editll = view.findViewById(R.id.editll);
        setting=view.findViewById(R.id.setting);
        mlistrecyclerview = findViewById(R.id.mlistrecyclerview);
        //用于调整edittext位置
        editLayoutParams = (LinearLayout.LayoutParams) editll.getLayoutParams();
        settingLayoutParams=(LinearLayout.LayoutParams) setting.getLayoutParams();
        Log.d("TAG","bottom="+settingLayoutParams.bottomMargin);
        measureEditHeight();
        editLayoutParams.topMargin=-mEditHeight;
        editll.setLayoutParams(editLayoutParams);

        //edittext回车监听（接口实现于MainActivity）
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (meditOnActionListener != null) {
                    meditOnActionListener.editOnActionListener(i,flags);
                }
                return true;
            }
        });
        //编辑框初始状态为未显示时，向下出现的动画
        //差值器Interpolator为先快后慢型
        editshowanim=ValueAnimator.ofFloat(0f,1f);//参数从0~1变化
        editshowanim.setDuration(300);//0.3s
        editshowanim.setInterpolator(new DecelerateInterpolator(5));//先快后慢
        editshowanim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {//动画过程监听
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float property=animation.getAnimatedFraction();//动画进行百分比
                //控制edittext位置逐渐升高
                editLayoutParams.topMargin = -mEditHeight+(int)(mEditHeight*property);
                editll.setLayoutParams(editLayoutParams);
                //
            }
        });

        //编辑框初始状态为完全显示时，向上消失的动画
        //差值器Interpolator为先快后慢型
        editmissanim=ValueAnimator.ofFloat(0f,1f);//参数从0~1变化
        editmissanim.setDuration(300);//0.3s
        editmissanim.setInterpolator(new DecelerateInterpolator(5));//先快后慢
        editmissanim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {//动画过程监听
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float property=animation.getAnimatedFraction();//动画进行百分比
                //控制edittext位置逐渐升高
                editLayoutParams.topMargin = (int)(-mEditHeight*property);
                editll.setLayoutParams(editLayoutParams);
                //
            }
        });

        //编辑框未完全显示时（手势up事件处理），向上消失的动画
        //同理设置upanim,由于其动画初始位置受到手势影响，暂不设置监听
        upanim=ValueAnimator.ofFloat(0f,1f);
        upanim.setDuration(500);
        upanim.setInterpolator(new DecelerateInterpolator(5));
    }

    //初始化所需参数
    public void init(Context context, AppCompatActivity activity, Button textbtn,Button placebtn) {
        cont = context;
        appcomp = activity;
        this.textbtn = textbtn;
        this.placebtn=placebtn;
    }

    public String getFlags() {
        return flags;
    }

    //外部调用，以实现接口
    public void setmeditOnActionListener(MeditOnActionListener meditOnActionListener1) {
        meditOnActionListener = meditOnActionListener1;
    }

    //内部接口类
    public interface MeditOnActionListener {
        //软键盘回车监听
        void editOnActionListener(int i,String flags);

        //向多列表activity过渡时调用
        void OnactivityChanged();

        //（点击空白区域）添加完成时调用
        void OnTouchFinished(String flags);

        //编辑框出现时调用
        void Onedittextshow();

        //触发向设置界面跳转时调用
        void Onsettingshow();
    }

    /*  @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        Boolean editshow;
        Boolean editmiss;
        editshow=dy<0&&(getScrollY()>0)&&!mlistrecyclerview.canScrollVertically(-1);
        editmiss=dy>0&&getScrollY()<mEditHeight;
        if(editshow||editmiss){
            scrollBy(0,dy);
            consumed[1]=dy;
        }
        Log.i(TAG, "scrollY="+getScrollY());
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return true;
    }*/

    /**
     *
     * @param “手势拦截”
     * @return “三种情况时会return true 拦截”
     * “1、双指缩放”
     * “2、下拉添加”
     * “3、嵌套滑动”
     */
   @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()&ev.getActionMasked()) {
            case MotionEvent.ACTION_POINTER_DOWN:
                //用于双指缩放监听
                pointnumber=ev.getPointerCount();
                if(pointnumber==2){//双指触屏时
                    mDownX0=(int)ev.getX(0);
                    mDownX1=(int)ev.getX(1);
                    mDownY0=(int)ev.getY(0);
                    mDownY1=(int)ev.getY(1);
                    allowchange=true;//标记
                }
                break;
            case MotionEvent.ACTION_DOWN://触屏时触发
                //获取初次触屏位置
                mDownX = (int) ev.getX();
                mDownY = (int) ev.getY();
                Log.d("tag","MDOWNY="+mDownY);
                //嵌套向上滑动时，不拦截，将事件交由recyclerview拦截处理
               if(cancalintercept){
                    Log.d("TAG","MOVEDOWN");
                    mlistrecyclerview.setAllowscroll(true);
                    cancalintercept=false;
                    return false;
                }

                //添加状态时，点击空白区域完成添加
                if (iseditShow && mDownY > mEditHeight) {
                    meditOnActionListener.OnTouchFinished(flags);
                    return true;
                }

                Log.d(TAG, " " + mDownX + " " + mDownY);
                break;

            case MotionEvent.ACTION_MOVE:
                //向下嵌套滑动时，直接拦截move事件
                if(mlistrecyclerview.getCancalintercept()){
                    Log.d("TAG","MOVE MOVE");
                    State=mlistrecyclerview.getSTATE();
                    processmove=true;//标记拦截了move事件
                    mlistrecyclerview.setCancalintercept(false);//重置参数
                    return true;
                }

                //如果未识别到双指触屏
                if(!allowchange) {
                    //则暂定允许拦截
                    allowintercept = true;

                    //如果recyclerview不在顶端
                    if (mlistrecyclerview.canScrollVertically(1)&&mlistrecyclerview.canScrollVertically(-1)) {
                        //则不需要拦截
                        allowintercept = false;
                        Log.d("TAG", "=================" + ((LinearLayoutManager) mlistrecyclerview.getLayoutManager()).findFirstVisibleItemPosition());
                    }

                    //需要拦截时
                    if (allowintercept) {
                        //获取当前触摸点坐标
                        int nowX = (int) ev.getX();
                        int nowY = (int) ev.getY();

                        if(mDownY==0){
                            mDownY=nowY;
                        }
                        if(mDownX==0){
                            mDownX=nowX;
                        }

                        //用diffX，diffY来实时获取move移动偏移量
                        int diffY = nowY - mDownY;
                        int diffX = nowX - mDownX;
                        mDownX=nowX;
                        mDownY=nowY;
                        //

                        Log.i(TAG, " " + diffY+" mDownY="+mDownY);
                        Log.i(TAG, "canslideup="+mlistrecyclerview.canScrollVertically(1));
                        //如果标准状态向上滑且recyclerview已到达底部，则需要拦截
                        if (Math.abs(diffY) > Math.abs(diffX) && diffY <-2&&!mlistrecyclerview.canScrollVertically(1)) {
                            processmove=true;//标记拦截成功
                            Log.i(TAG, "time to setting");
                            State=Upstate;
                            return true;
                        }
                        //如果Y方向偏移量大于X方向，且Y偏移量向下超过两个单位，则拦截，进入performmove进行事件处理
                        else if (Math.abs(diffY) > Math.abs(diffX) && diffY > 2&&!mlistrecyclerview.canScrollVertically(-1)) {
                            processmove=true;//标记拦截成功
                            State=Downstate;
                            Log.i(TAG, "time to add");
                            return true;//避免子布局中有点击的控件时滑动无效
                        }
                    }
                }

//                //否则为双指触屏时的处理
//                else{
//                    //分别获取双指触摸点坐标，计算差值
//                    mNowX0=(int)ev.getX(0);
//                    mNowX1=(int)ev.getX(1);
//                    mNowY0=(int)ev.getY(0);
//                    mNowY1=(int)ev.getY(1);
//                    int diffX0=Math.abs(mDownX0-mDownX1);
//                    int diffX1=Math.abs(mNowX0-mNowX1);
//                    int diffY0=Math.abs(mDownY0-mDownY1);
//                    int diffY1=Math.abs(mNowY0-mNowY1);
//
//                    //若在X方向或Y方向向内滑动超过5个单位，则拦截，准备activity跳转
//                    if((diffX0-diffX1>5)||(diffY0-diffY1>5)){
//                        //向内移动一段距离，触发向主列表跳转
//                        meditOnActionListener.OnactivityChanged();//回调接口
//                        return true;
//                    }
//                }

                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    /**
     *
     * @param “手势处理”
     * @return “主要用来控制下拉move时edittext的位置变化”
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {//事件响应
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                performActionDown(ev);
                break;
            case MotionEvent.ACTION_MOVE:
                performActionMove(ev);
                break;
            case MotionEvent.ACTION_UP:
                performActionUp(ev);
                return true;
             //   break;
        }
        return super.onTouchEvent(ev);
    }

    private void performActionDown(MotionEvent ev) {
        if(!allowchange){
        mDownX = (int) ev.getX();
        mDownY = (int) ev.getY();}
    }

    private boolean performActionMove(MotionEvent ev) {
        if(!allowchange) {
            //采用Y方向偏移量，改变edittext位置
            int nowY = (int) ev.getY();
            if(lastY==0){
                lastY=nowY;
            }
            int dy=nowY-lastY;
            lastY=nowY;
            Log.d("TAG","dy=="+dy);
if(State.equals(Downstate)) {
    //（在move事件被mLinearLayout拦截时）如果需要向上嵌套滑动
    // 则重新分发down事件，使得recyclerview捕获上滑move
    if (processmove && dy < 0 && (editLayoutParams.topMargin == -mEditHeight)) {
        Log.d("TAG", "SUCCESS");
        ev.setAction(MotionEvent.ACTION_DOWN);
        lastY = 0;
        cancalintercept = true;
        processmove = false;
        dispatchTouchEvent(ev);
    }

    if (!iseditShow) {
        //根据偏移量dy不断改变edittext位置
        //借助其layoutparams实现
        editLayoutParams.topMargin = editLayoutParams.topMargin + dy;

        //最小，最大位置判断处理
        if (editLayoutParams.topMargin > 0) {
            editLayoutParams.topMargin = 0;
        } else if (editLayoutParams.topMargin < -mEditHeight) {
            editLayoutParams.topMargin = -mEditHeight;
        }
        Log.i(TAG, String.valueOf(editLayoutParams.topMargin));

        //设置edittext位置
        editll.setLayoutParams(editLayoutParams);
        Log.i(TAG, "time to show");
    }
}
else if(State.equals(Upstate)){
    if (processmove && dy >0 && (editLayoutParams.topMargin == -mEditHeight)) {
        Log.d("TAG", "SUCCESS");
        ev.setAction(MotionEvent.ACTION_DOWN);
        lastY = 0;
        cancalintercept = true;
        processmove = false;
        dispatchTouchEvent(ev);
    }
    //根据偏移量dy不断改变edittext位置
    //借助其layoutparams实现
    if(dy<0)
    editLayoutParams.topMargin = editLayoutParams.topMargin + (1-(-mEditHeight-editLayoutParams.topMargin)/msetlayoutHeight)*dy;
    else
        editLayoutParams.topMargin = editLayoutParams.topMargin + dy;
    //最小，最大位置判断处理
    if (editLayoutParams.topMargin > -mEditHeight) {
        editLayoutParams.topMargin = -mEditHeight;
    } else if (editLayoutParams.topMargin < -mEditHeight-msetlayoutHeight) {
        editLayoutParams.topMargin = -mEditHeight-msetlayoutHeight;
    }
    Log.i(TAG, "topmargin"+editLayoutParams.topMargin);

    //设置edittext位置
    editll.setLayoutParams(editLayoutParams);
}
        }
        //return whatever
        return true;
    }

    //手势完成时，根据edittext的位置决定是否进入添加状态，或是恢复标准状态
    private void performActionUp(MotionEvent ev) {
        if(!(State==null)) {
            if (State.equals(Downstate)) {
                //规定界限为1/3 edittext高度
                if (editLayoutParams.topMargin >= (-mEditHeight / 3)) {
                    //开始edittext显示动画，目的是使画面看起来更连贯
                    upshowedit(editLayoutParams.topMargin);

                    //作edittext默认文字及字体改变（从“下拉添加...”变为“Do...”）
                    editText.setHintTextColor(Color.parseColor("#808080"));
                    editText.setHint("Do...");
                    //显示光标
                    editText.setCursorVisible(true);
                    //标记已显示
                    iseditShow = true;
                    //标记正在添加
                    if (!flags.equals("add")) {
                        flags = "add";
                    }
                    //弹出软键盘
                    ((InputMethodManager) cont.getSystemService(INPUT_METHOD_SERVICE)).showSoftInput(editText, 0);

                    //回调方法，由于需要根据软键盘高度计算timebutton的位置，延迟显示timebutton
                    meditOnActionListener.Onedittextshow();
                    //显示阴影蒙层
                    Listturnshadow();
                    // shadowview.setVisibility(VISIBLE);
                    Log.i(TAG, "time to fill");
                } else {//否则恢复原状
                    upturnNormal(editLayoutParams.topMargin);
                    Log.i(TAG, "time to hide===");
                }
            } else if (State.equals(Upstate)) {
                if (editLayoutParams.topMargin <= (-mEditHeight -msetlayoutHeight*4/5)) {
                    editLayoutParams.topMargin = -mEditHeight;
                    editll.setLayoutParams(editLayoutParams);
                    meditOnActionListener.Onsettingshow();
                    Log.d("TAG", "settingshow");
                } else {
                    downturnNormal(editLayoutParams.topMargin);
                    Log.d("TAG", "settingfalse");
                }
            }

            //初始化用到的参数
            State = null;
            mDownY = 0;
            lastY = 0;
            processmove = false;
            allowchange = false;
        }
    }

    /**
     *
     * @param “将dp转化为px”
     * @return “px”
     */
    private float dp2px(int dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getContext().getResources().getDisplayMetrics());
    }

    /**
     *
     * @param “edittext向上收回的动画”
     */
    private void upturnNormal(final int currenttopmarin){
        upanim.removeAllUpdateListeners();//先取消之前的监听
        upanim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //向上逐渐消失
                float fraction=animation.getAnimatedFraction();
                editLayoutParams.topMargin=(int)(currenttopmarin+(-mEditHeight-currenttopmarin)*fraction);
                editll.setLayoutParams(editLayoutParams);
            }
        });
        upanim.start();
        editdelayset();//延迟等待动画完成再设置
        iseditShow = false;
        textbtn.setVisibility(View.INVISIBLE);
        placebtn.setVisibility(View.INVISIBLE);

    }
    private void downturnNormal(final  int currenttopmargin){
        upanim.removeAllUpdateListeners();//先取消之前的监听
        upanim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //向上逐渐消失
                float fraction=animation.getAnimatedFraction();
                editLayoutParams.topMargin=(int)(currenttopmargin+(-mEditHeight-currenttopmargin)*fraction);
                editll.setLayoutParams(editLayoutParams);
            }
        });
        upanim.start();
    }
    public void editTurnChange(){
        if(!flags.equals("change")){
      flags="change";}
      meditOnActionListener.Onedittextshow();
      editText.setHint("Do...");
      editText.setCursorVisible(true);
      editshowanim.start();
      Listturnshadow();
      iseditShow=true;
      ((InputMethodManager) cont.getSystemService(INPUT_METHOD_SERVICE)).showSoftInput(editText, 0);
    }

    /**
     *
     * @param “edittext向下弹出的动画”
     */
    private void upshowedit(final int currenttopmarin){
        upanim.removeAllUpdateListeners();//先取消之前的监听
        upanim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //向下逐渐显示
                float fraction=animation.getAnimatedFraction();
                editLayoutParams.topMargin=(int)(currenttopmarin+(-currenttopmarin)*fraction);
                editll.setLayoutParams(editLayoutParams);
            }
        });
        upanim.start();
    }

    /**
     * 从添加状态返回原状态时调用
     * 包括edittext的收回动画，view的可视属性及参数初始化
     */
    public void turnNormal() {
        editmissanim.start();
     //   editLayoutParams.topMargin = -mEditHeight;
      //  editll.setLayoutParams(editLayoutParams);
        Log.i(TAG, "" + mEditHeight);
        iseditShow = false;
        Listturnnormal();
       // shadowview.setVisibility(INVISIBLE);
        textbtn.setVisibility(View.INVISIBLE);
        placebtn.setVisibility(View.INVISIBLE);
        Log.i(TAG, "turn normal");
    }

    /**
     *从添加状态返回原状态时调用
     * 参数初始化
     */
    public void finishedit() {
        turnNormal();
        editdelayset();
        im = (InputMethodManager) cont.getSystemService(INPUT_METHOD_SERVICE);
        editText.setHintTextColor(Color.parseColor("#ffffff"));
        editText.setCursorVisible(false);
        editText.setText(null);
        im.hideSoftInputFromWindow(appcomp.getCurrentFocus().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        mlistrecyclerview.scrollToPosition(0);
        flags="normal";
    }

    public boolean isIseditShow() {
        return iseditShow;
    }

    /**
     * 等待动画完成后恢复默认hint
     */
    private void editdelayset(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                editText.setHintTextColor(Color.parseColor("#000000"));
                editText.setHint("下拉添加");
            }
        },500);
    }

    /**
     * 获取edittext’s height
     */
    private void measureEditHeight(){
        int width =View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        int height =View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        editll.measure(width,height);
        mEditHeight=editll.getMeasuredHeight()+16;
        Log.d("TAG","HEIGHT="+mEditHeight);
    }

    /**
     * 切换listrecyclerview背景
     */
    private void Listturnshadow(){
        mlistrecyclerview.setAlpha(0);
        mlistrecyclerview.setBackgroundColor(Color.parseColor("#000000"));
    }
    private void Listturnnormal(){
        mlistrecyclerview.setAlpha(1);
        mlistrecyclerview.setBackground(null);
    }

    /**
     * 布局边框绘制
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getDisplayMetrics(cont)[0];//宽度等于屏幕宽
        int height = getDisplayMetrics(cont)[1];//高度等于屏幕高
            Paint paint=new Paint();
            paint.setColor(Color.parseColor("#000000"));
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2);
            paint.setShadowLayer(2,-2,0,Color.parseColor("#808080"));
            canvas.drawRect(0,0,width,height,paint);
    }
    public static final Integer[] getDisplayMetrics(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getApplicationContext().getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        return new Integer[]{screenWidth, screenHeight};
    }

}



