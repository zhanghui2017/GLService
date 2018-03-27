package com.gengli.glservice.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gengli.glservice.R;
import com.gengli.glservice.activity.OrderRateActivity;
import com.gengli.glservice.bean.Category;
import com.gengli.glservice.bean.Order;
import com.gengli.glservice.http.ServicePort;
import com.gengli.glservice.http.XUtilHttp;
import com.gengli.glservice.util.DatasUtil;
import com.gengli.glservice.util.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RepairAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<Order> orderList;

    public RepairAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return orderList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_repair, parent, false);
            holder.item_repair_type_img = (ImageView) convertView.findViewById(R.id.item_repair_type_img);
            holder.item_repair_id_text = (TextView) convertView.findViewById(R.id.item_repair_id_text);
            holder.item_repair_machine_text = (TextView) convertView.findViewById(R.id.item_repair_machine_text);
            holder.item_repair_name_text = (TextView) convertView.findViewById(R.id.item_repair_name_text);
            holder.item_repair_phone_text = (TextView) convertView.findViewById(R.id.item_repair_phone_text);
            holder.item_repair_address_text = (TextView) convertView.findViewById(R.id.item_repair_address_text);
            holder.item_repair_charge_text = (TextView) convertView.findViewById(R.id.item_repair_charge_text);
            holder.item_repair_time_text = (TextView) convertView.findViewById(R.id.item_repair_time_text);
            holder.item_repair_comment_img = (ImageView) convertView.findViewById(R.id.item_repair_comment_img);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Order order = orderList.get(position);
        String realname = DatasUtil.getUserInfo(context, "realname");
        holder.item_repair_id_text.setText("订单号：" + order.getId());
        holder.item_repair_machine_text.setText(order.getMachine());
        holder.item_repair_phone_text.setText(order.getPhone());
        holder.item_repair_name_text.setText(realname);
        holder.item_repair_address_text.setText(order.getExpressAddress());
        holder.item_repair_charge_text.setText(order.getChargeName() + " " + order.getChargePhone());
        holder.item_repair_time_text.setText(order.getTime());
        if (order.getType() == 1) {
            holder.item_repair_type_img.setVisibility(View.VISIBLE);
            holder.item_repair_type_img.setImageResource(R.drawable.order_begin_img);
            holder.item_repair_comment_img.setVisibility(View.VISIBLE);
            if (order.is_emerg()) {
                holder.item_repair_comment_img.setImageResource(R.drawable.img_repair_quick_pre);
            } else {
                holder.item_repair_comment_img.setImageResource(R.drawable.img_repair_quick);
                holder.item_repair_comment_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        repairEmerg(order);
                    }
                });
            }
        } else if (order.getType() == 3) {
            holder.item_repair_type_img.setVisibility(View.VISIBLE);
            holder.item_repair_type_img.setImageResource(R.drawable.order_ing_img);
            holder.item_repair_comment_img.setVisibility(View.INVISIBLE);
        } else if (order.getType() == 4) {
            holder.item_repair_type_img.setVisibility(View.VISIBLE);
            holder.item_repair_type_img.setImageResource(R.drawable.order_ok_img);
            holder.item_repair_comment_img.setVisibility(View.VISIBLE);
            if (order.is_comment()) {
                holder.item_repair_comment_img.setImageResource(R.drawable.img_repair_comment_pre);
            } else {
                holder.item_repair_comment_img.setImageResource(R.drawable.img_repair_comment);
                holder.item_repair_comment_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, OrderRateActivity.class);
                        intent.putExtra("order_rid", order.getId());
                        intent.putExtra("order_name", order.getName());
                        intent.putExtra("order_charge_name", order.getChargeName());
                        context.startActivity(intent);
                    }
                });
            }
        } else {
            holder.item_repair_type_img.setVisibility(View.INVISIBLE);
            holder.item_repair_comment_img.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    private static class ViewHolder {
        private ImageView item_repair_type_img;
        private TextView item_repair_id_text;
        private TextView item_repair_machine_text;
        private TextView item_repair_name_text;
        private TextView item_repair_phone_text;
        private TextView item_repair_address_text;
        private TextView item_repair_charge_text;
        private TextView item_repair_time_text;
        private ImageView item_repair_comment_img;

    }


    private void repairEmerg(final Order order) {
        String url = ServicePort.REPAIR_EMERG;
        XUtilHttp api = new XUtilHttp();
        Map<String, String> map = new HashMap<>();
        map.put("rid", order.getId());
        RequestParams params = api.getParam(context, url, map);
        params.addBodyParameter("rid", order.getId());
        api.post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String responce = result.toString();
                LogUtils.showLogD("----->加急返回数据----->" + responce);
                if (!TextUtils.isEmpty(responce)) {
                    try {
                        JSONObject jsonObject = new JSONObject(responce);
                        int err_no = jsonObject.getInt("err_no");
                        if (err_no == 0) {
                            handler.sendEmptyMessage(1);
                            order.setIs_emerg(true);
                            LogUtils.showCenterToast(context, "已加急");
                        } else {
                            LogUtils.showCenterToast(context, jsonObject.getInt("err_no") + "" + jsonObject.getString("err_msg"));
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
                    notifyDataSetChanged();
                    break;

            }
        }
    };

}