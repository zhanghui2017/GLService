package com.gengli.glservice.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gengli.glservice.R;
import com.gengli.glservice.bean.Fitting;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.List;

public class FittingInfoAdapter extends BaseAdapter {

    private Context context;
    private List<Fitting> fittingList;
    private LayoutInflater inflater;

    public FittingInfoAdapter(Context context, List<Fitting> fittingList) {
        super();
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
            convertView = inflater.inflate(R.layout.item_order_fitting_info, parent, false);
            holder.img_ing_fitting_info = (ImageView) convertView.findViewById(R.id.img_ing_fitting_info);
            holder.text_ing_fitting_info_name = (TextView) convertView.findViewById(R.id.text_ing_fitting_info_name);
            holder.text_ing_fitting_info_count = (TextView) convertView.findViewById(R.id.text_ing_fitting_info_count);
            holder.text_ing_fitting_info_price = (TextView) convertView.findViewById(R.id.text_ing_fitting_info_price);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Fitting fitting = fittingList.get(position);
        holder.text_ing_fitting_info_name.setText(fitting.getTitle());
        holder.text_ing_fitting_info_count.setText("*" + fitting.getChooseCount());
        holder.text_ing_fitting_info_price.setText("¥" + fitting.getPrice());
        ImageOptions imageOptions = new ImageOptions.Builder()
                .setCircular(true)
                .setLoadingDrawableId(R.mipmap.ic_launcher)
                .setFailureDrawableId(R.mipmap.ic_launcher)
                .build();
        x.image().bind(holder.img_ing_fitting_info, fitting.getImgUrl(), imageOptions);
        return convertView;
    }


    private static class ViewHolder {
        private ImageView img_ing_fitting_info;
        private TextView text_ing_fitting_info_name;
        private TextView text_ing_fitting_info_count;
        private TextView text_ing_fitting_info_price;
    }
}