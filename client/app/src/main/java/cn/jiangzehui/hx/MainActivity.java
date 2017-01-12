package cn.jiangzehui.hx;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.NetUtils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.jiangzehui.hx.model.ChatMessage;
import cn.jiangzehui.hx.util.T;

public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.tv_tishi)
    TextView tvTishi;
    @InjectView(R.id.lv)
    ListView lv;
    Intent intent;


    NotificationCompat.Builder builder;
    NotificationManagerCompat notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        EMClient.getInstance().groupManager().loadAllGroups();
        EMClient.getInstance().chatManager().loadAllConversations();
        //注册一个监听连接状态的listener
        EMClient.getInstance().addConnectionListener(new MyConnectionListener());
        EMClient.getInstance().chatManager().addMessageListener(msgListener);
    }


    @OnClick(R.id.tv_add)
    public void onClick() {
        T.open(this, AddFriendActivity.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final List<String> usernames = EMClient.getInstance().contactManager().getAllContactsFromServer();
                    if (usernames.size() != 0) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                lv.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, usernames));
                            }
                        });
                    }
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            T.open(MainActivity.this, ChatActivity.class, "username", usernames.get(i));
                        }
                    });


                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }


    EMMessageListener msgListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {

            Log.i("chat", "收到消息");
            for (int i = 0; i < messages.size(); i++) {
                if (messages.get(messages.size() - 1).getType().toString().equals("TXT")) {
                    EMTextMessageBody body = (EMTextMessageBody) messages.get(messages.size() - 1).getBody();
                    Log.i("msg" + i, body.getMessage());
                }
            }
//            //收到消息
            ChatMessage cm = new ChatMessage();

            if (messages.get(messages.size() - 1).getType().toString().equals("TXT")) {
                EMTextMessageBody body = (EMTextMessageBody) messages.get(messages.size() - 1).getBody();
                cm.setTxt(body.getMessage());
            }

            cm.setUser(messages.get(messages.size() - 1).getUserName());
            cm.setType(2);
            cm.setTime(T.getTime());
            if (ChatActivity.ca == null) {
                if (builder == null) {
                    builder = new NotificationCompat.Builder(MainActivity.this);
                    notificationManager =
                            NotificationManagerCompat.from(MainActivity.this);
                }
                Intent intent1 = new Intent(MainActivity.this, ChatActivity.class);
                intent1.putExtra("username", cm.getUser());
                PendingIntent pi = PendingIntent.getActivity(MainActivity.this, 1, intent1, PendingIntent.FLAG_UPDATE_CURRENT);

                Notification notification = builder
                        .setContentTitle(cm.getUser())
                        .setContentText(cm.getTxt())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(pi)
                        .build();
                notification.flags = Notification.FLAG_AUTO_CANCEL;

                notificationManager.notify(1, notification);
            } else {
                if (intent == null) {
                    intent = new Intent();
                }
                intent.setAction("com.chat.msg");
                intent.putExtra("cm", cm);
                intent.putExtra("msg", "msg");
                sendBroadcast(intent);
            }


        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            //收到透传消息
        }

        @Override
        public void onMessageReadAckReceived(List<EMMessage> messages) {
            //收到已读回执
        }

        @Override
        public void onMessageDeliveryAckReceived(List<EMMessage> message) {
            //收到已送达回执
        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {
            //消息状态变动
        }
    };

    //实现ConnectionListener接口
    private class MyConnectionListener implements EMConnectionListener {
        @Override
        public void onConnected() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvTishi.setVisibility(View.GONE);
                }
            });
        }

        @Override
        public void onDisconnected(final int error) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (error == EMError.USER_REMOVED) {
                        T.open(MainActivity.this, LoginActivity.class);
                        T.show(MainActivity.this, "帐号已经被移除");
                        finish();
                        // 显示帐号已经被移除
                    } else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                        T.open(MainActivity.this, LoginActivity.class);
                        T.show(MainActivity.this, "帐号在其他设备登录");
                        finish();
                        // 显示帐号在其他设备登录
                    } else {
                        if (NetUtils.hasNetwork(MainActivity.this)) {
                            tvTishi.setText("连接不到聊天服务器");
                            tvTishi.setVisibility(View.VISIBLE);
                            //连接不到聊天服务器
                        } else {
                            tvTishi.setText("当前网络不可用，请检查网络设置");
                            tvTishi.setVisibility(View.VISIBLE);
                            //当前网络不可用，请检查网络设置
                        }

                    }
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //记得在不需要的时候移除listener，如在activity的onDestroy()时
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
    }
}
