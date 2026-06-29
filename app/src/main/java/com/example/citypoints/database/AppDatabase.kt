package com.example.citypoints.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [SustainablePoint::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun pointDao(): PointDao

}