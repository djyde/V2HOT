package com.randy.client.v2hot;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;


public class TitleListActivity extends Activity {

    private ListView lv_title;
    private ProgressBar pb_load;
    private List<HashMap<String,String>> topicList = new ArrayList<HashMap<String, String>>();
    private SimpleAdapter topicAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title_list);
        //创建volley请求队列
        final RequestQueue queue= Volley.newRequestQueue(TitleListActivity.this);
        //根据API获取热议主题
        final JsonArrayRequest request = new JsonArrayRequest("http://www.v2ex.com/api/topics/hot.json",new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                //取id、title、username
                for (int i=0;i<response.length();i++){
                    HashMap<String,String> map = new HashMap<String, String>();
                    try {
                        map.put("id",response.getJSONObject(i).getString("id"));
                        map.put("title",response.getJSONObject(i).getString("title"));
                        map.put("username",response.getJSONObject(i).getJSONObject("member").getString("username"));
                        map.put("url",response.getJSONObject(i).getString("url"));
                        topicList.add(map);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                topicAdapter = new SimpleAdapter(TitleListActivity.this,topicList,R.layout.topic_item,new String[]{"title"},new int[]{R.id.tv_title});
                lv_title = (ListView)TitleListActivity.this.findViewById(R.id.lv_title);
                //lv_title.removeAllViews();
                lv_title.setAdapter(topicAdapter);
                //加载完毕后progress bar消失
                pb_load = (ProgressBar)TitleListActivity.this.findViewById(R.id.pb_load);
                pb_load.setVisibility(View.GONE);

                lv_title.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        HashMap<String,String> info = (HashMap<String, String>) lv_title.getItemAtPosition(i);
                        String id = info.get("id");
                        String title = info.get("title");
                        String username = info.get("username");
                        String url = info.get("url");
                        Intent intent = new Intent(TitleListActivity.this,ContentActivity.class);
                        intent.putExtra("id",id);
                        intent.putExtra("title",title);
                        intent.putExtra("username",username);
                        intent.putExtra("url",url);
                        startActivity(intent);
                    }
                });
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(TitleListActivity.this,"请检查网络",Toast.LENGTH_LONG).show();
            }
        });

        queue.add(request);

        final PullToRefreshLayout pullToRefreshLayout = (PullToRefreshLayout)TitleListActivity.this.findViewById(R.id.pull);
        ActionBarPullToRefresh.from(this).allChildrenArePullable().listener(new OnRefreshListener() {
            @Override
            public void onRefreshStarted(View view) {
                //lv_title = (ListView)TitleListActivity.this.findViewById(R.id.lv_title);
                topicList.clear();
                topicAdapter.notifyDataSetChanged();
                queue.add(request);
                pullToRefreshLayout.setRefreshComplete();

                //Toast.makeText(TitleListActivity.this,"hi",Toast.LENGTH_LONG).show();

            }
        }).setup(pullToRefreshLayout);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.title_list_acitity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_about) {
            Intent intent = new Intent(TitleListActivity.this,AboutActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
