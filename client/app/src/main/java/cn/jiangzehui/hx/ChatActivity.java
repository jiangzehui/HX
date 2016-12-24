package cn.jiangzehui.hx;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;

import java.util.List;
import java.util.zip.Inflater;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.jiangzehui.hx.model.ChatMessage;
import cn.jiangzehui.hx.util.T;

public class ChatActivity extends AppCompatActivity {

    @InjectView(R.id.rv)
    RecyclerView rv;
    List<ChatMessage> list;
    @InjectView(R.id.et_content)
    EditText etContent;
    String username;
    final int INPUT = 2;//接收
    final int OUTPUT = 1;//发送
    LayoutInflater inflater;
    MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.inject(this);
        username = getIntent().getStringExtra("username");
        inflater = LayoutInflater.from(this);
        EMClient.getInstance().chatManager().addMessageListener(msgListener);
//        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(username);
//        //获取此会话的所有消息
//        messages = conversation.getAllMessages();
    }

    EMMessageListener msgListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            //收到消息
            ChatMessage cm = new ChatMessage();
            cm.setBody(messages.get(messages.size() - 1).getBody().toString());
            cm.setUser(messages.get(messages.size() - 1).getUserName());
            cm.setType(INPUT);
            cm.setTime(T.getTime());
            list.add(cm);
            if (adapter == null) {
                adapter = new MyAdapter();
                rv.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //记得在不需要的时候移除listener，如在activity的onDestroy()时
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
    }

    @OnClick(R.id.btn_send)
    public void onClick() {
        String content = etContent.getText().toString();
        if (content.equals("")) {
            T.show(ChatActivity.this, "发送内容不能为空");
            return;
        }
        //创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
        EMMessage message = EMMessage.createTxtSendMessage(content, username);
        //发送消息
        EMClient.getInstance().chatManager().sendMessage(message);
        ChatMessage cm = new ChatMessage();
        cm.setType(OUTPUT);
        cm.setBody(content);
        cm.setTime(T.getTime());
        cm.setUser(EMClient.getInstance().getCurrentUser());
        list.add(cm);
        if (adapter == null) {
            adapter = new MyAdapter();
            rv.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }

    }


    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyHolder> {

        @Override
        public int getItemViewType(int position) {
            return list.get(position).getType();
        }

        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyHolder holder = null;
            switch (viewType) {
                case INPUT:
                    holder = new MyHolder(inflater.inflate(R.layout.item_chat_input, null));
                    break;
                case OUTPUT:
                    holder = new MyHolder(inflater.inflate(R.layout.item_chat_output, null));
                    break;
            }
            return holder;
        }

        @Override
        public void onBindViewHolder(MyHolder holder, int position) {
            if (holder != null) {
                holder.set(position);
            }

        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class MyHolder extends RecyclerView.ViewHolder {
            TextView tv;
            ImageView iv;

            public MyHolder(View itemView) {
                super(itemView);
                tv = (TextView) itemView.findViewById(R.id.tv);
                iv = (ImageView) itemView.findViewById(R.id.iv);
            }

            private void set(int position) {
                tv.setText(list.get(position).getBody());
            }
        }
    }
}
