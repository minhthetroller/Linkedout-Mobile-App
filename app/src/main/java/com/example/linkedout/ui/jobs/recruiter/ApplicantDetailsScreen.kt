package com.example.linkedout.ui.jobs.recruiter

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.linkedout.data.model.Applicant
import com.example.linkedout.ui.jobs.JobViewModel
import com.example.linkedout.util.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicantDetailsScreen(
    applicationId: Int,
    onNavigateBack: () -> Unit,
    viewModel: JobViewModel = hiltViewModel()
) {
    val applicantState by viewModel.applicantState.collectAsState()
    val profileImageSignedUrl by viewModel.profileImageSignedUrl.collectAsState()
    val resumeSignedUrl by viewModel.resumeSignedUrl.collectAsState()

    LaunchedEffect(applicationId) {
        viewModel.getApplicantDetails(applicationId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Applicant Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        when (val state = applicantState) {
            is Resource.Loading, null -> {
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
                val applicant = state.data
                if (applicant == null) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Applicant not found.")
                    }
                } else {
                    ApplicantDetails(
                        applicant = applicant,
                        profileImageSignedUrl = profileImageSignedUrl,
                        resumeSignedUrl = resumeSignedUrl,
                        onAccept = {
                            viewModel.updateApplicationStatus(applicationId, "accepted")
                            onNavigateBack()
                        },
                        onReject = {
                            viewModel.updateApplicationStatus(applicationId, "rejected")
                            onNavigateBack()
                        },
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }
            is Resource.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(state.message ?: "Error loading applicant details")
                }
            }
        }
    }
}

@Composable
fun ApplicantDetails(
    applicant: Applicant,
    profileImageSignedUrl: String?,
    resumeSignedUrl: String?,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Image
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape
                )
                .clip(CircleShape)
                .border(
                    width = 3.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (profileImageSignedUrl != null) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(profileImageSignedUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Profile Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else if (applicant.profileImageS3Url != null) {
                CircularProgressIndicator(
                    modifier = Modifier.size(40.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Default Profile",
                    modifier = Modifier.size(60.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Name
        Text(
            text = applicant.fullName,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Job Title if available
        applicant.jobTitle?.let {
            Text(
                text = "Applied for: $it",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Contact Information Card
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Contact Information",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Email: ${applicant.seekerEmail ?: applicant.email ?: "Not provided"}",
                    style = MaterialTheme.typography.bodyMedium
                )

                applicant.phone?.let {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Phone: $it", style = MaterialTheme.typography.bodyMedium)
                }

                applicant.location?.let {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Location: $it", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Professional Information Card
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Professional Information",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))

                applicant.currentJob?.let {
                    Text(text = "Current Job: $it", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                }

                applicant.yearsExperience?.let {
                    Text(
                        text = "Years of Experience: $it",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Resume Card
        if (applicant.resumeS3Url != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Resume",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Available",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                            )
                        }
                        Button(
                            onClick = {
                                resumeSignedUrl?.let { url ->
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                    context.startActivity(intent)
                                }
                            },
                            enabled = resumeSignedUrl != null
                        ) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("View Resume")
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Application Status
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = when (applicant.applicationStatus) {
                    "accepted" -> MaterialTheme.colorScheme.primaryContainer
                    "rejected" -> MaterialTheme.colorScheme.errorContainer
                    else -> MaterialTheme.colorScheme.secondaryContainer
                }
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Application Status",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = applicant.applicationStatus.uppercase(),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Cover Letter
        Text(text = "Cover Letter", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = applicant.coverLetter ?: "No cover letter provided.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Action Buttons (only show if status is pending)
        if (applicant.applicationStatus == "pending") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onReject,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Reject")
                }
                Button(
                    onClick = onAccept,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Accept")
                }
            }
        }
    }
}

@Composable
fun ApplicantDetailsSkeletonLoader(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Name skeleton
        Box(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(32.dp)
                .padding(bottom = 8.dp)
        ) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Email skeleton
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(20.dp)
                .padding(bottom = 8.dp)
        ) {
            CircularProgressIndicator(modifier = Modifier.size(16.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Current Job skeleton
        Box(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(20.dp)
                .padding(bottom = 8.dp)
        ) {
            CircularProgressIndicator(modifier = Modifier.size(16.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Status skeleton
        Box(
            modifier = Modifier
                .fillMaxWidth(0.4f)
                .height(20.dp)
                .padding(bottom = 16.dp)
        ) {
            CircularProgressIndicator(modifier = Modifier.size(16.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Cover Letter:", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        // Cover letter skeleton
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        ) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        Spacer(modifier = Modifier.weight(1f))

        // Buttons (disabled during loading)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = {}, enabled = false) {
                Text("Accept")
            }
            Button(
                onClick = {},
                enabled = false,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Reject")
            }
        }
    }
}

@Preview(showBackground = true, name = "Applicant Details - Pending")
@Composable
fun ApplicantDetailsPreview() {
    MaterialTheme {
        ApplicantDetails(
            applicant = Applicant(
                applicationId = 1,
                applicationStatus = "pending",
                coverLetter = "I am very interested in this position and believe my skills align perfectly with your requirements. I have 5 years of experience in full-stack development with expertise in React and Node.js, and I'm particularly excited about the opportunity to work on microservices architecture and AWS cloud infrastructure.",
                appliedAt = "2025-11-20T10:00:00Z",
                seekerId = 1,
                seekerEmail = "john.doe@example.com",
                fullName = "John Doe",
                currentJob = "Software Engineer at Tech Corp",
                yearsExperience = 5,
                location = "San Francisco, CA",
                phone = "+1 (555) 123-4567",
                profileImageS3Url = null,
                resumeS3Url = "https://example.com/resume.pdf",
                jobId = 1,
                jobTitle = "Senior Full Stack Developer",
                email = "john.doe@example.com"
            ),
            profileImageSignedUrl = null,
            resumeSignedUrl = null,
            onAccept = {},
            onReject = {}
        )
    }
}

@Preview(showBackground = true, name = "Applicant Details - Accepted")
@Composable
fun ApplicantDetailsAcceptedPreview() {
    MaterialTheme {
        ApplicantDetails(
            applicant = Applicant(
                applicationId = 2,
                applicationStatus = "accepted",
                coverLetter = "Looking forward to contributing to your team!",
                appliedAt = "2025-11-19T10:00:00Z",
                seekerId = 2,
                seekerEmail = "jane.smith@example.com",
                fullName = "Jane Smith",
                currentJob = "Senior Developer",
                yearsExperience = 8,
                location = "New York, NY",
                phone = "+1 (555) 987-6543",
                profileImageS3Url = null,
                resumeS3Url = null,
                jobId = 1,
                jobTitle = "Lead Backend Engineer",
                email = "jane.smith@example.com"
            ),
            profileImageSignedUrl = null,
            resumeSignedUrl = null,
            onAccept = {},
            onReject = {}
        )
    }
}

@Preview(showBackground = true, name = "Loading State")
@Composable
fun ApplicantDetailsSkeletonPreview() {
    MaterialTheme {
        ApplicantDetailsSkeletonLoader()
    }
}
