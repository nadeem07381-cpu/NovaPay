package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.TransactionItem
import com.example.data.models.UserProfile
import com.example.ui.components.NovaBalanceCard
import com.example.ui.components.NovaTopBar
import com.example.ui.components.QuickActionButton
import com.example.ui.components.TransactionListItem
import com.example.ui.theme.NovaElectricViolet
import com.example.ui.theme.NovaMintGreen
import com.example.ui.theme.NovaNavyCard
import com.example.util.CurrencyFormatter

@Composable
fun HomeScreen(
    userProfile: UserProfile?,
    transactions: List<TransactionItem>,
    unreadNotificationCount: Int,
    isLoading: Boolean,
    onSendMoneyClick: () -> Unit,
    onReceiveMoneyClick: () -> Unit,
    onAddMoneyClick: () -> Unit,
    onMobileLoadClick: () -> Unit,
    onPayBillsClick: () -> Unit,
    onBankTransferClick: () -> Unit,
    onQrClick: () -> Unit,
    onViewAllTransactionsClick: () -> Unit,
    onTransactionClick: (TransactionItem) -> Unit,
    onNotificationsClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Header
        NovaTopBar(
            displayName = userProfile?.displayName ?: "User",
            unreadNotificationCount = unreadNotificationCount,
            onNotificationsClick = onNotificationsClick,
            onProfileClick = onProfileClick
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Balance Card
            item {
                NovaBalanceCard(
                    userProfile = userProfile,
                    onAddMoneyClick = onAddMoneyClick,
                    onSendMoneyClick = onSendMoneyClick
                )
            }

            // Quick Actions Title
            item {
                Text(
                    text = "Quick Services",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // Quick Actions Grid (2 rows of 3)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        QuickActionButton(
                            icon = Icons.Default.NorthEast,
                            label = "Send",
                            onClick = onSendMoneyClick,
                            iconTint = NovaElectricViolet,
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionButton(
                            icon = Icons.Default.ArrowDownward,
                            label = "Receive",
                            onClick = onReceiveMoneyClick,
                            iconTint = NovaMintGreen,
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionButton(
                            icon = Icons.Default.ElectricBolt,
                            label = "Add Money",
                            onClick = onAddMoneyClick,
                            iconTint = Color(0xFFFFB300),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        QuickActionButton(
                            icon = Icons.Default.PhoneAndroid,
                            label = "Mobile Load",
                            onClick = onMobileLoadClick,
                            iconTint = Color(0xFF26C6DA),
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionButton(
                            icon = Icons.Default.Receipt,
                            label = "Pay Bills",
                            onClick = onPayBillsClick,
                            iconTint = Color(0xFFFF7043),
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionButton(
                            icon = Icons.Default.QrCodeScanner,
                            label = "Scan QR",
                            onClick = onQrClick,
                            iconTint = NovaElectricViolet,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Spending Summary / Activity Snapshot Card
            item {
                val totalSpent = transactions.filter { !it.isCredit }.sumOf { it.amount }
                val totalReceived = transactions.filter { it.isCredit }.sumOf { it.amount }

                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = NovaNavyCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E3B68)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Monthly Spent",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = CurrencyFormatter.formatPkr(totalSpent),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Box(
                            modifier = Modifier
                                .height(32.dp)
                                .width(1.dp)
                                .background(Color(0xFF2E3B68))
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Received",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = CurrencyFormatter.formatPkr(totalReceived),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = NovaMintGreen
                            )
                        }
                    }
                }
            }

            // Recent Transactions Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Activity",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    TextButton(onClick = onViewAllTransactionsClick) {
                        Text(
                            text = "View All",
                            color = NovaElectricViolet,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            if (isLoading && transactions.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = NovaElectricViolet,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            } else if (transactions.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(NovaNavyCard.copy(alpha = 0.5f))
                            .padding(28.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "No transactions yet",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Your sent and received payments will appear here.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            } else {
                items(transactions.take(5)) { tx ->
                    TransactionListItem(
                        transaction = tx,
                        onClick = { onTransactionClick(tx) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}
