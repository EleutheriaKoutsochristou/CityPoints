package com.example.citypoints.database

import androidx.room.Database
import androidx.room.RoomDatabase

import androidx.room.Room
import android.content.Context

@Database(
    entities = [SustainablePoint::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun pointDao(): PointDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "citypoints_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}