package com.gengli.glservice.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.gengli.glservice.R;
import com.gengli.glservice.activity.ProductDetailActivity;
import com.gengli.glservice.adapter.AboutFitAdapter;
import com.gengli.glservice.adapter.AboutProAdapter;
import com.gengli.glservice.bean.Fitting;
import com.gengli.glservice.bean.Product;
import com.gengli.glservice.util.LogUtils;
import com.gengli.glservice.view.HorizontalListView;
import com.gengli.glservice.view.ImageSlideView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.List;

@ContentView(R.layout.fragment_pdone)
public class PDONEFragment extends BaseFragment {

    @ViewInject(R.id.pro_one_slide_img)
    private ImageSlideView pro_one_slide_img;

    @ViewInject(R.id.pro_one_title)
    private TextView pro_one_title;

    @ViewInject(R.id.pro_one_des)
    private TextView pro_one_des;

    @ViewInject(R.id.pro_one_model)
    private TextView pro_one_model;

    @ViewInject(R.id.pro_one_weight)
    private TextView pro_one_weight;

    @ViewInject(R.id.pro_one_size)
    private TextView pro_one_size;

    @ViewInject(R.id.pro_one_scope)
    private TextView pro_one_scope;

    private Product product;
    private AboutFitAdapter fitAdapter;
    private AboutProAdapter proAdapter;
    private List<Product> aboutProList;
    private List<Fitting> aboutFitList;

    @ViewInject(R.id.pro_one_about_product_list)
    private HorizontalListView pro_one_about_product_list;

    @ViewInject(R.id.pro_one_about_fitting_list)
    private HorizontalListView pro_one_about_fitting_list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        product = ((ProductDetailActivity) getActivity()).getProduct();
        aboutProList = ((ProductDetailActivity) getActivity()).getAboutProList();
        aboutFitList = product.getFittings();
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        List<String> imageUrls = product.getImgList();
        for (int i = 0; i < imageUrls.size(); i++) {
            pro_one_slide_img.addImageUrl(imageUrls.get(i));
        }
        pro_one_slide_img.setDelay(2000);
        pro_one_slide_img.commit();
        pro_one_title.setText(product.getName());
        pro_one_des.setText(product.getDesc());
        pro_one_model.setText("产品型号：" + product.getModel());
        pro_one_weight.setText("整机重量：" + product.getWeight());
        pro_one_size.setText("外形尺寸：" + product.getSize());
        pro_one_scope.setText(product.getScope());


        proAdapter = new AboutProAdapter(getActivity(), aboutProList);
        pro_one_about_product_list.setAdapter(proAdapter);
        pro_one_about_product_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {}
        });

        fitAdapter = new AboutFitAdapter(getActivity(), aboutFitList);
        pro_one_about_fitting_list.setAdapter(fitAdapter);
        pro_one_about_fitting_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {}
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        pro_one_slide_img.releaseResource();
    }

}
