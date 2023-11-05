package com.example.medease.Modal

data class DoctorDetail(
    val name: String,
    val gender: String,
    val dateOfBirth: String,
    val specialization: String,
    val licenseNumber: String,
    val clinicName: String,
    val contactNumber: String,
    val email: String,
    val experience: String,
    val address: String,
    val fee: String,
    val languagesSpoken: MutableList<String>,
    val education: Education
) {
    constructor() : this(
        "", "","", "", "", "", "", "", "", "", "", mutableListOf(), Education("", null, null)
    )
}
data class Education(
    val dentalSchool: String?,
    val residency: String?,
    val boardCertification: String?
){
    constructor() : this("", "", "")
}