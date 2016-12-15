package cn.jiangzehui.hx;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.jiangzehui.hx.util.T;

public class AddFriendActivity extends AppCompatActivity {

    @InjectView(R.id.et)
    EditText et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        ButterKnife.inject(this);
    }

    @OnClick(R.id.btn)
    public void onClick() {
        final String username = et.getText().toString();
        if (EMClient.getInstance().getCurrentUser().equals(username)) {
            T.show(this, "不能添加自己为好友");
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                //参数为要添加的好友的username和添加理由
                try {
                    EMClient.getInstance().contactManager().addContact(username, "nihao");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            T.show(AddFriendActivity.this, "添加成功");
                        }
                    });
                } catch (final HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            T.show(AddFriendActivity.this, "添加失败," + e.getMessage());
                        }
                    });
                }
            }
        }).start();


    }
}
