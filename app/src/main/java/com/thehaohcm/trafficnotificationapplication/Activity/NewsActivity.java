package com.thehaohcm.trafficnotificationapplication.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.thehaohcm.trafficnotificationapplication.Adapter.NewsAdapter;
import com.thehaohcm.trafficnotificationapplication.Model.News;
import com.thehaohcm.trafficnotificationapplication.R;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class NewsActivity extends AppCompatActivity {

    private ListView lvNews;
    private NewsAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        setTitle("Tin tức giao thông");
        //get News
        lvNews=(ListView) findViewById(R.id.lvNews);
        lvNews.setOnItemClickListener(onItemClick);
        new NewsTask().execute("http://www.tapchigiaothong.vn/giao-thong-24h.rss");
    }

    private AdapterView.OnItemClickListener onItemClick=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent detail=new Intent(NewsActivity.this, NewsDetailActivity.class);
            detail.putExtra("LINK",adapter.getItem(position).getLink());
            startActivity(detail);
        }
    };


    class NewsTask extends AsyncTask<String,Void,ArrayList<News>> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd=new ProgressDialog(NewsActivity.this);
            pd.setMessage("Đang tải dữ liệu...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected ArrayList<News> doInBackground(String... params) {
            String url=params[0];
            ArrayList<News> result=new ArrayList<>();
            try {
                org.jsoup.nodes.Document doc= Jsoup.connect(url).get();
                Elements elements=doc.select("item");
                for (org.jsoup.nodes.Element item:elements) {
                    String title=item.select("title").text();
                    String link=item.select("link").text();
                    String imgUrl=item.select("image").text();
                    result.add(new News(title,link,imgUrl));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Load data and parse
            return result;
        }

        @Override
        protected void onPostExecute(ArrayList<News> newsArrayList) {
            super.onPostExecute(newsArrayList);
            pd.dismiss();
            adapter=new NewsAdapter(NewsActivity.this,0,newsArrayList,getLayoutInflater());
            lvNews.setAdapter(adapter);
        }
    }
}
