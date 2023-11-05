package com.example.medease.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.medease.Modal.Medication
import com.example.medease.R

class SuggestionAdapter(val SuggestionList: MutableList<String>) : RecyclerView.Adapter<SuggestionAdapter.SuggestionViewHolder>() {



    inner class SuggestionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var suggestion_textview= view.findViewById<TextView>(R.id.search_suggestion_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestionViewHolder {
        return SuggestionViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.search_suggestion, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return SuggestionList.size
    }

    override fun onBindViewHolder(holder: SuggestionViewHolder, position: Int) {
        val suggestionList = SuggestionList
        holder.suggestion_textview.text = suggestionList[position]
    }
}