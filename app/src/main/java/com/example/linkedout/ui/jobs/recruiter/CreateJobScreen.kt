package com.example.linkedout.ui.jobs.recruiter

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.linkedout.data.model.CreateJobRequest
import com.example.linkedout.data.model.UpdateJobRequest
import com.example.linkedout.ui.jobs.JobViewModel
import com.example.linkedout.util.Resource
import java.text.NumberFormat
import java.util.Locale

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
    var employmentTypeExpanded by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val employmentTypes = listOf("full-time", "part-time", "contract", "freelance", "internship")

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
                singleLine = true,
                isError = showError && title.isBlank()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = about,
                onValueChange = { about = it },
                label = { Text("About *") },
                placeholder = { Text("Brief overview of the role") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                isError = showError && about.isBlank()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Job Description *") },
                placeholder = { Text("Detailed job requirements and responsibilities") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 5,
                isError = showError && description.isBlank()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = salaryMin,
                    onValueChange = { salaryMin = it.filter { char -> char.isDigit() } },
                    label = { Text("Min Salary (VND) *") },
                    placeholder = { Text("e.g., 10000000") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    isError = showError && salaryMin.isBlank(),
                    supportingText = {
                        if (salaryMin.isNotBlank()) {
                            Text("₫${NumberFormat.getNumberInstance(Locale.US).format(salaryMin.toLongOrNull() ?: 0)}")
                        }
                    }
                )

                OutlinedTextField(
                    value = salaryMax,
                    onValueChange = { salaryMax = it.filter { char -> char.isDigit() } },
                    label = { Text("Max Salary (VND) *") },
                    placeholder = { Text("e.g., 20000000") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    isError = showError && salaryMax.isBlank(),
                    supportingText = {
                        if (salaryMax.isNotBlank()) {
                            Text("₫${NumberFormat.getNumberInstance(Locale.US).format(salaryMax.toLongOrNull() ?: 0)}")
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location *") },
                placeholder = { Text("e.g., Ho Chi Minh City, Vietnam or Remote") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = showError && location.isBlank()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Employment Type Dropdown
            ExposedDropdownMenuBox(
                expanded = employmentTypeExpanded,
                onExpandedChange = { employmentTypeExpanded = it }
            ) {
                OutlinedTextField(
                    value = employmentType.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Employment Type *") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = employmentTypeExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                    isError = showError && employmentType.isBlank(),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = employmentTypeExpanded,
                    onDismissRequest = { employmentTypeExpanded = false }
                ) {
                    employmentTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }) },
                            onClick = {
                                employmentType = type
                                employmentTypeExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = benefits,
                onValueChange = { benefits = it },
                label = { Text("Benefits *") },
                placeholder = { Text("Health insurance, annual leave, etc.") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                isError = showError && benefits.isBlank()
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
                    // Validate all required fields
                    val missingFields = mutableListOf<String>()
                    if (title.isBlank()) missingFields.add("Job Title")
                    if (about.isBlank()) missingFields.add("About")
                    if (description.isBlank()) missingFields.add("Job Description")
                    if (salaryMin.isBlank()) missingFields.add("Min Salary")
                    if (salaryMax.isBlank()) missingFields.add("Max Salary")
                    if (location.isBlank()) missingFields.add("Location")
                    if (employmentType.isBlank()) missingFields.add("Employment Type")
                    if (benefits.isBlank()) missingFields.add("Benefits")

                    if (missingFields.isEmpty()) {
                        // Validate salary range
                        val minSalary = salaryMin.toDoubleOrNull()
                        val maxSalary = salaryMax.toDoubleOrNull()

                        if (minSalary != null && maxSalary != null && minSalary > maxSalary) {
                            showError = true
                            errorMessage = "Minimum salary cannot be greater than maximum salary"
                            return@Button
                        }

                        showError = false
                        if (jobId == null) {
                            viewModel.createJob(
                                CreateJobRequest(
                                    title = title,
                                    about = about,
                                    description = description,
                                    salaryMin = salaryMin.toDoubleOrNull(),
                                    salaryMax = salaryMax.toDoubleOrNull(),
                                    benefits = benefits,
                                    location = location,
                                    employmentType = employmentType
                                )
                            )
                        } else {
                            viewModel.updateJob(
                                jobId,
                                UpdateJobRequest(
                                    title = title,
                                    about = about,
                                    description = description,
                                    salaryMin = salaryMin.toDoubleOrNull(),
                                    salaryMax = salaryMax.toDoubleOrNull(),
                                    benefits = benefits,
                                    location = location,
                                    employmentType = employmentType
                                )
                            )
                        }
                    } else {
                        showError = true
                        errorMessage = "Please fill in all required fields: ${missingFields.joinToString(", ")}"
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
