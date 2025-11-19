package com.example.linkedout.ui.jobs.recruiter

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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.linkedout.data.model.CreateJobRequest
import com.example.linkedout.data.model.UpdateJobRequest
import com.example.linkedout.ui.jobs.JobViewModel
import com.example.linkedout.util.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateJobScreen(
    jobId: Int? = null,
    onNavigateBack: () -> Unit,
    viewModel: JobViewModel = hiltViewModel()
) {
    var title by remember { mutableStateOf("") }
    var about by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var salaryMin by remember { mutableStateOf("") }
    var salaryMax by remember { mutableStateOf("") }
    var benefits by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var employmentType by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val createJobState by viewModel.createJobState.collectAsState()
    val jobDetailState by viewModel.jobDetailState.collectAsState()

    LaunchedEffect(jobId) {
        if (jobId != null) {
            viewModel.getJobDetails(jobId)
        }
    }

    LaunchedEffect(jobDetailState) {
        val state = jobDetailState
        if (state is Resource.Success) {
            val job = state.data
            if (job != null) {
                title = job.title
                about = job.about ?: ""
                description = job.description
                salaryMin = job.salaryMin?.toString() ?: ""
                salaryMax = job.salaryMax?.toString() ?: ""
                benefits = job.benefits ?: ""
                location = job.location ?: ""
                employmentType = job.employmentType ?: ""
            }
        }
    }

    LaunchedEffect(createJobState) {
        when (createJobState) {
            is Resource.Success -> {
                viewModel.resetCreateJobState()
                onNavigateBack()
            }
            is Resource.Error -> {
                showError = true
                errorMessage = (createJobState as Resource.Error).message ?: "Failed to create job"
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (jobId == null) "Create Job Posting" else "Edit Job Posting") },
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Job Title *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = about,
                onValueChange = { about = it },
                label = { Text("About (Optional)") },
                placeholder = { Text("Brief overview of the role") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Job Description *") },
                placeholder = { Text("Detailed job requirements and responsibilities") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 5
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = salaryMin,
                    onValueChange = { salaryMin = it },
                    label = { Text("Min Salary") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                OutlinedTextField(
                    value = salaryMax,
                    onValueChange = { salaryMax = it },
                    label = { Text("Max Salary") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location (Optional)") },
                placeholder = { Text("e.g., San Francisco, CA or Remote") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = employmentType,
                onValueChange = { employmentType = it },
                label = { Text("Employment Type (Optional)") },
                placeholder = { Text("e.g., Full-time, Part-time, Contract") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = benefits,
                onValueChange = { benefits = it },
                label = { Text("Benefits (Optional)") },
                placeholder = { Text("Health insurance, 401k, etc.") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
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
                    if (title.isNotBlank() && description.isNotBlank()) {
                        showError = false
                        if (jobId == null) {
                            viewModel.createJob(
                                CreateJobRequest(
                                    title = title,
                                    about = about.ifBlank { null },
                                    description = description,
                                    salaryMin = salaryMin.toDoubleOrNull(),
                                    salaryMax = salaryMax.toDoubleOrNull(),
                                    benefits = benefits.ifBlank { null },
                                    location = location.ifBlank { null },
                                    employmentType = employmentType.ifBlank { null }
                                )
                            )
                        } else {
                            viewModel.updateJob(
                                jobId,
                                UpdateJobRequest(
                                    title = title,
                                    about = about.ifBlank { null },
                                    description = description,
                                    salaryMin = salaryMin.toDoubleOrNull(),
                                    salaryMax = salaryMax.toDoubleOrNull(),
                                    benefits = benefits.ifBlank { null },
                                    location = location.ifBlank { null },
                                    employmentType = employmentType.ifBlank { null }
                                )
                            )
                        }
                    } else {
                        showError = true
                        errorMessage = "Title and description are required"
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = createJobState !is Resource.Loading
            ) {
                if (createJobState is Resource.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(if (jobId == null) "Create Job" else "Update Job")
                }
            }
        }
    }
}
