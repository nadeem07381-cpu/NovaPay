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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.UserPreferences
import com.example.data.models.UserProfile
import com.example.ui.theme.NovaElectricViolet
import com.example.ui.theme.NovaMintGreen
import com.example.ui.theme.NovaNavyCard
import com.example.ui.theme.NovaNavySurface

@Composable
fun ProfileScreen(
    userProfile: UserProfile?,
    preferences: UserPreferences?,
    onSecurityClick: () -> Unit,
    onHelpSupportClick: () -> Unit,
    onSignOutClick: () -> Unit,
    onToggleTheme: () -> Unit,
    onToggleLanguage: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Text(
            text = "My Profile",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Profile Identity Card
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
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(NovaNavySurface)
                        .border(2.dp, NovaElectricViolet, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Avatar",
                        tint = NovaElectricViolet,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = userProfile?.displayName ?: "NovaPay Customer",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = userProfile?.email ?: "customer@novapay.pk",
                    fontSize = 13.sp,
                    color = Color(0xFF94A3B8)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(NovaMintGreen.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = NovaMintGreen,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Verified Biometric Tier 1",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NovaMintGreen
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Wallet Limits Card
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = NovaNavyCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E3B68)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Monthly Sending Limit", fontSize = 13.sp, color = Color(0xFFBAC7E5))
                    Text("Rs. 125,000 / Rs. 500,000", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { 0.25f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape),
                    color = NovaElectricViolet,
                    trackColor = Color(0xFF141930)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "75% monthly limit remaining. Upgradable via NADRA Bio-Metric.",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Settings & Security",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Menu Items
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = NovaNavyCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E3B68)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                SettingsRow(
                    icon = Icons.Default.Security,
                    title = "Security & Privacy",
                    subtitle = "Biometrics, PIN, Active Sessions",
                    onClick = onSecurityClick
                )
                SettingsRow(
                    icon = Icons.Default.Palette,
                    title = "Theme Preference",
                    subtitle = "Current: ${preferences?.theme ?: "Dark Mode"}",
                    onClick = onToggleTheme
                )
                SettingsRow(
                    icon = Icons.Default.Language,
                    title = "App Language",
                    subtitle = "Current: ${preferences?.language ?: "English"}",
                    onClick = onToggleLanguage
                )
                SettingsRow(
                    icon = Icons.AutoMirrored.Filled.HelpOutline,
                    title = "Help & 24/7 Support",
                    subtitle = "FAQs, Chat, Helpline 042-111-NOVAPAY",
                    onClick = onHelpSupportClick
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Sign Out Button
        Button(
            onClick = onSignOutClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("profile_sign_out_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2E1A22),
                contentColor = Color(0xFFFF6B6B)
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF4D4D).copy(alpha = 0.5f)),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Sign Out", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }

        Spacer(modifier = Modifier.height(90.dp))
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(NovaElectricViolet.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = NovaElectricViolet, modifier = Modifier.size(20.dp))
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            Text(text = subtitle, fontSize = 11.sp, color = Color(0xFF94A3B8))
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = Color(0xFF64748B),
            modifier = Modifier.size(14.dp)
        )
    }
}
