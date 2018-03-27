package com.gengli.glservice.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.gengli.glservice.R;
import com.gengli.glservice.adapter.RepairAdapter;
import com.gengli.glservice.bean.Order;
import com.gengli.glservice.http.ServicePort;
import com.gengli.glservice.http.XUtilHttp;
import com.gengli.glservice.util.LogUtils;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

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

@ContentView(R.layout.activity_repairs)
public class RepairsActivity extends BaseActivity {

    @ViewInject(R.id.repairs_list_view)
    private PullToRefreshListView repairs_list_view;

    @ViewInject(R.id.order_no_data)
    private LinearLayout order_no_data;

    private RepairAdapter adapter;
    private List<Order> orderList;
    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getOrderData(1);
        init();

    }

    private void init() {
        orderList = new ArrayList<>();
        adapter = new RepairAdapter(this, orderList);
        repairs_list_view.setAdapter(adapter);
        repairs_list_view.setMode(PullToRefreshBase.Mode.BOTH);
        repairs_list_view.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                getOrderData(1);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                getOrderData(page++);
            }
        });
    }

    @Event(value = R.id.repairs_back_bt, type = View.OnClickListener.class)
    private void backClick(View view) {
        finish();
    }


    @Event(value = R.id.repairs_all_bt)
    private void allClick(View view) {
        startActivity(new Intent(this, AllOrderActivity.class));
    }

    @Event(value = R.id.repairs_list_view, type = AdapterView.OnItemClickListener.class)
    private void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Order order = orderList.get(position - 1);
        Intent intent = new Intent(this, OrderINGActivity.class);
        intent.putExtra("ING_ORDER", order);
        startActivity(intent);
    }

    private void getOrderData(final int page) {
        final List<Order> listTemp = new ArrayList<>();
        String url = ServicePort.REPAIR_LISTS;
        XUtilHttp api = new XUtilHttp();
        Map<String, String> map = new HashMap<>();
        map.put("status", 3 + "");
        map.put("page", page + "");
        RequestParams params = api.getParam(this, url, map);
        params.addBodyParameter("status", 3 + "");
        params.addBodyParameter("page", page + "");
        api.post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtils.showLogD("--------未完成维修单返回数据:" + result.toString());
                String responce = result.toString();
                if (!TextUtils.isEmpty(responce)) {
                    try {
                        JSONObject jsonObject = new JSONObject(responce);
                        int err_no = jsonObject.getInt("err_no");
                        if (err_no == 0) {
                            JSONObject results = jsonObject.getJSONObject("results");
                            if (!TextUtils.isEmpty(results.toString())) {
                                JSONArray lists = results.getJSONArray("lists");
                                LogUtils.showLogD("--------未完成维修单返回数据:" + lists.toString());
                                if (lists.length() > 0) {
                                    for (int i = 0; i < lists.length(); i++) {
                                        JSONObject item = lists.getJSONObject(i);
                                        Order order = new Order();
                                        order.setId(item.getString("rid"));
                                        order.setModel(item.getString("product_id"));
                                        order.setMachine(item.getString("product_model"));
                                        order.setName(item.getString("realname"));
                                        order.setPhone(item.getString("tel"));
                                        order.setCompany(item.getString("unit"));
                                        order.setAddress(item.getString("address"));
                                        order.setChargeName(item.getString("handle_name"));
                                        order.setChargePhone(item.getString("handle_phone"));
                                        order.setTime(item.getString("create_time"));
                                        order.setType(item.getInt("status"));
                                        order.setLevel(item.getString("category_name"));
                                        order.setDesc(item.getString("des"));
                                        order.setExpressAddress(item.getString("express"));
                                        order.setIs_comment(item.getBoolean("is_comment"));
                                        listTemp.add(order);
                                    }
                                    if (page == 1) {
                                        orderList.clear();
                                        if (listTemp.size() == 0) {
                                            LogUtils.showCenterToast(RepairsActivity.this, "没有数据");
                                        } else if (listTemp.size() > 0) {
                                            orderList.addAll(listTemp);
                                            adapter.notifyDataSetChanged();
                                        }

                                    } else if (page > 1) {
                                        if (listTemp.size() == 0) {
                                            LogUtils.showCenterToast(RepairsActivity.this, "没有更多数据");
                                        } else if (listTemp.size() > 0) {
                                            orderList.addAll(listTemp);
                                            adapter.notifyDataSetChanged();
                                        }
                                    }
                                } else {
                                    if (page > 1) {
                                        LogUtils.showCenterToast(RepairsActivity.this, "没有更多数据");
                                    } else {
                                        handle.sendEmptyMessage(1);
                                        LogUtils.showCenterToast(RepairsActivity.this, "没有订单");
                                    }
                                }
                            }
                            repairs_list_view.onRefreshComplete();
                        } else if (err_no == 2100) {
                            handle.sendEmptyMessage(3);
                            LogUtils.showCenterToast(RepairsActivity.this, jsonObject.getInt("err_no") + "" + jsonObject.getString("err_msg"));
                        } else {
                            LogUtils.showCenterToast(RepairsActivity.this, jsonObject.getInt("err_no") + "" + jsonObject.getString("err_msg"));
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

    private Handler handle = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                order_no_data.setVisibility(View.VISIBLE);
                repairs_list_view.setVisibility(View.INVISIBLE);
            } else if (msg.what == 3) {
                startActivity(new Intent(RepairsActivity.this, LoginActivity.class));
            }
        }
    };


}
