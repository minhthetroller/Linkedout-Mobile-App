package com.example.linkedout.data.model

data class AuthResponse(
    val token: String,
    val userId: Int,
    val userType: String,
    val profileCompletionStep: Int
)

data class SignUpStep1Request(
    val email: String,
    val password: String,
    val userType: String,
    val fullName: String,
    val birthDate: String
)

data class SignUpStep2SeekerRequest(
    val currentJob: String? = null,
    val yearsExperience: Int? = null,
    val location: String? = null,
    val phone: String? = null
)

data class SignUpStep2RecruiterRequest(
    val companyName: String,
    val companySize: String? = null,
    val companyWebsite: String? = null,
    val phone: String? = null
)

data class SignUpStep3Request(
    val preferredJobTitles: List<String>? = null,
    val preferredIndustries: List<String>? = null,
    val preferredLocations: List<String>? = null,
    val salaryExpectationMin: Double? = null,
    val salaryExpectationMax: Double? = null,
    val skip: Boolean? = null
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class ProfileCompletionData(
    val profileCompletionStep: Int
)

