package com.example.appalertagalpones.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val local = Locale("es", "CL")

/** Fecha y hora de medición (pregunta 3). */
fun formatoFechaHora(ms: Long): String = SimpleDateFormat("dd/MM/yyyy HH:mm", local).format(Date(ms))

fun formatoHora(ms: Long): String = SimpleDateFormat("HH:mm", local).format(Date(ms))

fun Double.grados(): String = "${"%.1f".format(this)} °C"

fun Double.porcentaje(): String = "${"%.0f".format(this)} %"
