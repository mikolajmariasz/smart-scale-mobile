package com.example.smartscale.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "meals")
data class MealEntity(
    @PrimaryKey
    val localId: String = UUID.randomUUID().toString(),
    val remoteId: String? = null,
    val name: String,
    val dateTime: Long,
    val emoji: String,
    val syncStatus: SyncStatus = SyncStatus.TO_SYNC
)