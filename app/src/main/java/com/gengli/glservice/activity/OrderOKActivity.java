package com.gengli.glservice.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.gengli.glservice.R;
import com.gengli.glservice.adapter.FittingInfoAdapter;
import com.gengli.glservice.adapter.GridImgAdapter;
import com.gengli.glservice.bean.Fitting;
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
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ContentView(R.layout.activity_order_ok)
public class OrderOKActivity extends BaseActivity {
    private Order order;

    @ViewInject(R.id.order_ok_repair_parts_view)
    LinearLayout order_ok_repair_parts_view;

    @ViewInject(R.id.order_ok_repair_id)
    private TextView order_ok_repair_id;

    @ViewInject(R.id.order_ok_repair_start_time)
    private TextView order_ok_repair_start_time;

    @ViewInject(R.id.order_ok_repair_need_fit)
    private TextView order_ok_repair_need_fit;

    @ViewInject(R.id.order_ok_repair_name)
    private TextView order_ok_repair_name;

    @ViewInject(R.id.order_ok_repair_unit)
    private TextView order_ok_repair_unit;

    @ViewInject(R.id.order_ok_repair_phone)
    private TextView order_ok_repair_phone;

    @ViewInject(R.id.order_ok_repair_model)
    private TextView order_ok_repair_model;

    @ViewInject(R.id.order_ok_repair_pro_id)
    private TextView order_ok_repair_pro_id;

    @ViewInject(R.id.order_ok_repair_time)
    private TextView order_ok_repair_time;

    @ViewInject(R.id.order_ok_repair_address)
    private TextView order_ok_repair_address;

    @ViewInject(R.id.order_ok_repair_des)
    private TextView order_ok_repair_des;

    @ViewInject(R.id.order_ok_repair_charge_name)
    private TextView order_ok_repair_charge_name;

    @ViewInject(R.id.order_ok_repair_charge_phone)
    private TextView order_ok_repair_charge_phone;

    @ViewInject(R.id.order_ok_repair_fit_list)
    private ListView order_ok_repair_fit_list;

    @ViewInject(R.id.order_ok_repair_end_time)
    private TextView order_ok_repair_end_time;

    @ViewInject(R.id.order_ok_repair_begin_img)
    private ImageView order_ok_repair_begin_img;

    @ViewInject(R.id.order_ok_repair_after_img)
    private ImageView order_ok_repair_after_img;

    @ViewInject(R.id.order_ok_repair_total_price)
    private TextView order_ok_repair_total_price;


    @ViewInject(R.id.order_ok_pic_grid)
    private GridView order_ok_pic_grid;
    private List<String> kehuImgList;
    private List<String> beginImgList;
    private List<String> afterImgList;
    private GridImgAdapter gridImgAdapter;

    private List<Fitting> fittingList;
    private FittingInfoAdapter fittingInfoAdapter;
    private int totalPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        order = (Order) getIntent().getSerializableExtra("OK_ORDER");
        kehuImgList = new ArrayList<>();
        beginImgList = new ArrayList<>();
        afterImgList = new ArrayList<>();
        fittingList = new ArrayList<>();
        getRepairDetail();
        gridImgAdapter = new GridImgAdapter(this, kehuImgList);
        order_ok_pic_grid.setAdapter(gridImgAdapter);
        fittingInfoAdapter = new FittingInfoAdapter(this, fittingList);
        order_ok_repair_fit_list.setAdapter(fittingInfoAdapter);

    }

    @Event(value = R.id.order_ok_back_bt, type = View.OnClickListener.class)
    private void backClick(View view) {
        finish();
    }

    @Event(value = R.id.order_ok_call_bt)
    private void callClick(View view) {
        callPhone("037969060016");
    }

    public void callPhone(String phoneNum) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        startActivity(intent);
    }

    @Event(value = R.id.order_ok_repair_rate_bt)
    private void rateClick(View view) {
        Intent intent = new Intent(this, OrderRateActivity.class);
        intent.putExtra("order_rid", order.getId());
        intent.putExtra("order_name", order.getName());
        intent.putExtra("order_charge_name", order.getChargeName());
        intent.putExtra("order_handle_avatar", handle_avatar);
        startActivity(intent);
    }

    private int partsSize;
    private String handle_avatar;

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
                                handle_avatar = results.getString("handle_avatar");
                                order.setEndTime(results.getString("handle_end"));
                                buy_period = results.getString("buy_period");
                                order_id = results.getString("order_id");

                                JSONObject details = results.getJSONObject("details");
                                if (details.length() > 0) {
                                    JSONArray client_images = details.getJSONArray("client_images");
                                    JSONArray worker_images = details.getJSONArray("worker_images");
                                    if (client_images.length() > 0) {
                                        for (int i = 0; i < client_images.length(); i++) {
                                            JSONObject item = client_images.getJSONObject(i);
                                            kehuImgList.add(item.getString("img"));
                                        }
                                        gridImgAdapter.notifyDataSetChanged();
                                    }

                                    if (worker_images.length() > 0) {
                                        for (int i = 0; i < worker_images.length(); i++) {
                                            JSONObject item = worker_images.getJSONObject(i);
                                            beginImgList.add(item.getString("img"));
                                            afterImgList.add(item.getString("img_after"));
                                        }
                                    }

                                    JSONArray parts = results.getJSONArray("parts");
                                    if (parts.length() > 0) {
                                        for (int j = 0; j < parts.length(); j++) {
                                            JSONObject item = parts.getJSONObject(j);
                                            Fitting f = new Fitting();
                                            f.setId(item.getInt("id"));
                                            f.setTitle(item.getString("name"));
                                            f.setChooseCount(item.getInt("amount"));
                                            f.setPrice(String.valueOf(item.getInt("price")));
                                            f.setImgUrl(item.getString("thumb"));
                                            fittingList.add(f);
                                            totalPrice += item.getInt("price");
                                        }
                                        partsSize = parts.length();
                                        fittingInfoAdapter.notifyDataSetChanged();
                                        handle.sendEmptyMessage(3);
                                    } else {
                                        handle.sendEmptyMessage(2);
                                    }
                                }
                                handle.sendEmptyMessage(1);

                            }

                        } else {
                            LogUtils.showCenterToast(OrderOKActivity.this, jsonObject.getInt("err_no") + "" + jsonObject.getString("err_msg"));
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
                String realname = DatasUtil.getUserInfo(OrderOKActivity.this, "realname");
                order_ok_repair_name.setText(realname);
                order_ok_repair_id.setText(order.getId());
                order_ok_repair_start_time.setText(order.getTime());
                order_ok_repair_unit.setText(order.getCompany());
                order_ok_repair_phone.setText(order.getPhone());
                order_ok_repair_model.setText(order.getMachine());
                order_ok_repair_pro_id.setText(order_id);
                order_ok_repair_time.setText(buy_period);
                order_ok_repair_address.setText(order.getExpressAddress());
                order_ok_repair_des.setText(order.getDesc());
                order_ok_repair_charge_name.setText(order.getChargeName());
                order_ok_repair_charge_phone.setText(order.getChargePhone());
                order_ok_repair_end_time.setText(order.getEndTime());

                ImageOptions imageOptions = new ImageOptions.Builder()
                        .setLoadingDrawableId(R.mipmap.ic_launcher)
                        .setFailureDrawableId(R.mipmap.ic_launcher)
                        .build();
                if (beginImgList.size() > 0) {
                    x.image().bind(order_ok_repair_begin_img, beginImgList.get(0), imageOptions);
                }
                if (afterImgList.size() > 0) {
                    x.image().bind(order_ok_repair_after_img, afterImgList.get(0), imageOptions);
                }
            } else if (msg.what == 2) {
                order_ok_repair_parts_view.setVisibility(View.GONE);
                order_ok_repair_need_fit.setText("无需更换配件");
//                order_ok_repair_total_price.setText("合计：" + 0 + "");
            } else if (msg.what == 3) {
                order_ok_repair_parts_view.setVisibility(View.VISIBLE);
                order_ok_repair_need_fit.setText("需要更换" + partsSize + "个配件，用户已同意更换");
                order_ok_repair_total_price.setText("合计：" + totalPrice + "");

            }
        }
    };


}
