package com.example.medease

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medease.Modal.DoctorDetail
import com.example.medease.adapters.DoctorListAdapter
import com.example.medease.adapters.TopDocAdapter
import com.example.medease.databinding.ActivityDoctorListBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class DoctorListActivity : AppCompatActivity() {
    var db = FirebaseFirestore.getInstance()
    var CollRef = db.collection("Doctors").document("Doctor Details").collection("Top Doctors")

    private lateinit var binding: ActivityDoctorListBinding
private lateinit var recyclerview:RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDoctorListBinding.inflate(layoutInflater)
        setContentView(binding.root)


        intent.getStringExtra("CATEGORY_NAME")
        recyclerview = binding.doctorlistRecyclerview
        recyclerview.layoutManager = LinearLayoutManager(this)

        binding.backButtonMedication.setOnClickListener {
            onBackPressed()
        }
        getdoclist()
    }


    private fun getdoclist() {
        Log.d("DocCatCrash","Error at 110")
        CollRef.get().addOnSuccessListener { querySnapshot ->
            Log.d("DocCatCrash","Error at 112")
            val doctorList = mutableListOf<DoctorDetail>()
            for (document in querySnapshot) {
                if (document!=null){
                    Log.d("DocCatCrash","Error at 115")
                    var doctor = document.toObject(DoctorDetail::class.java)
                    Log.d("DocCatCrash","${document.toObject<DoctorDetail>()}Error at 115")
                    doctorList.add(doctor)
                }
            }
            recyclerview.adapter = DoctorListAdapter(doctorList)

        }
            .addOnFailureListener { exception ->
                Toast.makeText(this,"Failed $exception to retrieve data", Toast.LENGTH_LONG).show()
            }

    }

}