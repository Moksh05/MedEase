package com.example.medease.Constants

import com.example.medease.Modal.doctorttype
import com.example.medease.R

object Constants {

    const val APP_ID = "55279f0d21ad43c589ff0ac65bd0969c"
    const val BASE_URL = "https://newsapi.org/v2/"
const val REQUEST_CODE = 123
const val tittleExtra = "tittle"
const val messageExtra = "Message"


    val TypeofDoctor = mutableListOf<doctorttype>(doctorttype("Pulmologist", R.drawable.pulmonologist_icon),
        doctorttype("Pulmologist", R.drawable.pulmonologist_icon),
        doctorttype("General Practitioner", R.drawable.pulmonologist_icon),
        doctorttype("Pediatrician", R.drawable.pulmonologist_icon),
        doctorttype("Surgeon", R.drawable.pulmonologist_icon),
        doctorttype("Cardiologist", R.drawable.pulmonologist_icon),
        doctorttype("Dermatologist", R.drawable.pulmonologist_icon),
        doctorttype("Gynecologist", R.drawable.pulmonologist_icon),
        doctorttype("Neurologist", R.drawable.pulmonologist_icon),
        doctorttype("Radiologist", R.drawable.pulmonologist_icon),
        doctorttype("Gastroenterologist", R.drawable.pulmonologist_icon),
        doctorttype("Anesthesiologist", R.drawable.pulmonologist_icon),
        doctorttype("Orthopedic", R.drawable.pulmonologist_icon),
        doctorttype("Psychiatrist", R.drawable.pulmonologist_icon),
        doctorttype("ENT", R.drawable.pulmonologist_icon),
        )
}