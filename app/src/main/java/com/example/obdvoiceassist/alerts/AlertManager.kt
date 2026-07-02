package com.example.obdvoiceassist.alerts

import com.example.obdvoiceassist.models.AlertLevel
import com.example.obdvoiceassist.models.AppLanguage
import com.example.obdvoiceassist.models.ObdLiveData
import com.example.obdvoiceassist.models.VehicleAlert

object AlertManager {

    private fun text(
        language: AppLanguage,
        english: String,
        german: String
    ): String {
        return if (language == AppLanguage.GERMAN) german else english
    }

    fun getAlert(
        data: ObdLiveData,
        language: AppLanguage = AppLanguage.ENGLISH
    ): VehicleAlert {
        return when {

            // -------------------------
            // CRITICAL HIGH ALERTS
            // -------------------------

            data.coolantTemp != null && data.coolantTemp >= 115 ->
                VehicleAlert(
                    AlertLevel.CRITICAL,
                    text(
                        language,
                        "Critical engine temperature. Please stop the vehicle.",
                        "Kritische Motortemperatur. Bitte halten Sie das Fahrzeug an."
                    )
                )

            data.rpm != null && data.rpm >= 6000 ->
                VehicleAlert(
                    AlertLevel.CRITICAL,
                    text(
                        language,
                        "Critical engine RPM. Please stop the vehicle.",
                        "Kritische Motordrehzahl. Bitte halten Sie das Fahrzeug an."
                    )
                )

            data.speed != null && data.speed >= 120 ->
                VehicleAlert(
                    AlertLevel.CRITICAL,
                    text(
                        language,
                        "Critical speed warning. Please slow down and stop the vehicle safely.",
                        "Kritische Geschwindigkeitswarnung. Bitte verlangsamen Sie und halten Sie das Fahrzeug sicher an."
                    )
                )

            data.engineLoad != null && data.engineLoad >= 95 ->
                VehicleAlert(
                    AlertLevel.CRITICAL,
                    text(
                        language,
                        "Critical engine load. Please stop the vehicle.",
                        "Kritische Motorlast. Bitte halten Sie das Fahrzeug an."
                    )
                )

            data.throttle != null && data.throttle >= 97 ->
                VehicleAlert(
                    AlertLevel.CRITICAL,
                    text(
                        language,
                        "Critical throttle position. Please stop the vehicle safely.",
                        "Kritische Drosselklappenstellung. Bitte halten Sie das Fahrzeug sicher an."
                    )
                )

            data.intakeAirTemp != null && data.intakeAirTemp >= 100 ->
                VehicleAlert(
                    AlertLevel.CRITICAL,
                    text(
                        language,
                        "Critical intake air temperature. Please stop the vehicle.",
                        "Kritische Ansauglufttemperatur. Bitte halten Sie das Fahrzeug an."
                    )
                )

            data.intakeManifoldPressure != null && data.intakeManifoldPressure >= 240 ->
                VehicleAlert(
                    AlertLevel.CRITICAL,
                    text(
                        language,
                        "Critical intake manifold pressure. Please stop the vehicle.",
                        "Kritischer Ansaugkrümmerdruck. Bitte halten Sie das Fahrzeug an."
                    )
                )

            data.massAirFlow != null && data.massAirFlow >= 500 ->
                VehicleAlert(
                    AlertLevel.CRITICAL,
                    text(
                        language,
                        "Critical mass air flow reading. Please stop the vehicle.",
                        "Kritischer Luftmassenmesserwert. Bitte halten Sie das Fahrzeug an."
                    )
                )

            data.ignitionTiming != null && data.ignitionTiming >= 55 ->
                VehicleAlert(
                    AlertLevel.CRITICAL,
                    text(
                        language,
                        "Critical ignition timing advance. Please stop the vehicle.",
                        "Kritisch vorverstellter Zündzeitpunkt. Bitte halten Sie das Fahrzeug an."
                    )
                )

            // -------------------------
            // CRITICAL LOW ALERTS
            // -------------------------

            data.fuelLevel != null && data.fuelLevel <= 5 ->
                VehicleAlert(
                    AlertLevel.CRITICAL,
                    text(
                        language,
                        "Critical fuel level. Please stop the vehicle safely or refuel immediately.",
                        "Kritischer Kraftstoffstand. Bitte halten Sie sicher an oder tanken Sie sofort."
                    )
                )

            data.coolantTemp != null && data.coolantTemp <= 20 ->
                VehicleAlert(
                    AlertLevel.CRITICAL,
                    text(
                        language,
                        "Critical low coolant temperature reading. Please stop the vehicle and check the engine.",
                        "Kritisch niedrige Kühlmitteltemperatur. Bitte halten Sie das Fahrzeug an und prüfen Sie den Motor."
                    )
                )

            data.intakeAirTemp != null && data.intakeAirTemp <= -20 ->
                VehicleAlert(
                    AlertLevel.CRITICAL,
                    text(
                        language,
                        "Critical low intake air temperature reading. Please stop the vehicle.",
                        "Kritisch niedrige Ansauglufttemperatur. Bitte halten Sie das Fahrzeug an."
                    )
                )

            data.intakeManifoldPressure != null && data.intakeManifoldPressure <= 10 ->
                VehicleAlert(
                    AlertLevel.CRITICAL,
                    text(
                        language,
                        "Critical low intake manifold pressure. Please stop the vehicle.",
                        "Kritisch niedriger Ansaugkrümmerdruck. Bitte halten Sie das Fahrzeug an."
                    )
                )

            data.massAirFlow != null && data.massAirFlow <= 0.3 ->
                VehicleAlert(
                    AlertLevel.CRITICAL,
                    text(
                        language,
                        "Critical low mass air flow reading. Please stop the vehicle.",
                        "Kritisch niedriger Luftmassenmesserwert. Bitte halten Sie das Fahrzeug an."
                    )
                )

            data.ignitionTiming != null && data.ignitionTiming <= -40 ->
                VehicleAlert(
                    AlertLevel.CRITICAL,
                    text(
                        language,
                        "Critical ignition timing retard. Please stop the vehicle.",
                        "Kritisch verzögerter Zündzeitpunkt. Bitte halten Sie das Fahrzeug an."
                    )
                )

            // -------------------------
            // WARNING HIGH ALERTS
            // -------------------------

            data.coolantTemp != null && data.coolantTemp >= 100 ->
                VehicleAlert(
                    AlertLevel.WARNING,
                    text(
                        language,
                        "Engine temperature is high.",
                        "Die Motortemperatur ist hoch."
                    )
                )

            data.rpm != null && data.rpm >= 4000 ->
                VehicleAlert(
                    AlertLevel.WARNING,
                    text(
                        language,
                        "Engine RPM is too high.",
                        "Die Motordrehzahl ist zu hoch."
                    )
                )

            data.speed != null && data.speed >= 80 ->
                VehicleAlert(
                    AlertLevel.WARNING,
                    text(
                        language,
                        "You are driving too fast.",
                        "Sie fahren zu schnell."
                    )
                )

            data.engineLoad != null && data.engineLoad >= 85 ->
                VehicleAlert(
                    AlertLevel.WARNING,
                    text(
                        language,
                        "Engine load is high.",
                        "Die Motorlast ist hoch."
                    )
                )

            data.throttle != null && data.throttle >= 90 ->
                VehicleAlert(
                    AlertLevel.WARNING,
                    text(
                        language,
                        "Throttle position is high.",
                        "Die Drosselklappenstellung ist hoch."
                    )
                )

            data.intakeAirTemp != null && data.intakeAirTemp >= 70 ->
                VehicleAlert(
                    AlertLevel.WARNING,
                    text(
                        language,
                        "Intake air temperature is high.",
                        "Die Ansauglufttemperatur ist hoch."
                    )
                )

            data.intakeManifoldPressure != null && data.intakeManifoldPressure >= 200 ->
                VehicleAlert(
                    AlertLevel.WARNING,
                    text(
                        language,
                        "Intake manifold pressure is high.",
                        "Der Ansaugkrümmerdruck ist hoch."
                    )
                )

            data.massAirFlow != null && data.massAirFlow >= 300 ->
                VehicleAlert(
                    AlertLevel.WARNING,
                    text(
                        language,
                        "Mass air flow is high.",
                        "Der Luftmassenstrom ist hoch."
                    )
                )

            data.ignitionTiming != null && data.ignitionTiming >= 40 ->
                VehicleAlert(
                    AlertLevel.WARNING,
                    text(
                        language,
                        "Ignition timing advance is high.",
                        "Der Zündzeitpunkt ist stark vorverstellt."
                    )
                )

            // -------------------------
            // WARNING LOW ALERTS
            // -------------------------

            data.fuelLevel != null && data.fuelLevel <= 15 ->
                VehicleAlert(
                    AlertLevel.WARNING,
                    text(
                        language,
                        "Fuel level is low.",
                        "Der Kraftstoffstand ist niedrig."
                    )
                )

            data.coolantTemp != null && data.coolantTemp <= 50 ->
                VehicleAlert(
                    AlertLevel.WARNING,
                    text(
                        language,
                        "Coolant temperature is low.",
                        "Die Kühlmitteltemperatur ist niedrig."
                    )
                )

            data.intakeAirTemp != null && data.intakeAirTemp <= 0 ->
                VehicleAlert(
                    AlertLevel.WARNING,
                    text(
                        language,
                        "Intake air temperature is low.",
                        "Die Ansauglufttemperatur ist niedrig."
                    )
                )

            data.intakeManifoldPressure != null && data.intakeManifoldPressure <= 20 ->
                VehicleAlert(
                    AlertLevel.WARNING,
                    text(
                        language,
                        "Intake manifold pressure is low.",
                        "Der Ansaugkrümmerdruck ist niedrig."
                    )
                )

            data.massAirFlow != null && data.massAirFlow <= 1.0 ->
                VehicleAlert(
                    AlertLevel.WARNING,
                    text(
                        language,
                        "Mass air flow is low.",
                        "Der Luftmassenstrom ist niedrig."
                    )
                )

            data.ignitionTiming != null && data.ignitionTiming <= -20 ->
                VehicleAlert(
                    AlertLevel.WARNING,
                    text(
                        language,
                        "Ignition timing is retarded.",
                        "Der Zündzeitpunkt ist verzögert."
                    )
                )

            else ->
                VehicleAlert(
                    AlertLevel.NORMAL,
                    text(
                        language,
                        "No warning detected.",
                        "Keine Warnung erkannt."
                    )
                )
        }
    }
}