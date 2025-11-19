package com.example.linkedout.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object SignUpStep1 : Screen("signup_step1")
    object SignUpStep2 : Screen("signup_step2")
    object SignUpStep3 : Screen("signup_step3")

    // Seeker screens
    object SeekerHome : Screen("seeker_home")
    object JobDetails : Screen("job_details/{jobId}") {
        fun createRoute(jobId: Int) = "job_details/$jobId"
    }
    object SearchJobs : Screen("search_jobs")
    object RecommendedJobs : Screen("recommended_jobs")
    object SeekerProfile : Screen("seeker_profile")

    // Recruiter screens
    object RecruiterHome : Screen("recruiter_home")
    object CreateJob : Screen("create_job")
    object EditJob : Screen("edit_job/{jobId}") {
        fun createRoute(jobId: Int) = "edit_job/$jobId"
    }
    object JobApplicants : Screen("job_applicants/{jobId}") {
        fun createRoute(jobId: Int) = "job_applicants/$jobId"
    }
    object RecruiterProfile : Screen("recruiter_profile")
}
