package io.hammerhead.karooexttemplate.models

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class HydrationState(
    val baseRateMlPerHour: Int = 500,
    val bottleSizeMl: Int = 750,
    val sipSizeMl: Int = 150,
    val totalDrunkMl: Int = 0,
    val currentTempC: Double = 22.0,
    val currentPowerW: Double = 180.0,
    val currentHrBpm: Double = 135.0,
    val dynamicLossRateMlPerHour: Int = 540,
    val secondsUntilNextSip: Int = 900,
    val isAlertDue: Boolean = false,
    val autoLogSecondsRemaining: Int = 8
) {
    val bottlesConsumed: Double
        get() = if (bottleSizeMl > 0) totalDrunkMl.toDouble() / bottleSizeMl else 0.0

    val bottlesConsumedString: String
        get() = String.format("%.1f", bottlesConsumed)

    val nextSipMinutes: Int
        get() = (secondsUntilNextSip / 60).coerceAtLeast(0)
}

object HydrationManager {

    private val _state = MutableStateFlow(HydrationState())
    val state: StateFlow<HydrationState> = _state.asStateFlow()

    fun updateSettings(baseRateMlPerHour: Int, bottleSizeMl: Int, sipSizeMl: Int) {
        val current = _state.value
        val newState = current.copy(
            baseRateMlPerHour = baseRateMlPerHour.coerceIn(300, 1200),
            bottleSizeMl = bottleSizeMl.coerceIn(300, 1500),
            sipSizeMl = sipSizeMl.coerceIn(50, 300)
        )
        _state.value = recalculate(newState)
    }

    fun updateSensors(tempC: Double?, powerW: Double?, hrBpm: Double?) {
        val current = _state.value
        val newState = current.copy(
            currentTempC = tempC ?: current.currentTempC,
            currentPowerW = powerW ?: current.currentPowerW,
            currentHrBpm = hrBpm ?: current.currentHrBpm
        )
        _state.value = recalculate(newState)
    }

    fun tickSecond() {
        val current = _state.value
        if (!current.isAlertDue) {
            val newTimer = current.secondsUntilNextSip - 1
            if (newTimer <= 0) {
                _state.value = current.copy(
                    secondsUntilNextSip = 0,
                    isAlertDue = true,
                    autoLogSecondsRemaining = 8
                )
            } else {
                _state.value = current.copy(secondsUntilNextSip = newTimer)
            }
        } else {
            // Auto-Log countdown ticker while alert is due
            val newAutoLog = current.autoLogSecondsRemaining - 1
            if (newAutoLog <= 0) {
                // Auto-Log sip hands-free!
                logSip()
            } else {
                _state.value = current.copy(autoLogSecondsRemaining = newAutoLog)
            }
        }
    }

    fun logSip() {
        val current = _state.value
        val newTotal = current.totalDrunkMl + current.sipSizeMl
        val resetTimer = calculateSipIntervalSeconds(current)
        _state.value = current.copy(
            totalDrunkMl = newTotal,
            secondsUntilNextSip = resetTimer,
            isAlertDue = false,
            autoLogSecondsRemaining = 8
        )
    }

    fun logBottle() {
        val current = _state.value
        val newTotal = current.totalDrunkMl + current.bottleSizeMl
        val resetTimer = calculateSipIntervalSeconds(current)
        _state.value = current.copy(
            totalDrunkMl = newTotal,
            secondsUntilNextSip = resetTimer,
            isAlertDue = false,
            autoLogSecondsRemaining = 8
        )
    }

    fun snoozeAlert() {
        val current = _state.value
        // Snooze for 5 minutes (300 seconds)
        _state.value = current.copy(
            secondsUntilNextSip = 300,
            isAlertDue = false,
            autoLogSecondsRemaining = 8
        )
    }

    fun resetSession() {
        val current = _state.value
        val resetTimer = calculateSipIntervalSeconds(current)
        _state.value = current.copy(
            totalDrunkMl = 0,
            secondsUntilNextSip = resetTimer,
            isAlertDue = false,
            autoLogSecondsRemaining = 8
        )
    }

    private fun recalculate(state: HydrationState): HydrationState {
        val tempDelta = (state.currentTempC - 20.0).coerceAtLeast(0.0)
        val tempFactor = 1.0 + (tempDelta * 0.025)

        val effortFactor = when {
            state.currentPowerW > 250.0 || state.currentHrBpm > 165.0 -> 1.5
            state.currentPowerW > 200.0 || state.currentHrBpm > 150.0 -> 1.3
            state.currentPowerW > 160.0 || state.currentHrBpm > 135.0 -> 1.1
            else -> 1.0
        }

        val calculatedLossRate = (state.baseRateMlPerHour * tempFactor * effortFactor).toInt()
        val sipInterval = calculateSipIntervalSeconds(state.copy(dynamicLossRateMlPerHour = calculatedLossRate))

        return state.copy(
            dynamicLossRateMlPerHour = calculatedLossRate,
            secondsUntilNextSip = if (state.secondsUntilNextSip > sipInterval) sipInterval else state.secondsUntilNextSip
        )
    }

    private fun calculateSipIntervalSeconds(state: HydrationState): Int {
        val lossPerSecond = state.dynamicLossRateMlPerHour / 3600.0
        if (lossPerSecond <= 0) return 900
        return (state.sipSizeMl / lossPerSecond).toInt().coerceIn(120, 1800)
    }
}
