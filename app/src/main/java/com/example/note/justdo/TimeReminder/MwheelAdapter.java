package com.example.note.justdo.TimeReminder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.contrarywind.listener.OnItemSelectedListener;
import com.contrarywind.view.WheelView;
import com.example.note.justdo.R;

import java.util.List;

/**
 * Created by Choz on 2018/5/15.
 * 时分秒滚轮选择器 recyclerview对应adapter
 * 提供滚轮滚动时选择监听接口，于MyTimeWindow中实现，以确定选择的时分秒
 */


public class MwheelAdapter extends RecyclerView.Adapter<MwheelAdapter.wheelViewHolder> {
    List<WheelView> wheelViews;
    Context mcontext;
    LayoutInflater layoutInflater;
    MyTimeSelectToolListener myTimeSelectToolListener;
    List<Integer> positionList;


    public MwheelAdapter(List<WheelView> wheelViews, Context mcontext, LayoutInflater layoutInflater,List<Integer> positionList) {
        this.wheelViews = wheelViews;
        this.mcontext = mcontext;
        this.layoutInflater = layoutInflater;
        this.positionList=positionList;//用于初始化滚轮位置
    }
    public void setMyTimeSelectToolListener(MyTimeSelectToolListener myTimeSelectToolListener){
        this.myTimeSelectToolListener=myTimeSelectToolListener;
    }

    public void setPositionList(List<Integer> positionList) {
        this.positionList = positionList;
        notifyDataSetChanged();
    }

    @Override
    public MwheelAdapter.wheelViewHolder  onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=layoutInflater.inflate(R.layout.wheelviews,parent,false);
        wheelViewHolder wheelViewHolder=new wheelViewHolder(view);
        return wheelViewHolder;
    }

    @Override
    public void onBindViewHolder(MwheelAdapter.wheelViewHolder holder, final int position) {
        holder.wheelView.setAdapter(wheelViews.get(position).getAdapter());
        holder.wheelView.setCyclic(false);
        if(positionList==null){holder.wheelView.setInitPosition(0);}
        else{
        holder.wheelView.setInitPosition(positionList.get(position));}
        holder.wheelView.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                myTimeSelectToolListener.OnTimeSelected(index,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return wheelViews.size();
    }
    class wheelViewHolder extends RecyclerView.ViewHolder {
        WheelView wheelView;
        public wheelViewHolder(View itemView) {
            super(itemView);
            wheelView=itemView.findViewById(R.id.wheelviews);
        }
    }
    public interface MyTimeSelectToolListener{
         void OnTimeSelected(int index,int whichwheel);
    }
}
