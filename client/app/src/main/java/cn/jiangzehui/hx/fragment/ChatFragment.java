package cn.jiangzehui.hx.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.jiangzehui.hx.R;

/**
 * Created by jiangzehui on 17/1/18.
 */
public class ChatFragment extends Fragment {
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if(view==null){
            view =inflater.inflate(R.layout.fragment_chat,container,false);
        }

        return view;
    }
}
