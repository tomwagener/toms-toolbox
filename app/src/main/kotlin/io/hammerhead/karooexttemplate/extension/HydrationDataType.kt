package io.hammerhead.karooexttemplate.extension

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.action.actionSendBroadcast
import androidx.glance.action.clickable
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
import io.hammerhead.karooexttemplate.models.HydrationManager
import io.hammerhead.karooexttemplate.models.HydrationState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalGlanceRemoteViewsApi::class)
class HydrationDataType(
    extension: String
) : DataTypeImpl(extension, "hydration_tracker") {

    private val glance = GlanceRemoteViews()

    override fun startView(context: Context, config: ViewConfig, emitter: ViewEmitter) {
        val job = CoroutineScope(Dispatchers.IO).launch {
            // Remove numeric 42 overlay by sending UpdateGraphicConfig
            emitter.onNext(UpdateGraphicConfig(showHeader = true))

            HydrationManager.state.collect { state ->
                val result = glance.compose(context, DpSize.Unspecified) {
                    HydrationGlanceView(context, state)
                }
                emitter.updateView(result.remoteViews)
            }
        }

        emitter.setCancellable {
            job.cancel()
        }
    }
}

@Composable
fun HydrationGlanceView(context: Context, state: HydrationState) {
    val clickIntent = Intent(context, HydrationReceiver::class.java).apply {
        action = HydrationReceiver.ACTION_LOG_SIP
    }

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .padding(6.dp)
            .clickable(actionSendBroadcast(clickIntent)),
        horizontalAlignment = Alignment.Horizontal.Start,
        verticalAlignment = Alignment.Vertical.CenterVertically
    ) {
        Row(
            modifier = GlanceModifier.fillMaxWidth().padding(bottom = 2.dp),
            horizontalAlignment = Alignment.Horizontal.Start
        ) {
            Text(
                text = "🚰 SMART HYDRATION (Tap +150ml)",
                style = TextStyle(color = ColorProvider(Color.LightGray), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            )
        }

        if (state.isAlertDue) {
            Text(
                text = "DRINK NOW! (${state.sipSizeMl}ml)",
                style = TextStyle(
                    color = ColorProvider(Color.Red),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        } else {
            Text(
                text = "Drink in ${state.nextSipMinutes}m (${state.sipSizeMl}ml)",
                style = TextStyle(
                    color = ColorProvider(Color.Cyan),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Text(
            text = "Drunk: ${state.totalDrunkMl}ml (${state.bottlesConsumedString} Bottles)",
            style = TextStyle(color = ColorProvider(Color.White), fontSize = 11.sp)
        )

        Text(
            text = "Rate: ~${state.dynamicLossRateMlPerHour} ml/h | ${state.currentTempC.toInt()}°C",
            style = TextStyle(color = ColorProvider(Color.Gray), fontSize = 10.sp)
        )
    }
}
