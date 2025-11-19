package com.example.linkedout

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.linkedout.ui.auth.AuthViewModel
import com.example.linkedout.ui.navigation.NavigationGraph
import com.example.linkedout.ui.navigation.Screen
import com.example.linkedout.ui.theme.LinkedoutTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LinkedoutTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainApp(authViewModel)
                }
            }
        }
    }
}

@Composable
fun MainApp(authViewModel: AuthViewModel) {
    val navController = rememberNavController()
    val userType by authViewModel.userType.collectAsState()
    val profileCompletionStep by authViewModel.profileCompletionStep.collectAsState()

    var startDestination by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(userType, profileCompletionStep) {
        if (startDestination == null) {
            startDestination = when {
                userType == null -> Screen.Login.route
                profileCompletionStep == null || profileCompletionStep!! < 2 -> Screen.SignUpStep2.route + "/$userType"
                userType == "seeker" -> Screen.SeekerHome.route
                userType == "recruiter" -> Screen.RecruiterHome.route
                else -> Screen.Login.route
            }
        }
    }

    if (startDestination != null) {
        NavigationGraph(
            navController = navController,
            startDestination = startDestination!!,
            onLogout = {
                authViewModel.logout()
                navController.navigate(Screen.Login.route) {
                    popUpTo(0) { inclusive = true }
                }
            }
        )
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

