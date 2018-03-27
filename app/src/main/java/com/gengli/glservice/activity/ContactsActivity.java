package com.gengli.glservice.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.gengli.glservice.R;
import com.gengli.glservice.adapter.ContactsAdapter;
import com.gengli.glservice.adapter.FavArticleAdapter;
import com.gengli.glservice.adapter.ProControlAdapter;
import com.gengli.glservice.bean.Article;
import com.gengli.glservice.bean.Contacts;
import com.gengli.glservice.http.ServicePort;
import com.gengli.glservice.http.XUtilHttp;
import com.gengli.glservice.util.LogUtils;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ContentView(R.layout.activity_contacts)
public class ContactsActivity extends BaseActivity {
    private int page = 1;
    private List<Contacts> contactsList;
    public boolean isShowDel = false;
    private ContactsAdapter adapter;

    @ViewInject(R.id.contacts_list_view)
    private PullToRefreshListView contacts_list_view;

    @ViewInject(R.id.contacts_edit_bt)
    private TextView contacts_edit_bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();

    }

    @Override
    protected void onResume() {
        super.onResume();
        getContacts(1);
    }

    private void init() {
        contactsList = new ArrayList<>();
        adapter = new ContactsAdapter(this, contactsList);
        contacts_list_view.setAdapter(adapter);
        contacts_list_view.setMode(PullToRefreshBase.Mode.BOTH);
        contacts_list_view.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                page = 1;
                getContacts(1);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page++;
                getContacts(page);
            }
        });

        adapter.setDelClickLintener(new ContactsAdapter.DelClickLintener() {
            @Override
            public void delClick(int position) {
                removeContacts(contactsList.get(position).getId());
                contactsList.remove(position);
                adapter.notifyDataSetChanged();
            }
        });

        adapter.setDefaultClickLintener(new ContactsAdapter.SetDefaultClickLintener() {
            @Override
            public void click(int position) {
                setDefault(contactsList.get(position).getId());
            }
        });
    }

    @Event(value = R.id.contacts_list_view, type = AdapterView.OnItemClickListener.class)
    private void onListClick(AdapterView<?> parent, View view, int position, long id) {
        startActivity(new Intent(ContactsActivity.this, ContactsInfoActivity.class).putExtra("cur_contacts", contactsList.get(position-1)));
    }

    @Event(value = R.id.contacts_back_bt, type = View.OnClickListener.class)
    private void backClick(View view) {
        finish();
    }


    @Event(value = R.id.contacts_add_bt)
    private void addClick(View view) {
        startActivity(new Intent(this, AddContactsActivity.class));
    }


    @Event(value = R.id.contacts_edit_bt)
    private void editClick(View view) {
        if (isShowDel) {
            contacts_edit_bt.setText("编辑");
            isShowDel = false;
            adapter.isShowDel = false;
            adapter.notifyDataSetChanged();
        } else {
            contacts_edit_bt.setText("完成");
            isShowDel = true;
            adapter.isShowDel = true;
            adapter.notifyDataSetChanged();
        }
    }


    public void removeContacts(int id) {
        String url = ServicePort.CONTACT_REMOVE;
        XUtilHttp api = new XUtilHttp();
        Map<String, String> map = new HashMap<>();
        map.put("id", id + "");
        RequestParams params = api.getParam(this, url, map);
        params.addBodyParameter("id", id + "");
        api.post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String responce = result.toString();
                LogUtils.showLogD("----->删除返回数据----->" + responce);
                if (!TextUtils.isEmpty(responce)) {
                    try {
                        JSONObject jsonObject = new JSONObject(responce);
                        int err_no = jsonObject.getInt("err_no");
                        if (err_no == 0) {
//                            handler.sendEmptyMessage(103);
                            LogUtils.showCenterToast(ContactsActivity.this, "删除成功");
                        } else {
                            LogUtils.showCenterToast(ContactsActivity.this, jsonObject.getInt("err_no") + "" + jsonObject.getString("err_msg"));
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


    private void getContacts(final int page) {
        final List<Contacts> listTemp = new ArrayList<>();
        String url = ServicePort.CONTACT_LISTS;
        XUtilHttp api = new XUtilHttp();
        Map<String, String> map = new HashMap<>();
        map.put("page", page + "");
        RequestParams params = api.getParam(this, url, map);
        params.addBodyParameter("page", page + "");
        api.post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtils.showLogD("--------联系人列表返回数据:" + result.toString());
                String responce = result.toString();
                if (!TextUtils.isEmpty(responce)) {
                    try {
                        JSONObject jsonObject = new JSONObject(responce);
                        int err_no = jsonObject.getInt("err_no");
                        if (err_no == 0) {
                            JSONObject results = jsonObject.getJSONObject("results");
                            JSONArray lists = results.getJSONArray("lists");
                            if (lists != null && lists.length() > 0) {
                                for (int i = 0; i < lists.length(); i++) {
                                    JSONObject object = lists.getJSONObject(i);
                                    Contacts contacts = new Contacts();
                                    contacts.setId(object.getInt("id"));
                                    contacts.setName(object.getString("realname"));
                                    contacts.setPhone(object.getString("mobile"));
                                    int isDefault = object.getInt("is_default");
                                    if (isDefault == 1)
                                        contacts.setIs_default(true);
                                    else
                                        contacts.setIs_default(false);
                                    listTemp.add(contacts);
                                }
                                if (page == 1) {
                                    contactsList.clear();
                                    if (listTemp.size() == 0) {
                                        LogUtils.showCenterToast(ContactsActivity.this, "没有数据");
                                    } else if (listTemp.size() > 0) {
                                        contactsList.addAll(listTemp);
                                        adapter.notifyDataSetChanged();
                                    }
                                } else if (page > 1) {
                                    if (listTemp.size() == 0) {
                                        LogUtils.showCenterToast(ContactsActivity.this, "没有更多数据");
                                    } else if (listTemp.size() > 0) {
                                        contactsList.addAll(listTemp);
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            } else {
                                if (page > 1) {
                                    LogUtils.showCenterToast(ContactsActivity.this, "没有更多数据");
                                } else {
                                    handler.sendEmptyMessage(1);
                                    LogUtils.showLogD("没有数据");
                                }
                            }
                            contacts_list_view.onRefreshComplete();
                        } else if (err_no == 2100) {
                            handler.sendEmptyMessage(3);
                            LogUtils.showCenterToast(ContactsActivity.this, jsonObject.getInt("err_no") + "" + jsonObject.getString("err_msg"));
                        } else {
                            LogUtils.showCenterToast(ContactsActivity.this, jsonObject.getInt("err_no") + "" + jsonObject.getString("err_msg"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    LogUtils.showCenterToast(ContactsActivity.this, "数据错误");
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

    private void setDefault(int id) {
        String url = ServicePort.CONTACT_SET_DEFAULT;
        XUtilHttp api = new XUtilHttp();
        Map<String, String> map = new HashMap<>();
        map.put("id", id + "");
        RequestParams params = api.getParam(this, url, map);
        params.addBodyParameter("id", id + "");
        api.post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String responce = result.toString();
                LogUtils.showLogD("----->返回数据----->" + responce);
                if (!TextUtils.isEmpty(responce)) {
                    try {
                        JSONObject jsonObject = new JSONObject(responce);
                        int err_no = jsonObject.getInt("err_no");
                        if (err_no == 0) {
                            LogUtils.showCenterToast(ContactsActivity.this, "设置成功");
                            handler.sendEmptyMessage(10);
                        } else {
                            LogUtils.showCenterToast(ContactsActivity.this, jsonObject.getInt("err_no") + "" + jsonObject.getString("err_msg"));
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

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
//                order_no_data.setVisibility(View.VISIBLE);
//                contacts_list_view.setVisibility(View.INVISIBLE);
            } else if (msg.what == 2) {
                adapter.notifyDataSetChanged();
            } else if (msg.what == 3) {
                startActivity(new Intent(ContactsActivity.this, LoginActivity.class));
            } else if (msg.what == 10) {
                getContacts(1);
            }
        }
    };
}
