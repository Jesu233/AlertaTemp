package com.example.appalertagalpones.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mediciones")
data class Medicion(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val galponId: Int,
    val temperatura: Double,
    val humedad: Double,
    val fechaMs: Long,
    val estado: String
)
