

package com.gengli.glservice.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.gengli.glservice.R;
import com.gengli.glservice.http.ServicePort;
import com.gengli.glservice.http.XUtilHttp;
import com.gengli.glservice.util.LogUtils;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.HashMap;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;

@ContentView(R.layout.activity_login)
public class LoginActivity extends BaseActivity {

    @ViewInject(R.id.login_company_edit)
    private EditText login_company_edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Event(value = R.id.login_close_bt, type = View.OnClickListener.class)
    private void closeClick(View view) {
        finish();
    }

    @Event(value = R.id.login_company_bt)
    private void companyClick(View view) {
        login();
    }


//    @Event(value = R.id.login_phone_bt)
//    private void phoneClick(View view) {
//        startActivity(new Intent(this, LoginPhoneActivity.class));
//        finish();
//    }


    private Handler handle = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                intent.putExtra("from_login", 0);
                startActivity(intent);
                finish();
            }
        }
    };


    public void login() {
        String url = ServicePort.ACCOUNT_LOGIN_COMPANY;
        XUtilHttp api = new XUtilHttp();
        final String company_id = login_company_edit.getText().toString();
        Map<String, String> map = new HashMap<>();
        map.put("company_id", company_id);
        RequestParams params = api.getParam(this, url, map);
        params.addParameter("company_id", company_id);
        if (TextUtils.isEmpty(company_id)) {
            LogUtils.showCenterToast(this, "请输入企业号");
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
                                    editor.putString("tel", results.getString("tel"));
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
                                    editor.putString("company_id", company_id);
                                    JPushInterface.setAlias(LoginActivity.this, 1, company_id);
                                    editor.commit();
                                }
                            } else {
                                LogUtils.showCenterToast(LoginActivity.this, jsonObject.getInt("err_no") + "" + jsonObject.getString("err_msg"));
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
}
