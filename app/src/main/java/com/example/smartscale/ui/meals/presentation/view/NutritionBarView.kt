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

    private var carbsProgress = 0f
    private var proteinProgress = 0f
    private var fatProgress = 0f

    private var animatedCarbsProgress = 0f
    private var animatedProteinProgress = 0f
    private var animatedFatProgress = 0f

    private var carbsText = "0/0 g"
    private var proteinText = "0/0 g"
    private var fatText = "0/0 g"

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
        textSize = 44f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT
    }

    private val animator = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = 500
        addUpdateListener {
            animatedCarbsProgress = carbsProgress * (it.animatedValue as Float)
            animatedProteinProgress = proteinProgress * (it.animatedValue as Float)
            animatedFatProgress = fatProgress * (it.animatedValue as Float)
            invalidate()
        }
    }

    fun setCarbsProgress(current: Int, total: Int) {
        this.carbsProgress = (current.toFloat() / total).coerceIn(0f, 1f)
        this.carbsText = "$current/$total g"
        animator.start()
    }

    fun setProteinProgress(current: Int, total: Int) {
        this.proteinProgress = (current.toFloat() / total).coerceIn(0f, 1f)
        this.proteinText = "$current/$total g"
        animator.start()
    }

    fun setFatProgress(current: Int, total: Int) {
        this.fatProgress = (current.toFloat() / total).coerceIn(0f, 1f)
        this.fatText = "$current/$total g"
        animator.start()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val barHeight = 20f
        val barMargin = 24f
        val textHeight = labelTextPaint.textSize + valueTextPaint.textSize + 16f // marginesy
        val totalHeight = barHeight + textHeight + barMargin * 2

        val desiredHeight = totalHeight.toInt() + paddingTop + paddingBottom

        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val finalHeight = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> minOf(desiredHeight, heightSize)
            else -> desiredHeight
        }

        val barWidth = (MeasureSpec.getSize(widthMeasureSpec) * 0.8f).toInt()
        val totalWidth = (3 * barWidth + 2 * barMargin).toInt()
        val desiredWidth = totalWidth + paddingLeft + paddingRight

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)

        val finalWidth = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST -> minOf(desiredWidth, widthSize)
            else -> desiredWidth
        }

        setMeasuredDimension(finalWidth, finalHeight)
    }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val barHeight = 20f
        val barMargin = 36f
        val barWidth = width * 0.2f
        val startY = barMargin + labelTextPaint.textSize + 8f // Space for the label

        val totalWidth = 3 * barWidth + 2 * barMargin

        val startX = (width - totalWidth) / 2

        drawProgressBar(canvas, startX, startY, barWidth, barHeight, animatedCarbsProgress, "Carbs", carbsText, carbsPaint)

        drawProgressBar(canvas, startX + barWidth + barMargin, startY, barWidth, barHeight, animatedProteinProgress, "Protein", proteinText, proteinPaint)

        drawProgressBar(canvas, startX + 2 * (barWidth + barMargin), startY, barWidth, barHeight, animatedFatProgress, "Fat", fatText, fatPaint)
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
        progressPaint: Paint
    ) {
        val cornerRadius = 10f

        canvas.drawText(
            label,
            startX + barWidth / 2,
            startY - barHeight - 8f,
            labelTextPaint,
        )

        canvas.drawRoundRect(
            startX, startY,
            startX + barWidth, startY + barHeight,
            cornerRadius, cornerRadius,
            barPaint
        )

        canvas.drawRoundRect(
            startX, startY,
            startX + barWidth * progress, startY + barHeight,
            cornerRadius, cornerRadius,
            progressPaint
        )

        canvas.drawText(
            valueText,
            startX + barWidth / 2,
            startY + barHeight + valueTextPaint.textSize + 8f,
            valueTextPaint
        )
    }
}