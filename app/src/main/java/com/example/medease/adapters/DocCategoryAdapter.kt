package com.example.medease.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.medease.DoctorListActivity
import com.example.medease.Modal.MedRec
import com.example.medease.Modal.doctorttype
import com.example.medease.R
import com.example.medease.add_edit_medrec
import com.google.gson.Gson


class DocCategoryAdapter(val categorylist: MutableList<doctorttype>) : RecyclerView.Adapter<DocCategoryAdapter.DocCatViewHolder>() {

    inner class DocCatViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val categoryselected = view.findViewById<CardView>(R.id.categoryitem)
        val Category = view.findViewById<TextView>(R.id.category_name)
        val categoryimg = view.findViewById<ImageView>(R.id.category_icon)


    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DocCategoryAdapter.DocCatViewHolder {
        return DocCatViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.doccategory_item,parent,false))
    }

    override fun onBindViewHolder(holder: DocCategoryAdapter.DocCatViewHolder, position: Int) {
        holder.categoryimg.setImageResource(categorylist[position].icon)
        holder.Category.text = categorylist[position].Type


        holder.categoryselected.setOnClickListener {
            val selectedcategory = categorylist[position]
//converting this to a json to pass it to next activity
//this .context refer to the parant activity context which is hosting the recycler view
            val intent = Intent(holder.categoryselected.context,DoctorListActivity::class.java)
            intent.putExtra("CATEGORY_NAME",selectedcategory.Type)
            holder.categoryselected.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
       return categorylist.size
    }
}