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
    fun getAllPoints(): kotlinx.coroutines.flow.Flow<List<SustainablePoint>>
    @Query("SELECT * FROM points WHERE id = :id")
    fun getPointById(id: Int): kotlinx.coroutines.flow.Flow<SustainablePoint?>

    @Query("SELECT * FROM points WHERE id = :id")
    suspend fun getPointByIdOnce(id: Int): SustainablePoint?
}