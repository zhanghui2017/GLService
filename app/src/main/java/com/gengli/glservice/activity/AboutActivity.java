package com.gengli.glservice.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.gengli.glservice.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

@ContentView(R.layout.activity_about)
public class AboutActivity extends BaseActivity {

    @ViewInject(R.id.about_webview)
    private WebView about_webview;

    @ViewInject(R.id.about_title_text)
    private TextView about_title_text;

    private String url ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int web_type = getIntent().getIntExtra("web_type", 0);
        if (web_type == 0){
            url = "http://gengli.test.dxkj.com/page/about";
            about_title_text.setText("关于耿力");
        }else{
            url = "http://gengli.test.dxkj.com/page/protocol";
            about_title_text.setText("用户协议及隐私政策");
        }


        WebSettings settings = about_webview.getSettings();
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setJavaScriptEnabled(true);
        about_webview.setWebViewClient(new AboutActivity.MyWebViewClient());
        about_webview.loadUrl(url);
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
        about_webview.loadUrl("javascript:(function(){"
                + "var objs = document.getElementsByTagName('img'); "
                + "for(var i=0;i<objs.length;i++)  " + "{"
                + "var img = objs[i];   "
                + "    img.style.width = '100%';   "
                + "    img.style.height = 'auto';   "
                + "}" + "})()");
    }

    @Event(value = R.id.about_back_bt, type = View.OnClickListener.class)
    private void backClick(View view) {
        finish();
    }

}
