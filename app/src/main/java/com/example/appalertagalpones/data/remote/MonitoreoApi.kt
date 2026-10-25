package com.example.appalertagalpones.data.remote

import kotlinx.coroutines.delay
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random

data class GalponDto(
    val id: Int,
    val fundo: String,
    val sector: String,
    val nombre: String,
    val edadDias: Int,
    val operarioId: String,
    val supervisorId: String
)

data class LecturaDto(val galponId: Int, val temperatura: Double, val humedad: Double, val timestamp: Long)

interface MonitoreoApi {
    suspend fun galpones(): List<GalponDto>
    suspend fun lecturas(sinFallos: Boolean = false): List<LecturaDto>

    fun simular(galponId: Int, temperatura: Double, humedad: Double)
    fun restablecer(galponId: Int)
}


class FakeMonitoreoApi : MonitoreoApi {
    private val catalogo = listOf(
        GalponDto(1, "Fundo Los Aromos", "Sector A", "Galpón 1", 12, "operario1", "supervisor1"),
        GalponDto(2, "Fundo Los Aromos", "Sector A", "Galpón 2", 21, "operario1", "supervisor1"),
        GalponDto(3, "Fundo Los Aromos", "Sector B", "Galpón 3", 28, "operario2", "supervisor1"),
        GalponDto(4, "Fundo Los Aromos", "Sector B", "Galpón 4", 35, "operario2", "supervisor1"),
        GalponDto(5, "Fundo El Peumo", "Sector A", "Galpón 5", 18, "operario3", "supervisor2"),
        GalponDto(6, "Fundo El Peumo", "Sector A", "Galpón 6", 30, "operario3", "supervisor2")
    )
    private val base = mapOf(1 to 26.0, 2 to 27.0, 3 to 25.5, 4 to 24.5, 5 to 27.5, 6 to 25.0)
    private val manual = ConcurrentHashMap<Int, Pair<Double, Double>>()

    override suspend fun galpones(): List<GalponDto> {
        delay(250)
        return catalogo
    }

    override suspend fun lecturas(sinFallos: Boolean): List<LecturaDto> {
        delay(400)
        if (!sinFallos && Random.nextInt(100) < 10) throw IOException("Sin conexión (simulada)")
        val ahora = System.currentTimeMillis()
        return catalogo.map {
            val fijo = manual[it.id]
            LecturaDto(
                galponId = it.id,
                temperatura = fijo?.first ?: (base.getValue(it.id) + Random.nextDouble(-1.5, 3.0)).redondear(),
                humedad = fijo?.second ?: Random.nextDouble(50.0, 68.0).redondear(),
                timestamp = ahora
            )
        }
    }

    override fun simular(galponId: Int, temperatura: Double, humedad: Double) {
        manual[galponId] = temperatura to humedad
    }

    override fun restablecer(galponId: Int) {
        manual.remove(galponId)
    }

    private fun Double.redondear() = Math.round(this * 10) / 10.0
}
