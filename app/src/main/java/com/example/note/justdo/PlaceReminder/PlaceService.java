package com.example.note.justdo.PlaceReminder;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class PlaceService extends Service {
    public static  final String TAG= "PlaceService";

    @Override
    //创建服务时
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
    }

    @Override
    // 服务执行的操作
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        new Thread(new Runnable() {
       public void run() {
                              //处理具体的逻辑
                           stopSelf();  //服务执行完毕后自动停止
                        }
         }).start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    //销毁服务时
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    //创建服务时调用

}
