package com.gengli.glservice.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.gengli.glservice.R;
import com.gengli.glservice.http.ServicePort;
import com.gengli.glservice.http.XUtilHttp;
import com.gengli.glservice.util.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.HashMap;
import java.util.Map;

@ContentView(R.layout.activity_after_sale)
public class AfterSaleActivity extends BaseActivity {

    @ViewInject(R.id.after_sale_webview)
    private WebView after_sale_webview;

    private String serUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WebSettings settings = after_sale_webview.getSettings();
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setJavaScriptEnabled(true);
        after_sale_webview.setWebViewClient(new MyWebViewClient());
        getUrl();
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
        after_sale_webview.loadUrl("javascript:(function(){"
                + "var objs = document.getElementsByTagName('img'); "
                + "for(var i=0;i<objs.length;i++)  " + "{"
                + "var img = objs[i];   "
                + "    img.style.width = '100%';   "
                + "    img.style.height = 'auto';   "
                + "}" + "})()");
    }

    @Event(value = R.id.after_sale_back_bt, type = View.OnClickListener.class)
    private void backClick(View view) {
        finish();
    }


    private void getUrl() {
        String url = ServicePort.PRODUCT_SERVICE;
        XUtilHttp api = new XUtilHttp();
        Map<String, String> map = new HashMap<>();
        RequestParams params = api.getParam(this, url, map);
        api.post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String responce = result.toString();
                LogUtils.showLogD("----->售后服务返回数据----->" + responce);
                if (!TextUtils.isEmpty(responce)) {
                    try {
                        JSONObject jsonObject = new JSONObject(responce);
                        int err_no = jsonObject.getInt("err_no");
                        if (err_no == 0) {
                            JSONObject results = jsonObject.getJSONObject("results");
                            serUrl = results.getString("h5_url");
                            handler.sendEmptyMessage(1);
                        } else {
                            LogUtils.showCenterToast(AfterSaleActivity.this, jsonObject.getInt("err_no") + "" + jsonObject.getString("err_msg"));
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
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    after_sale_webview.loadUrl(serUrl);
                    break;
            }
        }
    };
}
