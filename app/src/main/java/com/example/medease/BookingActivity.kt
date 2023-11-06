package com.example.medease

import android.content.Intent
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
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val historycollRef = db.collection("Users").document(auth.currentUser?.email.toString())
        .collection("Appointment History")
    private val collRef = db.collection("Users").document(auth.currentUser?.email.toString())
        .collection("Appointments")
    private val collRef2 = db.collection("Time slots")
    var selectedTextViewdate: TextView? = null
    var selectedTextViewtime: TextView? = null
    private lateinit var binding: ActivityBookingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupDates()




        // Initialize your variables here

        val datetextViewIds = arrayOf(
            R.id.date1, R.id.date2, R.id.date3, R.id.date4,
            R.id.date5, R.id.date6, R.id.date7, R.id.date8
        )

        val timeTextViewIds = arrayOf(
            R.id.time1, R.id.time2, R.id.time3, R.id.time4,
            R.id.time5, R.id.time6, R.id.time7, R.id.time8, R.id.time9
        )

        for (textViewId in datetextViewIds) {
            val textView = findViewById<TextView>(textViewId)
            setupTextViewClickListener(textView, "date")
        }

        for (textViewId in timeTextViewIds) {
            val textView = findViewById<TextView>(textViewId)
            setupTextViewClickListener(textView, "time")
        }

        binding.backButtonMedication.setOnClickListener {
            onBackPressed()
        }

        // Perform the remaining tasks here
        val docId = intent.getStringExtra("SELECTED_DOC")
        val typeOfAppointment = intent.getStringExtra("BOOKING_TYPE")


        // Get the current date


        binding.Username.setText(auth.currentUser?.displayName)
        binding.Email.setText(auth.currentUser?.email)

        binding.BookAppointment.setOnClickListener {
            val selectedDate = selectedTextViewdate!!.text.toString()
            val selectedTime = selectedTextViewtime!!.text.toString()
            val Email = binding.Email.text.toString()
            val Phoneno = binding.PhoneNo.text.toString()
            val Name = binding.Username.text.toString()

            if (selectedDate.isNotEmpty() && selectedTime.isNotEmpty() && Email.isNotEmpty() && Name.isNotEmpty() && Phoneno.isNotEmpty()) {
                if (selectedDate == dateFormat.format(Date()) && timeFormat.parse(selectedTime)<=timeFormat.parse(timeFormat.format(Date()))){
                    Toast.makeText(this,"Time has already passed cant book right now",Toast.LENGTH_LONG).show()
                }else{
                    scheduleAppointment(docId!!, typeOfAppointment!!)
                }

            } else {
                Toast.makeText(this, "Please Fill all the entries", Toast.LENGTH_LONG).show()
            }

            startActivity(Intent(this,Home::class.java))
        }
    }

    private fun setupTextViewClickListener(textView: TextView, type: String) {
        textView.setOnClickListener {
            onTextViewClicked(textView, type)
        }
    }

    private fun onTextViewClicked(textView: TextView, type: String) {
        // Deselect the previously selected TextView
        val selectedTextView = if (type == "time") selectedTextViewtime else selectedTextViewdate
        selectedTextView?.setBackgroundResource(R.drawable.unselected_radio)
        selectedTextView?.setTextColor(Color.BLACK)
        selectedTextView?.isSelected = false

        // Select the clicked TextView
        textView.setBackgroundResource(R.drawable.selected_radio)
        textView.setTextColor(Color.WHITE)
        textView.isSelected = true

        if (type == "time") selectedTextViewtime = textView else selectedTextViewdate = textView
    }

    private fun scheduleAppointment(docId: String, appointmentType: String) {
        val appointmentData = hashMapOf(
            "fullName" to binding.Username.text.toString().trim(),
            "email" to binding.Email.text.toString().trim(),
            "type" to appointmentType,
            "liscenseID" to docId,
            "phoneNumber" to binding.PhoneNo.text.toString().trim(),
            "date" to selectedTextViewdate!!.text.toString(),
            "time" to selectedTextViewtime!!.text.toString()
        )
        val timedatedoc = "${selectedTextViewdate!!.text}-${selectedTextViewtime!!.text}-$docId"

        collRef2.get().addOnSuccessListener { documents ->
            val existingTime = documents.map { it.id }
            if (existingTime.contains(timedatedoc)) {
                runOnUiThread {
                    Toast.makeText(this, "Time slot not available", Toast.LENGTH_LONG).show()
                }
            } else {
                addData(docId, appointmentData, timedatedoc)
            }
        }
    }



    private fun setupDates() {
        val calendar = Calendar.getInstance()
        val nextDates = mutableListOf<String>()

        for (i in 0 until 8) {
            val date: Date = calendar.time
            val formattedDate = dateFormat.format(date)
            nextDates.add(formattedDate)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        for (i in 1..8) {
            val textView =
                findViewById<TextView>(resources.getIdentifier("date$i", "id", packageName))
            textView.text = nextDates[i - 1]
        }
    }

    private fun addData(
        docId: String,
        appointmentData: HashMap<String, String>,
        timedatedoc: String
    ) {
        collRef.document("$docId-${selectedTextViewtime!!.text.trim()}")
            .set(appointmentData)
            .addOnSuccessListener {
                runOnUiThread {
                    Toast.makeText(this, "Appointment Scheduled", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { e ->
                runOnUiThread {
                    Toast.makeText(this, "Appointment Failed $e", Toast.LENGTH_LONG).show()
                }
            }

        historycollRef.document("$docId-${selectedTextViewtime!!.text.trim()}")
            .set(appointmentData)
            .addOnSuccessListener {
                runOnUiThread {
                    Toast.makeText(this, "History Added", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { e ->
                runOnUiThread {
                    Toast.makeText(this, "History addition failed $e", Toast.LENGTH_LONG).show()
                }
            }

        collRef2.document(timedatedoc).set(emptyMap<String, Any>())
            .addOnSuccessListener {
                runOnUiThread {
                    Toast.makeText(this, "Time slot added", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { e ->
                runOnUiThread {
                    Toast.makeText(this, "Failed to add time slot", Toast.LENGTH_LONG).show()
                }
            }
    }
}