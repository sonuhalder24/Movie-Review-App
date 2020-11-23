package com.example.moviereviews;



import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toolbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    ProgressBar progressBar;
    ArrayList<HashMap<String,String>> rssItemList=new ArrayList<>();
    ListView listView;

    RSSParser rssParser=new RSSParser();
    Toolbar toolbar;
    List<RSSItem> rssItem=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new LoadItems().execute("http://www.rediff.com/rss/moviesreviewsrss.xml");
        listView=findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(MainActivity.this,MainActivity2.class);
                String page_link=((TextView)view.findViewById(R.id.link)).getText().toString().trim();
                intent.putExtra("url",page_link);
                startActivity(intent);
            }
        });
    }
    public class LoadItems extends AsyncTask<String,String,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressBar=new ProgressBar(MainActivity.this,null,android.R.attr.progressBarStyleLarge);
            RelativeLayout relativeLayout=findViewById(R.id.relativeLayout);
            RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            progressBar.setLayoutParams(layoutParams);
            progressBar.setVisibility(View.VISIBLE);
            relativeLayout.addView(progressBar);
        }

        @Override
        protected String doInBackground(String... strings) {

            rssItem=rssParser.getRSSFeedItem(strings[0]);
            for(RSSItem item:rssItem){
                if(item.link.toString().equals("")){
                    break;
                }
                HashMap<String,String> hashMap=new HashMap<>();
                String pubdate=item.pubdate.trim();
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
                try{

                    Date date=simpleDateFormat.parse(pubdate);
                    SimpleDateFormat sdf=new SimpleDateFormat("EEEE, dd MMMM yyyy - hh:mm a", Locale.US);
                    item.pubdate=sdf.format(date);

                }catch (ParseException e) {
                    e.printStackTrace();
                }
                hashMap.put("title",item.title);
                hashMap.put("link",item.link);
                hashMap.put("pubDate",item.pubdate);
                rssItemList.add(hashMap);
            }
            runOnUiThread(new Runnable() {
                public void run() {
                    ListAdapter adapter = new SimpleAdapter(
                            MainActivity.this,
                            rssItemList, R.layout.custom_layout,
                            new String[]{"link", "title", "pubDate"},
                            new int[]{R.id.link, R.id.title, R.id.pubdate});
                    listView.setAdapter(adapter);
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            progressBar.setVisibility(View.GONE);
        }
    }
}