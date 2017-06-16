package cn.jiangzehui.hx.util;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by quxianglin on 16/12/14.
 */
public class T {
    private static Intent intent = new Intent();
    public static Context context;
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat sdfs = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat sdfss = new SimpleDateFormat("HH:mm");

    public static String getTime() {
        return sdf.format(new Date());
    }


    public static String getTime(long time) {
        return sdf.format(new Date(time));
    }

    public static String formatDate(long date) {
        String now = sdfs.format(new Date());
        try {
            Date nowDate = sdfs.parse(now);
            long nowLong = nowDate.getTime();
            long oneDayLong = 1000*60*60*24;//一天的毫秒数
            if(date>=nowLong){//今天
               // sdfss.format();
            }else if(date<nowLong&&date>=(nowLong-oneDayLong)){//昨天

            }else if(date<(nowLong-oneDayLong)&&date>=(nowLong-oneDayLong*2)){//前天

            }else{//历史事件

            }




        } catch (ParseException e) {
            e.printStackTrace();
        }


        return sdf.format(new Date(date));
    }



    public static void show(String content) {
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
    }


    public static void open(Context context, Class<?> classs) {
        context.startActivity(intent.setClass(context, classs));
    }

    public static void open(Context context, Class<?> classs, String... value) {
        for (int i = 1; i <= value.length; i++) {

            if (i % 2 == 0) {
                intent.putExtra(value[i - 2], value[i - 1]);
            }

        }
        context.startActivity(intent.setClass(context, classs));
    }
}
