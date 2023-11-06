package com.example.medease

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.medease.Constants.Constants
import com.example.medease.Modal.currentmed
import com.example.medease.adapters.NotificationReceiver
import com.example.medease.databinding.ActivityAddeditMedicationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import java.util.Calendar

class addedit_medication : AppCompatActivity() {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val collRef = db.collection("Users").document(auth.currentUser?.email.toString())
        .collection("Medications")
    val historycollRef = db.collection("Users").document(auth.currentUser?.email.toString())
        .collection("Medicine History")

    private lateinit var binding: ActivityAddeditMedicationBinding
    var selectedtimes = mutableListOf<String>()
    var aftermeal = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddeditMedicationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //scheduleNotification()
        //setAlarms()


        val selectedcurrentMEdJson = intent.getStringExtra("SELECTED_MED")
        if (selectedcurrentMEdJson != null) {
            val gson = Gson()
            val CurrentMEd = gson.fromJson(selectedcurrentMEdJson, currentmed::class.java)

            if (intent.getBooleanExtra("EDITABLE",true)){

                    Toast.makeText(
                        this,
                        "EDITABLE : ${intent.getBooleanExtra("EDITABLE", true)}",
                        Toast.LENGTH_LONG
                    ).show()

                binding.backButtonMedication.setOnClickListener {
                    onBackPressed()
                }
                binding.DosaGE.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                    override fun afterTextChanged(s: Editable?) {
                        // Enable the button if the EditText has text, or disable it if it's empty

                        if (!s.isNullOrBlank()) {
                            binding.addtimeButton.isEnabled = true
                            binding.addtimeButton.setBackgroundColor(Color.parseColor("#098EF8"))
                            binding.addtimeButton.setTextColor(Color.WHITE)
                        } else {
                            binding.addtimeButton.isEnabled = false
                            binding.addtimeButton.setBackgroundColor(Color.parseColor("#F6F7F8"))
                            binding.addtimeButton.setTextColor(Color.parseColor("#9B9B9B"))
                        }
                    }
                })

                binding.addtimeButton.setOnClickListener {
                    val intent = Intent(this, addmedicinetime::class.java)
                    intent.putExtra("Dosevalue", binding.DosaGE.text.toString().trim())
                    startActivityForResult(intent, Constants.REQUEST_CODE)
                }



                binding.addmedicationButton.setOnClickListener {


                    if (binding.MedicineName.text.isNullOrBlank() && binding.DosaGE.text.isNullOrBlank() && binding.numberodDays.text.isNullOrBlank()) {
                        Toast.makeText(this, "Please Fill the required", Toast.LENGTH_LONG).show()
                    }
                    val data = currentmed(
                        binding.MedicineName.text.toString().trim(),
                        binding.DosaGE.text.toString().trim(),
                        binding.numberodDays.text.toString().trim(),
                        selectedtimes, aftermeal
                    )
                    collRef.document(binding.MedicineName.text.toString().trim()).set(data)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Medication added succesfully", Toast.LENGTH_LONG).show()
                            finish()
                        }.addOnFailureListener { e ->
                            Toast.makeText(this, "task failed  $e", Toast.LENGTH_LONG).show()
                        }

                    historycollRef.document("${binding.MedicineName.text.toString().trim()}-${binding.numberodDays.text.toString().trim()} days").set(data)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Medication Hisstory added succesfully", Toast.LENGTH_LONG).show()
                            finish()
                        }.addOnFailureListener { e ->
                            Toast.makeText(this, "history addition failed  $e", Toast.LENGTH_LONG).show()
                        }

                }

                binding.beforemeallayout.setOnClickListener {
                    aftermeal = false
                    Instructionflip(aftermeal)

                }

                binding.aftermeal.setOnClickListener {
                    aftermeal = true
                    Instructionflip(aftermeal)

                }
            }
            else{

                binding.MedicineName.setText(CurrentMEd.MedName)
                binding.DosaGE.setText(CurrentMEd.Dose)
                binding.numberodDays.setText(CurrentMEd.NoofDays)
                Instructionflip(CurrentMEd.aftermeal)
                binding.selectedtimes.visibility = View.VISIBLE
                for (item in CurrentMEd.selectedtime) {
                    selectedtimes.add(item)
                }

                Toast.makeText(this, "${CurrentMEd.selectedtime}", Toast.LENGTH_LONG).show()
                val formattedString = selectedtimes.joinToString(separator = " , ") {
                    it.replace(
                        "[\",\\[\\]]".toRegex(),
                        ""
                    )
                }
                binding.addtimeButton.setBackgroundColor(Color.parseColor("#098EF8"))
                binding.addtimeButton.setText("Change Time")
                binding.selectedtimes.setText(formattedString)
                binding.addtimeButton.setTextColor(Color.WHITE)

                binding.addmedicationButton.isClickable = false
                binding.addmedicationButton.visibility = View.GONE

                binding.backButtonMedication.setOnClickListener {
                    onBackPressed()
                }
            }




        }else{
            binding.backButtonMedication.setOnClickListener {
                onBackPressed()
            }
            binding.DosaGE.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    // Enable the button if the EditText has text, or disable it if it's empty

                    if (!s.isNullOrBlank()) {
                        binding.addtimeButton.isEnabled = true
                        binding.addtimeButton.setBackgroundColor(Color.parseColor("#098EF8"))
                        binding.addtimeButton.setTextColor(Color.WHITE)
                    } else {
                        binding.addtimeButton.isEnabled = false
                        binding.addtimeButton.setBackgroundColor(Color.parseColor("#F6F7F8"))
                        binding.addtimeButton.setTextColor(Color.parseColor("#9B9B9B"))
                    }
                }
            })

            binding.addtimeButton.setOnClickListener {
                val intent = Intent(this, addmedicinetime::class.java)
                intent.putExtra("Dosevalue", binding.DosaGE.text.toString().trim())
                startActivityForResult(intent, Constants.REQUEST_CODE)
            }



            binding.addmedicationButton.setOnClickListener {


                if (binding.MedicineName.text.isNullOrBlank() && binding.DosaGE.text.isNullOrBlank() && binding.numberodDays.text.isNullOrBlank()) {
                    Toast.makeText(this, "Please Fill the required", Toast.LENGTH_LONG).show()
                }else{
                    val data = currentmed(
                        binding.MedicineName.text.toString().trim(),
                        binding.DosaGE.text.toString().trim(),
                        binding.numberodDays.text.toString().trim(),
                        selectedtimes, aftermeal
                    )
                    collRef.document(binding.MedicineName.text.toString().trim()).set(data)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Medication added succesfully", Toast.LENGTH_LONG).show()
                            finish()
                        }.addOnFailureListener { e ->
                            Toast.makeText(this, "task failed  $e", Toast.LENGTH_LONG).show()
                        }

                    historycollRef.document("${binding.MedicineName.text.toString().trim()}-${binding.numberodDays.text.toString().trim()} days").set(data)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Medication Hisstory added succesfully", Toast.LENGTH_LONG).show()
                            finish()
                        }.addOnFailureListener { e ->
                            Toast.makeText(this, "history addition failed  $e", Toast.LENGTH_LONG).show()
                        }
                }


            }

            binding.beforemeallayout.setOnClickListener {
                aftermeal = false
                Instructionflip(aftermeal)

            }

            binding.aftermeal.setOnClickListener {
                aftermeal = true
                Instructionflip(aftermeal)

            }
        }


    }


    private fun Instructionflip(aftermeal: Boolean) {
        if (aftermeal) {
            binding.aftermeal.setBackgroundResource(R.drawable.selected_back_background)
            binding.aftermealText.setTextColor(Color.WHITE)
            binding.imageaftermeal.setColorFilter(Color.WHITE)
            binding.beforemeallayout.setBackgroundResource(R.drawable.back_background)
            binding.beforemalText.setTextColor(Color.parseColor("#9B9B9B"))
            binding.beforemealImage.setColorFilter(Color.parseColor("#9B9B9B"))
        } else {
            binding.beforemeallayout.setBackgroundResource(R.drawable.selected_back_background)
            binding.beforemalText.setTextColor(Color.WHITE)
            binding.beforemealImage.setColorFilter(Color.WHITE)
            binding.aftermeal.setBackgroundResource(R.drawable.back_background)
            binding.aftermealText.setTextColor(Color.parseColor("#9B9B9B"))
            binding.imageaftermeal.setColorFilter(Color.parseColor("#9B9B9B"))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                selectedtimes.clear()


                val timelist = data?.getStringArrayListExtra("RESULT")
                if (timelist != null) {
                    if (timelist.isNotEmpty()) {
                        for (item in timelist) {
                            selectedtimes.add(item)
                        }
                        binding.addtimeButton.setText("Change Time")
                        binding.selectedtimes.visibility = View.VISIBLE
                        val formattedString = selectedtimes.joinToString(separator = " , ") {
                            it.replace(
                                "[\",\\[\\]]".toRegex(),
                                ""
                            )
                        }
                        binding.selectedtimes.setText(formattedString)

                    }

                }


            }
        }

    }

    private fun setAlarms() {
        var alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // val i = timer.text.toString().toInt()

        val intent = Intent(this, NotificationReceiver::class.java)

        var pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            add(Calendar.MINUTE, 1) // Set alarm 1 minute from now
        }

// setRepeating() lets you specify a precise custom interval--in this case,
// 20 minutes.


        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )

        Toast.makeText(this, "Alarm Set Successful", Toast.LENGTH_SHORT).show()
    }

//    private fun scheduleNotification() {
//
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val name = "MedicareproChannel"
//            val description = "Channel for Alarm Manager"
//            val importance = NotificationManager.IMPORTANCE_HIGH
//            val channel = NotificationChannel("MedEase", name, importance)
//
//            // Set the channel description
//            channel.description = description
//
//            val notificationManager = getSystemService(NotificationManager::class.java)
//
//            // Create the notification channel
//            notificationManager.createNotificationChannel(channel)
//        }
//
//
//    }


}








