package com.example.obdvoiceassist.models

enum class AlertLevel {
    NORMAL,
    WARNING,
    CRITICAL
}

data class VehicleAlert(
    val level: AlertLevel,
    val message: String
)