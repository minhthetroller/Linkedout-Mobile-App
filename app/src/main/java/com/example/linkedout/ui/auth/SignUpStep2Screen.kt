package com.example.linkedout.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.linkedout.util.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpStep2Screen(
    userType: String,
    onNavigateToStep3: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    // Common fields
    var phone by remember { mutableStateOf("") }

    // Seeker fields
    var currentJob by remember { mutableStateOf("") }
    var yearsExperience by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    // Recruiter fields
    var companyName by remember { mutableStateOf("") }
    var companySize by remember { mutableStateOf("") }
    var companyWebsite by remember { mutableStateOf("") }

    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val profileCompletionState by viewModel.profileCompletionState.collectAsState()

    LaunchedEffect(profileCompletionState) {
        when (profileCompletionState) {
            is Resource.Success -> {
                viewModel.resetProfileCompletionState()
                onNavigateToStep3()
            }
            is Resource.Error -> {
                showError = true
                errorMessage = (profileCompletionState as Resource.Error).message ?: "Update failed"
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sign Up - Step 2") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = if (userType == "seeker") "Personal Information" else "Company Information",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (userType == "seeker") {
                OutlinedTextField(
                    value = currentJob,
                    onValueChange = { currentJob = it },
                    label = { Text("Current Job (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = yearsExperience,
                    onValueChange = { yearsExperience = it },
                    label = { Text("Years of Experience (Optional)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            } else {
                OutlinedTextField(
                    value = companyName,
                    onValueChange = { companyName = it },
                    label = { Text("Company Name *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = companySize,
                    onValueChange = { companySize = it },
                    label = { Text("Company Size (Optional)") },
                    placeholder = { Text("e.g., 100-500") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = companyWebsite,
                    onValueChange = { companyWebsite = it },
                    label = { Text("Company Website (Optional)") },
                    placeholder = { Text("https://example.com") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone Number (Optional)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            if (showError) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (userType == "seeker") {
                        showError = false
                        viewModel.signUpStep2Seeker(
                            currentJob.ifBlank { null },
                            yearsExperience.toIntOrNull(),
                            location.ifBlank { null },
                            phone.ifBlank { null }
                        )
                    } else {
                        if (companyName.isNotBlank()) {
                            showError = false
                            viewModel.signUpStep2Recruiter(
                                companyName,
                                companySize.ifBlank { null },
                                companyWebsite.ifBlank { null },
                                phone.ifBlank { null }
                            )
                        } else {
                            showError = true
                            errorMessage = "Company name is required"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = profileCompletionState !is Resource.Loading
            ) {
                if (profileCompletionState is Resource.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Next")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpStep2SeekerPreview() {
    MaterialTheme {
        SignUpStep2Content(
            userType = "seeker",
            phone = "+1234567890",
            currentJob = "Software Engineer",
            yearsExperience = "5",
            location = "San Francisco, CA",
            companyName = "",
            companySize = "",
            companyWebsite = "",
            showError = false,
            errorMessage = "",
            isLoading = false,
            onPhoneChange = {},
            onCurrentJobChange = {},
            onYearsExperienceChange = {},
            onLocationChange = {},
            onCompanyNameChange = {},
            onCompanySizeChange = {},
            onCompanyWebsiteChange = {},
            onNextClick = {},
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpStep2RecruiterPreview() {
    MaterialTheme {
        SignUpStep2Content(
            userType = "recruiter",
            phone = "+1234567890",
            currentJob = "",
            yearsExperience = "",
            location = "",
            companyName = "Tech Corp Inc.",
            companySize = "100-500",
            companyWebsite = "https://techcorp.com",
            showError = false,
            errorMessage = "",
            isLoading = false,
            onPhoneChange = {},
            onCurrentJobChange = {},
            onYearsExperienceChange = {},
            onLocationChange = {},
            onCompanyNameChange = {},
            onCompanySizeChange = {},
            onCompanyWebsiteChange = {},
            onNextClick = {},
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpStep2RecruiterErrorPreview() {
    MaterialTheme {
        SignUpStep2Content(
            userType = "recruiter",
            phone = "",
            currentJob = "",
            yearsExperience = "",
            location = "",
            companyName = "",
            companySize = "",
            companyWebsite = "",
            showError = true,
            errorMessage = "Company name is required",
            isLoading = false,
            onPhoneChange = {},
            onCurrentJobChange = {},
            onYearsExperienceChange = {},
            onLocationChange = {},
            onCompanyNameChange = {},
            onCompanySizeChange = {},
            onCompanyWebsiteChange = {},
            onNextClick = {},
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpStep2LoadingPreview() {
    MaterialTheme {
        SignUpStep2Content(
            userType = "seeker",
            phone = "+1234567890",
            currentJob = "Software Engineer",
            yearsExperience = "5",
            location = "San Francisco, CA",
            companyName = "",
            companySize = "",
            companyWebsite = "",
            showError = false,
            errorMessage = "",
            isLoading = true,
            onPhoneChange = {},
            onCurrentJobChange = {},
            onYearsExperienceChange = {},
            onLocationChange = {},
            onCompanyNameChange = {},
            onCompanySizeChange = {},
            onCompanyWebsiteChange = {},
            onNextClick = {},
            onNavigateBack = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SignUpStep2Content(
    userType: String,
    phone: String,
    currentJob: String,
    yearsExperience: String,
    location: String,
    companyName: String,
    companySize: String,
    companyWebsite: String,
    showError: Boolean,
    errorMessage: String,
    isLoading: Boolean,
    onPhoneChange: (String) -> Unit,
    onCurrentJobChange: (String) -> Unit,
    onYearsExperienceChange: (String) -> Unit,
    onLocationChange: (String) -> Unit,
    onCompanyNameChange: (String) -> Unit,
    onCompanySizeChange: (String) -> Unit,
    onCompanyWebsiteChange: (String) -> Unit,
    onNextClick: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sign Up - Step 2") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = if (userType == "seeker") "Personal Information" else "Company Information",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (userType == "seeker") {
                OutlinedTextField(
                    value = currentJob,
                    onValueChange = onCurrentJobChange,
                    label = { Text("Current Job (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = yearsExperience,
                    onValueChange = onYearsExperienceChange,
                    label = { Text("Years of Experience (Optional)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = location,
                    onValueChange = onLocationChange,
                    label = { Text("Location (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            } else {
                OutlinedTextField(
                    value = companyName,
                    onValueChange = onCompanyNameChange,
                    label = { Text("Company Name *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = companySize,
                    onValueChange = onCompanySizeChange,
                    label = { Text("Company Size (Optional)") },
                    placeholder = { Text("e.g., 100-500") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = companyWebsite,
                    onValueChange = onCompanyWebsiteChange,
                    label = { Text("Company Website (Optional)") },
                    placeholder = { Text("https://example.com") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = onPhoneChange,
                label = { Text("Phone Number *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            if (showError) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onNextClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Next")
                }
            }
        }
    }
}

