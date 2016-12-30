package cn.jiangzehui.hx;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.NetUtils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.jiangzehui.hx.util.T;

public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.tv_tishi)
    TextView tvTishi;
    @InjectView(R.id.lv)
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        EMClient.getInstance().groupManager().loadAllGroups();
        EMClient.getInstance().chatManager().loadAllConversations();
        //注册一个监听连接状态的listener
        EMClient.getInstance().addConnectionListener(new MyConnectionListener());
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
}
