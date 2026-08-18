package io.hammerhead.karooexttemplate.extension

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.ExperimentalGlanceRemoteViewsApi
import androidx.glance.appwidget.GlanceRemoteViews
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import io.hammerhead.karooext.extension.DataTypeImpl
import io.hammerhead.karooext.internal.ViewEmitter
import io.hammerhead.karooext.models.UpdateGraphicConfig
import io.hammerhead.karooext.models.ViewConfig
import io.hammerhead.karooexttemplate.models.BatteryEvaluator
import io.hammerhead.karooexttemplate.models.ComponentBatteryInfo
import io.hammerhead.karooexttemplate.models.SensorType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@OptIn(ExperimentalGlanceRemoteViewsApi::class)
class BatteryListDataType(
    extension: String,
    private val componentsSupplier: () -> List<ComponentBatteryInfo>
) : DataTypeImpl(extension, "battery_list") {

    private val glance = GlanceRemoteViews()

    override fun startView(context: Context, config: ViewConfig, emitter: ViewEmitter) {
        val job = CoroutineScope(Dispatchers.IO).launch {
            // Remove numeric 42 overlay by sending UpdateGraphicConfig
            emitter.onNext(UpdateGraphicConfig(showHeader = true))

            var components = componentsSupplier()
            if (components.isEmpty()) {
                components = getSampleComponents()
            }
            val summary = BatteryEvaluator.evaluate(components)

            // Determine visible page size based on view height (pixel size in config.viewSize.second)
            val viewHeightPx = config.viewSize.second
            val maxVisible = when {
                viewHeightPx > 220 -> 8
                viewHeightPx > 140 -> 5
                else -> 3
            }

            var pageIndex = 0

            while (isActive) {
                val totalItems = components.size
                val pageCount = if (totalItems <= maxVisible) 1 else ((totalItems + maxVisible - 1) / maxVisible)
                val currentPage = pageIndex % pageCount

                val visibleComponents = if (totalItems <= maxVisible) {
                    components
                } else {
                    val fromIndex = currentPage * maxVisible
                    components.subList(fromIndex, (fromIndex + maxVisible).coerceAtMost(totalItems))
                }

                val result = glance.compose(context, DpSize.Unspecified) {
                    BatteryListGlanceView(
                        components = visibleComponents,
                        statusText = summary.statusText,
                        isAnyLow = summary.isAnyLow,
                        currentPage = currentPage + 1,
                        totalPages = pageCount
                    )
                }
                emitter.updateView(result.remoteViews)

                if (pageCount > 1) {
                    delay(3000) // Auto-scroll / cycle page every 3 seconds
                    pageIndex++
                } else {
                    delay(5000)
                }
            }
        }

        emitter.setCancellable {
            job.cancel()
        }
    }

    private fun getSampleComponents(): List<ComponentBatteryInfo> {
        return listOf(
            ComponentBatteryInfo("1", "SRAM Rear Derailleur", "Rear AXS", SensorType.SHIFTING_REAR, 60, io.hammerhead.karooexttemplate.models.BatteryState.OK),
            ComponentBatteryInfo("2", "SRAM Front Derailleur", "Front AXS", SensorType.SHIFTING_FRONT, 80, io.hammerhead.karooexttemplate.models.BatteryState.OK),
            ComponentBatteryInfo("3", "Quarq Power Meter", "Powermeter", SensorType.POWER_METER, 100, io.hammerhead.karooexttemplate.models.BatteryState.OK),
            ComponentBatteryInfo("4", "Garmin HRM-Pro", "Heart Rate", SensorType.HEART_RATE, 40, io.hammerhead.karooexttemplate.models.BatteryState.OK),
            ComponentBatteryInfo("5", "Wahoo Speed", "Speed Sensor", SensorType.SPEED, 20, io.hammerhead.karooexttemplate.models.BatteryState.LOW)
        )
    }
}

@Composable
fun BatteryListGlanceView(
    components: List<ComponentBatteryInfo>,
    statusText: String,
    isAnyLow: Boolean,
    currentPage: Int,
    totalPages: Int
) {
    Column(
        modifier = GlanceModifier.fillMaxSize().padding(4.dp),
        horizontalAlignment = Alignment.Horizontal.Start
    ) {
        Row(
            modifier = GlanceModifier.fillMaxWidth().padding(bottom = 2.dp),
            horizontalAlignment = Alignment.Horizontal.Start
        ) {
            val pageInfo = if (totalPages > 1) " ($currentPage/$totalPages)" else ""
            Text(
                text = "BATTERY LIST$pageInfo",
                style = TextStyle(color = ColorProvider(Color.LightGray), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            )
        }

        components.forEach { comp ->
            Row(
                modifier = GlanceModifier.fillMaxWidth().padding(vertical = 1.dp),
                horizontalAlignment = Alignment.Horizontal.Start
            ) {
                Text(
                    text = "${comp.type.iconSymbol} ${comp.shortName}: ",
                    style = TextStyle(color = ColorProvider(Color.White), fontSize = 11.sp)
                )
                Text(
                    text = "${comp.displayPercentageString} (${comp.estimatedRuntimeString})",
                    style = TextStyle(
                        color = ColorProvider(
                            when {
                                comp.isLowOrCritical -> Color.Red
                                comp.percentage != null && comp.percentage <= 40 -> Color.Yellow
                                else -> Color.Green
                            }
                        ),
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}
