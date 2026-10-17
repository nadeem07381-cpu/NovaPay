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
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NovaElectricViolet
import com.example.ui.theme.NovaMintGreen
import com.example.ui.theme.NovaNavyCard
import com.example.ui.theme.NovaNavySurface

data class DepositChannel(val id: String, val title: String, val subtitle: String, val icon: ImageVector)

@Composable
fun AddMoneyScreen(
    currentBalance: Double,
    isProcessing: Boolean,
    onBackClick: () -> Unit,
    onAddMoneyConfirm: (channelTitle: String, amount: Double) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedChannelId by remember { mutableStateOf("card") }
    var amountInput by remember { mutableStateOf("5000") }
    var validationError by remember { mutableStateOf<String?>(null) }

    val channels = remember {
        listOf(
            DepositChannel("card", "Debit / Credit Card", "Visa, Mastercard, PayPak", Icons.Default.CreditCard),
            DepositChannel("bank", "Bank Transfer (1Link)", "Direct from any Pakistani bank", Icons.Default.AccountBalance),
            DepositChannel("agent", "Cash Deposit Agent", "Easypaisa, JazzCash, 100K+ Agents", Icons.Default.Store)
        )
    }

    val quickAmounts = listOf(1000, 2500, 5000, 10000, 25000)
    val scrollState = rememberScrollState()

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
                text = "Add Money",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            Text(
                text = "Select Funding Source",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            channels.forEach { channel ->
                val isSelected = selectedChannelId == channel.id
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) NovaNavyCard else Color(0xFF141930),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = if (isSelected) NovaElectricViolet else Color(0xFF26325A)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { selectedChannelId = channel.id }
                        .testTag("channel_${channel.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(NovaElectricViolet.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = channel.icon,
                                contentDescription = null,
                                tint = NovaElectricViolet,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = channel.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = channel.subtitle,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        RadioButton(
                            selected = isSelected,
                            onClick = { selectedChannelId = channel.id },
                            colors = RadioButtonDefaults.colors(selectedColor = NovaElectricViolet)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Deposit Amount",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(10.dp))

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
                    .testTag("add_money_amount_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NovaElectricViolet,
                    unfocusedBorderColor = Color(0xFF2E3B68),
                    focusedContainerColor = NovaNavyCard,
                    unfocusedContainerColor = NovaNavyCard
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(quickAmounts) { amt ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = NovaNavyCard,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E3B68)),
                        modifier = Modifier.clickable { amountInput = amt.toString() }
                    ) {
                        Text(
                            text = "+Rs. $amt",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFBAC7E5),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            validationError?.let { err ->
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = err, color = Color(0xFFFF5252), fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = {
                    validationError = null
                    val amt = amountInput.toDoubleOrNull()
                    if (amt == null || amt <= 0) {
                        validationError = "Please enter a valid deposit amount."
                        return@Button
                    }
                    val selectedChannel = channels.find { it.id == selectedChannelId } ?: channels.first()
                    onAddMoneyConfirm(selectedChannel.title, amt)
                },
                enabled = !isProcessing,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("add_money_submit_button"),
                colors = ButtonDefaults.buttonColors(containerColor = NovaElectricViolet),
                shape = RoundedCornerShape(14.dp)
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Icon(imageVector = Icons.Default.ElectricBolt, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Add Funds Now", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
