package com.gengli.glservice.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.gengli.glservice.R;
import com.gengli.glservice.adapter.ArticleAdapter;
import com.gengli.glservice.adapter.ProductAdapter;
import com.gengli.glservice.adapter.SearchKeyAdapter;
import com.gengli.glservice.bean.Article;
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

@ContentView(R.layout.activity_main_search_activuty)
public class MainSearchActivuty extends BaseActivity {

    private SearchKeyAdapter searchKeyAdapter;
    private String[] data = new String[]{"产品", "资讯"};
    private List<String> keyWordlist = new ArrayList<>();
    private List<Product> productList;
    private ProductAdapter productAdapter;
    private ArticleAdapter articleAdapter;
    private List<Article> articleList;

    private int type = 0;
    private int proPage = 1;
    private int artPage = 1;

    @ViewInject(R.id.main_search_spinner)
    private Spinner main_search_spinner;

    @ViewInject(R.id.main_search_pro_list)
    private ListView main_search_pro_list;

    @ViewInject(R.id.main_search_help_list)
    private ListView main_search_help_list;

    @ViewInject(R.id.main_search_grid)
    private GridView main_search_grid;

    @ViewInject(R.id.main_search_edit)
    private EditText main_search_edit;

    @ViewInject(R.id.main_search_list_tab)
    private TextView main_search_list_tab;

    @ViewInject(R.id.order_no_data)
    private LinearLayout order_no_data;

    @Event(value = R.id.main_search_cancle_bt, type = View.OnClickListener.class)
    private void cancleClick(View view) {
        finish();
    }


    @Event(value = R.id.main_search_clear_bt)
    private void clearClick(View view) {
        clearSearchRecord();
    }

    @Event(value = R.id.main_search_change_bt)
    private void changeClick(View view) {
        if (type == 0) {
            proPage++;
            getHotProduct(proPage);
        } else {
            artPage++;
            getHotActicle(artPage);
        }

    }

    @Event(value = R.id.main_search_commit)
    private void searchClick(View view) {
        String keyWord = main_search_edit.getText().toString();
        if (TextUtils.isEmpty(keyWord)) {
            return;
        }
        addSearchRecord(keyWord);
        handler.sendEmptyMessage(1);
        if (type == 0) {
            startActivity(new Intent(this, ProSearchListActivity.class).putExtra("key_word", keyWord));
        } else {
            startActivity(new Intent(this, ArticleListActivity.class).putExtra("key_word", keyWord).putExtra("article_title", "搜索结果"));
        }


    }

    @Event(value = R.id.main_search_grid, type = AdapterView.OnItemClickListener.class)
    private void onGridClick(AdapterView<?> parent, View view, int position, long id) {
        LogUtils.showLogD(" position=== " + position);
        if (type == 0) {
            startActivity(new Intent(this, ProSearchListActivity.class).putExtra("key_word", keyWordlist.get(position)));
        } else {
            startActivity(new Intent(this, ArticleListActivity.class).putExtra("key_word", keyWordlist.get(position)).putExtra("article_title", "搜索结果"));
        }
    }

    @Event(value = R.id.main_search_pro_list, type = AdapterView.OnItemClickListener.class)
    private void onProListClick(AdapterView<?> parent, View view, int position, long id) {
        startActivity(new Intent(this, ProductDetailActivity.class).putExtra("cur_product", productList.get(position)));
    }

    @Event(value = R.id.main_search_help_list, type = AdapterView.OnItemClickListener.class)
    private void onHelpListClick(AdapterView<?> parent, View view, int position, long id) {
        int articleid = articleList.get(position).getId();
        startActivity(new Intent(MainSearchActivuty.this, ArticleDetailActivity.class).putExtra("articleid", articleid));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        getSearchRecord(false);
        getHotActicle(artPage);
        getHotProduct(proPage);
    }


    private void init() {
        searchKeyAdapter = new SearchKeyAdapter(this, keyWordlist);
        main_search_grid.setAdapter(searchKeyAdapter);
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.item_spinner, R.id.item_spinner_text, data);
        main_search_spinner.setAdapter(adapter);
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(this, productList);
        main_search_pro_list.setAdapter(productAdapter);

        articleList = new ArrayList<>();
        articleAdapter = new ArticleAdapter(this, articleList);
        main_search_help_list.setAdapter(articleAdapter);


        main_search_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    main_search_list_tab.setText("热搜产品");
                    main_search_pro_list.setVisibility(View.VISIBLE);
                    main_search_help_list.setVisibility(View.INVISIBLE);
                    type = 0;
                } else {
                    main_search_list_tab.setText("热搜咨询");
                    main_search_pro_list.setVisibility(View.INVISIBLE);
                    main_search_help_list.setVisibility(View.VISIBLE);
                    type = 1;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
            main_search_grid.setVisibility(View.GONE);
            order_no_data.setVisibility(View.VISIBLE);
        } else {
            main_search_grid.setVisibility(View.VISIBLE);
            order_no_data.setVisibility(View.GONE);
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
        main_search_grid.setVisibility(View.GONE);
        order_no_data.setVisibility(View.VISIBLE);
    }


    private void getHotProduct(final int page) {
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
                                    if (page > 1) {
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
                                    LogUtils.showCenterToast(MainSearchActivuty.this, "没有数据");
                                }
                            }
                        } else if (err_no == 2100) {
                            handle.sendEmptyMessage(3);
                            LogUtils.showCenterToast(MainSearchActivuty.this, jsonObject.getInt("err_no") + "" + jsonObject.getString("err_msg"));
                        } else {
                            LogUtils.showCenterToast(MainSearchActivuty.this, jsonObject.getInt("err_no") + "" + jsonObject.getString("err_msg"));
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


    private void getHotActicle(final int page) {
        String url = ServicePort.ARCHIVE_LISTS;
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
                LogUtils.showLogD("--------耿力头条返回数据:" + result.toString());
                String responce = result.toString();
                if (!TextUtils.isEmpty(responce)) {
                    try {
                        JSONObject jsonObject = new JSONObject(responce);
                        int err_no = jsonObject.getInt("err_no");
                        if (err_no == 0) {
                            JSONObject results = jsonObject.getJSONObject("results");
                            JSONArray list = results.getJSONArray("list");
                            if (list != null && list.length() > 0) {
                                if (page > 1) {
                                    articleList.clear();
                                }
                                for (int i = 0; i < list.length(); i++) {
                                    JSONObject object = list.getJSONObject(i);
                                    Article article = new Article();
                                    article.setId(object.getInt("id"));
                                    article.setImgUrl(object.getString("thumb"));
                                    article.setTitle(object.getString("title"));
                                    article.setDesc(object.getString("des").toString().trim());
                                    articleList.add(article);
                                }
                                articleAdapter.notifyDataSetChanged();
                            } else {
                                LogUtils.showCenterToast(MainSearchActivuty.this, "没有数据");
                            }

                        } else if (err_no == 2100) {
                            handler.sendEmptyMessage(3);
                            LogUtils.showCenterToast(MainSearchActivuty.this, jsonObject.getInt("err_no") + "" + jsonObject.getString("err_msg"));
                        } else {
                            LogUtils.showCenterToast(MainSearchActivuty.this, jsonObject.getInt("err_no") + "" + jsonObject.getString("err_msg"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    LogUtils.showCenterToast(MainSearchActivuty.this, "数据错误");
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
//                order_no_data.setVisibility(View.VISIBLE);
//                pro_search_list.setVisibility(View.INVISIBLE);
            } else if (msg.what == 2) {
//                productAdapter.notifyDataSetChanged();
            } else if (msg.what == 3) {
                startActivity(new Intent(MainSearchActivuty.this, LoginActivity.class));
            }
        }
    };


}
