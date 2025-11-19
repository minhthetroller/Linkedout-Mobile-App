package com.example.linkedout.ui.jobs.seeker

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
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
fun SeekerHomeScreen(
    onNavigateToJobDetails: (Int) -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToRecommended: () -> Unit,
    onLogout: () -> Unit,
    viewModel: JobViewModel = hiltViewModel()
) {
    val jobsState by viewModel.jobsState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.browseJobs()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Find Jobs") },
                actions = {
                    IconButton(onClick = onNavigateToSearch) {
                        Icon(Icons.Default.Search, "Search")
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, "Logout")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToRecommended,
                icon = { Icon(Icons.Default.Star, "Recommended") },
                text = { Text("Recommended") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = jobsState) {
                is Resource.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is Resource.Success -> {
                    val jobs = state.data ?: emptyList()
                    if (jobs.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No jobs available")
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(jobs) { job ->
                                JobCard(job = job, onClick = { onNavigateToJobDetails(job.id) })
                            }
                        }
                    }
                }
                is Resource.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = state.message ?: "Error loading jobs",
                                color = MaterialTheme.colorScheme.error
                            )
                            Button(
                                onClick = { viewModel.browseJobs() },
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
}

@Composable
fun JobCard(job: Job, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = job.title,
                style = MaterialTheme.typography.titleLarge
            )

            if (job.companyName != null) {
                Text(
                    text = job.companyName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            if (job.location != null) {
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = job.location,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (job.salaryMin != null || job.salaryMax != null) {
                val salaryText = when {
                    job.salaryMin != null && job.salaryMax != null ->
                        "$${job.salaryMin.toInt()} - $${job.salaryMax.toInt()}"
                    job.salaryMin != null -> "From $${job.salaryMin.toInt()}"
                    else -> "Up to $${job.salaryMax?.toInt()}"
                }
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = salaryText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (job.employmentType != null) {
                AssistChip(
                    onClick = {},
                    label = { Text(job.employmentType) },
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (job.matchScore != null && job.matchScoreDisplay != null) {
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Match: ${job.matchScoreDisplay}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

