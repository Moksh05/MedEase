package com.example.medease.adapters

import Article
import NewsItem
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.medease.R
import com.example.medease.showNews
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import org.w3c.dom.Text

class NewsAdapter(val NewsList:List<Article>) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {




    inner class NewsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val newsImage = view.findViewById<ImageView>(R.id.newsitem_img)
        val newsTitle = view.findViewById<TextView>(R.id.News_item_textview)
        val news_item = view.findViewById<CardView>(R.id.news_item)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsAdapter.NewsViewHolder {
        return NewsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.news_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: NewsAdapter.NewsViewHolder, position: Int) {
        holder.newsTitle.text = NewsList[position].title

        if(NewsList[position].urlToImage!=null){
            Picasso.get().load(NewsList[position].urlToImage).into(holder.newsImage)
        }else{
            holder.newsImage.setImageResource(R.drawable.imgnotfound)
        }

        holder.news_item.setOnClickListener {
            val selecteddNews = Gson().toJson(NewsList[position])
            val intent = Intent(holder.news_item.context,showNews::class.java)
            intent.putExtra("SELECTED_NEWS",selecteddNews)
            holder.news_item.context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return NewsList.size
    }
}