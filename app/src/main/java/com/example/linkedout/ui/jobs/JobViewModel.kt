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

    fun resetCreateJobState() {
        _createJobState.value = null
    }

    fun resetDeleteJobState() {
        _deleteJobState.value = null
    }
}
