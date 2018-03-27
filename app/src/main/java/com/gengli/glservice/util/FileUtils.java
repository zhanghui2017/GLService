package com.gengli.glservice.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * SD卡相关的辅助类
 */
public class FileUtils {
    private static final String HYPHOTO_FILES_NAME = "GengLi";

    private FileUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 判断SDCard是否可用
     *
     * @return
     */
    public static boolean isSDCardEnable() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);

    }

    /**
     * 获取SD卡路径
     *
     * @return
     */
    public static String getSDCardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator;
    }

    public static String getRootPath() {
        return getSDCardPath() + HYPHOTO_FILES_NAME;
    }

    /**
     * 获取手机可存储路径
     *
     * @param context 上下文
     * @return 手机可存储路径
     */
    public static String getPhoneRootPath(Context context) {
        // 是否有SD卡
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)
                || !Environment.isExternalStorageRemovable()) {
            // 获取SD卡根目录
            return context.getExternalCacheDir().getPath();
        } else {
            // 获取apk安装缓存路径
            return context.getCacheDir().getPath();
        }
    }

    public static File BitmapToFile(Context context, Bitmap bitmap) {
        String sdPath = getSDCardPath() + HYPHOTO_FILES_NAME;
//        File file = new File(getPhoneRootPath(context) + HYPHOTO_FILES_NAME , "header.jpg");
        File file = new File(getSDCardPath() + HYPHOTO_FILES_NAME);
        if (file.exists()) {
            file.delete();
        }
        file.mkdir();
        String path = sdPath + "/header.jpg";
        File headerFile = new File(path);
        try {
            headerFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(headerFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return headerFile;
    }
}
