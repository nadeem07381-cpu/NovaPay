package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NovaElectricViolet
import com.example.ui.theme.NovaMintGreen
import com.example.ui.theme.NovaNavyCard
import com.example.ui.theme.NovaNavySurface
import com.example.util.CurrencyFormatter

enum class TransferMethod(val label: String, val hint: String) {
    NOVAPAY("NovaPay Wallet", "0300 1234567"),
    BANK("Bank Account", "IBAN: PK.. or Account #"),
    RAAST("Raast ID", "Mobile or Raast IBAN"),
    CNIC("CNIC Transfer", "35201-1234567-1")
}

data class RecentContact(val name: String, val account: String, val method: TransferMethod)

@Composable
fun SendMoneyScreen(
    currentBalance: Double,
    isProcessing: Boolean,
    onBackClick: () -> Unit,
    onConfirmTransfer: (recipientName: String, recipientAccount: String, amount: Double, purpose: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedMethod by remember { mutableStateOf(TransferMethod.NOVAPAY) }
    var recipientName by remember { mutableStateOf("") }
    var recipientAccount by remember { mutableStateOf("") }
    var amountInput by remember { mutableStateOf("") }
    var selectedPurpose by remember { mutableStateOf("Personal Transfer") }
    var validationError by remember { mutableStateOf<String?>(null) }

    val recentContacts = remember {
        listOf(
            RecentContact("Zainab Bibi", "0312 9841234", TransferMethod.NOVAPAY),
            RecentContact("Hamza Khan", "0300 8492019", TransferMethod.RAAST),
            RecentContact("Usman Tariq", "PK76MEZN000184920", TransferMethod.BANK),
            RecentContact("Ayesha Noor", "35202-9842103-2", TransferMethod.CNIC)
        )
    }

    val quickAmounts = listOf(500, 1000, 2500, 5000, 10000)
    val purposes = listOf("Personal Transfer", "Family Support", "Bill Split", "Online Shopping", "Gift")

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        // Top App Bar
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
                text = "Send Money",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Balance Notice
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = NovaNavyCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E3B68)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Available Balance",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                    Text(
                        text = CurrencyFormatter.formatPkr(currentBalance),
                        color = NovaMintGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Method Selector
            Text(
                text = "Transfer Channel",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(TransferMethod.entries) { method ->
                    FilterChip(
                        selected = selectedMethod == method,
                        onClick = { selectedMethod = method },
                        label = { Text(method.label, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NovaElectricViolet,
                            selectedLabelColor = Color.White,
                            containerColor = NovaNavyCard,
                            labelColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Recent Contacts
            Text(
                text = "Recent Contacts",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(recentContacts) { contact ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                recipientName = contact.name
                                recipientAccount = contact.account
                                selectedMethod = contact.method
                            }
                            .padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(NovaNavyCard)
                                .border(1.dp, NovaElectricViolet, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = contact.name.take(1),
                                color = NovaElectricViolet,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = contact.name.split(" ").firstOrNull() ?: contact.name,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Recipient Account Input
            OutlinedTextField(
                value = recipientAccount,
                onValueChange = { recipientAccount = it },
                label = { Text("${selectedMethod.label} (${selectedMethod.hint})") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("recipient_account_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NovaElectricViolet,
                    unfocusedBorderColor = Color(0xFF2E3B68),
                    focusedContainerColor = NovaNavyCard,
                    unfocusedContainerColor = NovaNavyCard
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Recipient Name Input
            OutlinedTextField(
                value = recipientName,
                onValueChange = { recipientName = it },
                label = { Text("Recipient Name") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("recipient_name_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NovaElectricViolet,
                    unfocusedBorderColor = Color(0xFF2E3B68),
                    focusedContainerColor = NovaNavyCard,
                    unfocusedContainerColor = NovaNavyCard
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Amount Input
            OutlinedTextField(
                value = amountInput,
                onValueChange = { amountInput = it },
                label = { Text("Amount in PKR") },
                leadingIcon = {
                    Text(
                        text = "Rs.",
                        color = NovaMintGreen,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("send_amount_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NovaElectricViolet,
                    unfocusedBorderColor = Color(0xFF2E3B68),
                    focusedContainerColor = NovaNavyCard,
                    unfocusedContainerColor = NovaNavyCard
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Amount Chips
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(quickAmounts) { amt ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = NovaNavyCard,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E3B68)),
                        modifier = Modifier.clickable { amountInput = amt.toString() }
                    ) {
                        Text(
                            text = "+Rs. $amt",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFBAC7E5),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Transfer Purpose
            Text(
                text = "Purpose",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(purposes) { p ->
                    FilterChip(
                        selected = selectedPurpose == p,
                        onClick = { selectedPurpose = p },
                        label = { Text(p, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NovaNavySurface,
                            selectedLabelColor = NovaMintGreen,
                            containerColor = NovaNavyCard
                        )
                    )
                }
            }

            // Fee Notice
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(NovaMintGreen.copy(alpha = 0.12f))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = null,
                    tint = NovaMintGreen,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Transfer Fee: Rs. 0.00 (Instant Zero Fee)",
                    color = NovaMintGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            validationError?.let { err ->
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = err,
                    color = Color(0xFFFF5252),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    validationError = null
                    val amt = amountInput.toDoubleOrNull()
                    if (recipientAccount.isBlank()) {
                        validationError = "Please enter recipient account or phone number."
                        return@Button
                    }
                    if (recipientName.isBlank()) {
                        recipientName = "Beneficiary"
                    }
                    if (amt == null || amt <= 0) {
                        validationError = "Please enter a valid amount greater than 0."
                        return@Button
                    }
                    if (amt > currentBalance) {
                        validationError = "Insufficient wallet balance."
                        return@Button
                    }
                    onConfirmTransfer(recipientName, recipientAccount, amt, selectedPurpose)
                },
                enabled = !isProcessing,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("send_money_submit_button"),
                colors = ButtonDefaults.buttonColors(containerColor = NovaElectricViolet),
                shape = RoundedCornerShape(14.dp)
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = "Continue Transfer",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
