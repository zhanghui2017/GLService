package com.gengli.glservice.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.gengli.glservice.R;
import com.gengli.glservice.activity.ArticleDetailActivity;
import com.gengli.glservice.activity.ArticleListActivity;
import com.gengli.glservice.activity.CreatFormActivity;
import com.gengli.glservice.activity.FeedbackActivity;
import com.gengli.glservice.activity.LoginActivity;
import com.gengli.glservice.activity.MainActivity;
import com.gengli.glservice.activity.MainSearchActivuty;
import com.gengli.glservice.activity.MessageActivity;
import com.gengli.glservice.activity.ProSearchActivuty;
import com.gengli.glservice.activity.RepairsActivity;
import com.gengli.glservice.adapter.ArticleAdapter;
import com.gengli.glservice.bean.Article;
import com.gengli.glservice.http.ServicePort;
import com.gengli.glservice.http.XUtilHttp;
import com.gengli.glservice.util.DatasUtil;
import com.gengli.glservice.util.ImageUtil;
import com.gengli.glservice.util.LogUtils;
import com.gengli.glservice.util.SystemMsgUtil;
import com.gengli.glservice.view.ImageSlideView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainFragment extends BaseFragment implements View.OnClickListener {

    private ImageSlideView img_slider_view;
    private int messageCount = 0;
    private List<String> imageUrls;
    private int CLIENT_RUN_BANNERS = 1001;
    private LinearLayout main_search_view_bt;
//    private ImageView main_fragment_message_bt;
    private ImageView main_zxbx_bt;
    private ImageView main_wxd_bt;
    private ImageView main_yjfk_bt;
    private ImageView main_cjgz_bt;
    private ListView main_list_view;
    private ArticleAdapter adapter;
    private List<Article> articleList;
    private TextView main_more_bt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        init(view);
        runClient();
        getTopActicle();
        return view;
    }

    private void init(View view) {
        img_slider_view = (ImageSlideView) view.findViewById(R.id.img_slider_view);
        main_search_view_bt = (LinearLayout) view.findViewById(R.id.main_search_view_bt);
        main_zxbx_bt = (ImageView) view.findViewById(R.id.main_zxbx_bt);
        main_wxd_bt = (ImageView) view.findViewById(R.id.main_wxd_bt);
        main_yjfk_bt = (ImageView) view.findViewById(R.id.main_yjfk_bt);
        main_cjgz_bt = (ImageView) view.findViewById(R.id.main_cjgz_bt);
        main_list_view = (ListView) view.findViewById(R.id.main_list_view);
//        main_fragment_message_bt = (ImageView) view.findViewById(R.id.main_fragment_message_bt);
//        main_fragment_message_bt.setOnClickListener(this);

        main_list_view.setFocusable(false);
        main_more_bt = (TextView) view.findViewById(R.id.main_more_bt);
        main_zxbx_bt.setOnClickListener(this);
        main_wxd_bt.setOnClickListener(this);
        main_yjfk_bt.setOnClickListener(this);
        main_search_view_bt.setOnClickListener(this);
        main_more_bt.setOnClickListener(this);
        main_cjgz_bt.setOnClickListener(this);

        articleList = new ArrayList<>();
        adapter = new ArticleAdapter(getActivity(), articleList);
        main_list_view.setAdapter(adapter);
        main_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int articleid = articleList.get(position).getId();
                startActivity(new Intent(getActivity(), ArticleDetailActivity.class).putExtra("articleid", articleid));
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_search_view_bt:
                startActivity(new Intent(getActivity(), MainSearchActivuty.class));
                break;
            case R.id.main_zxbx_bt:
                if (DatasUtil.isLogin(getActivity())) {
                    startActivity(new Intent(getActivity(), CreatFormActivity.class));
                } else {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }

                break;
            case R.id.main_cjgz_bt:
                Intent intent = new Intent(getActivity(), ArticleListActivity.class);
                intent.putExtra("article_title", "常见故障");
                intent.putExtra("article_category_id", 7);
                startActivity(intent);
                break;
            case R.id.main_wxd_bt:
                if (DatasUtil.isLogin(getActivity())) {
                    startActivity(new Intent(getActivity(), RepairsActivity.class));
                } else {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }

                break;
            case R.id.main_yjfk_bt:
                startActivity(new Intent(getActivity(), FeedbackActivity.class));
                break;
            case R.id.main_more_bt:
                ((MainActivity) getActivity()).changeFrame(2);
                break;
//            case R.id.main_fragment_message_bt:
////                LogUtils.showCenterToast(getActivity(), "消息功能暂未开放");
//                startActivity(new Intent(getActivity(), MessageActivity.class));
//                break;
        }
    }

    public void runClient() {
        String deviceid = SystemMsgUtil.getSingleKey(getActivity());
        String screen = ImageUtil.getScreenWidth(getActivity()) + "*" + ImageUtil.getScreenHeight(getActivity());
        String brand = android.os.Build.MODEL;
        String pushtoken = "123";
        String url = ServicePort.GET_CLIENT_RUN;
        XUtilHttp api = new XUtilHttp();
        Map<String, String> map = new HashMap<>();
        map.put("deviceid", deviceid);
        map.put("pushtoken", pushtoken);
        map.put("brand", brand);
        map.put("screen", screen);
        RequestParams params = api.getParam(getActivity(), url, map);
        params.addBodyParameter("deviceid", deviceid);
        params.addBodyParameter("pushtoken", pushtoken);
        params.addBodyParameter("brand", brand);
        params.addBodyParameter("screen", screen);
        api.post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtils.showLogD("--------客户端启动返回数据:" + result.toString());
                String responce = result.toString();
                if (!TextUtils.isEmpty(responce)) {
                    try {
                        JSONObject jsonObject = new JSONObject(responce);
                        int err_no = jsonObject.getInt("err_no");
                        if (err_no == 0) {
                            JSONObject results = jsonObject.getJSONObject("results");
                            messageCount = results.getInt("num_msg");
                            final JSONArray banners = results.getJSONArray("banners");
                            imageUrls = new ArrayList<>();
                            for (int i = 0; i < banners.length(); i++) {
                                JSONObject object = banners.getJSONObject(i);
                                imageUrls.add(object.getString("thumb"));
                                LogUtils.showLogD("banner urls : " + imageUrls.get(i));
                            }
                            handler.sendEmptyMessage(CLIENT_RUN_BANNERS);
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
            if (msg.what == CLIENT_RUN_BANNERS) {
                for (int i = 0; i < imageUrls.size(); i++) {
                    img_slider_view.addImageUrl(imageUrls.get(i));
                }
                img_slider_view.setDelay(2000);
                img_slider_view.commit();
//                if (messageCount > 0) {
//                    main_fragment_message_bt.setImageResource(R.drawable.img_message_full);
//                } else {
//                    main_fragment_message_bt.setImageResource(R.drawable.img_message_null);
//                }
            } else if (msg.what == 3) {
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        }
    };

    private void getTopActicle() {
        String url = ServicePort.ARCHIVE_LISTS;
        XUtilHttp api = new XUtilHttp();
        Map<String, String> map = new HashMap<>();
        map.put("is_top", 1 + "");
        map.put("page", 1 + "");
        map.put("size", 5 + "");
        RequestParams params = api.getParam(getActivity(), url, map);
        params.addBodyParameter("is_top", 1 + "");
        params.addBodyParameter("page", 1 + "");
        params.addBodyParameter("size", 5 + "");
        api.post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtils.showLogD("--------耿力头条返回数据:" + result.toString());
                String responce = result.toString();
                if (!TextUtils.isEmpty(responce)) {
                    try {
                        JSONObject jsonObject = new JSONObject(responce);
                        int err_no = jsonObject.getInt("err_no");
                        if (err_no == 0) {
                            JSONObject results = jsonObject.getJSONObject("results");
                            JSONArray list = results.getJSONArray("list");
                            if (list != null && list.length() > 0) {
                                for (int i = 0; i < list.length(); i++) {
                                    JSONObject object = list.getJSONObject(i);
                                    Article article = new Article();
                                    article.setId(object.getInt("id"));
                                    article.setImgUrl(object.getString("thumb"));
                                    article.setTitle(object.getString("title"));
                                    article.setDesc(object.getString("des").toString().trim());
                                    articleList.add(article);
                                }
                                adapter.notifyDataSetChanged();
                            } else {
                                LogUtils.showCenterToast(getActivity(), "没有数据");
                            }

                        } else if (err_no == 2100) {
                            handler.sendEmptyMessage(3);
                            LogUtils.showCenterToast(getActivity(), jsonObject.getInt("err_no") + "" + jsonObject.getString("err_msg"));
                        } else {
                            LogUtils.showCenterToast(getActivity(), jsonObject.getInt("err_no") + "" + jsonObject.getString("err_msg"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    LogUtils.showCenterToast(getActivity(), "数据错误");
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


    @Override
    public void onDestroy() {
        super.onDestroy();
        img_slider_view.releaseResource();
    }
}
