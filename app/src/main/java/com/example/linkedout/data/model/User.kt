package com.example.linkedout.data.model

data class User(
    val id: Int,
    val email: String,
    val userType: String,
    val createdAt: String
)

data class UserProfile(
    val id: Int,
    val userId: Int,
    val fullName: String,
    val birthDate: String? = null,
    val phone: String? = null,
    val location: String? = null,
    val currentJob: String? = null,
    val yearsExperience: Int? = null,
    val resumeS3Url: String? = null,
    val profileImageS3Url: String? = null,
    val companyName: String? = null,
    val companySize: String? = null,
    val companyWebsite: String? = null,
    val companyLogoS3Url: String? = null,
    val profileCompletionStep: Int,
    val createdAt: String,
    val updatedAt: String
)

data class UserPreferences(
    val id: Int,
    val userId: Int,
    val preferredJobTitles: List<String>? = null,
    val preferredIndustries: List<String>? = null,
    val preferredLocations: List<String>? = null,
    val salaryExpectationMin: Double? = null,
    val salaryExpectationMax: Double? = null,
    val isSkipped: Boolean,
    val createdAt: String,
    val updatedAt: String
)

data class ProfileData(
    val user: User,
    val profile: UserProfile,
    val preferences: UserPreferences? = null,
    val canUseApp: Boolean,
    val profileComplete: Boolean
)

