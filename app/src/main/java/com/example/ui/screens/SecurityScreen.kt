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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NovaElectricViolet
import com.example.ui.theme.NovaMintGreen
import com.example.ui.theme.NovaNavyCard
import com.example.ui.theme.NovaNavySurface

@Composable
fun SecurityScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var biometricEnabled by remember { mutableStateOf(true) }
    var requirePinForPayments by remember { mutableStateOf(true) }
    var notifyOnLogins by remember { mutableStateOf(true) }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 12.dp)
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
                text = "Security & Privacy",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Security Shield Banner
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = NovaNavyCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E3B68)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(NovaMintGreen.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = NovaMintGreen,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text("Fintech Shield Active", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                    Text("State Bank of Pakistan regulations & 256-bit AES encryption compliant.", fontSize = 11.sp, color = Color(0xFF94A3B8))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Authentication Controls",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        Surface(
            shape = RoundedCornerShape(18.dp),
            color = NovaNavyCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E3B68)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                ToggleRow(
                    icon = Icons.Default.Fingerprint,
                    title = "Biometric Login",
                    subtitle = "Unlock wallet using Fingerprint / Face ID",
                    checked = biometricEnabled,
                    onCheckedChange = { biometricEnabled = it }
                )
                ToggleRow(
                    icon = Icons.Default.Lock,
                    title = "Transaction PIN",
                    subtitle = "Require 4-digit PIN for money transfers",
                    checked = requirePinForPayments,
                    onCheckedChange = { requirePinForPayments = it }
                )
                ToggleRow(
                    icon = Icons.Default.PhoneAndroid,
                    title = "New Device Alerts",
                    subtitle = "Instant alert when logging in from new phone",
                    checked = notifyOnLogins,
                    onCheckedChange = { notifyOnLogins = it }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Active Sessions",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(10.dp))

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = NovaNavyCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E3B68)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Current Android Device", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                    Text("Active Session • Lahore, Pakistan", fontSize = 12.sp, color = Color(0xFF94A3B8))
                }
                Text("This Phone", color = NovaMintGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun ToggleRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(NovaElectricViolet.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = NovaElectricViolet, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                Text(text = subtitle, fontSize = 11.sp, color = Color(0xFF94A3B8))
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = NovaMintGreen,
                uncheckedThumbColor = Color(0xFF94A3B8),
                uncheckedTrackColor = Color(0xFF141930)
            )
        )
    }
}
