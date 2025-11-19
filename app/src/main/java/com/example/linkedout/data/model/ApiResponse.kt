package com.example.linkedout.data.model

data class ApiResponse<T>(
    val success: Boolean,
    val message: String? = null,
    val data: T? = null,
    val errors: List<ValidationError>? = null
)

data class ValidationError(
    val msg: String,
    val param: String,
    val location: String
)

