package com.example.linkedout.ui.jobs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.linkedout.data.model.*
import com.example.linkedout.data.repository.LinkedOutRepository
import com.example.linkedout.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JobViewModel @Inject constructor(
    private val repository: LinkedOutRepository
) : ViewModel() {

    private val _jobsState = MutableStateFlow<Resource<List<Job>>?>(null)
    val jobsState: StateFlow<Resource<List<Job>>?> = _jobsState.asStateFlow()

    private val _jobDetailState = MutableStateFlow<Resource<Job>?>(null)
    val jobDetailState: StateFlow<Resource<Job>?> = _jobDetailState.asStateFlow()

    private val _recommendedJobsState = MutableStateFlow<Resource<JobListResponse>?>(null)
    val recommendedJobsState: StateFlow<Resource<JobListResponse>?> = _recommendedJobsState.asStateFlow()

    private val _createJobState = MutableStateFlow<Resource<JobResponse>?>(null)
    val createJobState: StateFlow<Resource<JobResponse>?> = _createJobState.asStateFlow()

    private val _deleteJobState = MutableStateFlow<Resource<Unit>?>(null)
    val deleteJobState: StateFlow<Resource<Unit>?> = _deleteJobState.asStateFlow()

    private val _applicantsState = MutableStateFlow<Resource<JobApplicantsResponse>?>(null)
    val applicantsState: StateFlow<Resource<JobApplicantsResponse>?> = _applicantsState.asStateFlow()

    private val _applicantState = MutableStateFlow<Resource<Applicant>?>(Resource.Loading())
    val applicantState: StateFlow<Resource<Applicant>?> = _applicantState.asStateFlow()

    private val _updateApplicationStatusState = MutableStateFlow<Resource<Unit>?>(null)
    val updateApplicationStatusState: StateFlow<Resource<Unit>?> = _updateApplicationStatusState.asStateFlow()

    private val _applyJobState = MutableStateFlow<Resource<Unit>?>(null)
    val applyJobState: StateFlow<Resource<Unit>?> = _applyJobState.asStateFlow()

    private val _profileImageSignedUrl = MutableStateFlow<String?>(null)
    val profileImageSignedUrl: StateFlow<String?> = _profileImageSignedUrl.asStateFlow()

    private val _resumeSignedUrl = MutableStateFlow<String?>(null)
    val resumeSignedUrl: StateFlow<String?> = _resumeSignedUrl.asStateFlow()

    // Recruiter methods
    fun getRecruiterJobs() {
        viewModelScope.launch {
            repository.getRecruiterJobs().collect { result ->
                _jobsState.value = result
            }
        }
    }

    fun createJob(request: CreateJobRequest) {
        viewModelScope.launch {
            repository.createJob(request).collect { result ->
                _createJobState.value = result
            }
        }
    }

    fun updateJob(jobId: Int, request: UpdateJobRequest) {
        viewModelScope.launch {
            repository.updateJob(jobId, request).collect { result ->
                if (result is Resource.Success) {
                    getRecruiterJobs()
                }
            }
        }
    }

    fun deleteJob(jobId: Int) {
        viewModelScope.launch {
            repository.deleteJob(jobId).collect { result ->
                _deleteJobState.value = result
                if (result is Resource.Success) {
                    getRecruiterJobs()
                }
            }
        }
    }

    fun getJobApplicants(jobId: Int) {
        viewModelScope.launch {
            repository.getJobApplicants(jobId).collect { result ->
                _applicantsState.value = result
            }
        }
    }

    fun getApplicantDetails(applicationId: Int) {
        viewModelScope.launch {
            repository.getApplicantDetails(applicationId).collect { result ->
                _applicantState.value = result
                // Fetch signed URLs if available
                if (result is Resource.Success && result.data != null) {
                    result.data.profileImageS3Url?.let { fetchProfileImageSignedUrl(it) }
                    result.data.resumeS3Url?.let { fetchResumeSignedUrl(it) }
                }
            }
        }
    }

    private fun fetchProfileImageSignedUrl(s3Url: String) {
        viewModelScope.launch {
            repository.getSignedUrl(s3Url).collect { resource ->
                if (resource is Resource.Success) {
                    _profileImageSignedUrl.value = resource.data
                }
            }
        }
    }

    private fun fetchResumeSignedUrl(s3Url: String) {
        viewModelScope.launch {
            repository.getSignedUrl(s3Url).collect { resource ->
                if (resource is Resource.Success) {
                    _resumeSignedUrl.value = resource.data
                }
            }
        }
    }

    fun updateApplicationStatus(applicationId: Int, status: String) {
        viewModelScope.launch {
            repository.updateApplicationStatus(applicationId, status).collect { result ->
                _updateApplicationStatusState.value = result
                if (result is Resource.Success) {
                    getApplicantDetails(applicationId) // Refresh details
                }
            }
        }
    }

    // Seeker methods
    fun browseJobs(
        location: String? = null,
        salaryMin: Double? = null,
        salaryMax: Double? = null,
        employmentType: String? = null,
        tags: String? = null,
        page: Int = 1
    ) {
        viewModelScope.launch {
            repository.browseJobs(location, salaryMin, salaryMax, employmentType, tags, page)
                .collect { result ->
                    when (result) {
                        is Resource.Success -> _jobsState.value = Resource.Success(result.data?.jobs ?: emptyList())
                        is Resource.Error -> _jobsState.value = Resource.Error(result.message ?: "Failed to load jobs")
                        is Resource.Loading -> _jobsState.value = Resource.Loading()
                    }
                }
        }
    }

    fun getRecommendedJobs(page: Int = 1) {
        viewModelScope.launch {
            repository.getRecommendedJobs(page).collect { result ->
                _recommendedJobsState.value = result
            }
        }
    }

    fun getJobDetails(jobId: Int) {
        viewModelScope.launch {
            repository.getJobDetails(jobId).collect { result ->
                _jobDetailState.value = result
            }
        }
    }

    fun applyToJob(jobId: Int, coverLetter: String) {
        viewModelScope.launch {
            repository.applyToJob(jobId, coverLetter).collect { result ->
                _applyJobState.value = result
            }
        }
    }

    fun resetCreateJobState() {
        _createJobState.value = null
    }

    fun resetDeleteJobState() {
        _deleteJobState.value = null
    }

    fun resetApplyJobState() {
        _applyJobState.value = null
    }
}
