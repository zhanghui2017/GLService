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
import com.gengli.glservice.bean.Article;
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

@ContentView(R.layout.activity_article_list)
public class ArticleListActivity extends BaseActivity {

    private ArticleAdapter articleAdapter;
    private List<Article> articleList;
    private int page = 1;
    private int category_id = 0;

    @ViewInject(R.id.article_list_view)
    private PullToRefreshListView article_list_view;

    @ViewInject(R.id.order_no_data)
    private LinearLayout order_no_data;

    @ViewInject(R.id.article_list_title_text)
    private TextView article_list_title_text;
    private String title;
    private String keyWord = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title = getIntent().getStringExtra("article_title");
        category_id = getIntent().getIntExtra("article_category_id", 0);
        keyWord = getIntent().getStringExtra("key_word");
        init();
        getActicle(1);
    }

    private void init() {
        article_list_title_text.setText(title);
        articleList = new ArrayList<>();
        articleAdapter = new ArticleAdapter(this, articleList);
        article_list_view.setAdapter(articleAdapter);
        article_list_view.setMode(PullToRefreshBase.Mode.BOTH);
        article_list_view.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                page = 1;
                getActicle(1);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page++;
                getActicle(page);
            }
        });
    }

    @Event(value = R.id.article_list_back_bt, type = View.OnClickListener.class)
    private void backClick(View view) {
        finish();
    }


    @Event(value = R.id.article_list_view, type = AdapterView.OnItemClickListener.class)
    private void onListClick(AdapterView<?> parent, View view, int position, long id) {
        int articleid = articleList.get(position - 1).getId();
        Intent intent = new Intent(this, ArticleDetailActivity.class);
        intent.putExtra("articleid", articleid);
        startActivity(intent);
    }


    private void getActicle(final int page) {
        final List<Article> listTemp = new ArrayList<>();
        String url = ServicePort.ARCHIVE_LISTS;
        XUtilHttp api = new XUtilHttp();
        Map<String, String> map = new HashMap<>();
        LogUtils.showLogD("----------------------" + category_id);
        map.put("category_id", category_id + "");
        if (!TextUtils.isEmpty(keyWord)) {
            map.put("keyword", keyWord);
        }
        map.put("page", page + "");
        map.put("size", 10 + "");
        RequestParams params = api.getParam(this, url, map);
        params.addBodyParameter("category_id", category_id + "");
        if (!TextUtils.isEmpty(keyWord)) {
            params.addBodyParameter("keyword", keyWord);
        }

        params.addBodyParameter("page", page + "");
        params.addBodyParameter("size", 10 + "");
        api.post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtils.showLogD("--------文章列表返回数据:" + result.toString());
                String responce = result.toString();
                if (!TextUtils.isEmpty(responce)) {
                    try {
                        JSONObject jsonObject = new JSONObject(responce);
                        int err_no = jsonObject.getInt("err_no");
                        if (err_no == 0) {
                            JSONObject results = jsonObject.getJSONObject("results");
                            JSONArray list = results.getJSONArray("list");
                            if (list != null && list.length() > 0) {
                                for (int i = 0; i < list.length(); i++) {
                                    JSONObject object = list.getJSONObject(i);
                                    Article article = new Article();
                                    article.setId(object.getInt("id"));
                                    article.setImgUrl(object.getString("thumb"));
                                    article.setTitle(object.getString("title"));
                                    article.setDesc(object.getString("des").toString().trim());
                                    listTemp.add(article);
                                }
                                if (page == 1) {
                                    articleList.clear();
                                    if (listTemp.size() == 0) {
                                        LogUtils.showCenterToast(ArticleListActivity.this, "没有数据");
                                    } else if (listTemp.size() > 0) {
                                        articleList.addAll(listTemp);
                                        articleAdapter.notifyDataSetChanged();
                                    }
                                } else if (page > 1) {
                                    if (listTemp.size() == 0) {
                                        LogUtils.showCenterToast(ArticleListActivity.this, "没有更多数据");
                                    } else if (listTemp.size() > 0) {
                                        articleList.addAll(listTemp);
                                        articleAdapter.notifyDataSetChanged();
                                    }
                                }
                            } else {
                                if (page > 1) {
                                    LogUtils.showCenterToast(ArticleListActivity.this, "没有更多数据");
                                } else {
                                    handler.sendEmptyMessage(1);
                                    LogUtils.showLogD("没有数据");
                                }
                            }
                            article_list_view.onRefreshComplete();
                        } else if (err_no == 2100) {
                            handler.sendEmptyMessage(3);
                            LogUtils.showCenterToast(ArticleListActivity.this, jsonObject.getInt("err_no") + "" + jsonObject.getString("err_msg"));
                        } else {
                            LogUtils.showCenterToast(ArticleListActivity.this, jsonObject.getInt("err_no") + "" + jsonObject.getString("err_msg"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    LogUtils.showCenterToast(ArticleListActivity.this, "数据错误");
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
                article_list_view.setVisibility(View.INVISIBLE);
            } else if (msg.what == 2) {
                articleAdapter.notifyDataSetChanged();
            } else if (msg.what == 3) {
                startActivity(new Intent(ArticleListActivity.this, LoginActivity.class));
            }
        }
    };
}
