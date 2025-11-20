package com.example.linkedout.data.remote

import com.example.linkedout.data.model.*
import okhttp3.MultipartBody
import retrofit2.http.*

interface ApiService {

    // Authentication
    @POST("auth/signup/step1")
    suspend fun signUpStep1(@Body request: SignUpStep1Request): ApiResponse<AuthResponse>

    @POST("auth/signup/step2")
    suspend fun signUpStep2Seeker(@Body request: SignUpStep2SeekerRequest): ApiResponse<ProfileCompletionData>

    @POST("auth/signup/step2")
    suspend fun signUpStep2Recruiter(@Body request: SignUpStep2RecruiterRequest): ApiResponse<ProfileCompletionData>

    @POST("auth/signup/step3")
    suspend fun signUpStep3(@Body request: SignUpStep3Request): ApiResponse<ProfileCompletionData>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<AuthResponse>

    @GET("auth/me")
    suspend fun getCurrentUser(): ApiResponse<ProfileData>

    // Jobs - Recruiter
    @POST("recruiter/jobs")
    suspend fun createJob(@Body request: CreateJobRequest): ApiResponse<JobResponse>

    @GET("recruiter/jobs")
    suspend fun getRecruiterJobs(): ApiResponse<JobListResponse>

    @PUT("recruiter/jobs/{id}")
    suspend fun updateJob(
        @Path("id") jobId: Int,
        @Body request: UpdateJobRequest
    ): ApiResponse<JobResponse>

    @DELETE("recruiter/jobs/{id}")
    suspend fun deleteJob(@Path("id") jobId: Int): ApiResponse<Unit>

    @GET("recruiter/jobs/{id}/applicants")
    suspend fun getJobApplicants(@Path("id") jobId: Int): ApiResponse<JobApplicantsResponse>

    @GET("recruiter/applications/{id}")
    suspend fun getApplicantDetails(@Path("id") applicationId: Int): ApiResponse<ApplicantDetailsResponse>

    @PUT("recruiter/applications/{id}/status")
    suspend fun updateApplicationStatus(
        @Path("id") applicationId: Int,
        @Body request: UpdateApplicationStatusRequest
    ): ApiResponse<Unit>

    // Jobs - Seeker
    @GET("jobs")
    suspend fun browseJobs(
        @Query("location") location: String? = null,
        @Query("salary_min") salaryMin: Double? = null,
        @Query("salary_max") salaryMax: Double? = null,
        @Query("employment_type") employmentType: String? = null,
        @Query("tags") tags: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): ApiResponse<JobListResponse>

    @GET("jobs/{id}")
    suspend fun getJobDetails(@Path("id") jobId: Int): ApiResponse<JobResponse>

    @GET("jobs/recommended")
    suspend fun getRecommendedJobs(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): ApiResponse<JobListResponse>

    @POST("jobs/{id}/apply")
    suspend fun applyToJob(
        @Path("id") jobId: Int,
        @Body request: ApplyJobRequest
    ): ApiResponse<Unit>

    @GET("seeker/applications")
    suspend fun getSeekerApplications(
        @Query("status") status: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): ApiResponse<SeekerApplicationsResponse>

    // File Uploads
    @Multipart
    @POST("upload/resume")
    suspend fun uploadResume(@Part resume: MultipartBody.Part): ApiResponse<FileUploadResponse>

    @Multipart
    @POST("upload/profile-image")
    suspend fun uploadProfileImage(@Part image: MultipartBody.Part): ApiResponse<FileUploadResponse>

    @GET("upload/file-url")
    suspend fun getSignedUrl(@Query("fileUrl") fileUrl: String): ApiResponse<SignedUrlResponse>
}
