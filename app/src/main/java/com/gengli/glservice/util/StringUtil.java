package com.gengli.glservice.util;

import java.util.ArrayList;
import java.util.List;

public class StringUtil {
    /**
     * 将["03796995486","13596854747"]类型的返回值，装换成String[]
     *
     * @return String[]
     */
    public static String[] getPhone(String str) {
        str = str.replace("[", "");
        str = str.replace("]", "");
        String[] a = str.split(",");
        for (int i = 0; i < a.length; i++) {
            a[i] = a[i].replaceAll("\"", "");
            a[i] = a[i].replace("/", "");
        }
        for (int j = 0; j < a.length; j++) {
            a[j] = a[j].trim();
            //			System.out.println("第五步a["+j+"]:"+a[j]);
        }
        return a;
    }

    public static String[] getPhoto(String str) {
        str = str.replace("[", "").trim();
        str = str.replace("]", "").trim();
        String[] a = str.split(",");
        for (int i = 0; i < a.length; i++) {
            a[i] = a[i].replaceAll("\\\\", "").trim();        //去掉转译符\
        }
        for (int j = 0; j < a.length; j++) {
            a[j] = a[j].replace("\"", "").trim();
            //			System.out.println("第四步a["+j+"]:"+a[j]);
        }
        return a;
    }

    /**
     * 将String【】 转换成list<string>
     */
    public static List<String> getListString(String[] str) {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < str.length; i++) {
            list.add(str[i]);
        }
        return list;
    }

    /**
     * 将"黄天,大地,1234,阿斯蒂芬"转换成List<String>
     */
    public static List<String> getListString(String str) {
        List<String> list = new ArrayList<String>();
        String[] arr = str.split(",");
        for (int i = 0; i < arr.length; i++) {
            //			System.out.println("解析后的关键词："+arr[i]);
            list.add(arr[i]);
        }
        return list;
    }

    /**
     * 将string 转换成int
     */
    public static int getStrToInt(String str) {
        int a = 0;
        if (str != null && !str.equals("null") && !str.equals("") && str.length() > 0) {
            return Integer.parseInt(str);
        } else {
            return a;
        }
    }

    public static String subString(String str, int index) {
        String s = String.valueOf(str.charAt(index - 1));
        return s;
    }
}
