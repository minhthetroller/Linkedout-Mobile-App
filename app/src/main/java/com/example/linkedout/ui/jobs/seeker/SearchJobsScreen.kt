package com.example.linkedout.ui.jobs.seeker

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.linkedout.ui.jobs.JobViewModel
import com.example.linkedout.util.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchJobsScreen(
    onNavigateToJobDetails: (Int) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: JobViewModel = hiltViewModel()
) {
    var location by remember { mutableStateOf("") }
    var salaryMin by remember { mutableStateOf("") }
    var salaryMax by remember { mutableStateOf("") }
    var employmentType by remember { mutableStateOf("") }
    var showFilters by remember { mutableStateOf(true) }

    val jobsState by viewModel.jobsState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search Jobs") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showFilters = !showFilters }) {
                        Icon(Icons.Default.Search, "Toggle Filters")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (showFilters) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = "Filters",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        OutlinedTextField(
                            value = location,
                            onValueChange = { location = it },
                            label = { Text("Location") },
                            placeholder = { Text("e.g., San Francisco or Remote") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
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

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = employmentType,
                            onValueChange = { employmentType = it },
                            label = { Text("Employment Type") },
                            placeholder = { Text("e.g., Full-time, Part-time") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    location = ""
                                    salaryMin = ""
                                    salaryMax = ""
                                    employmentType = ""
                                    viewModel.browseJobs()
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Clear")
                            }

                            Button(
                                onClick = {
                                    viewModel.browseJobs(
                                        location = location.ifBlank { null },
                                        salaryMin = salaryMin.toDoubleOrNull(),
                                        salaryMax = salaryMax.toDoubleOrNull(),
                                        employmentType = employmentType.ifBlank { null }
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Search")
                            }
                        }
                    }
                }
            }

            // Results
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
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("No jobs found")
                                TextButton(onClick = {
                                    location = ""
                                    salaryMin = ""
                                    salaryMax = ""
                                    employmentType = ""
                                    viewModel.browseJobs()
                                }) {
                                    Text("Clear filters")
                                }
                            }
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
                null -> {
                    LaunchedEffect(Unit) {
                        viewModel.browseJobs()
                    }
                }
            }
        }
    }
}

