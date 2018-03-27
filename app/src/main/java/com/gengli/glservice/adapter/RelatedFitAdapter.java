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
import com.gengli.glservice.bean.Fitting;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.List;

public class RelatedFitAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<Fitting> fittingList;

    public RelatedFitAdapter(Context context, List<Fitting> fittingList) {
        this.context = context;
        this.fittingList = fittingList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return fittingList.size();
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

        Fitting fitting = fittingList.get(position);
        ImageOptions imageOptions = new ImageOptions.Builder()
                .setCircular(true)
                .setLoadingDrawableId(R.mipmap.ic_launcher)
                .setFailureDrawableId(R.mipmap.ic_launcher)
                .build();
        x.image().bind(holder.item_category_img, fitting.getImgUrl(), imageOptions);
        holder.item_category_name.setText(fitting.getTitle());
        return convertView;
    }

    private static class ViewHolder {
        private ImageView item_category_img;
        private TextView item_category_name;
    }
}