package com.example.linkedout.data.model

import com.google.gson.annotations.SerializedName

data class User(
    val id: Int,
    val email: String,
    @SerializedName("user_type")
    val userType: String,
    @SerializedName("created_at")
    val createdAt: String
)

data class UserProfile(
    val id: Int,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("full_name")
    val fullName: String,
    @SerializedName("birth_date")
    val birthDate: String? = null,
    val phone: String? = null,
    val location: String? = null,
    @SerializedName("current_job")
    val currentJob: String? = null,
    @SerializedName("years_experience")
    val yearsExperience: Int? = null,
    @SerializedName("resume_s3_url")
    val resumeS3Url: String? = null,
    @SerializedName("profile_image_s3_url")
    val profileImageS3Url: String? = null,
    @SerializedName("company_name")
    val companyName: String? = null,
    @SerializedName("company_size")
    val companySize: String? = null,
    @SerializedName("company_website")
    val companyWebsite: String? = null,
    @SerializedName("company_logo_s3_url")
    val companyLogoS3Url: String? = null,
    @SerializedName("profile_completion_step")
    val profileCompletionStep: Int,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String
)

data class UserPreferences(
    val id: Int,
    @SerializedName("user_id")
    val userId: Int,
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
    @SerializedName("is_skipped")
    val isSkipped: Boolean,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String
)

data class ProfileData(
    val user: User,
    val profile: UserProfile,
    val preferences: UserPreferences? = null,
    @SerializedName("canUseApp")
    val canUseApp: Boolean,
    @SerializedName("profileComplete")
    val profileComplete: Boolean
)

