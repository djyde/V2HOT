package com.randy.client.v2hot;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.v2ex.api.GsonRequest;
import com.v2ex.api.ReplyList;

public class ContentActivity extends ActionBarActivity {

    private String title;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(false);
        }

        setContentView(R.layout.activity_content);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);

        Intent intent = getIntent();

        String id = intent.getStringExtra("id");
        String content = intent.getStringExtra("content");
        String username = intent.getStringExtra("username");

        title = intent.getStringExtra("title");
        url = intent.getStringExtra("url");

        final RepliesAdapter repliesAdapter = new RepliesAdapter(this, username);
        ListView repliesView = (ListView) findViewById(R.id.replies);

        View headerView = getLayoutInflater().inflate(R.layout.topic_header, repliesView, false);
        ((TextView) headerView.findViewById(R.id.header_title)).setText(title);
        ((TextView) headerView.findViewById(R.id.header_content)).setText(content);
        ((TextView) headerView.findViewById(R.id.header_username)).setText(username);

        repliesView.addHeaderView(headerView);
        repliesView.setAdapter(repliesAdapter);

        RequestQueue queue = Volley.newRequestQueue(ContentActivity.this);

        //获取回复
        queue.add(new GsonRequest<ReplyList>(
                Request.Method.GET, "https://www.v2ex.com/api/replies/show.json?topic_id=" + id, ReplyList.class,
                new Response.Listener<ReplyList>() {
                    @Override
                    public void onResponse(ReplyList response) {
                        repliesAdapter.addAll(response);
                        repliesAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ContentActivity.this, "请检查网络", Toast.LENGTH_LONG).show();
                    }
                }
        ));
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

    public Intent getShareIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, title + "\n" + url + "\n" + "分享自[V2HOT]");
        intent.setType("text/plain");
        return intent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.content_activity, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);
        ShareActionProvider shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        shareActionProvider.setShareIntent(getShareIntent());
        return true;
    }

}
