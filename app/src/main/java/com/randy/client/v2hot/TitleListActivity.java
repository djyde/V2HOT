package com.randy.client.v2hot;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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

public class TitleListActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title_list);

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.pull);

        final List<HashMap<String, String>> topicList = new ArrayList<HashMap<String, String>>();

        final SimpleAdapter topicsAdapter = new SimpleAdapter(
                this,
                topicList,
                R.layout.topic_item,
                new String[]{"title"},
                new int[]{R.id.title}
        );

        final ListView topicsView = (ListView) findViewById(R.id.topics);

        topicsView.setAdapter(topicsAdapter);
        topicsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                HashMap<String, String> info = (HashMap<String, String>) topicsView.getItemAtPosition(i);
                String id = info.get("id");
                String title = info.get("title");
                String username = info.get("username");
                String url = info.get("url");
                Intent intent = new Intent(TitleListActivity.this, ContentActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("title", title);
                intent.putExtra("username", username);
                intent.putExtra("url", url);
                startActivity(intent);
            }
        });

        //创建volley请求队列
        final RequestQueue queue = Volley.newRequestQueue(this);

        //根据API获取热议主题
        final JsonArrayRequest request = new JsonArrayRequest("http://www.v2ex.com/api/topics/hot.json",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        topicList.clear();

                        //取id、title、username
                        for (int i = 0; i < response.length(); i++) {
                            HashMap<String, String> map = new HashMap<String, String>();
                            try {
                                map.put("id", response.getJSONObject(i).getString("id"));
                                map.put("title", response.getJSONObject(i).getString("title"));
                                map.put("username", response.getJSONObject(i).getJSONObject("member").getString("username"));
                                map.put("url", response.getJSONObject(i).getString("url"));
                                topicList.add(map);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        topicsAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(TitleListActivity.this, "请检查网络", Toast.LENGTH_LONG).show();
                    }
                }
        );

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queue.add(request);
            }
        });

        queue.add(request);
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.title_list_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                Intent intent = new Intent(TitleListActivity.this, AboutActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
