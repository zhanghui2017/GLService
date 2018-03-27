package com.gengli.glservice.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.gengli.glservice.R;
import com.gengli.glservice.adapter.ProductAdapter;
import com.gengli.glservice.adapter.ProductMenuAdapter;
import com.gengli.glservice.bean.Category;
import com.gengli.glservice.bean.Order;
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

@ContentView(R.layout.activity_product_list)
public class ProductListActivity extends BaseActivity {

    @ViewInject(R.id.product_list_title_text)
    private TextView product_list_title_text;
    private String category_name = "";
    private int category_id = -1;
    private int page = 1;
    private List<Category> categoryList;

    @ViewInject(R.id.id_drawer_layout)
    private DrawerLayout mDrawerLayout;
    private ProductMenuAdapter proMenuAdapter;

    @ViewInject(R.id.product_menu_list)
    private ListView product_menu_list;

    @ViewInject(R.id.order_no_data)
    private LinearLayout order_no_data;

    @ViewInject(R.id.product_list_view)
    private PullToRefreshListView product_list_view;
    private ProductAdapter productAdapter;
    private List<Product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        category_name = getIntent().getStringExtra("category_name");
        category_id = getIntent().getIntExtra("category_id", -1);
        getProductList(category_id, 1);
        categoryList = (ArrayList<Category>) getIntent().getSerializableExtra("category_list");
        if (!TextUtils.isEmpty(category_name)) {
            product_list_title_text.setText(category_name);
        }
        proMenuAdapter = new ProductMenuAdapter(this, categoryList);
        product_menu_list.setAdapter(proMenuAdapter);
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(this, productList);
        product_list_view.setAdapter(productAdapter);
        product_list_view.setMode(PullToRefreshBase.Mode.BOTH);
        product_list_view.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                page = 1;
                getProductList(category_id, 1);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page++;
                getProductList(category_id, page);
            }
        });
    }

    @Event(value = R.id.product_list_back_bt, type = View.OnClickListener.class)
    private void backClick(View view) {
        finish();
    }


    @Event(value = R.id.product_list_more_bt)
    private void moreClick(View view) {
        LogUtils.showLogD("more");
        mDrawerLayout.openDrawer(GravityCompat.END);

    }

    @Event(value = R.id.product_menu_list, type = AdapterView.OnItemClickListener.class)
    private void onMenuClick(AdapterView<?> parent, View view, int position, long id) {
        LogUtils.showLogD("product_menu_list position=== " + position);
        category_id = categoryList.get(position).getId();
        category_name = categoryList.get(position).getName();
        product_list_title_text.setText(category_name);
        getProductList(category_id, 1);
        mDrawerLayout.closeDrawer(GravityCompat.END);

    }

    @Event(value = R.id.product_list_view, type = AdapterView.OnItemClickListener.class)
    private void onListClick(AdapterView<?> parent, View view, int position, long id) {
        LogUtils.showLogD("product_list_view position=== " + position);
        startActivity(new Intent(this, ProductDetailActivity.class).putExtra("cur_product", productList.get(position-1)));

    }

    private void getProductList(int id, final int page) {
        final List<Product> listTemp = new ArrayList<>();
        String url = ServicePort.PRODUCT_LISTS;
        XUtilHttp api = new XUtilHttp();
        Map<String, String> map = new HashMap<>();
        map.put("category_id", id + "");
        map.put("page", page + "");
        map.put("size", 10 + "");
        RequestParams params = api.getParam(this, url, map);
        params.addBodyParameter("category_id", id + "");
        params.addBodyParameter("page", page + "");
        params.addBodyParameter("size", 10 + "");
        api.post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtils.showLogD("--------产品列表返回数据:" + result.toString());
                String responce = result.toString();
                if (!TextUtils.isEmpty(responce)) {
                    try {
                        JSONObject jsonObject = new JSONObject(responce);
                        int err_no = jsonObject.getInt("err_no");
                        if (err_no == 0) {
                            JSONObject results = jsonObject.getJSONObject("results");
                            if (!TextUtils.isEmpty(results.toString())) {
                                final JSONArray lists = results.getJSONArray("lists");
                                if (lists != null && lists.length() > 0) {
                                    for (int i = 0; i < lists.length(); i++) {
                                        JSONObject item = lists.getJSONObject(i);
                                        Product product = new Product();
                                        product.setId(item.getInt("id"));
                                        product.setImgUrl(item.getString("thumb"));
                                        product.setName(item.getString("title"));
                                        product.setDesc(item.getString("des"));
                                        product.setHot(item.getBoolean("is_hot"));
                                        product.setNew(item.getBoolean("is_new"));
                                        listTemp.add(product);
                                    }
                                    if (page == 1) {
                                        productList.clear();
                                        if (listTemp.size() == 0) {
                                            LogUtils.showCenterToast(ProductListActivity.this, "没有数据");
                                        } else if (listTemp.size() > 0) {
                                            productList.addAll(listTemp);
                                            productAdapter.notifyDataSetChanged();
                                        }
                                    } else if (page > 1) {
                                        if (listTemp.size() == 0) {
                                            LogUtils.showCenterToast(ProductListActivity.this, "没有更多数据");
                                        } else if (listTemp.size() > 0) {
                                            productList.addAll(listTemp);
                                            productAdapter.notifyDataSetChanged();
                                        }
                                    }
//                                    handle.sendEmptyMessage(2);
                                } else {
                                    if (page > 1) {
                                        LogUtils.showCenterToast(ProductListActivity.this, "没有更多数据");
                                    }else {
                                        handle.sendEmptyMessage(1);
                                        LogUtils.showLogD("没有数据");
                                    }

                                }
                            }
                            product_list_view.onRefreshComplete();
                        } else if (err_no == 2100) {
                            handle.sendEmptyMessage(3);
                            LogUtils.showCenterToast(ProductListActivity.this, jsonObject.getInt("err_no") + "" + jsonObject.getString("err_msg"));
                        } else {
                            LogUtils.showCenterToast(ProductListActivity.this, jsonObject.getInt("err_no") + "" + jsonObject.getString("err_msg"));
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
                product_list_view.setVisibility(View.INVISIBLE);
            } else if (msg.what == 2) {
                productAdapter.notifyDataSetChanged();
            } else if (msg.what == 3) {
                startActivity(new Intent(ProductListActivity.this, LoginActivity.class));
            }
        }
    };

}
