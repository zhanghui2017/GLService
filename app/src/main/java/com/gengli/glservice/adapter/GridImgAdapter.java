package com.gengli.glservice.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.gengli.glservice.R;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.List;

public class GridImgAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<String> imgList;

    public GridImgAdapter(Context context, List<String> imgList) {
        this.context = context;
        this.imgList = imgList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return imgList.size();
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
            convertView = inflater.inflate(R.layout.item_grid_image, parent, false);
            holder.img_view = (ImageView) convertView.findViewById(R.id.img_view);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String img = imgList.get(position);
        ImageOptions imageOptions = new ImageOptions.Builder()
                .setLoadingDrawableId(R.mipmap.ic_launcher)
                .setFailureDrawableId(R.mipmap.ic_launcher)
                .build();
        x.image().bind(holder.img_view, img, imageOptions);

        return convertView;
    }

    private static class ViewHolder {
        private ImageView img_view;
    }
}