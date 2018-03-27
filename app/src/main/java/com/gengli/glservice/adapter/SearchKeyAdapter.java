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

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.List;

public class SearchKeyAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<String> strs;

    public SearchKeyAdapter(Context context, List<String> strs) {
        this.context = context;
        this.strs = strs;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return strs.size();
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
            convertView = inflater.inflate(R.layout.item_search_key, parent, false);
            holder.item_search_word = (TextView) convertView.findViewById(R.id.item_search_word);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String str = strs.get(position);
        holder.item_search_word.setText(str);
        return convertView;
    }

    private static class ViewHolder {
        TextView item_search_word;
    }
}