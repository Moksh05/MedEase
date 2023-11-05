package com.example.medease

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.medease.Modal.DoctorDetail
import com.example.medease.databinding.ActivityDoctorDetailBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class DoctorDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDoctorDetailBinding

    val db = FirebaseFirestore.getInstance()
    val CollREf = db.collection("Doctors").document("Doctor Details").collection("Top Doctors")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityDoctorDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.backButtonMedication.setOnClickListener {
            onBackPressed()
        }

        if (intent.getStringExtra("SELECTED_DOC")!= null){
            val selecteddoc = intent.getStringExtra("SELECTED_DOC")

            CollREf.document(selecteddoc!!).get().addOnSuccessListener { document ->
                if(document!=null){
                    var doctor = document.toObject(DoctorDetail::class.java)!!

                    binding.DoctorName.text = doctor.name
                    binding.Specialisation.text =  doctor.specialization
                    binding.clinic.text = doctor.clinicName
                    binding.experience.text = doctor.experience+ " Years of Experience"
                    binding.address.text = doctor.address
                    binding.fee.text = "Consultation Fee : " + doctor.fee
                    binding.liscenseNumber.text = "Liscense No. : " +doctor.licenseNumber
                    binding.education.text = listOfNotNull(
                        doctor.education.dentalSchool,
                        doctor.education.residency,
                        doctor.education.boardCertification
                    ).joinToString(", ")
                    binding.languages.text = doctor.languagesSpoken.toString().replace("[\\[\\]]".toRegex(), "")
                    binding.phoneNo.text = "Phone no. : "+doctor.contactNumber
                    binding.email.text = "Email id : " + doctor.email
                    binding.imageTextview.text = doctor.name.toString().substring(4,6).uppercase()
                    binding.profilepicText.text = doctor.name.toString().substring(4,6).uppercase()

                    binding.onlineConsultButton.setOnClickListener {
                        var intent = Intent(this,BookingActivity::class.java)
                        intent.putExtra("BOOKING_TYPE","online")
                        intent.putExtra("SELECTED_DOC",doctor.licenseNumber)
                        startActivity(intent)
                    }

                    binding.BookClinicButton.setOnClickListener {
                        var intent = Intent(this,BookingActivity::class.java)
                        intent.putExtra("BOOKING_TYPE","offline")
                        intent.putExtra("SELECTED_DOC",doctor.licenseNumber)
                        startActivity(intent)
                    }
                }

            }.addOnFailureListener { e->
                Toast.makeText(this,"Failed to retireve it $e",Toast.LENGTH_LONG).show()
            }


        }else{
            Toast.makeText(this,"Not received any intent",Toast.LENGTH_LONG).show()
        }


    }
}