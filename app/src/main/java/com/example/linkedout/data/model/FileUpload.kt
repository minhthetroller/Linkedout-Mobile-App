package com.example.linkedout.data.model

data class FileUploadResponse(
    val resumeUrl: String? = null,
    val imageUrl: String? = null
)

data class SignedUrlResponse(
    val signedUrl: String,
    val expiresIn: Int
)

