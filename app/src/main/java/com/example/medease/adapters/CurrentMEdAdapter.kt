package com.example.medease.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.medease.Modal.currentmed
import com.example.medease.R
import com.example.medease.addedit_medication
import com.google.gson.Gson

class CurrentMEdAdapter(val CurrentMedList : MutableList<currentmed>): RecyclerView.Adapter<CurrentMEdAdapter.currentmedViewHolder>() {
    inner class currentmedViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val medicinename = view.findViewById<TextView>(R.id.Medicine_name)
        val medicinenameimg = view.findViewById<TextView>(R.id.image_textview)
        val dose = view.findViewById<TextView>(R.id.dose_and_days)
        val intruction = view.findViewById<TextView>(R.id.intruction)
        val selectedMed = view.findViewById<CardView>(R.id.currentmed_item)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CurrentMEdAdapter.currentmedViewHolder {
        return currentmedViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.currentmedicine_item,parent,false))
    }

    override fun getItemCount(): Int {
        return CurrentMedList.size
    }

    override fun onBindViewHolder(holder: currentmedViewHolder, position: Int) {
        holder.medicinename.text = CurrentMedList[position].MedName
        holder.dose.text = "Dose : " + CurrentMedList[position].Dose +" | Till : "+CurrentMedList[position].NoofDays+" Days"
        if (CurrentMedList[position].aftermeal){
            holder.intruction.text = "After Meal"
        }else{
            holder.intruction.text = "Before Meal"
        }
        holder.medicinenameimg.text = CurrentMedList[position].MedName.substring(0,2).uppercase()

        holder.selectedMed.setOnClickListener {
            val jsondata = Gson().toJson(CurrentMedList[position])
            val intent = Intent(holder.selectedMed.context,addedit_medication::class.java)
            intent.putExtra("SELECTED_MED",jsondata)
            holder.selectedMed.context.startActivity(intent)

        }
    }
}