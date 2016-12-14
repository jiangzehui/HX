package cn.jiangzehui.hx.util;

import android.content.Context;
import android.content.Intent;

/**
 * Created by quxianglin on 16/12/14.
 */
public class T {
    private static Intent intent = new Intent();

    public static void open(Context context, Class c) {
        intent.setClass(context, c);
        context.startActivity(intent);
    }
}
