package com.randy.client.v2hot;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.v2ex.api.GsonRequest;
import com.v2ex.api.Topic;
import com.v2ex.api.TopicList;

public class TitleListActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title_list);

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.pull);

        final ArrayAdapter<Topic> topicsAdapter = new ArrayAdapter<Topic>(this, 0) {

            private final LayoutInflater inflater = LayoutInflater.from(getContext());

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.topic_item, parent, false);
                }

                TextView title = (TextView) convertView.findViewById(R.id.title);
                title.setText(getItem(position).title);

                return convertView;
            }

        };

        final ListView topicsView = (ListView) findViewById(R.id.topics);

        topicsView.setAdapter(topicsAdapter);
        topicsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Topic topic = topicsAdapter.getItem(position);
                Intent intent = new Intent(TitleListActivity.this, ContentActivity.class);
                intent.putExtra("id", topic.id);
                intent.putExtra("title", topic.title);
                intent.putExtra("username", topic.member.username);
                intent.putExtra("url", topic.url);
                startActivity(intent);
            }
        });

        //创建volley请求队列
        final RequestQueue queue = Volley.newRequestQueue(this);

        //根据API获取热议主题
        final GsonRequest<TopicList> request = new GsonRequest<TopicList>(
                Request.Method.GET, "https://www.v2ex.com/api/topics/hot.json", TopicList.class,
                new Response.Listener<TopicList>() {
                    @Override
                    public void onResponse(TopicList response) {
                        topicsAdapter.clear();
                        topicsAdapter.addAll(response);
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
