package cn.jiangzehui.hx.util;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by quxianglin on 16/12/14.
 */
public class T {
    private static Intent intent = new Intent();


    public static void show(Context context, String content) {
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
