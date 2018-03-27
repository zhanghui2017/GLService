package com.gengli.glservice.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.gengli.glservice.R;
import com.gengli.glservice.bean.Article;
import com.gengli.glservice.bean.Fitting;
import com.gengli.glservice.bean.Product;
import com.gengli.glservice.fragment.HelpFragment;
import com.gengli.glservice.fragment.MainFragment;
import com.gengli.glservice.fragment.MineFragment;
import com.gengli.glservice.fragment.PDONEFragment;
import com.gengli.glservice.fragment.PDTHREEFragment;
import com.gengli.glservice.fragment.PDTWOFragment;
import com.gengli.glservice.fragment.ProductFragment;
import com.gengli.glservice.http.ServicePort;
import com.gengli.glservice.http.XUtilHttp;
import com.gengli.glservice.util.DatasUtil;
import com.gengli.glservice.util.LogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductDetailActivity extends FragmentActivity implements View.OnClickListener {

    private RelativeLayout pro_tab_one_bt, pro_tab_two_bt, pro_tab_three_bt;
    private View pro_tab_one_line, pro_tab_two_line, pro_tab_three_line;
    private ImageView product_detail_back_bt;
    private ImageView product_add_bt;
    private ImageView product_call_phone_bt;
    private PDONEFragment pdoneFragment;
    private PDTWOFragment pdtwoFragment;
    private PDTHREEFragment pdthreeFragment;
    private FragmentManager fragmentManager;
    private Product product;
    private int pro_id;

    private List<Product> aboutProList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        product = (Product) getIntent().getSerializableExtra("cur_product");

        pro_id = getIntent().getIntExtra("pro_id", 0);
        getProductDetail();
        initView();
    }

    private void initView() {
        pro_tab_one_bt = (RelativeLayout) findViewById(R.id.pro_tab_one_bt);
        pro_tab_two_bt = (RelativeLayout) findViewById(R.id.pro_tab_two_bt);
        pro_tab_three_bt = (RelativeLayout) findViewById(R.id.pro_tab_three_bt);
        product_detail_back_bt = (ImageView) findViewById(R.id.product_detail_back_bt);
        product_add_bt = (ImageView) findViewById(R.id.product_add_bt);
        product_call_phone_bt = (ImageView) findViewById(R.id.product_call_phone_bt);
        product_call_phone_bt.setOnClickListener(this);
        product_add_bt.setOnClickListener(this);
        product_detail_back_bt.setOnClickListener(this);
        pro_tab_one_bt.setOnClickListener(this);
        pro_tab_two_bt.setOnClickListener(this);
        pro_tab_three_bt.setOnClickListener(this);
        pro_tab_one_line = findViewById(R.id.pro_tab_one_line);
        pro_tab_two_line = findViewById(R.id.pro_tab_two_line);
        pro_tab_three_line = findViewById(R.id.pro_tab_three_line);

        fragmentManager = getSupportFragmentManager();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pro_tab_one_bt:
                pro_tab_one_line.setVisibility(View.VISIBLE);
                pro_tab_two_line.setVisibility(View.INVISIBLE);
                pro_tab_three_line.setVisibility(View.INVISIBLE);
                changeFrame(0);
                break;
            case R.id.pro_tab_two_bt:
                pro_tab_one_line.setVisibility(View.INVISIBLE);
                pro_tab_two_line.setVisibility(View.VISIBLE);
                pro_tab_three_line.setVisibility(View.INVISIBLE);
                changeFrame(1);
                break;
            case R.id.pro_tab_three_bt:
                pro_tab_one_line.setVisibility(View.INVISIBLE);
                pro_tab_two_line.setVisibility(View.INVISIBLE);
                pro_tab_three_line.setVisibility(View.VISIBLE);
                changeFrame(2);
                break;
            case R.id.product_detail_back_bt:
                finish();
                break;
            case R.id.product_add_bt:
                if (DatasUtil.isLogin(this)) {
                    addProduct();
                } else {
                    startActivity(new Intent(this, LoginActivity.class));
                }

                break;
            case R.id.product_call_phone_bt:
                callPhone("037960118689");
                break;
        }
    }


    private void changeFrame(int index) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        hideAllFrame(transaction);
        switch (index) {
            case 0:
                if (pdoneFragment == null) {
                    pdoneFragment = new PDONEFragment();
                    transaction.add(R.id.product_detail_cotent, pdoneFragment);
                } else {
                    transaction.show(pdoneFragment);
                }
                break;
            case 1:
                if (pdtwoFragment == null) {
                    pdtwoFragment = new PDTWOFragment();
                    transaction.add(R.id.product_detail_cotent, pdtwoFragment);
                } else {
                    transaction.show(pdtwoFragment);
                }
                break;

            case 2:
                if (pdthreeFragment == null) {
                    pdthreeFragment = new PDTHREEFragment();
                    transaction.add(R.id.product_detail_cotent, pdthreeFragment);
                } else {
                    transaction.show(pdthreeFragment);
                }
                break;
            default:
                break;
        }
        transaction.commit();
    }

    private void hideAllFrame(FragmentTransaction transaction) {
        if (pdoneFragment != null) {
            transaction.hide(pdoneFragment);
        }
        if (pdtwoFragment != null) {
            transaction.hide(pdtwoFragment);
        }
        if (pdthreeFragment != null) {
            transaction.hide(pdthreeFragment);
        }
    }


    private void getProductDetail() {
        String url = ServicePort.PRODUCT_DETAIL;
        XUtilHttp api = new XUtilHttp();
        Map<String, String> map = new HashMap<>();
        if (product == null) {
            map.put("id", pro_id + "");
        } else {
            map.put("id", product.getId() + "");
        }
        RequestParams params = api.getParam(this, url, map);
        if (product == null) {
            params.addBodyParameter("id", pro_id + "");
        } else {
            params.addBodyParameter("id", product.getId() + "");
        }

        api.post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtils.showLogI("--------产品详情返回数据:" + result.toString());
                String responce = result.toString();
                if (!TextUtils.isEmpty(responce)) {
                    try {
                        JSONObject jsonObject = new JSONObject(responce);
                        int err_no = jsonObject.getInt("err_no");
                        if (err_no == 0) {
                            JSONObject results = jsonObject.getJSONObject("results");
                            JSONArray images = results.getJSONArray("images");
                            JSONArray archives = results.getJSONArray("archives");
                            JSONArray products = results.getJSONArray("products");
                            JSONArray parts = results.getJSONArray("parts");
                            if (!TextUtils.isEmpty(results.toString())) {
                                if (product == null) {
                                    product = new Product();
                                }
                                product.setId(results.getInt("id"));
                                product.setDesc(results.getString("des"));
                                product.setModel(results.getString("model"));
                                product.setContent(results.getString("content"));
                                product.setSize(results.getString("size"));
                                product.setWeight(results.getString("weight"));
                                product.setScope(results.getString("scope"));
                                product.setParams(results.getString("params"));
                                product.setTechImg(results.getString("tech"));

                            }
                            List<String> imgurl = new ArrayList<>();
                            for (int i = 0; i < images.length(); i++) {
                                JSONObject object = images.getJSONObject(i);
                                imgurl.add(object.getString("img"));
                            }
                            product.setImgList(imgurl);

                            List<Article> articles = new ArrayList<>();
                            for (int i = 0; i < archives.length(); i++) {
                                JSONObject object = archives.getJSONObject(i);
                                Article article = new Article();
                                article.setId(object.getInt("id"));
                                article.setImgUrl(object.getString("thumb"));
                                article.setTitle(object.getString("title"));
                                article.setDesc(object.getString("des").toString().trim());
                                articles.add(article);
                            }
                            product.setArticles(articles);
                            List<Fitting> fittings = new ArrayList<Fitting>();
                            for (int i = 0; i < parts.length(); i++) {
                                JSONObject object = parts.getJSONObject(i);
                                Fitting f = new Fitting();
                                f.setId(object.getInt("id"));
                                f.setImgUrl(object.getString("thumb"));
                                f.setTitle(object.getString("title"));
                                fittings.add(f);
                            }
                            product.setFittings(fittings);
                            aboutProList = new ArrayList<>();
                            for (int i = 0; i < products.length(); i++) {
                                JSONObject object = products.getJSONObject(i);
                                Product p = new Product();
                                p.setId(object.getInt("id"));
                                p.setImgUrl(object.getString("thumb"));
                                p.setName(object.getString("title"));
                                aboutProList.add(p);
                            }
                            handler.sendEmptyMessage(1);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void addProduct() {
        String url = ServicePort.USER_PRODUCT_ADD;
        XUtilHttp api = new XUtilHttp();
        Map<String, String> map = new HashMap<>();
        map.put("product_id", product.getId() + "");
        RequestParams params = api.getParam(this, url, map);
        params.addBodyParameter("product_id", product.getId() + "");
        api.post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String responce = result.toString();
                LogUtils.showLogD("--------添加常用设备返回数据:" + result.toString());
                if (!TextUtils.isEmpty(responce)) {
                    try {
                        JSONObject jsonObject = new JSONObject(responce);
                        int err_no = jsonObject.getInt("err_no");
                        if (err_no == 0) {
                            LogUtils.showCenterToast(ProductDetailActivity.this, "添加成功");
                        } else {
                            LogUtils.showCenterToast(ProductDetailActivity.this, jsonObject.getInt("err_no") + "" + jsonObject.getString("err_msg"));
                        }
                    } catch (JSONException e) {

                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }


    public void callPhone(String phoneNum) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        startActivity(intent);
    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 1) {
                changeFrame(0);
            }
        }
    };


    public Product getProduct() {
        return product;
    }

    public List<Product> getAboutProList() {
        return aboutProList;
    }
}
