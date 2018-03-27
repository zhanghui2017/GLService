package com.gengli.glservice.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

@ContentView(R.layout.activity_pro_search_list)
public class ProSearchListActivity extends BaseActivity {

    @ViewInject(R.id.order_no_data)
    private LinearLayout order_no_data;

    @ViewInject(R.id.pro_search_list_view)
    private ListView pro_search_list_view;

    private ProductAdapter productAdapter;
    private List<Product> productList;

    private int page = 1;
    private String keyWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        keyWord = getIntent().getStringExtra("key_word");
        init();
    }

    private void init() {
        getProductList();
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(this, productList);
        pro_search_list_view.setAdapter(productAdapter);

    }

    @Event(value = R.id.pro_search_list_back_bt, type = View.OnClickListener.class)
    private void backClick(View view) {
        finish();
    }


    @Event(value = R.id.pro_search_list_view, type = AdapterView.OnItemClickListener.class)
    private void onListClick(AdapterView<?> parent, View view, int position, long id) {
        startActivity(new Intent(this, ProductDetailActivity.class).putExtra("cur_product", productList.get(position - 1)));
    }

    private void getProductList() {
        String url = ServicePort.PRODUCT_LISTS;
        XUtilHttp api = new XUtilHttp();
        Map<String, String> map = new HashMap<>();
        map.put("keyword", keyWord);
        map.put("size", 10 + "");
        RequestParams params = api.getParam(this, url, map);
        params.addBodyParameter("keyword", keyWord);
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
                                        productList.add(product);
                                    }
                                    productAdapter.notifyDataSetChanged();
                                } else {
                                    handle.sendEmptyMessage(1);
                                    LogUtils.showLogD("没有数据");
                                }
                            }
                        } else if (err_no == 2100) {
                            handle.sendEmptyMessage(3);
                            LogUtils.showCenterToast(ProSearchListActivity.this, jsonObject.getInt("err_no") + "" + jsonObject.getString("err_msg"));
                        } else {
                            LogUtils.showCenterToast(ProSearchListActivity.this, jsonObject.getInt("err_no") + "" + jsonObject.getString("err_msg"));
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
                pro_search_list_view.setVisibility(View.INVISIBLE);
            } else if (msg.what == 2) {
                productAdapter.notifyDataSetChanged();
            } else if (msg.what == 3) {
                startActivity(new Intent(ProSearchListActivity.this, LoginActivity.class));
            }
        }
    };

}
