package com.example.note.justdo.PlaceReminder;

public class PlaceRemind {
    public double latitude;
    public double longtitude;
    public double radius;
    public String content;
    int isfinish=0;
    boolean isRight=true;
    // 0 为初始状态
    // 1 为在圈内
    // 2 为在圈外

    public PlaceRemind(double latitude,double longtitude,double radius,String content){
        this.latitude=latitude;
        this.longtitude=longtitude;
        this.radius=radius;
        this.content=content;
    }
    public float distance(double nlatitude,double nlongtitude){
        nlatitude *= 0.01745329251994329D;
        nlongtitude *= 0.01745329251994329D;
        latitude *= 0.01745329251994329D;
        longtitude *= 0.01745329251994329D;
        double var10 = Math.sin(nlatitude);
        double var12 = Math.sin(nlongtitude);
        double var14 = Math.cos(nlatitude);
        double var16 = Math.cos(nlongtitude);
        double var18 = Math.sin(latitude);
        double var20 = Math.sin(longtitude);
        double var22 = Math.cos(latitude);
        double var24 = Math.cos(longtitude);
        double[] var26 = new double[3];
        double[] var27 = new double[3];
        var26[0] = var16 * var14;
        var26[1] = var16 * var10;
        var26[2] = var12;
        var27[0] = var24 * var22;
        var27[1] = var24 * var18;
        var27[2] = var20;
        double var28 = Math.sqrt((var26[0] - var27[0]) * (var26[0] - var27[0]) + (var26[1] - var27[1]) * (var26[1] - var27[1]) + (var26[2] - var27[2]) * (var26[2] - var27[2]));
        return (float)(Math.asin(var28 / 2.0D) * 1.27420015798544E7D);
    }
    public void isfinish(double nlatitude,double nlongtitude){
        float distance=distance(nlatitude,nlongtitude);
        if(isfinish==0) {
            if (distance <= radius) {
                isfinish = 1;
            } else {
                isfinish = 2;
            }
        }
        else if((isfinish==1&&distance>=radius)) {
            isfinish=2;
            isRight=false;
        }
        else if((isfinish==2&&distance<=radius)){
            isfinish=1;
            isRight=false;
            }

    }
}
