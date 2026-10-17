package com.example.data.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class UserPreferences(
    val userId: String = "",
    val theme: String = "SYSTEM",
    val language: String = "English",
    val biometricEnabled: Boolean = true,
    val notificationPreferences: Boolean = true,
    @ServerTimestamp val updatedAt: Timestamp? = null
)
