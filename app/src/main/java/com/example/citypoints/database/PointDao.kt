package com.example.citypoints.database

import androidx.room.*

@Dao
interface PointDao {

    @Insert
    suspend fun insert(point: SustainablePoint)

    @Update
    suspend fun update(point: SustainablePoint)

    @Delete
    suspend fun delete(point: SustainablePoint)

    @Query("SELECT * FROM points ORDER BY id DESC")
    suspend fun getAllPoints(): List<SustainablePoint>
}