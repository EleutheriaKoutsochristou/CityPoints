package com.example.citypoints.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "points")
data class SustainablePoint(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String,

    val category: String,

    val rating: Float,

    val status: String,

    val latitude: Double,

    val longitude: Double,

    val photoPath: String,

    val comments: String
)