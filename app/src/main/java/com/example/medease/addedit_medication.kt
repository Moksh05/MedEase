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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Random

class addedit_medication : AppCompatActivity() {
    private lateinit var calendar: Calendar
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val collRef = db.collection("Users").document(auth.currentUser?.email.toString())
        .collection("Medications")
    private val historycollRef =
        db.collection("Users").document(auth.currentUser?.email.toString())
            .collection("Medicine History")

    private lateinit var binding: ActivityAddeditMedicationBinding
    private var selectedTimes = mutableListOf<String>()
    private var afterMeal = false
    private var myHours: Int = 0
    private var myMinutes: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddeditMedicationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Add permission such that it is always granted
        // Note: Uncomment the following code if needed
//        if (ActivityCompat.checkSelfPermission(
//                context,
//                Manifest.permission.POST_NOTIFICATIONS
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
//                NotificationReceiver.PERMISSION_REQUEST_CODE
//            )
//            return
//        }

        val selectedCurrentMedJson = intent.getStringExtra("SELECTED_MED")
        if (selectedCurrentMedJson != null) {
            createnotificationChannel()
            val gson = Gson()
            val currentMed = gson.fromJson(selectedCurrentMedJson, currentmed::class.java)

            if (intent.getBooleanExtra("EDITABLE", true)) {
                initializeEditableMode(currentMed)
            } else {
                initializeNonEditableMode(currentMed)
            }
        } else {
            initializeDefaultMode()
        }


    }

    private fun initializeEditableMode(currentMed: currentmed) {
        createnotificationChannel()

        binding.MedicineName.setText(currentMed.MedName)
        binding.DosaGE.setText(currentMed.Dose)
        binding.numberodDays.setText(currentMed.NoofDays)
        Instructionflip(currentMed.aftermeal)
        binding.selectedtimes.visibility = View.VISIBLE

        for (item in currentMed.selectedtime) {
            selectedTimes.add(item)
        }

        val formattedString = selectedTimes.joinToString(separator = " , ") {
            it.replace(
                "[\",\\[\\]]".toRegex(),
                ""
            )
        }

        binding.addtimeButton.setBackgroundColor(Color.parseColor("#098EF8"))
        binding.addtimeButton.setText("Change Time")
        binding.selectedtimes.setText(formattedString)
        binding.addtimeButton.setTextColor(Color.WHITE)

        binding.backButtonMedication.setOnClickListener {
            onBackPressed()
        }

        binding.DosaGE.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
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
            if (binding.MedicineName.text.isNullOrBlank() &&
                binding.DosaGE.text.isNullOrBlank() &&
                binding.numberodDays.text.isNullOrBlank()
            ) {
                Toast.makeText(this, "Please Fill the required", Toast.LENGTH_LONG).show()
            } else {
                // Your logic for saving medication data
                val data = currentmed(
                    binding.MedicineName.text.toString().trim(),
                    binding.DosaGE.text.toString().trim(),
                    binding.numberodDays.text.toString().trim(),
                    selectedTimes,
                    afterMeal
                )

                collRef.document(binding.MedicineName.text.toString().trim()).set(data)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Medication added successfully", Toast.LENGTH_LONG)
                            .show()
                    }.addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to add medication: $e", Toast.LENGTH_LONG)
                            .show()
                    }

                historycollRef.document(
                    "${
                        binding.MedicineName.text.toString().trim()
                    }-${binding.numberodDays.text.toString().trim()} days"
                )
                    .set(data)
                    .addOnSuccessListener {
                        Toast.makeText(
                            this,
                            "Medication History added successfully",
                            Toast.LENGTH_LONG
                        ).show()
                    }.addOnFailureListener { e ->
                        Toast.makeText(
                            this,
                            "Failed to add medication history: $e",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                // Example code for setting alarms
                val inputFormat = SimpleDateFormat("hh:mm a")
                val outputFormat = SimpleDateFormat("HH:mm")

                // Assuming you have at least one selected time
                val date = inputFormat.parse(selectedTimes[0])
                val formattedTime = outputFormat.format(date)

                val parts = formattedTime.split(":")
                myHours = parts[0].toInt()
                myMinutes = parts[1].toInt()

                // Set alarms
                for (i in selectedTimes){
                    setAlarms(i)
                }

                // Additional logic as needed
                // ...

                // Finally, finish the activity
                finish()
            }

        }

        binding.beforemeallayout.setOnClickListener {
            afterMeal = false
            Instructionflip(afterMeal)
        }

        binding.aftermeal.setOnClickListener {
            afterMeal = true
            Instructionflip(afterMeal)
        }
    }


    private fun initializeNonEditableMode(currentMed: currentmed) {
        binding.MedicineName.setText(currentMed.MedName)
        binding.MedicineName.isFocusable = false
        binding.MedicineName.isFocusableInTouchMode = false
        binding.MedicineName.isClickable = false

        binding.DosaGE.setText(currentMed.Dose)
        binding.DosaGE.isFocusable = false
        binding.DosaGE.isFocusableInTouchMode = false
        binding.DosaGE.isClickable = false

        binding.numberodDays.setText(currentMed.NoofDays)
        binding.numberodDays.isFocusable = false
        binding.numberodDays.isFocusableInTouchMode = false
        binding.numberodDays.isClickable = false

        Instructionflip(currentMed.aftermeal)
        binding.selectedtimes.visibility = View.VISIBLE

        for (item in currentMed.selectedtime) {
            selectedTimes.add(item)
        }

        val formattedString = selectedTimes.joinToString(separator = " , ") {
            it.replace(
                "[\",\\[\\]]".toRegex(),
                ""
            )
        }


        binding.addtimeButton.setText("Time")
        binding.selectedtimes.setText(formattedString)
        binding.addtimeButton.setBackgroundColor(Color.parseColor("#F6F7F8"))
        binding.addtimeButton.setTextColor(Color.parseColor("#9B9B9B"))

        binding.addmedicationButton.isClickable = false
        binding.addmedicationButton.visibility = View.GONE

        binding.backButtonMedication.setOnClickListener {
            onBackPressed()
        }
    }


    private fun initializeDefaultMode() {
        binding.backButtonMedication.setOnClickListener {
            onBackPressed()
        }

        binding.DosaGE.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
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
            if (binding.MedicineName.text.isNullOrBlank() &&
                binding.DosaGE.text.isNullOrBlank() &&
                binding.numberodDays.text.isNullOrBlank() &&
                selectedTimes.isEmpty()
            ) {
                Toast.makeText(this, "Please Fill the required", Toast.LENGTH_LONG).show()
            } else {
                // Your logic for saving medication data
                val data = currentmed(
                    binding.MedicineName.text.toString().trim(),
                    binding.DosaGE.text.toString().trim(),
                    binding.numberodDays.text.toString().trim(),
                    selectedTimes,
                    afterMeal
                )

                collRef.document(binding.MedicineName.text.toString().trim()).set(data)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Medication added successfully", Toast.LENGTH_LONG)
                            .show()
                    }.addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to add medication: $e", Toast.LENGTH_LONG)
                            .show()
                    }

                historycollRef.document(
                    "${
                        binding.MedicineName.text.toString().trim()
                    }-${binding.numberodDays.text.toString().trim()} days"
                )
                    .set(data)
                    .addOnSuccessListener {
                        Toast.makeText(
                            this,
                            "Medication History added successfully",
                            Toast.LENGTH_LONG
                        ).show()
                    }.addOnFailureListener { e ->
                        Toast.makeText(
                            this,
                            "Failed to add medication history: $e",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                // Example code for setting alarms
                val inputFormat = SimpleDateFormat("hh:mm a")
                val outputFormat = SimpleDateFormat("HH:mm")

                // Assuming you have at least one selected time
                val date = inputFormat.parse(selectedTimes[0])
                val formattedTime = outputFormat.format(date)

                val parts = formattedTime.split(":")
                myHours = parts[0].toInt()
                myMinutes = parts[1].toInt()

                // Set alarms
                for (i in selectedTimes){
                    setAlarms(i)
                }


                // Additional logic as needed
                // ...

                // Finally, finish the activity
                finish()
            }

        }

        binding.beforemeallayout.setOnClickListener {
            afterMeal = false
            Instructionflip(afterMeal)
        }

        binding.aftermeal.setOnClickListener {
            afterMeal = true
            Instructionflip(afterMeal)
        }
    }


    private fun Instructionflip(afterMeal: Boolean) {
        if (afterMeal) {
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

    // Other methods...

    private fun createnotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "MedEase"
            val channelName = "Medicine Reminders"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance)
            channel.enableVibration(true)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun setAlarms(time : String) {
        val random = Random()
        val requestCode = random.nextInt()
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, NotificationReceiver::class.java)
        intent.putExtra("SELECTED_TIME",time)
        intent.putExtra("NOTIFICATION_TEXT", binding.MedicineName.text.toString())
        val pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent, 0)
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, myHours)
        calendar.set(Calendar.MINUTE, myMinutes)

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )

        Toast.makeText(this, "Alarm Set Successful for $time and ${binding.MedicineName.text.toString()}", Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                selectedTimes.clear()


                val timelist = data?.getStringArrayListExtra("RESULT")
                if (timelist != null) {
                    if (timelist.isNotEmpty()) {
                        for (item in timelist) {
                            selectedTimes.add(item)
                        }
                        binding.addtimeButton.setText("Change Time")
                        binding.selectedtimes.visibility = View.VISIBLE
                        val formattedString = selectedTimes.joinToString(separator = " , ") {
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
}
