package com.example.linkedout.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.linkedout.util.Resource

@Composable
fun SignUpStep3Screen(
    userType: String,
    onComplete: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var preferredJobTitles by remember { mutableStateOf("") }
    var preferredIndustries by remember { mutableStateOf("") }
    var preferredLocations by remember { mutableStateOf("") }
    var salaryMin by remember { mutableStateOf("") }
    var salaryMax by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val profileCompletionState by viewModel.profileCompletionState.collectAsState()

    LaunchedEffect(profileCompletionState) {
        when (profileCompletionState) {
            is Resource.Success -> {
                viewModel.resetProfileCompletionState()
                onComplete()
            }
            is Resource.Error -> {
                showError = true
                errorMessage = (profileCompletionState as Resource.Error).message ?: "Update failed"
            }
            else -> {}
        }
    }

    SignUpStep3ScreenContent(
        userType = userType,
        preferredJobTitles = preferredJobTitles,
        preferredIndustries = preferredIndustries,
        preferredLocations = preferredLocations,
        salaryMin = salaryMin,
        salaryMax = salaryMax,
        showError = showError,
        errorMessage = errorMessage,
        isLoading = profileCompletionState is Resource.Loading,
        onPreferredJobTitlesChange = { preferredJobTitles = it },
        onPreferredIndustriesChange = { preferredIndustries = it },
        onPreferredLocationsChange = { preferredLocations = it },
        onSalaryMinChange = { salaryMin = it },
        onSalaryMaxChange = { salaryMax = it },
        onFinishClick = {
            showError = false
            if (userType == "seeker") {
                val jobTitlesList = preferredJobTitles.split(",").map { it.trim() }.filter { it.isNotBlank() }
                val industriesList = preferredIndustries.split(",").map { it.trim() }.filter { it.isNotBlank() }
                val locationsList = preferredLocations.split(",").map { it.trim() }.filter { it.isNotBlank() }

                viewModel.signUpStep3(
                    if (jobTitlesList.isNotEmpty()) jobTitlesList else null,
                    if (industriesList.isNotEmpty()) industriesList else null,
                    if (locationsList.isNotEmpty()) locationsList else null,
                    salaryMin.toDoubleOrNull(),
                    salaryMax.toDoubleOrNull(),
                    false
                )
            } else {
                viewModel.signUpStep3(null, null, null, null, null, false)
            }
        },
        onSkipClick = {
            showError = false
            viewModel.signUpStep3(null, null, null, null, null, true)
        }
    )
}

@Preview(showBackground = true)
@Composable
fun SignUpStep3SeekerPreview() {
    MaterialTheme {
        SignUpStep3Screen(
            userType = "seeker",
            onComplete = {},
            viewModel = hiltViewModel()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpStep3SeekerFilledPreview() {
    MaterialTheme {
        SignUpStep3ScreenContent(
            userType = "seeker",
            preferredJobTitles = "Software Engineer, Developer",
            preferredIndustries = "Technology, Finance",
            preferredLocations = "San Francisco, Remote",
            salaryMin = "80000",
            salaryMax = "150000",
            showError = false,
            errorMessage = "",
            isLoading = false,
            onPreferredJobTitlesChange = {},
            onPreferredIndustriesChange = {},
            onPreferredLocationsChange = {},
            onSalaryMinChange = {},
            onSalaryMaxChange = {},
            onFinishClick = {},
            onSkipClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpStep3RecruiterPreview() {
    MaterialTheme {
        SignUpStep3Screen(
            userType = "recruiter",
            onComplete = {},
            viewModel = hiltViewModel()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpStep3LoadingPreview() {
    MaterialTheme {
        SignUpStep3ScreenContent(
            userType = "seeker",
            preferredJobTitles = "Software Engineer",
            preferredIndustries = "Technology",
            preferredLocations = "Remote",
            salaryMin = "100000",
            salaryMax = "150000",
            showError = false,
            errorMessage = "",
            isLoading = true,
            onPreferredJobTitlesChange = {},
            onPreferredIndustriesChange = {},
            onPreferredLocationsChange = {},
            onSalaryMinChange = {},
            onSalaryMaxChange = {},
            onFinishClick = {},
            onSkipClick = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SignUpStep3ScreenContent(
    userType: String,
    preferredJobTitles: String,
    preferredIndustries: String,
    preferredLocations: String,
    salaryMin: String,
    salaryMax: String,
    showError: Boolean,
    errorMessage: String,
    isLoading: Boolean,
    onPreferredJobTitlesChange: (String) -> Unit,
    onPreferredIndustriesChange: (String) -> Unit,
    onPreferredLocationsChange: (String) -> Unit,
    onSalaryMinChange: (String) -> Unit,
    onSalaryMaxChange: (String) -> Unit,
    onFinishClick: () -> Unit,
    onSkipClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sign Up - Step 3") }
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
                text = if (userType == "seeker") "Job Preferences (Optional)" else "Complete Setup",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (userType == "seeker") {
                Text(
                    text = "Help us find the perfect jobs for you",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = preferredJobTitles,
                    onValueChange = onPreferredJobTitlesChange,
                    label = { Text("Preferred Job Titles") },
                    placeholder = { Text("Software Engineer, Developer (comma-separated)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = preferredIndustries,
                    onValueChange = onPreferredIndustriesChange,
                    label = { Text("Preferred Industries") },
                    placeholder = { Text("Technology, Finance (comma-separated)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = preferredLocations,
                    onValueChange = onPreferredLocationsChange,
                    label = { Text("Preferred Locations") },
                    placeholder = { Text("San Francisco, Remote (comma-separated)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = salaryMin,
                        onValueChange = onSalaryMinChange,
                        label = { Text("Min Salary") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = salaryMax,
                        onValueChange = onSalaryMaxChange,
                        label = { Text("Max Salary") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
            } else {
                Text(
                    text = "You're all set! Click finish to start posting jobs.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            if (showError) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onFinishClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Finish")
                }
            }

            if (userType == "seeker") {
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onSkipClick,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    Text("Skip for Now")
                }
            }
        }
    }
}

