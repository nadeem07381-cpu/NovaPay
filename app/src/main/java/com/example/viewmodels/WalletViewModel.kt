package com.example.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.models.NotificationItem
import com.example.data.models.TransactionItem
import com.example.data.models.UserPreferences
import com.example.data.models.UserProfile
import com.example.data.repositories.WalletRepository
import com.example.ui.components.BottomNavTab
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

class WalletViewModel(
    private val repository: WalletRepository,
    val currentUserId: String
) : ViewModel() {

    private val _currentTab = MutableStateFlow(BottomNavTab.HOME)
    val currentTab = _currentTab.asStateFlow()

    private val _actionMessage = MutableStateFlow<String?>(null)
    val actionMessage = _actionMessage.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing = _isProcessing.asStateFlow()

    private val _lastCompletedTransaction = MutableStateFlow<TransactionItem?>(null)
    val lastCompletedTransaction = _lastCompletedTransaction.asStateFlow()

    val profileState: StateFlow<UiState<UserProfile>> = repository.observeUserProfile(currentUserId)
        .map<UserProfile?, UiState<UserProfile>> { profile ->
            if (profile != null) UiState.Success(profile) else UiState.Loading
        }
        .catch { error ->
            Log.e(TAG, "Error observing profile", error)
            emit(UiState.Error(error.message ?: "Failed to load profile"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = UiState.Loading
        )

    val transactionsState: StateFlow<UiState<List<TransactionItem>>> = repository.observeTransactions(currentUserId)
        .map<List<TransactionItem>, UiState<List<TransactionItem>>> { UiState.Success(it) }
        .catch { error ->
            Log.e(TAG, "Error observing transactions", error)
            emit(UiState.Error(error.message ?: "Failed to load transactions"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = UiState.Loading
        )

    val notificationsState: StateFlow<UiState<List<NotificationItem>>> = repository.observeNotifications(currentUserId)
        .map<List<NotificationItem>, UiState<List<NotificationItem>>> { UiState.Success(it) }
        .catch { error ->
            Log.e(TAG, "Error observing notifications", error)
            emit(UiState.Error(error.message ?: "Failed to load notifications"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = UiState.Loading
        )

    val preferencesState: StateFlow<UiState<UserPreferences>> = repository.observePreferences(currentUserId)
        .map<UserPreferences?, UiState<UserPreferences>> { prefs ->
            UiState.Success(prefs ?: UserPreferences(userId = currentUserId))
        }
        .catch { error ->
            Log.e(TAG, "Error observing preferences", error)
            emit(UiState.Error(error.message ?: "Failed to load preferences"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = UiState.Loading
        )

    fun selectTab(tab: BottomNavTab) {
        _currentTab.value = tab
    }

    fun clearActionMessage() {
        _actionMessage.value = null
    }

    fun clearLastCompletedTransaction() {
        _lastCompletedTransaction.value = null
    }

    /**
     * Executes Send Money transaction.
     */
    fun sendMoney(
        recipientName: String,
        recipientAccount: String,
        amount: Double,
        purpose: String,
        onSuccess: (TransactionItem) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isProcessing.value = true
            try {
                val currentProfile = (profileState.value as? UiState.Success)?.data
                if (currentProfile != null && currentProfile.balance < amount) {
                    _isProcessing.value = false
                    onError("Insufficient balance in your NovaPay wallet.")
                    return@launch
                }

                val tx = repository.submitTransaction(
                    userId = currentUserId,
                    type = "SEND",
                    title = "Sent to $recipientName",
                    description = purpose.ifBlank { "Fund Transfer" },
                    amount = amount,
                    category = "Transfer",
                    recipientName = recipientName,
                    recipientAccount = recipientAccount
                )
                _lastCompletedTransaction.value = tx
                _isProcessing.value = false
                onSuccess(tx)
            } catch (e: Exception) {
                _isProcessing.value = false
                Log.e(TAG, "Send money failed", e)
                onError(e.localizedMessage ?: "Failed to send money")
            }
        }
    }

    /**
     * Executes Add Money deposit.
     */
    fun addMoney(
        methodTitle: String,
        amount: Double,
        onSuccess: (TransactionItem) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isProcessing.value = true
            try {
                val tx = repository.submitTransaction(
                    userId = currentUserId,
                    type = "ADD_MONEY",
                    title = "Deposit via $methodTitle",
                    description = "Top up to NovaPay Wallet",
                    amount = amount,
                    category = "Deposit",
                    recipientName = "Self Deposit",
                    recipientAccount = methodTitle
                )
                _lastCompletedTransaction.value = tx
                _isProcessing.value = false
                onSuccess(tx)
            } catch (e: Exception) {
                _isProcessing.value = false
                Log.e(TAG, "Add money failed", e)
                onError(e.localizedMessage ?: "Failed to add money")
            }
        }
    }

    /**
     * Executes Mobile Top-Up (Load).
     */
    fun payMobileLoad(
        operator: String,
        mobileNumber: String,
        amount: Double,
        onSuccess: (TransactionItem) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isProcessing.value = true
            try {
                val tx = repository.submitTransaction(
                    userId = currentUserId,
                    type = "TOPUP",
                    title = "$operator Load",
                    description = "Mobile recharge for $mobileNumber",
                    amount = amount,
                    category = "Mobile Load",
                    recipientName = operator,
                    recipientAccount = mobileNumber
                )
                _lastCompletedTransaction.value = tx
                _isProcessing.value = false
                onSuccess(tx)
            } catch (e: Exception) {
                _isProcessing.value = false
                Log.e(TAG, "Mobile load failed", e)
                onError(e.localizedMessage ?: "Failed to process mobile recharge")
            }
        }
    }

    /**
     * Executes Utility Bill payment.
     */
    fun payUtilityBill(
        company: String,
        consumerNumber: String,
        amount: Double,
        billType: String,
        onSuccess: (TransactionItem) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isProcessing.value = true
            try {
                val tx = repository.submitTransaction(
                    userId = currentUserId,
                    type = "BILL",
                    title = "$company Bill",
                    description = "$billType Payment",
                    amount = amount,
                    category = "Utilities",
                    recipientName = company,
                    recipientAccount = consumerNumber
                )
                _lastCompletedTransaction.value = tx
                _isProcessing.value = false
                onSuccess(tx)
            } catch (e: Exception) {
                _isProcessing.value = false
                Log.e(TAG, "Bill payment failed", e)
                onError(e.localizedMessage ?: "Failed to pay bill")
            }
        }
    }

    fun markNotificationRead(notificationId: String) {
        viewModelScope.launch {
            try {
                repository.markNotificationAsRead(currentUserId, notificationId)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to mark notification as read", e)
            }
        }
    }

    fun updatePreferences(theme: String? = null, language: String? = null, biometric: Boolean? = null) {
        viewModelScope.launch {
            try {
                val current = (preferencesState.value as? UiState.Success)?.data ?: UserPreferences(userId = currentUserId)
                val updated = current.copy(
                    theme = theme ?: current.theme,
                    language = language ?: current.language,
                    biometricEnabled = biometric ?: current.biometricEnabled
                )
                repository.savePreferences(currentUserId, updated)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to update preferences", e)
            }
        }
    }

    private companion object {
        const val TAG = "WalletViewModel"
        const val STOP_TIMEOUT_MILLIS = 5000L
    }
}
