package com.example.medease.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.medease.Modal.Appointment
import com.example.medease.Modal.DoctorDetail
import com.example.medease.R
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore

class AppointmentsAdapter(
    val deletable: Boolean
) :
    RecyclerView.Adapter<AppointmentsAdapter.AppointmentsViewHolder>() {

    private val db = FirebaseFirestore.getInstance()
    private val CollRef =
        db.collection("Doctors").document("Doctor Details").collection("Top Doctors")


    private val appointmentcollref = db.collection("Users").document(
        auth.currentUser?.email.toString()
    )
        .collection("Appointments")
    private val timecollref = db.collection("Time slots")


    class AppointmentsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // ViewHolder components as before
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



    companion object{
        var scheduledappointmentlist = mutableListOf<Appointment>()

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentsViewHolder {
        return AppointmentsViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.scheduled_appointment_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return scheduledappointmentlist.size
    }

    override fun onBindViewHolder(holder: AppointmentsViewHolder, position: Int) {

        val appointment = scheduledappointmentlist[position]
        val doctorId = appointment.liscenseID

        if (deletable) {
            holder.cancel_button.setOnClickListener {
                if (position >= 0 && position < scheduledappointmentlist.size) {


                    val date = scheduledappointmentlist[holder.adapterPosition].date
                    val time = scheduledappointmentlist[holder.adapterPosition].time
                    val licenseId = scheduledappointmentlist[holder.adapterPosition].liscenseID
                    // Show a confirmation dialog
                    val builder = AlertDialog.Builder(holder.selectedappointment.context)
                    builder.setTitle("Confirmation")
                    builder.setMessage("Are you sure you want to cancel this appointment?")
                    builder.setPositiveButton("Yes") { _, _ ->
                        // Handle the appointment cancellation here
                        // You can remove the appointment from the list and update the RecyclerView
                        scheduledappointmentlist.removeAt(position)
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, scheduledappointmentlist.size)


                        timecollref.document("$date-$time-$doctorId")
                            .delete().addOnSuccessListener {
                                Toast.makeText(
                                    holder.selectedappointment.context,
                                    "Time slot deleted",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }.addOnFailureListener {
                                Toast.makeText(
                                    holder.selectedappointment.context,
                                    "Time slot deletion failed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        appointmentcollref.document("$doctorId-$time").delete()
                            .addOnSuccessListener {
                                Toast.makeText(
                                    holder.selectedappointment.context,
                                    "Appointment deleted",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }.addOnFailureListener {
                                Toast.makeText(
                                    holder.selectedappointment.context,
                                    "Appointment deletion failed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }


                        Toast.makeText(
                            holder.selectedappointment.context,
                            "Appointment canceled",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    builder.setNegativeButton("No") { _, _ ->
                        // Do nothing or dismiss the dialog
                    }
                    builder.show()

                    // ...
                } else {
                    Toast.makeText(
                        holder.selectedappointment.context,
                        "Index out of bound problem",
                        Toast.LENGTH_SHORT
                    ).show()
                    // Handle the case where position is out of bounds.
                }
            }
        } else {
            holder.cancel_button.visibility = View.GONE
            holder.cancellation_text.visibility = View.GONE
        }

        // Check if doctorDetailsMap already contains the doctor details


        // Fetch doctor details from Firestore
        getDoctorData(doctorId) { fetchedDoctorDetail ->
            if (fetchedDoctorDetail != null) {
                bindDoctorDetails(holder, fetchedDoctorDetail, appointment)
            } else {
                // Handle the case where doctor details couldn't be fetched
                Log.d("appointmentcrash", "Failed to fetch doctor details for $doctorId")
            }

        }
    }

    private fun bindDoctorDetails(
        holder: AppointmentsViewHolder,
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

    fun Updatelist(list : MutableList<Appointment>){
        scheduledappointmentlist.clear()
        scheduledappointmentlist.addAll(list)
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
