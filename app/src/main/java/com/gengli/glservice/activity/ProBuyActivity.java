package com.gengli.glservice.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.gengli.glservice.R;
import com.gengli.glservice.adapter.ProBuyAdapter;
import com.gengli.glservice.bean.ProBuy;
import com.gengli.glservice.http.ServicePort;
import com.gengli.glservice.http.XUtilHttp;
import com.gengli.glservice.util.LogUtils;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

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

@ContentView(R.layout.activity_pro_buy)
public class ProBuyActivity extends BaseActivity {

    private ProBuyAdapter adapter;
    private List<ProBuy> proBuyList;
    private int page = 1;

    @ViewInject(R.id.pro_buy_list_view)
    private PullToRefreshListView pro_buy_list_view;

    @ViewInject(R.id.pro_buy_no_data)
    private LinearLayout pro_buy_no_data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        getProBuy(1);
    }

    private void init() {
        proBuyList = new ArrayList<>();
        adapter = new ProBuyAdapter(this, proBuyList);
        pro_buy_list_view.setAdapter(adapter);
        pro_buy_list_view.setMode(PullToRefreshBase.Mode.BOTH);
        pro_buy_list_view.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                page = 1;
                getProBuy(1);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page++;
                getProBuy(page);
            }
        });
    }

    @Event(value = R.id.pro_buy_list_view, type = AdapterView.OnItemClickListener.class)
    private void onListClick(AdapterView<?> parent, View view, int position, long id) {
        int pro_id = proBuyList.get(position - 1).getProduct_id();
        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra("pro_id", pro_id);
        startActivity(intent);
    }


    @Event(value = R.id.pro_buy_back_bt, type = View.OnClickListener.class)
    private void backClick(View view) {
        finish();
    }


    private void getProBuy(final int page) {
        final List<ProBuy> listTemp = new ArrayList<>();
        String url = ServicePort.USER_ORDER;
        XUtilHttp api = new XUtilHttp();
        Map<String, String> map = new HashMap<>();
        map.put("page", page + "");
        map.put("size", 10 + "");
        RequestParams params = api.getParam(this, url, map);
        params.addBodyParameter("page", page + "");
        params.addBodyParameter("size", 10 + "");
        api.post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtils.showLogD("--------购买记录返回数据:" + result.toString());
                String responce = result.toString();
                if (!TextUtils.isEmpty(responce)) {
                    try {
                        JSONObject jsonObject = new JSONObject(responce);
                        int err_no = jsonObject.getInt("err_no");
                        if (err_no == 0) {
                            JSONObject results = jsonObject.getJSONObject("results");
                            JSONArray lists = results.getJSONArray("lists");
                            if (lists != null && lists.length() > 0) {
                                for (int i = 0; i < lists.length(); i++) {
                                    JSONObject object = lists.getJSONObject(i);
                                    ProBuy proBuy = new ProBuy();
                                    proBuy.setProduct_id(object.getInt("product_id"));
                                    proBuy.setThumb(object.getString("thumb"));
                                    proBuy.setTitle(object.getString("title"));
                                    proBuy.setDes(object.getString("des"));
                                    proBuy.setBuy_date(object.getString("buy_date"));
                                    proBuy.setBuy_period(object.getString("buy_period"));
                                    listTemp.add(proBuy);
                                }
                                if (page == 1) {
                                    proBuyList.clear();
                                    if (listTemp.size() == 0) {
                                        LogUtils.showCenterToast(ProBuyActivity.this, "没有数据");
                                    } else if (listTemp.size() > 0) {
                                        proBuyList.addAll(listTemp);
                                        adapter.notifyDataSetChanged();
                                    }
                                } else if (page > 1) {
                                    if (listTemp.size() == 0) {
                                        LogUtils.showCenterToast(ProBuyActivity.this, "没有更多数据");
                                    } else if (listTemp.size() > 0) {
                                        proBuyList.clear();
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            } else {
                                if (page > 1) {
                                    LogUtils.showCenterToast(ProBuyActivity.this, "没有更多数据");
                                } else {
                                    handler.sendEmptyMessage(1);
                                    LogUtils.showLogD("没有数据");
                                }
                            }
                            pro_buy_list_view.onRefreshComplete();
                        } else if (err_no == 2100) {
                            handler.sendEmptyMessage(3);
                            LogUtils.showCenterToast(ProBuyActivity.this, jsonObject.getInt("err_no") + "" + jsonObject.getString("err_msg"));
                        } else {
                            LogUtils.showCenterToast(ProBuyActivity.this, jsonObject.getInt("err_no") + "" + jsonObject.getString("err_msg"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    LogUtils.showCenterToast(ProBuyActivity.this, "数据错误");
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
                pro_buy_no_data.setVisibility(View.VISIBLE);
                pro_buy_list_view.setVisibility(View.INVISIBLE);
            } else if (msg.what == 2) {
                adapter.notifyDataSetChanged();
            } else if (msg.what == 3) {
                startActivity(new Intent(ProBuyActivity.this, LoginActivity.class));
            } else if (msg.what == 4) {

                adapter.notifyDataSetChanged();
            }
        }
    };
}
