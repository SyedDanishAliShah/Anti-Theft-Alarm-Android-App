package com.example.antitheftalarmapp.dataclasses


data class IntruderPhoto(
    val uniqueId: String, // Unique ID for each photo
    val id: Long,
    val imageData: ByteArray,
    val title: String,
    val description: String,
    val rotationDegrees: Float // New property to store rotation angle
)
