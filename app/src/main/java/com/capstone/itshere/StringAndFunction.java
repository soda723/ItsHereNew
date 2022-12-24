package com.capstone.itshere;

import android.util.Log;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class StringAndFunction {
    public static String cp_alarm1 = "3일전";
    public static String cp_alarm2 = "5일전";
    public static String cp_alarm3 = "7일전";
    public static String dateformat = "yyyy-MM-dd";

    public static Timestamp StringToTimeStamp(String datestring){
        String newdate = datestring + " 00:00:00";
        Timestamp timestamp = Timestamp.valueOf(newdate);
        return timestamp;
    }

    public static String timestampToString(String stamp){
        stamp = stamp.replace("Timestamp(seconds=", "").replace(" nanoseconds=", "").replace(")", "");
        String[] array = stamp.split(",");
        String ds = array[0];
        Log.i("Tsts", "타임스탬프 변환실행"+ds);
        long timestamp = Long.parseLong(ds);
        Date date = new java.util.Date(timestamp*1000L);
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+9"));
        String formattedDate = sdf.format(date);
        Log.i("Tsts", "타임스탬프 변환실행"+formattedDate);
        return formattedDate;
    }

    public static String DdayCounter(String stringdate){
        try{
            String[] tmpdate = stringdate.split("-");
            int myear = Integer.parseInt(tmpdate[0]);
            int mmonth = Integer.parseInt(tmpdate[1]);
            int mday = Integer.parseInt(tmpdate[2]);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateformat);
            Calendar todayCal = Calendar.getInstance(); //오늘날짜
            Calendar ddayCal = Calendar.getInstance(); //변형시킬오늘날짜

            mmonth-=1;
            ddayCal.set(myear,mmonth,mday);

            long today = todayCal.getTimeInMillis() / (24*60*60*1000);
            long dday = ddayCal.getTimeInMillis() / (24*60*60*1000);
            long count = today-dday;

            if (count >= 0 ) return "D+"+count;
            else return "D" + count;
        }catch (Exception e) {
            e.printStackTrace();
            return "-1";
        }
    }


}
