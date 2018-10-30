package com.example.note.justdo.TActivityTools;

import android.graphics.drawable.Drawable;

/**
 * Created by Choz on 2018/4/5.
 * 多列表数据类
 */

public class Eventview {
    Drawable background;
    String title;
    int Type;

    public int getType() {
        return Type;
    }

    public Eventview(Drawable background, String title, int Type) {
        this.background = background;
        this.title = title;
        this.Type = Type;
    }

    public void setType(int Type) {
        this.Type = Type;

    }

    public Eventview(int type,Drawable background) {
        Type = type;
        this.background=background;
    }

    public Eventview(Drawable background, String title){
     this.background=background;
     this.title=title;
    }

    public Drawable getBackground() {
        return background;
    }

    public void setBackground(Drawable background) {
        this.background = background;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
