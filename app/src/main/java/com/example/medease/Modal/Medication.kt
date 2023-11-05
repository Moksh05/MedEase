package com.example.medease.Modal

data class currentmed(
    var MedName : String,
    var Dose: String,
    var NoofDays:String,
    var selectedtime: MutableList<String>,
    var aftermeal:Boolean
)
