package com.example.isc;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class Common {
    public static final int KILL_ACTIVITY_REG=12;
    public static final int ONE_MEGA_BYTE = 1024 * 1024;
    public static final String[] position = new String[]{ "Head","Member"};
    public static final int IMAGE_QUALITY =25;//percent



    public static  int convertPostionStringToInt(String postionText){
       for(int i=0;i<position.length;i++){
           if(postionText.equals(position[i])){
               return i;
           }
       }
       return -1;
    }

   public static String convertBitmapToString(Bitmap image){

        if(image!=null){
            ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.WEBP,85,byteArrayOutputStream);
            return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
        }
        else{
            return "";
        }

    }

   public static Bitmap convertStringToBitmap(String image) {
       if(!image.equals("")){
           byte[] bytes;
           bytes = Base64.decode(image, Base64.DEFAULT);
           return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
       }
        else{
            return null;
       }
    }
}
