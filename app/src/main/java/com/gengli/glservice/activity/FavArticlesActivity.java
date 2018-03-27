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
import com.gengli.glservice.adapter.FavArticleAdapter;
import com.gengli.glservice.adapter.ProControlAdapter;
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

@ContentView(R.layout.activity_fav_article)
public class FavArticlesActivity extends BaseActivity {

    private FavArticleAdapter articleAdapter;
    private List<Article> articleList;
    private int page = 1;
    private boolean isShowDel = false;

    @ViewInject(R.id.fav_article_list_view)
    private PullToRefreshListView fav_article_list_view;

    @ViewInject(R.id.order_no_data)
    private LinearLayout order_no_data;

    @ViewInject(R.id.fav_article_edit_bt)
    private TextView fav_article_edit_bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        getActicle(1);
    }

    private void init() {
        articleList = new ArrayList<>();
        articleAdapter = new FavArticleAdapter(this, articleList);
        fav_article_list_view.setAdapter(articleAdapter);
        fav_article_list_view.setMode(PullToRefreshBase.Mode.BOTH);
        fav_article_list_view.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
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

        articleAdapter.setDelClickLintener(new ProControlAdapter.DelClickLintener() {
            @Override
            public void delClick(int position) {
                removeFav(articleList.get(position).getId());
                articleList.remove(position);
                articleAdapter.notifyDataSetChanged();
            }
        });
    }

    @Event(value = R.id.fav_article_back_bt, type = View.OnClickListener.class)
    private void backClick(View view) {
        finish();
    }


    @Event(value = R.id.fav_article_edit_bt)
    private void editClick(View view) {
        if (isShowDel) {
            fav_article_edit_bt.setText("编辑");
            isShowDel = false;
            articleAdapter.isShowDel = false;
            articleAdapter.notifyDataSetChanged();
        } else {
            fav_article_edit_bt.setText("完成");
            isShowDel = true;
            articleAdapter.isShowDel = true;
            articleAdapter.notifyDataSetChanged();
        }
    }


    @Event(value = R.id.fav_article_list_view, type = AdapterView.OnItemClickListener.class)
    private void onListClick(AdapterView<?> parent, View view, int position, long id) {
        int articleid = articleList.get(position - 1).getId();
        Intent intent = new Intent(this, ArticleDetailActivity.class);
        intent.putExtra("articleid", articleid);
        startActivity(intent);
    }


    private void getActicle(final int page) {
        final List<Article> listTemp = new ArrayList<>();
        String url = ServicePort.FAV_LISTS;
        XUtilHttp api = new XUtilHttp();
        Map<String, String> map = new HashMap<>();
        map.put("type", "archive");
        map.put("page", page + "");
        RequestParams params = api.getParam(this, url, map);
        params.addBodyParameter("type", "archive");
        params.addBodyParameter("page", page + "");
        api.post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtils.showLogD("--------收藏列表返回数据:" + result.toString());
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
                                    Article article = new Article();
                                    article.setId(object.getInt("info_id"));
                                    article.setImgUrl(object.getString("thumb"));
                                    article.setTitle(object.getString("title"));
                                    article.setDesc(object.getString("des").toString().trim());
                                    listTemp.add(article);
                                }
                                if (page == 1) {
                                    articleList.clear();
                                    if (listTemp.size() == 0) {
                                        LogUtils.showCenterToast(FavArticlesActivity.this, "没有数据");
                                    } else if (listTemp.size() > 0) {
                                        articleList.addAll(listTemp);
                                        articleAdapter.notifyDataSetChanged();
                                    }
                                } else if (page > 1) {
                                    if (listTemp.size() == 0) {
                                        LogUtils.showCenterToast(FavArticlesActivity.this, "没有更多数据");
                                    } else if (listTemp.size() > 0) {
                                        articleList.addAll(listTemp);
                                        articleAdapter.notifyDataSetChanged();
                                    }
                                }
                            } else {
                                if (page > 1) {
                                    LogUtils.showCenterToast(FavArticlesActivity.this, "没有更多数据");
                                } else {
                                    handler.sendEmptyMessage(1);
                                    LogUtils.showLogD("没有数据");
                                }
                            }
                            fav_article_list_view.onRefreshComplete();
                        } else if (err_no == 2100) {
                            handler.sendEmptyMessage(3);
                            LogUtils.showCenterToast(FavArticlesActivity.this, jsonObject.getInt("err_no") + "" + jsonObject.getString("err_msg"));
                        } else {
                            LogUtils.showCenterToast(FavArticlesActivity.this, jsonObject.getInt("err_no") + "" + jsonObject.getString("err_msg"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    LogUtils.showCenterToast(FavArticlesActivity.this, "数据错误");
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


    /**
     * 取消收藏
     */
    public void removeFav(int articleid) {
        String url = ServicePort.FAV_REMOVE;
        XUtilHttp api = new XUtilHttp();
        Map<String, String> map = new HashMap<>();
        map.put("id", articleid + "");
        RequestParams params = api.getParam(this, url, map);
        params.addBodyParameter("id", articleid + "");
        api.post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String responce = result.toString();
                LogUtils.showLogD("----->取消收藏返回数据----->" + responce);
                if (!TextUtils.isEmpty(responce)) {
                    try {
                        JSONObject jsonObject = new JSONObject(responce);
                        int err_no = jsonObject.getInt("err_no");
                        if (err_no == 0) {
//                            handler.sendEmptyMessage(103);
                            LogUtils.showCenterToast(FavArticlesActivity.this, "取消收藏成功");
                        } else {
                            LogUtils.showCenterToast(FavArticlesActivity.this, jsonObject.getInt("err_no") + "" + jsonObject.getString("err_msg"));
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


    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                order_no_data.setVisibility(View.VISIBLE);
                fav_article_list_view.setVisibility(View.INVISIBLE);
            } else if (msg.what == 2) {
                articleAdapter.notifyDataSetChanged();
            } else if (msg.what == 3) {
                startActivity(new Intent(FavArticlesActivity.this, LoginActivity.class));
            }
        }
    };
}
