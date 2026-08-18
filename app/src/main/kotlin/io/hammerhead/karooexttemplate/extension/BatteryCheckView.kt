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

class BatteryCheckView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var components: List<ComponentBatteryInfo> = emptyList()

    private val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.LTGRAY
        textSize = 28f
        textAlign = Paint.Align.CENTER
    }

    private val statusPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.GREEN
        textSize = 64f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }

    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
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

        // Fill background
        cardRect.set(10f, 10f, w - 10f, h - 10f)
        canvas.drawRoundRect(cardRect, 20f, 20f, bgPaint)

        val summary = BatteryEvaluator.evaluate(components)

        // Title at top
        titlePaint.textSize = (h * 0.14f).coerceIn(20f, 34f)
        canvas.drawText("BATTERIE CHECK", w / 2f, h * 0.28f, titlePaint)

        // Main Status
        if (summary.isAnyLow) {
            statusPaint.color = Color.parseColor("#FF5252") // Red
            statusPaint.textSize = (h * 0.22f).coerceIn(28f, 56f)
            canvas.drawText(summary.statusText, w / 2f, h * 0.62f, statusPaint)
        } else {
            statusPaint.color = Color.parseColor("#4CAF50") // Green
            statusPaint.textSize = (h * 0.32f).coerceIn(40f, 80f)
            canvas.drawText("OK", w / 2f, h * 0.68f, statusPaint)
        }
    }
}
