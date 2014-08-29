package com.randy.client.v2hot;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ContentActivity extends Activity {

    private ProgressBar pb_load;
    private ListView lv_reply;
    private List<HashMap<String,String>> repliesList = new ArrayList<HashMap<String, String>>();
    private TextView headertitle;
    private TextView headercontent;
    private View header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(false);
        }

        setContentView(R.layout.activity_content);

        //获取相应的topic id
        Intent getIntent = getIntent();
        String id = getIntent.getStringExtra("id");
        final String title = getIntent.getStringExtra("title");
        header = getLayoutInflater().inflate(R.layout.topic_header,null);
        headertitle = (TextView)header.findViewById(R.id.header_title);
        headercontent = (TextView)header.findViewById(R.id.header_content);
        headertitle.setText(title);

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
                    headercontent.setText(content);
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
                        repliesList.add(map);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }//遍历结束
                lv_reply = (ListView)ContentActivity.this.findViewById(R.id.lv_reply);
                lv_reply.addHeaderView(header);
                SimpleAdapter replyAdapter = new SimpleAdapter(ContentActivity.this,repliesList,R.layout.reply_item,new String[]{"content","username"},new int[]{R.id.tv_reply,R.id.tv_username});
                lv_reply.setAdapter(replyAdapter);
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
