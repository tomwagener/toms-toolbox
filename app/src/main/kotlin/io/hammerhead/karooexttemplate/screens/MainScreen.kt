package io.hammerhead.karooexttemplate.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.style.TextAlign
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
            text = "Karoo Extension Active • v1.3",
            fontSize = 12.sp,
            color = Color(0xFF4CAF50)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Smart Hydration Settings Section (Redesigned Stepper Controls)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "⚙️ Smart Hydration Settings",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Cyan
                )
                Spacer(modifier = Modifier.height(12.dp))

                // 1. Bottle Size Stepper
                StepperSettingControl(
                    title = "Bottle Size (Flaschengröße):",
                    currentValueText = "${hydrationState.bottleSizeMl} ml",
                    accentColor = Color(0xFF2196F3),
                    onDecrement = {
                        val newSize = (hydrationState.bottleSizeMl - 50).coerceAtLeast(300)
                        HydrationManager.updateSettings(
                            baseRateMlPerHour = hydrationState.baseRateMlPerHour,
                            bottleSizeMl = newSize,
                            sipSizeMl = hydrationState.sipSizeMl
                        )
                    },
                    onIncrement = {
                        val newSize = (hydrationState.bottleSizeMl + 50).coerceAtMost(1500)
                        HydrationManager.updateSettings(
                            baseRateMlPerHour = hydrationState.baseRateMlPerHour,
                            bottleSizeMl = newSize,
                            sipSizeMl = hydrationState.sipSizeMl
                        )
                    },
                    presets = listOf(500, 600, 750, 1000).map { size ->
                        PresetOption("${size}ml", isSelected = hydrationState.bottleSizeMl == size) {
                            HydrationManager.updateSettings(
                                baseRateMlPerHour = hydrationState.baseRateMlPerHour,
                                bottleSizeMl = size,
                                sipSizeMl = hydrationState.sipSizeMl
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 2. Sip Size Stepper
                StepperSettingControl(
                    title = "Per Sip (Pro Schluck):",
                    currentValueText = "${hydrationState.sipSizeMl} ml",
                    accentColor = Color(0xFF00ACC1),
                    onDecrement = {
                        val newSip = (hydrationState.sipSizeMl - 25).coerceAtLeast(50)
                        HydrationManager.updateSettings(
                            baseRateMlPerHour = hydrationState.baseRateMlPerHour,
                            bottleSizeMl = hydrationState.bottleSizeMl,
                            sipSizeMl = newSip
                        )
                    },
                    onIncrement = {
                        val newSip = (hydrationState.sipSizeMl + 25).coerceAtMost(300)
                        HydrationManager.updateSettings(
                            baseRateMlPerHour = hydrationState.baseRateMlPerHour,
                            bottleSizeMl = hydrationState.bottleSizeMl,
                            sipSizeMl = newSip
                        )
                    },
                    presets = listOf(100, 125, 150, 200).map { sip ->
                        PresetOption("${sip}ml", isSelected = hydrationState.sipSizeMl == sip) {
                            HydrationManager.updateSettings(
                                baseRateMlPerHour = hydrationState.baseRateMlPerHour,
                                bottleSizeMl = hydrationState.bottleSizeMl,
                                sipSizeMl = sip
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 3. Base Hourly Consumption Stepper
                StepperSettingControl(
                    title = "Base Hourly Rate (Verbrauch/Std bei 20°C):",
                    currentValueText = "${hydrationState.baseRateMlPerHour} ml/h",
                    accentColor = Color(0xFF4CAF50),
                    onDecrement = {
                        val newRate = (hydrationState.baseRateMlPerHour - 50).coerceAtLeast(200)
                        HydrationManager.updateSettings(
                            baseRateMlPerHour = newRate,
                            bottleSizeMl = hydrationState.bottleSizeMl,
                            sipSizeMl = hydrationState.sipSizeMl
                        )
                    },
                    onIncrement = {
                        val newRate = (hydrationState.baseRateMlPerHour + 50).coerceAtMost(1500)
                        HydrationManager.updateSettings(
                            baseRateMlPerHour = newRate,
                            bottleSizeMl = hydrationState.bottleSizeMl,
                            sipSizeMl = hydrationState.sipSizeMl
                        )
                    },
                    presets = listOf(400, 500, 600, 750).map { rate ->
                        PresetOption("${rate}ml", isSelected = hydrationState.baseRateMlPerHour == rate) {
                            HydrationManager.updateSettings(
                                baseRateMlPerHour = rate,
                                bottleSizeMl = hydrationState.bottleSizeMl,
                                sipSizeMl = hydrationState.sipSizeMl
                            )
                        }
                    }
                )
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
                    text = "1. Auto-Log Banner: When alert triggers ('Drink ${hydrationState.sipSizeMl}ml now!'), it plays a beep and automatically logs after 8s.\n" +
                           "2. Touch Data Field: Tap '🚰 SMART HYDRATION' field on ride screen to log sip immediately.\n" +
                           "3. Hardware Buttons / SRAM AXS: Map a button to action 'log_sip' (+${hydrationState.sipSizeMl}ml).",
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

data class PresetOption(
    val label: String,
    val isSelected: Boolean,
    val onClick: () -> Unit
)

@Composable
fun StepperSettingControl(
    title: String,
    currentValueText: String,
    accentColor: Color,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    presets: List<PresetOption>
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(6.dp))

        // Stepper Control Row (- VALUE +)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Decrement Button (-)
            Button(
                onClick = onDecrement,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF333333)),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier
                    .width(48.dp)
                    .height(42.dp)
            ) {
                Text("-", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            // Value Text (Centered, Bold)
            Text(
                text = currentValueText,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = accentColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )

            // Increment Button (+)
            Button(
                onClick = onIncrement,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF333333)),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier
                    .width(48.dp)
                    .height(42.dp)
            ) {
                Text("+", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Quick Preset Chips Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            presets.forEach { preset ->
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { preset.onClick() },
                    shape = RoundedCornerShape(6.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (preset.isSelected) accentColor else Color(0xFF2A2A2A)
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = preset.label,
                            fontSize = 11.sp,
                            fontWeight = if (preset.isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = Color.White
                        )
                    }
                }
            }
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
