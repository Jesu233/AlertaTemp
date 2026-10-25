package com.example.appalertagalpones.ui.viewmodel

object Validadores {
    fun usuario(v: String): String? = when {
        v.isBlank() -> "Ingresa tu usuario"
        v.trim().length < 4 -> "Mínimo 4 caracteres"
        v.any { it.isWhitespace() } -> "No puede contener espacios"
        else -> null
    }

    fun clave(v: String): String? = when {
        v.isBlank() -> "Ingresa tu contraseña"
        v.length < 8 -> "Mínimo 8 caracteres"
        v.none { it.isDigit() } -> "Debe incluir al menos un número"
        v.none { it.isUpperCase() } -> "Debe incluir una mayúscula"
        else -> null
    }

    fun descripcion(v: String): String? = when {
        v.isBlank() -> "Describe la acción realizada"
        v.trim().length < 10 -> "Mínimo 10 caracteres"
        v.length > 200 -> "Máximo 200 caracteres"
        else -> null
    }

    fun numero(v: String, min: Double, max: Double, unidad: String): String? {
        val n = v.trim().replace(',', '.').toDoubleOrNull() ?: return "Ingresa un número válido"
        return if (n < min || n > max) "Debe estar entre ${min.toInt()} y ${max.toInt()} $unidad" else null
    }

    fun temperatura(v: String) = numero(v, 10.0, 50.0, "°C")
    fun humedad(v: String) = numero(v, 20.0, 100.0, "%")

    fun coherencia(advertencia: Double, critico: Double, nombre: String): String? =
        if (critico <= advertencia) "El umbral crítico de $nombre debe ser mayor al de advertencia" else null
}

fun String.aDouble(): Double? = trim().replace(',', '.').toDoubleOrNull()
