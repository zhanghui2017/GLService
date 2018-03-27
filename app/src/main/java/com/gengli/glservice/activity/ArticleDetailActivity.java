package com.gengli.glservice.activity;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.gengli.glservice.R;
import com.gengli.glservice.http.ServicePort;
import com.gengli.glservice.http.XUtilHttp;
import com.gengli.glservice.util.DatasUtil;
import com.gengli.glservice.util.LogUtils;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.HashMap;
import java.util.Map;

@ContentView(R.layout.activity_article_detail)
public class ArticleDetailActivity extends BaseActivity {
    private int articleid;
    private String loadUrl;
    private String title;
    private String category_name;
    private String create_time;
    private boolean isFav = false;

    @ViewInject(R.id.video_play_view)
    private VideoView video_play_view;

    @ViewInject(R.id.article_title_text)
    private TextView article_title_text;

    @ViewInject(R.id.article_time_text)
    private TextView article_time_text;

    @ViewInject(R.id.artticle_cat_id_text)
    private TextView artticle_cat_id_text;

    @ViewInject(R.id.article_fav_img)
    private ImageView article_fav_img;

    @ViewInject(R.id.article_webview)
    private WebView article_webview;

    private int curId;
    private String videoUrl;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 101:
                    if (isFav)
                        article_fav_img.setImageResource(R.drawable.article_collection_pre);
                    else
                        article_fav_img.setImageResource(R.drawable.article_collection);
                    article_title_text.setText(title);
                    artticle_cat_id_text.setText("类别：" + category_name + "   来自：耿力机械");
                    article_time_text.setText("日期：" + create_time);
                    article_webview.loadUrl(loadUrl);
                    if (!TextUtils.isEmpty(videoUrl)) {
                        video_play_view.setVisibility(View.VISIBLE);
                        Uri uri = Uri.parse(videoUrl);
                        video_play_view.setMediaController(new MediaController(ArticleDetailActivity.this));
                        video_play_view.setVideoURI(uri);
                        video_play_view.start();
                        video_play_view.requestFocus();
                    } else {
                        video_play_view.setVisibility(View.GONE);
                    }
                    break;
                case 102:
                    isFav = true;
                    article_fav_img.setImageResource(R.drawable.article_collection_pre);
                    break;
                case 103:
                    isFav = false;
                    article_fav_img.setImageResource(R.drawable.article_collection);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        articleid = intent.getIntExtra("articleid", -1);
        LogUtils.showLogD("cur articleid === " + articleid);

        WebSettings settings = article_webview.getSettings();
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setJavaScriptEnabled(true);
        article_webview.setWebViewClient(new MyWebViewClient());
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        getArticleDetile();
    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            imgReset();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    private void imgReset() {
        article_webview.loadUrl("javascript:(function(){"
                + "var objs = document.getElementsByTagName('img'); "
                + "for(var i=0;i<objs.length;i++)  " + "{"
                + "var img = objs[i];   "
                + "    img.style.width = '100%';   "
                + "    img.style.height = 'auto';   "
                + "}" + "})()");
    }


    @Event(value = R.id.article_detail_back_bt, type = View.OnClickListener.class)
    private void backClick(View view) {
        finish();
    }


    @Event(value = R.id.article_detail_close_bt)
    private void closeClick(View view) {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }


    @Event(value = R.id.article_fav_img)
    private void favClick(View view) {
        if (DatasUtil.isLogin(this)) {
            if (isFav) {
                removeFav();
            } else {
                addFavour();
            }
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }

    }

    /**
     * 获取文章详情
     */
    private void getArticleDetile() {
        String url = ServicePort.ARCHIVE_DETAIL;
        XUtilHttp api = new XUtilHttp();
        Map<String, String> map = new HashMap<>();
        map.put("id", articleid + "");
        RequestParams params = api.getParam(this, url, map);
        params.addBodyParameter("id", articleid + "");
        api.post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String responce = result.toString();
                LogUtils.showLogD("----->文章详情返回数据----->" + responce);
                if (!TextUtils.isEmpty(responce)) {
                    try {
                        JSONObject jsonObject = new JSONObject(responce);
                        int err_no = jsonObject.getInt("err_no");
                        if (err_no == 0) {
                            JSONObject results = jsonObject.getJSONObject("results");
                            if (!TextUtils.isEmpty(results.toString())) {
                                curId = results.getInt("id");
                                loadUrl = results.getString("h5_url");
                                videoUrl = results.getString("video");
                                isFav = results.getBoolean("is_fav");
                                title = results.getString("title");
                                category_name = results.getString("category_name");
                                create_time = results.getString("create_time");
                                handler.sendEmptyMessage(101);
                            }
                        } else {
                            LogUtils.showCenterToast(ArticleDetailActivity.this, jsonObject.getInt("err_no") + "" + jsonObject.getString("err_msg"));
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

    /**
     * 取消收藏
     */
    public void removeFav() {
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
                            handler.sendEmptyMessage(103);
                            LogUtils.showCenterToast(ArticleDetailActivity.this, "取消收藏成功");
                        } else {
                            LogUtils.showCenterToast(ArticleDetailActivity.this, jsonObject.getInt("err_no") + "" + jsonObject.getString("err_msg"));
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

    /**
     * 添加收藏
     */
    public void addFavour() {
        String url = ServicePort.FAV_ADD;
        XUtilHttp api = new XUtilHttp();
        Map<String, String> map = new HashMap<>();
        map.put("type", "archive");
        map.put("info_id", articleid + "");
        RequestParams params = api.getParam(this, url, map);
        params.addBodyParameter("type", "archive");
        params.addBodyParameter("info_id", articleid + "");
        api.post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String responce = result.toString();
                LogUtils.showLogD("----->收藏返回数据----->" + responce);
                if (!TextUtils.isEmpty(responce)) {
                    try {
                        JSONObject jsonObject = new JSONObject(responce);
                        int err_no = jsonObject.getInt("err_no");
                        if (err_no == 0) {
                            handler.sendEmptyMessage(102);
                            LogUtils.showCenterToast(ArticleDetailActivity.this, "收藏成功");
                        } else {
                            LogUtils.showCenterToast(ArticleDetailActivity.this, jsonObject.getInt("err_no") + "" + jsonObject.getString("err_msg"));
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


}
