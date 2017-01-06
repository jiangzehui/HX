package cn.jiangzehui.hx;

import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.jiangzehui.hx.model.ChatMessage;
import cn.jiangzehui.hx.receiver.ChatReceiver;
import cn.jiangzehui.hx.util.T;

public class ChatActivity extends AppCompatActivity {

    @InjectView(R.id.rv)
    RecyclerView rv;
    List<ChatMessage> list = new ArrayList<>();
    @InjectView(R.id.et_content)
    EditText etContent;
    String username;
    final int OUTPUT = 1;//发送
    final int INPUT = 2;//接收

    LayoutInflater inflater;
    MyAdapter adapter;
    ChatReceiver cr;
    IntentFilter filter = new IntentFilter();
    public static ChatActivity ca;

    @Override
    protected void onStart() {
        super.onStart();
         ca = this;
        cr = new ChatReceiver(ucu);
        filter.addAction("com.chat.msg");
        //注册receiver
        registerReceiver(cr, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(cr);
        ca = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.inject(this);
        rv.setLayoutManager(new LinearLayoutManager(this));
        username = getIntent().getStringExtra("username");
        if (username == null) {
            ChatMessage cm = (ChatMessage) getIntent().getSerializableExtra("cm");
            username = cm.getUser();
            list.add(cm);
            adapter = new MyAdapter(list);
            rv.setAdapter(adapter);

        }
        inflater = LayoutInflater.from(this);


//        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(username);
//        //获取此会话的所有消息
//        messages = conversation.getAllMessages();
    }


    public ChatReceiver.UpdateChatUi ucu = new ChatReceiver.UpdateChatUi() {

        @Override
        public void msg(ChatMessage cm) {
            list.add(cm);
            if (adapter == null) {
                adapter = new MyAdapter(list);
                rv.setAdapter(adapter);
            } else {
                adapter.setList(list);
            }
        }
    };


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
        cm.setTxt(content);
        cm.setTime(T.getTime());
        cm.setUser(EMClient.getInstance().getCurrentUser());
        list.add(cm);
        if (adapter == null) {
            adapter = new MyAdapter(list);
            rv.setAdapter(adapter);
        } else {
            adapter.setList(list);
        }

    }


    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyHolder> {

        List<ChatMessage> list;

        public MyAdapter(List<ChatMessage> list) {
            this.list = list;
        }

        public void setList(List<ChatMessage> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        @Override
        public int getItemViewType(int position) {
            return list.get(position).getType();
        }

        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyHolder holder = null;
            switch (viewType) {
                case INPUT:
                    holder = new MyHolder(inflater.inflate(R.layout.item_chat_input, parent, false));
                    break;
                case OUTPUT:
                    holder = new MyHolder(inflater.inflate(R.layout.item_chat_output, parent, false));
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
                tv.setText(list.get(position).getTxt());
            }
        }
    }
}
