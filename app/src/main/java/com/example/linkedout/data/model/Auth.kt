package com.example.linkedout.data.model

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    val token: String,
    val userId: Int,
    val userType: String,
    val profileCompletionStep: Int
)

data class SignUpStep1Request(
    val email: String,
    val password: String,
    @SerializedName("user_type")
    val userType: String,
    @SerializedName("full_name")
    val fullName: String,
    @SerializedName("birth_date")
    val birthDate: String
)

data class SignUpStep2SeekerRequest(
    @SerializedName("current_job")
    val currentJob: String? = null,
    @SerializedName("years_experience")
    val yearsExperience: Int? = null,
    val location: String? = null,
    val phone: String? = null
)

data class SignUpStep2RecruiterRequest(
    @SerializedName("company_name")
    val companyName: String,
    @SerializedName("company_size")
    val companySize: String? = null,
    @SerializedName("company_website")
    val companyWebsite: String? = null,
    val phone: String? = null
)

data class SignUpStep3Request(
    @SerializedName("preferred_job_titles")
    val preferredJobTitles: List<String>? = null,
    @SerializedName("preferred_industries")
    val preferredIndustries: List<String>? = null,
    @SerializedName("preferred_locations")
    val preferredLocations: List<String>? = null,
    @SerializedName("salary_expectation_min")
    val salaryExpectationMin: Double? = null,
    @SerializedName("salary_expectation_max")
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

