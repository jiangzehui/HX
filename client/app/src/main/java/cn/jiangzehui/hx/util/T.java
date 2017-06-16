package cn.jiangzehui.hx.util;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by quxianglin on 16/12/14.
 */
public class T {
    private static Intent intent = new Intent();
    public static Context context;
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String getTime() {
        return sdf.format(new Date());
    }
    public static String getTime(long time) {
        return sdf.format(new Date(time));
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
