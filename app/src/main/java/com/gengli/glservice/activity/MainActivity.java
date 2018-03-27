package com.gengli.glservice.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gengli.glservice.R;
import com.gengli.glservice.fragment.BaoXiuFragment;
import com.gengli.glservice.fragment.HelpFragment;
import com.gengli.glservice.fragment.MainFragment;
import com.gengli.glservice.fragment.MineFragment;
import com.gengli.glservice.fragment.ProductFragment;
import com.gengli.glservice.http.ServicePort;
import com.gengli.glservice.http.XUtilHttp;
import com.gengli.glservice.util.DatasUtil;
import com.gengli.glservice.util.FileUtils;
import com.gengli.glservice.util.LogUtils;
import com.gengli.glservice.util.SystemMsgUtil;
import com.gengli.glservice.util.Util;
import com.gengli.glservice.view.UpdatePopu;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;

public class MainActivity extends FragmentActivity implements View.OnClickListener {


    private LinearLayout bottom_main_bt, bottom_product_bt, bottom_help_bt, bottom_mine_bt, bottom_baoxiu_bt;
    private TextView main_first_text, main_second_text, main_third_text, main_fourth_text;
    private ImageView main_first_img, main_seconde_img, main_third_img, main_fourth_img;
    private BaoXiuFragment baoXiuFragment;
    private MainFragment mainFragment;
    private ProductFragment productFragment;
    private HelpFragment helpFragment;
    private MineFragment mineFragment;
    private FragmentManager fragmentManager;
    private List<TextView> textList;
    private List<ImageView> vList;
    private Long time = (long) 0;
    private UpdatePopu popUpdate;
    private int[] bottom_img_one = {R.drawable.img_tab_home_normal, R.drawable.img_tab_guide_normal, R.drawable.img_tab_product_normal,
            R.drawable.img_tab_mine_normal};
    private int[] bottom_img_two = {R.drawable.img_tab_home_checked, R.drawable.img_tab_guide_checked, R.drawable.img_tab_product_checked,
            R.drawable.img_tab_mine_checked};

    private FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Intent intent = getIntent();
//        Bundle bundle = intent.getExtras();
//        int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
        initView();
        checkUpdate();
    }

    private void initView() {
        frameLayout = (FrameLayout) findViewById(R.id.main_content);
        bottom_main_bt = (LinearLayout) findViewById(R.id.bottom_main_bt);
        bottom_product_bt = (LinearLayout) findViewById(R.id.bottom_product_bt);
        bottom_help_bt = (LinearLayout) findViewById(R.id.bottom_help_bt);
        bottom_mine_bt = (LinearLayout) findViewById(R.id.bottom_mine_bt);
        bottom_baoxiu_bt = (LinearLayout) findViewById(R.id.bottom_baoxiu_bt);

        main_first_img = (ImageView) findViewById(R.id.main_first_img);
        main_seconde_img = (ImageView) findViewById(R.id.main_second_img);
        main_third_img = (ImageView) findViewById(R.id.main_third_img);
        main_fourth_img = (ImageView) findViewById(R.id.main_fourth_img);

        textList = new ArrayList<TextView>();
        vList = new ArrayList<ImageView>();
        vList.add(main_first_img);
        vList.add(main_seconde_img);
        vList.add(main_third_img);
        vList.add(main_fourth_img);

        main_first_text = (TextView) findViewById(R.id.main_first_text);
        main_second_text = (TextView) findViewById(R.id.main_second_text);
        main_third_text = (TextView) findViewById(R.id.main_third_text);
        main_fourth_text = (TextView) findViewById(R.id.main_fourth_text);

        if (textList != null) {
            textList.clear();
        }
        textList.add(main_first_text);
        textList.add(main_second_text);
        textList.add(main_third_text);
        textList.add(main_fourth_text);

        bottom_main_bt.setOnClickListener(this);
        bottom_product_bt.setOnClickListener(this);
        bottom_help_bt.setOnClickListener(this);
        bottom_mine_bt.setOnClickListener(this);
        bottom_baoxiu_bt.setOnClickListener(this);

        fragmentManager = getSupportFragmentManager();
        //设置默认
        changeFrame(0);
    }

    public void changeFrame(int index) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        hideAllFrame(transaction);
        changeMenuAttr(index);
        switch (index) {
            case 0:
                if (mainFragment == null) {
                    mainFragment = new MainFragment();
                    transaction.add(R.id.main_content, mainFragment);
                } else {
                    transaction.show(mainFragment);
                }
                break;
            case 1:
                if (productFragment == null) {
                    productFragment = new ProductFragment();
                    transaction.add(R.id.main_content, productFragment);
                } else {
                    transaction.show(productFragment);
                }
                break;

            case 2:
                if (helpFragment == null) {
                    helpFragment = new HelpFragment();
                    transaction.add(R.id.main_content, helpFragment);
                } else {
                    transaction.show(helpFragment);
                }
                break;
            case 3:
                if (mineFragment == null) {
                    mineFragment = new MineFragment();
                    transaction.add(R.id.main_content, mineFragment);
                } else {
                    transaction.show(mineFragment);
                }
                break;

            case 4:
                if (baoXiuFragment == null) {
                    baoXiuFragment = new BaoXiuFragment();
                    transaction.add(R.id.main_content, baoXiuFragment);
                } else {
                    transaction.show(baoXiuFragment);
                }
                break;
            default:
                break;
        }
        transaction.commit();
    }

    private void hideAllFrame(FragmentTransaction transaction) {
        if (mainFragment != null) {
            transaction.hide(mainFragment);
        }
        if (productFragment != null) {
            transaction.hide(productFragment);
        }
        if (helpFragment != null) {
            transaction.hide(helpFragment);
        }
        if (mineFragment != null) {
            transaction.hide(mineFragment);
        }
        if (baoXiuFragment != null) {
            transaction.hide(baoXiuFragment);
        }
    }

    /**
     * 修改菜单属性
     */
    @SuppressWarnings("deprecation")
    private void changeMenuAttr(int index) {
        for (int i = 0; i < textList.size(); i++) {
            if (index == i) {
                textList.get(i).setTextColor(getResources().getColor(R.color.color_4373fe));
                vList.get(i).setImageResource(bottom_img_two[i]);
            } else {
                textList.get(i).setTextColor(getResources().getColor(R.color.color_515151));
                vList.get(i).setImageResource(bottom_img_one[i]);
            }
        }
    }

    /**
     * 双击退出
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (time == 0) {
                time = System.currentTimeMillis();
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            } else {
                if ((System.currentTimeMillis() - time) > 2000) {
                    Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                    time = System.currentTimeMillis();
                } else {
//                    Logout();
                    this.finish();
                }
            }
            return true;
        } else
            return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bottom_main_bt:
                changeFrame(0);
                break;
            case R.id.bottom_product_bt:
                changeFrame(1);
                break;
            case R.id.bottom_help_bt:
                changeFrame(2);
                break;
            case R.id.bottom_mine_bt:
                changeFrame(3);
                break;
            case R.id.bottom_baoxiu_bt:
                if (DatasUtil.isLogin(this)) {
                    changeFrame(4);
                } else {
                    startActivity(new Intent(this, LoginActivity.class));
                }
            default:
                break;
        }
    }

    private String new_version;
    private String cur_version;
    private String filePATH;

    private void checkUpdate() {
        cur_version = String.valueOf(SystemMsgUtil.getVersionName(this));
        String url = ServicePort.CLIENT_UPDATE;
        XUtilHttp api = new XUtilHttp();
        Map<String, String> map = new HashMap<>();
        RequestParams params = api.getParam(this, url, map);
        api.post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtils.showLogD("--------版本更新返回数据:" + result.toString());
                String responce = result.toString();
                if (!TextUtils.isEmpty(responce)) {
                    try {
                        JSONObject jsonObject = new JSONObject(responce);
                        int err_no = jsonObject.getInt("err_no");
                        if (err_no == 0) {
                            JSONObject results = jsonObject.getJSONObject("results");
//                            needUpdate = results.getBoolean("update");
                            new_version = results.getString("new_version");
//                            old_version = results.getString("old_version");
                            filePATH = results.getString("file");

//                            needUpdate = Util.aVb(Util.chartStr(new_version), Util.chartStr(cur_version));
                            LogUtils.showLogD("...cur_version === " + cur_version);
                            LogUtils.showLogD("...new_version === " + new_version);
                            int i = Util.compareVersion(cur_version, new_version);
                            if (i == 0) {
                                LogUtils.showLogD("...0...");
                            } else if (i == 1) {
                                LogUtils.showLogD("...1...");
                            } else if (i == -1) {
                                LogUtils.showLogD("...-1...");
                                handler.sendEmptyMessage(0);
                            }
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


    private void clientUpdate() {
        String url = filePATH;
        if (TextUtils.isEmpty(filePATH)) {
            return;
        }
        XUtilHttp api = new XUtilHttp();
        Map<String, String> map = new HashMap<>();
        RequestParams params = api.getParam(this, url, map);
        params.setSaveFilePath(FileUtils.getRootPath());
        params.setAutoRename(true);
        api.post(params, new Callback.ProgressCallback<File>() {
            @Override
            public void onWaiting() {
            }

            @Override
            public void onStarted() {
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                LogUtils.showLogD("current === " + current + "， total === " + total);
            }

            @Override
            public void onSuccess(File result) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(result), "application/vnd.android.package-archive");
                startActivity(intent);
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
            if (msg.what == 0) {
                popUpdate = new UpdatePopu(MainActivity.this, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (v.getId()) {
                            case R.id.pop_update_bt1:
                                popUpdate.dismiss();
                                break;
                            case R.id.pop_update_bt2:
                                clientUpdate();
                                LogUtils.showCenterToast(MainActivity.this, "正在下载请稍候");
                                break;
                        }

                    }
                });
                popUpdate.showPopupWindow(frameLayout);
            }
        }
    };
}
