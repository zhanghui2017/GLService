package com.gengli.glservice.util;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

public class LogUtils {
    private static final String TAG = "logGL";

    /**
     * 居中显示toast，显示时间短
     *
     * @param context,string
     */
    public static void showCenterToast(Context context, String msg) {
        if (msg == null) {
            msg = "";
        }
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
//        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    /**
     * 居中显示toast，显示时间长
     *
     * @param context,string
     */
    public static void showCenterToastLong(Context context, String msg) {
        if (msg == null) {
            msg = "";
        }
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    /**
     * 显示Log.e(),错误信息
     */
    public static void showLogD(String msg) {
        Log.d(TAG, msg);
    }

    public static void showLogI(String msg) {  //信息太长,分段打印
        //因为String的length是字符数量不是字节数量所以为了防止中文字符过多，
        //  把4*1024的MAX字节打印长度改为2001字符数
        int max_str_length = 2001 - TAG.length();
        //大于4000时
        while (msg.length() > max_str_length) {
            Log.i(TAG, msg.substring(0, max_str_length));
            msg = msg.substring(max_str_length);
        }
        //剩余部分
        Log.i(TAG, msg);
    }
}
