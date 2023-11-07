package com.example.medease

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medease.Modal.Appointment
import com.example.medease.Modal.DoctorDetail
import com.example.medease.adapters.AppointmentsAdapter
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

val auth = FirebaseAuth.getInstance()
val db = FirebaseFirestore.getInstance()
val collRef = db.collection("Users").document(auth.currentUser?.email.toString())
    .collection("Appointments")
class AppointmentFragment : Fragment() {
    // TODO: Rename and change types of parameters


    private lateinit var RecyclerView:RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_appointment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        RecyclerView = view.findViewById(R.id.appointment_recyclerview)
        RecyclerView.layoutManager = LinearLayoutManager(requireActivity())
         getdata()

        view.findViewById<ImageView>(R.id.history).setOnClickListener {
            val intent = Intent(requireActivity(),History::class.java)
            intent.putExtra("HISTORY_TYPE","Appointment")
            startActivity(intent)
        }

        view.findViewById<ExtendedFloatingActionButton>(R.id.add_appointment_button).setOnClickListener {
            startActivity(Intent(requireActivity(),DoctorCategory::class.java))
        }


    }

    private fun getdata(){

        collRef.get().addOnSuccessListener { documents ->
            var appointmentlist = mutableListOf<Appointment>()
            for (document in documents){
               if (document!= null){
                   Log.d("appointmentcrash","failed at line 59")
                   var appointment = document.toObject(Appointment::class.java)

                   Log.d("DocCatCrash","${document.toObject<Appointment>()}Error at 115")
                   appointmentlist.add(appointment)
               }
            }
            val adapter = AppointmentsAdapter(true)
            adapter.Updatelist(appointmentlist)
            RecyclerView.adapter = adapter
            Log.d("appointmentcrash","failed at line 67")
        }.addOnFailureListener { e->
            Toast.makeText(requireActivity(),"Failed to retrieve  $e ",Toast.LENGTH_LONG).show()
        }

    }
}