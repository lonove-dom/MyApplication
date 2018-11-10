package com.example.note.justdo.MainLayoutTools;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by Choz on 2018/8/13.
 */
//重写一个recyclerview（listview）以实现顶部嵌套滑动（内部拦截法）
    //之前无法嵌套滑动的原因在于：recyclerview拦截了手势的move事件，
    //因此通过在recyclerview onTouchevent的move事件中判断是否达到嵌套滑动的条件
    //满足条件则用dispatchevent重新分发Down手势事件（Down→Move）
    //从而让父布局mLinearlayout可以拦截move事件以实现下拉添加
    // （将move事件交给mLinearlayout处理）
    //连接了两个手势处理

public class mRecyclerview extends RecyclerView{
    int lastY;
    int lastX;
    Boolean cancalintercept=false;
    Boolean allowscroll=false;
    Boolean IsDrag=false;
    Boolean IsSwipe=false;
    Boolean IsRightSlip=false;

    public String getSTATE() {
        return STATE;
    }

    public void setSTATE(String STATE) {
        this.STATE = STATE;
    }

    String STATE;
    public mRecyclerview(Context context) {
        super(context);
    }

    public mRecyclerview(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public mRecyclerview(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setSwipe(Boolean swipe) {
        IsSwipe = swipe;
    }

    public Boolean getSwipe() {
        return IsSwipe;
    }

    public Boolean getRightSlip() {
        return IsRightSlip;
    }

    public void setRightSlip(Boolean rightSlip) {
        IsRightSlip = rightSlip;
    }

    public void setDrag(Boolean drag) {
        IsDrag = drag;
    }

    public Boolean getDrag() {
        return IsDrag;
    }

    public void setAllowscroll(Boolean allowscroll) {
        this.allowscroll = allowscroll;
    }

    public Boolean getCancalintercept() {
        return cancalintercept;
    }

    public void setCancalintercept(Boolean cancalintercept) {
        this.cancalintercept = cancalintercept;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(cancalintercept){
            //       cancalintercept=false;
            lastY=0;
            Log.d("TAG","intercept down");
            getParent().requestDisallowInterceptTouchEvent(false);
            return false;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        switch (e.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(allowscroll){
                    allowscroll=false;
                    return true;
                }
           /*     if(cancalintercept){
             //       cancalintercept=false;
                    lastY=0;
                    Log.d("TAG","intercept down");
                    getParent().requestDisallowInterceptTouchEvent(false);
                    return false;
                }*/
        }
        return super.onInterceptTouchEvent(e);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()){
            case MotionEvent.ACTION_MOVE:
                int nowY=(int)e.getY();
                int nowX=(int)e.getX();
                if(lastY==0){
                    lastY=nowY;
                }
                if(lastX==0){
                    lastX=nowX;
                }
                int dx=nowX-lastX;
                int dy=nowY-lastY;
                Log.d("TAG","DY=="+dy);
                Log.d("TAG"," "+ IsSwipe+canScrollVertically(-1)+IsDrag);
                lastX=nowX;
                lastY=nowY;
//                if(IsSwipe==false&&(Math.abs(dx)>Math.abs(dy)+100)){
//                    IsSwipe=true;
//                }
             if(!IsSwipe&&(!canScrollVertically(-1))&&dy>2&&(!IsDrag)){
                 e.setAction(MotionEvent.ACTION_DOWN);
                 setSTATE("PULLDOWN");
                 Log.d("TAG","DOWNDISPATCH");
                 cancalintercept=true;
                dispatchTouchEvent(e);
                 lastY=0;
                 return false;
             }
                if(!IsSwipe&&(!canScrollVertically(1))&&dy<-2&&(!IsDrag)){
                    e.setAction(MotionEvent.ACTION_DOWN);
                    setSTATE("PULLUP");
                    Log.d("TAG","UPDISPATCH");
                    cancalintercept=true;
                    dispatchTouchEvent(e);
                    lastY=0;
                    return false;
                }
        }
        return super.onTouchEvent(e);
    }
}

