package com.gengli.glservice.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gengli.glservice.R;
import com.gengli.glservice.bean.Category;
import com.gengli.glservice.bean.Product;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.List;

public class ProductAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<Product> productList;

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return productList.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_product, parent, false);
            holder.item_product_img = (ImageView) convertView.findViewById(R.id.item_product_img);
            holder.item_product_type_new_img = (ImageView) convertView.findViewById(R.id.item_product_type_new_img);
            holder.item_product_type_hot_img = (ImageView) convertView.findViewById(R.id.item_product_type_hot_img);
            holder.item_product_name = (TextView) convertView.findViewById(R.id.item_product_name);
            holder.item_product_des = (TextView) convertView.findViewById(R.id.item_product_des);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Product product = productList.get(position);
        ImageOptions imageOptions = new ImageOptions.Builder()
                .setCircular(true)
                .setLoadingDrawableId(R.mipmap.ic_launcher)
                .setFailureDrawableId(R.mipmap.ic_launcher)
                .build();
        x.image().bind(holder.item_product_img, product.getImgUrl(), imageOptions);
        holder.item_product_name.setText(product.getName());
        holder.item_product_des.setText(product.getDesc());
        if (product.isNew())
            holder.item_product_type_new_img.setImageResource(R.drawable.img_product_new);
        if (product.isHot())
            holder.item_product_type_hot_img.setImageResource(R.drawable.img_product_hot);
        return convertView;
    }

    private static class ViewHolder {
        private ImageView item_product_img;
        private ImageView item_product_type_new_img;
        private ImageView item_product_type_hot_img;
        private TextView item_product_name;
        private TextView item_product_des;
    }
}