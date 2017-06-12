package cn.jiangzehui.hx.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.jiangzehui.hx.R;
import cn.jiangzehui.hx.util.T;

/**
 * Created by jiangzehui on 17/6/12.
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
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @OnClick(R.id.tv)
    public void onClick() {
        final EditText et = new EditText(getActivity());
        new AlertDialog.Builder(getActivity()).setTitle("创建聊天室").setView(et).setPositiveButton("创建", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final String name = et.getText().toString();
                if (name.equals("")) {
                    T.show("聊天室名字不能为空");
                } else {

                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            try {
                                EMClient.getInstance().chatroomManager().createChatRoom(name, name, "欢迎加入01聊天室", 100, null);
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        T.show("聊天室创建成功");
                                    }
                                });
                            } catch (final HyphenateException e) {
                                e.printStackTrace();
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        T.show("创建失败，"+e.getMessage());
                                    }
                                });
                            }

                        }
                    }.start();

                }
            }
        }).setNegativeButton("取消", null).show();
    }
}
