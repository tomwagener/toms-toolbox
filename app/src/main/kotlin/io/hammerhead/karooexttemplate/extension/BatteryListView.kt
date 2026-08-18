package io.hammerhead.karooexttemplate.extension

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import io.hammerhead.karooexttemplate.models.BatteryEvaluator
import io.hammerhead.karooexttemplate.models.ComponentBatteryInfo

class BatteryListView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var components: List<ComponentBatteryInfo> = emptyList()

    private val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.LTGRAY
        textSize = 26f
        textAlign = Paint.Align.LEFT
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 28f
        textAlign = Paint.Align.LEFT
    }

    private val detailPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.GRAY
        textSize = 24f
        textAlign = Paint.Align.RIGHT
    }

    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#121212")
        style = Paint.Style.FILL
    }

    private val cardPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#1E1E1E")
        style = Paint.Style.FILL
    }

    private val cardRect = RectF()

    fun updateComponents(newComponents: List<ComponentBatteryInfo>) {
        this.components = newComponents
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width.toFloat()
        val h = height.toFloat()
        if (w <= 0 || h <= 0) return

        // Background
        canvas.drawColor(Color.parseColor("#121212"))

        // Outer Card
        cardRect.set(8f, 8f, w - 8f, h - 8f)
        canvas.drawRoundRect(cardRect, 16f, 16f, cardPaint)

        val summary = BatteryEvaluator.evaluate(components)

        // Header Title
        titlePaint.textSize = (h * 0.08f).coerceIn(20f, 30f)
        canvas.drawText("BATTERIE LISTE", 24f, 40f, titlePaint)

        // Header Status Pill on right
        val headerStatusText = if (summary.isAnyLow) summary.statusText else "ALL OK"
        val headerColor = if (summary.isAnyLow) Color.parseColor("#FF5252") else Color.parseColor("#4CAF50")
        detailPaint.color = headerColor
        detailPaint.textSize = (h * 0.08f).coerceIn(20f, 30f)
        detailPaint.isFakeBoldText = true
        canvas.drawText(headerStatusText, w - 24f, 40f, detailPaint)
        detailPaint.isFakeBoldText = false

        val displayList = if (components.isEmpty()) getSampleComponents() else components
        val startY = 80f
        val availableHeight = h - startY - 16f
        val itemHeight = (availableHeight / displayList.size.coerceAtLeast(1)).coerceIn(36f, 70f)

        textPaint.textSize = (itemHeight * 0.42f).coerceIn(18f, 28f)
        detailPaint.textSize = (itemHeight * 0.38f).coerceIn(16f, 24f)

        displayList.forEachIndexed { index, comp ->
            val y = startY + (index * itemHeight) + (itemHeight * 0.65f)
            if (y < h - 10f) {
                // Icon + Short Name
                textPaint.color = Color.WHITE
                val nameText = "${comp.type.iconSymbol} ${comp.shortName}"
                canvas.drawText(nameText, 24f, y, textPaint)

                // Percentage + Estimated Runtime
                detailPaint.color = when {
                    comp.isLowOrCritical -> Color.parseColor("#FF5252")
                    comp.percentage != null && comp.percentage <= 40 -> Color.parseColor("#FFB74D")
                    else -> Color.parseColor("#81C784")
                }
                val detailText = "${comp.displayPercentageString} (${comp.estimatedRuntimeString})"
                canvas.drawText(detailText, w - 24f, y, detailPaint)
            }
        }
    }

    private fun getSampleComponents(): List<ComponentBatteryInfo> {
        return listOf(
            ComponentBatteryInfo("1", "SRAM Rear Derailleur", "Rear AXS", io.hammerhead.karooexttemplate.models.SensorType.SHIFTING_REAR, 60, io.hammerhead.karooexttemplate.models.BatteryState.OK),
            ComponentBatteryInfo("2", "SRAM Front Derailleur", "Front AXS", io.hammerhead.karooexttemplate.models.SensorType.SHIFTING_FRONT, 80, io.hammerhead.karooexttemplate.models.BatteryState.OK),
            ComponentBatteryInfo("3", "Quarq Power Meter", "Powermeter", io.hammerhead.karooexttemplate.models.SensorType.POWER_METER, 100, io.hammerhead.karooexttemplate.models.BatteryState.OK),
            ComponentBatteryInfo("4", "Garmin HRM-Pro", "Pulsgurt", io.hammerhead.karooexttemplate.models.SensorType.HEART_RATE, 40, io.hammerhead.karooexttemplate.models.BatteryState.OK),
            ComponentBatteryInfo("5", "Wahoo Speed", "Speed Sensor", io.hammerhead.karooexttemplate.models.SensorType.SPEED, 20, io.hammerhead.karooexttemplate.models.BatteryState.LOW)
        )
    }
}
