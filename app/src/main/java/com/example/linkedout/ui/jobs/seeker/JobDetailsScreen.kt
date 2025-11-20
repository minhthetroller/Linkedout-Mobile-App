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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.linkedout.data.model.Job
import com.example.linkedout.data.model.Tag
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
    }

    LaunchedEffect(jobDetailState) {
        (jobDetailState as? Resource.Success)?.data?.let { job ->
            hasApplied = job.hasApplied ?: false
        }
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
                            FlowRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy((-8).dp)
                            ) {
                                job.tags.take(3).forEach { tag ->
                                    AssistChip(
                                        onClick = {},
                                        label = { Text(tag.name) }
                                    )
                                }
                            }
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
                                .padding(vertical = 16.dp),
                            enabled = !hasApplied // Disable if already applied
                        ) {
                            Text(if (hasApplied) "Applied" else "Apply Now")
                        }
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

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "Job Details - Full Info")
@Composable
fun JobDetailsScreenPreview() {
    MaterialTheme {
        val dummyJob = Job(
            id = 1,
            recruiterId = 1,
            title = "Senior Full Stack Developer",
            about = "Join our innovative team building next-generation solutions for the healthcare industry.",
            description = "We are seeking an experienced Full Stack Developer with expertise in React, Node.js, and PostgreSQL. You will work on cutting-edge web applications and microservices architecture. Strong problem-solving skills and experience with AWS are required.\n\nResponsibilities:\n• Design and develop scalable web applications\n• Collaborate with cross-functional teams\n• Write clean, maintainable code\n• Participate in code reviews",
            salaryMin = 15000000.0,
            salaryMax = 25000000.0,
            benefits = "• Health insurance and dental coverage\n• 15 days annual leave\n• Professional development budget\n• Flexible working hours\n• Modern office equipment",
            location = "Tây Hồ, Hà Nội",
            employmentType = "full-time",
            status = "active",
            createdAt = "2025-11-18T12:00:00.000Z",
            updatedAt = "2025-11-18T12:00:00.000Z",
            tags = listOf(
                Tag(1, "React", "Skills"),
                Tag(2, "Node.js", "Skills"),
                Tag(3, "Full Stack Developer", "Job Roles")
            ),
            recruiterEmail = "recruiter@techcorp.com",
            companyName = "Tech Corp Inc.",
            companySize = "100-500",
            companyWebsite = "https://techcorp.com",
            hasApplied = false
        )

        Surface(color = MaterialTheme.colorScheme.background) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Job Details") },
                        navigationIcon = {
                            IconButton(onClick = {}) {
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
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    Text(
                        text = dummyJob.title,
                        style = MaterialTheme.typography.headlineMedium
                    )

                    if (dummyJob.companyName != null) {
                        Text(
                            text = dummyJob.companyName,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Location
                    if (dummyJob.location != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = dummyJob.location, style = MaterialTheme.typography.bodyLarge)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Salary
                    if (dummyJob.salaryMin != null || dummyJob.salaryMax != null) {
                        val salaryText = when {
                            dummyJob.salaryMin != null && dummyJob.salaryMax != null ->
                                "₫${dummyJob.salaryMin.toInt()} - ₫${dummyJob.salaryMax.toInt()}"
                            dummyJob.salaryMin != null -> "From ₫${dummyJob.salaryMin.toInt()}"
                            else -> "Up to ₫${dummyJob.salaryMax?.toInt()}"
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
                    if (dummyJob.employmentType != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Work,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = dummyJob.employmentType, style = MaterialTheme.typography.bodyLarge)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Tags
                    if (!dummyJob.tags.isNullOrEmpty()) {
                        FlowRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy((-8).dp)
                        ) {
                            dummyJob.tags.take(3).forEach { tag ->
                                AssistChip(
                                    onClick = {},
                                    label = { Text(tag.name) }
                                )
                            }
                        }
                    }

                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(16.dp))

                    // About
                    if (!dummyJob.about.isNullOrBlank()) {
                        Text(
                            text = "About",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = dummyJob.about,
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
                        text = dummyJob.description,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    // Benefits
                    if (!dummyJob.benefits.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Benefits",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = dummyJob.benefits,
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
                            if (dummyJob.companySize != null) {
                                Text("Size: ${dummyJob.companySize}")
                            }
                            if (dummyJob.companyWebsite != null) {
                                Text("Website: ${dummyJob.companyWebsite}")
                            }
                            if (dummyJob.recruiterEmail != null) {
                                Text("Contact: ${dummyJob.recruiterEmail}")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Apply Button
                    Button(
                        onClick = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    ) {
                        Text("Apply Now")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "Job Details - Already Applied")
@Composable
fun JobDetailsScreenAppliedPreview() {
    MaterialTheme {
        val dummyJob = Job(
            id = 2,
            recruiterId = 2,
            title = "Youth Coaching & Support Specialist",
            about = "Make a difference in young people's lives through mentoring and coaching.",
            description = "We're looking for a passionate individual to provide coaching and support to young people. This role involves one-on-one mentoring, group workshops, and community engagement.\n\nKey Requirements:\n• Experience working with youth\n• Strong communication skills\n• Empathy and patience\n• Problem-solving abilities",
            salaryMin = 8000000.0,
            salaryMax = 12000000.0,
            benefits = "• Comprehensive training\n• Career development opportunities\n• Team bonding activities\n• Health insurance",
            location = "Tây Hồ, Hà Nội",
            employmentType = "part-time",
            status = "active",
            createdAt = "2025-11-15T10:00:00.000Z",
            updatedAt = "2025-11-15T10:00:00.000Z",
            tags = listOf(
                Tag(4, "Youth Coaching", "Skills"),
                Tag(5, "Technical Instruction", "Skills"),
                Tag(6, "Coaching Support", "Skills")
            ),
            recruiterEmail = "hr@youthorg.com",
            companyName = "Youth Development Organization",
            companySize = "50-100",
            companyWebsite = "https://youthorg.com",
            hasApplied = true
        )

        Surface(color = MaterialTheme.colorScheme.background) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Job Details") },
                        navigationIcon = {
                            IconButton(onClick = {}) {
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
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    Text(
                        text = dummyJob.title,
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Text(
                        text = dummyJob.companyName ?: "",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = dummyJob.location ?: "", style = MaterialTheme.typography.bodyLarge)
                    }

                    // Tags with wrapping
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        dummyJob.tags?.forEach { tag ->
                            AssistChip(
                                onClick = {},
                                label = { Text(tag.name) }
                            )
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                    Text(
                        text = "About",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = dummyJob.about ?: "",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Apply Button - Disabled
                    Button(
                        onClick = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        enabled = false
                    ) {
                        Text("Applied")
                    }
                }
            }
        }
    }
}

