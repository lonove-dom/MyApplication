package com.example.note.justdo.Widget;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.note.justdo.Event;
import com.example.note.justdo.Eventdaomanger;
import com.example.note.justdo.R;

public class WidgetProvider extends AppWidgetProvider {
    public static final String CLICK_ACTION = "widget.listview.action.CLICK"; // listview点击事件的广播ACTION
    public static final String JUST_DO="widget.icon.action.CLICK";//点击icon或者标题
    public static final String LISTVIEW_POSITION= "widget.listview.CLECCTION";
    public static final String CLICK_ADD="widget.button.ADD";
    InputMethodManager im;//软键盘

    public void updateAppWidget(Context context,AppWidgetManager appWidgetManager,int appWidgetId){
        // 获取Widget的组件名
        ComponentName thisWidget = new ComponentName(context,
                WidgetProvider.class);
        // 创建一个RemoteView
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
        // 把这个Widget绑定到RemoteViewsService
        Intent intent = new Intent(context, WidgetService.class);
        // When intents are compared, the extras are ignored, so we need to embed the extras
        // into the data so that the extras will not be ignored.
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        //设置适配器
        remoteViews.setRemoteAdapter(R.id.wg_listview, intent);
        remoteViews.setEmptyView(R.id.wg_listview,R.id.wg_empty);

        //点击图标及标题
        Intent mainIntent = new Intent().setAction(JUST_DO);
        PendingIntent mainPendingIntent = PendingIntent.getBroadcast(context, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.wg_icon,mainPendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.wg_title,mainPendingIntent);



// 设置点击列表触发事件
        Intent clickIntent = new Intent(context, WidgetProvider.class);
        // 设置响应 “ListView” 的intent模板
        // 说明：“集合控件(如GridView、ListView、StackView等)”中包含很多子元素，如GridView包含很多格子。
        //     它们不能像普通的按钮一样通过 setOnClickPendingIntent 设置点击事件，必须先通过两步。
        //        (01) 通过 setPendingIntentTemplate 设置 “intent模板”，这是比不可少的！
        //        (02) 然后在处理该“集合控件”的RemoteViewsFactory类的getViewAt()接口中 通过 setOnClickFillInIntent 设置“集合控件的某一项的数据”
        // 设置Action，方便在onReceive中区别点击事件
        clickIntent.setAction(CLICK_ACTION);
        clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        clickIntent.setData(Uri.parse(clickIntent.toUri(Intent.URI_INTENT_SCHEME)));
        PendingIntent pendingIntentTemplate = PendingIntent.getBroadcast(
                context, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //使用"集合视图",如果直接setOnClickPendingIntent是不可行的,
        //建议setPendingIntentTemplate和FillInIntent结合使用
        //FillInIntent用于区分单个点击事件
        remoteViews.setPendingIntentTemplate(R.id.wg_listview,
                pendingIntentTemplate);

        // 刷新按钮
        final Intent addIntent = new Intent(context,
                WidgetProvider.class);
        addIntent.setAction(CLICK_ADD);
        final PendingIntent refreshPendingIntent = PendingIntent.getBroadcast(
                context, 0, addIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.wg_button,
                refreshPendingIntent);


        // 更新Widget

        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
        super.onUpdate(context,appWidgetManager,appWidgetIds);

    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        Toast.makeText(context, "已添加JUST DO的桌面小组件",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Toast.makeText(context, "已移除JUST DO的桌面小组件",
                Toast.LENGTH_SHORT).show();
        super.onDeleted(context, appWidgetIds);
    }

    /**
     * 接受Intent
     *
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Intent intent2=new Intent(context,WidgetService.class);
        context.startService(intent2);
        String action = intent.getAction();
//        if (action.equals("refresh")) {
//            int i = 0;
//            // 刷新Widget
//            final AppWidgetManager mgr = AppWidgetManager.getInstance(context);
//            final ComponentName cn = new ComponentName(context,
//                    WidgetProvider.class);
//
//            MyRemoteViewsFactory.mList.add("音乐" + i);
//            i=i+1;
//            // 这句话会调用RemoteViewSerivce中RemoteViewsFactory的onDataSetChanged()方法。
//            mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn),
//                    R.id.wg_listview);
//
//        } else
        if (action.equals(CLICK_ACTION)) {
            //取消时间提醒！
            int poi=intent.getIntExtra(LISTVIEW_POSITION,0);
            Log.d("TAG","wg_list被点击了"+poi);
            Eventdaomanger manger=new Eventdaomanger(context);
          int finalposition =WidgetFactory.mEvents.size()-1;
            Log.d("TAG","finalposition"+finalposition);
          if(!WidgetFactory.mEvents.get(poi).getIsLinearShow()){
         manger.updateSwapedevents(1,poi,finalposition,true);
        }
          else {
              manger.updateSwapedevents(1,poi,0,false);
          }
            final AppWidgetManager mgr = AppWidgetManager.getInstance(context);
            final ComponentName cn = new ComponentName(context,
                    WidgetProvider.class);
            mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn),
                    R.id.wg_listview);


        }
        if(action.equals(JUST_DO)){
            //String packge = this.get;//可以传递一些数据到主客户端
            Intent startAcIntent = new Intent();
            startAcIntent.setComponent(new ComponentName("com.example.note.justdo","com.example.note.justdo.MainActivity"));//第一个是包名，第二个是类所在位置的全称
            //startAcIntent.putExtra("flag",flag);
            //startAcIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(startAcIntent);
            Log.d("TAG","标题或图标被点击了");

        }
        if(action.equals(CLICK_ADD)){
            Log.d("TAG","添加事项");
//            showDialog(context);
            Intent startAcIntent = new Intent();
            startAcIntent.setComponent(new ComponentName("com.example.note.justdo","com.example.note.justdo.Widget.Widget_dialog"));
            context.startActivity(startAcIntent);

        }

    }
    private void showDialog (final Context context) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.widget_add_dialog, null);
       builder.setView(R.layout.widget_add_dialog);
        final EditText newEditText = (EditText) view.findViewById(R.id.wg_edittext);
        Button tick=(Button)view.findViewById(R.id.wg_tick);
        final Dialog dialog = builder.create();
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);//这里设置的可以在桌面中显示对话框
        dialog.show();
        tick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content=newEditText.getText().toString();
                addEvent(content,context);
                dialog.dismiss();
            }
        });
    }
private void addEvent(String string,Context context){
    Event event=new Event(1,string);
    Eventdaomanger manger=new Eventdaomanger(context);
    manger.insertevent(event);
}
}

