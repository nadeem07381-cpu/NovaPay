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
import androidx.compose.material.icons.filled.Check
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

data class MobileBundle(val name: String, val amount: Double, val validity: String, val benefits: String)

@Composable
fun MobileTopUpScreen(
    currentBalance: Double,
    isProcessing: Boolean,
    onBackClick: () -> Unit,
    onTopUpConfirm: (operator: String, mobileNumber: String, amount: Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val operators = listOf("Jazz", "Telenor", "Zong", "Ufone")
    var selectedOperator by remember { mutableStateOf("Jazz") }
    var mobileNumber by remember { mutableStateOf("") }
    var amountInput by remember { mutableStateOf("500") }
    var isPrepaid by remember { mutableStateOf(true) }
    var validationError by remember { mutableStateOf<String?>(null) }

    val operatorColors = mapOf(
        "Jazz" to Color(0xFFD32F2F),
        "Telenor" to Color(0xFF0288D1),
        "Zong" to Color(0xFF388E3C),
        "Ufone" to Color(0xFFFF8F00)
    )

    val bundles = remember(selectedOperator) {
        when (selectedOperator) {
            "Jazz" -> listOf(
                MobileBundle("Weekly Super Max", 420.0, "7 Days", "30 GB Data + 6000 Jazz Mins"),
                MobileBundle("Monthly Super Card", 1150.0, "30 Days", "20 GB Data + 3000 All Net Mins"),
                MobileBundle("Daily Extreme", 75.0, "1 Day", "2 GB Data (12 AM - 12 PM)")
            )
            "Zong" -> listOf(
                MobileBundle("Weekly HLO", 450.0, "7 Days", "12 GB Data + 5000 Zong Mins"),
                MobileBundle("Monthly Digital Max", 1400.0, "30 Days", "100 GB Data + 1000 Off-net"),
                MobileBundle("Daily Shandaar", 50.0, "1 Day", "Unlimited Zong + 500 MB")
            )
            "Telenor" -> listOf(
                MobileBundle("EasyCard 950", 950.0, "30 Days", "18 GB + 3000 Telenor Mins"),
                MobileBundle("Weekly Mega Plus", 380.0, "7 Days", "15 GB Data (1 AM - 11 AM)"),
                MobileBundle("Monthly Ultra", 1250.0, "30 Days", "50 GB Data + 500 Off-net")
            )
            else -> listOf(
                MobileBundle("Super Card Gold", 1499.0, "30 Days", "35 GB + Unlimited Mins"),
                MobileBundle("Weekly Super Internet", 299.0, "7 Days", "7 GB High Speed Data"),
                MobileBundle("Monthly Heavy", 999.0, "30 Days", "20 GB Data + 500 Off-net")
            )
        }
    }

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
                text = "Mobile Load & Bundles",
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
            // Operator Selector
            Text(
                text = "Mobile Operator",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                operators.forEach { op ->
                    val isSelected = selectedOperator == op
                    val opColor = operatorColors[op] ?: NovaElectricViolet
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) opColor.copy(alpha = 0.2f) else NovaNavyCard,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) opColor else Color(0xFF2E3B68)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedOperator = op }
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(vertical = 12.dp)
                        ) {
                            Text(
                                text = op,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (isSelected) opColor else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Mobile Number Input
            OutlinedTextField(
                value = mobileNumber,
                onValueChange = { mobileNumber = it },
                label = { Text("Mobile Number (e.g. 0300 1234567)") },
                leadingIcon = {
                    Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = NovaElectricViolet)
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("topup_mobile_number_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NovaElectricViolet,
                    unfocusedBorderColor = Color(0xFF2E3B68),
                    focusedContainerColor = NovaNavyCard,
                    unfocusedContainerColor = NovaNavyCard
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Prepaid / Postpaid
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = isPrepaid,
                    onClick = { isPrepaid = true },
                    label = { Text("Prepaid Load") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NovaElectricViolet,
                        selectedLabelColor = Color.White
                    )
                )
                FilterChip(
                    selected = !isPrepaid,
                    onClick = { isPrepaid = false },
                    label = { Text("Postpaid Bill") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NovaElectricViolet,
                        selectedLabelColor = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Bundles
            Text(
                text = "Popular Packages",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))

            bundles.forEach { bundle ->
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = NovaNavyCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E3B68)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { amountInput = bundle.amount.toInt().toString() }
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = bundle.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${bundle.validity} • ${bundle.benefits}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = CurrencyFormatter.formatPkr(bundle.amount),
                            color = NovaMintGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Custom Recharge Amount
            OutlinedTextField(
                value = amountInput,
                onValueChange = { amountInput = it },
                label = { Text("Recharge Amount in PKR") },
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
                    .testTag("topup_amount_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NovaElectricViolet,
                    unfocusedBorderColor = Color(0xFF2E3B68),
                    focusedContainerColor = NovaNavyCard,
                    unfocusedContainerColor = NovaNavyCard
                )
            )

            validationError?.let { err ->
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = err, color = Color(0xFFFF5252), fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    validationError = null
                    val amt = amountInput.toDoubleOrNull()
                    if (mobileNumber.length < 10) {
                        validationError = "Please enter a valid 11-digit mobile number."
                        return@Button
                    }
                    if (amt == null || amt <= 0) {
                        validationError = "Please enter a valid amount."
                        return@Button
                    }
                    if (amt > currentBalance) {
                        validationError = "Insufficient wallet balance."
                        return@Button
                    }
                    onTopUpConfirm(selectedOperator, mobileNumber, amt)
                },
                enabled = !isProcessing,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("topup_submit_button"),
                colors = ButtonDefaults.buttonColors(containerColor = NovaElectricViolet),
                shape = RoundedCornerShape(14.dp)
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = "Recharge Now",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
