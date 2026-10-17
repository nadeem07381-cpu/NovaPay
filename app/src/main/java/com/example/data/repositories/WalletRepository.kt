package com.example.data.repositories

import android.content.Context
import com.example.R
import com.example.data.models.NotificationItem
import com.example.data.models.TransactionItem
import com.example.data.models.UserPreferences
import com.example.data.models.UserProfile
import com.example.util.CurrencyFormatter
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class WalletRepository(
    private val db: FirebaseFirestore
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth = Firebase.auth

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be authenticated before performing wallet operations.")
    }

    /**
     * Observes the user's wallet profile document in real-time.
     */
    fun observeUserProfile(userId: String): Flow<UserProfile?> {
        val docRef = db.collection("users").document(userId)
        return docRef.snapshots()
            .map { snapshot ->
                if (snapshot.exists()) {
                    snapshot.toObject(UserProfile::class.java)
                } else {
                    null
                }
            }
            .catch { error ->
                if (error is Exception) handleFirestoreError(error, OperationType.GET, docRef.path)
                throw error
            }
    }

    /**
     * Ensures the user profile exists upon authentication.
     */
    suspend fun getOrCreateProfile(user: FirebaseUser): UserProfile {
        val docRef = db.collection("users").document(user.uid)
        return try {
            val snapshot = docRef.get().await()
            if (snapshot.exists()) {
                val profile = snapshot.toObject(UserProfile::class.java)
                if (profile != null) return profile
            }

            // Create initial profile
            val initialBalance = 125000.0
            val initialProfile = UserProfile(
                uid = user.uid,
                displayName = user.displayName?.takeIf { it.isNotBlank() } ?: "NovaPay User",
                email = user.email ?: "user@novapay.pk",
                photoURL = user.photoUrl?.toString(),
                phoneNumber = "+92 300 8492019",
                balance = initialBalance,
                accountNumber = "PK76NOVA000" + (1000000 + (Math.random() * 8999999).toInt()),
                accountStatus = "ACTIVE",
                preferredCurrency = "PKR",
                onboardingCompleted = true
            )

            val payload = mapOf(
                "uid" to initialProfile.uid,
                "displayName" to initialProfile.displayName,
                "email" to initialProfile.email,
                "photoURL" to initialProfile.photoURL,
                "phoneNumber" to initialProfile.phoneNumber,
                "balance" to initialProfile.balance,
                "accountNumber" to initialProfile.accountNumber,
                "accountStatus" to initialProfile.accountStatus,
                "preferredCurrency" to initialProfile.preferredCurrency,
                "onboardingCompleted" to initialProfile.onboardingCompleted,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            ).filterValues { it != null }

            docRef.set(payload).await()
            seedSampleTransactions(user.uid)
            initialProfile
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            throw e
        }
    }

    /**
     * Observes real-time transactions for the user.
     */
    fun observeTransactions(userId: String): Flow<List<TransactionItem>> {
        val path = "users/$userId/transactions"
        return db.collection("users").document(userId)
            .collection("transactions")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(TransactionItem::class.java)
            }
            .catch { error ->
                if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                throw error
            }
    }

    /**
     * Observes user in-app notifications.
     */
    fun observeNotifications(userId: String): Flow<List<NotificationItem>> {
        val path = "users/$userId/notifications"
        return db.collection("users").document(userId)
            .collection("notifications")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { snapshot -> snapshot.toObjects(NotificationItem::class.java) }
            .catch { error ->
                if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                throw error
            }
    }

    /**
     * Observes user preferences.
     */
    fun observePreferences(userId: String): Flow<UserPreferences?> {
        val docRef = db.collection("users").document(userId).collection("settings").document("preferences")
        return docRef.snapshots()
            .map { snapshot ->
                if (snapshot.exists()) snapshot.toObject(UserPreferences::class.java) else null
            }
            .catch { error ->
                if (error is Exception) handleFirestoreError(error, OperationType.GET, docRef.path)
                throw error
            }
    }

    /**
     * Updates user preferences in Firestore.
     */
    suspend fun savePreferences(userId: String, prefs: UserPreferences) {
        val docRef = db.collection("users").document(userId).collection("settings").document("preferences")
        try {
            val payload = mapOf(
                "userId" to userId,
                "theme" to prefs.theme,
                "language" to prefs.language,
                "biometricEnabled" to prefs.biometricEnabled,
                "notificationPreferences" to prefs.notificationPreferences,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            throw e
        }
    }

    /**
     * Marks a notification as read.
     */
    suspend fun markNotificationAsRead(userId: String, notificationId: String) {
        val docRef = db.collection("users").document(userId).collection("notifications").document(notificationId)
        try {
            docRef.update("read", true).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    /**
     * Submits a transaction, adjusting balance and logging notification.
     */
    suspend fun submitTransaction(
        userId: String,
        type: String,
        title: String,
        description: String,
        amount: Double,
        category: String,
        recipientName: String = "",
        recipientAccount: String = ""
    ): TransactionItem {
        val userRef = db.collection("users").document(userId)
        val txCollection = userRef.collection("transactions")
        val txDoc = txCollection.document()
        val refId = CurrencyFormatter.generateReferenceId()

        val isCredit = (type == "RECEIVE" || type == "ADD_MONEY")
        val delta = if (isCredit) amount else -amount

        try {
            // Write transaction record
            val txPayload = mapOf(
                "userId" to userId,
                "type" to type,
                "title" to title,
                "description" to description,
                "amount" to amount,
                "currency" to "PKR",
                "category" to category,
                "status" to "COMPLETED",
                "recipientName" to recipientName,
                "recipientAccount" to recipientAccount,
                "referenceId" to refId,
                "demoTransaction" to true,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            ).filterValues { it != null && (it !is String || it.isNotEmpty()) }

            txDoc.set(txPayload).await()

            // Update user balance
            userRef.update(
                mapOf(
                    "balance" to FieldValue.increment(delta),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()

            // Add notification
            val notifRef = userRef.collection("notifications").document()
            notifRef.set(
                mapOf(
                    "userId" to userId,
                    "title" to if (isCredit) "Money Received" else "Payment Successful",
                    "message" to "$title for ${CurrencyFormatter.formatPkr(amount)} has been processed successfully.",
                    "type" to "TRANSACTION",
                    "read" to false,
                    "createdAt" to FieldValue.serverTimestamp()
                )
            ).await()

            return TransactionItem(
                id = txDoc.id,
                userId = userId,
                type = type,
                title = title,
                description = description,
                amount = amount,
                currency = "PKR",
                category = category,
                status = "COMPLETED",
                recipientName = recipientName,
                recipientAccount = recipientAccount,
                referenceId = refId,
                demoTransaction = true
            )
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, txDoc.path)
            throw e
        }
    }

    /**
     * Seeds initial transactions for demo experience so the wallet looks realistic immediately.
     */
    private suspend fun seedSampleTransactions(userId: String) {
        val userRef = db.collection("users").document(userId)
        val txCollection = userRef.collection("transactions")
        val notifCollection = userRef.collection("notifications")

        val samples = listOf(
            Triple("Salary Credit (Monthly)", 85000.0, "RECEIVE") to ("Habib Bank Ltd" to "Salary Transfer"),
            Triple("Sent to Zainab Bibi", 3500.0, "SEND") to ("0312 9841234" to "Dinner split"),
            Triple("LESCO Electricity Bill", 12450.0, "BILL") to ("04-11223-098231" to "September Electricity"),
            Triple("Jazz Super Card Mobile Load", 1150.0, "TOPUP") to ("0300 8492019" to "Monthly Bundle"),
            Triple("Received from Hamza Khan", 15000.0, "RECEIVE") to ("Raast ID" to "Freelance Project milestone")
        )

        samples.forEach { (meta, details) ->
            val (title, amount, type) = meta
            val (account, desc) = details
            val txDoc = txCollection.document()
            val refId = CurrencyFormatter.generateReferenceId()

            txDoc.set(
                mapOf(
                    "userId" to userId,
                    "type" to type,
                    "title" to title,
                    "description" to desc,
                    "amount" to amount,
                    "currency" to "PKR",
                    "category" to if (type == "BILL") "Utilities" else if (type == "TOPUP") "Mobile Load" else "Transfer",
                    "status" to "COMPLETED",
                    "recipientName" to if (type == "SEND") "Zainab Bibi" else "",
                    "recipientAccount" to account,
                    "referenceId" to refId,
                    "demoTransaction" to true,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                ).filterValues { it != null && (it !is String || it.isNotEmpty()) }
            ).await()
        }

        // Add welcome notification
        val notifDoc = notifCollection.document()
        notifDoc.set(
            mapOf(
                "userId" to userId,
                "title" to "Welcome to NovaPay!",
                "message" to "Your premium digital wallet has been activated with verified status. Explore seamless transfers, bills, and loads.",
                "type" to "SYSTEM",
                "read" to false,
                "createdAt" to FieldValue.serverTimestamp()
            )
        ).await()
    }
}
