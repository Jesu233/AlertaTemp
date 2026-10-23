package com.example.appalertagalpones.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "galpones")
data class Galpon(
    @PrimaryKey val id: Int,
    val fundo: String,
    val sector: String,
    val nombre: String,
    val edadDias: Int,
    val operarioId: String,
    val supervisorId: String,
    val temperatura: Double,
    val humedad: Double,
    val actualizadoMs: Long,
    val estado: String
)