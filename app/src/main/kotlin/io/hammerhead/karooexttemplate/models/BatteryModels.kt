package io.hammerhead.karooexttemplate.models

enum class BatteryState {
    OK,
    LOW,
    CRITICAL,
    UNKNOWN
}

enum class SensorType(val displayName: String, val iconSymbol: String) {
    SHIFTING_REAR("Rear AXS", "⚙️"),
    SHIFTING_FRONT("Front AXS", "⚙️"),
    SHIFTER_LEFT("Shifter L", "🔘"),
    SHIFTER_RIGHT("Shifter R", "🔘"),
    POWER_METER("Powermeter", "⚡"),
    HEART_RATE("Heart Rate", "🔴"),
    SPEED("Speed Sensor", "🚴"),
    CADENCE("Cadence Sensor", "🚴"),
    RADAR("Radar", "📡"),
    OTHER("Sensor", "🔋")
}

data class ComponentBatteryInfo(
    val id: String,
    val name: String,
    val shortName: String,
    val type: SensorType,
    val percentage: Int?,
    val state: BatteryState
) {
    val isLowOrCritical: Boolean
        get() = state == BatteryState.LOW || state == BatteryState.CRITICAL || (percentage != null && percentage <= 20)

    val displayPercentageString: String
        get() = when {
            percentage != null -> "$percentage%"
            state == BatteryState.OK -> "OK"
            state == BatteryState.LOW -> "20%"
            state == BatteryState.CRITICAL -> "<10%"
            else -> "--"
        }

    val estimatedRuntimeString: String
        get() = when (type) {
            SensorType.SHIFTING_REAR, SensorType.SHIFTING_FRONT -> {
                when {
                    percentage != null && percentage > 80 -> "~15-20 hrs"
                    percentage != null && percentage > 40 -> "~8-15 hrs"
                    percentage != null && percentage > 20 -> "~5-8 hrs"
                    state == BatteryState.LOW || (percentage != null && percentage <= 20) -> "~2-5 hrs ⚠️"
                    state == BatteryState.CRITICAL || (percentage != null && percentage <= 10) -> "<1-2 hrs 🚨"
                    else -> "Good"
                }
            }
            SensorType.POWER_METER -> {
                when {
                    percentage != null && percentage > 40 -> ">30 hrs"
                    state == BatteryState.LOW || (percentage != null && percentage <= 20) -> "~5-10 hrs ⚠️"
                    state == BatteryState.CRITICAL || (percentage != null && percentage <= 10) -> "<5 hrs 🚨"
                    else -> "Good"
                }
            }
            SensorType.HEART_RATE, SensorType.SPEED, SensorType.CADENCE, SensorType.SHIFTER_LEFT, SensorType.SHIFTER_RIGHT -> {
                when {
                    state == BatteryState.LOW || (percentage != null && percentage <= 20) -> "~5-10 hrs (Replace) ⚠️"
                    state == BatteryState.CRITICAL || (percentage != null && percentage <= 10) -> "<2 hrs (Replace!) 🚨"
                    else -> "Good (>50 hrs)"
                }
            }
            else -> {
                when {
                    state == BatteryState.LOW || (percentage != null && percentage <= 20) -> "Low ⚠️"
                    state == BatteryState.CRITICAL || (percentage != null && percentage <= 10) -> "Critical 🚨"
                    else -> "Good"
                }
            }
        }
}

data class OverallBatterySummary(
    val isAnyLow: Boolean,
    val statusText: String,
    val lowComponents: List<ComponentBatteryInfo>,
    val allComponents: List<ComponentBatteryInfo>
)

object BatteryEvaluator {
    fun evaluate(components: List<ComponentBatteryInfo>): OverallBatterySummary {
        if (components.isEmpty()) {
            return OverallBatterySummary(
                isAnyLow = false,
                statusText = "OK",
                lowComponents = emptyList(),
                allComponents = emptyList()
            )
        }

        val lowList = components.filter { it.isLowOrCritical }

        return if (lowList.isEmpty()) {
            OverallBatterySummary(
                isAnyLow = false,
                statusText = "OK",
                lowComponents = emptyList(),
                allComponents = components
            )
        } else {
            val statusText = if (lowList.size == 1) {
                "LOW: ${lowList.first().shortName}"
            } else {
                "LOW: ${lowList.joinToString(", ") { it.shortName }}"
            }
            OverallBatterySummary(
                isAnyLow = true,
                statusText = statusText,
                lowComponents = lowList,
                allComponents = components
            )
        }
    }
}
