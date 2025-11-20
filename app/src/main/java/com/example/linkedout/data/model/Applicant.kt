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
    @SerializedName("application_updated_at")
    val applicationUpdatedAt: String? = null,
    @SerializedName("seeker_id")
    val seekerId: Int,
    @SerializedName("seeker_email")
    val seekerEmail: String? = null,
    @SerializedName("full_name")
    val fullName: String,
    @SerializedName("current_job")
    val currentJob: String?,
    @SerializedName("years_experience")
    val yearsExperience: Int?,
    @SerializedName("location")
    val location: String?,
    @SerializedName("phone")
    val phone: String? = null,
    @SerializedName("profile_image_s3_url")
    val profileImageS3Url: String?,
    @SerializedName("resume_s3_url")
    val resumeS3Url: String? = null,
    @SerializedName("job_id")
    val jobId: Int? = null,
    @SerializedName("job_title")
    val jobTitle: String? = null,
    val email: String? = null
)

data class JobApplicantsResponse(
    val applicants: List<Applicant>,
    val job: Job,
    val statistics: Map<String, Int>
)

data class UpdateApplicationStatusRequest(
    val status: String
)

data class ApplicantDetailsResponse(
    val application: Applicant
)

// Seeker Applications
data class SeekerApplication(
    @SerializedName("application_id")
    val applicationId: Int,
    @SerializedName("application_status")
    val applicationStatus: String,
    @SerializedName("cover_letter")
    val coverLetter: String?,
    @SerializedName("applied_at")
    val appliedAt: String,
    @SerializedName("application_updated_at")
    val applicationUpdatedAt: String? = null,
    @SerializedName("job_id")
    val jobId: Int,
    @SerializedName("job_title")
    val jobTitle: String,
    @SerializedName("job_location")
    val jobLocation: String?,
    @SerializedName("employment_type")
    val employmentType: String?,
    @SerializedName("company_name")
    val companyName: String?,
    @SerializedName("salary_min")
    val salaryMin: Double?,
    @SerializedName("salary_max")
    val salaryMax: Double?
)

data class SeekerApplicationsResponse(
    val applications: List<SeekerApplication>,
    val statistics: ApplicationStatistics? = null,
    val pagination: Pagination? = null
)

data class ApplicationStatistics(
    val total: Int,
    val pending: Int,
    val reviewed: Int,
    val accepted: Int,
    val rejected: Int
)

