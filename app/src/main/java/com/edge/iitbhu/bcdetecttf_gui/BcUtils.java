package com.edge.iitbhu.bcdetecttf_gui;


import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class BcUtils {
    private List<Bitmap> imageList;
    private int imageCount;
    public String fName;
    public String lName;
    private String email;
    private BcUtils(){
        imageList = new ArrayList<Bitmap>();
        imageCount =0;
        fName ="";
        lName="";
        email="";
    }


    private static BcUtils mBcUtils;
    public static BcUtils get(){
        if(mBcUtils ==null){
            mBcUtils = new BcUtils();
        }
        return mBcUtils;

    }

    // images
    public List<Bitmap> getImageList(){
        return imageList;
    }
    public void addImageToList(Bitmap bmp){
        imageList.add(bmp);
    }

    public void clearImageList(){
        imageList = new ArrayList<Bitmap>();
    }
    public int getImageCount(){
        return imageCount;
    }
    public void setImageCount(int x){
        imageCount = x;
    }

    // User profile

    public void setfName(String x){
        fName = x;

    }
    public void setlName(String x){
        lName = x;
    }
    public  void setEmail(String x){
        email = x;
    }

    public String getfName() {
        return fName;
    }

    public String getlName() {
        return lName;
    }

    public String getEmail() {
        return email;
    }

    public void scaleForDisplay(ImageView iView){
        Bitmap bitmap = convertImageViewToBitmap(iView);
        bitmap = Bitmap.createScaledBitmap(bitmap,224,224,false);
        iView.setImageBitmap(bitmap);
    }
    public Bitmap convertImageViewToBitmap(ImageView iView){
        BitmapDrawable drawable = (BitmapDrawable) iView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        return bitmap;
    }

    public Float roundOff(Float val){
        val = val*10000;
        int temp = Math.round(val);
        val = (float) temp;
        val =val/100;
        return val;
    }


}
