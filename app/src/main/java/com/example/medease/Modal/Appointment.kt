package com.example.medease.Modal

data class Appointment(
    var email : String,
    var fullName : String,
    var liscenseID : String,
    var type : String,
    var phoneNumber : String,
    var date : String,
    var time : String
) {
    constructor() : this("","","","","","","")

}
