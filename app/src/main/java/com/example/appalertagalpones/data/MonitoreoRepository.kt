package com.example.appalertagalpones.data

import com.example.appalertagalpones.data.model.AccionRegistrada
import com.example.appalertagalpones.data.model.Alerta
import com.example.appalertagalpones.data.model.AtencionAlerta
import com.example.appalertagalpones.data.model.Estado
import com.example.appalertagalpones.data.model.EvaluadorEstado
import com.example.appalertagalpones.data.model.Galpon
import com.example.appalertagalpones.data.model.Medicion
import com.example.appalertagalpones.data.remote.MonitoreoApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.IOException

/** Fuente única de verdad: la UI lee de Room; la API (simulada) solo sincroniza la base local. */
class MonitoreoRepository(
    private val dao: MonitoreoDao,
    private val api: MonitoreoApi,
    private val preferencias: PreferenciasRepository
) {
    val galpones: Flow<List<Galpon>> = dao.observarGalpones()
    val alertas: Flow<List<Alerta>> = dao.observarAlertas()
    val todasLasAcciones: Flow<List<AccionRegistrada>> = dao.observarAcciones()

    private val _alertasNuevas = MutableSharedFlow<Alerta>(extraBufferCapacity = 32)
    val alertasNuevas: SharedFlow<Alerta> = _alertasNuevas.asSharedFlow()

    fun galpon(id: Int): Flow<Galpon?> = dao.observarGalpon(id)
    fun mediciones(id: Int, desdeMs: Long): Flow<List<Medicion>> = dao.observarMediciones(id, desdeMs)
    fun accionesDe(id: Int): Flow<List<AccionRegistrada>> = dao.observarAccionesDeGalpon(id)
    suspend fun obtenerGalpon(id: Int): Galpon? = dao.obtenerGalpon(id)

    suspend fun actualizarAtencion(id: Long, atencion: AtencionAlerta) =
        dao.actualizarAtencion(id, atencion.name)

    /** Registrar una acción correctiva marca como atendidas las alertas del galpón (pregunta 2). */
    suspend fun registrarAccion(accion: AccionRegistrada) {
        dao.insertarAccion(accion)
        dao.atenderAlertasDeGalpon(accion.galponId)
    }

    /** Pregunta 5: valores ficticios para provocar alertas. */
    suspend fun simular(galponId: Int, temperatura: Double, humedad: Double): Result<List<Alerta>> {
        api.simular(galponId, temperatura, humedad)
        return sincronizar(sinFallos = true)
    }

    suspend fun restablecerSimulacion(galponId: Int): Result<List<Alerta>> {
        api.restablecer(galponId)
        return sincronizar(sinFallos = true)
    }

    /** Pregunta 10: cada sincronización registra una medición (temperatura, humedad, galpón, fecha y hora). */
    suspend fun sincronizar(sinFallos: Boolean = false): Result<List<Alerta>> = withContext(Dispatchers.IO) {
        try {
            val umbrales = preferencias.umbrales.first()
            val info = api.galpones().associateBy { it.id }
            val nuevas = mutableListOf<Alerta>()
            for (l in api.lecturas(sinFallos)) {
                val g = info[l.galponId] ?: continue
                val evaluacion = EvaluadorEstado.evaluar(l.temperatura, l.humedad, umbrales)
                val estado = evaluacion.estado
                val anterior = dao.obtenerGalpon(g.id)?.estado
                dao.guardarGalpon(
                    Galpon(
                        id = g.id, fundo = g.fundo, sector = g.sector, nombre = g.nombre, edadDias = g.edadDias,
                        operarioId = g.operarioId, supervisorId = g.supervisorId,
                        temperatura = l.temperatura, humedad = l.humedad,
                        actualizadoMs = l.timestamp, estado = estado.name
                    )
                )
                dao.insertarMedicion(
                    Medicion(galponId = g.id, temperatura = l.temperatura, humedad = l.humedad, fechaMs = l.timestamp, estado = estado.name)
                )
                if (estado != Estado.NORMAL && anterior != estado.name) {
                    val alerta = Alerta(
                        galponId = g.id,
                        galponNombre = "${g.nombre} · ${g.fundo} (${g.sector})",
                        tipo = estado.name,
                        temperatura = l.temperatura,
                        humedad = l.humedad,
                        mensaje = evaluacion.motivo,
                        fechaMs = l.timestamp
                    )
                    dao.insertarAlerta(alerta)
                    nuevas += alerta
                    _alertasNuevas.tryEmit(alerta)
                }
            }
            Result.success(nuevas)
        } catch (e: IOException) {
            Result.failure(e)
        }
    }
}
