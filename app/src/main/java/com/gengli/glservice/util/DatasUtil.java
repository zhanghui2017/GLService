package com.gengli.glservice.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: DatasUtil
 * @Description: TODO 工具类，获取数据
 */
public class DatasUtil {

    /**
     * 判断是否已登录
     */
    public static boolean isLogin(Context context) {
        boolean loginState = false;
        SharedPreferences preferences = context.getSharedPreferences("GLUser", Activity.MODE_PRIVATE);
        boolean state = preferences.getBoolean("LoginState", false);
        LogUtils.showLogD("---->登录状态---->" + state);
        if (state == true) {
            loginState = true;
        }
        return loginState;
    }

    /**
     * 清除用户信息
     */
    public static void cleanUserData(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("GLUser", Activity.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.remove("LoginState");
        editor.remove("sessionid");
        editor.remove("company_id");
        editor.remove("unit");
        editor.remove("tel");
        editor.remove("mobile");
        editor.remove("realname");
        editor.remove("username");
        editor.remove("gender");
        editor.remove("prov");
        editor.remove("prov_name");
        editor.remove("city");
        editor.remove("city_name");
        editor.remove("birthday");
        editor.remove("avatar");

        editor.remove("account");
        editor.remove("password");

        editor.commit();
    }


    /**
     * TODO 获取用户USER 存储信息
     */
    public static String getUserInfo(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences("GLUser", Activity.MODE_PRIVATE);
        String value = preferences.getString(key, "");
        return value;
    }

    /**
     * TODO 获取用户USER 存储信息
     */
    public static int getUserInfoInt(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences("GLUser", Activity.MODE_PRIVATE);
        int value = preferences.getInt(key, -1);
        return value;
    }

    /**
     * TODO 更新User数据
     */
    public static void changeUserState(Context context, String key, boolean value) {
        SharedPreferences preferences = context.getSharedPreferences("GLUser", Activity.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static void changeUserString(Context context, String key, String value) {
        SharedPreferences preferences = context.getSharedPreferences("GLUser", Activity.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * TODO 搜索关键词记录
     */
    public static void storeSearchKeyWord(Context context, String strArray) {
        SharedPreferences sp = context.getSharedPreferences("SearchKeyWord", Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString("keyWordHistroy", strArray.toString());
        editor.commit();
    }

    /**
     * TODO 获取关键词记录
     */
    public static List<String> getSearchKeyWord(Context context) {
        SharedPreferences sp = context.getSharedPreferences("SearchKeyWord", Context.MODE_PRIVATE);
        String str = sp.getString("keyWordHistroy", "");
        LogUtils.showLogD(" ---->关键词历史记录---->" + str);
        List<String> list = new ArrayList<String>();
        if (!str.equals("")) {
            System.out.println("list is not null");
            list = StringUtil.getListString(str);
            return list;
        }
        return list;
    }

    /**
     * TODO 清除关键词记录
     */
    public static void clearSearchKeyWord(Context context) {
        SharedPreferences sp = context.getSharedPreferences("SearchKeyWord", Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.remove("keyWordHistroy");
        editor.commit();
    }

}
