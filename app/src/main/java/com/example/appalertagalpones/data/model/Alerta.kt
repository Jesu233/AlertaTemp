package com.example.appalertagalpones.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class AtencionAlerta(val etiqueta: String) {
    PENDIENTE("Pendiente"), EN_ATENCION("En atención"), ATENDIDA("Atendida")
}


@Entity(tableName = "alertas")
data class Alerta(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val galponId: Int,
    val galponNombre: String,
    val tipo: String,
    val temperatura: Double,
    val humedad: Double,
    val mensaje: String,
    val fechaMs: Long,
    val atencion: String = AtencionAlerta.PENDIENTE.name
)
