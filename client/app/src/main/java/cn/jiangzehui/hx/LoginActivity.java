package cn.jiangzehui.hx;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jiangzehui.hx.util.T;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.et_user)
    EditText etUser;
    @BindView(R.id.et_pswd)
    EditText etPswd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }


    @OnClick({R.id.btn_login, R.id.tv_regster})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                login();
                break;
            case R.id.tv_regster:
                T.open(LoginActivity.this, LoginActivity.class);
                break;
        }
    }

    /**
     * 登陆操作
     */
    private void login() {
        String str_user = etUser.getText().toString();
        String str_pswd = etPswd.getText().toString();
    }
}
