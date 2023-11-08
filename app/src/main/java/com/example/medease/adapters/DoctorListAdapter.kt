package com.example.medease.adapters


import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.medease.BookingActivity
import com.example.medease.DoctorDetailActivity
import com.example.medease.Modal.DoctorDetail
import com.example.medease.R
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class DoctorListAdapter(var DoctorList: MutableList<DoctorDetail>) :
    RecyclerView.Adapter<DoctorListAdapter.DoctorListViewHolder>() {

    inner class DoctorListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtuimg = view.findViewById<TextView>(R.id.image_textview)
        val DocName = view.findViewById<TextView>(R.id.Doctor_name)
        val specialisation = view.findViewById<TextView>(R.id.Specialisation)
        val fee = view.findViewById<TextView>(R.id.fee)
        val selecteddoc = view.findViewById<CardView>(R.id.appointment_card)
        val experience = view.findViewById<TextView>(R.id.experience)
        val address = view.findViewById<TextView>(R.id.address)
        val clinic = view.findViewById<TextView>(R.id.clinic)
        val online = view.findViewById<ExtendedFloatingActionButton>(R.id.online_consult_button)
        val offline = view.findViewById<ExtendedFloatingActionButton>(R.id.Book_clinic_button)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DoctorListAdapter.DoctorListViewHolder {
        return DoctorListViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.appointment_item, parent, false)
        )

    }

    override fun getItemCount(): Int {
        return DoctorList.size
    }

    override fun onBindViewHolder(holder: DoctorListViewHolder, position: Int) {
        holder.DocName.text = DoctorList[position].name
        holder.clinic.text = DoctorList[position].clinicName
        holder.txtuimg.text = DoctorList[position].name.substring(4, 6).uppercase()
        holder.specialisation.text = DoctorList[position].specialization
        holder.fee.text = "Consultaion Fee : " + DoctorList[position].fee + "/-"
        holder.experience.text = DoctorList[position].experience + "Years of Experience"
        holder.address.text = DoctorList[position].address

        holder.selecteddoc.setOnClickListener {
            val intent = Intent(holder.selecteddoc.context, DoctorDetailActivity::class.java)
            intent.putExtra("SELECTED_DOC", DoctorList[position].licenseNumber)
            holder.selecteddoc.context.startActivity(intent)
        }
        holder.online.setOnClickListener {
            val intent = Intent(holder.selecteddoc.context, BookingActivity::class.java)
            intent.putExtra("SELECTED_DOC", DoctorList[position].licenseNumber)
            intent.putExtra("BOOKING_TYPE","offline")
            intent.putExtra("FEE", DoctorList[position].fee)
            holder.selecteddoc.context.startActivity(intent)
        }
        holder.offline.setOnClickListener {
            val intent = Intent(holder.selecteddoc.context, BookingActivity::class.java)
            intent.putExtra("SELECTED_DOC", DoctorList[position].licenseNumber)
            intent.putExtra("BOOKING_TYPE","offline")
            intent.putExtra("FEE",DoctorList[position].fee)
            holder.selecteddoc.context.startActivity(intent)
        }
        //has to change the activity it is going to
    }

}