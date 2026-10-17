package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.data.models.TransactionItem
import com.example.data.repositories.WalletRepository
import com.example.ui.components.BottomNavTab
import com.example.ui.components.NovaBottomNav
import com.example.ui.screens.AddMoneyScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.BillPaymentScreen
import com.example.ui.screens.HelpSupportScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MobileTopUpScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.PaymentConfirmationDialog
import com.example.ui.screens.PaymentSuccessScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ReceiveMoneyScreen
import com.example.ui.screens.SecurityScreen
import com.example.ui.screens.SendMoneyScreen
import com.example.ui.screens.TransactionHistoryScreen
import com.example.ui.screens.signOutUser
import com.example.ui.theme.NovaPayTheme
import com.example.viewmodels.UiState
import com.example.viewmodels.WalletViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

sealed interface ActiveScreen {
    data object Dashboard : ActiveScreen
    data object SendMoney : ActiveScreen
    data object ReceiveMoney : ActiveScreen
    data object AddMoney : ActiveScreen
    data object MobileLoad : ActiveScreen
    data object PayBills : ActiveScreen
    data object Notifications : ActiveScreen
    data object Security : ActiveScreen
    data object HelpSupport : ActiveScreen
    data class PaymentSuccess(val transaction: TransactionItem) : ActiveScreen
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            NovaPayTheme {
                NovaPayApp()
            }
        }
    }
}

@Composable
fun NovaPayApp() {
    val auth = Firebase.auth
    var currentUser by remember { mutableStateOf<FirebaseUser?>(auth.currentUser) }

    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            currentUser = firebaseAuth.currentUser
        }
        auth.addAuthStateListener(listener)
        onDispose {
            auth.removeAuthStateListener(listener)
        }
    }

    val user = currentUser
    if (user == null) {
        AuthScreen(
            onAuthSuccess = {
                currentUser = Firebase.auth.currentUser
            }
        )
    } else {
        MainWalletSession(
            user = user,
            onSignOut = {
                currentUser = null
            }
        )
    }
}

@Composable
fun MainWalletSession(
    user: FirebaseUser,
    onSignOut: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Resolve custom Firestore database ID and repository at the factory boundary
    val databaseId = context.getString(R.string.firestore_database_id)
    val db = remember(databaseId) { FirebaseFirestore.getInstance(databaseId) }
    val repository = remember(db) { WalletRepository(db) }

    val viewModel: WalletViewModel = viewModel(
        key = user.uid,
        factory = viewModelFactory {
            initializer {
                WalletViewModel(repository, user.uid)
            }
        }
    )

    // Ensure profile exists and initial demo data is seeded upon login
    LaunchedEffect(user.uid) {
        try {
            repository.getOrCreateProfile(user)
        } catch (_: Exception) {}
    }

    var currentScreen by remember { mutableStateOf<ActiveScreen>(ActiveScreen.Dashboard) }
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()

    val profileState by viewModel.profileState.collectAsStateWithLifecycle()
    val transactionsState by viewModel.transactionsState.collectAsStateWithLifecycle()
    val notificationsState by viewModel.notificationsState.collectAsStateWithLifecycle()
    val preferencesState by viewModel.preferencesState.collectAsStateWithLifecycle()
    val isProcessing by viewModel.isProcessing.collectAsStateWithLifecycle()

    val userProfile = (profileState as? UiState.Success)?.data
    val transactions = (transactionsState as? UiState.Success)?.data ?: emptyList()
    val notifications = (notificationsState as? UiState.Success)?.data ?: emptyList()
    val preferences = (preferencesState as? UiState.Success)?.data
    val unreadNotifCount = notifications.count { !it.read }

    // Dialog state for payment confirmation
    var pendingConfirmation by remember {
        mutableStateOf<Triple<String, String, Double>?>(null)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (currentScreen is ActiveScreen.Dashboard) {
                NovaBottomNav(
                    currentTab = currentTab,
                    onTabSelected = { tab ->
                        viewModel.selectTab(tab)
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val screen = currentScreen) {
                is ActiveScreen.Dashboard -> {
                    when (currentTab) {
                        BottomNavTab.HOME -> {
                            HomeScreen(
                                userProfile = userProfile,
                                transactions = transactions,
                                unreadNotificationCount = unreadNotifCount,
                                isLoading = profileState is UiState.Loading,
                                onSendMoneyClick = { currentScreen = ActiveScreen.SendMoney },
                                onReceiveMoneyClick = { currentScreen = ActiveScreen.ReceiveMoney },
                                onAddMoneyClick = { currentScreen = ActiveScreen.AddMoney },
                                onMobileLoadClick = { currentScreen = ActiveScreen.MobileLoad },
                                onPayBillsClick = { currentScreen = ActiveScreen.PayBills },
                                onBankTransferClick = { currentScreen = ActiveScreen.SendMoney },
                                onQrClick = { currentScreen = ActiveScreen.ReceiveMoney },
                                onViewAllTransactionsClick = { viewModel.selectTab(BottomNavTab.ACTIVITY) },
                                onTransactionClick = { /* Handled in history */ },
                                onNotificationsClick = { currentScreen = ActiveScreen.Notifications },
                                onProfileClick = { viewModel.selectTab(BottomNavTab.PROFILE) }
                            )
                        }
                        BottomNavTab.PAYMENTS -> {
                            SendMoneyScreen(
                                currentBalance = userProfile?.balance ?: 0.0,
                                isProcessing = isProcessing,
                                onBackClick = { viewModel.selectTab(BottomNavTab.HOME) },
                                onConfirmTransfer = { name, acc, amount, purpose ->
                                    pendingConfirmation = Triple(name, acc, amount)
                                }
                            )
                        }
                        BottomNavTab.ACTIVITY -> {
                            TransactionHistoryScreen(
                                transactions = transactions,
                                onBackClick = { viewModel.selectTab(BottomNavTab.HOME) }
                            )
                        }
                        BottomNavTab.PROFILE -> {
                            ProfileScreen(
                                userProfile = userProfile,
                                preferences = preferences,
                                onSecurityClick = { currentScreen = ActiveScreen.Security },
                                onHelpSupportClick = { currentScreen = ActiveScreen.HelpSupport },
                                onSignOutClick = {
                                    signOutUser(
                                        context = context,
                                        credentialManager = androidx.credentials.CredentialManager.create(context),
                                        scope = coroutineScope,
                                        onComplete = onSignOut
                                    )
                                },
                                onToggleTheme = {
                                    val nextTheme = if (preferences?.theme == "DARK") "LIGHT" else "DARK"
                                    viewModel.updatePreferences(theme = nextTheme)
                                },
                                onToggleLanguage = {
                                    val nextLang = if (preferences?.language == "English") "Urdu" else "English"
                                    viewModel.updatePreferences(language = nextLang)
                                }
                            )
                        }
                    }
                }

                is ActiveScreen.SendMoney -> {
                    BackHandler { currentScreen = ActiveScreen.Dashboard }
                    SendMoneyScreen(
                        currentBalance = userProfile?.balance ?: 0.0,
                        isProcessing = isProcessing,
                        onBackClick = { currentScreen = ActiveScreen.Dashboard },
                        onConfirmTransfer = { name, acc, amount, _ ->
                            pendingConfirmation = Triple(name, acc, amount)
                        }
                    )
                }

                is ActiveScreen.ReceiveMoney -> {
                    BackHandler { currentScreen = ActiveScreen.Dashboard }
                    ReceiveMoneyScreen(
                        userProfile = userProfile,
                        onBackClick = { currentScreen = ActiveScreen.Dashboard }
                    )
                }

                is ActiveScreen.AddMoney -> {
                    BackHandler { currentScreen = ActiveScreen.Dashboard }
                    AddMoneyScreen(
                        currentBalance = userProfile?.balance ?: 0.0,
                        isProcessing = isProcessing,
                        onBackClick = { currentScreen = ActiveScreen.Dashboard },
                        onAddMoneyConfirm = { channel, amount ->
                            viewModel.addMoney(
                                methodTitle = channel,
                                amount = amount,
                                onSuccess = { tx ->
                                    currentScreen = ActiveScreen.PaymentSuccess(tx)
                                },
                                onError = { msg ->
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(msg)
                                    }
                                }
                            )
                        }
                    )
                }

                is ActiveScreen.MobileLoad -> {
                    BackHandler { currentScreen = ActiveScreen.Dashboard }
                    MobileTopUpScreen(
                        currentBalance = userProfile?.balance ?: 0.0,
                        isProcessing = isProcessing,
                        onBackClick = { currentScreen = ActiveScreen.Dashboard },
                        onTopUpConfirm = { operator, number, amount ->
                            viewModel.payMobileLoad(
                                operator = operator,
                                mobileNumber = number,
                                amount = amount,
                                onSuccess = { tx ->
                                    currentScreen = ActiveScreen.PaymentSuccess(tx)
                                },
                                onError = { msg ->
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(msg)
                                    }
                                }
                            )
                        }
                    )
                }

                is ActiveScreen.PayBills -> {
                    BackHandler { currentScreen = ActiveScreen.Dashboard }
                    BillPaymentScreen(
                        currentBalance = userProfile?.balance ?: 0.0,
                        isProcessing = isProcessing,
                        onBackClick = { currentScreen = ActiveScreen.Dashboard },
                        onPayBillConfirm = { company, consumerNumber, amount, billType ->
                            viewModel.payUtilityBill(
                                company = company,
                                consumerNumber = consumerNumber,
                                amount = amount,
                                billType = billType,
                                onSuccess = { tx ->
                                    currentScreen = ActiveScreen.PaymentSuccess(tx)
                                },
                                onError = { msg ->
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(msg)
                                    }
                                }
                            )
                        }
                    )
                }

                is ActiveScreen.Notifications -> {
                    BackHandler { currentScreen = ActiveScreen.Dashboard }
                    NotificationsScreen(
                        notifications = notifications,
                        onBackClick = { currentScreen = ActiveScreen.Dashboard },
                        onNotificationClick = { notif ->
                            viewModel.markNotificationRead(notif.id)
                        },
                        onMarkAllReadClick = {
                            notifications.filter { !it.read }.forEach {
                                viewModel.markNotificationRead(it.id)
                            }
                        }
                    )
                }

                is ActiveScreen.Security -> {
                    BackHandler { currentScreen = ActiveScreen.Dashboard }
                    SecurityScreen(
                        onBackClick = { currentScreen = ActiveScreen.Dashboard }
                    )
                }

                is ActiveScreen.HelpSupport -> {
                    BackHandler { currentScreen = ActiveScreen.Dashboard }
                    HelpSupportScreen(
                        onBackClick = { currentScreen = ActiveScreen.Dashboard }
                    )
                }

                is ActiveScreen.PaymentSuccess -> {
                    BackHandler { currentScreen = ActiveScreen.Dashboard }
                    PaymentSuccessScreen(
                        transaction = screen.transaction,
                        onDoneClick = { currentScreen = ActiveScreen.Dashboard }
                    )
                }
            }
        }
    }

    // Payment Confirmation Dialog
    pendingConfirmation?.let { (recipient, account, amount) ->
        PaymentConfirmationDialog(
            recipientName = recipient,
            recipientAccount = account,
            amount = amount,
            purpose = "Fund Transfer",
            isProcessing = isProcessing,
            onDismiss = { pendingConfirmation = null },
            onConfirm = {
                viewModel.sendMoney(
                    recipientName = recipient,
                    recipientAccount = account,
                    amount = amount,
                    purpose = "Fund Transfer",
                    onSuccess = { tx ->
                        pendingConfirmation = null
                        currentScreen = ActiveScreen.PaymentSuccess(tx)
                    },
                    onError = { err ->
                        pendingConfirmation = null
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(err)
                        }
                    }
                )
            }
        )
    }
}
