package cn.jiangzehui.hx;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import cn.jiangzehui.hx.util.T;

public class LogoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(1000);
                    T.open(LogoActivity.this, LoginActivity.class);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
