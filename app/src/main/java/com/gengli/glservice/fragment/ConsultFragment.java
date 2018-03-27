package com.gengli.glservice.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.gengli.glservice.R;
import com.gengli.glservice.activity.ArticleDetailActivity;
import com.gengli.glservice.activity.ArticleListActivity;
import com.gengli.glservice.activity.LoginActivity;
import com.gengli.glservice.adapter.ArticleAdapter;
import com.gengli.glservice.bean.Article;
import com.gengli.glservice.http.ServicePort;
import com.gengli.glservice.http.XUtilHttp;
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

@ContentView(R.layout.fragment_consult)
public class ConsultFragment extends BaseFragment {
    private ArticleAdapter adapter;
    private List<Article> articleList;
    @ViewInject(R.id.consult_list_view)
    private ListView consult_list_view;

    @Event(value = R.id.consult_list_view, type = AdapterView.OnItemClickListener.class)
    private void onConsultItemClick(AdapterView<?> parent, View view, int position, long id) {
        int articleid = articleList.get(position).getId();
        startActivity(new Intent(getActivity(), ArticleDetailActivity.class).putExtra("articleid", articleid));
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        getTopActicle();
    }

    private void init() {
        articleList = new ArrayList<>();
        adapter = new ArticleAdapter(getActivity(), articleList);
        consult_list_view.setFocusable(false);
        consult_list_view.setAdapter(adapter);
    }

    @Event(value = R.id.consult_bt_1, type = View.OnClickListener.class)
    private void click1(View view) {
        Intent intent = new Intent(getActivity(), ArticleListActivity.class);
        intent.putExtra("article_title", "企业新闻");
        intent.putExtra("article_category_id", 1);
        startActivity(intent);
    }

    @Event(value = R.id.consult_bt_2)
    private void click2(View view) {
        LogUtils.showCenterToast(getActivity(), "视频新闻功能暂未开放");
//        LogUtils.showLogD("----------2-----------");
//        Intent intent = new Intent(getActivity(), ArticleListActivity.class);
//        intent.putExtra("article_title", "视频新闻");
//        intent.putExtra("article_category_id", 2);
//        startActivity(intent);
    }

    @Event(value = R.id.consult_bt_3)
    private void click3(View view) {
        Intent intent = new Intent(getActivity(), ArticleListActivity.class);
        intent.putExtra("article_title", "行业新闻");
        intent.putExtra("article_category_id", 3);
        startActivity(intent);
    }

    @Event(value = R.id.consult_bt_4)
    private void click4(View view) {
        Intent intent = new Intent(getActivity(), ArticleListActivity.class);
        intent.putExtra("article_title", "成功案例");
        intent.putExtra("article_category_id", 4);
        startActivity(intent);
    }


    private void getTopActicle() {
        String url = ServicePort.ARCHIVE_LISTS;
        XUtilHttp api = new XUtilHttp();
        Map<String, String> map = new HashMap<>();
        map.put("is_top", 1 + "");
        map.put("page", 1 + "");
        map.put("size", 5 + "");
        RequestParams params = api.getParam(getActivity(), url, map);
        params.addBodyParameter("is_top", 1 + "");
        params.addBodyParameter("page", 1 + "");
        params.addBodyParameter("size", 5 + "");
        api.post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtils.showLogD("--------热门资讯返回数据:" + result.toString());
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
                                    articleList.add(article);
                                }
                                adapter.notifyDataSetChanged();
                            } else {
                                LogUtils.showCenterToast(getActivity(), "没有数据");
                            }

                        } else if (err_no == 2100) {
                            handler.sendEmptyMessage(3);
                            LogUtils.showCenterToast(getActivity(), jsonObject.getInt("err_no") + "" + jsonObject.getString("err_msg"));
                        } else {
                            LogUtils.showCenterToast(getActivity(), jsonObject.getInt("err_no") + "" + jsonObject.getString("err_msg"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    LogUtils.showCenterToast(getActivity(), "数据错误");
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
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    break;
                case 3:
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    break;
                default:
                    break;
            }
        }
    };

}
