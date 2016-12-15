package cn.jiangzehui.hx;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.util.NetUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.jiangzehui.hx.util.T;

public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.tv_tishi)
    TextView tvTishi;

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


    //实现ConnectionListener接口
    private class MyConnectionListener implements EMConnectionListener {
        @Override
        public void onConnected() {
            tvTishi.setVisibility(View.INVISIBLE);
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
