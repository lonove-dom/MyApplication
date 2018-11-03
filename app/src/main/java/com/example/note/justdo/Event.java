package com.example.note.justdo;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Choz on 2018/2/27.
 */
@Entity
public class Event {
    @Id(autoincrement=true)
    private Long id;
    @Property(nameInDb = "CONTEXT")
    private String context;
    @Property(nameInDb = "LISTID")
    private int listid;
    @Property(nameInDb = "LISTTITLE")
    private String listtitle;
    private int listnum;
    private String BackgroundString;
    @Property
    Boolean IsLinearShow=false;
 //   @Property
   // float Latitude;
    //@Property
    //float longitude;
    long startmills;//时间
    int intervel;//间隔
    int type;//种类
    int hour;
    int minute;
    int second;
    private long tLatitude;
    private long Longitude;
    private double radius;//选定地点提醒的半径
    private String place;//选择地点的名称

    public int getListnum() {
        return listnum;
    }

    public void setListnum(int listnum) {
        this.listnum = listnum;
    }

    public Event(String context){
        this.context=context;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }


    public long gettLatitude() {
        return tLatitude;
    }

    public void settLatitude(long tLatitude) {
        this.tLatitude = tLatitude;
    }

    public long getLongitude() {
        return Longitude;
    }

    public void setLongitude(long longitude) {
        Longitude = longitude;
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

    public Event(Long id, String context, int listid, String listtitle, int listnum) {
        this.id = id;
        this.context = context;
        this.listid = listid;
        this.listtitle = listtitle;
        this.listnum = listnum;
    }

    public Event(String context, int listid, String listtitle) {
        this.context = context;
        this.listid = listid;
        this.listtitle = listtitle;
    }

    @Generated(hash = 344677835)
    public Event() {
    }

    @Generated(hash = 1309130168)
    public Event(Long id, String context, int listid, String listtitle, int listnum,
            String BackgroundString, Boolean IsLinearShow, long startmills, int intervel, int type,
            int hour, int minute, int second) {
        this.id = id;
        this.context = context;
        this.listid = listid;
        this.listtitle = listtitle;
        this.listnum = listnum;
        this.BackgroundString = BackgroundString;
        this.IsLinearShow = IsLinearShow;
        this.startmills = startmills;
        this.intervel = intervel;
        this.type = type;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }

    @Override
    public String toString() {
        return  context;

    }

    public Long getId() {
        return this.id;
    }


    public int getListid() {
        return this.listid;
    }

    public void setListid(int listid) {
        this.listid = listid;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getListtitle() {
        return this.listtitle;
    }

    public void setListtitle(String listtitle) {
        this.listtitle = listtitle;
    }

    public String getBackgroundString() {
        return this.BackgroundString;
    }

    public void setBackgroundString(String BackgroundString) {
        this.BackgroundString = BackgroundString;
    }
    public boolean equal(Event event) {
        return getContext().equals(((Event)event).getContext())?true:false;
    }

    public Boolean getLinearShow() {
        return IsLinearShow;
    }

    public void setLinearShow(Boolean linearShow) {
        IsLinearShow = linearShow;
    }

    public Boolean getIsLinearShow() {
        return this.IsLinearShow;
    }

    public void setIsLinearShow(Boolean IsLinearShow) {
        this.IsLinearShow = IsLinearShow;
    }

    public long getStartmills() {
        return startmills;
    }

    public void setStartmills(long startmills) {
        this.startmills = startmills;
        caculateTime();
    }

    public int getIntervel() {
        return intervel;
    }

    public void setIntervel(int intervel) {
        this.intervel = intervel;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
    public Date getDate(){
        return new Date(startmills);
    }
    public Calendar getCalendar(){
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(getDate());
        return calendar;
    }
    public void getNextstartmills(){
        Calendar currentcalendar = Calendar.getInstance();
        Calendar cusorCal;
        currentcalendar.setTime(new Date(startmills));
        long currentmills=new Date().getTime();
        int startYear = currentcalendar.get(Calendar.YEAR);
        int startMonth = currentcalendar.get(Calendar.MONTH);
        int startDay = currentcalendar.get(Calendar.DATE);
        while (startmills<currentmills) {
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
                    startmills = startmills + 1000 * intervel * 24 * 60 * 60;
                    break;
                case 6://customweek
                    startmills = startmills + 1000 * intervel * 7 * 24 * 60 * 60;
                    break;

                case 7://custommonth
                    startMonth = startMonth + intervel;
                    if (startMonth > 11) {
                        startYear = startYear + startMonth / 12;
                        startMonth = startMonth % 12;
                    }
                    cusorCal = new GregorianCalendar(startYear, startMonth, startDay, hour, minute, second);
                    startmills = cusorCal.getTimeInMillis();
                    break;

                case 8://customyear
                    startYear = startYear + intervel;
                    cusorCal = new GregorianCalendar(startYear, startMonth, startDay, hour, minute, second);
                    startmills = cusorCal.getTimeInMillis();
                    break;

            }
        }
    }
    private void caculateTime(){
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(new Date(startmills));
        hour=calendar.get(Calendar.HOUR_OF_DAY);
        minute=calendar.get(Calendar.MINUTE);
        second=calendar.get(Calendar.SECOND);
    }

    public int getHour() {
        return this.hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return this.minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getSecond() {
        return this.second;
    }

    public void setSecond(int second) {
        this.second = second;
    }
}
