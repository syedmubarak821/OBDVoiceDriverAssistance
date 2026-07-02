package com.example.obdvoiceassist.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.obdvoiceassist.alerts.AlertManager
import com.example.obdvoiceassist.localization.AppStrings
import com.example.obdvoiceassist.models.AlertLevel
import com.example.obdvoiceassist.models.AppLanguage
import com.example.obdvoiceassist.models.ObdLiveData
import com.example.obdvoiceassist.models.VehicleAlert

data class DashboardItem(
    val title: String,
    val value: String,
    val unit: String,
    val min: Double,
    val max: Double,
    val current: Double?
)

@Composable
fun DashboardScreen(
    strings: AppStrings.Strings,
    statusText: String,
    obdLiveData: ObdLiveData,
    isLiveReading: Boolean,
    selectedLanguage: AppLanguage,
    onLanguageToggleClick: () -> Unit,
    onStartLiveClick: () -> Unit,
    onStopLiveClick: () -> Unit,
    onBackToBluetoothClick: () -> Unit
) {
    val dashboardItems = buildDashboardItems(obdLiveData, strings)
    val vehicleAlert = AlertManager.getAlert(obdLiveData, selectedLanguage)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF020617),
                        Color(0xFF07111F),
                        Color(0xFF0F172A)
                    )
                )
            )
            .padding(18.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(28.dp))

            DashboardHeader(
                strings = strings,
                statusText = statusText,
                isLiveReading = isLiveReading
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onLanguageToggleClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF334155)
                )
            ) {
                Text(
                    text = "${strings.languageLabel}: ${
                        if (selectedLanguage == AppLanguage.ENGLISH) {
                            strings.languageEnglish
                        } else {
                            strings.languageGerman
                        }
                    }",
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            SpeedHeroCard(
                strings = strings,
                speed = obdLiveData.speed,
                rpm = obdLiveData.rpm,
                coolant = obdLiveData.coolantTemp
            )

            Spacer(modifier = Modifier.height(14.dp))

            ControlButtons(
                strings = strings,
                isLiveReading = isLiveReading,
                onStartLiveClick = onStartLiveClick,
                onStopLiveClick = onStopLiveClick,
                onBackToBluetoothClick = onBackToBluetoothClick
            )

            Spacer(modifier = Modifier.height(14.dp))

            AlertCard(
                strings = strings,
                vehicleAlert = vehicleAlert
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = strings.vehicleParameters,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = MaterialTheme.typography.titleLarge.fontSize
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.height(760.dp)
            ) {
                items(dashboardItems) { item ->
                    DashboardDataCard(item = item)
                }
            }
        }
    }
}

@Composable
private fun DashboardHeader(
    strings: AppStrings.Strings,
    statusText: String,
    isLiveReading: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column {
            Text(
                text = strings.appName,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = MaterialTheme.typography.headlineMedium.fontSize
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = statusText,
                color = Color(0xFF94A3B8),
                fontSize = MaterialTheme.typography.bodyMedium.fontSize
            )
        }

        StatusPill(
            strings = strings,
            isLiveReading = isLiveReading
        )
    }
}

@Composable
private fun StatusPill(
    strings: AppStrings.Strings,
    isLiveReading: Boolean
) {
    val text = if (isLiveReading) strings.liveDataOn else strings.liveDataOff
    val background = if (isLiveReading) Color(0xFF064E3B) else Color(0xFF7C2D12)
    val foreground = if (isLiveReading) Color(0xFF6EE7B7) else Color(0xFFFDBA74)

    Card(
        shape = RoundedCornerShape(50.dp),
        colors = CardDefaults.cardColors(
            containerColor = background
        )
    ) {
        Text(
            text = text,
            color = foreground,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun SpeedHeroCard(
    strings: AppStrings.Strings,
    speed: Int?,
    rpm: Int?,
    coolant: Int?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0F172A)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF1E40AF),
                            Color(0xFF0F172A)
                        )
                    )
                )
                .padding(22.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = strings.currentSpeed,
                    color = Color(0xFFBFDBFE),
                    fontWeight = FontWeight.Bold,
                    fontSize = MaterialTheme.typography.labelLarge.fontSize
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = speed?.toString() ?: "--",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = MaterialTheme.typography.displayLarge.fontSize
                )

                Text(
                    text = "km/h",
                    color = Color(0xFFCBD5E1),
                    fontSize = MaterialTheme.typography.titleMedium.fontSize
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MiniStatCard(
                        title = strings.rpm,
                        value = rpm?.toString() ?: "--",
                        unit = "rpm",
                        modifier = Modifier.weight(1f)
                    )

                    MiniStatCard(
                        title = strings.coolant,
                        value = coolant?.toString() ?: "--",
                        unit = "°C",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun MiniStatCard(
    title: String,
    value: String,
    unit: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xCC020617)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                color = Color(0xFF94A3B8),
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = value,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = MaterialTheme.typography.headlineSmall.fontSize
            )

            Text(
                text = unit,
                color = Color(0xFFCBD5E1)
            )
        }
    }
}

@Composable
private fun ControlButtons(
    strings: AppStrings.Strings,
    isLiveReading: Boolean,
    onStartLiveClick: () -> Unit,
    onStopLiveClick: () -> Unit,
    onBackToBluetoothClick: () -> Unit
) {
    Column {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = onStartLiveClick,
                enabled = !isLiveReading,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF059669),
                    disabledContainerColor = Color(0xFF14532D)
                )
            ) {
                Text(strings.startLive)
            }

            Button(
                onClick = onStopLiveClick,
                enabled = isLiveReading,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFDC2626),
                    disabledContainerColor = Color(0xFF7F1D1D)
                )
            ) {
                Text(strings.stopLive)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = onBackToBluetoothClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                text = strings.bluetoothSetupButton,
                color = Color(0xFF93C5FD)
            )
        }
    }
}

@Composable
private fun AlertCard(
    strings: AppStrings.Strings,
    vehicleAlert: VehicleAlert
) {
    val background = when (vehicleAlert.level) {
        AlertLevel.NORMAL -> Color(0xFF052E2B)
        AlertLevel.WARNING -> Color(0xFF451A03)
        AlertLevel.CRITICAL -> Color(0xFF450A0A)
    }

    val titleColor = when (vehicleAlert.level) {
        AlertLevel.NORMAL -> Color(0xFF5EEAD4)
        AlertLevel.WARNING -> Color(0xFFFBBF24)
        AlertLevel.CRITICAL -> Color(0xFFFCA5A5)
    }

    val title = when (vehicleAlert.level) {
        AlertLevel.NORMAL -> strings.systemStatus
        AlertLevel.WARNING -> strings.drivingAlert
        AlertLevel.CRITICAL -> strings.criticalAlert
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = background
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                color = titleColor,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = vehicleAlert.message,
                color = Color.White,
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                fontWeight = if (vehicleAlert.level == AlertLevel.CRITICAL) {
                    FontWeight.Bold
                } else {
                    FontWeight.Normal
                }
            )
        }
    }
}

@Composable
private fun DashboardDataCard(
    item: DashboardItem
) {
    val progress = calculateProgress(
        current = item.current,
        min = item.min,
        max = item.max
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF111827)
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Text(
                text = item.title,
                color = Color(0xFF94A3B8),
                fontWeight = FontWeight.Medium,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.value,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                textAlign = TextAlign.Start
            )

            Text(
                text = item.unit,
                color = Color(0xFFCBD5E1),
                fontSize = MaterialTheme.typography.bodySmall.fontSize
            )

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = getProgressColor(progress),
                trackColor = Color(0xFF1F2937)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = item.min.toInt().toString(),
                    color = Color(0xFF64748B),
                    fontSize = MaterialTheme.typography.labelSmall.fontSize
                )

                Text(
                    text = item.max.toInt().toString(),
                    color = Color(0xFF64748B),
                    fontSize = MaterialTheme.typography.labelSmall.fontSize
                )
            }
        }
    }
}

private fun buildDashboardItems(
    data: ObdLiveData,
    strings: AppStrings.Strings
): List<DashboardItem> {
    return listOf(
        DashboardItem(
            title = strings.throttle,
            value = data.throttle?.toString() ?: "--",
            unit = "%",
            min = 0.0,
            max = 99.0,
            current = data.throttle?.toDouble()
        ),
        DashboardItem(
            title = strings.engineLoad,
            value = data.engineLoad?.toString() ?: "--",
            unit = "%",
            min = 0.0,
            max = 99.0,
            current = data.engineLoad?.toDouble()
        ),
        DashboardItem(
            title = strings.fuelLevel,
            value = data.fuelLevel?.toString() ?: "--",
            unit = "%",
            min = 0.0,
            max = 99.0,
            current = data.fuelLevel?.toDouble()
        ),
        DashboardItem(
            title = strings.manifold,
            value = data.intakeManifoldPressure?.toString() ?: "--",
            unit = "kPa",
            min = 0.0,
            max = 254.0,
            current = data.intakeManifoldPressure?.toDouble()
        ),
        DashboardItem(
            title = strings.intakeTemp,
            value = data.intakeAirTemp?.toString() ?: "--",
            unit = "°C",
            min = -40.0,
            max = 214.0,
            current = data.intakeAirTemp?.toDouble()
        ),
        DashboardItem(
            title = strings.maf,
            value = data.massAirFlow?.let { String.format("%.2f", it) } ?: "--",
            unit = "g/s",
            min = 0.0,
            max = 654.54,
            current = data.massAirFlow
        ),
        DashboardItem(
            title = strings.ignition,
            value = data.ignitionTiming?.let { String.format("%.1f", it) } ?: "--",
            unit = "°",
            min = -64.0,
            max = 63.0,
            current = data.ignitionTiming
        )
    )
}

private fun calculateProgress(
    current: Double?,
    min: Double,
    max: Double
): Float {
    if (current == null) return 0f
    val progress = (current - min) / (max - min)
    return progress.coerceIn(0.0, 1.0).toFloat()
}

private fun getProgressColor(progress: Float): Color {
    return when {
        progress < 0.60f -> Color(0xFF38BDF8)
        progress < 0.85f -> Color(0xFFF59E0B)
        else -> Color(0xFFEF4444)
    }
}