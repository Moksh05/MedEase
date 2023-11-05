package com.example.medease.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.medease.Modal.Appointment
import com.example.medease.Modal.DoctorDetail
import com.example.medease.R
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
class AppointmentsAdapter(var scheduledappointmentlist: MutableList<Appointment>) :
    RecyclerView.Adapter<AppointmentsAdapter.AppointmentsViewHolder>() {

    private val db = FirebaseFirestore.getInstance()
    private val CollRef = db.collection("Doctors").document("Doctor Details").collection("Top Doctors")
    private var doctorDetailsMap: MutableMap<String, DoctorDetail> = mutableMapOf()

    class AppointmentsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // ViewHolder components as before
        val imgtext = view.findViewById<TextView>(R.id.image_textview)
        val DocName = view.findViewById<TextView>(R.id.Doctor_name)
        val specialisation = view.findViewById<TextView>(R.id.Specialisation)
        val clinic = view.findViewById<TextView>(R.id.intruction)
        val experience = view.findViewById<TextView>(R.id.experience)
        val address = view.findViewById<TextView>(R.id.address)
        val appointment = view.findViewById<TextView>(R.id.SHeduled_Booking)
        val cancel_button = view.findViewById<ExtendedFloatingActionButton>(R.id.cancel_appointment)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentsViewHolder {
        return AppointmentsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.scheduled_appointment_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return scheduledappointmentlist.size
    }

    override fun onBindViewHolder(holder: AppointmentsViewHolder, position: Int) {
        val appointment = scheduledappointmentlist[position]
        val doctorId = appointment.liscenseID

        // Check if doctorDetailsMap already contains the doctor details
        val doctorDetail = doctorDetailsMap[doctorId]

        if (doctorDetail != null) {
            // Use cached doctor details
            bindDoctorDetails(holder, doctorDetail, appointment)
        } else {
            // Fetch doctor details from Firestore
            getDoctorData(doctorId) { fetchedDoctorDetail ->
                if (fetchedDoctorDetail != null) {
                    doctorDetailsMap[doctorId] = fetchedDoctorDetail // Cache the details
                    bindDoctorDetails(holder, fetchedDoctorDetail, appointment)
                } else {
                    // Handle the case where doctor details couldn't be fetched
                    Log.d("appointmentcrash", "Failed to fetch doctor details for $doctorId")
                }
            }
        }
    }

    private fun bindDoctorDetails(holder: AppointmentsViewHolder, doctorDetail: DoctorDetail, appointment: Appointment) {
        holder.DocName.text = doctorDetail.name
        holder.imgtext.text = doctorDetail.name.substring(4, 6).uppercase()
        holder.specialisation.text = doctorDetail.specialization
        holder.clinic.text = doctorDetail.clinicName
        holder.experience.text = "${doctorDetail.experience} years of experience"
        holder.address.text = doctorDetail.address
        holder.appointment.text = "${appointment.type} booking: ${appointment.time},${appointment.date}"
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
