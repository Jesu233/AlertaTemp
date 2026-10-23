package com.example.appalertagalpones.data.model

enum class Estado(val etiqueta: String, val recomendacion: String) {
    NORMAL("Normal", "Condiciones dentro del rango esperado. Mantener las rondas de inspección."),
    ADVERTENCIA("Advertencia", "Revisar las condiciones del galpón y verificar los sistemas de ventilación."),
    CRITICO("Crítico", "Alerta prioritaria: notificar al supervisor y registrar las acciones correctivas (simuladas).")
}

fun String.toEstado(): Estado = Estado.valueOf(this)


data class Umbrales(
    val tempAdvertencia: Double = 28.0,
    val tempCritico: Double = 32.0,
    val humAdvertencia: Double = 70.0,
    val humCritico: Double = 80.0
)

data class Evaluacion(val estado: Estado, val motivo: String)

object EvaluadorEstado {
    fun evaluar(temperatura: Double, humedad: Double, u: Umbrales): Evaluacion {
        val porTemp = when {
            temperatura >= u.tempCritico -> Estado.CRITICO
            temperatura >= u.tempAdvertencia -> Estado.ADVERTENCIA
            else -> Estado.NORMAL
        }
        val porHum = when {
            humedad >= u.humCritico -> Estado.CRITICO
            humedad >= u.humAdvertencia -> Estado.ADVERTENCIA
            else -> Estado.NORMAL
        }
        val estado = if (porTemp.ordinal >= porHum.ordinal) porTemp else porHum
        val partes = buildList {
            if (porTemp != Estado.NORMAL) add("temperatura ${"%.1f".format(temperatura)} °C")
            if (porHum != Estado.NORMAL) add("humedad ${"%.0f".format(humedad)} %")
        }
        val motivo = if (partes.isEmpty()) "Valores dentro del rango"
        else partes.joinToString(" y ").replaceFirstChar { it.uppercase() } + " sobre el umbral de ${estado.etiqueta.lowercase()}"
        return Evaluacion(estado, motivo)
    }
}
