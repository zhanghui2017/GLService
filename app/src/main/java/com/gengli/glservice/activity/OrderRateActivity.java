package com.gengli.glservice.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;

import com.gengli.glservice.R;
import com.gengli.glservice.http.ServicePort;
import com.gengli.glservice.http.XUtilHttp;
import com.gengli.glservice.util.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

@ContentView(R.layout.activity_order_rate)
public class OrderRateActivity extends BaseActivity {

    @ViewInject(R.id.order_rate_repair_id)
    private TextView order_rate_repair_id;

    @ViewInject(R.id.order_rate_repair_name)
    private TextView order_rate_repair_name;

//    @ViewInject(R.id.order_rate_fitting_info)
//    private TextView order_rate_fitting_info;

    @ViewInject(R.id.order_rate_charge_header_img)
    private ImageView order_rate_charge_header_img;

    @ViewInject(R.id.order_rate_charge_name)
    private TextView order_rate_charge_name;

    @ViewInject(R.id.order_rate_ratingbar_1)
    private RatingBar order_rate_ratingbar_1;

    @ViewInject(R.id.order_rate_ratingbar_2)
    private RatingBar order_rate_ratingbar_2;

    @ViewInject(R.id.order_rate_ratingbar_3)
    private RatingBar order_rate_ratingbar_3;

    @ViewInject(R.id.order_rate_switch)
    private Switch order_rate_switch;

    @ViewInject(R.id.order_rate_edit)
    private EditText order_rate_edit;

    private String id;
    private String name;
    private String chargeName;
    private String handle_avatar;
    int finishType = 0;
    private int rating_1 = 0;
    private int rating_2 = 0;
    private int rating_3 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id = getIntent().getStringExtra("order_rid");
        name = getIntent().getStringExtra("order_name");
        chargeName = getIntent().getStringExtra("order_charge_name");
        handle_avatar = getIntent().getStringExtra("order_handle_avatar");
        order_rate_repair_id.setText(id);
        order_rate_repair_name.setText(name);
        order_rate_charge_name.setText(chargeName);

        if (!TextUtils.isEmpty(handle_avatar)) {
            ImageOptions imageOptions = new ImageOptions.Builder()
                    .setCircular(true)
                    .setLoadingDrawableId(R.drawable.img_default_head_icon)
                    .setFailureDrawableId(R.drawable.img_default_head_icon)
                    .build();
            x.image().bind(order_rate_charge_header_img, handle_avatar, imageOptions);
        }

        order_rate_ratingbar_1.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                rating_1 = (int) rating;
            }
        });

        order_rate_ratingbar_2.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                rating_2 = (int) rating;
            }
        });

        order_rate_ratingbar_3.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                rating_3 = (int) rating;
            }
        });
    }


    @Event(value = R.id.order_rate_back_bt, type = View.OnClickListener.class)
    private void backClick(View view) {
        finish();
    }

    @Event(value = R.id.order_rate_commit_bt, type = View.OnClickListener.class)
    private void commitClick(View view) {
        commitRate();
    }


    @Event(value = R.id.order_rate_switch)
    private void switchClick(View view) {
        boolean isChecked = order_rate_switch.isChecked();
        if (isChecked) {
            finishType = 0;
            LogUtils.showLogD("开启");
        } else {
            finishType = 1;
            LogUtils.showLogD("关闭");
        }
    }


    private void commitRate() {
        String url = ServicePort.REPAIR_COMMENT_ADD;
        XUtilHttp api = new XUtilHttp();
        Map<String, String> map = new HashMap<>();
        map.put("rid", id);
        map.put("score_service", rating_1 + "");
        map.put("score_professional", rating_2 + "");
        map.put("score_speed", rating_3 + "");
        map.put("content", order_rate_edit.getText().toString());
        map.put("is_finish", finishType + "");
        RequestParams params = api.getParam(this, url, map);
        params.addBodyParameter("rid", id);
        params.addBodyParameter("score_service", rating_1 + "");
        params.addBodyParameter("score_professional", rating_2 + "");
        params.addBodyParameter("score_speed", rating_3 + "");
        params.addBodyParameter("content", order_rate_edit.getText().toString());
        params.addBodyParameter("is_finish", finishType + "");
        api.post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String responce = result.toString();
                LogUtils.showLogD("----->评价返回数据----->" + responce);
                if (!TextUtils.isEmpty(responce)) {
                    try {
                        JSONObject jsonObject = new JSONObject(responce);
                        int err_no = jsonObject.getInt("err_no");
                        if (err_no == 0) {
                            handler.sendEmptyMessage(1);
                            LogUtils.showCenterToast(OrderRateActivity.this, "评价成功");
                        } else {
                            LogUtils.showCenterToast(OrderRateActivity.this, jsonObject.getInt("err_no") + "" + jsonObject.getString("err_msg"));
                        }
                    } catch (JSONException e) {

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


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    finish();
                    break;

            }
        }
    };
}
