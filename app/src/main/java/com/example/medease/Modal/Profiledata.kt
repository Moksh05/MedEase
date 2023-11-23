package com.example.medease.Modal

data class Profiledata(
    var name: String,
    var address: String? ,
    val age: String? ,
    val sex: String? ,
    val contactNo: String?
) {
    // Secondary constructor with additional initialization logic if needed
   constructor():this("","","","","")
}
