package com.gengli.glservice.fragment;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.gengli.glservice.R;
import com.gengli.glservice.activity.AfterSaleActivity;
import com.gengli.glservice.activity.AllOrderActivity;
import com.gengli.glservice.activity.CompanyInfoActivity;
import com.gengli.glservice.activity.ContactsActivity;
import com.gengli.glservice.activity.CustomerActivity;
import com.gengli.glservice.activity.FavArticlesActivity;
import com.gengli.glservice.activity.LogArticlesActivity;
import com.gengli.glservice.activity.LoginActivity;
import com.gengli.glservice.activity.MessageActivity;
import com.gengli.glservice.activity.ProBuyActivity;
import com.gengli.glservice.activity.ProControlActivity;
import com.gengli.glservice.activity.SystemActivity;
import com.gengli.glservice.http.ServicePort;
import com.gengli.glservice.http.XUtilHttp;
import com.gengli.glservice.util.DatasUtil;
import com.gengli.glservice.util.FileUtils;
import com.gengli.glservice.util.LogUtils;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

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
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

@ContentView(R.layout.fragment_mine)
public class MineFragment extends BaseFragment implements View.OnClickListener {

    @ViewInject(R.id.mine_login_bt)
    private Button mine_login_bt;

    @ViewInject(R.id.mine_info_view)
    private LinearLayout mine_info_view;

    @ViewInject(R.id.mine_job_num_text)
    private TextView mine_job_num_text;

    @ViewInject(R.id.mine_company_text)
    private TextView mine_company_text;

    @ViewInject(R.id.mine_head_bt)
    private ImageView mine_head_bt;

    @ViewInject(R.id.mine_head_type_img)
    private ImageView mine_head_type_img;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        init();
        String headUrl = DatasUtil.getUserInfo(getActivity(), "avatar");
        imageOptions = new ImageOptions.Builder()
                .setCircular(true)
                .setLoadingDrawableId(R.drawable.img_tmp_head)
                .setFailureDrawableId(R.drawable.img_tmp_head)
                .build();
        x.image().bind(mine_head_bt, headUrl, imageOptions);
    }

    private void init() {
        if (DatasUtil.isLogin(getActivity())) {
            mine_info_view.setVisibility(View.VISIBLE);
            mine_login_bt.setVisibility(View.INVISIBLE);
            String company_id = DatasUtil.getUserInfo(getActivity(), "company_id");
            String unit = DatasUtil.getUserInfo(getActivity(), "unit");

            mine_head_type_img.setImageResource(R.drawable.icon_authentication_pre);
            mine_job_num_text.setText("企业号：" + company_id);
            mine_company_text.setText(unit);

        } else {
            mine_info_view.setVisibility(View.INVISIBLE);
            mine_login_bt.setVisibility(View.VISIBLE);
            mine_head_type_img.setImageResource(R.drawable.icon_authentication);
        }
    }


    @Event(value = R.id.mine_company_info_bt, type = View.OnClickListener.class)
    private void companyClick(View view) {
        if (DatasUtil.isLogin(getActivity())) {
            startActivity(new Intent(getActivity(), CompanyInfoActivity.class));
        } else {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }

    }

    @Event(value = R.id.mine_login_bt)
    private void loginClick(View view) {
        startActivity(new Intent(getActivity(), LoginActivity.class));

    }

    @Event(value = R.id.mine_head_bt)
    private void headerClick(View view) {
        if (DatasUtil.isLogin(getActivity())) {
            getHeadPopup();
            headerPopupWindow.setAnimationStyle(R.style.popup_select_way);
            headerPopupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        } else {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }


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
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
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
            Toast.makeText(getActivity(), "未找到存储卡，无法存储照片！", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getActivity(), "未找到存储卡，无法存储照片！", Toast.LENGTH_SHORT).show();
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

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                String headUrl = DatasUtil.getUserInfo(getActivity(), "avatar");
                x.image().bind(mine_head_bt, headUrl, imageOptions);
            }
        }
    };

    private void uploadHeader() {
        String url = ServicePort.ACCOUNT_MODIFY;
        XUtilHttp api = new XUtilHttp();
        Map<String, String> map = new HashMap<>();
        RequestParams params = api.getParam(getActivity(), url, map);
        params.addBodyParameter("avatar", FileUtils.BitmapToFile(getActivity(), bitmap));
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
                                handler.sendEmptyMessage(1);
                                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("GLUser", getActivity().MODE_PRIVATE);
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Event(value = R.id.mine_sbgl_bt)
    private void Click1(View view) {
        if (DatasUtil.isLogin(getActivity())) {
            startActivity(new Intent(getActivity(), ProControlActivity.class));
        } else {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }
    }

    @Event(value = R.id.main_bxgl_bt)
    private void Click2(View view) {
        if (DatasUtil.isLogin(getActivity())) {
            startActivity(new Intent(getActivity(), AllOrderActivity.class));
        } else {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }

    }

    @Event(value = R.id.main_gmjl_bt)
    private void Click3(View view) {
        if (DatasUtil.isLogin(getActivity())) {
            startActivity(new Intent(getActivity(), ProBuyActivity.class));
        } else {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }
    }

    @Event(value = R.id.main_lxr_bt)
    private void Click4(View view) {
        if (DatasUtil.isLogin(getActivity())) {
            startActivity(new Intent(getActivity(), ContactsActivity.class));
        } else {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }
    }
    
//    @Event(value = R.id.mine_item_1)
//    private void item1(View view) {
//        LogUtils.showCenterToast(getActivity(), "消息功能暂未开放");
////        startActivity(new Intent(getActivity(), MessageActivity.class));
//    }

    @Event(value = R.id.mine_item_2)
    private void item2(View view) {
        if (DatasUtil.isLogin(getActivity())) {
            startActivity(new Intent(getActivity(), LogArticlesActivity.class));
        } else {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }
    }

    @Event(value = R.id.mine_item_3)
    private void item3(View view) {
        if (DatasUtil.isLogin(getActivity())) {
            startActivity(new Intent(getActivity(), FavArticlesActivity.class));
        } else {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }

    }

    @Event(value = R.id.mine_item_4)
    private void item4(View view) {
        startActivity(new Intent(getActivity(), CustomerActivity.class));

    }

    @Event(value = R.id.mine_item_5)
    private void item5(View view) {
        startActivity(new Intent(getActivity(), AfterSaleActivity.class));
    }

    @Event(value = R.id.mine_item_6)
    private void item6(View view) {
        startActivity(new Intent(getActivity(), SystemActivity.class));
    }


}
