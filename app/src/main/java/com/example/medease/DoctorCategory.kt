package com.example.medease

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medease.Constants.Constants
import com.example.medease.Modal.DoctorDetail
import com.example.medease.adapters.DocCategoryAdapter
import com.example.medease.adapters.TopDocAdapter
import com.example.medease.databinding.ActivityDoctorCategoryBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class DoctorCategory : AppCompatActivity() {

    var db = FirebaseFirestore.getInstance()
    var CollRef = db.collection("Doctors").document("Doctor Details").collection("Top Doctors")

    private lateinit var binding: ActivityDoctorCategoryBinding

    private lateinit var recyclerview: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityDoctorCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.backButtonMedication.setOnClickListener {
            onBackPressed()
        }

        binding.doccategoryLayout1.setOnClickListener {
            val intent = Intent(this, DoctorListActivity::class.java)
            intent.putExtra("CATEGORY_NAME", "Pulmologist")
            startActivity(intent)
        }
        binding.doccategoryLayout2.setOnClickListener {
            val intent = Intent(this, DoctorListActivity::class.java)
            intent.putExtra("CATEGORY_NAME", "Pulmologist")
            startActivity(intent)
        }
        binding.doccategoryLayout3.setOnClickListener {
            val intent = Intent(this, DoctorListActivity::class.java)
            intent.putExtra("CATEGORY_NAME", "Pulmologist")
            startActivity(intent)
        }
        binding.doccategoryLayout4.setOnClickListener {
            val intent = Intent(this, DoctorListActivity::class.java)
            intent.putExtra("CATEGORY_NAME", "Pulmologist")
            startActivity(intent)
        }
        binding.doccategoryLayout5.setOnClickListener {
            val intent = Intent(this, DoctorListActivity::class.java)
            intent.putExtra("CATEGORY_NAME", "Pulmologist")
            startActivity(intent)
        }
        binding.doccategoryLayout6.setOnClickListener {
            val intent = Intent(this, DoctorListActivity::class.java)
            intent.putExtra("CATEGORY_NAME", "Pulmologist")
            startActivity(intent)
        }



        binding.viewAllButton.setOnClickListener {

            val dialog = BottomSheetDialog(this)
            val view = layoutInflater.inflate(R.layout.categorylist_dialog, null)
            dialog.setContentView(view)


            val dialogrecyclerview = dialog.findViewById<RecyclerView>(R.id.category_recyclerview)
            if (dialogrecyclerview != null) {
                dialogrecyclerview.layoutManager = LinearLayoutManager(this)
            }

            if (dialogrecyclerview != null) {
                dialogrecyclerview.adapter = DocCategoryAdapter(Constants.TypeofDoctor)
            }


            dialog.show()
        }
        recyclerview = binding.topDocRecyclerview
        recyclerview.layoutManager = LinearLayoutManager(this)


        gettopDoc()
    }


    private fun gettopDoc() {
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
            recyclerview.adapter = TopDocAdapter(doctorList)

        }
            .addOnFailureListener { exception ->
               Toast.makeText(this,"Failed $exception to retrieve data",Toast.LENGTH_LONG).show()
            }

    }

}