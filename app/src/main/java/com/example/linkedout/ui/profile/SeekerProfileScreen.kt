package com.example.linkedout.ui.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.linkedout.util.Resource
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeekerProfileScreen(
    onNavigateBack: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val profileState by viewModel.profileState.collectAsState()
    val uploadResumeState by viewModel.uploadResumeState.collectAsState()
    val uploadImageState by viewModel.uploadImageState.collectAsState()
    val profileImageSignedUrl by viewModel.profileImageSignedUrl.collectAsState()
    val resumeFileName by viewModel.resumeFileName.collectAsState()
    val applicationsState by viewModel.applicationsState.collectAsState()
    val context = LocalContext.current

    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }
    var selectedTabIndex by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        viewModel.loadProfile()
        viewModel.loadApplications()
    }

    // Handle upload states
    LaunchedEffect(uploadResumeState) {
        when (val state = uploadResumeState) {
            is Resource.Success -> {
                snackbarMessage = "Resume uploaded successfully"
                showSnackbar = true
                viewModel.resetUploadStates()
            }
            is Resource.Error -> {
                snackbarMessage = state.message ?: "Failed to upload resume"
                showSnackbar = true
                viewModel.resetUploadStates()
            }
            else -> {}
        }
    }

    LaunchedEffect(uploadImageState) {
        when (val state = uploadImageState) {
            is Resource.Success -> {
                snackbarMessage = "Profile image uploaded successfully"
                showSnackbar = true
                viewModel.resetUploadStates()
            }
            is Resource.Error -> {
                snackbarMessage = state.message ?: "Failed to upload image"
                showSnackbar = true
                viewModel.resetUploadStates()
            }
            else -> {}
        }
    }

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(it)
                val file = File(context.cacheDir, "profile_image_${System.currentTimeMillis()}.jpg")
                val outputStream = FileOutputStream(file)
                inputStream?.copyTo(outputStream)
                inputStream?.close()
                outputStream.close()
                viewModel.uploadProfileImage(file)
            } catch (e: Exception) {
                snackbarMessage = "Failed to process image: ${e.message}"
                showSnackbar = true
            }
        }
    }

    // Resume picker launcher
    val resumePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(it)
                val file = File(context.cacheDir, "resume_${System.currentTimeMillis()}.pdf")
                val outputStream = FileOutputStream(file)
                inputStream?.copyTo(outputStream)
                inputStream?.close()
                outputStream.close()
                viewModel.uploadResume(file)
            } catch (e: Exception) {
                snackbarMessage = "Failed to process resume: ${e.message}"
                showSnackbar = true
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        },
        snackbarHost = {
            if (showSnackbar) {
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    action = {
                        TextButton(
                            onClick = {
                                showSnackbar = false
                            }
                        ) {
                            Text("Dismiss")
                        }
                    }
                ) {
                    Text(snackbarMessage)
                }
            }
        }
    ) { paddingValues ->
        when (val state = profileState) {
            is Resource.Loading, null -> {
                // Skeleton loading screen
                ProfileSkeletonLoading(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
            is Resource.Success -> {
                val profileData = state.data
                if (profileData != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Profile Image Section
                        Box(
                            modifier = Modifier
                                .size(150.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = CircleShape
                                )
                                .clip(CircleShape)
                                .border(
                                    width = 3.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = CircleShape
                                )
                                .clickable { imagePickerLauncher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            // Use signed URL if available, otherwise show placeholder
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
                            } else if (profileData.profile.profileImageS3Url != null) {
                                // Show loading indicator while fetching signed URL
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(40.dp),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Add Profile Image",
                                    modifier = Modifier.size(80.dp),
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }

                            // Add icon overlay - only show when no profile image
                            if (profileImageSignedUrl == null && profileData.profile.profileImageS3Url == null) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .size(40.dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.primary,
                                            shape = CircleShape
                                        )
                                        .border(
                                            width = 2.dp,
                                            color = MaterialTheme.colorScheme.surface,
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add",
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }

                        if (uploadImageState is Resource.Loading) {
                            Spacer(modifier = Modifier.height(8.dp))
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Name
                        Text(
                            text = profileData.profile.fullName,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Email
                        Text(
                            text = profileData.user.email,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Profile Information Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "Personal Information",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                HorizontalDivider()

                                if (profileData.profile.currentJob != null) {
                                    ProfileInfoItem(
                                        label = "Current Job",
                                        value = profileData.profile.currentJob
                                    )
                                }

                                if (profileData.profile.yearsExperience != null) {
                                    ProfileInfoItem(
                                        label = "Years of Experience",
                                        value = "${profileData.profile.yearsExperience} years"
                                    )
                                }

                                if (profileData.profile.location != null) {
                                    ProfileInfoItem(
                                        label = "Location",
                                        value = profileData.profile.location
                                    )
                                }

                                if (profileData.profile.phone != null) {
                                    ProfileInfoItem(
                                        label = "Phone",
                                        value = profileData.profile.phone
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Preferences Card
                        if (profileData.preferences != null && !profileData.preferences.isSkipped) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(
                                        text = "Job Preferences",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )

                                    HorizontalDivider()

                                    if (!profileData.preferences.preferredJobTitles.isNullOrEmpty()) {
                                        ProfileInfoItem(
                                            label = "Preferred Roles",
                                            value = profileData.preferences.preferredJobTitles.joinToString(", ")
                                        )
                                    }

                                    if (!profileData.preferences.preferredLocations.isNullOrEmpty()) {
                                        ProfileInfoItem(
                                            label = "Preferred Locations",
                                            value = profileData.preferences.preferredLocations.joinToString(", ")
                                        )
                                    }

                                    if (profileData.preferences.salaryExpectationMin != null ||
                                        profileData.preferences.salaryExpectationMax != null
                                    ) {
                                        val salaryText = when {
                                            profileData.preferences.salaryExpectationMin != null &&
                                                    profileData.preferences.salaryExpectationMax != null ->
                                                "$${profileData.preferences.salaryExpectationMin.toInt()} - $${profileData.preferences.salaryExpectationMax.toInt()}"
                                            profileData.preferences.salaryExpectationMin != null ->
                                                "From $${profileData.preferences.salaryExpectationMin.toInt()}"
                                            else ->
                                                "Up to $${profileData.preferences.salaryExpectationMax?.toInt()}"
                                        }
                                        ProfileInfoItem(
                                            label = "Salary Expectation",
                                            value = salaryText
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Resume Upload Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Resume",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (uploadResumeState is Resource.Loading) {
                                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                    }
                                }

                                HorizontalDivider()

                                if (profileData.profile.resumeS3Url != null) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                modifier = Modifier.weight(1f),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Description,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = resumeFileName ?: "Resume.pdf",
                                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        fontWeight = FontWeight.Medium,
                                                        maxLines = 1
                                                    )
                                                    Text(
                                                        text = "Uploaded",
                                                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
                                                        style = MaterialTheme.typography.bodySmall
                                                    )
                                                }
                                            }
                                            TextButton(
                                                onClick = { resumePickerLauncher.launch("application/pdf") }
                                            ) {
                                                Text("Replace")
                                            }
                                        }
                                    }
                                } else {
                                    Button(
                                        onClick = { resumePickerLauncher.launch("application/pdf") },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = null
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Upload Resume (PDF)")
                                    }
                                }
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
                            text = state.message ?: "Error loading profile",
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(
                            onClick = { viewModel.loadProfile() },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileInfoItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ProfileSkeletonLoading(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Image Skeleton
        Box(
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = CircleShape
                )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Name Skeleton
        Box(
            modifier = Modifier
                .width(200.dp)
                .height(32.dp)
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Email Skeleton
        Box(
            modifier = Modifier
                .width(160.dp)
                .height(24.dp)
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Personal Information Card Skeleton
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(150.dp)
                        .height(20.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )

                HorizontalDivider()

                repeat(3) {
                    Column {
                        Box(
                            modifier = Modifier
                                .width(100.dp)
                                .height(14.dp)
                                .clip(MaterialTheme.shapes.small)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.7f)
                                .height(20.dp)
                                .clip(MaterialTheme.shapes.small)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Resume Card Skeleton
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(20.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )

                HorizontalDivider()

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
            }
        }
    }
}

