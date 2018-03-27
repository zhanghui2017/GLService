package com.gengli.glservice.http;

import android.content.Context;
import android.util.Log;

import com.gengli.glservice.util.DatasUtil;
import com.gengli.glservice.util.LogUtils;
import com.gengli.glservice.util.MD5Utils;
import com.gengli.glservice.util.SystemMsgUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class XUtilHttp {
    private static String SKEY = "69G4M}Dvc>k+U0BpBXb+s[{oWj{^sHY7";
    private static String KEY = "Jswd~WsxYxyz!Shn";
    private static String loc = "0,0";
    private static String os = "android";
    private static RequestParams params;


    public String getSign(Context context, String timestamp, Map<String, String> funs) {
        String cip = SystemMsgUtil.getIPAddress(context);
        String sv = String.valueOf(SystemMsgUtil.getVersionName(context));
        String sessionid = DatasUtil.getUserInfo(context, "sessionid");
        if (sessionid == "") {
            sessionid = "111";
        }
        StringBuilder signBuilder = new StringBuilder();
        Map<String, String> signs = new HashMap<>();
        signs.put("cip", cip);
        signs.put("sv", sv);
        signs.put("os", os);
        signs.put("sessionid", sessionid);
        signs.put("loc", loc);
        signs.put("key", KEY);
        signs.put("timestamp", timestamp);
        Set<Map.Entry<String, String>> entrySet = funs.entrySet();
        for (Map.Entry<String, String> entry : entrySet) {
            signs.put(entry.getKey(), entry.getValue());
        }
        List<Map.Entry<String, String>> mapList = new ArrayList<>(signs.entrySet());
        Collections.sort(mapList, new Comparator<Map.Entry<String, String>>() {
            public int compare(Map.Entry<String, String> map1, Map.Entry<String, String> map2) {
                return map1.getKey().compareTo(map2.getKey());
            }
        });
        for (int i = 0; i < mapList.size(); i++) {
            Map.Entry<String, String> entrys = mapList.get(i);
//            LogUtils.showLogD(" ------------->" + entrys.getKey() + "-------------" + entrys.getValue());
            signBuilder.append(entrys.getValue());
        }

        String sign = MD5Utils.toMd5(MD5Utils.toMd5(signBuilder.toString()) + SKEY);
        return sign;
    }

    public RequestParams getParam(Context context, String url, Map<String, String> funs) {
        String timestamp = System.currentTimeMillis() / 1000 + "";
        String cip = SystemMsgUtil.getIPAddress(context);
        String sv = String.valueOf(SystemMsgUtil.getVersionName(context));
        String sessionid = DatasUtil.getUserInfo(context, "sessionid");
        if (sessionid == "") {
            sessionid = "111";
        }

        params = new RequestParams(url);
        params.addParameter("cip", cip);
        params.addParameter("key", KEY);
        params.addParameter("loc", loc);
        params.addParameter("sessionid", sessionid);
        params.addParameter("sign", getSign(context, timestamp, funs));
        params.addParameter("sv", sv);
        params.addParameter("os", os);
        params.addParameter("timestamp", timestamp);

        return params;
    }

    /**
     * get请求 无AjaxParams params
     *
     * @param callBack
     */
    public void post(RequestParams params, Callback.CommonCallback<String> callBack) {
        x.http().post(params, callBack);
    }

    public void post(RequestParams params, Callback.ProgressCallback<File> callback){
        x.http().post(params, callback);
    }

}
