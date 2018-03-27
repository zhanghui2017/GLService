package com.gengli.glservice.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gengli.glservice.R;
import com.gengli.glservice.activity.LoginActivity;
import com.gengli.glservice.activity.ProSearchActivuty;
import com.gengli.glservice.activity.ProductListActivity;
import com.gengli.glservice.adapter.CategoryAdapter;
import com.gengli.glservice.bean.Category;
import com.gengli.glservice.http.ServicePort;
import com.gengli.glservice.http.XUtilHttp;
import com.gengli.glservice.util.LogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ProductFragment extends BaseFragment {

    private LinearLayout product_search_view;
    private GridView product_grid_view;
    private CategoryAdapter adapter;
    private List<Category> categories;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product, container, false);
        init(view);
        getCategory();
        return view;
    }

    private void init(View view) {
        categories = new ArrayList<>();
        product_search_view = (LinearLayout) view.findViewById(R.id.product_search_view);
        product_grid_view = (GridView) view.findViewById(R.id.product_grid_view);
        adapter = new CategoryAdapter(getActivity(), categories);
        product_grid_view.setAdapter(adapter);
        product_grid_view.setOnItemClickListener(new ItemClick());
        product_search_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ProSearchActivuty.class));
            }
        });
    }

    private class ItemClick implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getActivity(), ProductListActivity.class);
            intent.putExtra("category_name", categories.get(position).getName());
            intent.putExtra("category_id", categories.get(position).getId());
            intent.putExtra("category_list", (Serializable) categories);
            startActivity(intent);
        }
    }


    private void getCategory() {
        String url = ServicePort.PRODUCT_CATEGORY;
        XUtilHttp api = new XUtilHttp();
        Map<String, String> map = new HashMap<>();
        RequestParams params = api.getParam(getActivity(), url, map);
        api.post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtils.showLogD("--------产品系列返回数据:" + result.toString());
                String responce = result.toString();
                if (!TextUtils.isEmpty(responce)) {
                    try {
                        JSONObject jsonObject = new JSONObject(responce);
                        int err_no = jsonObject.getInt("err_no");
                        if (err_no == 0) {
                            JSONObject results = jsonObject.getJSONObject("results");
                            final JSONArray lists = results.getJSONArray("lists");
                            if (lists != null && lists.length() > 0) {
                                for (int i = 0; i < lists.length(); i++) {
                                    JSONObject item = lists.getJSONObject(i);
                                    Category category = new Category();
                                    category.setId(item.getInt("id"));
                                    category.setImgUrl(item.getString("thumb"));
                                    category.setName(item.getString("name"));
                                    categories.add(category);
                                }
                                adapter.notifyDataSetChanged();
                            } else {
                                LogUtils.showLogD("没有数据");
                            }
                        } else if (err_no == 2100) {
                            handler.sendEmptyMessage(1);
                            LogUtils.showCenterToast(getActivity(), jsonObject.getInt("err_no") + "" + jsonObject.getString("err_msg"));
                        } else {
                            LogUtils.showCenterToast(getActivity(), jsonObject.getInt("err_no") + "" + jsonObject.getString("err_msg"));
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

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 1) {
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        }
    };
}
