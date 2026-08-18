package io.hammerhead.karooexttemplate.extension

import io.hammerhead.karooext.KarooSystemService
import io.hammerhead.karooext.extension.DataTypeImpl
import io.hammerhead.karooext.extension.KarooExtension
import io.hammerhead.karooext.models.InRideAlert
import io.hammerhead.karooext.models.PlayBeepPattern
import io.hammerhead.karooexttemplate.R
import io.hammerhead.karooexttemplate.models.BatteryState
import io.hammerhead.karooexttemplate.models.ComponentBatteryInfo
import io.hammerhead.karooexttemplate.models.HydrationManager
import io.hammerhead.karooexttemplate.models.SensorType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class TomsToolboxExtension : KarooExtension("toms-toolbox", "1.2") {

    private val extensionScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private lateinit var karooSystem: KarooSystemService
    private var isAlertActive = false

    private val connectedBatteryComponents = MutableStateFlow<List<ComponentBatteryInfo>>(
        listOf(
            ComponentBatteryInfo("1", "SRAM Rear Derailleur", "Rear AXS", SensorType.SHIFTING_REAR, 80, BatteryState.OK),
            ComponentBatteryInfo("2", "SRAM Front Derailleur", "Front AXS", SensorType.SHIFTING_FRONT, 100, BatteryState.OK),
            ComponentBatteryInfo("3", "Quarq Power Meter", "Powermeter", SensorType.POWER_METER, 100, BatteryState.OK),
            ComponentBatteryInfo("4", "Heart Rate Monitor", "Heart Rate", SensorType.HEART_RATE, 60, BatteryState.OK),
            ComponentBatteryInfo("5", "Speed Sensor", "Speed", SensorType.SPEED, 80, BatteryState.OK)
        )
    )

    override val types: List<DataTypeImpl> = listOf(
        BatteryCheckDataType(extension) { connectedBatteryComponents.value },
        BatteryListDataType(extension) { connectedBatteryComponents.value },
        HydrationDataType(extension)
    )

    override fun onCreate() {
        super.onCreate()

        karooSystem = KarooSystemService(this)
        karooSystem.connect {
            // Connected to Karoo System Service
        }

        // Hydration 1-second countdown ticker & alert trigger
        extensionScope.launch {
            while (isActive) {
                delay(1000)
                HydrationManager.tickSecond()

                val state = HydrationManager.state.value
                if (state.isAlertDue && !isAlertActive) {
                    isAlertActive = true

                    // 1. Play double beep tone
                    karooSystem.dispatch(
                        PlayBeepPattern(
                            tones = listOf(
                                PlayBeepPattern.Tone(frequency = 2800, durationMs = 150),
                                PlayBeepPattern.Tone(frequency = null, durationMs = 100),
                                PlayBeepPattern.Tone(frequency = 3200, durationMs = 250)
                            )
                        )
                    )

                    // 2. Dispatch Karoo In-Ride Alert banner
                    karooSystem.dispatch(
                        InRideAlert(
                            id = "hydration_alert",
                            icon = R.drawable.ic_toolbox_launcher,
                            title = "Drink ${state.sipSizeMl}ml Now! 🚰",
                            detail = "Auto-logging ${state.sipSizeMl}ml in ${state.autoLogSecondsRemaining}s...",
                            autoDismissMs = 8000L,
                            backgroundColor = android.R.color.holo_blue_dark,
                            textColor = android.R.color.white
                        )
                    )
                } else if (!state.isAlertDue) {
                    isAlertActive = false
                }
            }
        }
    }

    override fun onBonusAction(actionId: String) {
        when (actionId) {
            "log_sip", "sip", "intake" -> HydrationManager.logSip()
            "log_bottle", "bottle" -> HydrationManager.logBottle()
            "snooze" -> HydrationManager.snoozeAlert()
            "reset_hydration" -> HydrationManager.resetSession()
        }
    }
}
