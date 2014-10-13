package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.randy.client.v2hot.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by randy on 14-10-4.
 */
public class ReplyAdapter extends BaseAdapter{
    Context context;
    List<HashMap<String,String>> list = new ArrayList<HashMap<String, String>>();
    String username;
    public ReplyAdapter(Context context,List<HashMap<String,String>> list,String username){
        this.context = context;
        this.list = list;
        this.username = username;
    }

    static class ViewHolder{
        @InjectView(R.id.tv_username) TextView username;
        @InjectView(R.id.tv_reply) TextView content;
        @InjectView(R.id.iv_avatar) ImageView avatar;

        public ViewHolder(View view){
            ButterKnife.inject(this,view);
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        LayoutInflater mInflater = LayoutInflater.from(context);

        if(view == null){
            view = mInflater.inflate(R.layout.reply_item,viewGroup,false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)view.getTag();
        }



        String usr_name = list.get(i).get("username");
        if(usr_name.equals(this.username)){
            viewHolder.username.setText(usr_name + "(楼主)");
        }else{
            viewHolder.username.setText(usr_name);
        }
        viewHolder.content.setText(list.get(i).get("content"));
        //Universal Image Loader Configure
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .build();
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(list.get(i).get("avatar"),viewHolder.avatar,options);

        return view;
    }
}
