package com.example.linkedout.ui.auth

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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.linkedout.util.Resource

@Composable
fun SignUpStep1Screen(
    onNavigateToStep2: (String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var userType by remember { mutableStateOf("seeker") }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        when (authState) {
            is Resource.Success -> {
                viewModel.resetAuthState()
                onNavigateToStep2(userType)
            }
            is Resource.Error -> {
                showError = true
                errorMessage = (authState as Resource.Error).message ?: "Sign up failed"
            }
            else -> {}
        }
    }

    SignUpStep1Content(
        email = email,
        password = password,
        fullName = fullName,
        birthDate = birthDate,
        userType = userType,
        showError = showError,
        errorMessage = errorMessage,
        isLoading = authState is Resource.Loading,
        onEmailChange = { email = it },
        onPasswordChange = { password = it },
        onFullNameChange = { fullName = it },
        onBirthDateChange = { birthDate = it },
        onUserTypeChange = { userType = it },
        onNextClick = {
            if (fullName.isNotBlank() && email.isNotBlank() &&
                password.length >= 6 && birthDate.isNotBlank()) {
                showError = false
                viewModel.signUpStep1(email, password, userType, fullName, birthDate)
            } else {
                showError = true
                errorMessage = "Please fill in all fields correctly"
            }
        },
        onNavigateBack = onNavigateBack
    )
}

@Preview(showBackground = true)
@Composable
fun SignUpStep1SeekerPreview() {
    MaterialTheme {
        SignUpStep1Content(
            email = "john.doe@example.com",
            password = "password123",
            fullName = "John Doe",
            birthDate = "1995-06-15",
            userType = "seeker",
            showError = false,
            errorMessage = "",
            isLoading = false,
            onEmailChange = {},
            onPasswordChange = {},
            onFullNameChange = {},
            onBirthDateChange = {},
            onUserTypeChange = {},
            onNextClick = {},
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpStep1RecruiterPreview() {
    MaterialTheme {
        SignUpStep1Content(
            email = "recruiter@company.com",
            password = "secure123",
            fullName = "Jane Smith",
            birthDate = "1990-03-20",
            userType = "recruiter",
            showError = false,
            errorMessage = "",
            isLoading = false,
            onEmailChange = {},
            onPasswordChange = {},
            onFullNameChange = {},
            onBirthDateChange = {},
            onUserTypeChange = {},
            onNextClick = {},
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpStep1ErrorPreview() {
    MaterialTheme {
        SignUpStep1Content(
            email = "invalid-email",
            password = "123",
            fullName = "",
            birthDate = "",
            userType = "seeker",
            showError = true,
            errorMessage = "Please fill in all fields correctly",
            isLoading = false,
            onEmailChange = {},
            onPasswordChange = {},
            onFullNameChange = {},
            onBirthDateChange = {},
            onUserTypeChange = {},
            onNextClick = {},
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpStep1LoadingPreview() {
    MaterialTheme {
        SignUpStep1Content(
            email = "john.doe@example.com",
            password = "password123",
            fullName = "John Doe",
            birthDate = "1995-06-15",
            userType = "seeker",
            showError = false,
            errorMessage = "",
            isLoading = true,
            onEmailChange = {},
            onPasswordChange = {},
            onFullNameChange = {},
            onBirthDateChange = {},
            onUserTypeChange = {},
            onNextClick = {},
            onNavigateBack = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SignUpStep1Content(
    email: String,
    password: String,
    fullName: String,
    birthDate: String,
    userType: String,
    showError: Boolean,
    errorMessage: String,
    isLoading: Boolean,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onFullNameChange: (String) -> Unit,
    onBirthDateChange: (String) -> Unit,
    onUserTypeChange: (String) -> Unit,
    onNextClick: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sign Up - Step 1") },
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
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Create Account",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "I am a:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FilterChip(
                    selected = userType == "seeker",
                    onClick = { onUserTypeChange("seeker") },
                    label = { Text("Job Seeker") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = userType == "recruiter",
                    onClick = { onUserTypeChange("recruiter") },
                    label = { Text("Recruiter") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = fullName,
                onValueChange = onFullNameChange,
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = { Text("Password (min 6 characters)") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = birthDate,
                onValueChange = onBirthDateChange,
                label = { Text("Birth Date (YYYY-MM-DD)") },
                placeholder = { Text("1995-06-15") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
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
                onClick = onNextClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Next")
                }
            }
        }
    }
}

