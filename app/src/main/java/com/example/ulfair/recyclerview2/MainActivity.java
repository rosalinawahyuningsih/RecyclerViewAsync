package com.example.ulfair.recyclerview2;

import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {

    //private final LinkedList<String> mWordList = new LinkedList<>();
    private final LinkedList<NewsItem> mNewsList = new LinkedList<>();
    private int mCount = 0;
    private RecyclerView mRecyclerView;
    private WordListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String base_url =
                "https://www.googleapis.com/books/v1/volumes?q=sherlock";

        FetchData fetchData = new FetchData();
        fetchData.execute(base_url);

        for (int i = 0; i < 10; i++) {
            //mNewsList.addLast("Word " + mCount++);
        }
            NewsItem news1 = new NewsItem();
            news1.title = "Bayar Wifi";
            news1.desc = "Rabu, 09 Maret 2018";
            news1.idResGambar = R.drawable.android;
            mNewsList.add(news1);

            NewsItem news2 = new NewsItem();
            news2.title = "Beli paketan bulanan";
            news2.desc = "Senin, 18 April 2018";
            news2.idResGambar = R.drawable.user;
            mNewsList.add(news2);

            NewsItem news3 = new NewsItem();
            news3.title = "Membayar Kos bulanan";
            news3.desc = "Sabtu, 23 April 2018";
            news3.idResGambar = R.drawable.clerk;
            mNewsList.add(news3);

            // Get a handle to the RecyclerView.
            mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
            // Create an adapter and supply the data to be displayed.
            mAdapter = new WordListAdapter(this, mNewsList);
            // Connect the adapter with the RecyclerView.
            mRecyclerView.setAdapter(mAdapter);
            // Give the RecyclerView a default layout manager.
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
           // Log.d("WordList", mWordList.getLast());

        // Add a floating action click handler for creating new entries.
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int wordListSize = mNewsList.size();
                // Add a new word to the end of the wordList.
                //mNewsList.addLast("+ Word " + wordListSize);
                // Notify the adapter, that the data has changed so it can
                // update the RecyclerView to display the data.
                mRecyclerView.getAdapter().notifyItemInserted(wordListSize);
                // Scroll to the bottom.
                mRecyclerView.smoothScrollToPosition(wordListSize);
            }
        });
    }
    //Fungsi Download saja, belum tampilan
    public class FetchData extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {
            String result = null;

            try {
                URL url = new URL(strings[0]); //diambil array yg pertamanya dari strings(parameternya). Kalo URL harus ada Try-Catch

                //membuka koneksinya
                HttpURLConnection connection =
                        (HttpURLConnection)url.openConnection();//ini juga harus ada Try-Catchnya

                //di connect kan
                connection.setRequestMethod("GET");
                connection.connect();

                //check response apakah connect atau tdk
                int response = connection.getResponseCode();
                Log.d("DEBUG1", "RESPONSE CODE : " + response);

                //mendownload data yang berupa String
                BufferedReader r = new BufferedReader(
                        new InputStreamReader(connection.getInputStream())
                );

                StringBuilder total = new StringBuilder();
                String line;

                while ((line = r.readLine()) !=null){
                    total.append(line);
                }
                result = total.toString();
                Log.d("DEBUG1", "RESULT :" + result);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        //Fungsi yang bisa mengupdate tampilan
        @Override
        protected void onPostExecute(String s) { //String didapat dari td diatas
            super.onPostExecute(s);

            //olah data mnjd json
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray itemsArray = jsonObject.getJSONArray("items");

                for (int i=0;i<itemsArray.length();i++){
                    JSONObject book = itemsArray.getJSONObject(i); //diambil dulu id nya
                    JSONObject volumeinfo = book.getJSONObject("volumeInfo");

                    NewsItem news = new NewsItem();
                    news.title = volumeinfo.getString("title");
                    news.desc = volumeinfo.getString("publisher");

                    news.linkResGambar =
                            volumeinfo.getJSONObject("imageLinks")
                            .getString("smallThumbnail");

                    mNewsList.add(news);
                }

                mAdapter.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
