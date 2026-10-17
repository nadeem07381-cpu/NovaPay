package com.example.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.UserProfile
import com.example.ui.theme.NovaElectricViolet
import com.example.ui.theme.NovaMintGreen
import com.example.ui.theme.NovaNavyCard
import com.example.ui.theme.NovaNavySurface

@Composable
fun ReceiveMoneyScreen(
    userProfile: UserProfile?,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    var copiedNotice by remember { mutableStateOf<String?>(null) }

    val accountNumber = userProfile?.accountNumber ?: "PK76NOVA0008492019"
    val raastId = userProfile?.phoneNumber ?: "+92 300 8492019"

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
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
                text = "Receive Money",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // QR Code Card
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier
                .size(240.dp)
                .padding(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                // Stylized QR pattern
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val s = size.minDimension
                    val cellSize = s / 7f

                    // Draw corner finder patterns
                    fun drawFinder(x: Float, y: Float) {
                        drawRect(Color.Black, Offset(x, y), Size(cellSize * 2.5f, cellSize * 2.5f))
                        drawRect(Color.White, Offset(x + cellSize * 0.5f, y + cellSize * 0.5f), Size(cellSize * 1.5f, cellSize * 1.5f))
                        drawRect(Color(0xFF10152B), Offset(x + cellSize * 0.8f, y + cellSize * 0.8f), Size(cellSize * 0.9f, cellSize * 0.9f))
                    }

                    drawFinder(0f, 0f)
                    drawFinder(s - cellSize * 2.5f, 0f)
                    drawFinder(0f, s - cellSize * 2.5f)

                    // Data matrix blocks
                    for (i in 0..5) {
                        for (j in 0..5) {
                            if ((i + j) % 2 == 0) {
                                drawRect(
                                    color = if ((i * j) % 3 == 0) Color(0xFF6C5CE7) else Color(0xFF10152B),
                                    topLeft = Offset(cellSize * (i + 1f), cellSize * (j + 1f)),
                                    size = Size(cellSize * 0.8f, cellSize * 0.8f)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Scan this QR code with any banking or Raast app to receive instant payments.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Raast ID Row
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = NovaNavyCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E3B68)),
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    clipboardManager.setText(AnnotatedString(raastId))
                    copiedNotice = "Raast ID copied!"
                }
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Raast ID (Instant Transfer)", fontSize = 11.sp, color = NovaMintGreen)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = raastId, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy Raast ID",
                    tint = NovaMintGreen,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // NovaPay IBAN Row
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = NovaNavyCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E3B68)),
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    clipboardManager.setText(AnnotatedString(accountNumber))
                    copiedNotice = "Account number copied!"
                }
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "NovaPay Account / IBAN", fontSize = 11.sp, color = Color(0xFF94A3B8))
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = accountNumber, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy Account Number",
                    tint = NovaElectricViolet,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        copiedNotice?.let { notice ->
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = notice, color = NovaMintGreen, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                clipboardManager.setText(AnnotatedString("Pay to my NovaPay Wallet:\nRaast ID: $raastId\nIBAN: $accountNumber"))
                copiedNotice = "Payment details copied to clipboard to share!"
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("share_payment_details_button"),
            colors = ButtonDefaults.buttonColors(containerColor = NovaElectricViolet),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Share Payment Details", fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
