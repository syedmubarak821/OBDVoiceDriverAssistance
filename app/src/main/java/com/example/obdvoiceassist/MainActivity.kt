package com.example.obdvoiceassist

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.obdvoiceassist.alerts.AlertManager
import com.example.obdvoiceassist.bluetooth.BluetoothObdManager
import com.example.obdvoiceassist.localization.AppStrings
import com.example.obdvoiceassist.models.AppLanguage
import com.example.obdvoiceassist.models.BluetoothDeviceItem
import com.example.obdvoiceassist.models.ObdLiveData
import com.example.obdvoiceassist.obd.ObdParser
import com.example.obdvoiceassist.tts.VoiceAlertManager
import com.example.obdvoiceassist.ui.screens.BluetoothScreen
import com.example.obdvoiceassist.ui.screens.DashboardScreen
import com.example.obdvoiceassist.ui.screens.WelcomeScreen
import com.example.obdvoiceassist.ui.theme.OBDVoiceAssistTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import com.example.obdvoiceassist.ui.screens.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen


class MainActivity : ComponentActivity() {

    private lateinit var bluetoothObdManager: BluetoothObdManager
    private lateinit var voiceAlertManager: VoiceAlertManager

    private var screen by mutableStateOf("welcome")
    private var selectedLanguage by mutableStateOf(AppLanguage.ENGLISH)

    private var statusText by mutableStateOf("")
    private var responseText by mutableStateOf("")

    private var isLiveReading by mutableStateOf(false)
    private var showSplash by mutableStateOf(true)
    private var obdLiveData by mutableStateOf(ObdLiveData())
    private var liveReadCounter = 0
    private val pairedDevices = mutableStateListOf<BluetoothDeviceItem>()

    private val bluetoothPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val strings = AppStrings.get(selectedLanguage)
            val granted = permissions.values.all { it }

            if (granted) {
                loadPairedDevices()
                screen = "bluetooth"
            } else {
                statusText = strings.bluetoothPermissionDenied
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        bluetoothObdManager = BluetoothObdManager(this)
        voiceAlertManager = VoiceAlertManager(this)

        statusText = AppStrings.get(selectedLanguage).notConnected
        responseText = AppStrings.get(selectedLanguage).noResponseReceived

        enableEdgeToEdge()
        lifecycleScope.launch {
            delay(3000)
            showSplash = false
        }
        setContent {
            OBDVoiceAssistTheme {
                val strings = AppStrings.get(selectedLanguage)

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF07111F)
                ) {
                    if (showSplash) {
                        SplashScreen(strings = strings)
                    } else {
                        when (screen) {
                            "welcome" -> WelcomeScreen(
                                strings = strings,
                                selectedLanguage = selectedLanguage,
                                onLanguageToggleClick = {
                                    toggleLanguage()
                                },
                                onStartClick = {
                                    requestBluetoothPermissions()
                                }
                            )

                            "bluetooth" -> BluetoothScreen(
                                strings = strings,
                                statusText = statusText,
                                responseText = responseText,
                                devices = pairedDevices,
                                onRefreshClick = {
                                    requestBluetoothPermissions()
                                },
                                onDeviceClick = { device ->
                                    connectToDevice(device)
                                },
                                onTestCommandClick = {
                                    sendAtzCommand()
                                },
                                onReadAllDataClick = {
                                    readAllDataOnce()
                                },
                                onStartLiveClick = {
                                    startLiveDataReading()
                                },
                                onStopLiveClick = {
                                    stopLiveDataReading()
                                },
                                onGoToDashboardClick = {
                                    screen = "dashboard"
                                }
                            )

                            "dashboard" -> DashboardScreen(
                                strings = strings,
                                statusText = statusText,
                                obdLiveData = obdLiveData,
                                isLiveReading = isLiveReading,
                                selectedLanguage = selectedLanguage,
                                onLanguageToggleClick = {
                                    toggleLanguage()
                                },
                                onStartLiveClick = {
                                    startLiveDataReading()
                                },
                                onStopLiveClick = {
                                    stopLiveDataReading()
                                },
                                onBackToBluetoothClick = {
                                    screen = "bluetooth"
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun toggleLanguage() {
        selectedLanguage = if (selectedLanguage == AppLanguage.ENGLISH) {
            AppLanguage.GERMAN
        } else {
            AppLanguage.ENGLISH
        }

        voiceAlertManager.setLanguage(selectedLanguage)

        val strings = AppStrings.get(selectedLanguage)

        if (!isLiveReading && screen == "welcome") {
            statusText = strings.notConnected
        }

        if (responseText == AppStrings.get(AppLanguage.ENGLISH).liveDataRunning ||
            responseText == AppStrings.get(AppLanguage.GERMAN).liveDataRunning
        ) {
            responseText = strings.liveDataRunning
        }
    }

    private fun requestBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            bluetoothPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN
                )
            )
        } else {
            bluetoothPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        }
    }

    private fun loadPairedDevices() {
        val strings = AppStrings.get(selectedLanguage)

        pairedDevices.clear()

        if (!bluetoothObdManager.isBluetoothSupported()) {
            statusText = strings.bluetoothNotSupported
            return
        }

        if (!bluetoothObdManager.isBluetoothEnabled()) {
            statusText = strings.bluetoothTurnedOff
            return
        }

        if (!bluetoothObdManager.hasBluetoothConnectPermission()) {
            statusText = strings.bluetoothPermissionMissing
            return
        }

        val devices = bluetoothObdManager.getPairedDevices()
        pairedDevices.addAll(devices)

        statusText = if (devices.isEmpty()) {
            strings.noPairedDevices
        } else {
            "${strings.foundPairedDevices}: ${devices.size}"
        }
    }

    private fun connectToDevice(device: BluetoothDeviceItem) {
        val strings = AppStrings.get(selectedLanguage)

        stopLiveDataReading()

        statusText = "${strings.connectingTo} ${device.name}..."
        responseText = "${strings.connectingTo} ${device.name}..."

        CoroutineScope(Dispatchers.IO).launch {
            try {
                bluetoothObdManager.connect(device.address)

                // Stable ELM327 initialization
                bluetoothObdManager.sendCommand("ATZ", 1000)
                bluetoothObdManager.sendCommand("ATE0", 150)   // Echo off
                bluetoothObdManager.sendCommand("ATL0", 150)   // Linefeeds off
                bluetoothObdManager.sendCommand("ATH0", 150)   // Headers off
                bluetoothObdManager.sendCommand("ATS1", 150)   // Keep spaces ON
                bluetoothObdManager.sendCommand("ATSP0", 300)  // Auto protocol

                withContext(Dispatchers.Main) {
                    val currentStrings = AppStrings.get(selectedLanguage)
                    statusText = "${currentStrings.connectedTo} ${device.name}"
                    responseText = currentStrings.connectionSuccessful
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    val currentStrings = AppStrings.get(selectedLanguage)
                    statusText = currentStrings.connectionFailed
                    responseText = e.message ?: currentStrings.connectionFailed
                }
            }
        }
    }

    private fun sendAtzCommand() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                withContext(Dispatchers.Main) {
                    responseText = AppStrings.get(selectedLanguage).sendingAtz
                }

                val response = bluetoothObdManager.sendCommand("ATZ", 1500)

                withContext(Dispatchers.Main) {
                    val strings = AppStrings.get(selectedLanguage)
                    responseText = response ?: strings.noResponseReceived
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    val strings = AppStrings.get(selectedLanguage)
                    responseText = e.message ?: strings.noResponseReceived
                }
            }
        }
    }

    private fun readAllDataOnce() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                withContext(Dispatchers.Main) {
                    responseText = AppStrings.get(selectedLanguage).readingObdData
                }

                val data = readAllObdData()

                withContext(Dispatchers.Main) {
                    obdLiveData = data
                    responseText = ObdParser.formatObdData(data)
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    val strings = AppStrings.get(selectedLanguage)
                    responseText = e.message ?: strings.liveReadingError
                }
            }
        }
    }

    private fun startLiveDataReading() {
        val strings = AppStrings.get(selectedLanguage)

        if (isLiveReading) {
            responseText = strings.liveDataAlreadyRunning
            return
        }

        isLiveReading = true
        liveReadCounter = 0
        statusText = strings.liveDataStarted

        CoroutineScope(Dispatchers.IO).launch {
            while (isLiveReading) {
                try {
                    liveReadCounter++

                    val oldData = withContext(Dispatchers.Main) {
                        obdLiveData
                    }

                    // Fast values - update every loop
                    val speedRaw = bluetoothObdManager.sendCommand("010D", 80)
                    val rpmRaw = bluetoothObdManager.sendCommand("010C", 80)
                    val coolantRaw = bluetoothObdManager.sendCommand("0105", 80)
                    val throttleRaw = bluetoothObdManager.sendCommand("0111", 80)

                    val fastData = oldData.copy(
                        speed = speedRaw?.let { ObdParser.parseSpeed(it) } ?: oldData.speed,
                        rpm = rpmRaw?.let { ObdParser.parseRpm(it) } ?: oldData.rpm,
                        coolantTemp = coolantRaw?.let { ObdParser.parseCoolantTemp(it) } ?: oldData.coolantTemp,
                        throttle = throttleRaw?.let { ObdParser.parseThrottle(it) } ?: oldData.throttle
                    )

                    var finalData = fastData

                    // Slower values - update every second loop
                    if (liveReadCounter % 2 == 0) {
                        val loadRaw = bluetoothObdManager.sendCommand("0104", 80)
                        val fuelRaw = bluetoothObdManager.sendCommand("012F", 80)
                        val manifoldRaw = bluetoothObdManager.sendCommand("010B", 80)
                        val intakeTempRaw = bluetoothObdManager.sendCommand("010F", 80)
                        val mafRaw = bluetoothObdManager.sendCommand("0110", 80)
                        val ignitionRaw = bluetoothObdManager.sendCommand("010E", 80)

                        finalData = fastData.copy(
                            engineLoad = loadRaw?.let { ObdParser.parseEngineLoad(it) } ?: fastData.engineLoad,
                            fuelLevel = fuelRaw?.let { ObdParser.parseFuelLevel(it) } ?: fastData.fuelLevel,
                            intakeManifoldPressure = manifoldRaw?.let { ObdParser.parseIntakeManifoldPressure(it) } ?: fastData.intakeManifoldPressure,
                            intakeAirTemp = intakeTempRaw?.let { ObdParser.parseIntakeAirTemp(it) } ?: fastData.intakeAirTemp,
                            massAirFlow = mafRaw?.let { ObdParser.parseMassAirFlow(it) } ?: fastData.massAirFlow,
                            ignitionTiming = ignitionRaw?.let { ObdParser.parseIgnitionTiming(it) } ?: fastData.ignitionTiming
                        )
                    }

                    withContext(Dispatchers.Main) {
                        obdLiveData = finalData
                        responseText = AppStrings.get(selectedLanguage).liveDataRunning

                        if (liveReadCounter % 2 == 0) {
                            val warningMessage = getVoiceAlertMessage(finalData)
                            voiceAlertManager.setLanguage(selectedLanguage)
                            voiceAlertManager.speakWarning(warningMessage)
                        }
                    }

                    Thread.sleep(100)

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        val currentStrings = AppStrings.get(selectedLanguage)
                        isLiveReading = false
                        statusText = currentStrings.liveReadingStoppedDueToError
                        responseText = e.message ?: currentStrings.liveReadingError
                    }
                }
            }
        }
    }

    private fun stopLiveDataReading() {
        if (!isLiveReading) return

        isLiveReading = false
        statusText = AppStrings.get(selectedLanguage).liveDataStopped
    }

    private fun readAllObdData(): ObdLiveData {
        val speedRaw = bluetoothObdManager.sendCommand("010D", 100)
        val rpmRaw = bluetoothObdManager.sendCommand("010C", 100)
        val coolantRaw = bluetoothObdManager.sendCommand("0105", 100)
        val throttleRaw = bluetoothObdManager.sendCommand("0111", 100)
        val loadRaw = bluetoothObdManager.sendCommand("0104", 100)
        val fuelRaw = bluetoothObdManager.sendCommand("012F", 100)
        val manifoldRaw = bluetoothObdManager.sendCommand("010B", 100)
        val intakeTempRaw = bluetoothObdManager.sendCommand("010F", 100)
        val mafRaw = bluetoothObdManager.sendCommand("0110", 100)
        val ignitionRaw = bluetoothObdManager.sendCommand("010E", 100)

        return ObdParser.buildObdData(
            speedRaw = speedRaw,
            rpmRaw = rpmRaw,
            coolantRaw = coolantRaw,
            throttleRaw = throttleRaw,
            loadRaw = loadRaw,
            fuelRaw = fuelRaw,
            manifoldRaw = manifoldRaw,
            intakeTempRaw = intakeTempRaw,
            mafRaw = mafRaw,
            ignitionRaw = ignitionRaw
        )
    }

    private fun getVoiceAlertMessage(data: ObdLiveData): String {
        val alert = AlertManager.getAlert(data, selectedLanguage)
        return alert.message
    }

    override fun onDestroy() {
        super.onDestroy()
        isLiveReading = false
        bluetoothObdManager.close()
        voiceAlertManager.shutdown()
    }
}