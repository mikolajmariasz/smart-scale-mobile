package com.example.smartscale.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Meal(
    val localId: String,
    val dateTime: Long,
    val name: String,
    val time: String,
    val emoji: String,
    val calories: Float,
    val carbs: Float,
    val protein: Float,
    val fat: Float
) : Parcelable
