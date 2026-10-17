package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.TransactionItem
import com.example.ui.components.TransactionListItem
import com.example.ui.theme.NovaElectricViolet
import com.example.ui.theme.NovaMintGreen
import com.example.ui.theme.NovaNavyCard
import com.example.ui.theme.NovaNavySurface
import com.example.util.CurrencyFormatter
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun TransactionHistoryScreen(
    transactions: List<TransactionItem>,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    var selectedTransaction by remember { mutableStateOf<TransactionItem?>(null) }

    val filterOptions = listOf("All", "Sent", "Received", "Bills", "Mobile Load")

    val filteredTransactions = remember(transactions, searchQuery, selectedFilter) {
        transactions.filter { tx ->
            val matchesFilter = when (selectedFilter) {
                "Sent" -> tx.type == "SEND"
                "Received" -> tx.type == "RECEIVE" || tx.type == "ADD_MONEY"
                "Bills" -> tx.type == "BILL"
                "Mobile Load" -> tx.type == "TOPUP"
                else -> true
            }
            val matchesSearch = searchQuery.isBlank() ||
                    tx.title.contains(searchQuery, ignoreCase = true) ||
                    tx.recipientName.contains(searchQuery, ignoreCase = true) ||
                    tx.referenceId.contains(searchQuery, ignoreCase = true)

            matchesFilter && matchesSearch
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        // App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(NovaNavySurface)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Transaction Activity",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Search Field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by name, account or reference #") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = NovaElectricViolet)
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .testTag("tx_history_search_input"),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NovaElectricViolet,
                unfocusedBorderColor = Color(0xFF2E3B68),
                focusedContainerColor = NovaNavyCard,
                unfocusedContainerColor = NovaNavyCard
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Filter Chips
        LazyRow(
            modifier = Modifier.padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filterOptions) { filter ->
                val isSelected = selectedFilter == filter
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedFilter = filter },
                    label = { Text(filter, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NovaElectricViolet,
                        selectedLabelColor = Color.White,
                        containerColor = NovaNavyCard
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (filteredTransactions.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                tint = Color(0xFF64748B),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No transactions found",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "Try adjusting your search or filter tags.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            } else {
                items(filteredTransactions) { tx ->
                    TransactionListItem(
                        transaction = tx,
                        onClick = { selectedTransaction = tx }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }

    // Transaction Details Dialog
    selectedTransaction?.let { tx ->
        val formattedDate = tx.createdAt?.toDate()?.let { date ->
            val sdf = SimpleDateFormat("dd MMMM yyyy, hh:mm:ss a", Locale.getDefault())
            sdf.format(date)
        } ?: "Recent"

        AlertDialog(
            onDismissRequest = { selectedTransaction = null },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(NovaNavyCard),
            title = {
                Text(
                    text = "Transaction Details",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Column {
                    Text(
                        text = CurrencyFormatter.formatPkr(tx.amount),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (tx.isCredit) NovaMintGreen else Color.White
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    DetailItem("Title", tx.title)
                    if (tx.recipientName.isNotBlank()) {
                        DetailItem("Counterparty", tx.recipientName)
                    }
                    if (tx.recipientAccount.isNotBlank()) {
                        DetailItem("Account", tx.recipientAccount)
                    }
                    DetailItem("Category", tx.category)
                    DetailItem("Reference ID", tx.referenceId)
                    DetailItem("Date & Time", formattedDate)
                    DetailItem("Status", tx.status)
                    DetailItem("Mode", "Verified Demo Wallet")
                }
            },
            confirmButton = {
                Button(
                    onClick = { selectedTransaction = null },
                    colors = ButtonDefaults.buttonColors(containerColor = NovaElectricViolet)
                ) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
private fun DetailItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color(0xFF94A3B8), fontSize = 12.sp)
        Text(text = value, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}
