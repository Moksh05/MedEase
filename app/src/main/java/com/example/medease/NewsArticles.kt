package com.example.medease

import NewsApiService
import NewsItem
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.medease.Constants.Constants
import com.example.medease.adapters.NewsAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NewsArticles : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_news_fragment)


        findViewById<ImageView>(R.id.back_button_medication).setOnClickListener {
            onBackPressed()
        }
        val swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.news_layout)
        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = false
            getNews()
        }

        getNews()
    }

    private fun getNews() {
        findViewById<ProgressBar>(R.id.loadingProgressBar_news).visibility = View.VISIBLE
        findViewById<RecyclerView>(R.id.news_recyclerView).visibility = View.GONE

        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val serviceapi = retrofit.create(NewsApiService::class.java)

        val call = serviceapi.getNews("in", "health", Constants.APP_ID)

        call.enqueue(object : Callback<NewsItem> {
            override fun onResponse(call: Call<NewsItem>, response: Response<NewsItem>) {
                Toast.makeText(this@NewsArticles, "Callback received", Toast.LENGTH_LONG).show()
                if (response.isSuccessful) {

                    val newsResponse = response.body()
                    if (newsResponse != null) {
                        val newsRecyclerView = findViewById<RecyclerView>(R.id.news_recyclerView)
                        newsRecyclerView.layoutManager = LinearLayoutManager(this@NewsArticles)
                        newsRecyclerView.adapter = NewsAdapter(newsResponse.articles!!)

                        findViewById<ProgressBar>(R.id.loadingProgressBar_news).visibility = View.GONE
                        newsRecyclerView.visibility = View.VISIBLE
                        Toast.makeText(this@NewsArticles, "Callback received", Toast.LENGTH_LONG)
                            .show()
                    }

                } else {
                    findViewById<TextView>(R.id.News_item_textview).text =
                        "response failed ${response.body()}"
                }
            }

            override fun onFailure(call: Call<NewsItem>, t: Throwable) {
                Toast.makeText(this@NewsArticles, "Unable to fetch news $t", Toast.LENGTH_LONG)
                    .show()
                findViewById<ProgressBar>(R.id.loadingProgressBar_news).visibility = View.GONE
                findViewById<RecyclerView>(R.id.news_recyclerView).visibility = View.VISIBLE
            }
        })
    }
}