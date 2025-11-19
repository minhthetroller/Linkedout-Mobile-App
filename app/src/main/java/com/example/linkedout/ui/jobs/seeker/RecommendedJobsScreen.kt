package com.example.linkedout.ui.jobs.seeker

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
fun RecommendedJobsScreen(
    onNavigateToJobDetails: (Int) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: JobViewModel = hiltViewModel()
) {
    val recommendedJobsState by viewModel.recommendedJobsState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getRecommendedJobs()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recommended Jobs") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        when (val state = recommendedJobsState) {
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
                val jobListResponse = state.data
                val jobs = jobListResponse?.jobs ?: emptyList()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    if (jobListResponse?.message != null) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Text(
                                text = jobListResponse.message,
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    if (jobs.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No recommended jobs found")
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
                            text = state.message ?: "Error loading recommended jobs",
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(
                            onClick = { viewModel.getRecommendedJobs() },
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

