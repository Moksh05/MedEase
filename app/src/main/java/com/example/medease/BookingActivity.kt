package com.example.medease

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.medease.databinding.ActivityBookingBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class BookingActivity : AppCompatActivity() {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val collRef = db.collection("Users").document(auth.currentUser?.email.toString())
        .collection("Appointments")
    val collRef2 = db.collection("Time slots")
    private var selectedTextViewdate: TextView? = null
    private var selectedTextViewtime: TextView? = null
    private lateinit var binding: ActivityBookingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        intent.putExtra("BOOKING_TYPE","online")
//        intent.putExtra("SELECTED_DOC",doctor.licenseNumber)

        var DocId = intent.getStringExtra("SELECTED_DOC")
        var Typeofappointment = intent.getStringExtra("BOOKING_TYPE")


        // Get the current date
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

        // Create an array or list to store the next 8 dates
        val nextDates = mutableListOf<String>()

        for (i in 0 until 8) {
            // Calculate the next date
            val date: Date = calendar.time
            val formattedDate = dateFormat.format(date)
            nextDates.add(formattedDate)

            // Move to the next day
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        // Get all the RadioButtons by their IDs and set their text
        for (i in 1..8) {
            val TextView =
                findViewById<TextView>(resources.getIdentifier("date$i", "id", packageName))
            TextView.text = nextDates[i - 1] // Subtract 1 because arrays/lists are 0-indexed
        }
        binding.backButtonMedication.setOnClickListener {
            onBackPressed()
        }
        binding.Username.setText(auth.currentUser?.displayName.toString())
        binding.Email.setText(auth.currentUser?.email.toString())

        binding.BookAppointment.setOnClickListener {
            scheduleappointment(DocId!!, Typeofappointment!!)
        }


        val datetextViewIds =
            arrayOf(R.id.date1, R.id.date2, R.id.date3, R.id.date4, R.id.date5, R.id.date6, R.id.date7, R.id.date8)

        val timeTextViewIds =
            arrayOf(R.id.time1, R.id.time2, R.id.time3, R.id.time4, R.id.time5, R.id.time6, R.id.time7, R.id.time8,R.id.time9)


        for (textViewId in datetextViewIds) {
            val textView = findViewById<TextView>(textViewId)
            setupTextViewClickListener(textView,"date")
        }
        for (textViewId in timeTextViewIds) {
            val textView = findViewById<TextView>(textViewId)
            setupTextViewClickListener(textView,"time")
        }

    }

    private fun setupTextViewClickListener(textView: TextView,type:String) {
        textView.setOnClickListener {
            onTextViewClicked(textView,type)
        }
    }

    private fun onTextViewClicked(textView: TextView,type:String) {
        // Deselect the previously selected TextView
        if (type == "time"){
            selectedTextViewtime?.setBackgroundResource(R.drawable.unselected_radio)
            selectedTextViewtime?.setTextColor(Color.BLACK)
            selectedTextViewtime?.isSelected = false

            textView.setBackgroundResource(R.drawable.selected_radio)
            textView.setTextColor(Color.WHITE)
            textView.isSelected = true
            selectedTextViewtime = textView
        }else{
            selectedTextViewdate?.setBackgroundResource(R.drawable.unselected_radio)
            selectedTextViewdate?.setTextColor(Color.BLACK)

            selectedTextViewdate?.isSelected = false

            textView.setBackgroundResource(R.drawable.selected_radio)

            textView.setTextColor(Color.WHITE)
            textView.isSelected = true
            selectedTextViewdate = textView
        }




    }

    private fun scheduleappointment(docid: String, appointmenttype: String) {



        val appointmentData = hashMapOf(
            "fullName" to binding.Username.text.toString().trim(),
            "email" to binding.Email.text.toString().trim(),
            "type" to appointmenttype,
            "liscenseID" to docid,
            "phoneNumber" to binding.PhoneNo.text.toString().trim(),
            "date" to selectedTextViewdate?.text.toString(),
            "time" to selectedTextViewtime?.text.toString()
        )
        var timedatedoc = "${selectedTextViewdate?.text.toString()}-${selectedTextViewtime?.text.toString()}-$docid"

        collRef2.get().addOnSuccessListener { documents ->
            var existingtime = mutableListOf<String>()
            for (document in documents) {
                existingtime.add(document.id)
            }

            if (existingtime.contains(timedatedoc)) {
                Toast.makeText(this, "Time slot Not available", Toast.LENGTH_LONG).show()
            } else {
                adddata(docid,appointmentData,timedatedoc)
            }

        }


    }
    private fun adddata(docid:String,appointmentData:HashMap<String,String>,timedatedoc:String) {
        collRef.document(docid).set(appointmentData).addOnSuccessListener {
            Toast.makeText(this, "Appointment Scheduled", Toast.LENGTH_LONG).show()
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Appointment Failed $e", Toast.LENGTH_LONG).show()
        }

        collRef2.document(timedatedoc).set(emptyMap<String, Any>())
            .addOnSuccessListener {
                Toast.makeText(this, "time added Scheduled", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "failed to add time Scheduled", Toast.LENGTH_LONG).show()
            }
    }
}


// Usage:


