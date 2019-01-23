package com.example.note.justdo.PlaceReminder;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PlaceService extends Service implements AMapLocationListener {
    public static  final String TAG= "PlaceService";
    //声明mlocationClient对象
    AMapLocationClient mlocationClient=null;
    //声明mLocationOption对象
    AMapLocationClientOption mLocationOption = null;
    //public AMapLocationListener mLocationListener = new AMapLocationListener();
    //AMapLocation amapLocation=null;
    PlaceRemind placeRemind=null;
    List<PlaceRemind> data=new ArrayList<PlaceRemind>();

    @Override
    //创建服务时
    public void onCreate() {

        mlocationClient = new AMapLocationClient(getApplicationContext());
//初始化定位参数
        mLocationOption = new AMapLocationClientOption();
//设置定位监听
        mlocationClient.setLocationListener(this);
//设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
//设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(5000);
//设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
// 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
// 注意设置合适的定位时间的间隔（最小间隔支持为1000ms），并且在合适时间调用stopLocation()方法来取消定位请求
// 在定位结束后，在合适的生命周期调用onDestroy()方法
// 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
//启动定位
        mlocationClient.startLocation();
       // AMapLocation amapLocation=mlocationClient.getLastKnownLocation();
        super.onCreate();
        Log.d(TAG, "onCreate");
    }

    @Override
    // 服务执行的操作
    public int onStartCommand(Intent intent, int flags, int startId) {
        double latitude=intent.getDoubleExtra("latitude",0);
        double longtitude=intent.getDoubleExtra("longtitude",0);
        double radius=intent.getDoubleExtra("radius",0);
        String content=intent.getStringExtra("content");
            placeRemind = new PlaceRemind(latitude, longtitude, radius, content);
            data.add(placeRemind);
     //   PlaceRemind placeRemind=new PlaceRemind(intent.getDoubleExtra("latitude",0),intent.getDoubleExtra("longtitude",0),intent.getDoubleExtra("radius",0),intent.getStringExtra("content"));
        Log.d(TAG, "onStartCommand");
        return START_REDELIVER_INTENT;
//重传Intent。使用这个返回值时，如果在执行完onStartCommand后，服务被异常kill掉，系统会自动重启该服务，并将Intent的值传入。
       // return super.onStartCommand(intent, flags, startId);
    }

    @Override
    //销毁服务时
    public void onDestroy() {
        mlocationClient.onDestroy();//销毁定位客户端，同时销毁本地定位服务。
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                double latitude=amapLocation.getLatitude();//获取纬度
                amapLocation.getLongitude();//获取经度
                amapLocation.getAccuracy();//获取精度信息
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(amapLocation.getTime());
                df.format(date);//定位时间
                if(data.size()==0){

                }
                else{
                    for(int i=0;i<=data.size();i++){
                        data.get(i).isfinish(amapLocation.getLatitude(),amapLocation.getLongitude());
                        if(!data.get(i).isRight){
                            data.remove(i);
                            Intent intent=new Intent(this,PlaceDialog.class);
                            startActivity(intent);
                        }
                    }
                }
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError","location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
            }
        }
    }
    //创建服务时调用1


}
