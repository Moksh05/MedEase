package com.example.medease.adapters

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.medease.Modal.Appointment
import com.example.medease.Modal.DoctorDetail
import com.example.medease.R
import com.example.medease.VideoChatActivity
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class jJOINappointmentADAPTER(var list : MutableList<Appointment>) : RecyclerView.Adapter<jJOINappointmentADAPTER.JoinAppointmentViewHolder>() {

    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())


    private val db = FirebaseFirestore.getInstance()
    private val CollRef =
        db.collection("Doctors").document("Doctor Details").collection("Top Doctors")

    class JoinAppointmentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgtext = view.findViewById<TextView>(R.id.image_textview)
        val selectedappointment = view.findViewById<CardView>(R.id.appointment_card)
        val DocName = view.findViewById<TextView>(R.id.Doctor_name)
        val specialisation = view.findViewById<TextView>(R.id.Specialisation)
        val clinic = view.findViewById<TextView>(R.id.intruction)
        val experience = view.findViewById<TextView>(R.id.experience)
        val address = view.findViewById<TextView>(R.id.address)
        val appointment = view.findViewById<TextView>(R.id.SHeduled_Booking)
        val cancellation_text = view.findViewById<TextView>(R.id.cancellation_text)
        val cancel_button = view.findViewById<ExtendedFloatingActionButton>(R.id.cancel_appointment)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): jJOINappointmentADAPTER.JoinAppointmentViewHolder {
        return JoinAppointmentViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.scheduled_appointment_item,parent,false))
    }

    override fun onBindViewHolder(
        holder: jJOINappointmentADAPTER.JoinAppointmentViewHolder,
        position: Int
    ) {



        holder.cancel_button.setText("JOIN")
        holder.cancel_button.backgroundTintList = ColorStateList.valueOf(Color.GREEN)

        if (AppointmentsAdapter.scheduledappointmentlist.isNotEmpty()) {
            val appointment = AppointmentsAdapter.scheduledappointmentlist[position]
            val doctorId = appointment.liscenseID

            getDoctorData(doctorId) { fetchedDoctorDetail ->
                if (fetchedDoctorDetail != null) {
                    bindDoctorDetails(holder, fetchedDoctorDetail, appointment)
                } else {
                    // Handle the case where doctor details couldn't be fetched
                    Log.d("appointmentcrash", "Failed to fetch doctor details for $doctorId")
                }

            }
        }


        holder.cancel_button.setOnClickListener {

            var currentdate = dateFormat.format(Date())
            var currenttime = timeFormat.format(Date())

            var meetingstart = timeFormat.parse(list[position].time)
            if (meetingstart!= null ){
                var calendar = Calendar.getInstance()
                calendar.time = meetingstart
                calendar.add(Calendar.HOUR_OF_DAY,1)
                val endtime = timeFormat.format(calendar.time)

                if (currentdate == list[position].date && (timeFormat.parse(currenttime) >= meetingstart  || timeFormat.parse(currenttime) < timeFormat.parse(endtime) )){
                    holder.selectedappointment.context.startActivity(Intent(holder.selectedappointment.context,VideoChatActivity::class.java))
                }else{
                    Toast.makeText(holder.selectedappointment.context,"Cant Join Now Not the time reached",Toast.LENGTH_LONG).show()
                    holder.selectedappointment.context.startActivity(Intent(holder.selectedappointment.context,VideoChatActivity::class.java))
                }
            }



        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun bindDoctorDetails(
        holder: jJOINappointmentADAPTER.JoinAppointmentViewHolder,
        doctorDetail: DoctorDetail,
        appointment: Appointment
    ) {
        holder.DocName.text = doctorDetail.name
        holder.imgtext.text = doctorDetail.name.substring(4, 6).uppercase()
        holder.specialisation.text = doctorDetail.specialization
        holder.clinic.text = doctorDetail.clinicName
        holder.experience.text = "${doctorDetail.experience} years of experience"
        holder.address.text = doctorDetail.address
        holder.appointment.text =
            "${appointment.type} booking: ${appointment.time},${appointment.date}"

    }

    private fun getDoctorData(docId: String, callback: (DoctorDetail?) -> Unit) {
        CollRef.document(docId).get().addOnSuccessListener { document ->
            if (document != null) {
                val doctorDetail = document.toObject(DoctorDetail::class.java)
                callback(doctorDetail)
            } else {
                callback(null)
            }
        }.addOnFailureListener {
            callback(null)
        }
    }
}