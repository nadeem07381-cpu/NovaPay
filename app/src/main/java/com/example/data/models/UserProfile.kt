package com.example.data.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val uid: String = "",
    val displayName: String = "NovaPay User",
    val email: String = "",
    val photoURL: String? = null,
    val phoneNumber: String = "+92 300 1234567",
    val balance: Double = 125000.0,
    val accountNumber: String = "PK76NOVA0008492019",
    val accountStatus: String = "ACTIVE",
    val preferredCurrency: String = "PKR",
    val onboardingCompleted: Boolean = true,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)
