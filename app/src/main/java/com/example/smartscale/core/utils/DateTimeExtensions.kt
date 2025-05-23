package com.example.smartscale.core.utils

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import java.util.*

object DateTimePicker {
    fun pickDateTime(
        context: Context,
        initial: Calendar = Calendar.getInstance(),
        onPicked: (Calendar) -> Unit
    ) {
        val now = initial
        DatePickerDialog(context, { _, y, m, d ->
            TimePickerDialog(context, { _, h, min ->
                now.set(y, m, d, h, min)
                onPicked(now)
            }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true)
                .show()
        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))
            .show()
    }
}
