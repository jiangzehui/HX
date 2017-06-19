package cn.jiangzehui.hx.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.jiangzehui.hx.ChatActivity;
import cn.jiangzehui.hx.R;
import cn.jiangzehui.hx.util.T;
import cn.jiangzehui.hx.view.CircleImageView;

/**
 * Created by jiangzehui on 17/1/18.
 */
public class MsgFragment extends Fragment {
    View view;
    @InjectView(R.id.lv)
    ListView lv;
    ArrayList<String> list;
    MyAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (view == null) {
            view = inflater.inflate(R.layout.fragment_msg, container, false);
        }

        ButterKnife.inject(this, view);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                T.open(getActivity(), ChatActivity.class, "username", list.get(i));
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                //删除和某个user会话，如果需要保留聊天记录，传false
                EMClient.getInstance().chatManager().deleteConversation(list.get(i), false);
                updateUi();
                return true;
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUi();
    }

    /**
     * 更新会话信息
     */
    public void updateUi() {
        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
        list=new ArrayList<>(conversations.keySet());
        if(adapter==null){
            adapter=new MyAdapter();
            lv.setAdapter(adapter);
        }else{
            adapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            Holder holder;
            if(view ==null){
                view = LayoutInflater.from(getActivity()).inflate(R.layout.item_msg,viewGroup,false);
                holder = new Holder(view);
                view.setTag(holder);
            }else{
                holder = (Holder) view.getTag();
            }
            EMConversation conversation = EMClient.getInstance().chatManager().getConversation(list.get(i));
            EMMessage message = conversation.getLastMessage();
            String msg = "";
            if (message.getType().name().equals("TXT")) {
                EMTextMessageBody body = (EMTextMessageBody) message.getBody();
                msg = body.getMessage();

            }
            int count = conversation.getUnreadMsgCount();
            if(count == 0){
                holder.tvCount.setVisibility(View.GONE);
            }else{
                holder.tvCount.setVisibility(View.VISIBLE);
                holder.tvCount.setText(count+"");
            }
            holder.tvName.setText(list.get(i));
            holder.tvMsg.setText(msg);
            holder.tvTime.setText(T.getTime(message.getMsgTime()));


            return view;
        }
    }

    class Holder {
        TextView tvName,tvMsg,tvTime,tvCount;
        CircleImageView ivIcon;

        public Holder(View v){
            tvName = (TextView) v.findViewById(R.id.tvName);
            tvMsg = (TextView) v.findViewById(R.id.tvMsg);
            tvTime = (TextView) v.findViewById(R.id.tvTime);
            tvCount = (TextView) v.findViewById(R.id.tvCount);
            ivIcon = (CircleImageView) v.findViewById(R.id.ivIcon);
        }



    }


}
