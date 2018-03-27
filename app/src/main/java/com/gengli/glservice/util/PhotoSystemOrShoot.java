package com.gengli.glservice.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.gengli.glservice.R;
import com.gengli.glservice.activity.PicEditActivity;

import java.io.File;

/**
 * 拍照选择
 * <p/>
 * Created by zhuwentao on 2016-09-08.
 */
public abstract class PhotoSystemOrShoot {

    /**
     * 打开相册
     */
    public static final int OPEN_IMAGE = 101;

    /**
     * 打开相机
     */
    public static final int OPEN_CAMERA = 102;

    /**
     * 裁剪图片
     */
    public static final int GET_DATA = 103;

    /**
     * 系统图片
     */
    private TextView mSystemPhotoTv;

    /**
     * 手机拍照
     */
    private TextView mShootPhotoTv;

    /**
     * 取消
     */
    private TextView mCancelPhotoTv;

    /**
     * 选择界面
     */
    private LayoutInflater inflater;

    /**
     * 泡泡窗口
     */
    private PopupWindow mPopupWindow;

    private Context mContext;

    /**
     * 对话框布局
     */
    private View mMenuView;

    private String photoPath;
    private Uri imageUri; // 拍照Uri

    public PhotoSystemOrShoot(Context context) {
        this.mContext = context;
        initUI(mContext);
        initListener();
    }

    /**
     * 设置监听事件
     */
    private void initListener() {
        mShootPhotoTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addBitmapShoots();
                closePopupSelect();
            }
        });

        mSystemPhotoTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addBitmapSystem();
                closePopupSelect();
            }
        });

        mCancelPhotoTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closePopupSelect();
            }
        });
    }

    /**
     * 显示图片选择窗口
     */
    public void showPopupSelect(View mView) {
        mPopupWindow.showAtLocation(mView, Gravity.BOTTOM, 0, 0);
    }

    public void closePopupSelect() {
        mPopupWindow.dismiss();
    }

    private void initUI(Context context) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.popup_select_way_photo, null);
        mSystemPhotoTv = (TextView) mMenuView.findViewById(R.id.tv_bitmap_system);
        mShootPhotoTv = (TextView) mMenuView.findViewById(R.id.tv_bitmap_shoot);
        mCancelPhotoTv = (TextView) mMenuView.findViewById(R.id.tv_bitmap_cancel);

        mPopupWindow = new PopupWindow(context);

        // 背景图片透明化
        ColorDrawable dw = new ColorDrawable(0x00000000);
        mPopupWindow.setBackgroundDrawable(dw);
        mPopupWindow.setContentView(mMenuView);
        mPopupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        mPopupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setAnimationStyle(R.style.popup_select_way);
        mPopupWindow.setFocusable(true);

    }

    /**
     * 抽象的回调方法
     *
     * @param intent
     * @param requestCode
     */
    public abstract void onStartActivityForResult(Intent intent, int requestCode);

    /**
     * 相机拍照
     */
    private String pathTakePhoto; // 拍照路径

    private void addBitmapShoots() {

        // 设置图片要保存的 根路径+文件名
        photoPath = PhotoBitmapUtil.getPhotoFileName(mContext);
        File file = new File(photoPath);
        pathTakePhoto = file.toString();
        try {
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        imageUri = Uri.fromFile(file);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        onStartActivityForResult(intent, OPEN_CAMERA);
    }

    /**
     * 本地图片
     */
    private void addBitmapSystem() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        onStartActivityForResult(intent, OPEN_IMAGE);
    }

    /**
     * 获取图片的地址
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public String getPhotoResultPath(int requestCode, int resultCode, Intent data) {

        if (resultCode != Activity.RESULT_OK) {
            return null;
        }
        // 相机返回
        if (requestCode == OPEN_CAMERA) {
            Intent intent = new Intent("com.android.camera.action.CROP"); // 剪裁
            intent.setDataAndType(imageUri, "image/*");
            intent.putExtra("scale", true);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            // 广播刷新相册
            Intent intentBc = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intentBc.setData(imageUri);
            mContext.sendBroadcast(intentBc);
            // 向处理活动传递数据
            Intent intentPut = new Intent(mContext, PicEditActivity.class); // 主活动->处理活动
            intentPut.putExtra("img_path", pathTakePhoto);
            LogUtils.showLogD("..........拍照返回 === " + pathTakePhoto);
            onStartActivityForResult(intentPut, GET_DATA);
        } else if (requestCode == OPEN_IMAGE) { // 相册返回
            Uri uri = data.getData();
            if (!TextUtils.isEmpty(uri.getAuthority())) {
                // 查询选择图片
                Cursor cursor = mContext.getContentResolver().query(uri, new String[]{MediaStore.Images.Media.DATA}, null,
                        null, null);
                if (null == cursor) {
                    return "";
                }
                cursor.moveToFirst();
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                Intent intent = new Intent(mContext, PicEditActivity.class); // 主活动->处理活动
                intent.putExtra("img_path", path);
                LogUtils.showLogD("..........本地返回 === " + path);
                onStartActivityForResult(intent, GET_DATA);
            } else {
                Intent intent = new Intent(mContext, PicEditActivity.class); // 主活动->处理活动
                intent.putExtra("img_path", uri.getPath());
                onStartActivityForResult(intent, GET_DATA);
            }
        } else if (requestCode == GET_DATA) {  // 裁剪图片
            String path = data.getStringExtra("pathProcess");
            if (path != null) {
                return path;
            } else {
                return null;
            }
        }

        return null;
    }


}
