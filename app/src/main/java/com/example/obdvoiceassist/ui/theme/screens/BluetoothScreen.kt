package com.example.obdvoiceassist.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.obdvoiceassist.localization.AppStrings
import com.example.obdvoiceassist.models.BluetoothDeviceItem

@Composable
fun BluetoothScreen(
    strings: AppStrings.Strings,
    statusText: String,
    responseText: String,
    devices: List<BluetoothDeviceItem>,
    onRefreshClick: () -> Unit,
    onDeviceClick: (BluetoothDeviceItem) -> Unit,
    onTestCommandClick: () -> Unit,
    onReadAllDataClick: () -> Unit,
    onStartLiveClick: () -> Unit,
    onStopLiveClick: () -> Unit,
    onGoToDashboardClick: () -> Unit
) {
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
            .padding(20.dp)
    ) {
        Column {
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = strings.bluetoothSetup,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = MaterialTheme.typography.headlineMedium.fontSize
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = statusText,
                color = Color(0xFFCBD5E1)
            )

            Spacer(modifier = Modifier.height(16.dp))

            ResponseCard(
                title = strings.obdResponse,
                responseText = responseText
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onTestCommandClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF16A34A)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(strings.testAtzCommand)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onGoToDashboardClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0EA5E9)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(strings.goToDashboard)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onRefreshClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2563EB)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(strings.refreshPairedDevices)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = strings.pairedDevices,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(devices) { device ->
                    BluetoothDeviceCard(
                        device = device,
                        onClick = { onDeviceClick(device) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ResponseCard(
    title: String,
    responseText: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF111827)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = responseText,
                color = Color(0xFFCBD5E1)
            )
        }
    }
}

@Composable
private fun BluetoothDeviceCard(
    device: BluetoothDeviceItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF111827)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = device.name,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = device.address,
                color = Color(0xFF94A3B8)
            )
        }
    }
}