package cn.jiangzehui.hx;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.jiangzehui.hx.emoji.Emoji;
import cn.jiangzehui.hx.emoji.EmojiFragment;
import cn.jiangzehui.hx.emoji.EmojiUtil;
import cn.jiangzehui.hx.model.ChatMessage;
import cn.jiangzehui.hx.util.T;
import cn.jiangzehui.hx.view.CircleImageView;

public class ChatActivity extends AppCompatActivity implements EmojiFragment.OnEmojiClickListener {

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
    IntentFilter filter = new IntentFilter();
    public static ChatActivity ca;
    boolean isShow = false;
    EmojiFragment emojiFragment;
    @InjectView(R.id.tvName)
    TextView tvName;
    EMConversation conversation;
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ca = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.inject(this);
        EventBus.getDefault().register(this);
        emojiFragment = EmojiFragment.Instance();
        getSupportFragmentManager().beginTransaction().add(R.id.Container, emojiFragment).hide(emojiFragment).commit();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        linearLayoutManager.setStackFromEnd(true);//列表再底部开始展示，反转后由上面开始展示
        rv.setLayoutManager(linearLayoutManager);

        inflater = LayoutInflater.from(this);
        username = getIntent().getStringExtra("username");
        tvName.setText(username);
         conversation = EMClient.getInstance().chatManager().getConversation(username, EMConversation.EMConversationType.Chat, true);
        if (conversation == null) {
            return;
        }
        conversation.markAllMessagesAsRead();//设置已读
        //获取聊天记录
        final List<EMMessage> msgs = conversation.getAllMessages();
        int msgCount = msgs != null ? msgs.size() : 0;
        if (msgCount < conversation.getAllMsgCount() && msgCount < 20) {
            String msgId = null;
            if (msgs != null && msgs.size() > 0) {
                msgId = msgs.get(0).getMsgId();
            }
            conversation.loadMoreMsgFromDB(msgId, 20 - msgCount);
        }
        //获取此会话的所有消息
        List<EMMessage> messages = conversation.getAllMessages();
        for (int i = 0; i < messages.size(); i++) {
            ChatMessage cm = new ChatMessage();

            if (messages.get(i).getType().toString().equals("TXT")) {
                EMTextMessageBody body = (EMTextMessageBody) messages.get(i).getBody();
                cm.setTxt(body.getMessage());
            }
            cm.setUser(messages.get(i).getUserName());
            if (messages.get(i).getFrom().equals(username)) {
                cm.setType(2);
            } else {
                cm.setType(1);
            }

            cm.setTime(T.getTime(messages.get(i).getMsgTime()));
            list.add(cm);
        }
        adapter = new MyAdapter(list);
        rv.setAdapter(adapter);
        rv.smoothScrollToPosition(list.size());
        ca = this;
        filter.addAction("com.chat.msg");


//        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(username);
//        //获取此会话的所有消息
//        messages = conversation.getAllMessages();
    }

    @Override
    protected void onPause() {
        super.onPause();
        conversation.markAllMessagesAsRead();//设置已读
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    @Subscribe(threadMode= ThreadMode.MAIN)
    public void updateUi(ChatMessage cm){
        list.add(cm);
        if (adapter == null) {
            adapter = new MyAdapter(list);
            rv.setAdapter(adapter);
        } else {
            adapter.setList(list);
        }

    }

    @Override
    public void onBackPressed() {

        if(emojiFragment.isVisible()){
            getSupportFragmentManager().beginTransaction().hide(emojiFragment).commit();
        }else{
            finish();
        }
    }

    @OnClick({R.id.btn_img, R.id.btn_send,R.id.ivBack})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_img:
                if (emojiFragment.isVisible()) {//判断是否显示
                    getSupportFragmentManager().beginTransaction().hide(emojiFragment).commit();
                } else {
                    getSupportFragmentManager().beginTransaction().show(emojiFragment).commit();
                }

                break;
            case R.id.btn_send:
                String content = etContent.getText().toString();
                if (content.equals("")) {
                    T.show( "发送内容不能为空");
                    return;
                }
                etContent.setText("");
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
                rv.smoothScrollToPosition(list.size());
                break;
            case R.id.ivBack:
                finish();
                break;
        }
    }

    @Override
    public void onEmojiDelete() {
        String text = etContent.getText().toString();
        if (text.isEmpty()) {
            return;
        }
        if ("]".equals(text.substring(text.length() - 1, text.length()))) {
            int index = text.lastIndexOf("[");
            if (index == -1) {
                int action = KeyEvent.ACTION_DOWN;
                int code = KeyEvent.KEYCODE_DEL;
                KeyEvent event = new KeyEvent(action, code);
                etContent.onKeyDown(KeyEvent.KEYCODE_DEL, event);
                displayTextView();
                return;
            }
            etContent.getText().delete(index, text.length());
            displayTextView();
            return;
        }
        int action = KeyEvent.ACTION_DOWN;
        int code = KeyEvent.KEYCODE_DEL;
        KeyEvent event = new KeyEvent(action, code);
        etContent.onKeyDown(KeyEvent.KEYCODE_DEL, event);
        displayTextView();
    }

    @Override
    public void onEmojiClick(Emoji emoji) {
        if (emoji != null) {
            int index = etContent.getSelectionStart();
            Editable editable = etContent.getEditableText();
            if (index < 0) {
                editable.append(emoji.getContent());
            } else {
                editable.insert(index, emoji.getContent());
            }
        }
        displayTextView();
    }

    private void displayTextView() {
        try {
            EmojiUtil.handlerEmojiText(etContent, etContent.getText().toString(), this);
        } catch (IOException e) {
            e.printStackTrace();
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
            CircleImageView iv;

            public MyHolder(View itemView) {
                super(itemView);
                tv = (TextView) itemView.findViewById(R.id.tv);
                iv = (CircleImageView) itemView.findViewById(R.id.iv);
            }

            private void set(int position) {

                try {
                    EmojiUtil.handlerEmojiText(tv, list.get(position).getTxt(), ChatActivity.this);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
