package com.gengli.glservice.activity;

import android.content.Intent;
import android.content.SharedPreferences;
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

@ContentView(R.layout.activity_login_phone)
public class LoginPhoneActivity extends BaseActivity {

    @ViewInject(R.id.login_phone_edit)
    private EditText login_phone_edit;

//    @ViewInject(R.id.login_phone_verify_edit)
//    private EditText login_phone_verify_edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Event(value = R.id.login_phone_close_bt, type = View.OnClickListener.class)
    private void closeClick(View view) {
        finish();
    }

    @Event(value = R.id.login_phone_phone_bt)
    private void phoneClick(View view) {
        login();
    }

    @Event(value = R.id.login_phone_company_bt)
    private void companyClick(View view) {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }


    public void login() {
        String url = ServicePort.ACCOUNT_LOGIN_MOBILE;
        XUtilHttp api = new XUtilHttp();
        final String phone = login_phone_edit.getText().toString();
        String verify = "471003";
        Map<String, String> map = new HashMap<>();
        map.put("mobile", phone);
        map.put("verify", verify);
        RequestParams params = api.getParam(this, url, map);
        params.addParameter("mobile", phone);
        params.addParameter("verify", verify);
        if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(verify)) {
            LogUtils.showCenterToast(this, "请输入手机号码和验证码");
        } else {
            api.post(params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    String responce = result.toString();
                    LogUtils.showLogD("----->登录返回数据----->" + responce);
                    if (!TextUtils.isEmpty(responce)) {
                        try {
                            JSONObject jsonObject = new JSONObject(responce);
                            int err_no = jsonObject.getInt("err_no");
                            if (err_no == 0) {
                                JSONObject results = jsonObject.getJSONObject("results");
                                if (!TextUtils.isEmpty(results.toString())) {
                                    LogUtils.showLogD("----->login data----->" + results.toString());
                                    handle.sendEmptyMessage(1);
                                    SharedPreferences sharedPreferences = getSharedPreferences("GLUser", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("sessionid", results.getString("sessionid"));
                                    editor.putString("company_id", results.getString("company_id"));
                                    editor.putString("mobile", results.getString("mobile"));
                                    editor.putString("username", results.getString("username"));
                                    editor.putString("realname", results.getString("realname"));
                                    editor.putInt("gender", results.getInt("gender"));
                                    editor.putString("prov", results.getString("prov"));
                                    editor.putString("prov_name", results.getString("prov_name"));
                                    editor.putString("city", results.getString("city"));
                                    editor.putString("city_name", results.getString("city_name"));
                                    editor.putString("unit", results.getString("unit"));
                                    editor.putString("address", results.getString("address"));
                                    editor.putString("avatar", results.getString("avatar"));

                                    editor.putBoolean("LoginState", true);//登录状态
                                    editor.commit();
                                }
                            } else {
                                LogUtils.showCenterToast(LoginPhoneActivity.this, jsonObject.getInt("err_no") + "" + jsonObject.getString("err_msg"));
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
                Intent intent = new Intent(LoginPhoneActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
    };
}
