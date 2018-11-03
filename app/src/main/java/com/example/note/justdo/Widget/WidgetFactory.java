package com.example.note.justdo.Widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.note.justdo.Event;
import com.example.note.justdo.Eventdaomanger;
import com.example.note.justdo.R;

import java.util.ArrayList;
import java.util.List;

public class WidgetFactory implements RemoteViewsService.RemoteViewsFactory {
    private final static String TAG = "Widget";
    private Context mContext;
    private int mAppWidgetId;

     static List<Event> mEvents=new ArrayList<Event>();
    /**
     * 构造GridRemoteViewsFactory
     */
    public WidgetFactory(Context context, Intent intent) {
        mContext = context;
        initData();
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public RemoteViews getViewAt(int position) {
        //  HashMap<String, Object> map;

        // 获取 item_widget_device.xml 对应的RemoteViews
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.item);

        // 设置 第position位的“视图”的数据
       Event event = mEvents.get(position);
        //  rv.setImageViewResource(R.id.iv_lock, ((Integer) map.get(IMAGE_ITEM)).intValue());
        rv.setTextViewText(R.id.textView, event.getContext());
//        // 设置 第position位的“视图”对应的响应事件
//        Intent fillInIntent = new Intent();
//        fillInIntent.putExtra("Type", 0);
//        fillInIntent.putExtra(ListWidgetProvider.COLLECTION_VIEW_EXTRA, position);
//        rv.setOnClickFillInIntent(R.id.rl_widget_device, fillInIntent);
//
//
//        Intent lockIntent = new Intent();
//        lockIntent.putExtra(ListWidgetProvider.COLLECTION_VIEW_EXTRA, position);
//        lockIntent.putExtra("Type", 1);
//        rv.setOnClickFillInIntent(R.id.iv_lock, lockIntent);
//
//        Intent unlockIntent = new Intent();
//        unlockIntent.putExtra("Type", 2);
//        unlockIntent.putExtra(ListWidgetProvider.COLLECTION_VIEW_EXTRA, position);
//        rv.setOnClickFillInIntent(R.id.iv_unlock, unlockIntent);
        Intent fillInIntent = new Intent();
       // fillInIntent.putExtra("Type", 0);
        fillInIntent.putExtra(WidgetProvider.LISTVIEW_POSITION, position);
        rv.setOnClickFillInIntent(R.id.textView, fillInIntent);
        return rv;
    }


    /**
     * 初始化ListView的数据
     */
    private void initData() {
        int i;
     List<Event> original=new Eventdaomanger(mContext).getfinalEventlist(1);
        for(i=0;i<original.size();i++) {
            if(!original.get(i).getIsLinearShow()){
                mEvents.add(original.get(i));
            }
        }
    //    mEvents=new Eventdaomanger(mContext).getfinalEventlist(1);
      //  Log.d("TAG","第0个数据"+mEvents.get(0).getContext());
        }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
        // 初始化“集合视图”中的数据
    }

    @Override
    public int getCount() {
        // 返回“集合视图”中的数据的总数
        return mEvents.size();
    }

    @Override
    public long getItemId(int position) {
        // 返回当前项在“集合视图”中的位置
        return position;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        // 只有一类 ListView
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onDataSetChanged() {
        mEvents.clear();
        int i;
        List<Event> original=new Eventdaomanger(mContext).getfinalEventlist(1);
        for(i=0;i<original.size();i++) {
            if(!original.get(i).getIsLinearShow()){
                mEvents.add(original.get(i));
            }
        }
    }

    @Override
    public void onDestroy() {
        mEvents.clear();
    }
}
