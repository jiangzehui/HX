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
import android.widget.ListView;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;

import java.util.ArrayList;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.jiangzehui.hx.ChatActivity;
import cn.jiangzehui.hx.R;
import cn.jiangzehui.hx.util.T;

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
        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
        list=new ArrayList<>(conversations.keySet());
        if(list!=null&&list.size()>0){
            Log.i("conversations",conversations.toString());
            Log.i("conversations",list.size()+"");
            Log.i("conversations",list.get(0));
            if(adapter==null){
                adapter=new MyAdapter();
                lv.setAdapter(adapter);
            }else{
                adapter.notifyDataSetChanged();
            }
        }
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                T.open(getActivity(), ChatActivity.class, "username", list.get(i));

            }
        });

        return view;
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
            TextView tv=new TextView(getActivity());
            tv.setPadding(20,20,20,20);
            tv.setTextSize(20);
            tv.setText(list.get(i));
            return tv;
        }
    }


}
