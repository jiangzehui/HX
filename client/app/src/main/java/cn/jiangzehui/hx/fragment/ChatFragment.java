package cn.jiangzehui.hx.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;

import java.util.ArrayList;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.jiangzehui.hx.R;

/**
 * Created by jiangzehui on 17/1/18.
 */
public class ChatFragment extends Fragment {
    View view;
    @InjectView(R.id.lv)
    ListView lv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (view == null) {
            view = inflater.inflate(R.layout.fragment_chat, container, false);
        }

        ButterKnife.inject(this, view);
        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
        ArrayList<String> list=new ArrayList<>(conversations.keySet());
        if(list!=null&&list.size()>0){
            Log.i("conversations",conversations.toString());
            Log.i("conversations",list.size()+"");
            Log.i("conversations",list.get(0));
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
