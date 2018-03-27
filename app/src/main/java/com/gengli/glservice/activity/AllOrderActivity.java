package com.gengli.glservice.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gengli.glservice.R;
import com.gengli.glservice.adapter.RepairAdapter;
import com.gengli.glservice.bean.Order;
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

@ContentView(R.layout.activity_all_order)
public class AllOrderActivity extends BaseActivity {

    @ViewInject(R.id.all_order_back_bt)
    private ImageView all_order_back_bt;
    @ViewInject(R.id.order_all_tab_text)
    private TextView order_all_tab_text;
    @ViewInject(R.id.order_begin_tab_text)
    private TextView order_begin_tab_text;
    @ViewInject(R.id.order_ing_tab_text)
    private TextView order_ing_tab_text;
    @ViewInject(R.id.order_ok_tab_text)
    private TextView order_ok_tab_text;

    @ViewInject(R.id.order_all_tab_img)
    private View order_all_tab_img;
    @ViewInject(R.id.order_begin_tab_img)
    private View order_begin_tab_img;
    @ViewInject(R.id.order_ing_tab_img)
    private View order_ing_tab_img;
    @ViewInject(R.id.order_ok_tab_img)
    private View order_ok_tab_img;

    @ViewInject(R.id.order_no_data)
    private LinearLayout order_no_data;

    @ViewInject(R.id.all_order_list_view)
    private PullToRefreshListView all_order_list_view;
    private RepairAdapter adapter;
    private List<Order> orderList;
    private List<Order> allList;
    private List<Order> chooseList;
    private int currentTab = 0;
    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getOrderData(1);
    }

    private void init() {
        all_order_back_bt.setImageResource(R.drawable.img_back_icon);
        orderList = new ArrayList<>();
        allList = new ArrayList<>();
        chooseList = new ArrayList<>();
        adapter = new RepairAdapter(this, orderList);
        all_order_list_view.setAdapter(adapter);
        all_order_list_view.setMode(PullToRefreshBase.Mode.BOTH);
        all_order_list_view.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
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

    @Event(value = R.id.all_order_list_view, type = AdapterView.OnItemClickListener.class)
    private void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Order order = orderList.get(position - 1);
        if (order.getType() == 1) {
            Intent intent = new Intent(this, OrderBeginActivity.class);
            intent.putExtra("BEGIN_ORDER", order);
            startActivity(intent);
        } else if (order.getType() == 3) {
            Intent intent = new Intent(this, OrderINGActivity.class);
            intent.putExtra("ING_ORDER", order);
            startActivity(intent);
        } else if (order.getType() == 4) {
            Intent intent = new Intent(this, OrderOKActivity.class);
            intent.putExtra("OK_ORDER", order);
            startActivity(intent);
        }
    }


    @Event(value = R.id.order_all_tab_view, type = View.OnClickListener.class)
    private void allTabClick(View view) {
        order_all_tab_img.setVisibility(View.VISIBLE);
        order_begin_tab_img.setVisibility(View.INVISIBLE);
        order_ing_tab_img.setVisibility(View.INVISIBLE);
        order_ok_tab_img.setVisibility(View.INVISIBLE);
        order_all_tab_text.setTextColor(setColor(R.color.color_4e8dcf));
        order_begin_tab_text.setTextColor(setColor(R.color.color_666666));
        order_ing_tab_text.setTextColor(setColor(R.color.color_666666));
        order_ok_tab_text.setTextColor(setColor(R.color.color_666666));
        currentTab = 0;
        getChooseList(0);
        adapter.notifyDataSetChanged();

    }

    @Event(value = R.id.order_begin_tab_view)
    private void beginTabClick(View view) {
        order_all_tab_img.setVisibility(View.INVISIBLE);
        order_begin_tab_img.setVisibility(View.VISIBLE);
        order_ing_tab_img.setVisibility(View.INVISIBLE);
        order_ok_tab_img.setVisibility(View.INVISIBLE);
        order_all_tab_text.setTextColor(setColor(R.color.color_666666));
        order_begin_tab_text.setTextColor(setColor(R.color.color_4e8dcf));
        order_ing_tab_text.setTextColor(setColor(R.color.color_666666));
        order_ok_tab_text.setTextColor(setColor(R.color.color_666666));
        currentTab = 1;
        getChooseList(1);
        adapter.notifyDataSetChanged();
    }

    @Event(value = R.id.order_ing_tab_view)
    private void ingTabClick(View view) {
        order_all_tab_img.setVisibility(View.INVISIBLE);
        order_begin_tab_img.setVisibility(View.INVISIBLE);
        order_ing_tab_img.setVisibility(View.VISIBLE);
        order_ok_tab_img.setVisibility(View.INVISIBLE);
        order_all_tab_text.setTextColor(setColor(R.color.color_666666));
        order_begin_tab_text.setTextColor(setColor(R.color.color_666666));
        order_ing_tab_text.setTextColor(setColor(R.color.color_4e8dcf));
        order_ok_tab_text.setTextColor(setColor(R.color.color_666666));
        currentTab = 3;
        getChooseList(3);
        adapter.notifyDataSetChanged();
    }

    @Event(value = R.id.order_ok_tab_view)
    private void okTabClick(View view) {
        order_all_tab_img.setVisibility(View.INVISIBLE);
        order_begin_tab_img.setVisibility(View.INVISIBLE);
        order_ing_tab_img.setVisibility(View.INVISIBLE);
        order_ok_tab_img.setVisibility(View.VISIBLE);
        order_all_tab_text.setTextColor(setColor(R.color.color_666666));
        order_begin_tab_text.setTextColor(setColor(R.color.color_666666));
        order_ing_tab_text.setTextColor(setColor(R.color.color_666666));
        order_ok_tab_text.setTextColor(setColor(R.color.color_4e8dcf));
        currentTab = 4;
        getChooseList(4);
        adapter.notifyDataSetChanged();
    }

    @Event(value = R.id.all_order_back_bt)
    private void backClick(View view) {
        finish();
    }

    private int setColor(int color) {
        return ContextCompat.getColor(this, color);
    }


    /**
     * 获取选择框中选择的列表
     *
     * @param type
     */
    public void getChooseList(int type) {
        chooseList.clear();
        if (type == 0) {
            if (allList.size() <= 0) {
                order_no_data.setVisibility(View.VISIBLE);
                all_order_list_view.setVisibility(View.INVISIBLE);
            } else {
                order_no_data.setVisibility(View.INVISIBLE);
                all_order_list_view.setVisibility(View.VISIBLE);
                if (orderList.size() > 0) {
                    orderList.clear();
                    orderList.addAll(allList);
                } else if (orderList.size() == 0) {
                    orderList.addAll(allList);
                } else {
                    Log.d("test", "-----------------这什么情况-----------------");
                }
            }
        } else {
            if (allList != null) {
                for (int i = 0; i < allList.size(); i++) {
                    Order order = allList.get(i);
                    if (type == order.getType()) {
                        chooseList.add(order);
                    }
                }
                if (chooseList.size() > 0) {
                    order_no_data.setVisibility(View.INVISIBLE);
                    all_order_list_view.setVisibility(View.VISIBLE);
                    if (orderList.size() > 0) {
                        orderList.clear();
                        orderList.addAll(chooseList);
                    } else if (orderList.size() == 0) {
                        orderList.addAll(chooseList);
                    } else {
                        Log.d("test", "I don't know what happen!");
                    }
                } else {
                    order_no_data.setVisibility(View.VISIBLE);
                    all_order_list_view.setVisibility(View.INVISIBLE);
                }
            } else {
                Toast.makeText(this, "暂没有新数据", Toast.LENGTH_SHORT).show();
            }
        }

    }


    private void getOrderData(final int page) {
        final List<Order> listTemp = new ArrayList<>();
        String url = ServicePort.REPAIR_LISTS;
        XUtilHttp api = new XUtilHttp();
        Map<String, String> map = new HashMap<>();
        map.put("status", 0 + "");
        map.put("page", page + "");
        RequestParams params = api.getParam(this, url, map);
        params.addBodyParameter("status", 0 + "");
        params.addBodyParameter("page", page + "");
        api.post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtils.showLogI("--------所有维修单返回数据:" + result.toString());
                String responce = result.toString();
                if (!TextUtils.isEmpty(responce)) {
                    try {
                        JSONObject jsonObject = new JSONObject(responce);
                        int err_no = jsonObject.getInt("err_no");
                        if (err_no == 0) {
                            JSONObject results = jsonObject.getJSONObject("results");
                            if (!TextUtils.isEmpty(results.toString())) {
                                JSONArray lists = results.getJSONArray("lists");
//                                LogUtils.showLogI("--------所有维修单返回数据:" + lists.toString());
                                if (lists.length() > 0) {
                                    for (int i = 0; i < lists.length(); i++) {
                                        JSONObject item = lists.getJSONObject(i);
                                        int type = item.getInt("status");
                                        if (type != 2) {
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
                                            order.setIs_emerg(item.getBoolean("is_emerg"));
                                            listTemp.add(order);
                                        }

                                    }
                                    if (page == 1) {
                                        allList.clear();
                                        if (listTemp.size() == 0) {
                                            LogUtils.showCenterToast(AllOrderActivity.this, "没有数据");
                                        } else if (listTemp.size() > 0) {
                                            allList.addAll(listTemp);
                                            adapter.notifyDataSetChanged();
                                        }
                                    } else if (page > 1) {
                                        if (listTemp.size() == 0) {
                                            LogUtils.showCenterToast(AllOrderActivity.this, "没有更多数据");
                                        } else if (listTemp.size() > 0) {
                                            allList.addAll(listTemp);
                                            adapter.notifyDataSetChanged();
                                        }
                                    }
                                    handle.sendEmptyMessage(2);
                                } else {
                                    if (page > 1) {
                                        LogUtils.showCenterToast(AllOrderActivity.this, "没有更多数据");
                                    } else {
                                        handle.sendEmptyMessage(1);
                                        LogUtils.showCenterToast(AllOrderActivity.this, "没有订单");
                                    }

                                }
                            }
                            all_order_list_view.onRefreshComplete();
                        } else if (err_no == 2100) {
                            handle.sendEmptyMessage(3);
                            LogUtils.showCenterToast(AllOrderActivity.this, jsonObject.getInt("err_no") + "" + jsonObject.getString("err_msg"));
                        } else {
                            LogUtils.showCenterToast(AllOrderActivity.this, jsonObject.getInt("err_no") + "" + jsonObject.getString("err_msg"));
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
                all_order_list_view.setVisibility(View.INVISIBLE);
            } else if (msg.what == 2) {
                getChooseList(currentTab);
                adapter.notifyDataSetChanged();
            } else if (msg.what == 3) {
                startActivity(new Intent(AllOrderActivity.this, LoginActivity.class));
            }
        }
    };


}
