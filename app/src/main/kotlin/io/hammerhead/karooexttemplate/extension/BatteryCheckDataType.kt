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
import androidx.glance.layout.fillMaxSize
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalGlanceRemoteViewsApi::class)
class BatteryCheckDataType(
    extension: String,
    private val componentsSupplier: () -> List<ComponentBatteryInfo>
) : DataTypeImpl(extension, "battery_check") {

    private val glance = GlanceRemoteViews()

    override fun startView(context: Context, config: ViewConfig, emitter: ViewEmitter) {
        val job = CoroutineScope(Dispatchers.IO).launch {
            // Disable default numeric 42 overlay by sending UpdateGraphicConfig
            emitter.onNext(UpdateGraphicConfig(showHeader = true))

            val components = componentsSupplier()
            val summary = BatteryEvaluator.evaluate(components)
            val result = glance.compose(context, DpSize.Unspecified) {
                BatteryCheckGlanceView(summary.statusText, summary.isAnyLow)
            }
            emitter.updateView(result.remoteViews)
        }
        emitter.setCancellable {
            job.cancel()
        }
    }
}

@Composable
fun BatteryCheckGlanceView(statusText: String, isAnyLow: Boolean) {
    Column(
        modifier = GlanceModifier.fillMaxSize().padding(6.dp),
        horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
        verticalAlignment = Alignment.Vertical.CenterVertically
    ) {
        Text(
            text = "BATTERY CHECK",
            style = TextStyle(
                color = ColorProvider(Color.LightGray),
                fontSize = 11.sp
            )
        )
        Text(
            text = statusText,
            style = TextStyle(
                color = ColorProvider(if (isAnyLow) Color.Red else Color.Green),
                fontSize = if (isAnyLow) 18.sp else 28.sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
}
