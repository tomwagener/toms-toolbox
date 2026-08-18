# 🧰 Toms Toolbox - Hammerhead Karoo Extension

**Toms Toolbox** is a feature-rich background extension app for the **Hammerhead Karoo 2 (and newer)** cycle computer. It provides live battery monitoring for all connected sensors (SRAM AXS, Power Meters, HR Straps, Speed/Cadence, Radar) with runtime estimates, as well as an autonomous, hands-free **Smart Hydration Tracker & Reminder**.

---

## ✨ Features

### 🔋 1. Battery Monitoring Data Fields
- **Battery Check (`battery_check`):**
  - Instant status overview (**`OK`** vs. **`LOW: Rear AXS`**).
- **Battery List (`battery_list`):**
  - Live battery percentage in 20% steps for all connected components.
  - Remaining runtime estimates (e.g. `~15-20 hrs`, `~8-15 hrs`, `<2 hrs (Replace)`).
  - **Smart Auto-Scrolling:** If the data field is placed in a small 1-row tile, the list automatically pages through all sensors every 3 seconds. When resized to 2 rows or larger, all sensors are shown simultaneously.
  - **Clean Graphical View:** Suppresses default Karoo OS `42` sample number overlays.

### 🚰 2. Smart Hydration Tracker & Reminder
- **Dynamic Fluid Loss Calculation:**
  - Subscribes to live Karoo sensor streams (`TEMPERATURE`, `POWER`, `HEART_RATE`).
  - Automatically scales fluid loss higher in high ambient temperatures (+2.5% per °C > 20°C) and high effort zones (Power/HR).
- **Hands-Free Auto-Log Banner (`InRideAlert`):**
  - Plays a double-beep tone (`PlayBeepPattern`) when a drink is due.
  - Displays a clean Karoo banner: **`Drink 150ml Now! 🚰`** with an 8-second countdown.
  - **100% Hands-Free:** If you take your sip and take no action, the banner auto-dismisses after 8 seconds and automatically logs **+150ml (1 Sip)** and resets the timer!
- **Direct Touch-Click & Hardware Buttons:**
  - **Data Field Touch:** Tapping the `🚰 SMART HYDRATION` data field directly on the ride screen instantly logs **+150ml**.
  - **Hardware Buttons / SRAM AXS Blips:** Map a button shortcut to trigger `log_sip` (+150ml) or `log_bottle` (+750ml).

### ⚙️ 3. In-App Setup & Settings Dashboard
- Accessible directly from the Karoo main menu grid via the custom **Red Toolbox 🧰** app icon.
- Interactive configuration options:
  - **Bottle Size:** `500 ml` | `600 ml` | `750 ml` | `1000 ml`
  - **Sip Size:** `100 ml` | `125 ml` | `150 ml (Default)` | `200 ml`
  - **Base Hourly Rate:** `400 ml/h` | `500 ml/h (Default)` | `600 ml/h` | `750 ml/h`
- Includes top and bottom **`← Back`** buttons to return seamlessly to the Karoo main menu.

---

## 📸 Data Fields Overview

| Data Field | Type | Description |
|---|---|---|
| **Battery Check** | Graphical | Quick status summary (`OK` or `LOW: Component`) |
| **Battery List** | Graphical | Detailed battery % & remaining hours with auto-scrolling |
| **Smart Hydration** | Graphical | Dynamic countdown, fluid loss rate, total drunk & bottles consumed |

---

## 🛠️ Build & Installation Guide

### Prerequisites
- JDK 17
- Android SDK Platform 34 & Build-Tools 34.0.0
- `adb` (Android Debug Bridge)

### 1. Compile APK
```bash
./gradlew assembleDebug
```
The compiled APK will be generated at:
`app/build/outputs/apk/debug/app-debug.apk`

### 2. Install on Karoo 2 via ADB
Connect your Karoo 2 via USB with Developer Options & USB Debugging enabled:
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

---

## 📜 License
Apache License 2.0 - See [LICENSE](LICENSE) for details.
