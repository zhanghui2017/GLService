package com.gengli.glservice;

import android.app.Application;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.gengli.glservice.broadcast.NetCheckReceiver;

import org.xutils.x;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by wyouflf on 15/10/28.
 */
public class MyApplication extends Application {
    private NetCheckReceiver mReceiver;
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG); // 开启debug会影响性能

        //极光推送
        JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(getApplicationContext());     		// 初始化 JPush

        //注册广播接受者用于监听网络状态
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mReceiver = new NetCheckReceiver();
        registerReceiver(mReceiver, mFilter);

        // 全局默认信任所有https域名 或 仅添加信任的https域名
        // 使用RequestParams#setHostnameVerifier(...)方法可设置单次请求的域名校验
//        x.Ext.setDefaultHostnameVerifier(new HostnameVerifier() {
//            @Override
//            public boolean verify(String hostname, SSLSession session) {
//                return true;
//            }
//        });
    }
}
