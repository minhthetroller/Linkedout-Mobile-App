package com.example.linkedout.data.repository

import com.example.linkedout.data.local.UserPreferencesManager
import com.example.linkedout.data.model.*
import com.example.linkedout.data.remote.ApiService
import com.example.linkedout.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LinkedOutRepository @Inject constructor(
    private val apiService: ApiService,
    private val preferencesManager: UserPreferencesManager
) {

    // Auth methods
    fun signUpStep1(request: SignUpStep1Request): Flow<Resource<AuthResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.signUpStep1(request)
            if (response.success && response.data != null) {
                preferencesManager.saveAuthData(
                    response.data.token,
                    response.data.userId,
                    response.data.userType,
                    response.data.profileCompletionStep
                )
                emit(Resource.Success(response.data))
            } else {
                emit(Resource.Error(response.message ?: "Unknown error"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Network error"))
        }
    }

    fun signUpStep2Seeker(request: SignUpStep2SeekerRequest): Flow<Resource<ProfileCompletionData>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.signUpStep2Seeker(request)
            if (response.success && response.data != null) {
                preferencesManager.updateProfileCompletionStep(response.data.profileCompletionStep)
                emit(Resource.Success(response.data))
            } else {
                emit(Resource.Error(response.message ?: "Unknown error"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Network error"))
        }
    }

    fun signUpStep2Recruiter(request: SignUpStep2RecruiterRequest): Flow<Resource<ProfileCompletionData>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.signUpStep2Recruiter(request)
            if (response.success && response.data != null) {
                preferencesManager.updateProfileCompletionStep(response.data.profileCompletionStep)
                emit(Resource.Success(response.data))
            } else {
                emit(Resource.Error(response.message ?: "Unknown error"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Network error"))
        }
    }

    fun signUpStep3(request: SignUpStep3Request): Flow<Resource<ProfileCompletionData>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.signUpStep3(request)
            if (response.success && response.data != null) {
                preferencesManager.updateProfileCompletionStep(response.data.profileCompletionStep)
                emit(Resource.Success(response.data))
            } else {
                emit(Resource.Error(response.message ?: "Unknown error"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Network error"))
        }
    }

    fun login(request: LoginRequest): Flow<Resource<AuthResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.login(request)
            if (response.success && response.data != null) {
                preferencesManager.saveAuthData(
                    response.data.token,
                    response.data.userId,
                    response.data.userType,
                    response.data.profileCompletionStep
                )
                emit(Resource.Success(response.data))
            } else {
                emit(Resource.Error(response.message ?: "Invalid credentials"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Network error"))
        }
    }

    fun getCurrentUser(): Flow<Resource<ProfileData>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getCurrentUser()
            if (response.success && response.data != null) {
                emit(Resource.Success(response.data))
            } else {
                emit(Resource.Error(response.message ?: "Failed to load profile"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Network error"))
        }
    }

    suspend fun logout() {
        preferencesManager.clearAuthData()
    }

    // Job methods - Recruiter
    fun createJob(request: CreateJobRequest): Flow<Resource<JobResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.createJob(request)
            if (response.success && response.data != null) {
                emit(Resource.Success(response.data))
            } else {
                emit(Resource.Error(response.message ?: "Failed to create job"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Network error"))
        }
    }

    fun getRecruiterJobs(): Flow<Resource<List<Job>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getRecruiterJobs()
            if (response.success && response.data != null) {
                emit(Resource.Success(response.data.jobs))
            } else {
                emit(Resource.Error(response.message ?: "Failed to load jobs"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Network error"))
        }
    }

    fun updateJob(jobId: Int, request: UpdateJobRequest): Flow<Resource<JobResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.updateJob(jobId, request)
            if (response.success && response.data != null) {
                emit(Resource.Success(response.data))
            } else {
                emit(Resource.Error(response.message ?: "Failed to update job"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Network error"))
        }
    }

    fun deleteJob(jobId: Int): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.deleteJob(jobId)
            if (response.success) {
                emit(Resource.Success(Unit))
            } else {
                emit(Resource.Error(response.message ?: "Failed to delete job"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Network error"))
        }
    }

    fun getJobApplicants(jobId: Int): Flow<Resource<JobApplicantsResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getJobApplicants(jobId)
            if (response.success && response.data != null) {
                emit(Resource.Success(response.data))
            } else {
                emit(Resource.Error(response.message ?: "Failed to load applicants"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Network error"))
        }
    }

    fun getApplicantDetails(applicationId: Int): Flow<Resource<Applicant>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getApplicantDetails(applicationId)
            if (response.success && response.data != null) {
                emit(Resource.Success(response.data.application))
            } else {
                emit(Resource.Error(response.message ?: "Failed to load applicant details"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Network error"))
        }
    }

    fun updateApplicationStatus(applicationId: Int, status: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val request = UpdateApplicationStatusRequest(status)
            val response = apiService.updateApplicationStatus(applicationId, request)
            if (response.success) {
                emit(Resource.Success(Unit))
            } else {
                emit(Resource.Error(response.message ?: "Failed to update status"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Network error"))
        }
    }

    // Job methods - Seeker
    fun browseJobs(
        location: String? = null,
        salaryMin: Double? = null,
        salaryMax: Double? = null,
        employmentType: String? = null,
        tags: String? = null,
        page: Int = 1,
        limit: Int = 20
    ): Flow<Resource<JobListResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.browseJobs(location, salaryMin, salaryMax, employmentType, tags, page, limit)
            if (response.success && response.data != null) {
                emit(Resource.Success(response.data))
            } else {
                emit(Resource.Error(response.message ?: "Failed to load jobs"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Network error"))
        }
    }

    fun getJobDetails(jobId: Int): Flow<Resource<Job>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getJobDetails(jobId)
            if (response.success && response.data != null) {
                emit(Resource.Success(response.data.job))
            } else {
                emit(Resource.Error(response.message ?: "Failed to load job details"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Network error"))
        }
    }

    fun getRecommendedJobs(page: Int = 1, limit: Int = 20): Flow<Resource<JobListResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getRecommendedJobs(page, limit)
            if (response.success && response.data != null) {
                emit(Resource.Success(response.data))
            } else {
                emit(Resource.Error(response.message ?: "Failed to load recommendations"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Network error"))
        }
    }

    fun applyToJob(jobId: Int, coverLetter: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val request = ApplyJobRequest(coverLetter = coverLetter)
            val response = apiService.applyToJob(jobId, request)
            if (response.success) {
                emit(Resource.Success(Unit))
            } else {
                emit(Resource.Error(response.message ?: "Failed to apply to job"))
            }
        } catch (e: retrofit2.HttpException) {
            val errorMessage = when (e.code()) {
                400 -> "You have already applied to this job"
                401 -> "Please login to apply"
                403 -> "Please complete your profile before applying"
                404 -> "Job not found"
                500 -> "Server error. Please try again later"
                else -> e.message() ?: "Failed to apply"
            }
            emit(Resource.Error(errorMessage))
        } catch (e: java.net.UnknownHostException) {
            emit(Resource.Error("Network error. Please check your internet connection"))
        } catch (e: java.net.SocketTimeoutException) {
            emit(Resource.Error("Connection timeout. Please try again"))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to apply to job"))
        }
    }

    fun getSeekerApplications(status: String? = null, page: Int = 1, limit: Int = 20): Flow<Resource<SeekerApplicationsResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getSeekerApplications(status, page, limit)
            if (response.success && response.data != null) {
                emit(Resource.Success(response.data))
            } else {
                emit(Resource.Error(response.message ?: "Failed to load applications"))
            }
        } catch (e: retrofit2.HttpException) {
            val errorMessage = when (e.code()) {
                401 -> "Please login to view applications"
                403 -> "Access denied"
                500 -> "Server error. Please try again later"
                else -> e.message() ?: "Failed to load applications"
            }
            emit(Resource.Error(errorMessage))
        } catch (e: java.net.UnknownHostException) {
            emit(Resource.Error("Network error. Please check your internet connection"))
        } catch (e: java.net.SocketTimeoutException) {
            emit(Resource.Error("Connection timeout. Please try again"))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to load applications"))
        }
    }

    // File upload methods
    fun uploadResume(file: File): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val requestBody = file.asRequestBody("application/pdf".toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("resume", file.name, requestBody)
            val response = apiService.uploadResume(part)
            if (response.success && response.data?.resumeUrl != null) {
                emit(Resource.Success(response.data.resumeUrl))
            } else {
                emit(Resource.Error(response.message ?: "Failed to upload resume"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Network error"))
        }
    }

    fun uploadProfileImage(file: File): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val mediaType = when (file.extension.lowercase()) {
                "jpg", "jpeg" -> "image/jpeg"
                "png" -> "image/png"
                else -> "image/*"
            }.toMediaTypeOrNull()
            val requestBody = file.asRequestBody(mediaType)
            val part = MultipartBody.Part.createFormData("image", file.name, requestBody)
            val response = apiService.uploadProfileImage(part)
            if (response.success && response.data?.imageUrl != null) {
                emit(Resource.Success(response.data.imageUrl))
            } else {
                emit(Resource.Error(response.message ?: "Failed to upload image"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Network error"))
        }
    }

    fun getSignedUrl(fileUrl: String): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getSignedUrl(fileUrl)
            if (response.success && response.data != null) {
                emit(Resource.Success(response.data.signedUrl))
            } else {
                emit(Resource.Error(response.message ?: "Failed to get file URL"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Network error"))
        }
    }

    // Preference getters
    fun getAuthToken(): Flow<String?> = preferencesManager.authToken
    fun getUserType(): Flow<String?> = preferencesManager.userType
    fun getProfileCompletionStep(): Flow<Int?> = preferencesManager.profileCompletionStep
}
