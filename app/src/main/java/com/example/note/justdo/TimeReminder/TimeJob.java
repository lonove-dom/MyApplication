package com.example.note.justdo.TimeReminder;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;
import com.evernote.android.job.util.support.PersistableBundleCompat;
import com.example.note.justdo.MainActivity;
import com.example.note.justdo.R;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Choz on 2018/8/21.
 * 后台任务类
 */

public class TimeJob extends Job {

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        String content=params.getExtras().getString("content","null");
        int type=params.getExtras().getInt("type",0);
        long id=params.getExtras().getLong("id",0);
        notify(id,content);
        cancalJobByIdTAG(Long.toString(id));
        if(type>0) {
            long exactmills = params.getExtras().getLong("startmills", 0);
            int interval = params.getExtras().getInt("interval", 0);
            setExactTimeJob(id,content,exactmills,interval,type);
        }
        Log.d("TAG","Job finished");
        return Result.SUCCESS;
    }

    /**
     * 设置下一时间job，用于循环的时间提醒
     * @param id
     * @param content
     * @param exacttimemills
     * @param interval
     * @param type
     */
    public void setExactTimeJob(long id,String content,long exacttimemills,int interval,int type){
        long nextexacttimemills=getNextExactTimeMills(exacttimemills,interval,type);
        long currentmills=System.currentTimeMillis();
        PersistableBundleCompat extras = new PersistableBundleCompat();
        extras.putLong("id",id);
        extras.putLong("startmills",nextexacttimemills);
        extras.putInt("interval",interval);
        extras.putInt("type",type);
        extras.putString("content",content);
        new JobRequest.Builder(Long.toString(id))
                .setExecutionWindow(nextexacttimemills-currentmills-30000,nextexacttimemills-currentmills+30000)
                .setExtras(extras)
                .setUpdateCurrent(false)
                .build()
                .schedule();
        new JobRequest.Builder(Long.toString(id))
                .setExact(nextexacttimemills-currentmills)
                .setExtras(extras)
                .setUpdateCurrent(false)
                .build()
                .schedule();
    }
    private void cancalJobByIdTAG(String IDTAG){
        JobManager.instance().cancelAllForTag(IDTAG);
    }

    /**
     * 计算下一job应执行的精确时间
     * @param exacttimemills
     * @param interval
     * @param type
     * @return
     */
    private long getNextExactTimeMills(long exacttimemills,int interval,int type) {
        Calendar currentcalendar = Calendar.getInstance();
        Calendar cusorCal;
        currentcalendar.setTime(new Date(exacttimemills));
        int hour=currentcalendar.get(Calendar.HOUR_OF_DAY);
        int minute=currentcalendar.get(Calendar.MINUTE);
        int second=currentcalendar.get(Calendar.SECOND);
        long startmills=exacttimemills;
        int startYear = currentcalendar.get(Calendar.YEAR);
        int startMonth = currentcalendar.get(Calendar.MONTH);
        int startDay = currentcalendar.get(Calendar.DATE);
        switch (type) {
            case 1://everyday
                startmills = startmills + 1000 * 24 * 60 * 60;
                break;
            case 2://everyweek
                startmills = startmills + 1000 * 7 * 24 * 60 * 60;
                break;

            case 3://everymonth
                startMonth = startMonth + 1;
                if (startMonth > 11) {
                    startYear = startYear + startMonth / 12;
                    startMonth = startMonth % 12;
                }
                cusorCal = new GregorianCalendar(startYear, startMonth, startDay, hour, minute, second);
                startmills = cusorCal.getTimeInMillis();
                break;
            case 4://workday
                int weekday = currentcalendar.get(Calendar.DAY_OF_WEEK);
                if (weekday < 6) {
                    startmills = startmills + 1000 * 24 * 60 * 60;
                } else if (weekday == 6) {
                    startmills = startmills + 1000 * 3 * 24 * 60 * 60;
                }
                break;
            case 5://customday
                startmills = startmills + 1000 * interval * 24 * 60 * 60;
                break;
            case 6://customweek
                startmills = startmills + 1000 * interval * 7 * 24 * 60 * 60;
                break;

            case 7://custommonth
                startMonth = startMonth + interval;
                if (startMonth > 11) {
                    startYear = startYear + startMonth / 12;
                    startMonth = startMonth % 12;
                }
                cusorCal = new GregorianCalendar(startYear, startMonth, startDay, hour, minute, second);
                startmills = cusorCal.getTimeInMillis();
                break;

            case 8://customyear
                startYear = startYear + interval;
                cusorCal = new GregorianCalendar(startYear, startMonth, startDay, hour, minute, second);
                startmills = cusorCal.getTimeInMillis();
                break;
        }
        return startmills;
    }
    private void notify(long id,String content){
        NotificationManager notificationManager =(NotificationManager)getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        String Channelid=null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Channelid ="TimeRemindid";
            String Channelname="Just Do";
            NotificationChannel channel = new NotificationChannel(Channelid, Channelname, NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(false); //是否在桌面icon右上角展示小红点
            channel.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知
            channel.enableVibration(false);
            channel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PRIVATE);
            // 设置绕过免打扰模式
            channel.setBypassDnd(true);
            channel.setSound(null, null);
            notificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), Channelid)
                .setSmallIcon(R.drawable.icon2)
                .setContentTitle("just do 0.8")
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentText(content)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setTicker("title")
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(PendingIntent.getActivity(getContext(), 0, new Intent(getContext(), MainActivity.class), 0))
        ;
        notificationManager.notify((int)id,builder.build());
    }
}
