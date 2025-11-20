package com.example.linkedout.data.model

import com.google.gson.annotations.SerializedName

data class Job(
    val id: Int,
    @SerializedName("recruiter_id")
    val recruiterId: Int,
    val title: String,
    val about: String? = null,
    val description: String,
    @SerializedName("salary_min")
    val salaryMin: Double? = null,
    @SerializedName("salary_max")
    val salaryMax: Double? = null,
    val benefits: String? = null,
    val location: String? = null,
    @SerializedName("employment_type")
    val employmentType: String? = null,
    val status: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    val tags: List<Tag>? = null,
    @SerializedName("recruiter_email")
    val recruiterEmail: String? = null,
    @SerializedName("company_name")
    val companyName: String? = null,
    @SerializedName("company_size")
    val companySize: String? = null,
    @SerializedName("company_website")
    val companyWebsite: String? = null,
    @SerializedName("match_score")
    val matchScore: Int? = null,
    @SerializedName("match_score_display")
    val matchScoreDisplay: String? = null,
    @SerializedName("has_applied")
    val hasApplied: Boolean? = null
)

data class Tag(
    val id: Int,
    val name: String,
    val category: String
)

data class CreateJobRequest(
    val title: String,
    val about: String? = null,
    val description: String,
    @SerializedName("salary_min")
    val salaryMin: Double? = null,
    @SerializedName("salary_max")
    val salaryMax: Double? = null,
    val benefits: String? = null,
    val location: String? = null,
    @SerializedName("employment_type")
    val employmentType: String? = null
)

data class UpdateJobRequest(
    val title: String? = null,
    val about: String? = null,
    val description: String? = null,
    @SerializedName("salary_min")
    val salaryMin: Double? = null,
    @SerializedName("salary_max")
    val salaryMax: Double? = null,
    val benefits: String? = null,
    val location: String? = null,
    @SerializedName("employment_type")
    val employmentType: String? = null,
    val status: String? = null
)

data class JobResponse(
    val job: Job,
    val tags: List<Tag>? = null
)

data class JobListResponse(
    val jobs: List<Job>,
    val pagination: Pagination? = null,
    val totalPreferredTags: Int? = null,
    val message: String? = null
)

data class Pagination(
    val page: Int,
    val limit: Int,
    val total: Int,
    val pages: Int
)

data class ApplyJobRequest(
    @SerializedName("cover_letter")
    val coverLetter: String
)
