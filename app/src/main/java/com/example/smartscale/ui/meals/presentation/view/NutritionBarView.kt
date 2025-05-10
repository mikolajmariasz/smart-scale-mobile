package com.example.smartscale.ui.meals.presentation.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.example.smartscale.R

class NutritionBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var carbsProgress      = 0f
    private var proteinProgress    = 0f
    private var fatProgress        = 0f

    private var animatedCarbsProg   = 0f
    private var animatedProteinProg = 0f
    private var animatedFatProg     = 0f

    private var carbsText    = "0/0 g"
    private var proteinText  = "0/0 g"
    private var fatText      = "0/0 g"

    private val barPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.white)
        style = Paint.Style.FILL
    }
    private val carbsPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.green)
        style = Paint.Style.FILL
    }
    private val proteinPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.green)
        style = Paint.Style.FILL
    }
    private val fatPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.green)
        style = Paint.Style.FILL
    }
    private val labelTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.green)
        textSize = 48f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }
    private val valueTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.green)
        textSize = 36f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT
    }

    private val animator = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = 500
        addUpdateListener { anim ->
            val frac = anim.animatedValue as Float
            animatedCarbsProg   = carbsProgress   * frac
            animatedProteinProg = proteinProgress * frac
            animatedFatProg     = fatProgress     * frac
            invalidate()
        }
    }

    /**
     * @param current ilość gramów (Float)
     * @param total   cel w gramach (Float)
     */
    fun setCarbsProgress(current: Float, total: Float) {
        carbsProgress = (current / total).coerceIn(0f, 1f)
        carbsText     = String.format("%.1f/%.1f g", current, total)
        animator.start()
    }

    fun setProteinProgress(current: Float, total: Float) {
        proteinProgress = (current / total).coerceIn(0f, 1f)
        proteinText     = String.format("%.1f/%.1f g", current, total)
        animator.start()
    }

    fun setFatProgress(current: Float, total: Float) {
        fatProgress = (current / total).coerceIn(0f, 1f)
        fatText      = String.format("%.1f/%.1f g", current, total)
        animator.start()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val barHeight   = 20f
        val barMargin   = 24f
        val textHeight  = labelTextPaint.textSize + valueTextPaint.textSize + 16f
        val totalHeight = barHeight + textHeight + barMargin * 2

        val desiredH = totalHeight.toInt() + paddingTop + paddingBottom
        val hMode    = MeasureSpec.getMode(heightMeasureSpec)
        val hSize    = MeasureSpec.getSize(heightMeasureSpec)
        val finalH   = if (hMode == MeasureSpec.EXACTLY) hSize
        else if (hMode == MeasureSpec.AT_MOST) minOf(desiredH, hSize)
        else desiredH

        val barWidth    = (MeasureSpec.getSize(widthMeasureSpec) * 0.8f).toInt()
        val totalWidth  = (3 * barWidth + 2 * barMargin).toInt()
        val desiredW    = totalWidth + paddingLeft + paddingRight
        val wMode       = MeasureSpec.getMode(widthMeasureSpec)
        val wSize       = MeasureSpec.getSize(widthMeasureSpec)
        val finalW      = if (wMode == MeasureSpec.EXACTLY) wSize
        else if (wMode == MeasureSpec.AT_MOST) minOf(desiredW, wSize)
        else desiredW

        setMeasuredDimension(finalW, finalH)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val barHeight = 20f
        val barMargin = 36f
        val barWidth  = width * 0.2f
        val startY    = barMargin + labelTextPaint.textSize + 8f
        val totalW    = 3 * barWidth + 2 * barMargin
        val startX    = (width - totalW) / 2

        // rysowanie dla każdego makro
        drawProgressBar(
            canvas, startX, startY, barWidth, barHeight,
            animatedCarbsProg, "Carbs",   carbsText,   carbsPaint
        )
        drawProgressBar(
            canvas, startX + barWidth + barMargin, startY, barWidth, barHeight,
            animatedProteinProg, "Protein", proteinText, proteinPaint
        )
        drawProgressBar(
            canvas, startX + 2*(barWidth + barMargin), startY, barWidth, barHeight,
            animatedFatProg, "Fat", fatText, fatPaint
        )
    }

    private fun drawProgressBar(
        canvas: Canvas,
        startX: Float,
        startY: Float,
        barWidth: Float,
        barHeight: Float,
        progress: Float,
        label: String,
        valueText: String,
        paintProg: Paint
    ) {
        val radius = 10f
        // label
        canvas.drawText(label, startX + barWidth/2, startY - barHeight - 8f, labelTextPaint)
        // tło paska
        canvas.drawRoundRect(startX, startY, startX + barWidth, startY + barHeight,
            radius, radius, barPaint)
        // wypełnienie
        canvas.drawRoundRect(startX, startY, startX + barWidth * progress, startY + barHeight,
            radius, radius, paintProg)
        // tekst wartości
        canvas.drawText(valueText,
            startX + barWidth/2,
            startY + barHeight + valueTextPaint.textSize + 8f,
            valueTextPaint)
    }
}
