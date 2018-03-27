package com.gengli.glservice.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

@ContentView(R.layout.activity_add_contacts)
public class AddContactsActivity extends BaseActivity {

    @ViewInject(R.id.add_contacts_name_edit)
    private EditText add_contacts_name_edit;

    @ViewInject(R.id.add_contacts_phone_edit)
    private EditText add_contacts_phone_edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Event(value = R.id.add_contacts_back_bt, type = View.OnClickListener.class)
    private void backClick(View view) {
        finish();
    }

    @Event(value = R.id.add_contacts_ok_bt)
    private void commitClick(View view) {
        addContacts();
    }

    private void addContacts() {
        String name = add_contacts_name_edit.getText().toString();
        String phone = add_contacts_phone_edit.getText().toString();
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone)) {
            LogUtils.showCenterToast(this, "姓名和电话请输入完整");
            return;
        }
        String url = ServicePort.CONTACT_ADD;
        XUtilHttp api = new XUtilHttp();
        Map<String, String> map = new HashMap<>();
        map.put("realname", name);
        map.put("mobile", phone);
        RequestParams params = api.getParam(this, url, map);
        params.addBodyParameter("realname", name);
        params.addBodyParameter("mobile", phone);
        api.post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String responce = result.toString();
                LogUtils.showLogD("----->返回数据----->" + responce);
                if (!TextUtils.isEmpty(responce)) {
                    try {
                        JSONObject jsonObject = new JSONObject(responce);
                        int err_no = jsonObject.getInt("err_no");
                        if (err_no == 0) {
                            handler.sendEmptyMessage(1);
                        } else {
                            LogUtils.showCenterToast(AddContactsActivity.this, jsonObject.getInt("err_no") + "" + jsonObject.getString("err_msg"));
                        }
                    } catch (JSONException e) {

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
                LogUtils.showCenterToast(AddContactsActivity.this, "添加成功");
                finish();
            }
        }
    };
}
