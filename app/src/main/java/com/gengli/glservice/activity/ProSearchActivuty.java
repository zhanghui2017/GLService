package com.gengli.glservice.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.gengli.glservice.R;
import com.gengli.glservice.adapter.ProductAdapter;
import com.gengli.glservice.adapter.SearchKeyAdapter;
import com.gengli.glservice.bean.Product;
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

@ContentView(R.layout.activity_pro_search)
public class ProSearchActivuty extends BaseActivity {

    private List<Product> productList;
    private ProductAdapter productAdapter;
    private int page = 1;

    private SearchKeyAdapter searchKeyAdapter;
    private List<String> keyWordlist = new ArrayList<>();

    @ViewInject(R.id.pro_search_list)
    private ListView pro_search_list;

    @ViewInject(R.id.pro_search_grid)
    private GridView pro_search_grid;

    @ViewInject(R.id.pro_search_edit)
    private EditText pro_search_edit;

    @ViewInject(R.id.keyword_no_data)
    private LinearLayout keyword_no_data;

    @ViewInject(R.id.order_no_data)
    private LinearLayout order_no_data;

    @Event(value = R.id.pro_search_cancle_bt, type = View.OnClickListener.class)
    private void cancleClick(View view) {
        finish();
    }


    @Event(value = R.id.pro_search_clear_bt)
    private void clearClick(View view) {
        clearSearchRecord();
    }

    @Event(value = R.id.pro_search_change_bt)
    private void changeClick(View view) {
        page++;
        getProductList(page);
    }

    @Event(value = R.id.pro_search_commit)
    private void searchClick(View view) {
        String keyWord = pro_search_edit.getText().toString();
        if (TextUtils.isEmpty(keyWord)) {
            return;
        }
        addSearchRecord(keyWord);
        handler.sendEmptyMessage(1);
        startActivity(new Intent(this, ProSearchListActivity.class).putExtra("key_word", keyWord));
    }

    @Event(value = R.id.pro_search_grid, type = AdapterView.OnItemClickListener.class)
    private void onGridClick(AdapterView<?> parent, View view, int position, long id) {
        startActivity(new Intent(this, ProSearchListActivity.class).putExtra("key_word", keyWordlist.get(position)));

    }

    @Event(value = R.id.pro_search_list, type = AdapterView.OnItemClickListener.class)
    private void onListClick(AdapterView<?> parent, View view, int position, long id) {
        startActivity(new Intent(this, ProductDetailActivity.class).putExtra("cur_product", productList.get(position)));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        getProductList(page);
        getSearchRecord(false);
    }


    private void init() {
        searchKeyAdapter = new SearchKeyAdapter(this, keyWordlist);
        pro_search_grid.setAdapter(searchKeyAdapter);
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(this, productList);
        pro_search_list.setAdapter(productAdapter);
    }


    /**
     * 添加记录
     */
    private void addSearchRecord(String str) {
        List<String> list = DatasUtil.getSearchKeyWord(this);
        if (list.contains(str)) {
            return;
        } else {
            if (list.size() > 15) {
                list.remove(0);
                StringBuffer buffer = new StringBuffer();
                for (int i = 0; i < list.size(); i++) {
                    buffer.append(list.get(i) + ",");
                }
                buffer.append(str);
                String newRecord = buffer.toString();
                DatasUtil.storeSearchKeyWord(this, newRecord);
            } else {
                if (list.size() > 0) {
                    StringBuffer buffer = new StringBuffer();
                    for (int i = 0; i < list.size(); i++) {
                        buffer.append(list.get(i) + ",");
                    }
                    buffer.append(str);
                    String newRecord = buffer.toString();
                    DatasUtil.storeSearchKeyWord(this, newRecord);
                } else {
                    DatasUtil.storeSearchKeyWord(this, str);
                }
            }
        }
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO 自动生成的方法存根
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    getSearchRecord(true);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 获取搜索记录
     */
    private void getSearchRecord(boolean isFresh) {
        List<String> listTemp = DatasUtil.getSearchKeyWord(this);
        if (listTemp.size() == 0) {
            pro_search_grid.setVisibility(View.GONE);
            keyword_no_data.setVisibility(View.VISIBLE);
        } else {
            pro_search_grid.setVisibility(View.VISIBLE);
            keyword_no_data.setVisibility(View.GONE);
            if (isFresh) {
                if (listTemp == null || (listTemp != null && listTemp.size() <= 0)) {
                    if (keyWordlist != null) {

                    }
                } else {
                    keyWordlist.clear();
                    searchKeyAdapter.notifyDataSetChanged();
                }
            }
            if (listTemp == null || (listTemp != null && listTemp.size() <= 0)) {
                if (isFresh) {
                    if (keyWordlist != null) {
                    }
                }
            } else {
                keyWordlist.addAll(listTemp);
            }
        }


    }

    private void clearSearchRecord() {
        DatasUtil.clearSearchKeyWord(this);
        keyWordlist.clear();
        searchKeyAdapter.notifyDataSetChanged();
        pro_search_grid.setVisibility(View.GONE);
        keyword_no_data.setVisibility(View.VISIBLE);
    }


    private void getProductList(final int page) {
        String url = ServicePort.PRODUCT_LISTS;
        XUtilHttp api = new XUtilHttp();
        Map<String, String> map = new HashMap<>();
        map.put("is_hot", 1 + "");
        map.put("page", page + "");
        map.put("size", 5 + "");
        RequestParams params = api.getParam(this, url, map);
        params.addBodyParameter("is_hot", 1 + "");
        params.addBodyParameter("page", page + "");
        params.addBodyParameter("size", 5 + "");
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
                                    if (page>1){
                                        productList.clear();
                                    }
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
                                    LogUtils.showCenterToast(ProSearchActivuty.this, "没有数据");
                                }
                            }
                        } else if (err_no == 2100) {
                            handle.sendEmptyMessage(3);
                            LogUtils.showCenterToast(ProSearchActivuty.this, jsonObject.getInt("err_no") + "" + jsonObject.getString("err_msg"));
                        } else {
                            LogUtils.showCenterToast(ProSearchActivuty.this, jsonObject.getInt("err_no") + "" + jsonObject.getString("err_msg"));
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
                pro_search_list.setVisibility(View.INVISIBLE);
            } else if (msg.what == 2) {
                productAdapter.notifyDataSetChanged();
            } else if (msg.what == 3) {
                startActivity(new Intent(ProSearchActivuty.this, LoginActivity.class));
            }
        }
    };


}
