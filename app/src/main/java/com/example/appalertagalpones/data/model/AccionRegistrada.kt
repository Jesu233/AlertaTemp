package com.example.appalertagalpones.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TipoAccion(val etiqueta: String) {
    REVISION("Revisión de condiciones"),
    VENTILACION("Verificación de ventilación"),
    BEBEDEROS("Ajuste de bebederos"),
    AVISO_SUPERVISOR("Aviso al supervisor"),
    OTRA("Otra acción")
}


@Entity(tableName = "acciones")
data class AccionRegistrada(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val usuarioId: String,
    val usuarioNombre: String,
    val galponId: Int,
    val galponNombre: String,
    val tipo: String,
    val descripcion: String,
    val fotoPath: String?,
    val fechaMs: Long
)
