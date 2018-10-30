package com.example.note.justdo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.view.View;

import java.io.ByteArrayOutputStream;

/**
 * Created by Choz on 2018/4/5.
 */

public class imageConverter {
    //Bitmap转字节数组
    public static byte[] BitmapToBytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }
    //字节数组转Bitmap
    public static Bitmap BytesToBimap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }
    //Bitmap缩放
    public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) width / w);
        float scaleHeight = ((float) height / h);
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
        return newbmp;
    }
    //drawble转Bitmap
    public static Bitmap drawableToBitmap(Drawable drawable) {
        // 取 drawable 的长宽
        int w = drawable.getIntrinsicWidth()-1;
        int h = drawable.getIntrinsicHeight()-1;

        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }
    //Bitmap转Drawable
    public static Drawable BitmapToDrawable(Bitmap bitmap){
        return (Drawable)new BitmapDrawable(bitmap);
    }
    //字节数组转String
    public static String byteTostring(byte[] Data){
       return Base64.encodeToString(Data, Base64.DEFAULT);
    }
    //String转字节数组
    public static byte[] stringTobyte(String string){
        return Base64.decode(string.getBytes(), Base64.DEFAULT);
    }
    //Drawable转字符串
    public static String BitmapToString(Bitmap bitmap){
        if(bitmap!=null){
        return byteTostring(BitmapToBytes(bitmap));}
        return null;
    }
    //字符串转Drawable
    public static Drawable StringToDrawable(String string){
        if(string!=null){
        return BitmapToDrawable(BytesToBimap(stringTobyte(string)));}
        else
            return null;
    }
    public  Bitmap ScreenShotTObitmap(Activity activity){
        View screenview=activity.getWindow().getDecorView();
        screenview.destroyDrawingCache();
        screenview.setDrawingCacheEnabled(true);
        return screenview.getDrawingCache();
    }
}
