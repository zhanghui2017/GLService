package com.gengli.glservice.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.gengli.glservice.R;
import com.gengli.glservice.bean.Category;
import com.gengli.glservice.http.ServicePort;
import com.gengli.glservice.http.XUtilHttp;
import com.gengli.glservice.util.DatasUtil;
import com.gengli.glservice.util.FileUtils;
import com.gengli.glservice.util.LogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@ContentView(R.layout.activity_company_info)
public class CompanyInfoActivity extends BaseActivity implements View.OnClickListener {

    @ViewInject(R.id.company_info_header_img)
    private ImageView company_info_header_img;
    private PopupWindow headerPopupWindow;
    @ViewInject(R.id.take_photos_bt)
    private TextView take_photos_bt;
    @ViewInject(R.id.getpic_from_sd)
    private TextView getpic_from_sd;
    @ViewInject(R.id.popup_cancle_bt)
    private TextView popup_cancle_bt;

    private static final String PHOTO_FILE_NAME = "header.jpg";
    private File tempFile;
    private static final int PHOTO_REQUEST_CAREMA = 1;
    private static final int PHOTO_REQUEST_GALLERY = 2;
    private static final int PHOTO_REQUEST_CUT = 3;

    private Bitmap bitmap;
    private ImageOptions imageOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String headUrl = DatasUtil.getUserInfo(this, "avatar");
        imageOptions = new ImageOptions.Builder()
                .setCircular(true)
                .setLoadingDrawableId(R.mipmap.ic_launcher)
                .setFailureDrawableId(R.mipmap.ic_launcher)
                .build();
        x.image().bind(company_info_header_img, headUrl, imageOptions);

    }

    @Event(value = R.id.company_info_back_bt, type = View.OnClickListener.class)
    private void backClick(View view) {
        finish();
    }

    @Event(value = R.id.company_info_header_bt)
    private void headerClick(View view) {
        getHeadPopup();
        headerPopupWindow.setAnimationStyle(R.style.popup_select_way);
        headerPopupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }

    @Event(value = R.id.company_info_address_bt)
    private void addressClick(View view) {
        String address = DatasUtil.getUserInfo(this, "address");
        startActivity(new Intent(this, ChangeInfoActivity.class).putExtra("change_info", address).putExtra("change_type", 1));
    }

    @Event(value = R.id.company_info_phone_bt)
    private void phoneClick(View view) {
        String mobile = DatasUtil.getUserInfo(this, "mobile");
        startActivity(new Intent(this, ChangeInfoActivity.class).putExtra("change_info", mobile).putExtra("change_type", 1));
    }

    @Event(value = R.id.company_info_scale_bt)
    private void scaleClick(View view) {

    }

    @Event(value = R.id.company_info_email_bt)
    private void emailClick(View view) {

    }

    @Event(value = R.id.company_info_logout_bt)
    private void logoutClick(View view) {
        logout();
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Intent intent = new Intent(CompanyInfoActivity.this, LoginActivity.class);
                startActivity(intent);
                DatasUtil.cleanUserData(CompanyInfoActivity.this);
                finish();
            } else if (msg.what == 2) {
                String headUrl = DatasUtil.getUserInfo(CompanyInfoActivity.this, "avatar");
                x.image().bind(company_info_header_img, headUrl, imageOptions);
            }
        }
    };


    private void logout() {
        String url = ServicePort.ACCOUNT_LOGOUT;
        XUtilHttp api = new XUtilHttp();
        Map<String, String> map = new HashMap<>();
        RequestParams params = api.getParam(this, url, map);
        api.post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String responce = result.toString();
                LogUtils.showLogD(" 退出登录返回数据：" + responce);
                if (!TextUtils.isEmpty(responce)) {
                    try {
                        JSONObject jsonObject = new JSONObject(responce);
                        int err_no = jsonObject.getInt("err_no");
                        if (err_no == 0) {
                            handler.sendEmptyMessage(1);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
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

    private void getHeadPopup() {
        if (headerPopupWindow != null) {
            headerPopupWindow.dismiss();
            return;
        } else {
            heanderPopupWindow();
        }
    }

    public void heanderPopupWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View headPopup = layoutInflater.inflate(R.layout.popup_photo_files, null);

        take_photos_bt = (TextView) headPopup.findViewById(R.id.take_photos_bt);
        getpic_from_sd = (TextView) headPopup.findViewById(R.id.getpic_from_sd);
        popup_cancle_bt = (TextView) headPopup.findViewById(R.id.popup_cancle_bt);

        take_photos_bt.setOnClickListener(this);
        getpic_from_sd.setOnClickListener(this);
        popup_cancle_bt.setOnClickListener(this);

        headerPopupWindow = new PopupWindow(headPopup, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        headerPopupWindow.setFocusable(true);
        headerPopupWindow.setBackgroundDrawable(new BitmapDrawable());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.take_photos_bt:
                takePhotos();
                headerPopupWindow.dismiss();
                break;
            case R.id.getpic_from_sd:
                getFromCd();
                headerPopupWindow.dismiss();
                break;
            case R.id.popup_cancle_bt:
                headerPopupWindow.dismiss();
                break;
        }
    }


    /**
     * 拍照
     */
    private void takePhotos() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            tempFile = new File(Environment.getExternalStorageDirectory(), PHOTO_FILE_NAME);
            Uri uri = Uri.fromFile(tempFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(intent, PHOTO_REQUEST_CAREMA);
        } else {
            Toast.makeText(this, "未找到存储卡，无法存储照片！", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 文件获取
     */
    private void getFromCd() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
    }

    /**
     * 回调
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHOTO_REQUEST_GALLERY) {        //本地获取图片
            if (data != null) {
                Uri uri = data.getData();
                crop(uri);
            }

        } else if (requestCode == PHOTO_REQUEST_CAREMA) {//拍照
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                crop(Uri.fromFile(tempFile));
            } else {
                Toast.makeText(this, "未找到存储卡，无法存储照片！", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PHOTO_REQUEST_CUT) {    //剪切图片
            if (data != null) {
                bitmap = data.getParcelableExtra("data");
                uploadHeader();

            }
            try {
                if (tempFile != null && tempFile.exists())
                    tempFile.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void crop(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("outputFormat", "JPEG");
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }


    private void uploadHeader() {
        String url = ServicePort.ACCOUNT_MODIFY;
        XUtilHttp api = new XUtilHttp();
        Map<String, String> map = new HashMap<>();
        RequestParams params = api.getParam(this, url, map);
        params.addBodyParameter("avatar", FileUtils.BitmapToFile(this, bitmap));
        api.post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String responce = result.toString();
                LogUtils.showLogD("修改头像返回 === " + responce);
                if (!TextUtils.isEmpty(responce)) {
                    try {
                        JSONObject jsonObject = new JSONObject(responce);
                        int err_no = jsonObject.getInt("err_no");
                        if (err_no == 0) {
                            JSONObject results = jsonObject.getJSONObject("results");
                            if (!TextUtils.isEmpty(results.toString())) {
                                handler.sendEmptyMessage(2);
                                SharedPreferences sharedPreferences = getSharedPreferences("GLUser", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("avatar", results.getString("avatar"));
                                editor.commit();
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
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

}
