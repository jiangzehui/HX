package cn.jiangzehui.hx;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ChatActivity extends AppCompatActivity {

    @InjectView(R.id.rv)
    RecyclerView rv;
     List<EMMessage> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.inject(this);
        String username = getIntent().getStringExtra("username");
        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(username);
        //获取此会话的所有消息
        messages = conversation.getAllMessages();
    }


    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyHolder> {

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }

        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(MyHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return messages.size();
        }

        class MyHolder extends RecyclerView.ViewHolder {
            TextView tv;
            ImageView iv;

            public MyHolder(View itemView) {
                super(itemView);
                tv = (TextView) itemView.findViewById(R.id.tv);
                iv = (ImageView) itemView.findViewById(R.id.iv);
            }
        }
    }
}
