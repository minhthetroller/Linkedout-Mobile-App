package com.example.linkedout.ui.jobs.seeker

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.linkedout.ui.jobs.JobViewModel
import com.example.linkedout.util.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobDetailsScreen(
    jobId: Int,
    onNavigateBack: () -> Unit,
    viewModel: JobViewModel = hiltViewModel()
) {
    val jobDetailState by viewModel.jobDetailState.collectAsState()
    val applyJobState by viewModel.applyJobState.collectAsState()
    var showCoverLetterDialog by remember { mutableStateOf(false) }
    var coverLetterText by remember { mutableStateOf("") }
    var showApplyDialog by remember { mutableStateOf(false) }
    var applyDialogTitle by remember { mutableStateOf("") }
    var applyDialogMessage by remember { mutableStateOf("") }
    var isApplySuccess by remember { mutableStateOf(false) }
    var hasApplied by remember { mutableStateOf(false) }

    LaunchedEffect(jobId) {
        viewModel.getJobDetails(jobId)
        hasApplied = false // Reset when viewing a different job
    }

    LaunchedEffect(applyJobState) {
        when (applyJobState) {
            is Resource.Success -> {
                applyDialogTitle = "Success"
                applyDialogMessage = "Your application has been submitted successfully!"
                isApplySuccess = true
                hasApplied = true // Mark as applied
                showApplyDialog = true
                viewModel.resetApplyJobState()
            }
            is Resource.Error -> {
                applyDialogTitle = "Application Failed"
                applyDialogMessage = (applyJobState as Resource.Error).message ?: "Failed to apply to job"
                isApplySuccess = false
                showApplyDialog = true
                viewModel.resetApplyJobState()
            }
            else -> {}
        }
    }

    // Cover Letter Dialog
    if (showCoverLetterDialog) {
        AlertDialog(
            onDismissRequest = {
                showCoverLetterDialog = false
                coverLetterText = ""
            },
            title = { Text("Apply to Job") },
            text = {
                Column {
                    Text(
                        text = "Please write a cover letter to introduce yourself and explain why you're a good fit for this position.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    OutlinedTextField(
                        value = coverLetterText,
                        onValueChange = { coverLetterText = it },
                        label = { Text("Cover Letter") },
                        placeholder = { Text("I am very interested in this position and believe my skills align perfectly with your requirements...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        maxLines = 10,
                        supportingText = {
                            Text("${coverLetterText.length} characters")
                        }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (coverLetterText.isNotBlank()) {
                            viewModel.applyToJob(jobId, coverLetterText)
                            showCoverLetterDialog = false
                            coverLetterText = ""
                        }
                    },
                    enabled = coverLetterText.isNotBlank()
                ) {
                    Text("Submit Application")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showCoverLetterDialog = false
                    coverLetterText = ""
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Result Dialog
    if (showApplyDialog) {
        AlertDialog(
            onDismissRequest = { showApplyDialog = false },
            title = { Text(applyDialogTitle) },
            text = { Text(applyDialogMessage) },
            confirmButton = {
                Button(onClick = { showApplyDialog = false }) {
                    Text("OK")
                }
            },
            icon = if (isApplySuccess) {
                { Icon(Icons.Default.Info, contentDescription = null) }
            } else null
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Job Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        when (val state = jobDetailState) {
            is Resource.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is Resource.Success -> {
                val job = state.data
                if (job != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        Text(
                            text = job.title,
                            style = MaterialTheme.typography.headlineMedium
                        )

                        if (job.companyName != null) {
                            Text(
                                text = job.companyName,
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Location
                        if (job.location != null) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Place,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = job.location, style = MaterialTheme.typography.bodyLarge)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // Salary
                        if (job.salaryMin != null || job.salaryMax != null) {
                            val salaryText = when {
                                job.salaryMin != null && job.salaryMax != null ->
                                    "$${job.salaryMin.toInt()} - $${job.salaryMax.toInt()}"
                                job.salaryMin != null -> "From $${job.salaryMin.toInt()}"
                                else -> "Up to $${job.salaryMax?.toInt()}"
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = salaryText, style = MaterialTheme.typography.bodyLarge)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // Employment Type
                        if (job.employmentType != null) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Work,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = job.employmentType, style = MaterialTheme.typography.bodyLarge)
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Tags
                        if (!job.tags.isNullOrEmpty()) {
                            Text(
                                text = "Skills & Tags",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                job.tags.take(5).forEach { tag ->
                                    AssistChip(
                                        onClick = {},
                                        label = { Text(tag.name) }
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(16.dp))

                        // About
                        if (!job.about.isNullOrBlank()) {
                            Text(
                                text = "About",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = job.about,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Description
                        Text(
                            text = "Job Description",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = job.description,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        // Benefits
                        if (!job.benefits.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Benefits",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = job.benefits,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Company Info
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Company Information",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                if (job.companySize != null) {
                                    Text("Size: ${job.companySize}")
                                }
                                if (job.companyWebsite != null) {
                                    Text("Website: ${job.companyWebsite}")
                                }
                                if (job.recruiterEmail != null) {
                                    Text("Contact: ${job.recruiterEmail}")
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Apply Button
                        Button(
                            onClick = { showCoverLetterDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            enabled = !hasApplied && applyJobState !is Resource.Loading
                        ) {
                            if (applyJobState is Resource.Loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Text(
                                    text = if (hasApplied) "Applied" else "Apply to this Job",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
            is Resource.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = state.message ?: "Error loading job details",
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(
                            onClick = { viewModel.getJobDetails(jobId) },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }
            null -> {}
        }
    }
}

