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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.ui.theme.NovaElectricViolet
import com.example.ui.theme.NovaMintGreen
import com.example.ui.theme.NovaNavyCard
import com.example.util.CurrencyFormatter

@Composable
fun PaymentSuccessScreen(
    transaction: TransactionItem,
    onDoneClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Glowing Success Badge
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(NovaMintGreen.copy(alpha = 0.15f))
                .border(2.dp, NovaMintGreen, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Success",
                tint = NovaMintGreen,
                modifier = Modifier.size(44.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Payment Successful!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Your funds have been transferred instantly.",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Receipt Card
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = NovaNavyCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E3B68)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = CurrencyFormatter.formatPkr(transaction.amount),
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0xFF2E3B68))
                )

                Spacer(modifier = Modifier.height(16.dp))

                ReceiptRow("Recipient", transaction.recipientName.ifBlank { transaction.title })
                if (transaction.recipientAccount.isNotBlank()) {
                    ReceiptRow("Account", transaction.recipientAccount)
                }
                ReceiptRow("Reference ID", transaction.referenceId)
                ReceiptRow("Category", transaction.category)
                ReceiptRow("Status", "COMPLETED")
                ReceiptRow("Fee", "Rs. 0.00")
                ReceiptRow("Mode", "Simulated Demo")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Actions
        Button(
            onClick = onDoneClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("done_receipt_button"),
            colors = ButtonDefaults.buttonColors(containerColor = NovaElectricViolet),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                text = "Back to Home",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ReceiptRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color(0xFF94A3B8), fontSize = 13.sp)
        Text(text = value, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}
