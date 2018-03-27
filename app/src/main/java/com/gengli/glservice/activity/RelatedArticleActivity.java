package com.gengli.glservice.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.gengli.glservice.R;
import com.gengli.glservice.adapter.ArticleAdapter;
import com.gengli.glservice.adapter.RelatedFitAdapter;
import com.gengli.glservice.bean.Article;
import com.gengli.glservice.bean.Fitting;
import com.gengli.glservice.bean.Product;
import com.gengli.glservice.http.ServicePort;
import com.gengli.glservice.http.XUtilHttp;
import com.gengli.glservice.util.LogUtils;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ContentView(R.layout.activity_related_article)
public class RelatedArticleActivity extends BaseActivity {
    private Product product;

    private ArticleAdapter articleAdapter;
    private List<Article> articleList;
    @ViewInject(R.id.related_article_list)
    private ListView related_article_list;

    @ViewInject(R.id.related_article_pro_img)
    private ImageView related_article_pro_img;

    @ViewInject(R.id.related_article_pro_type_new_img)
    private ImageView related_article_pro_type_new_img;

    @ViewInject(R.id.related_article_pro_type_hot_img)
    private ImageView related_article_pro_type_hot_img;

    @ViewInject(R.id.related_article_pro_name)
    private TextView related_article_pro_name;

    @ViewInject(R.id.related_article_pro_des)
    private TextView related_article_pro_des;

    @ViewInject(R.id.order_no_data)
    private LinearLayout order_no_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        product = (Product) getIntent().getSerializableExtra("control_product");
        articleList = new ArrayList<>();
        articleAdapter = new ArticleAdapter(this, articleList);
        related_article_list.setAdapter(articleAdapter);

        getProductDetail();
        ImageOptions imageOptions = new ImageOptions.Builder()
                .setCircular(true)
                .setLoadingDrawableId(R.mipmap.ic_launcher)
                .setFailureDrawableId(R.mipmap.ic_launcher)
                .build();
        x.image().bind(related_article_pro_img, product.getImgUrl(), imageOptions);
        related_article_pro_name.setText(product.getName());
        related_article_pro_des.setText(product.getDesc());
    }

    @Event(value = R.id.related_article_list, type = AdapterView.OnItemClickListener.class)
    private void onListClick(AdapterView<?> parent, View view, int position, long id) {
        int articleid = articleList.get(position).getId();
        Intent intent = new Intent(this, ArticleDetailActivity.class);
        intent.putExtra("articleid", articleid);
        startActivity(intent);
    }

    @Event(value = R.id.related_article_back_bt, type = View.OnClickListener.class)
    private void backClick(View view) {
        finish();
    }

    @Event(value = R.id.related_article_close_bt)
    private void closeClick(View view) {
        finish();
    }


    private void getProductDetail() {
        String url = ServicePort.PRODUCT_DETAIL;
        XUtilHttp api = new XUtilHttp();
        Map<String, String> map = new HashMap<>();
        map.put("id", product.getId() + "");
        RequestParams params = api.getParam(this, url, map);
        params.addBodyParameter("id", product.getId() + "");
        api.post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtils.showLogI("--------产品咨询返回数据:" + result.toString());
                String responce = result.toString();
                if (!TextUtils.isEmpty(responce)) {
                    try {
                        JSONObject jsonObject = new JSONObject(responce);
                        int err_no = jsonObject.getInt("err_no");
                        if (err_no == 0) {
                            JSONObject results = jsonObject.getJSONObject("results");
                            JSONArray archives = results.getJSONArray("archives");
                            if (archives.length() > 0) {
                                for (int i = 0; i < archives.length(); i++) {
                                    JSONObject object = archives.getJSONObject(i);
                                    Article article = new Article();
                                    article.setId(object.getInt("id"));
                                    article.setImgUrl(object.getString("thumb"));
                                    article.setTitle(object.getString("title"));
                                    article.setDesc(object.getString("des").toString().trim());
                                    articleList.add(article);
                                }
                                articleAdapter.notifyDataSetChanged();
                            } else {
                                handler.sendEmptyMessage(1);
                            }

                        } else if (err_no == 2100) {
                            handler.sendEmptyMessage(3);
                            LogUtils.showCenterToast(RelatedArticleActivity.this, jsonObject.getInt("err_no") + "" + jsonObject.getString("err_msg"));
                        } else {
                            LogUtils.showCenterToast(RelatedArticleActivity.this, jsonObject.getInt("err_no") + "" + jsonObject.getString("err_msg"));
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

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO 自动生成的方法存根
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    related_article_list.setVisibility(View.INVISIBLE);
                    order_no_data.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    startActivity(new Intent(RelatedArticleActivity.this, LoginActivity.class));
                    break;
                default:
                    break;
            }
        }
    };
}
