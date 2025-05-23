package com.example.smartscale.ui.common.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
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
     * Ustawia procentowy postęp (0f..1f) i animuje.
     */
    fun setProgress(targetProgress: Float) {
        progress = targetProgress.coerceIn(0f, 1f)
        animator.setFloatValues(animatedProgress, progress)
        animator.start()
    }

    /**
     * Ustawia dowolny tekst wyświetlany w środku (np. "2000/2800 kcal").
     */
    fun setCaloriesText(text: String) {
        caloriesText = text
        invalidate()
    }

    /**
     * Wygodna metoda: podajesz spożyte i docelowe kcal, a klasa sama liczy `progress`
     * i ustawia odpowiedni tekst.
     */
    fun setCalories(consumed: Float, goal: Float) {
        val safeGoal = goal.coerceAtLeast(1f)
        val pct = (consumed / safeGoal).coerceIn(0f, 1f)
        setProgress(pct)

        // formatowanie liczb: jeśli całkowite, bez miejsc po przecinku, inaczej z jedną
        caloriesText = if (consumed % 1f == 0f && goal % 1f == 0f) {
            String.format("%.0f/%.0f kcal", consumed, goal)
        } else {
            String.format("%.1f/%.1f kcal", consumed, goal)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val radius = (width * 0.8f / 2f)
        val cx = width / 2f
        val cy = height / 2f

        // tło koła
        canvas.drawArc(cx - radius, cy - radius, cx + radius, cy + radius,
            -90f, 360f, false, backgroundPaint)

        // wypełniony łuk
        canvas.drawArc(cx - radius, cy - radius, cx + radius, cy + radius,
            -90f, 360 * animatedProgress, false, paint)

        // tekst w środku
        canvas.drawText(caloriesText, cx, cy + textPaint.textSize/3, textPaint)
    }
}
