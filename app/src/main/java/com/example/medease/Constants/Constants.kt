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
        doctorttype("General Practitioner", R.drawable.general),
        doctorttype("Pediatrician", R.drawable.pedia),
        doctorttype("Surgeon", R.drawable.surgeon),
        doctorttype("Cardiologist", R.drawable.cardiologist),
        doctorttype("Dermatologist", R.drawable.dermatologist),
        doctorttype("Gynecologist", R.drawable.gyna),
        doctorttype("Neurologist", R.drawable.neurologist),
        doctorttype("Radiologist", R.drawable.radiologist),
        doctorttype("Gastroenterologist", R.drawable.gas),
        doctorttype("Anesthesiologist", R.drawable.anesthesia),
        doctorttype("Orthopedic", R.drawable.ortho),
        doctorttype("Psychiatrist", R.drawable.psychatrist),
        doctorttype("ENT", R.drawable.ent),
        )
}