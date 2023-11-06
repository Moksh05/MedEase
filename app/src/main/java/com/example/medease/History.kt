package com.example.medease

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medease.Modal.Appointment
import com.example.medease.Modal.MedRec
import com.example.medease.Modal.currentmed
import com.example.medease.adapters.AppointmentsAdapter
import com.example.medease.adapters.CurrentMEdAdapter
import com.example.medease.adapters.medrecAdapter
import com.example.medease.databinding.ActivityHistoryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class History : AppCompatActivity() {

    private lateinit var recyclerview: RecyclerView
    private lateinit var binding: ActivityHistoryBinding
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val MedrecHisCollref = db.collection("Users").document(auth.currentUser?.email.toString())
        .collection("MedRec History")

    val AppointmentcollRef = db.collection("Users").document(auth.currentUser?.email.toString())
        .collection("Appointment History")
    val MedcollRef = db.collection("Users").document(auth.currentUser?.email.toString())
        .collection("Medicine History")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButtonHistory.setOnClickListener {
            onBackPressed()
        }
        recyclerview = binding.historyRecyclerview
        recyclerview.layoutManager = LinearLayoutManager(this)
        if (intent.hasExtra("HISTORY_TYPE")) {
            if (intent.getStringExtra("HISTORY_TYPE") == "Medication") {


                getmeddata()


            } else if (intent.getStringExtra("HISTORY_TYPE") == "Appointment") {
                getAppointmentData()

            }else{
                getMedrecData()
            }
        }
    }

    private fun getmeddata() {
        var historyMedList = mutableListOf<currentmed>()
        MedcollRef.get().addOnSuccessListener { documents ->
            for (document in documents) {
                Log.d("MedicationCrash", "working till 66")
                if (document != null) {
                    val medname = document.getString("medName")
                    val dose = document.getString("dose")
                    val Noofdays = document.getString("noofDays")
                    val aftermeal = document.getBoolean("aftermeal")
                    val selectedtimeList = document.get("selectedtime") as MutableList<String>

                    Log.d("MedicationCrash", "working till 73 $selectedtimeList")
                    Log.d("MedicationCrash", "working till 73 $medname $dose $Noofdays $aftermeal")


                    val currentmeddata =
                        currentmed(medname!!, dose!!, Noofdays!!, selectedtimeList, aftermeal!!)
                    Log.d("MedicationCrash", "working till 78 $currentmeddata")
                    historyMedList.add(currentmeddata)
                    Log.d("MedicationCrash", "working till 79 $historyMedList")
                }
            }
            var adapter = CurrentMEdAdapter(historyMedList, false)
            recyclerview.adapter = adapter

        }.addOnFailureListener { e ->
            Toast.makeText(this, "Failure of task $e", Toast.LENGTH_LONG).show()
        }
    }

    private fun getMedrecData() {
        MedrecHisCollref.get().addOnSuccessListener { querySnapshot ->
            var documentList = mutableListOf<MedRec>()
            Log.d("medrecfailure", "Working till line 60 ${querySnapshot.documents.toString()}")
            for (document in querySnapshot) {

                if (document != null) {
                    Toast.makeText(this, "${document.id}", Toast.LENGTH_SHORT).show()
                    val title = document.getString("tittle") ?: ""
                    val description = document.get("description") ?: ""
                    Log.d(
                        "medrecfailure",
                        " line 67 $description  hii ${document.get("description")}"
                    )
                    val fileName = document.getString("fileName") ?: ""
                    val fileurl = document.getString("fileurl") ?: ""
                    val date = document.getString("date") ?: ""

                    val medRec = MedRec(title, description.toString(), date, fileName, fileurl)
                    documentList.add(medRec)

                }
                Log.d("medrecfailure", "Working till line 74")
                recyclerview.adapter = medrecAdapter(documentList, false)

            }


            Log.d("medrecfailure", "Working till line 80")

        }.addOnFailureListener { e ->
            Toast.makeText(this, "failed to fetch med rec $e", Toast.LENGTH_LONG).show()
        }
    }


    private fun getAppointmentData() {
        AppointmentcollRef.get().addOnSuccessListener { documents ->
            var appointmentlist = mutableListOf<Appointment>()
            for (document in documents) {
                if (document != null) {
                    Log.d("appointmentcrash", "failed at line 59")
                    var appointment = document.toObject(Appointment::class.java)

                    Log.d("DocCatCrash", "${document.toObject<Appointment>()}Error at 115")
                    appointmentlist.add(appointment)
                }
            }
            recyclerview.adapter = AppointmentsAdapter(appointmentlist, false)
            Log.d("appointmentcrash", "failed at line 67")
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Failed to retrieve  $e ", Toast.LENGTH_LONG).show()
        }

    }
}
