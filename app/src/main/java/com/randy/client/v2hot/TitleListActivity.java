package com.randy.client.v2hot;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TitleListActivity extends Activity {

    private ListView lv_title;
    private TextView tv;
    private ProgressBar pb_load;
    private ArrayList<String> idList = new ArrayList<String>();
    private ArrayList<String> titleList = new ArrayList<String>();
    private List<HashMap<String,String>> topicList = new ArrayList<HashMap<String, String>>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title_list);
        //创建volley请求队列
        RequestQueue queue= Volley.newRequestQueue(TitleListActivity.this);

        //使用Volley获取V2EX首页源代码
        StringRequest request = new StringRequest("http://v2ex.com",new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //用Jsoup解析源代码
                Document doc = Jsoup.parse(response);
                //用Jsoup选择器取出title和topic id
                Elements titles = doc.select("span.item_hot_topic_title > a");
                Elements ids = doc.select("span.item_hot_topic_title > a");
                for (Element id:ids){
                    idList.add(id.attr("href").split("/")[2]);
                }
                for (Element title:titles){
                    titleList.add(title.text());
                }
                for (int i=0; i < idList.size();i++){
                    HashMap<String,String> map = new HashMap<String, String>();
                    map.put("id",idList.get(i));
                    map.put("title",titleList.get(i));
                    topicList.add(map);
                }

                //适配器
                lv_title = (ListView)TitleListActivity.this.findViewById(R.id.lv_title);
                SimpleAdapter topicAdapter = new SimpleAdapter(TitleListActivity.this,topicList,R.layout.topic_item,new String[]{"title"},new int[]{R.id.tv_title});
                lv_title.setAdapter(topicAdapter);
                lv_title.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent = new Intent(TitleListActivity.this,ContentActivity.class);
                        HashMap<String,String> info = (HashMap<String, String>) lv_title.getItemAtPosition(i);
                        //取id和title传递到另一个activity
                        String id = info.get("id");
                        String title = info.get("title");
                        intent.putExtra("id",id);
                        intent.putExtra("title",title);
                        startActivity(intent);
                    }
                });

                //加载结束后progressbar消失
                pb_load = (ProgressBar)findViewById(R.id.pb_load);
                pb_load.setVisibility(View.GONE);

            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //错误提示处理
                Toast.makeText(TitleListActivity.this,"请检查网络",Toast.LENGTH_LONG).show();
            }
        }){
            //重写getHeader方法，修改User agent
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = new HashMap<String,String>();
                headers.put("User-agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/36.0.1985.143 Safari/537.36");
                return headers;
            }
        };

        queue.add(request);
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
