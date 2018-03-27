package com.gengli.glservice.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.gengli.glservice.R;
import com.gengli.glservice.bean.Contacts;
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

@ContentView(R.layout.activity_contacts_info)
public class ContactsInfoActivity extends BaseActivity {

    @ViewInject(R.id.contacts_info_name_text)
    private TextView contacts_info_name_text;

    @ViewInject(R.id.contacts_info_phone_text)
    private TextView contacts_info_phone_text;

    @ViewInject(R.id.contacts_info_name_edit)
    private EditText contacts_info_name_edit;

    @ViewInject(R.id.contacts_info_phone_edit)
    private EditText contacts_info_phone_edit;

    private Contacts contacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contacts = (Contacts) getIntent().getSerializableExtra("cur_contacts");
        contacts_info_name_text.setText(contacts.getName());
        contacts_info_phone_text.setText(contacts.getPhone());
    }

    @Event(value = R.id.contacts_info_back_bt, type = View.OnClickListener.class)
    private void backClick(View view) {
        finish();
    }


    @Event(value = R.id.contacts_info_name_text)
    private void changeNameClick(View view) {
        contacts_info_name_text.setVisibility(View.INVISIBLE);
        contacts_info_name_edit.setVisibility(View.VISIBLE);
    }

    @Event(value = R.id.contacts_info_phone_text)
    private void changePhoneClick(View view) {
        contacts_info_phone_text.setVisibility(View.INVISIBLE);
        contacts_info_phone_edit.setVisibility(View.VISIBLE);
    }

    @Event(value = R.id.contacts_info_ok_bt)
    private void commitClick(View view) {
        String name = contacts_info_name_edit.getText().toString();
        String phone = contacts_info_phone_edit.getText().toString();
        if (TextUtils.isEmpty(name) && TextUtils.isEmpty(phone)) {
            LogUtils.showCenterToast(this, "请输入需要修改的内容");
            return;
        } else{
            modifyContacts();
        }

    }


    private void modifyContacts() {
        String name = contacts_info_name_edit.getText().toString();
        String phone = contacts_info_phone_edit.getText().toString();
//        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone)) {
//            LogUtils.showCenterToast(this, "姓名和电话请输入完整");
//            return;
//        }
        String url = ServicePort.CONTACT_MODIFY;
        XUtilHttp api = new XUtilHttp();
        Map<String, String> map = new HashMap<>();
        map.put("id", contacts.getId() + "");
        if (!TextUtils.isEmpty(name)) {
            map.put("realname", name);
        }
        if (!TextUtils.isEmpty(phone)) {
            map.put("mobile", phone);
        }
        RequestParams params = api.getParam(this, url, map);
        params.addBodyParameter("id", contacts.getId() + "");
        if (!TextUtils.isEmpty(name)) {
            params.addBodyParameter("realname", name);
        }
        if (!TextUtils.isEmpty(phone)) {
            params.addBodyParameter("mobile", phone);
        }
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
                            LogUtils.showCenterToast(ContactsInfoActivity.this, jsonObject.getInt("err_no") + "" + jsonObject.getString("err_msg"));
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
                LogUtils.showCenterToast(ContactsInfoActivity.this, "修改成功");
                finish();
            }
        }
    };


}
