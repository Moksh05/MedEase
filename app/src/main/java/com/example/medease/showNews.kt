package com.example.medease

import Article
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.medease.databinding.ActivityShowNewsBinding
import com.google.gson.Gson
import com.squareup.picasso.Picasso

class showNews : AppCompatActivity() {
    private lateinit var binding: ActivityShowNewsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val selectedNews = intent.getStringExtra("SELECTED_NEWS")
        if (selectedNews != null) {
            val NewsInfo = Gson().fromJson(selectedNews, Article::class.java)
            if (NewsInfo.url!=null)
            {
                binding.webView.loadUrl(NewsInfo.url)
            }else{
                Toast.makeText(this,"No link for this news available",Toast.LENGTH_SHORT).show()
            }
        }



    }
}