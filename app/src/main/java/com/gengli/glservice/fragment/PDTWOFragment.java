package com.gengli.glservice.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.gengli.glservice.R;
import com.gengli.glservice.activity.ProductDetailActivity;
import com.gengli.glservice.bean.Product;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.io.File;

@ContentView(R.layout.fragment_pdtwo)
public class PDTWOFragment extends BaseFragment {

    @ViewInject(R.id.pro_two_text)
    private TextView pro_two_text;

    @ViewInject(R.id.pro_two_img)
    private ImageView pro_two_img;

    @ViewInject(R.id.pro_two_img2)
    private ImageView pro_two_img2;
    private Product product;

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
        pro_two_text.setText(product.getContent());
        Glide.with(this).load(product.getParams()).downloadOnly(new SimpleTarget<File>() {
            @Override
            public void onResourceReady(File resource, GlideAnimation<? super File> glideAnimation) {
                Bitmap bitmap= BitmapFactory.decodeFile(resource.getAbsolutePath(),new BitmapFactory.Options());
                pro_two_img.setImageBitmap(bitmap);
            }
        });
        Glide.with(this).load(product.getTechImg()).downloadOnly(new SimpleTarget<File>() {
            @Override
            public void onResourceReady(File resource, GlideAnimation<? super File> glideAnimation) {
                Bitmap bitmap= BitmapFactory.decodeFile(resource.getAbsolutePath(),new BitmapFactory.Options());
                pro_two_img2.setImageBitmap(bitmap);
            }
        });
    }


}
