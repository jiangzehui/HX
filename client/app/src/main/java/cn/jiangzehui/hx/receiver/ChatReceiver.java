package cn.jiangzehui.hx.receiver;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;

import cn.jiangzehui.hx.ChatActivity;
import cn.jiangzehui.hx.R;
import cn.jiangzehui.hx.model.ChatMessage;

/**
 * Created by quxianglin on 17/1/6.
 */
public class ChatReceiver extends BroadcastReceiver {
    UpdateChatUi ucu;


    public ChatReceiver(UpdateChatUi ucu) {
        this.ucu = ucu;
    }



    @Override
    public void onReceive(Context context, Intent intent) {
        String type = intent.getStringExtra("msg");
        switch (type) {
            case "msg"://聊天信息
                ChatMessage cm = (ChatMessage) intent.getSerializableExtra("cm");
                if (ucu != null) {
                    ucu.msg(cm);
                }

                break;

        }
    }

    public interface UpdateChatUi {
        void msg(ChatMessage cm);
    }
}
