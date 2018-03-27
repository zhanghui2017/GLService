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
import com.gengli.glservice.util.LogUtils;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.List;

public class CategoryAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<Category> categories;

    public CategoryAdapter(Context context, List<Category> categories) {
        this.context = context;
        this.categories = categories;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return categories.size();
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
            convertView = inflater.inflate(R.layout.item_category, parent, false);
            holder.item_category_img = (ImageView) convertView.findViewById(R.id.item_category_img);
            holder.item_category_name = (TextView) convertView.findViewById(R.id.item_category_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Category category = categories.get(position);
        ImageOptions imageOptions = new ImageOptions.Builder()
                .setCircular(true)
                .setLoadingDrawableId(R.mipmap.ic_launcher)
                .setFailureDrawableId(R.mipmap.ic_launcher)
                .build();
        x.image().bind(holder.item_category_img, category.getImgUrl(), imageOptions);
        holder.item_category_name.setText(category.getName());
        return convertView;
    }

    private static class ViewHolder {
        private ImageView item_category_img;
        private TextView item_category_name;
    }
}