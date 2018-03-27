package com.gengli.glservice.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gengli.glservice.R;
import com.gengli.glservice.bean.Article;
import com.gengli.glservice.bean.Category;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.List;

public class ArticleAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<Article> articleList;

    public ArticleAdapter(Context context, List<Article> articleList) {
        this.context = context;
        this.articleList = articleList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return articleList.size();
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
            convertView = inflater.inflate(R.layout.item_article, parent, false);
            holder.item_article_img = (ImageView) convertView.findViewById(R.id.item_article_img);
            holder.item_article_title = (TextView) convertView.findViewById(R.id.item_article_title);
            holder.item_article_des = (TextView) convertView.findViewById(R.id.item_article_des);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Article article = articleList.get(position);
        ImageOptions imageOptions = new ImageOptions.Builder()
                .setLoadingDrawableId(R.mipmap.ic_launcher)
                .setFailureDrawableId(R.mipmap.ic_launcher)
                .build();
        x.image().bind(holder.item_article_img, article.getImgUrl(), imageOptions);
        holder.item_article_title.setText(article.getTitle());
        holder.item_article_des.setText(article.getDesc());
        return convertView;
    }

    private static class ViewHolder {
        private ImageView item_article_img;
        private TextView item_article_title;
        private TextView item_article_des;
    }
}