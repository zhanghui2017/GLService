package com.gengli.glservice.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import com.gengli.glservice.R;
import com.gengli.glservice.adapter.GridImgAdapter;
import com.gengli.glservice.bean.Order;
import com.gengli.glservice.http.ServicePort;
import com.gengli.glservice.http.XUtilHttp;
import com.gengli.glservice.util.DatasUtil;
import com.gengli.glservice.util.LogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ContentView(R.layout.activity_order_ing)
public class OrderINGActivity extends BaseActivity {
    private Order order;

    @ViewInject(R.id.order_ing_repair_id)
    private TextView order_ing_repair_id;

    @ViewInject(R.id.order_ing_repair_start_time)
    private TextView order_ing_repair_start_time;

    @ViewInject(R.id.order_ing_repair_name)
    private TextView order_ing_repair_name;

    @ViewInject(R.id.order_ing_repair_unit)
    private TextView order_ing_repair_unit;

    @ViewInject(R.id.order_ing_repair_phone)
    private TextView order_ing_repair_phone;

    @ViewInject(R.id.order_ing_repair_model)
    private TextView order_ing_repair_model;

    @ViewInject(R.id.order_ing_repair_pro_id)
    private TextView order_ing_repair_pro_id;

    @ViewInject(R.id.order_ing_repair_time)
    private TextView order_ing_repair_time;

    @ViewInject(R.id.order_ing_repair_address)
    private TextView order_ing_repair_address;

    @ViewInject(R.id.order_ing_repair_des)
    private TextView order_ing_repair_des;

    @ViewInject(R.id.order_ing_repair_charge_name)
    private TextView order_ing_repair_charge_name;

    @ViewInject(R.id.order_ing_repair_charge_phone)
    private TextView order_ing_repair_charge_phone;

    @ViewInject(R.id.order_ing_pic_grid)
    private GridView order_ing_pic_grid;
    private List<String> imgList;
    private GridImgAdapter gridImgAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        order = (Order) getIntent().getSerializableExtra("ING_ORDER");
        imgList = new ArrayList<>();
        getRepairDetail();
        gridImgAdapter = new GridImgAdapter(this, imgList);
        order_ing_pic_grid.setAdapter(gridImgAdapter);

    }

    @Event(value = R.id.order_ing_back_bt, type = View.OnClickListener.class)
    private void backClick(View view) {
        finish();
    }



    @Event(value = R.id.order_ing_call_bt)
    private void callClick(View view) {
        callPhone("037969060016");
    }

    private void getRepairDetail() {
        String url = ServicePort.REPAIR_DETAIL;
        XUtilHttp api = new XUtilHttp();
        Map<String, String> map = new HashMap<>();
        map.put("rid", order.getId());
        RequestParams params = api.getParam(this, url, map);
        params.addBodyParameter("rid", order.getId());
        api.post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String responce = result.toString();
                LogUtils.showLogD("----->维修详情返回数据----->" + responce);
                if (!TextUtils.isEmpty(responce)) {
                    try {
                        JSONObject jsonObject = new JSONObject(responce);
                        int err_no = jsonObject.getInt("err_no");
                        if (err_no == 0) {
                            JSONObject results = jsonObject.getJSONObject("results");
                            if (results.length() > 0) {

                                order.setId(results.getString("rid"));
                                order.setModel(results.getString("product_id"));
                                order.setMachine(results.getString("product_model"));
                                order.setName(results.getString("realname"));
                                order.setPhone(results.getString("tel"));
                                order.setCompany(results.getString("unit"));
                                order.setAddress(results.getString("address"));
                                order.setChargeName(results.getString("handle_name"));
                                order.setTime(results.getString("create_time"));
                                order.setLevel(results.getString("category_name"));
                                order.setDesc(results.getString("des"));
                                order.setExpressAddress(results.getString("express"));
                                order.setChargeName(results.getString("handle_name"));
                                order.setChargePhone(results.getString("handle_phone"));
                                buy_period = results.getString("buy_period");
                                order_id = results.getString("order_id");
                                handle.sendEmptyMessage(1);

                                JSONObject details = results.getJSONObject("details");
                                if (details.length() > 0) {
                                    JSONArray client_images = details.getJSONArray("client_images");
                                    if (client_images.length() > 0) {
                                        for (int i = 0; i < client_images.length(); i++) {
                                            JSONObject item = client_images.getJSONObject(i);
                                            imgList.add(item.getString("img"));
                                        }
                                    }
                                }

                            }

                        } else {
                            LogUtils.showCenterToast(OrderINGActivity.this, jsonObject.getInt("err_no") + "" + jsonObject.getString("err_msg"));
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

    private String buy_period;
    private String order_id;
    private Handler handle = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                String realname = DatasUtil.getUserInfo(OrderINGActivity.this, "realname");
                order_ing_repair_id.setText(order.getId());
                order_ing_repair_start_time.setText(order.getTime());
                order_ing_repair_name.setText(realname);
                order_ing_repair_unit.setText(order.getCompany());
                order_ing_repair_phone.setText(order.getPhone());
                order_ing_repair_model.setText(order.getMachine());
                order_ing_repair_pro_id.setText(buy_period);
                order_ing_repair_time.setText(order_id);
                order_ing_repair_address.setText(order.getExpressAddress());
                order_ing_repair_des.setText(order.getDesc());
                order_ing_repair_charge_name.setText(order.getChargeName());
                order_ing_repair_charge_phone.setText(order.getChargePhone());

                gridImgAdapter.notifyDataSetChanged();
            }
        }
    };


    public void callPhone(String phoneNum) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        startActivity(intent);
    }
}
