package com.example.obdvoiceassist.models

data class ObdLiveData(
    val speed: Int? = null,
    val rpm: Int? = null,
    val coolantTemp: Int? = null,
    val throttle: Int? = null,
    val engineLoad: Int? = null,
    val fuelLevel: Int? = null,
    val intakeManifoldPressure: Int? = null,
    val intakeAirTemp: Int? = null,
    val massAirFlow: Double? = null,
    val ignitionTiming: Double? = null
)