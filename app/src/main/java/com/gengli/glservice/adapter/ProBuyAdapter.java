package com.gengli.glservice.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gengli.glservice.R;
import com.gengli.glservice.activity.RelatedArticleActivity;
import com.gengli.glservice.activity.RelatedFitActivity;
import com.gengli.glservice.bean.ProBuy;
import com.gengli.glservice.bean.Product;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.List;

public class ProBuyAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<ProBuy> proBuyList;

    public ProBuyAdapter(Context context, List<ProBuy> proBuyList) {
        this.context = context;
        this.proBuyList = proBuyList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return proBuyList.size();
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
            convertView = inflater.inflate(R.layout.item_pro_buy, parent, false);
            holder.item_pro_buy_img = (ImageView) convertView.findViewById(R.id.item_pro_buy_img);
            holder.item_pro_buy_type_new_img = (ImageView) convertView.findViewById(R.id.item_pro_buy_type_new_img);
            holder.item_pro_buy_type_hot_img = (ImageView) convertView.findViewById(R.id.item_pro_buy_type_hot_img);
            holder.item_pro_buy_name = (TextView) convertView.findViewById(R.id.item_pro_buy_name);
            holder.item_pro_buy_des = (TextView) convertView.findViewById(R.id.item_pro_buy_des);
            holder.item_pro_buy_text1 = (TextView) convertView.findViewById(R.id.item_pro_buy_text1);
            holder.item_pro_buy_text2 = (TextView) convertView.findViewById(R.id.item_pro_buy_text2);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ProBuy proBuy = proBuyList.get(position);
        ImageOptions imageOptions = new ImageOptions.Builder()
                .setCircular(true)
                .setLoadingDrawableId(R.mipmap.ic_launcher)
                .setFailureDrawableId(R.mipmap.ic_launcher)
                .build();
        x.image().bind(holder.item_pro_buy_img, proBuy.getThumb(), imageOptions);
        holder.item_pro_buy_name.setText(proBuy.getTitle());
        holder.item_pro_buy_des.setText(proBuy.getDes());
        holder.item_pro_buy_text1.setText("购买时间：" + proBuy.getBuy_date());
        holder.item_pro_buy_text2.setText("质保：" + proBuy.getBuy_period());
        if (proBuy.isNew())
            holder.item_pro_buy_type_new_img.setImageResource(R.drawable.img_product_new);
        if (proBuy.isHot())
            holder.item_pro_buy_type_hot_img.setImageResource(R.drawable.img_product_hot);
        return convertView;
    }

    private static class ViewHolder {
        private ImageView item_pro_buy_img;
        private ImageView item_pro_buy_type_new_img;
        private ImageView item_pro_buy_type_hot_img;
        private TextView item_pro_buy_name;
        private TextView item_pro_buy_des;
        private TextView item_pro_buy_text1;
        private TextView item_pro_buy_text2;
    }

}