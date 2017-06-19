package cn.jiangzehui.hx.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.jiangzehui.hx.AddFriendActivity;
import cn.jiangzehui.hx.ChatActivity;
import cn.jiangzehui.hx.LoginActivity;
import cn.jiangzehui.hx.R;
import cn.jiangzehui.hx.util.T;
import cn.jiangzehui.hx.view.CircleImageView;

/**
 * Created by jiangzehui on 17/1/18.
 */
public class FriendFragment extends Fragment {
    View view;
    @InjectView(R.id.lv)
    ListView lv;
    List<String> usernames;
    MyAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (view == null) {
            view = inflater.inflate(R.layout.fragment_friend, container, false);
        }

        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    usernames = EMClient.getInstance().contactManager().getAllContactsFromServer();
                    if (usernames.size() != 0) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(adapter==null){
                                    adapter = new MyAdapter();
                                    lv.setAdapter(adapter);
                                }else{
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        });
                    }
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            T.open(getActivity(), ChatActivity.class, "username", usernames.get(i));
                        }
                    });


                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }


    class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return usernames.size();
        }

        @Override
        public Object getItem(int i) {
            return usernames.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            Holder holder;
            if(view ==null){
                view = LayoutInflater.from(getActivity()).inflate(R.layout.item_friend,viewGroup,false);
                holder = new Holder(view);
                view.setTag(holder);
            }else{
                holder = (Holder) view.getTag();
            }
            holder.tvName.setText(usernames.get(i));
            return view;
        }
    }

    class Holder {
        TextView tvName;
        CircleImageView ivIcon;

        public Holder(View v){
            tvName = (TextView) v.findViewById(R.id.tvName);
            ivIcon = (CircleImageView) v.findViewById(R.id.ivIcon);
        }



    }


}
