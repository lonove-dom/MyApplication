package com.example.note.justdo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.Date;
import java.util.List;

/**
 * Created by Choz on 2018/3/18.
 */

public class Eventdaomanger {
    public static DaoSession daoSession = null;
    public static Context context;
    public static SQLiteDatabase db = null;
    public static DaoMaster.DevOpenHelper helper = null;
    public static DaoMaster daoMaster = null;
    public Eventdaomanger(Context context){
        this.context=context;
        setupDatabase();
    }
    public synchronized DaoMaster.DevOpenHelper getHelperInstance(Context context) {
        if (helper == null) {
            helper = new DaoMaster.DevOpenHelper(context, "greendaodb", null);
        }
        return helper;
    }
    public DaoSession getDaoSession(){
        if(daoSession==null)
        {   setupDatabase();}
        return daoSession;

    }
    private void setupDatabase() {
        // 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper 对象。
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为 greenDAO 已经帮你做了。
        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
        helper = getHelperInstance(context);
        db = helper.getWritableDatabase();
        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
        if (daoMaster == null) {
            daoMaster = new DaoMaster(db);
        }
        if (daoSession == null) {
            daoSession = daoMaster.newSession();
        }
    }
    public SQLiteDatabase getDb() {
        if (db == null) {
            setupDatabase();
        }
        return db;
    }
    /**
     * 获取可读数据库
     */
    private SQLiteDatabase getReadableDatabase() {
        SQLiteDatabase db = getHelperInstance(context).getReadableDatabase();
        return db;
    }
    private SQLiteDatabase getWritableDatabase() {
        SQLiteDatabase db = getHelperInstance(context).getWritableDatabase();
        return db;
    }
    public void insertUserList(List<Event> events) {
        if (events == null || events.isEmpty()) {
            return;
        }
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        EventDao eventDao = daoSession.getEventDao();
        eventDao.insertInTx(events);
    }
    public Boolean IsTimeRemindExist(int listid){
        List<Event> events=queryEventList(listid);
        Date date=new Date();
        for(Event event:events){
            if (event.getStartmills()>date.getTime()){
                return true;
            }
        }
        return false;
    }
    public void insertevent(Event event){
        daoSession.getEventDao().insert(event);
    }
    public List<Event> getfinalEventlist(int listid){
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        EventDao eventDao = daoSession.getEventDao();
        QueryBuilder<Event> qb = eventDao.queryBuilder();
        qb.where(EventDao.Properties.Listid.eq(listid),EventDao.Properties.Context.notEq("SYMBOL STR")).orderAsc(EventDao.Properties.Listid);
        List<Event> list = qb.list();
        return list;
       /* List<Event> list=queryEventList(listid);
        List<Event> finlist=new ArrayList<>();
        for(Event event:list){
            if(!event.getContext().equals("SYMBOL STR")){
                finlist.add(event);
                Log.d("TAG","list message=="+event.getContext()+"list ID=="+event.getId());
            }
        }*/
    }
    public void deletefinishedEvent(int listid){
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        EventDao eventDao = daoSession.getEventDao();
        QueryBuilder<Event> qb = eventDao.queryBuilder();
        qb.where(EventDao.Properties.Listid.eq(listid),EventDao.Properties.Context.notEq("SYMBOL STR"),EventDao.Properties.IsLinearShow.eq(true)).orderAsc(EventDao.Properties.Listid);
        List<Event> list = qb.list();
        for(Event event:list){
            deleteByid(event);
        }

       /* List<Event> list=queryEventList(listid);
        List<Event> finlist=new ArrayList<>();
        for(Event event:list){
            if(!event.getContext().equals("SYMBOL STR")){
                finlist.add(event);
                Log.d("TAG","list message=="+event.getContext()+"list ID=="+event.getId());
            }
        }*/
    }
    public List<Event> queryEventList(int listid) {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        EventDao eventDao = daoSession.getEventDao();
        QueryBuilder<Event> qb = eventDao.queryBuilder();
        qb.where(EventDao.Properties.Listid.eq(listid)).orderAsc(EventDao.Properties.Listid);
        List<Event> list = qb.list();
        return list;
    }
    public int getLineareventsNum(int listid){
        List<Event> events=getfinalEventlist(listid);
        int num=0;
        for(Event event:events){
            if(!event.getIsLinearShow()){
                num++;
            }
        }
        return num;
    }
    public void deleteByposition(int listid,int position){
        List<Event> events=getfinalEventlist(listid);
        daoSession.getEventDao().deleteByKey(events.get(position).getId());
    }
    public void updateReducedlistid(int listid){
        List<Event> list=queryEventList(listid);
        for(Event event:list){
            event.setListid(listid-1);
            daoSession.getEventDao().update(event);
            Log.d("TAG",event.getContext()+"ID="+event.getListid());
        }
    }
    public void updateIncreasedlistid(int listid){
        List<Event> list=queryEventList(listid);
        for(Event event:list){
            event.setListid(listid+1);
            daoSession.getEventDao().update(event);
            Log.d("TAG",event.getContext()+"ID="+event.getListid());
        }
    }
        public void deleteBylistid(int listid){
        List<Event> events=queryEventList(listid);
        for(Event event:events){
            daoSession.getEventDao().deleteByKey(event.getId());
        }
    }
    public void deleteButRemainfirst(int listid){
        List<Event> events=getfinalEventlist(listid);
        for(Event event:events){
                daoSession.getEventDao().deleteByKey(event.getId());
        }
        Event event=daoSession.getEventDao().queryBuilder().where(EventDao.Properties.Listid.eq(listid),EventDao.Properties.Context.eq("SYMBOL STR")).unique();
        event.setBackgroundString(null);
        event.setListtitle("first");
        daoSession.getEventDao().update(event);
    }
    public void updateSwapedlistid(List<Event> startlist,int finalid){
        for(Event event:startlist){
            event.setListid(finalid);
            daoSession.getEventDao().update(event);
        }
    }
    public void updateSingleEvent(Event event){
        Event subev=daoSession.getEventDao().load(event.getId());
        subev.setIsLinearShow(event.getIsLinearShow());
        daoSession.getEventDao().update(subev);
    }
 /*   public void updateSwapedlistevent(Event startev,Event finalev){
        String content=startev.getContext();
        startev.setContext(finalev.getContext());
        daoSession.getEventDao().update(startev);
        finalev.setContext(content);
        daoSession.getEventDao().update(finalev);
    }*/

 /*
   数据库中的event移位操作
   思路大概为整体的上移，下移操作
  */

    public void updateSwapedevents(int listid,int startpos,int finalpos,Boolean isLinearShow) {//第三个参数为移动事件是否标记完成的设置
        List<Event> events=getfinalEventlist(listid);//根据listid获取eventlist
  //      Boolean islinearshow=events.get(startpos).getIsLinearShow();
        Event event=events.get(startpos);
        String str = event.getContext();//暂存该事件的内容
        long startmills=event.getStartmills();
        int Type=event.getType();
        int interval=event.getIntervel();
        //下移
        if (finalpos > startpos) {
            for (int i = startpos; i <= finalpos; i++) {
                if (i < finalpos) {
                    //除最后一个事件外，依次将后一事件的信息转移到当前事件
                    Event event1 = events.get(i);
                    Event event2=events.get(i+1);
                    event1.setContext(event2.getContext());
                    event1.setLinearShow(event2.getLinearShow());
                    event1.setStartmills(event2.getStartmills());
                    event1.setIntervel(event2.getIntervel());
                    event1.setType(event2.getType());
                    Log.d("TAG",event1.getContext()+"ID=="+event1.getId());
             //       daoSession.getEventDao().update(event);
                } else {//最后一个事件特殊处理，赋移位事件的信息
                    Event ev = events.get(i);
                    ev.setContext(str);
                    ev.setLinearShow(isLinearShow);
                    ev.setType(Type);
                    ev.setIntervel(interval);
                    ev.setStartmills(startmills);
                 //   daoSession.getEventDao().update(ev);
                    Log.d("TAG",ev.getContext()+"ID=="+ev.getId());
                }
            }
        } else {
            //上移，操作与上类似，方向相反
            for (int i = startpos; i >=finalpos;i--) {
                   if(i>finalpos){
                       Event event1 = events.get(i);
                       Event event2=events.get(i-1);
                       event1.setContext(event2.getContext());
                       event1.setLinearShow(event2.getLinearShow());
                       event1.setStartmills(event2.getStartmills());
                       event1.setIntervel(event2.getIntervel());
                       event1.setType(event2.getType());
                   //    daoSession.getEventDao().update(event);
                       Log.d("TAG",event1.getContext());
                   }
                   else {
                       Event ev = events.get(i);
                       ev.setContext(str);
                       ev.setLinearShow(isLinearShow);
                       ev.setType(Type);
                       ev.setIntervel(interval);
                       ev.setStartmills(startmills);
                 //      daoSession.getEventDao().update(ev);
                       Log.d("TAG",ev.getContext());
                   }
            }
        }
        //数据库整体更新该eventlist
        daoSession.getEventDao().updateInTx(events);

    }
    public void updateSwapedevents(int listid,int startpos,int finalpos) {
        List<Event> events=getfinalEventlist(listid);//根据listid获取eventlist
        Event event=events.get(startpos);
        Boolean islinearshow=event.getIsLinearShow();
        String str = event.getContext();//暂存该事件的内容
        long startmills=event.getStartmills();
        int Type=event.getType();
        int interval=event.getIntervel();
        //下移
        if (finalpos > startpos) {
            for (int i = startpos; i <= finalpos; i++) {
                if (i < finalpos) {
                    //除最后一个事件外，依次将后一事件的信息转移到当前事件
                    Event event1 = events.get(i);
                    Event event2=events.get(i+1);
                    event1.setContext(event2.getContext());
                    event1.setLinearShow(event2.getLinearShow());
                    event1.setStartmills(event2.getStartmills());
                    event1.setIntervel(event2.getIntervel());
                    event1.setType(event2.getType());
                    Log.d("TAG",event1.getContext()+"ID=="+event1.getId());
                    //       daoSession.getEventDao().update(event);
                } else {//最后一个事件特殊处理，赋移位事件的信息
                    Event ev = events.get(i);
                    ev.setContext(str);
                    ev.setLinearShow(islinearshow);
                    ev.setType(Type);
                    ev.setIntervel(interval);
                    ev.setStartmills(startmills);
                    //   daoSession.getEventDao().update(ev);
                    Log.d("TAG",ev.getContext()+"ID=="+ev.getId());
                }
            }
        } else {
            //上移，操作与上类似，方向相反
            for (int i = startpos; i >=finalpos;i--) {
                if(i>finalpos){
                    Event event1 = events.get(i);
                    Event event2=events.get(i-1);
                    event1.setContext(event2.getContext());
                    event1.setLinearShow(event2.getLinearShow());
                    event1.setStartmills(event2.getStartmills());
                    event1.setIntervel(event2.getIntervel());
                    event1.setType(event2.getType());
                    //    daoSession.getEventDao().update(event);
                    Log.d("TAG",event1.getContext());
                }
                else {
                    Event ev = events.get(i);
                    ev.setContext(str);
                    ev.setLinearShow(islinearshow);
                    ev.setType(Type);
                    ev.setIntervel(interval);
                    ev.setStartmills(startmills);
                    //      daoSession.getEventDao().update(ev);
                    Log.d("TAG",ev.getContext());
                }
            }
        }
        //数据库整体更新该eventlist
        daoSession.getEventDao().updateInTx(events);
    }
    public long getfirsteventId(int listid){
        List<Event> list=getfinalEventlist(listid);
        return (list.get(0)).getId();
    }
    public void deleSQL(){
        SQLiteDatabase db=getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoMaster.dropAllTables(daoMaster.getDatabase(),true);
        DaoMaster.createAllTables(daoMaster.getDatabase(),true);
    }
    public void deleteByid(Event event){
        daoSession.getEventDao().deleteByKey(event.getId());
    }

    /*public void insertEvent(Event event) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        EventDao eventDao = daoSession.getEventDao();
        eventDao.insert(event);
    }
    public void deleteEvent(Event event) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        EventDao eventDao = daoSession.getEventDao();
        eventDao.delete(event);
    }
    public void updateEvent(Event event) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        EventDao eventDao = daoSession.getEventDao();
        eventDao.update(event);
    }*/
}
