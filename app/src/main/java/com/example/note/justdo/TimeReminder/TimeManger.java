package com.example.note.justdo.TimeReminder;

import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;
import com.evernote.android.job.util.support.PersistableBundleCompat;
import com.example.note.justdo.App;
import com.example.note.justdo.Event;
import com.example.note.justdo.Eventdaomanger;

import java.util.Date;
import java.util.List;

/**
 * Created by Choz on 2018/8/22.
 * 时间提醒管理类
 * 可直接通过该类方法设置时间提醒
 */

public class TimeManger {
    public TimeManger(){

    }

    /**
     * 添加job
     * @param id
     * @param content
     * @param startmills
     * @param interval
     * @param type
     */
    public static void addTimeJob(long id, String content, long startmills, int interval, int type){
        long currentmills=System.currentTimeMillis();
        PersistableBundleCompat extras = new PersistableBundleCompat();
        extras.putLong("id",id);
        extras.putLong("startmills",startmills);
        extras.putInt("interval",interval);
        extras.putInt("type",type);
        extras.putString("content",content);
        new JobRequest.Builder(Long.toString(id))
                .setExecutionWindow(startmills-currentmills-30000,startmills-currentmills+30000)
                .setExtras(extras)
                .setUpdateCurrent(false)
                .build()
                .schedule();
        new JobRequest.Builder(Long.toString(id))
                .setExact(startmills-currentmills)
                .setExtras(extras)
                .setUpdateCurrent(false)
                .build()
                .schedule();
    }

    /**
     * 根据idTAG，取消job
     * @param IDTAG
     */
    public void cancalJobByIdTAG(String IDTAG){
        JobManager.instance().cancelAllForTag(IDTAG);
    }

    /**
     * 更改job，先cancal，后add
     * @param id
     * @param content
     * @param startmills
     * @param interval
     * @param type
     */
    public void changeTimeJob(long id,String content,long startmills,int interval,int type){
        cancalJobByIdTAG(Long.toString(id));
        addTimeJob(id,content,startmills,interval,type);
    }

    private App getApp(){
        return App.getInstance();
    }

    private Eventdaomanger getEventdaomanger(){
        return getApp().getEventdaomanger();
    }

    /**
     * 刷新对应listid列表项所有job
     * @param listid
     */
    public void refreshTimeJobInOneList(int listid){
        List<Event> events=getEventdaomanger().getfinalEventlist(listid);
        long currentmills=new Date().getTime();
        for(Event event:events){
            cancalJobByIdTAG(Long.toString(event.getId()));
            if(!event.getIsLinearShow()&&event.getStartmills()>currentmills){
                addTimeJob(event.getId(),event.getContext(),event.getStartmills(),event.getIntervel(),event.getType());
            }
        }
    }
}
