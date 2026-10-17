package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.repositories.WalletRepository
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class WalletRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun createAndObserveTransaction_authenticatedUser_success() = runBlocking {
        val uid = signInTestUser("alice_${System.currentTimeMillis()}@novapay.pk")
        val repository = WalletRepository(firestore)

        // Seed initial user profile so user document exists
        firestore.collection("users").document(uid).set(
            mapOf(
                "uid" to uid,
                "displayName" to "Alice",
                "email" to "alice@novapay.pk",
                "balance" to 50000.0,
                "accountNumber" to "PK76NOVA11223344",
                "accountStatus" to "ACTIVE",
                "preferredCurrency" to "PKR",
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
        ).await()

        val tx = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.submitTransaction(
                userId = uid,
                type = "SEND",
                title = "Sent to Ali",
                description = "Dinner Split",
                amount = 2500.0,
                category = "Transfer",
                recipientName = "Ali Raza",
                recipientAccount = "03001122334"
            )
        }

        assertNotNull(tx.id)
        assertEquals(2500.0, tx.amount, 0.001)

        val transactions = withTimeout(FLOW_TIMEOUT_MS) {
            repository.observeTransactions(uid).first { list -> list.any { it.id == tx.id } }
        }

        assertTrue(transactions.any { it.id == tx.id })
    }

    private companion object {
        const val DEFAULT_TIMEOUT_MS = 5000L
        const val FLOW_TIMEOUT_MS = 10000L
    }
}
