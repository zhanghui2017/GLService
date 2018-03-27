package com.gengli.glservice.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.gengli.glservice.R;
import com.gengli.glservice.http.ServicePort;
import com.gengli.glservice.http.XUtilHttp;
import com.gengli.glservice.util.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.HashMap;
import java.util.Map;

@ContentView(R.layout.activity_feedback)
public class FeedbackActivity extends BaseActivity {

    @ViewInject(R.id.feed_phone_edit)
    private EditText feed_phone_edit;

    @ViewInject(R.id.feed_content_edit)
    private EditText feed_content_edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Event(value = R.id.feed_commit_bt, type = View.OnClickListener.class)
    private void commitClick(View view) {
        feedBack();
    }

    @Event(value = R.id.feedback_back_bt)
    private void backClick(View view) {
        finish();
    }


    private void feedBack() {
        String url = ServicePort.FEEDBACK_ADD;
        XUtilHttp api = new XUtilHttp();
        String phone = feed_phone_edit.getText().toString();
        String content = feed_content_edit.getText().toString();
        Map<String, String> map = new HashMap<>();
        map.put("content", content);
        map.put("mobile", phone);
        RequestParams params = api.getParam(this, url, map);
        params.addParameter("content", content);
        params.addParameter("mobile", phone);
        if (TextUtils.isEmpty(content) || TextUtils.isEmpty(phone)) {
            LogUtils.showCenterToast(this, "请输入完整信息");
        } else {
            api.post(params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    String responce = result.toString();
                    LogUtils.showLogD("----->意见反馈返回数据----->" + responce);
                    if (!TextUtils.isEmpty(responce)) {
                        try {
                            JSONObject jsonObject = new JSONObject(responce);
                            int err_no = jsonObject.getInt("err_no");
                            if (err_no == 0) {
                                handle.sendEmptyMessage(1);
                            } else {
                                LogUtils.showCenterToast(FeedbackActivity.this, jsonObject.getInt("err_no") + "" + jsonObject.getString("err_msg"));
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
    }
    private Handler handle = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                LogUtils.showCenterToast(FeedbackActivity.this, "提交成功");
                finish();
            }
        }
    };

}
