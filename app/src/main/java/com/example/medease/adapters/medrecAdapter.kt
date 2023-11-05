package com.example.medease.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.medease.Modal.MedRec
import com.example.medease.R
import com.example.medease.add_edit_medrec
import com.google.gson.Gson


class medrecAdapter(val medreclist: MutableList<MedRec>) : RecyclerView.Adapter<medrecAdapter.medrecViewHolder>() {

    inner class medrecViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val selectedmedrec = view.findViewById<CardView>(R.id.medrecitem_layout)
        val tittle = view.findViewById<TextView>(R.id.medrec_tittle)
        val Desc = view.findViewById<TextView>(R.id.medrec_desc)
        val fileName = view.findViewById<TextView>(R.id.medrec_uploadedfilename)
        val date = view.findViewById<TextView>(R.id.medrec_dateuploaded)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): medrecAdapter.medrecViewHolder {
        return medrecViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.med_rec_item,parent,false))
    }

    override fun onBindViewHolder(holder: medrecAdapter.medrecViewHolder, position: Int) {
        holder.tittle.text =medreclist[position].Tittle
        holder.Desc.text =medreclist[position].Description
        holder.fileName.text = medreclist[position].fileName
        holder.date.text = medreclist[position].Date


        holder.selectedmedrec.setOnClickListener {
            val selectedMedRec = medreclist[position]
//converting this to a json to pass it to next activity
            val selectedMedRecJson = Gson().toJson(selectedMedRec)
//this .context refer to the parant activity context which is hosting the recycler view
            val intent = Intent(holder.selectedmedrec.context,add_edit_medrec::class.java)
            intent.putExtra("SELECTED_MEDREC",selectedMedRecJson)
            holder.selectedmedrec.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
       return medreclist.size
    }
}