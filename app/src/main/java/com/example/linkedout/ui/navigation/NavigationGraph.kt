package com.example.linkedout.ui.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.linkedout.ui.auth.LoginScreen
import com.example.linkedout.ui.auth.SignUpStep1Screen
import com.example.linkedout.ui.auth.SignUpStep2Screen
import com.example.linkedout.ui.auth.SignUpStep3Screen
import com.example.linkedout.ui.jobs.seeker.SeekerHomeScreen
import com.example.linkedout.ui.jobs.seeker.JobDetailsScreen
import com.example.linkedout.ui.jobs.seeker.RecommendedJobsScreen
import com.example.linkedout.ui.jobs.seeker.SearchJobsScreen
import com.example.linkedout.ui.profile.SeekerProfileScreen
import com.example.linkedout.ui.profile.RecruiterProfileScreen
import com.example.linkedout.ui.jobs.recruiter.RecruiterHomeScreen
import com.example.linkedout.ui.jobs.recruiter.CreateJobScreen
import com.example.linkedout.ui.jobs.recruiter.JobApplicantsScreen
import com.example.linkedout.ui.jobs.recruiter.ApplicantDetailsScreen

@Composable
fun NavigationGraph(
    navController: NavHostController,
    startDestination: String,
    onLogout: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Auth screens
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToSignUp = {
                    navController.navigate(Screen.SignUpStep1.route)
                },
                onLoginSuccess = { userType, profileStep ->
                    val destination = when {
                        profileStep < 2 -> Screen.SignUpStep2.route + "/$userType"
                        userType == "seeker" -> Screen.SeekerHome.route
                        userType == "recruiter" -> Screen.RecruiterHome.route
                        else -> Screen.Login.route
                    }
                    navController.navigate(destination) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.SignUpStep1.route) {
            SignUpStep1Screen(
                onNavigateToStep2 = { userType ->
                    navController.navigate(Screen.SignUpStep2.route + "/$userType")
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.SignUpStep2.route + "/{userType}",
            arguments = listOf(navArgument("userType") { type = NavType.StringType })
        ) { backStackEntry ->
            val userType = backStackEntry.arguments?.getString("userType") ?: "seeker"
            SignUpStep2Screen(
                userType = userType,
                onNavigateToStep3 = {
                    navController.navigate(Screen.SignUpStep3.route + "/$userType")
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.SignUpStep3.route + "/{userType}",
            arguments = listOf(navArgument("userType") { type = NavType.StringType })
        ) { backStackEntry ->
            val userType = backStackEntry.arguments?.getString("userType") ?: "seeker"
            SignUpStep3Screen(
                userType = userType,
                onComplete = {
                    navController.navigate(
                        if (userType == "seeker") Screen.SeekerHome.route
                        else Screen.RecruiterHome.route
                    ) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // Seeker screens
        composable(Screen.SeekerHome.route) {
            SeekerHomeScreen(
                onNavigateToJobDetails = { jobId: Int ->
                    navController.navigate(Screen.JobDetails.createRoute(jobId))
                },
                onNavigateToSearch = {
                    navController.navigate(Screen.SearchJobs.route)
                },
                onNavigateToRecommended = {
                    navController.navigate(Screen.RecommendedJobs.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.SeekerProfile.route)
                },
                onLogout = onLogout
            )
        }

        composable(Screen.SeekerProfile.route) {
            SeekerProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.JobDetails.route,
            arguments = listOf(navArgument("jobId") { type = NavType.IntType })
        ) { backStackEntry ->
            val jobId = backStackEntry.arguments?.getInt("jobId") ?: 0
            JobDetailsScreen(
                jobId = jobId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.RecommendedJobs.route) {
            RecommendedJobsScreen(
                onNavigateToJobDetails = { jobId: Int ->
                    navController.navigate(Screen.JobDetails.createRoute(jobId))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.SearchJobs.route) {
            SearchJobsScreen(
                onNavigateToJobDetails = { jobId: Int ->
                    navController.navigate(Screen.JobDetails.createRoute(jobId))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Recruiter screens
        composable(Screen.RecruiterHome.route) {
            RecruiterHomeScreen(
                onNavigateToCreateJob = {
                    navController.navigate(Screen.CreateJob.route)
                },
                onNavigateToEditJob = { jobId: Int ->
                    navController.navigate(Screen.EditJob.createRoute(jobId))
                },
                onNavigateToApplicants = { jobId: Int ->
                    navController.navigate(Screen.JobApplicants.createRoute(jobId))
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.RecruiterProfile.route)
                },
                onLogout = onLogout
            )
        }

        composable(Screen.CreateJob.route) {
            CreateJobScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.EditJob.route,
            arguments = listOf(navArgument("jobId") { type = NavType.IntType })
        ) { backStackEntry ->
            val jobId = backStackEntry.arguments?.getInt("jobId") ?: 0
            // Correctly navigate to CreateJobScreen with jobId for editing
            CreateJobScreen(
                jobId = jobId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.JobApplicants.route,
            arguments = listOf(navArgument("jobId") { type = NavType.IntType })
        ) { backStackEntry ->
            val jobId = backStackEntry.arguments?.getInt("jobId") ?: 0
            JobApplicantsScreen(
                jobId = jobId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onApplicantClick = { applicationId ->
                    navController.navigate(Screen.ApplicantDetails.createRoute(applicationId))
                }
            )
        }

        composable(
            route = Screen.ApplicantDetails.route,
            arguments = listOf(navArgument("applicationId") { type = NavType.IntType })
        ) { backStackEntry ->
            val applicationId = backStackEntry.arguments?.getInt("applicationId") ?: 0
            ApplicantDetailsScreen(
                applicationId = applicationId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.RecruiterProfile.route) {
            RecruiterProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
