package com.gengli.glservice.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.gengli.glservice.R;
import com.gengli.glservice.adapter.AddImgListAdapter;
import com.gengli.glservice.http.ServicePort;
import com.gengli.glservice.http.XUtilHttp;
import com.gengli.glservice.util.CommandPhotoUtil;
import com.gengli.glservice.util.DatasUtil;
import com.gengli.glservice.util.LogUtils;
import com.gengli.glservice.util.PhotoSystemOrShoot;
import com.gengli.glservice.util.StringUtil;
import com.gengli.glservice.util.Util;
import com.gengli.glservice.view.FormGridView;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@ContentView(R.layout.activity_creat_form)
public class CreatFormActivity extends BaseActivity {


    @ViewInject(R.id.creat_form_id_edit)
    private EditText creat_form_id_edit;

    @ViewInject(R.id.creat_form_model_edit)
    private EditText creat_form_model_edit;

    @ViewInject(R.id.creat_form_time_edit)
    private EditText creat_form_time_edit;

    @ViewInject(R.id.creat_form_address_edit)
    private EditText creat_form_address_edit;

    @ViewInject(R.id.creat_form_name_edit)
    private EditText creat_form_name_edit;

    @ViewInject(R.id.creat_form_company_edit)
    private EditText creat_form_company_edit;

    @ViewInject(R.id.creat_form_phone_edit)
    private EditText creat_form_phone_edit;

    @ViewInject(R.id.creat_form_des_edit)
    private EditText creat_form_des_edit;


    @ViewInject(R.id.creat_form_pic_grid)
    private FormGridView creat_form_pic_grid;

    private AddImgListAdapter addImgListAdapter;
    private PhotoSystemOrShoot selectPhoto;
    private CommandPhotoUtil commandPhoto;

    String time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String realname = DatasUtil.getUserInfo(this, "realname");
        String mobile = DatasUtil.getUserInfo(this, "mobile");
        String unit = DatasUtil.getUserInfo(this, "unit");
        creat_form_name_edit.setText(realname);
        creat_form_phone_edit.setText(mobile);
        creat_form_company_edit.setText(unit);
        addForm();
        creat_form_id_edit.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = s.toString();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                if (s.length() > 4) {
                    String buyDate = "20" + StringUtil.subString(str, 1) + StringUtil.subString(str, 3) + "-" + StringUtil.subString(str, 2) + StringUtil.subString(str, 4);
//                    Date date = new Date(System.currentTimeMillis());
//                    String nowDate = simpleDateFormat.format(date);
//                    int diffStr = Util.getTimeDifference(buyDate + "-01 00:00:00", nowDate);
                    creat_form_time_edit.setText(buyDate);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    /**
     * 实例化组件
     */
    private void addForm() {
        addImgListAdapter = new AddImgListAdapter(this, 4);
        creat_form_pic_grid.setAdapter(addImgListAdapter);

        // 选择图片获取途径
        selectPhoto = new PhotoSystemOrShoot(this) {
            @Override
            public void onStartActivityForResult(Intent intent, int requestCode) {
                startActivityForResult(intent, requestCode);
            }
        };
        commandPhoto = new CommandPhotoUtil(this, creat_form_pic_grid, addImgListAdapter, selectPhoto);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (selectPhoto != null) {
            String photoPath = selectPhoto.getPhotoResultPath(requestCode, resultCode, data);
            LogUtils.showLogD("--->photo save path == " + photoPath);
            if (!TextUtils.isEmpty(photoPath)) {
                commandPhoto.showGridPhoto(photoPath);
            }
        }
        if (addImgListAdapter.getCount() == 0) {
            addImgListAdapter.setClearImgShow(false);
        }
    }

    private void commitRepair() {
        String url = ServicePort.REPAIR_ADD;
        XUtilHttp api = new XUtilHttp();
        String order_id = creat_form_id_edit.getText().toString();
        String product_model = creat_form_model_edit.getText().toString();
        String express = creat_form_address_edit.getText().toString();
        String des = creat_form_des_edit.getText().toString();
        String mobile = DatasUtil.getUserInfo(this, "mobile");
        String unit = DatasUtil.getUserInfo(this, "unit");
        LogUtils.showLogD("---"+unit);
        LogUtils.showLogD("---"+mobile);
        Map<String, String> map = new HashMap<>();
        map.put("order_id", order_id);
        map.put("product_model", product_model);
        map.put("express", express);
        map.put("des", des);
        map.put("unit", unit);
        map.put("tel", mobile);
        RequestParams params = api.getParam(this, url, map);
        params.addParameter("order_id", order_id);
        params.addParameter("product_model", product_model);
        params.addParameter("express", express);
        params.addParameter("des", des);
        params.addParameter("unit", unit);
        params.addParameter("tel", mobile);

        for (int i = 0; i < addImgListAdapter.imageItemData.size(); i++) {
            params.addParameter("image_" + i, new File(addImgListAdapter.imageItemData.get(i)));
        }
        if (TextUtils.isEmpty(order_id) || TextUtils.isEmpty(product_model)) {
            LogUtils.showCenterToast(this, "产品编号和型号请输入完整");
        } else {
            api.post(params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    String responce = result.toString();
                    LogUtils.showLogD("----->添加报修单返回数据----->" + responce);
                    if (!TextUtils.isEmpty(responce)) {
                        try {
                            JSONObject jsonObject = new JSONObject(responce);
                            int err_no = jsonObject.getInt("err_no");
                            if (err_no == 0) {
                                handle.sendEmptyMessage(1);
                            } else if (err_no == 2100) {
                                handle.sendEmptyMessage(2);
                                LogUtils.showCenterToast(CreatFormActivity.this, jsonObject.getInt("err_no") + "" + jsonObject.getString("err_msg"));
                            } else {
                                LogUtils.showCenterToast(CreatFormActivity.this, jsonObject.getInt("err_no") + "" + jsonObject.getString("err_msg"));
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
                LogUtils.showCenterToast(CreatFormActivity.this, "保修单已提交");
                finish();
            } else if (msg.what == 2){
                startActivity(new Intent(CreatFormActivity.this, LoginActivity.class));
            }
        }
    };

    @Event(value = R.id.creat_form_back_bt, type = View.OnClickListener.class)
    private void backClick(View view) {
        finish();
    }

    @Event(value = R.id.creat_form_commit_bt)
    private void commitClick(View view) {
        commitRepair();

    }
}
