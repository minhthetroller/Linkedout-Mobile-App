package com.example.linkedout.ui.jobs.recruiter

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.linkedout.data.model.Job
import com.example.linkedout.ui.jobs.JobViewModel
import com.example.linkedout.util.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecruiterHomeScreen(
    onNavigateToCreateJob: () -> Unit,
    onNavigateToEditJob: (Int) -> Unit,
    onNavigateToApplicants: (Int) -> Unit,
    onNavigateToProfile: () -> Unit,
    onLogout: () -> Unit,
    viewModel: JobViewModel = hiltViewModel()
) {
    val jobsState by viewModel.jobsState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf<Job?>(null) }

    LaunchedEffect(Unit) {
        viewModel.getRecruiterJobs()
    }

    if (showDeleteDialog != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Delete Job") },
            text = { Text("Are you sure you want to delete '${showDeleteDialog?.title}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog?.let { viewModel.deleteJob(it.id) }
                        showDeleteDialog = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Job Postings") },
                actions = {
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.AccountCircle, "Profile")
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, "Logout")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreateJob) {
                Icon(Icons.Default.Add, "Create Job")
            }
        }
    ) { paddingValues ->
        when (val state = jobsState) {
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
                val jobs = state.data ?: emptyList()
                if (jobs.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No jobs posted yet",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = "Create your first job posting",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(jobs) { job ->
                            RecruiterJobCard(
                                job = job,
                                onEdit = { onNavigateToEditJob(job.id) },
                                onDelete = { showDeleteDialog = job },
                                onViewApplicants = { onNavigateToApplicants(job.id) }
                            )
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
                            text = state.message ?: "Error loading jobs",
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(
                            onClick = { viewModel.getRecruiterJobs() },
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

@Composable
fun RecruiterJobCard(
    job: Job,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onViewApplicants: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = job.title,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "Status: ${job.status}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (job.status == "active")
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, "Edit")
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Default.Delete,
                            "Delete",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            if (job.location != null) {
                Text(
                    text = job.location,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (job.salaryMin != null || job.salaryMax != null) {
                val salaryText = when {
                    job.salaryMin != null && job.salaryMax != null ->
                        "$${job.salaryMin.toInt()} - $${job.salaryMax.toInt()}"
                    job.salaryMin != null -> "From $${job.salaryMin.toInt()}"
                    else -> "Up to $${job.salaryMax?.toInt()}"
                }
                Text(
                    text = salaryText,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

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

            Button(
                onClick = onViewApplicants,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                Text("View Applicants")
            }
        }
    }
}
