package com.example.smartscale.ui.meals.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import com.example.smartscale.R

class CircularProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var progress = 0f
    private var animatedProgress = 0f
    private var caloriesText = ""

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.green)
        style = Paint.Style.STROKE
        strokeWidth = 30f
        strokeCap = Paint.Cap.ROUND
    }

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.white)
        style = Paint.Style.STROKE
        strokeWidth = 30f
        strokeCap = Paint.Cap.ROUND
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.green)
        textSize = 64f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }

    private val animator = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = 500
        addUpdateListener {
            animatedProgress = it.animatedValue as Float
            invalidate()
        }
    }

    /**
     * Sets the progress of the circular progress bar.
     */
    fun setProgress(targetProgress: Float) {
        this.progress = targetProgress.coerceIn(0f, 1f)
        animator.setFloatValues(animatedProgress, progress)
        animator.start()
    }

    /**
     * Sets the calories text of the circular progress bar.
     */
    fun setCaloriesText(text: String) {
        caloriesText = text
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val radius = (width / 2 * 0.8).toFloat()

        val centerX = width / 2f
        val centerY = height / 2f

        /**
         * Draw the background of the circular progress bar
         */
        canvas.drawArc(
            centerX - radius, centerY - radius,
            centerX + radius, centerY + radius,
            -90f,
            360f,
            false,
            backgroundPaint
        )

        val sweepAngle = 360 * animatedProgress

        /**
         * Draw the circular progress bar
         */
        canvas.drawArc(
            centerX - radius, centerY - radius,
            centerX + radius, centerY + radius,
            -90f,
            sweepAngle,
            false,
            paint
        )

        /**
         * Draw the calories text
         */
        canvas.drawText(
            caloriesText,
            centerX,
            centerY + textPaint.textSize / 3,
            textPaint
        )
    }
}