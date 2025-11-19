package com.example.linkedout.data.model

data class Job(
    val id: Int,
    val recruiterId: Int,
    val title: String,
    val about: String? = null,
    val description: String,
    val salaryMin: Double? = null,
    val salaryMax: Double? = null,
    val benefits: String? = null,
    val location: String? = null,
    val employmentType: String? = null,
    val status: String,
    val createdAt: String,
    val updatedAt: String,
    val tags: List<Tag>? = null,
    val recruiterEmail: String? = null,
    val companyName: String? = null,
    val companySize: String? = null,
    val companyWebsite: String? = null,
    val matchScore: Int? = null,
    val matchScoreDisplay: String? = null
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
    val salaryMin: Double? = null,
    val salaryMax: Double? = null,
    val benefits: String? = null,
    val location: String? = null,
    val employmentType: String? = null
)

data class UpdateJobRequest(
    val title: String? = null,
    val about: String? = null,
    val description: String? = null,
    val salaryMin: Double? = null,
    val salaryMax: Double? = null,
    val benefits: String? = null,
    val location: String? = null,
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

