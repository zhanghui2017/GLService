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

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.List;

public class FavArticleAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<Article> articleList;
    public boolean isShowDel = false;
    private ProControlAdapter.DelClickLintener lintener;

    public void setDelClickLintener(ProControlAdapter.DelClickLintener lintener) {
        this.lintener = lintener;
    }

    public FavArticleAdapter(Context context, List<Article> articleList) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_fav_article, parent, false);
            holder.item_fav_delete_bt = (ImageView) convertView.findViewById(R.id.item_fav_delete_bt);
            holder.item_fav_img = (ImageView) convertView.findViewById(R.id.item_fav_img);
            holder.item_fav_title = (TextView) convertView.findViewById(R.id.item_fav_title);
            holder.item_fav_des = (TextView) convertView.findViewById(R.id.item_fav_des);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Article article = articleList.get(position);
        ImageOptions imageOptions = new ImageOptions.Builder()
                .setLoadingDrawableId(R.mipmap.ic_launcher)
                .setFailureDrawableId(R.mipmap.ic_launcher)
                .build();
        x.image().bind(holder.item_fav_img, article.getImgUrl(), imageOptions);
        holder.item_fav_title.setText(article.getTitle());
        holder.item_fav_des.setText(article.getDesc());

        if (isShowDel)
            showDel(holder.item_fav_delete_bt);
        else
            hideDel(holder.item_fav_delete_bt);

        holder.item_fav_delete_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lintener.delClick(position);
            }
        });
        return convertView;
    }

    private static class ViewHolder {
        private ImageView item_fav_delete_bt;
        private ImageView item_fav_img;
        private TextView item_fav_title;
        private TextView item_fav_des;
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