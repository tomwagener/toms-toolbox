package io.hammerhead.karooexttemplate.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.hammerhead.karooexttemplate.models.HydrationManager
import io.hammerhead.karooexttemplate.theme.AppTheme

@Composable
fun MainScreen(onBackClick: () -> Unit = {}) {
    val hydrationState by HydrationManager.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar with Back Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "← Back",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2196F3),
                modifier = Modifier
                    .clickable { onBackClick() }
                    .padding(vertical = 6.dp, horizontal = 4.dp)
            )
        }

        Text(
            text = "🧰 Toms Toolbox",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "Karoo Extension Active • v1.2",
            fontSize = 12.sp,
            color = Color(0xFF4CAF50)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Smart Hydration Settings Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "⚙️ Smart Hydration Settings",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Cyan
                )
                Spacer(modifier = Modifier.height(10.dp))

                // 1. Bottle Size
                Text(
                    text = "Bottle Size (Flaschengröße):",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf(500, 600, 750, 1000).forEach { size ->
                        val isSelected = hydrationState.bottleSizeMl == size
                        Button(
                            onClick = {
                                HydrationManager.updateSettings(
                                    baseRateMlPerHour = hydrationState.baseRateMlPerHour,
                                    bottleSizeMl = size,
                                    sipSizeMl = hydrationState.sipSizeMl
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) Color(0xFF2196F3) else Color(0xFF333333)
                            ),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("${size}ml", fontSize = 10.sp, color = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 2. Sip Size
                Text(
                    text = "Per Sip (Pro Schluck - Standard 150ml):",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf(100, 125, 150, 200).forEach { sip ->
                        val isSelected = hydrationState.sipSizeMl == sip
                        Button(
                            onClick = {
                                HydrationManager.updateSettings(
                                    baseRateMlPerHour = hydrationState.baseRateMlPerHour,
                                    bottleSizeMl = hydrationState.bottleSizeMl,
                                    sipSizeMl = sip
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) Color(0xFF00ACC1) else Color(0xFF333333)
                            ),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("${sip}ml", fontSize = 10.sp, color = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 3. Base Hourly Consumption
                Text(
                    text = "Base Hourly Rate (Wasserverbrauch/Std bei 20°C):",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf(400, 500, 600, 750).forEach { rate ->
                        val isSelected = hydrationState.baseRateMlPerHour == rate
                        Button(
                            onClick = {
                                HydrationManager.updateSettings(
                                    baseRateMlPerHour = rate,
                                    bottleSizeMl = hydrationState.bottleSizeMl,
                                    sipSizeMl = hydrationState.sipSizeMl
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) Color(0xFF4CAF50) else Color(0xFF333333)
                            ),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("${rate}ml", fontSize = 10.sp, color = Color.White)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // In-Ride Logging Instructions
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "🚰 In-Ride Logging (Auf dem Fahrprofil)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "1. Banner Pop-up: When alert triggers ('Drink 150ml now!'), tap banner or press Select to log sip.\n" +
                           "2. Hardware Buttons / SRAM AXS: Map a button to action 'log_sip' (+150ml) or 'log_bottle' (+${hydrationState.bottleSizeMl}ml).",
                    fontSize = 12.sp,
                    color = Color.LightGray
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Data Fields Guide
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "⚙️ Data Fields Setup Guide",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "1. Go to Settings ➔ Profiles ➔ Select Profile\n" +
                           "2. Edit Layout ➔ Add Data Field\n" +
                           "3. Choose category 'Toms Toolbox':\n" +
                           "   • Battery Check (OK / LOW: Sensor)\n" +
                           "   • Battery List (Detailed list with runtime)\n" +
                           "   • Smart Hydration (Live fluid loss & timer)",
                    fontSize = 12.sp,
                    color = Color.LightGray
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { onBackClick() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = "← Back to Karoo Menu", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun DashboardDeviceRow(name: String, battery: String, runtime: String, statusColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = name, fontSize = 12.sp, color = Color.White)
        Text(text = "$battery ($runtime)", fontSize = 12.sp, color = statusColor, fontWeight = FontWeight.SemiBold)
    }
}

@Preview(widthDp = 256, heightDp = 426)
@Composable
fun DefaultPreview() {
    AppTheme {
        MainScreen()
    }
}
