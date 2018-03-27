package com.gengli.glservice.util;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 功能描述：常量工具类
 */
public class Util {

    public static String TEMP_ONE = "yyyy-MM-dd HH:mm:ss";
    public static String TEMP_TWO = "yyyy年MM月DD日 HH:mm";

    /**
     * 得到设备屏幕的宽度
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 得到设备屏幕的高度
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 得到设备的密度
     */
    public static float getScreenDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    /**
     * 把密度转换为像素
     */
    public static int dip2px(Context context, float px) {
        final float scale = getScreenDensity(context);
        return (int) (px * scale + 0.5);
    }

    /**
     * 获取系统时间
     * 暂未使用
     */
    public static String getCurrentTime(String format) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        String currentTime = sdf.format(date);
        return currentTime;
    }

    /**
     * 获取系统时间戳前10位
     *
     * @return string
     */
    public static String getCurrentTimeTop10() {
        String temp = System.currentTimeMillis() + "";
        temp = temp.substring(0, 10);
        System.out.println("时间戳：" + temp);
        return temp;
    }

    /**
     * 获取系统时间
     * 时间格式yyyyMM
     */
    public static String getCurrentTime() {
        return getCurrentTime("yyyyMM");
    }

    /**
     * 日期格式字符串转换成时间戳
     *
     * @param format 如：yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String date2TimeStamp(String date_str, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return String.valueOf(sdf.parse(date_str).getTime() / 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    public static String getPackageName(Context context) {
        return context.getPackageName();
    }

    /**
     * TODO 版本信息转换为整数
     */
    public static int stringToInt(String version) {
        System.out.println("带转换的版本号：" + version);
        String ver1 = version.replace(".", "");
        int v1 = Integer.parseInt(ver1);
        System.out.println("转换后的版本号对应的数字：" + v1);
        return v1;
    }

    /**
     * 版本信息转换为整数数组
     */
    public static int[] chartStr(String str) {
        System.out.println("要切割的str=" + str);
        String[] st = null;
        st = str.split("\\.");
        int[] s = new int[st.length];
        for (int i = 0; i < st.length; i++) {
            String item = st[i];
            s[i] = Integer.parseInt(item);
        }
        return s;
    }

    /**
     * 用户版本号比较
     * a > b 返回true
     */
    public static boolean aVb(int[] a, int[] b) {
        boolean flag = false;
        if (a.length == b.length || a.length > b.length) {
            System.out.println("a.length >= b.length");
            for (int i = 0; i < b.length; i++) {
                System.out.println("比较次数：" + (i + 1) + "比较对象：" + a[i] + "<-->" + b[i]);
                if (a[i] != b[i]) {
                    if (a[i] > b[i]) {
                        flag = true;
                        return flag;
                    } else {
                        flag = false;
                        return flag;
                    }
                }
            }
        } else {
            System.out.println("a.length < b.length 或者相等");
            for (int i = 0; i < a.length; i++) {
                System.out.println("比较次数：" + (i + 1) + "比较对象：" + a[i] + "<-->" + b[i]);
                if (a[i] != b[i]) {
                    if (a[i] > b[i]) {
                        flag = true;
                        return flag;
                    } else {
                        flag = false;
                        return flag;
                    }
                }
            }
        }
        return flag;
    }

    /**
     * 时间差计算， 返回几分钟，几小时，几天，几月信息。时间格式 "yyyy-MM-dd HH:mm:ss"
     *
     * @param oldtime 过期的时间
     * @param nowtime 当前时间
     */
    public static String getDatePoor(String oldtime, String nowtime) throws ParseException {
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        // long ns = 1000;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date endDate = sdf.parse(oldtime);
        Date nowDate = sdf.parse(nowtime);
        // 获得两个时间的毫秒时间差异
        long diff = nowDate.getTime() - endDate.getTime();
        // 计算差多少天
        int day = (int) (diff / nd);
        // 计算差多少小时
        int hour = (int) (diff % nd / nh);
        // 计算差多少分钟
        int min = (int) (diff % nd % nh / nm);
        // 计算差多少秒//输出结果
        // long sec = diff % nd % nh % nm / ns;
//		System.out.println(" diff = "+ diff);
//		System.out.println(" day = "+ day);
//		System.out.println(" hour = "+ hour);
//		System.out.println(" min = "+ min);
        String str = "";
        if (day == 0 && hour == 0) {
            if (min < 6) {
                str = "刚刚";
            } else if (min < 31) {

                str = "半小时前";
            } else if (min < 60) {
                str = "1小时前";
            }
        } else if (day == 0 && hour > 0) {
            if (hour < 13) {
                str = "半天前";
            } else if (hour > 12 && hour < 24) {
                str = "1天内";
            }
        } else if (day > 0) {
            if (day < 7) {
                str = "1周内";
            } else if (day > 7 && day < 15) {

                str = "半月内";
            } else if (day > 14 && day < 30) {
                str = "1月内";
            } else {
                str = oldtime.substring(0, oldtime.indexOf(" "));
            }
        }
        //        return "有效期："+day + "天" + hour + "小时" + min + "分钟";
        return str;
    }

    public static int setColor(Context context, int color) {
        return ContextCompat.getColor(context, color);
    }


    public static int getTimeDifference(String starTime, String endTime) {
        String timeString = "";
        int td = 0;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        try {
            Date parse = dateFormat.parse(starTime);
            Date parse1 = dateFormat.parse(endTime);

            long diff = parse1.getTime() - parse.getTime();

            long day = diff / (24 * 60 * 60 * 1000);
            long hour = (diff / (60 * 60 * 1000) - day * 24);
            long min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
            long s = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
            long ms = (diff - day * 24 * 60 * 60 * 1000 - hour * 60 * 60 * 1000
                    - min * 60 * 1000 - s * 1000);
            // System.out.println(day + "天" + hour + "小时" + min + "分" + s +
            // "秒");
            long hour1 = diff / (60 * 60 * 1000);
            String hourString = hour1 + "";
            long min1 = ((diff / (60 * 1000)) - hour1 * 60);
            timeString = hour1 + "小时" + min1 + "分";
            // System.out.println(day + "天" + hour + "小时" + min + "分" + s +
            // "秒");
            td = (int) day / 30;
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return td;

    }

    /**
     * 版本号比较 * * @param version1 * @param version2 * @return
     */
    public static int compareVersion(String version1, String version2) {
        if (version1.equals(version2)) {
            return 0;
        }
        String[] version1Array = version1.split("\\.");
        String[] version2Array = version2.split("\\.");
        int index = 0;
        int minLen = Math.min(version1Array.length, version2Array.length);
        int diff = 0;
        while (index < minLen && (diff = Integer.parseInt(version1Array[index]) - Integer.parseInt(version2Array[index])) == 0) {
            index++;
        }
        if (diff == 0) { // 如果位数不一致，比较多余位数
            for (int i = index; i < version1Array.length; i++) {
                if (Integer.parseInt(version1Array[i]) > 0) {
                    return 1;
                }
            }
            for (int i = index; i < version2Array.length; i++) {
                if (Integer.parseInt(version2Array[i]) > 0) {
                    return -1;
                }
            }
            return 0;
        } else {
            return diff > 0 ? 1 : -1;
        }
    }

}
