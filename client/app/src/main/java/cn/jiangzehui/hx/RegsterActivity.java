package cn.jiangzehui.hx;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.jiangzehui.hx.util.T;

public class RegsterActivity extends AppCompatActivity {


    @InjectView(R.id.et_user)
    EditText etUser;
    @InjectView(R.id.et_pswd)
    EditText etPswd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regster);
        ButterKnife.inject(this);

    }


    @OnClick({R.id.ivBack, R.id.btn_regster})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                finish();
                break;
            case R.id.btn_regster:
                final String str_user = etUser.getText().toString();
                final String str_pswd = etPswd.getText().toString();
                if (str_user.equals("") || str_pswd.equals("")) {
                    T.show("用户名或密码不能为空");
                    return;
                }



                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        //注册失败会抛出HyphenateException
                        try {
                            EMClient.getInstance().createAccount(str_user, str_pswd);//同步方法
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    T.show("注册成功");
                                    finish();
                                }
                            });
                        } catch (final HyphenateException e) {
                            e.printStackTrace();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    T.show(e.getMessage());
                                }
                            });

                        }

                    }
                }.start();
                break;
        }
    }
}
