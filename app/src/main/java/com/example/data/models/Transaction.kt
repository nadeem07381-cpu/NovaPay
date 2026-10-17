package com.example.data.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

enum class TransactionType(val label: String) {
    SEND("Sent Money"),
    RECEIVE("Received Money"),
    TOPUP("Mobile Load"),
    BILL("Bill Payment"),
    ADD_MONEY("Deposit")
}

enum class TransactionStatus {
    COMPLETED,
    PENDING,
    FAILED
}

@IgnoreExtraProperties
data class TransactionItem(
    @DocumentId val id: String = "",
    val userId: String = "",
    val type: String = "SEND",
    val title: String = "",
    val description: String = "",
    val amount: Double = 0.0,
    val currency: String = "PKR",
    val category: String = "Transfer",
    val status: String = "COMPLETED",
    val recipientName: String = "",
    val recipientAccount: String = "",
    val referenceId: String = "",
    val demoTransaction: Boolean = true,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    val isCredit: Boolean
        get() = type == "RECEIVE" || type == "ADD_MONEY"
}
