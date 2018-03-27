package com.gengli.glservice.activity;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.gengli.glservice.R;
import com.gengli.glservice.http.ServicePort;
import com.gengli.glservice.http.XUtilHttp;
import com.gengli.glservice.util.DatasUtil;
import com.gengli.glservice.util.FileUtils;
import com.gengli.glservice.util.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.HashMap;
import java.util.Map;

@ContentView(R.layout.activity_change_info)
public class ChangeInfoActivity extends BaseActivity {

    @ViewInject(R.id.change_info_content_edit)
    private EditText change_info_content_edit;

    private String change_info;
    private int change_type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        change_info = getIntent().getStringExtra("change_info");
        change_type = getIntent().getIntExtra("change_type", 0);
        change_info_content_edit.setHint(change_info);
    }

    @Event(value = R.id.change_info_back_bt, type = View.OnClickListener.class)
    private void backClick(View view) {
        finish();
    }

    @Event(value = R.id.change_info_commit_bt)
    private void commitClick(View view) {
        String editStr = change_info_content_edit.getText().toString();
        if (TextUtils.isEmpty(editStr)){
            return;
        }
        if (change_type == 1) {
            changeInfo("address", editStr);
        } else if (change_type == 2) {
            changeInfo("tel", editStr);
        }
    }


    private void changeInfo(String key, String value) {
        String url = ServicePort.ACCOUNT_MODIFY;
        XUtilHttp api = new XUtilHttp();
        Map<String, String> map = new HashMap<>();
        map.put(key, value);
        RequestParams params = api.getParam(this, url, map);
        params.addBodyParameter(key, value);
        api.post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String responce = result.toString();
                LogUtils.showLogD("修改信息返回 === " + responce);
                if (!TextUtils.isEmpty(responce)) {
                    try {
                        JSONObject jsonObject = new JSONObject(responce);
                        int err_no = jsonObject.getInt("err_no");
                        if (err_no == 0) {
                            JSONObject results = jsonObject.getJSONObject("results");
                            if (!TextUtils.isEmpty(results.toString())) {
                                SharedPreferences sharedPreferences = getSharedPreferences("GLUser", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("avatar", results.getString("avatar"));
                                editor.commit();
                                handler.sendEmptyMessage(1);
                            }

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
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                finish();
            }
        }
    };

}
