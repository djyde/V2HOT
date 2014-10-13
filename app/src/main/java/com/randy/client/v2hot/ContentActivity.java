package com.randy.client.v2hot;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ShareActionProvider;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import adapter.ReplyAdapter;


public class ContentActivity extends Activity {

    private ProgressBar pb_load;
    private ListView lv_reply;
    private List<HashMap<String,String>> repliesList = new ArrayList<HashMap<String, String>>();
    private TextView header_title;
    private TextView header_content;
    private TextView header_username;
    private View header;
    private String title;
    private String url;
    private String username;
    private ShareActionProvider shareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(false);
        }

        setContentView(R.layout.activity_content);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);


        //获取相应的topic id
        Intent getIntent = getIntent();
        String id = getIntent.getStringExtra("id");
        title = getIntent.getStringExtra("title");
        username = getIntent.getStringExtra("username");
        url = getIntent.getStringExtra("url");
        header = getLayoutInflater().inflate(R.layout.topic_header,null);
        header_title = (TextView)header.findViewById(R.id.header_title);
        header_content = (TextView)header.findViewById(R.id.header_content);
        header_username = (TextView)header.findViewById(R.id.header_username);

        header_title.setText(title);
        header_username.setText(username);

        RequestQueue queue = Volley.newRequestQueue(ContentActivity.this);
        //根据API(http://github.com/djyde/v2ex-api)获取topic内容.
        JsonArrayRequest topicInfoRequest = new JsonArrayRequest("http://www.v2ex.com/api/topics/show.json?id=" + id,new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                pb_load = (ProgressBar)ContentActivity.this.findViewById(R.id.pb_load);
                pb_load.setVisibility(View.GONE);
                try {
                    //获取topic内容
                    String content = response.getJSONObject(0).getString("content");
                    header_content.setText(content);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //处理错误
                Toast.makeText(ContentActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

        //获取回复
        JsonArrayRequest replyRequest = new JsonArrayRequest("http://www.v2ex.com/api/replies/show.json?topic_id=" + id,new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                //遍历所有回复
                for (int i=0;i<response.length();i++){
                    HashMap<String,String> map = new HashMap<String, String>();
                    try {
                        map.put("content",response.getJSONObject(i).getString("content"));
                        map.put("username",response.getJSONObject(i).getJSONObject("member").getString("username"));
                        //判断是否为gravatar
                        String avatar = response.getJSONObject(i).getJSONObject("member").getString("avatar_mini");
                        if (String.valueOf(avatar.charAt(0)).equals("/")){
                            map.put("avatar","http:" + response.getJSONObject(i).getJSONObject("member").getString("avatar_mini"));
                        }else{
                            map.put("avatar",response.getJSONObject(i).getJSONObject("member").getString("avatar_mini"));
                        }
                        repliesList.add(map);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }//遍历结束
                lv_reply = (ListView)ContentActivity.this.findViewById(R.id.lv_reply);
                lv_reply.addHeaderView(header);
                lv_reply.setAdapter(new ReplyAdapter(ContentActivity.this,repliesList,username));
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ContentActivity.this,"请检查网络",Toast.LENGTH_LONG).show();
            }
        });

        queue.add(topicInfoRequest);
        queue.add(replyRequest);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.open_browser:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public Intent getShareIntent(){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT,title + "\n" + url + "\n" + "分享自[V2HOT]");
        intent.setType("text/plain");
        return intent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.content_activity, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);
        shareActionProvider = (ShareActionProvider) item.getActionProvider();
        shareActionProvider.setShareIntent(getShareIntent());
        return true;
    }
}
