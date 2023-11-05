package com.example.medease

import Article
import NewsApiService
import NewsItem
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medease.Constants.Constants
import com.example.medease.adapters.NewsAdapter
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class News_fragment : Fragment() {



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        getNews()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_news_fragment, container, false)
    }

    private fun getNews() {

        val retrofit =
            Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        var serviceapi = retrofit.create(NewsApiService::class.java)

        var call = serviceapi.getNews("in","health",Constants.APP_ID)

        call.enqueue(object : Callback<NewsItem>{
            override fun onResponse(call: Call<NewsItem>, response: Response<NewsItem>) {
                Toast.makeText(requireActivity(),"Callback received",Toast.LENGTH_LONG).show()
                if (response.isSuccessful){

                    val newsResponse = response.body()
                    if (newsResponse != null) {
                        val NewsRecyclerView = view?.findViewById<RecyclerView>(R.id.news_recyclerView)
                        NewsRecyclerView?.layoutManager = LinearLayoutManager(requireActivity())
                        NewsRecyclerView?.adapter = NewsAdapter(newsResponse.articles!!)

                        Toast.makeText(requireActivity(),"Callback received",Toast.LENGTH_LONG).show()

                    }

                }else{
                    view?.findViewById<TextView>(R.id.News_item_textview)?.text= "response failed ${response.body()}"
                }

            }

            override fun onFailure(call: Call<NewsItem>, t: Throwable) {
                Toast.makeText(requireActivity(),"Unable to fetch news $t",Toast.LENGTH_LONG).show()

            }

        })
    }



}