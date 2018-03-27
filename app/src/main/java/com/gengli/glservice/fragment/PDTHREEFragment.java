package com.gengli.glservice.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.gengli.glservice.R;
import com.gengli.glservice.activity.ProductDetailActivity;
import com.gengli.glservice.adapter.ArticleAdapter;
import com.gengli.glservice.bean.Article;
import com.gengli.glservice.bean.Product;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.fragment_pdthree)
public class PDTHREEFragment extends BaseFragment {

    @ViewInject(R.id.pro_three_list_view)
    private ListView pro_three_list_view;

    private Product product;
    private ArticleAdapter adapter;
    private List<Article> articleList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        product = ((ProductDetailActivity) getActivity()).getProduct();
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        articleList = new ArrayList<>();
        articleList = product.getArticles();
        adapter = new ArticleAdapter(getActivity(), articleList);
        pro_three_list_view.setAdapter(adapter);
        pro_three_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }


}
