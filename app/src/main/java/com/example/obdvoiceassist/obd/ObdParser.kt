package com.example.obdvoiceassist.obd

import com.example.obdvoiceassist.models.ObdLiveData

object ObdParser {

    fun cleanResponse(rawResponse: String): List<String> {
        val cleaned = rawResponse
            .replace("\r", " ")
            .replace("\n", " ")
            .replace(">", " ")
            .replace("SEARCHING...", " ")
            .replace("NO DATA", " ")
            .trim()

        val spacedParts = cleaned
            .split(" ")
            .filter { it.isNotBlank() }

        if (spacedParts.size > 1) {
            return spacedParts
        }

        val compact = cleaned.replace(" ", "")

        if (compact.length >= 6 && compact.startsWith("41")) {
            return compact.chunked(2)
        }

        return spacedParts
    }

    private fun parsePidByte(rawResponse: String, pid: String): Int? {
        return try {
            val parts = cleanResponse(rawResponse)
            val index = parts.indexOf(pid)

            if (index != -1 && index + 1 < parts.size) {
                parts[index + 1].toInt(16)
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun parsePidTwoBytes(rawResponse: String, pid: String): Pair<Int, Int>? {
        return try {
            val parts = cleanResponse(rawResponse)
            val index = parts.indexOf(pid)

            if (index != -1 && index + 2 < parts.size) {
                val a = parts[index + 1].toInt(16)
                val b = parts[index + 2].toInt(16)
                Pair(a, b)
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    fun parseSpeed(rawResponse: String): Int? {
        return parsePidByte(rawResponse, "0D")
    }

    fun parseRpm(rawResponse: String): Int? {
        val bytes = parsePidTwoBytes(rawResponse, "0C")
        return bytes?.let {
            ((it.first * 256) + it.second) / 4
        }
    }

    fun parseCoolantTemp(rawResponse: String): Int? {
        return parsePidByte(rawResponse, "05")?.minus(40)
    }

    fun parseThrottle(rawResponse: String): Int? {
        return parsePidByte(rawResponse, "11")?.let {
            (it * 100) / 255
        }
    }

    fun parseEngineLoad(rawResponse: String): Int? {
        return parsePidByte(rawResponse, "04")?.let {
            (it * 100) / 255
        }
    }

    fun parseFuelLevel(rawResponse: String): Int? {
        return parsePidByte(rawResponse, "2F")?.let {
            (it * 100) / 255
        }
    }

    fun parseIntakeManifoldPressure(rawResponse: String): Int? {
        return parsePidByte(rawResponse, "0B")
    }

    fun parseIntakeAirTemp(rawResponse: String): Int? {
        return parsePidByte(rawResponse, "0F")?.minus(40)
    }

    fun parseMassAirFlow(rawResponse: String): Double? {
        val bytes = parsePidTwoBytes(rawResponse, "10")
        return bytes?.let {
            ((it.first * 256) + it.second) / 100.0
        }
    }

    fun parseIgnitionTiming(rawResponse: String): Double? {
        return parsePidByte(rawResponse, "0E")?.let {
            (it / 2.0) - 64.0
        }
    }

    fun buildObdData(
        speedRaw: String?,
        rpmRaw: String?,
        coolantRaw: String?,
        throttleRaw: String?,
        loadRaw: String?,
        fuelRaw: String?,
        manifoldRaw: String?,
        intakeTempRaw: String?,
        mafRaw: String?,
        ignitionRaw: String?
    ): ObdLiveData {
        return ObdLiveData(
            speed = speedRaw?.let { parseSpeed(it) },
            rpm = rpmRaw?.let { parseRpm(it) },
            coolantTemp = coolantRaw?.let { parseCoolantTemp(it) },
            throttle = throttleRaw?.let { parseThrottle(it) },
            engineLoad = loadRaw?.let { parseEngineLoad(it) },
            fuelLevel = fuelRaw?.let { parseFuelLevel(it) },
            intakeManifoldPressure = manifoldRaw?.let { parseIntakeManifoldPressure(it) },
            intakeAirTemp = intakeTempRaw?.let { parseIntakeAirTemp(it) },
            massAirFlow = mafRaw?.let { parseMassAirFlow(it) },
            ignitionTiming = ignitionRaw?.let { parseIgnitionTiming(it) }
        )
    }

    fun formatObdData(data: ObdLiveData): String {
        return """
            LIVE OBD DATA
            
            Speed: ${data.speed ?: "--"} km/h
            RPM: ${data.rpm ?: "--"} rpm
            Coolant: ${data.coolantTemp ?: "--"} °C
            Throttle: ${data.throttle ?: "--"} %
            Engine Load: ${data.engineLoad ?: "--"} %
            Fuel Level: ${data.fuelLevel ?: "--"} %
            Intake Manifold Pressure: ${data.intakeManifoldPressure ?: "--"} kPa
            Intake Air Temperature: ${data.intakeAirTemp ?: "--"} °C
            Mass Air Flow: ${data.massAirFlow?.let { String.format("%.2f", it) } ?: "--"} g/s
            Ignition Timing: ${data.ignitionTiming?.let { String.format("%.1f", it) } ?: "--"} °
        """.trimIndent()
    }
}