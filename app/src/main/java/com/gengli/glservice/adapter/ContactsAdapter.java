package com.gengli.glservice.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gengli.glservice.R;
import com.gengli.glservice.activity.ArticleDetailActivity;
import com.gengli.glservice.bean.Category;
import com.gengli.glservice.bean.Contacts;
import com.gengli.glservice.http.ServicePort;
import com.gengli.glservice.http.XUtilHttp;
import com.gengli.glservice.util.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactsAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<Contacts> contactsList;
    public boolean isShowDel = false;
    private DelClickLintener listener;
    private SetDefaultClickLintener defaultClickLintener;

    public void setDefaultClickLintener(SetDefaultClickLintener defaultClickLintener){
        this.defaultClickLintener = defaultClickLintener;
    }
    public void setDelClickLintener(DelClickLintener listener) {
        this.listener = listener;
    }

    public ContactsAdapter(Context context, List<Contacts> contactsList) {
        this.context = context;
        this.contactsList = contactsList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return contactsList.size();
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
            convertView = inflater.inflate(R.layout.item_contacts, parent, false);
            holder.item_contacts_delete_bt = (ImageView) convertView.findViewById(R.id.item_contacts_delete_bt);
            holder.item_contacts_default_img = (ImageView) convertView.findViewById(R.id.item_contacts_default_img);
            holder.item_contacts_logo = (TextView) convertView.findViewById(R.id.item_contacts_logo);
            holder.item_contacts_name = (TextView) convertView.findViewById(R.id.item_contacts_name);
            holder.item_contacts_phone = (TextView) convertView.findViewById(R.id.item_contacts_phone);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Contacts contacts = contactsList.get(position);
        String name = contacts.getName();
        if (!TextUtils.isEmpty(name)){
            holder.item_contacts_logo.setText(name.substring(0,1));
        }
        holder.item_contacts_name.setText(contacts.getName());
        holder.item_contacts_phone.setText(contacts.getPhone());
        if (contacts.is_default())
            holder.item_contacts_default_img.setImageResource(R.drawable.img_default_sel);
        else
            holder.item_contacts_default_img.setImageResource(R.drawable.img_default);


        if (isShowDel)
            showDel(holder.item_contacts_delete_bt);
        else
            hideDel(holder.item_contacts_delete_bt);

        holder.item_contacts_default_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (!contacts.is_default()) {
//                    setDefault(contacts.getId());
//                }
                defaultClickLintener.click(position);

            }
        });

        holder.item_contacts_delete_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.delClick(position);
            }
        });
        return convertView;
    }

    private static class ViewHolder {
        private ImageView item_contacts_delete_bt;
        private TextView item_contacts_logo;
        private TextView item_contacts_name;
        private TextView item_contacts_phone;
        private ImageView item_contacts_default_img;
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

    public interface SetDefaultClickLintener {
        void click(int position);
    }




}