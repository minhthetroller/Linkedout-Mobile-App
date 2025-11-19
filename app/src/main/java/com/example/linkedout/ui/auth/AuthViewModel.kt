package com.example.linkedout.ui.auth

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
class AuthViewModel @Inject constructor(
    private val repository: LinkedOutRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<Resource<AuthResponse>?>(null)
    val authState: StateFlow<Resource<AuthResponse>?> = _authState.asStateFlow()

    private val _profileCompletionState = MutableStateFlow<Resource<ProfileCompletionData>?>(null)
    val profileCompletionState: StateFlow<Resource<ProfileCompletionData>?> = _profileCompletionState.asStateFlow()

    val userType: StateFlow<String?> = repository.getUserType()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val profileCompletionStep: StateFlow<Int?> = repository.getProfileCompletionStep()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun signUpStep1(
        email: String,
        password: String,
        userType: String,
        fullName: String,
        birthDate: String
    ) {
        viewModelScope.launch {
            repository.signUpStep1(
                SignUpStep1Request(email, password, userType, fullName, birthDate)
            ).collect { result ->
                _authState.value = result
            }
        }
    }

    fun signUpStep2Seeker(
        currentJob: String?,
        yearsExperience: Int?,
        location: String?,
        phone: String?
    ) {
        viewModelScope.launch {
            repository.signUpStep2Seeker(
                SignUpStep2SeekerRequest(currentJob, yearsExperience, location, phone)
            ).collect { result ->
                _profileCompletionState.value = result
            }
        }
    }

    fun signUpStep2Recruiter(
        companyName: String,
        companySize: String?,
        companyWebsite: String?,
        phone: String?
    ) {
        viewModelScope.launch {
            repository.signUpStep2Recruiter(
                SignUpStep2RecruiterRequest(companyName, companySize, companyWebsite, phone)
            ).collect { result ->
                _profileCompletionState.value = result
            }
        }
    }

    fun signUpStep3(
        preferredJobTitles: List<String>?,
        preferredIndustries: List<String>?,
        preferredLocations: List<String>?,
        salaryMin: Double?,
        salaryMax: Double?,
        skip: Boolean = false
    ) {
        viewModelScope.launch {
            repository.signUpStep3(
                SignUpStep3Request(
                    preferredJobTitles,
                    preferredIndustries,
                    preferredLocations,
                    salaryMin,
                    salaryMax,
                    if (skip) true else null
                )
            ).collect { result ->
                _profileCompletionState.value = result
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            repository.login(LoginRequest(email, password)).collect { result ->
                _authState.value = result
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun resetAuthState() {
        _authState.value = null
    }

    fun resetProfileCompletionState() {
        _profileCompletionState.value = null
    }
}

