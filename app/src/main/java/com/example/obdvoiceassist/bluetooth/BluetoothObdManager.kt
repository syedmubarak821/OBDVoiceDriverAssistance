package com.example.obdvoiceassist.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.example.obdvoiceassist.models.BluetoothDeviceItem
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID

class BluetoothObdManager(
    private val context: Context
) {
    private var bluetoothSocket: BluetoothSocket? = null
    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null

    private val sppUuid: UUID =
        UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    private fun getBluetoothAdapter(): BluetoothAdapter? {
        val bluetoothManager =
            context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        return bluetoothManager.adapter
    }

    fun hasBluetoothConnectPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) ==
                    PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun isBluetoothSupported(): Boolean {
        return getBluetoothAdapter() != null
    }

    fun isBluetoothEnabled(): Boolean {
        return getBluetoothAdapter()?.isEnabled == true
    }

    @SuppressLint("MissingPermission")
    fun getPairedDevices(): List<BluetoothDeviceItem> {
        val adapter = getBluetoothAdapter() ?: return emptyList()

        if (!hasBluetoothConnectPermission()) {
            return emptyList()
        }

        return adapter.bondedDevices.map { device ->
            BluetoothDeviceItem(
                name = device.name ?: "Unknown device",
                address = device.address
            )
        }
    }

    @SuppressLint("MissingPermission")
    fun connect(address: String) {
        close()

        val adapter = getBluetoothAdapter()
            ?: throw IllegalStateException("Bluetooth is not supported")

        if (!adapter.isEnabled) {
            throw IllegalStateException("Bluetooth is turned off")
        }

        if (!hasBluetoothConnectPermission()) {
            throw SecurityException("Bluetooth permission missing")
        }

        adapter.cancelDiscovery()
        Thread.sleep(500)

        val device = adapter.getRemoteDevice(address)

        try {
            val socket = device.createRfcommSocketToServiceRecord(sppUuid)
            socket.connect()

            bluetoothSocket = socket
            inputStream = socket.inputStream
            outputStream = socket.outputStream

        } catch (firstError: Exception) {
            close()

            try {
                // Fallback method often works better with ELM327 / HC-05 / HC-06 clones
                val method = device.javaClass.getMethod(
                    "createRfcommSocket",
                    Int::class.javaPrimitiveType
                )

                val fallbackSocket = method.invoke(device, 1) as BluetoothSocket
                fallbackSocket.connect()

                bluetoothSocket = fallbackSocket
                inputStream = fallbackSocket.inputStream
                outputStream = fallbackSocket.outputStream

            } catch (secondError: Exception) {
                close()
                throw secondError
            }
        }
    }

    fun sendCommand(command: String, delayMs: Long = 500): String? {
        val out = outputStream ?: throw IllegalStateException("Output stream is not ready")
        val input = inputStream ?: throw IllegalStateException("Input stream is not ready")

        try {
            while (input.available() > 0) {
                input.read()
            }

            out.write("$command\r".toByteArray())
            out.flush()

            Thread.sleep(delayMs)

            val responseBuilder = StringBuilder()
            val buffer = ByteArray(1024)

            val startTime = System.currentTimeMillis()
            val timeoutMs = 2000L

            while (System.currentTimeMillis() - startTime < timeoutMs) {
                val available = input.available()

                if (available > 0) {
                    val bytesRead = input.read(buffer)
                    if (bytesRead == -1) {
                        throw IllegalStateException("Bluetooth connection closed by device")
                    }

                    val chunk = String(buffer, 0, bytesRead)
                    responseBuilder.append(chunk)

                    if (chunk.contains(">")) {
                        break
                    }
                }

                Thread.sleep(30)
            }

            val response = responseBuilder.toString().trim()
            return response.ifBlank { null }

        } catch (e: Exception) {
            close()
            throw e
        }
    }

    fun close() {
        try {
            bluetoothSocket?.close()
        } catch (_: Exception) {
        }

        bluetoothSocket = null
        inputStream = null
        outputStream = null
    }
}