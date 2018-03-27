package com.gengli.glservice.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gengli.glservice.R;
import com.gengli.glservice.bean.Message;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.List;

public class MessageAdapter extends BaseAdapter{

    private Context context;
    private List<Message> messageList;
    private LayoutInflater inflater;

    public MessageAdapter(Context context, List<Message> messageList) {
        this.context = context;
        this.messageList = messageList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return messageList.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_message, null);
            holder.message_time_text = (TextView)convertView.findViewById(R.id.message_time_text);
            holder.text_message_title = (TextView) convertView.findViewById(R.id.text_message_title);
            holder.text_message_content = (TextView) convertView.findViewById(R.id.text_message_content);
            holder.img_message = (ImageView) convertView.findViewById(R.id.img_message);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Message message = messageList.get(position);
        holder.text_message_title.setText(message.getTitle());
        holder.text_message_content.setText(message.getContent());
        holder.message_time_text.setText(message.getTime());
        ImageOptions imageOptions = new ImageOptions.Builder()
                .setLoadingDrawableId(R.mipmap.ic_launcher)
                .setFailureDrawableId(R.mipmap.ic_launcher)
                .build();
        x.image().bind(holder.img_message, message.getImgUrl(), imageOptions);
//        ImageLoader.getInstance().displayImage(message.getImgUrl(), holder.img_message);
        return convertView;
    }



    private class ViewHolder {
        private ImageView img_message;
        private TextView text_message_title;
        private TextView text_message_content;
        private TextView message_time_text;
    }

}