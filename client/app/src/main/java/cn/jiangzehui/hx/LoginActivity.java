package cn.jiangzehui.hx;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.jiangzehui.hx.util.T;

public class LoginActivity extends AppCompatActivity {


    @InjectView(R.id.et_user)
    EditText etUser;
    @InjectView(R.id.et_pswd)
    EditText etPswd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);


    }


    /**
     * 登陆操作
     */
    private void login() {
        String str_user = etUser.getText().toString();
        String str_pswd = etPswd.getText().toString();
        EMClient.getInstance().login(str_user, str_pswd, new EMCallBack() {//回调
            @Override
            public void onSuccess() {
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();
                Log.d("main", "登录聊天服务器成功！");
                handler.sendEmptyMessage(0);
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                handler.sendEmptyMessage(1);
                Log.d("main", "登录聊天服务器失败！");
            }
        });
    }


    @OnClick({R.id.btn_login, R.id.tv_regster})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                login();
                break;
            case R.id.tv_regster:
                T.open(LoginActivity.this, RegsterActivity.class);
                break;
        }
    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    T.show(LoginActivity.this, "登录聊天服务器成功！");
                    T.open(LoginActivity.this, MainActivity.class);
                    finish();
                    break;
                case 1:
                    T.show(LoginActivity.this, "登录聊天服务器失败！");
                    break;
            }
        }
    };
}
