package com.example.linkedout.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.linkedout.data.model.ProfileData
import com.example.linkedout.data.repository.LinkedOutRepository
import com.example.linkedout.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: LinkedOutRepository
) : ViewModel() {

    private val _profileState = MutableStateFlow<Resource<ProfileData>?>(null)
    val profileState: StateFlow<Resource<ProfileData>?> = _profileState.asStateFlow()

    private val _uploadResumeState = MutableStateFlow<Resource<String>?>(null)
    val uploadResumeState: StateFlow<Resource<String>?> = _uploadResumeState.asStateFlow()

    private val _uploadImageState = MutableStateFlow<Resource<String>?>(null)
    val uploadImageState: StateFlow<Resource<String>?> = _uploadImageState.asStateFlow()

    private val _profileImageSignedUrl = MutableStateFlow<String?>(null)
    val profileImageSignedUrl: StateFlow<String?> = _profileImageSignedUrl.asStateFlow()

    private val _resumeFileName = MutableStateFlow<String?>(null)
    val resumeFileName: StateFlow<String?> = _resumeFileName.asStateFlow()

    private val _applicationsState = MutableStateFlow<Resource<com.example.linkedout.data.model.SeekerApplicationsResponse>?>(null)
    val applicationsState: StateFlow<Resource<com.example.linkedout.data.model.SeekerApplicationsResponse>?> = _applicationsState.asStateFlow()

    fun loadProfile() {
        viewModelScope.launch {
            repository.getCurrentUser().collect { resource ->
                _profileState.value = resource
                // Fetch signed URL for profile image if it exists (for seeker)
                if (resource is Resource.Success && resource.data?.profile?.profileImageS3Url != null) {
                    fetchSignedUrlForImage(resource.data.profile.profileImageS3Url)
                }
                // Fetch signed URL for company logo if it exists (for recruiter)
                if (resource is Resource.Success && resource.data?.profile?.companyLogoS3Url != null) {
                    fetchSignedUrlForImage(resource.data.profile.companyLogoS3Url)
                }
                // Extract resume file name if it exists
                if (resource is Resource.Success && resource.data?.profile?.resumeS3Url != null) {
                    extractResumeFileName(resource.data.profile.resumeS3Url)
                }
            }
        }
    }

    private fun fetchSignedUrlForImage(s3Url: String) {
        viewModelScope.launch {
            repository.getSignedUrl(s3Url).collect { resource ->
                if (resource is Resource.Success) {
                    _profileImageSignedUrl.value = resource.data
                }
            }
        }
    }

    private fun extractResumeFileName(s3Url: String) {
        // Extract file name from S3 URL
        // Example: https://bucket.s3.amazonaws.com/resumes/1/1731936000000-file.pdf
        val fileName = s3Url.substringAfterLast("/")
            .substringAfter("-") // Remove timestamp prefix if present
            .ifEmpty { "Resume.pdf" }
        _resumeFileName.value = fileName
    }

    fun uploadResume(file: File) {
        viewModelScope.launch {
            repository.uploadResume(file).collect { resource ->
                _uploadResumeState.value = resource
                // Reload profile after successful upload
                if (resource is Resource.Success) {
                    // Store the resume file name
                    _resumeFileName.value = file.name
                    loadProfile()
                }
            }
        }
    }

    fun uploadProfileImage(file: File) {
        viewModelScope.launch {
            repository.uploadProfileImage(file).collect { resource ->
                _uploadImageState.value = resource
                // Reload profile and fetch signed URL after successful upload
                if (resource is Resource.Success && resource.data != null) {
                    // Fetch signed URL for the newly uploaded image
                    fetchSignedUrlForImage(resource.data)
                    loadProfile()
                }
            }
        }
    }

    fun resetUploadStates() {
        _uploadResumeState.value = null
        _uploadImageState.value = null
    }

    fun loadApplications(status: String? = null) {
        viewModelScope.launch {
            repository.getSeekerApplications(status).collect { resource ->
                _applicationsState.value = resource
            }
        }
    }
}

