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
import com.gengli.glservice.bean.Product;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.List;

public class ProControlAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<Product> productList;
    public boolean isShowDel = false;
    private DelClickLintener lintener;

    public void setDelClickLintener(DelClickLintener lintener) {
        this.lintener = lintener;
    }

    public ProControlAdapter(Context context, List<Product> productList) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_pro_control, parent, false);
            holder.item_pro_control_img = (ImageView) convertView.findViewById(R.id.item_pro_control_img);
            holder.item_pro_control_type_new_img = (ImageView) convertView.findViewById(R.id.item_pro_control_type_new_img);
            holder.item_pro_control_type_hot_img = (ImageView) convertView.findViewById(R.id.item_pro_control_type_hot_img);
            holder.item_pro_control_name = (TextView) convertView.findViewById(R.id.item_pro_control_name);
            holder.item_pro_control_des = (TextView) convertView.findViewById(R.id.item_pro_control_des);
            holder.item_pro_control_bt1 = (ImageView) convertView.findViewById(R.id.item_pro_control_bt1);
            holder.item_pro_control_bt2 = (ImageView) convertView.findViewById(R.id.item_pro_control_bt2);
            holder.item_pro_control_delete_bt = (ImageView) convertView.findViewById(R.id.item_pro_control_delete_bt);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Product product = productList.get(position);
        ImageOptions imageOptions = new ImageOptions.Builder()
                .setCircular(true)
                .setLoadingDrawableId(R.mipmap.ic_launcher)
                .setFailureDrawableId(R.mipmap.ic_launcher)
                .build();
        x.image().bind(holder.item_pro_control_img, product.getImgUrl(), imageOptions);
        holder.item_pro_control_name.setText(product.getName());
        holder.item_pro_control_des.setText(product.getDesc());
        if (product.isNew())
            holder.item_pro_control_type_new_img.setImageResource(R.drawable.img_product_new);
        if (product.isHot())
            holder.item_pro_control_type_hot_img.setImageResource(R.drawable.img_product_hot);

        if (isShowDel)
            showDel(holder.item_pro_control_delete_bt);
        else
            hideDel(holder.item_pro_control_delete_bt);

        holder.item_pro_control_bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RelatedArticleActivity.class);
                intent.putExtra("control_product", product);
                context.startActivity(intent);
            }
        });

        holder.item_pro_control_bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RelatedFitActivity.class);
                intent.putExtra("control_product", product);
                context.startActivity(intent);
            }
        });

        holder.item_pro_control_delete_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lintener.delClick(position);
            }
        });


        return convertView;
    }

    private static class ViewHolder {
        private ImageView item_pro_control_img;
        private ImageView item_pro_control_type_new_img;
        private ImageView item_pro_control_type_hot_img;
        private TextView item_pro_control_name;
        private TextView item_pro_control_des;
        private ImageView item_pro_control_bt1;
        private ImageView item_pro_control_bt2;
        private ImageView item_pro_control_delete_bt;
    }

    public void showDel(ImageView iv) {
        iv.setVisibility(View.VISIBLE);
    }

    public void hideDel(ImageView iv) {
        iv.setVisibility(View.GONE);
    }

    public interface DelClickLintener {
        void delClick(int position);
    }

}