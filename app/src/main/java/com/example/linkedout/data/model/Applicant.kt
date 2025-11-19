package com.example.linkedout.data.model

import com.google.gson.annotations.SerializedName

data class Applicant(
    @SerializedName("application_id")
    val applicationId: Int,
    @SerializedName("application_status")
    val applicationStatus: String,
    @SerializedName("cover_letter")
    val coverLetter: String?,
    @SerializedName("applied_at")
    val appliedAt: String,
    @SerializedName("seeker_id")
    val seekerId: Int,
    @SerializedName("full_name")
    val fullName: String,
    @SerializedName("current_job")
    val currentJob: String?,
    @SerializedName("years_experience")
    val yearsExperience: Int?,
    @SerializedName("location")
    val location: String?,
    @SerializedName("profile_image_s3_url")
    val profileImageS3Url: String?
)

data class JobApplicantsResponse(
    val applicants: List<Applicant>,
    val job: Job,
    val statistics: Map<String, Int>
)

