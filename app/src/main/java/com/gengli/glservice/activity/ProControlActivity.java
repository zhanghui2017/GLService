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
import android.widget.TextView;

import com.gengli.glservice.R;
import com.gengli.glservice.adapter.ArticleAdapter;
import com.gengli.glservice.adapter.ProControlAdapter;
import com.gengli.glservice.bean.Article;
import com.gengli.glservice.bean.Product;
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

@ContentView(R.layout.activity_pro_control)
public class ProControlActivity extends BaseActivity {

    private ProControlAdapter adapter;
    private List<Product> productList;
    private int page = 1;
    private boolean isShowDel = false;

    @ViewInject(R.id.pro_control_list_view)
    private PullToRefreshListView pro_control_list_view;

    @ViewInject(R.id.order_no_data)
    private LinearLayout order_no_data;

    @ViewInject(R.id.pro_control_edit_bt)
    private TextView pro_control_edit_bt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        getProControl(1);
    }

    private void init() {
        productList = new ArrayList<>();
        adapter = new ProControlAdapter(this, productList);
        pro_control_list_view.setAdapter(adapter);
        pro_control_list_view.setMode(PullToRefreshBase.Mode.BOTH);
        pro_control_list_view.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                page = 1;
                getProControl(1);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page++;
                getProControl(page);
            }
        });

        adapter.setDelClickLintener(new ProControlAdapter.DelClickLintener() {
            @Override
            public void delClick(int position) {
                proRemove(productList.get(position).getId()+"");
                productList.remove(position);
            }
        });

    }

    @Event(value = R.id.pro_control_back_bt, type = View.OnClickListener.class)
    private void backClick(View view) {
        finish();
    }


    @Event(value = R.id.pro_control_edit_bt)
    private void editClick(View view) {
        if (isShowDel) {
            pro_control_edit_bt.setText("编辑");
            isShowDel = false;
            adapter.isShowDel = false;
            adapter.notifyDataSetChanged();
        } else {
            pro_control_edit_bt.setText("完成");
            isShowDel = true;
            adapter.isShowDel = true;
            adapter.notifyDataSetChanged();
        }
    }

    private void proRemove(String id){
        String url = ServicePort.USER_PRODUCT_REMOVE;
        XUtilHttp api = new XUtilHttp();
        Map<String, String> map = new HashMap<>();
        map.put("id", id);
        RequestParams params = api.getParam(this, url, map);
        params.addBodyParameter("id", id);
        api.post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String responce = result.toString();
                LogUtils.showLogD("----->删除常用设备返回数据----->" + responce);
                if (!TextUtils.isEmpty(responce)) {
                    try {
                        JSONObject jsonObject = new JSONObject(responce);
                        int err_no = jsonObject.getInt("err_no");
                        if (err_no == 0) {
                            handler.sendEmptyMessage(4);
                            LogUtils.showCenterToast(ProControlActivity.this, "删除成功");
                        } else {
                            LogUtils.showCenterToast(ProControlActivity.this, jsonObject.getInt("err_no") + "" + jsonObject.getString("err_msg"));
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

    private void getProControl(final int page) {
        final List<Product> listTemp = new ArrayList<>();
        String url = ServicePort.USER_PRODUCT;
        XUtilHttp api = new XUtilHttp();
        Map<String, String> map = new HashMap<>();
        map.put("page", page + "");
        RequestParams params = api.getParam(this, url, map);
        params.addBodyParameter("page", page + "");
        api.post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtils.showLogI("--------常用设备列表返回数据:" + result.toString());
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
                                    Product product = new Product();
                                    product.setId(object.getInt("product_id"));
                                    product.setImgUrl(object.getString("thumb"));
                                    product.setName(object.getString("title"));
                                    product.setDesc(object.getString("des"));
                                    listTemp.add(product);
                                }
                                if (page == 1) {
                                    productList.clear();
                                    if (listTemp.size() == 0) {
                                        LogUtils.showCenterToast(ProControlActivity.this, "没有数据");
                                    } else if (listTemp.size() > 0) {
                                        productList.addAll(listTemp);
                                        adapter.notifyDataSetChanged();
                                    }
                                } else if (page > 1) {
                                    if (listTemp.size() == 0) {
                                        LogUtils.showCenterToast(ProControlActivity.this, "没有更多数据");
                                    } else if (listTemp.size() > 0) {
                                        productList.clear();
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            } else {
                                if (page > 1) {
                                    LogUtils.showCenterToast(ProControlActivity.this, "没有更多数据");
                                } else {
                                    handler.sendEmptyMessage(1);
                                    LogUtils.showLogD("没有数据");
                                }
                            }
                            pro_control_list_view.onRefreshComplete();
                        } else if (err_no == 2100) {
                            handler.sendEmptyMessage(3);
                            LogUtils.showCenterToast(ProControlActivity.this, jsonObject.getInt("err_no") + "" + jsonObject.getString("err_msg"));
                        } else {
                            LogUtils.showCenterToast(ProControlActivity.this, jsonObject.getInt("err_no") + "" + jsonObject.getString("err_msg"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    LogUtils.showCenterToast(ProControlActivity.this, "数据错误");
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
                order_no_data.setVisibility(View.VISIBLE);
                pro_control_list_view.setVisibility(View.INVISIBLE);
            } else if (msg.what == 2) {
                adapter.notifyDataSetChanged();
            } else if (msg.what == 3) {
                startActivity(new Intent(ProControlActivity.this, LoginActivity.class));
            } else if (msg.what == 4){

                adapter.notifyDataSetChanged();
            }
        }
    };
}
