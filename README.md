# OBD Voice Assist

OBD Voice Assist is an Android application developed using Kotlin and Jetpack Compose. The app connects to an ELM327-compatible OBD-II simulator through Bluetooth, receives live vehicle parameters, displays them in a modern dashboard, and provides bilingual voice warnings in English and German.

## Features

- Bluetooth connection with ELM327-compatible OBD-II simulator
- Live vehicle dashboard
- Speed, RPM, coolant temperature, throttle, engine load, fuel level, intake temperature, manifold pressure, mass air flow, and ignition timing
- Voice alerts using Android Text-to-Speech
- English and German language support
- Warning and critical alert system
- Professional splash screen and app logo
- Dark automotive-style UI built with Jetpack Compose

## Technologies Used

- Kotlin
- Android Studio
- Jetpack Compose
- Bluetooth Classic / RFCOMM
- ELM327 OBD-II commands
- Android Text-to-Speech
- Material 3

## Screenshots

### Splash Screen
![Splash Screen](screenshots/splash_screen.png)

### Welcome Screen
![Welcome Screen](screenshots/welcome_screen.png)

### Bluetooth Setup
![Bluetooth Setup](screenshots/bluetooth_screen.png)

### Dashboard
![Dashboard](screenshots/dashboard_screen.png)

## OBD-II Commands Used

| Parameter | OBD-II PID |
|---|---|
| Speed | 010D |
| RPM | 010C |
| Coolant Temperature | 0105 |
| Throttle Position | 0111 |
| Engine Load | 0104 |
| Fuel Level | 012F |
| Intake Manifold Pressure | 010B |
| Intake Air Temperature | 010F |
| Mass Air Flow | 0110 |
| Ignition Timing | 010E |

## Alert System

The app provides warning and critical alerts. Critical alerts instruct the driver to stop the vehicle safely.

Example alerts:

- You are driving too fast.
- Engine temperature is high.
- Critical engine temperature. Please stop the vehicle.
- Kritische Motortemperatur. Bitte halten Sie das Fahrzeug an.

## Current Target

This version is currently tested with an OBD Simulator-B-V1.5 / ELM327-compatible Bluetooth simulator.

## Future Improvements

- Settings screen for custom warning limits
- Save preferences using DataStore
- Auto-connect to last OBD device
- Diagnostic trouble code reading
- Alert history
- Real vehicle testing