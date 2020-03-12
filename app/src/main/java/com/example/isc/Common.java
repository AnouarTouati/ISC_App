package com.example.isc;

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
}
