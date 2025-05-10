package com.example.smartscale.ui.common.decorations

import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.util.TypedValue
import androidx.recyclerview.widget.RecyclerView

class FadeEdgeDecoration(context: Context, private val fadeHeight: Int) : RecyclerView.ItemDecoration() {
    private val paint = Paint()
    private val backgroundColor: Int

    init {
        val typedValue = TypedValue()
        val theme = context.theme
        theme.resolveAttribute(android.R.attr.windowBackground, typedValue, true)
        backgroundColor = typedValue.data
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val width = parent.width

        paint.shader = LinearGradient(
            0f, 0f, 0f, fadeHeight.toFloat(),
            backgroundColor, 0x00FFFFFF, Shader.TileMode.CLAMP
        )
        c.drawRect(0f, 0f, width.toFloat(), fadeHeight.toFloat(), paint)
    }
}
