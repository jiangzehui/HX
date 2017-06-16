package cn.jiangzehui.hx;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.util.NetUtils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.jiangzehui.hx.fragment.ChatFragment;
import cn.jiangzehui.hx.fragment.FriendFragment;
import cn.jiangzehui.hx.fragment.MsgFragment;
import cn.jiangzehui.hx.model.ChatMessage;
import cn.jiangzehui.hx.util.T;

public class MainActivity extends FragmentActivity {

    @InjectView(R.id.tv_tishi)
    TextView tvTishi;
    Intent intent;
    @InjectView(R.id.tv1)
    TextView tv1;
    @InjectView(R.id.tv2)
    TextView tv2;
    private MsgFragment mf;
    private FriendFragment ff;
    private ChatFragment cf;


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


        init(0);

    }


    public void init(int index) {
        tv1.setTextColor(ContextCompat.getColor(this,R.color.mainfalse));
        tv2.setTextColor(ContextCompat.getColor(this,R.color.mainfalse));
        tv1.setCompoundDrawablesWithIntrinsicBounds(0,R.mipmap.msg_false,0,0);
        tv2.setCompoundDrawablesWithIntrinsicBounds(0,R.mipmap.friend_false,0,0);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment f : fragments) {
                ft.hide(f);
            }
        }

        Fragment fragment;
        switch (index) {

            case 0:
                tv1.setTextColor(ContextCompat.getColor(this,R.color.maintrue));
                tv1.setCompoundDrawablesWithIntrinsicBounds(0,R.mipmap.msg_true,0,0);
                fragment = getSupportFragmentManager().findFragmentByTag("mf");
                if (fragment == null) {
                    mf = new MsgFragment();
                    ft.add(R.id.frame, mf, "mf");
                } else {
                    ft.show(fragment);
                }
                break;
            case 1:
                tv2.setTextColor(ContextCompat.getColor(this,R.color.maintrue));
                tv2.setCompoundDrawablesWithIntrinsicBounds(0,R.mipmap.friend_true,0,0);
                fragment = getSupportFragmentManager().findFragmentByTag("ff");
                if (fragment == null) {
                    ff = new FriendFragment();
                    ft.add(R.id.frame, ff, "ff");
                } else {
                    ft.show(fragment);
                }
                break;
            case 2:

                fragment = getSupportFragmentManager().findFragmentByTag("cf");
                if (fragment == null) {
                    cf = new ChatFragment();
                    ft.add(R.id.frame, cf, "cf");
                } else {
                    ft.show(fragment);
                }
                break;


        }
        ft.commit();
    }


    EMMessageListener msgListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {

            Log.i("chat", "收到消息");
            for (int i = 0; i < messages.size(); i++) {
                //收到消息
                ChatMessage cm = new ChatMessage();

                if (messages.get(i).getType().toString().equals("TXT")) {
                    EMTextMessageBody body = (EMTextMessageBody) messages.get(i).getBody();
                    cm.setTxt(body.getMessage());
                }

                cm.setUser(messages.get(i).getUserName());
                cm.setType(2);
                cm.setTime(T.getTime(messages.get(i).getMsgTime()));
                if (ChatActivity.ca == null) {
                    if (builder == null) {
                        builder = new NotificationCompat.Builder(MainActivity.this);
                        notificationManager =
                                NotificationManagerCompat.from(MainActivity.this);
                    }
                    Intent intent1 = new Intent(MainActivity.this, ChatActivity.class);
                    intent1.putExtra("username", cm.getUser());
                    PendingIntent pi = PendingIntent.getActivity(MainActivity.this, i, intent1, PendingIntent.FLAG_UPDATE_CURRENT);

                    Notification notification = builder
                            .setContentTitle(cm.getUser())
                            .setContentText(cm.getTxt())
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentIntent(pi)
                            .build();
                    notification.flags = Notification.FLAG_AUTO_CANCEL;

                    notificationManager.notify(i, notification);
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
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mf.updateUi();

                }
            });
//


        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            //收到透传消息
        }

        @Override
        public void onMessageRead(List<EMMessage> messages) {

        }

        @Override
        public void onMessageDelivered(List<EMMessage> messages) {

        }


        @Override
        public void onMessageChanged(EMMessage message, Object change) {
            //消息状态变动
        }
    };

    @OnClick({R.id.tv_zx, R.id.tv_add, R.id.tv1, R.id.tv2, R.id.tv3})
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.tv_zx:
                EMClient.getInstance().logout(true, new EMCallBack() {

                    @Override
                    public void onSuccess() {
                        T.open(MainActivity.this, LoginActivity.class);
                        finish();

                    }

                    @Override
                    public void onProgress(int progress, String status) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onError(int code, String message) {
                        // TODO Auto-generated method stub

                    }
                });

                break;
            case R.id.tv_add:
                T.open(MainActivity.this, AddFriendActivity.class);
                break;

            case R.id.tv1:
                init(0);
                break;
            case R.id.tv2:
                init(1);
                break;
            case R.id.tv3:
                init(2);
                break;
        }
    }

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
                        T.show("帐号已经被移除");
                        finish();
                        // 显示帐号已经被移除
                    } else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                        T.open(MainActivity.this, LoginActivity.class);
                        T.show("帐号在其他设备登录");
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
