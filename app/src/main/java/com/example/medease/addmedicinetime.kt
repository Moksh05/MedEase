package com.example.medease

import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.widget.LinearLayoutCompat
import com.example.medease.Constants.Constants
import com.example.medease.databinding.ActivityAddmedicinetimeBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import kotlin.math.min

class addmedicinetime : AppCompatActivity() {
    private lateinit var binding : ActivityAddmedicinetimeBinding
    var Dosageamnt = 0
    var selectedTimes = mutableListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddmedicinetimeBinding.inflate(layoutInflater)
        setContentView(binding.root)



        if (intent.getStringExtra("Dosevalue")!= null){
            var Dosage = intent.getStringExtra("Dosevalue")
            if (Dosage != null) {
                Dosageamnt= Dosage.toInt()
                for (i in 1..Dosageamnt) {
                    val layout = findViewById<LinearLayoutCompat>(resources.getIdentifier("Medtime_layout$i", "id", packageName))

                    layout.visibility = View.VISIBLE


                }
            }
        }

        binding.medtimeSelector1.setOnClickListener {
            opendialog(binding.medtimeEdittext1)
        }

        binding.medtimeSelector2.setOnClickListener {
            opendialog(binding.medtimeEdittext2)
        }

        binding.medtimeSelector3.setOnClickListener {
            opendialog(binding.medtimeEdittext3)
        }

        binding.medtimeSelector4.setOnClickListener {
            opendialog(binding.medtimeEdittext4)
        }
        binding.medtimeSelector5.setOnClickListener {
            opendialog(binding.medtimeEdittext5)
        }
        binding.medtimeSelector6.setOnClickListener {
            opendialog(binding.medtimeEdittext6)
        }



        binding.addmedicationButton.setOnClickListener {
            val resultIntent = Intent()
            resultIntent.putStringArrayListExtra("RESULT",ArrayList(selectedTimes))
            setResult(RESULT_OK,resultIntent)
            finish()
        }
binding.backButtonMedication.setOnClickListener {
    onBackPressed()
}

    }

    private fun opendialog(editext:EditText) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            val selectedTime = formatTime(hourOfDay, minute)
            editext.setText(selectedTime)
            selectedTimes.add(selectedTime)
        }, hour, minute, false)

        timePickerDialog.show()
    }

    private fun formatTime(hour: Int, minute: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        val simpleDateFormat = SimpleDateFormat("h:mm a")
        return simpleDateFormat.format(calendar.time)
    }




}