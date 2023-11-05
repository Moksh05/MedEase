package com.example.medease.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.medease.DoctorDetailActivity
import com.example.medease.Modal.DoctorDetail
import com.example.medease.Modal.currentmed
import com.example.medease.R
import com.example.medease.addedit_medication
import com.google.gson.Gson

class TopDocAdapter(var TopDocList: MutableList<DoctorDetail>): RecyclerView.Adapter<TopDocAdapter.TopDocViewHolder>() {




    inner class TopDocViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val medicinename = view.findViewById<TextView>(R.id.Medicine_name)
        val medicinenameimg = view.findViewById<TextView>(R.id.image_textview)
        val dose = view.findViewById<TextView>(R.id.dose_and_days)
        val intruction = view.findViewById<TextView>(R.id.intruction)
        val selectedMed = view.findViewById<CardView>(R.id.currentmed_item)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TopDocAdapter.TopDocViewHolder {
        return TopDocViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.currentmedicine_item,parent,false))
    }

    override fun getItemCount(): Int {
        return TopDocList.size
    }

    override fun onBindViewHolder(holder: TopDocViewHolder, position: Int) {
        holder.medicinename.text = TopDocList[position].name
        holder.dose.text = TopDocList[position].specialization
        holder.intruction.text = TopDocList[position].experience +" Years of Experience"
        holder.medicinenameimg.text = TopDocList[position].name.substring(4,6).uppercase()

        holder.selectedMed.setOnClickListener {
            val intent = Intent(holder.selectedMed.context,DoctorDetailActivity::class.java)
            intent.putExtra("SELECTED_DOC",TopDocList[position].licenseNumber)
            holder.selectedMed.context.startActivity(intent)

        }
    }


}