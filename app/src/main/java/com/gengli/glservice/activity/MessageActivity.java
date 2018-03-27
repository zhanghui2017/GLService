package com.gengli.glservice.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.gengli.glservice.R;
import com.gengli.glservice.adapter.MessageAdapter;
import com.gengli.glservice.bean.Message;
import com.gengli.glservice.http.ServicePort;
import com.gengli.glservice.http.XUtilHttp;
import com.gengli.glservice.util.LogUtils;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageActivity extends BaseActivity implements View.OnClickListener {
    private ImageView message_back_img;
    private PullToRefreshListView message_list_view;
    private MessageAdapter adapter;
    private List<Message> messageList;

    private LinearLayout order_no_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        messageList = new ArrayList<>();
        getMmessage();
        initView();
    }

    private void initView() {
        message_back_img = (ImageView) findViewById(R.id.message_back_img);
        message_back_img.setOnClickListener(this);
        message_list_view = (PullToRefreshListView) findViewById(R.id.message_list_view);
        adapter = new MessageAdapter(this, messageList);
        message_list_view.setAdapter(adapter);
        order_no_data = (LinearLayout) findViewById(R.id.order_no_data);
        message_list_view.setOnItemClickListener(new MessageItemClick());
        message_list_view.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                getMmessage();
            }
        });
    }

    public class MessageItemClick implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Message message = messageList.get(position);
//            if (message.getType() == 1) {
//
//            } else if (message.getType() == 2) {
//
//            } else if (message.getType() == 3) {
//
//            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.message_back_img:
                finish();
                break;
        }
    }


    private void getMmessage() {
        String url = ServicePort.MSG_LISTS;
        XUtilHttp api = new XUtilHttp();
        Map<String, String> map = new HashMap<>();
        RequestParams params = api.getParam(this, url, map);
        api.post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String responce = result.toString();
                LogUtils.showLogD("----->消息列表返回数据----->" + responce);
                if (!TextUtils.isEmpty(responce)) {
                    try {
                        JSONObject jsonObject = new JSONObject(responce);
                        int err_no = jsonObject.getInt("err_no");
                        if (err_no == 0) {
                            if (messageList.size() > 0) {
                                messageList.clear();
                            }
                            JSONObject results = jsonObject.getJSONObject("results");
                            JSONArray list = results.getJSONArray("list");
                            if (list != null && list.length() > 0) {
                                for (int i = 0; i < list.length(); i++) {
                                    JSONObject item = list.getJSONObject(i);
                                    Message message = new Message();
                                    message.setId(item.getString("id"));
                                    message.setImgUrl(item.getString("thumb"));
                                    message.setContent(item.getString("content"));
                                    message.setTitle(item.getString("title"));
                                    message.setTime(item.getString("create_time"));
                                    messageList.add(message);
                                }
                                message_list_view.onRefreshComplete();
                                adapter.notifyDataSetChanged();
                            } else {
                                handler.sendEmptyMessage(1);
                            }

                        } else if (err_no == 2100) {
                            handler.sendEmptyMessage(3);
                            LogUtils.showCenterToast(MessageActivity.this, jsonObject.getInt("err_no") + "" + jsonObject.getString("err_msg"));
                        } else {
                            LogUtils.showCenterToast(MessageActivity.this, jsonObject.getInt("err_no") + "" + jsonObject.getString("err_msg"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
            }
        });
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 3:
                    startActivity(new Intent(MessageActivity.this, LoginActivity.class));
                    finish();
                    break;
                case 1:
                    order_no_data.setVisibility(View.VISIBLE);
                    message_list_view.setVisibility(View.INVISIBLE);
                    break;
            }
        }
    };

}
