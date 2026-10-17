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
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Wifi
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

data class BillCategory(val id: String, val title: String)

@Composable
fun BillPaymentScreen(
    currentBalance: Double,
    isProcessing: Boolean,
    onBackClick: () -> Unit,
    onPayBillConfirm: (company: String, consumerNumber: String, amount: Double, billType: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = remember {
        listOf(
            BillCategory("electricity", "Electricity"),
            BillCategory("gas", "Gas"),
            BillCategory("internet", "Internet"),
            BillCategory("water", "Water")
        )
    }

    var selectedCategoryId by remember { mutableStateOf("electricity") }
    var selectedCompany by remember { mutableStateOf("LESCO") }
    var consumerNumber by remember { mutableStateOf("04-11223-098231") }
    var billFetched by remember { mutableStateOf(false) }
    var billAmount by remember { mutableStateOf(12450.0) }
    var validationError by remember { mutableStateOf<String?>(null) }

    val companies = remember(selectedCategoryId) {
        when (selectedCategoryId) {
            "electricity" -> listOf("LESCO (Lahore)", "K-Electric (Karachi)", "IESCO (Islamabad)", "GEPCO (Gujranwala)", "FESCO")
            "gas" -> listOf("SNGPL (Punjab & KP)", "SSGC (Sindh & Balochistan)")
            "internet" -> listOf("PTCL Broadband", "Nayatel", "StormFiber", "Transworld")
            else -> listOf("WASA Lahore", "WASA Rawalpindi", "KWSB Karachi")
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
                text = "Pay Utility Bills",
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
            // Category Chips
            Text(
                text = "Utility Category",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { cat ->
                    val isSelected = selectedCategoryId == cat.id
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedCategoryId = cat.id
                            selectedCompany = when (cat.id) {
                                "gas" -> "SNGPL (Punjab & KP)"
                                "internet" -> "PTCL Broadband"
                                "water" -> "WASA Lahore"
                                else -> "LESCO (Lahore)"
                            }
                            billFetched = false
                        },
                        label = { Text(cat.title, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NovaElectricViolet,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Company Selector
            Text(
                text = "Company / Provider",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(companies) { company ->
                    val isSelected = selectedCompany == company
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) NovaElectricViolet.copy(alpha = 0.2f) else NovaNavyCard,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) NovaElectricViolet else Color(0xFF2E3B68)
                        ),
                        modifier = Modifier.clickable {
                            selectedCompany = company
                            billFetched = false
                        }
                    ) {
                        Text(
                            text = company,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSelected) NovaElectricViolet else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Consumer Number Input
            OutlinedTextField(
                value = consumerNumber,
                onValueChange = {
                    consumerNumber = it
                    billFetched = false
                },
                label = { Text("Consumer / Account Reference Number") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("consumer_number_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NovaElectricViolet,
                    unfocusedBorderColor = Color(0xFF2E3B68),
                    focusedContainerColor = NovaNavyCard,
                    unfocusedContainerColor = NovaNavyCard
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = {
                    if (consumerNumber.length < 6) {
                        validationError = "Please enter a valid consumer number."
                    } else {
                        validationError = null
                        billFetched = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("fetch_bill_button"),
                colors = ButtonDefaults.buttonColors(containerColor = NovaNavyCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, NovaElectricViolet),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Fetch Bill Details", color = NovaElectricViolet, fontWeight = FontWeight.Bold)
            }

            // Bill Summary Details Card
            if (billFetched) {
                Spacer(modifier = Modifier.height(20.dp))

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = NovaNavyCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E3B68)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Consumer Name", color = Color(0xFF94A3B8), fontSize = 12.sp)
                            Text("M. Tariq Khan", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Billing Month", color = Color(0xFF94A3B8), fontSize = 12.sp)
                            Text("October 2026", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Due Date", color = Color(0xFF94A3B8), fontSize = 12.sp)
                            Text("28 Oct 2026", color = Color(0xFFFF9800), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Status", color = Color(0xFF94A3B8), fontSize = 12.sp)
                            Text("UNPAID", color = Color(0xFFFF5252), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFF2E3B68)))
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Payable Amount", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(CurrencyFormatter.formatPkr(billAmount), color = NovaMintGreen, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                validationError?.let { err ->
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = err, color = Color(0xFFFF5252), fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (billAmount > currentBalance) {
                            validationError = "Insufficient wallet balance."
                        } else {
                            onPayBillConfirm(selectedCompany, consumerNumber, billAmount, selectedCategoryId)
                        }
                    },
                    enabled = !isProcessing,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("pay_bill_submit_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = NovaElectricViolet),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Pay Bill Now", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
