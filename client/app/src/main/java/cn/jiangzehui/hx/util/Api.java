package cn.jiangzehui.hx.util;

import android.content.Context;


import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;



import cz.msebera.android.httpclient.Header;

/**
 * Created by quxianglin on 16/6/2.
 */
public class Api {

    public static AsyncHttpClient client = new AsyncHttpClient();
    public static RequestHandle handle;


    public static void get(final Context context, String url, final CallBackJSON back) {

        client.setConnectTimeout(5000);

        handle = client.get(context, url, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {


                try {
                    back.success(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                back.faile();
            }
        });


    }

    public static void post(final Context context, String url, RequestParams params, final CallBackJSON back) {

        client.setConnectTimeout(5000);

        handle = client.post(context, url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    back.success(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                back.faile();
            }
        });


    }

    public static void cancel() {
        if (handle != null) {
            handle.cancel(true);
        }
    }

    public static void cancelAll() {
        if (client != null) {
            client.cancelAllRequests(true);
        }
    }


    public interface CallBackJSON {
        void success(JSONObject object) throws JSONException;

        void faile();
    }


}
