package com.example.note.justdo;

import android.app.Application;
import android.graphics.Bitmap;

import com.amap.api.maps.model.LatLng;
import com.evernote.android.job.JobManager;
import com.example.note.justdo.TimeReminder.TimeJobCreator;

/**
 * Created by Choz on 2018/2/27.
 */

public class App extends Application {
    public static App instance;
    private Eventdaomanger eventdaomanger;
 //   private String TAG = App.class.getSimpleName();
    private Bitmap viewbackground;
    private Bitmap listsViewBackground;
    private Bitmap voidviewbackground;
    private final String symbolstr="SYMBOL STR";
    private LatLng latlng;//选定的地点
    private double radius;//选定地点提醒的半径
    private String place;//选择地点的名称

    public Bitmap getViewbackground() {
        return viewbackground;
    }

    public void setViewbackground(Bitmap viewbackground) {
        this.viewbackground = viewbackground;
    }

    /*private List<Eventlist> eventlists=new ArrayList<>();*/
    private int listid=1;
    private String listtitle="first";
   /*private Eventlist currentEv=null;*/
   /*private  Eventlist getfirstEvlist(){
        Eventlist eventlist=new Eventlist("first");
        eventlists.add(eventlist);
        return eventlist;
    }
    public List<Eventlist> getEventlists() {
        return eventlists;
    }
    public void initcurrentEv(){
        currentEv=(eventlists.isEmpty()?getfirstEvlist():eventlists.get(cusor));
    }*/

    public Bitmap getVoidviewbackground() {
        return voidviewbackground;
    }

    public void setVoidviewbackground(Bitmap voidviewbackground) {
        this.voidviewbackground = voidviewbackground;
    }

    public Bitmap getListsViewBackground() {
        return listsViewBackground;
    }

    public void setListsViewBackground(Bitmap listsViewBackground) {
        this.listsViewBackground = listsViewBackground;
    }

    public String getListtitle() {
        return listtitle;
    }

    public int getListid() {
        return listid;
    }

    public void setListid(int listid) {
        this.listid = listid;
    }

    public void setListtitle(String listtitle) {
        this.listtitle = listtitle;
    }

  /*  public Eventlist getCurrentEv() {
        return currentEv;
    }*/

   /* public void setCurrentEv(Eventlist currentEv) {
        this.currentEv = currentEv;
    }*/


    private void initmanger(){
        eventdaomanger=new Eventdaomanger(this);
    }

    public Eventdaomanger getEventdaomanger() {
        if (eventdaomanger==null){
            eventdaomanger=new Eventdaomanger(this);
        }
        return eventdaomanger;
    }
    private void initdata(){
       //eventdaomanger.deleSQL();
        Event cev = eventdaomanger.getDaoSession().getEventDao().queryBuilder().where(EventDao.Properties.Listid.eq(0)).unique();
        if(cev==null){
            cev=new Event(null,"charge",0,"chargeev",1) ;
            eventdaomanger.getDaoSession().getEventDao().insert(cev);
           Event preev = new Event(symbolstr,  1, "first");
           eventdaomanger.getDaoSession().getEventDao().insert(preev);
        }
    }
    @Override
    public void onCreate() {
        super.onCreate();
        //获取全局Context
        instance=this;
        JobManager.create(this).addJobCreator(new TimeJobCreator());
     /*   Geteventlists();*/
        initmanger();
        initdata();
        setRadius(0);
        //setRadius(0);
      /*  initcurrentEv();*/

    }

    public static App getInstance() {
        return instance;
    }
    //*接下来的方法用于eventlists的导入导出数据库操作
   /* public void Saveeventlists(){
        for(int i=0;i<eventlists.size();i++){
            Eventlist eventlist=eventlists.get(i);
            List<Event> list=eventlist.getList();
            for(Event event:list){
                event.setListtitle(eventlist.getTitle());
                event.setListid(i);
            }
            insertUserList(list);
        }
    }
    public void Geteventlists(){
        SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        Cursor c=sqLiteDatabase.rawQuery("SELECT * FROM event",null);
        c.moveToLast();
        int maxid=c.getColumnIndex("listid");
        for(int i=0;i<=maxid;i++)
        {List<Event> evlist=queryUserList(i);
          String evtitle=evlist.get(0).getListtitle();
          Eventlist eventlist=new Eventlist(evlist,evtitle);
          eventlists.add(eventlist);
        }
  }*/
public LatLng getLatlng(){
    return latlng;
}
public void setLatlng(LatLng mylatlng){
latlng=mylatlng;
}
    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

}
