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
import android.widget.TextView;

import com.gengli.glservice.R;
import com.gengli.glservice.adapter.ArticleAdapter;
import com.gengli.glservice.adapter.SearchKeyAdapter;
import com.gengli.glservice.bean.Article;
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

@ContentView(R.layout.activity_help_search)
public class HelpSearchActivuty extends BaseActivity {

    private int page = 1;
    private ArticleAdapter articleAdapter;
    private List<Article> articleList;

    private SearchKeyAdapter searchKeyAdapter;
    private List<String> keyWordlist = new ArrayList<>();

    @ViewInject(R.id.help_search_list)
    private ListView help_search_list;

    @ViewInject(R.id.help_search_grid)
    private GridView help_search_grid;

    @ViewInject(R.id.help_search_edit)
    private EditText help_search_edit;

    @ViewInject(R.id.help_search_list_tab)
    private TextView help_search_list_tab;

    @ViewInject(R.id.order_no_data)
    private LinearLayout order_no_data;

    @Event(value = R.id.help_search_cancle_bt, type = View.OnClickListener.class)
    private void cancleClick(View view) {
        finish();
    }


    @Event(value = R.id.help_search_clear_bt)
    private void clearClick(View view) {
        clearSearchRecord();
    }

    @Event(value = R.id.help_search_change_bt)
    private void changeClick(View view) {
        page++;
        getHotActicle(page);
    }

    @Event(value = R.id.help_search_commit)
    private void searchClick(View view) {
        String keyWord = help_search_edit.getText().toString();
        if (TextUtils.isEmpty(keyWord)) {
            return;
        }
        addSearchRecord(keyWord);
        handler.sendEmptyMessage(1);
        startActivity(new Intent(this, ArticleListActivity.class).putExtra("key_word", keyWord).putExtra("article_title", "搜索结果"));
    }

    @Event(value = R.id.help_search_grid, type = AdapterView.OnItemClickListener.class)
    private void onGridClick(AdapterView<?> parent, View view, int position, long id) {
        startActivity(new Intent(this, ArticleListActivity.class).putExtra("key_word", keyWordlist.get(position)).putExtra("article_title", "搜索结果"));

    }

    @Event(value = R.id.help_search_list)
    private void onListClick(AdapterView<?> parent, View view, int position, long id) {
        int articleid = articleList.get(position - 1).getId();
        Intent intent = new Intent(HelpSearchActivuty.this, ArticleDetailActivity.class);
        intent.putExtra("articleid", articleid);
        startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        getHotActicle(page);
        getSearchRecord(false);
    }

    private void init() {
        searchKeyAdapter = new SearchKeyAdapter(this, keyWordlist);
        help_search_grid.setAdapter(searchKeyAdapter);

        articleList = new ArrayList<>();
        articleAdapter = new ArticleAdapter(this, articleList);
        help_search_list.setAdapter(articleAdapter);

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
                case 3:
                    startActivity(new Intent(HelpSearchActivuty.this, LoginActivity.class));
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
        if (listTemp.size()==0){
            help_search_grid.setVisibility(View.GONE);
            order_no_data.setVisibility(View.VISIBLE);
        }else{
            help_search_grid.setVisibility(View.VISIBLE);
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
        help_search_grid.setVisibility(View.GONE);
        order_no_data.setVisibility(View.VISIBLE);
    }


    private void getHotActicle(final int page) {
        String url = ServicePort.ARCHIVE_LISTS;
        XUtilHttp api = new XUtilHttp();
        Map<String, String> map = new HashMap<>();
        map.put("is_hot", 1 + "");
        map.put("page", page + "");
        map.put("size", 5+"");
        RequestParams params = api.getParam(this, url, map);
        params.addBodyParameter("is_hot", 1 + "");
        params.addBodyParameter("page", page + "");
        params.addBodyParameter("size", 5+"");
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
                                if (page>1){
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
                                LogUtils.showCenterToast(HelpSearchActivuty.this, "没有数据");
                            }

                        } else if (err_no == 2100) {
                            handler.sendEmptyMessage(3);
                            LogUtils.showCenterToast(HelpSearchActivuty.this, jsonObject.getInt("err_no") + "" + jsonObject.getString("err_msg"));
                        } else {
                            LogUtils.showCenterToast(HelpSearchActivuty.this, jsonObject.getInt("err_no") + "" + jsonObject.getString("err_msg"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    LogUtils.showCenterToast(HelpSearchActivuty.this, "数据错误");
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

}
