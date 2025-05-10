package com.example.smartscale.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.smartscale.data.local.dao.IngredientDao
import com.example.smartscale.data.local.dao.MealDao
import com.example.smartscale.data.local.entity.IngredientEntity
import com.example.smartscale.data.local.entity.MealEntity

@Database(
    entities = [ MealEntity::class, IngredientEntity::class ],
    version  = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun mealDao(): MealDao
    abstract fun ingredientDao(): IngredientDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(ctx: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(ctx).also { INSTANCE = it }
            }

        private fun buildDatabase(ctx: Context): AppDatabase =
            Room.databaseBuilder(
                ctx.applicationContext,
                AppDatabase::class.java,
                "smartscale.db"
            )
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
    }
}
