package com.example.note.justdo.Widget;

import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

public class WidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsService.RemoteViewsFactory onGetViewFactory(Intent intent) {
            return new WidgetFactory(this,intent);
        }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("TAG","onCreat");
    }

    @Override
    public void onDestroy() {
        Log.d("TAG", "onDestroy");
        super.onDestroy();
    }
}

