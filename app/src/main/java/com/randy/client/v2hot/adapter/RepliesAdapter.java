package com.randy.client.v2hot.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.randy.client.v2hot.R;
import com.v2ex.api.Reply;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class RepliesAdapter extends ArrayAdapter<Reply> {

    static class ViewHolder {
        @InjectView(R.id.tv_username)
        TextView username;

        @InjectView(R.id.tv_reply)
        TextView content;

        @InjectView(R.id.iv_avatar)
        ImageView avatar;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    private static final DisplayImageOptions IMAGE_OPTIONS = new DisplayImageOptions.Builder()
            .cacheOnDisk(true)
            .cacheInMemory(true)
            .build();

    private final LayoutInflater inflater;
    private final String hostUsername;

    public RepliesAdapter(Context context, String hostUsername) {
        super(context, 0);
        this.inflater = LayoutInflater.from(context);
        this.hostUsername = hostUsername;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.reply_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Reply item = getItem(position);

        String username = item.member.username;
        holder.username.setText(username.equals(hostUsername) ? username + "(楼主)" : username);

        holder.content.setText(item.content);

        String avatar = item.member.avatarMini;
        if (avatar.startsWith("//")) {
            avatar = "https:" + avatar;
        }

        ImageLoader.getInstance().displayImage(avatar, holder.avatar, IMAGE_OPTIONS);

        return convertView;
    }

}
