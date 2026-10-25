package com.example.appalertagalpones.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appalertagalpones.util.Notificador
import com.example.appalertagalpones.data.MonitoreoRepository
import com.example.appalertagalpones.data.PreferenciasRepository
import com.example.appalertagalpones.data.model.Estado
import com.example.appalertagalpones.data.model.Galpon
import com.example.appalertagalpones.data.model.Rol
import com.example.appalertagalpones.data.model.Usuario
import com.example.appalertagalpones.data.model.puedeVer
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

data class GalponesUiState(
    val usuario: Usuario? = null,
    val galpones: List<Galpon> = emptyList(),
    val filtro: Estado? = null,
    val actualizando: Boolean = false,
    val sinConexion: Boolean = false,
    val criticos: Int = 0,
    val advertencias: Int = 0,
    val normales: Int = 0,
    val ultimaActualizacionMs: Long? = null
)

class GalponesViewModel(
    private val repositorio: MonitoreoRepository,
    private val preferencias: PreferenciasRepository,
    private val notificador: Notificador
) : ViewModel() {

    companion object {
        /** Pregunta 10: mediciones simuladas cada 1 minuto (frecuencia a validar con el docente). */
        const val INTERVALO_MS = 60_000L
    }

    private val filtro = MutableStateFlow<Estado?>(null)
    private val actualizando = MutableStateFlow(false)
    private val sinConexion = MutableStateFlow(false)
    private val mutex = Mutex()

    val uiState: StateFlow<GalponesUiState> = combine(
        repositorio.galpones, preferencias.usuario, filtro, actualizando, sinConexion
    ) { galpones, usuario, f, act, sin ->
        val visibles = galpones.filter { usuario?.puedeVer(it) == true }
        GalponesUiState(
            usuario = usuario,
            galpones = if (f == null) visibles else visibles.filter { it.estado == f.name },
            filtro = f,
            actualizando = act,
            sinConexion = sin,
            criticos = visibles.count { it.estado == Estado.CRITICO.name },
            advertencias = visibles.count { it.estado == Estado.ADVERTENCIA.name },
            normales = visibles.count { it.estado == Estado.NORMAL.name },
            ultimaActualizacionMs = visibles.maxOfOrNull { it.actualizadoMs }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), GalponesUiState())

    init {
        // Actualización periódica solo mientras haya sesión iniciada.
        viewModelScope.launch {
            preferencias.usuario.map { it?.usuario }.distinctUntilChanged().collectLatest { id ->
                if (id != null) {
                    while (true) {
                        sincronizar()
                        delay(INTERVALO_MS)
                    }
                }
            }
        }
        // Notificaciones: solo alertas críticas, solo a operarios/supervisores responsables (pregunta 4).
        viewModelScope.launch {
            repositorio.alertasNuevas.collect { alerta ->
                val usuario = preferencias.usuario.first() ?: return@collect
                if (alerta.tipo == Estado.CRITICO.name && usuario.rol != Rol.JEFATURA) {
                    val galpon = repositorio.obtenerGalpon(alerta.galponId)
                    if (galpon != null && usuario.puedeVer(galpon)) {
                        notificador.alertaCritica(alerta.galponId, alerta.galponNombre, alerta.mensaje)
                    }
                }
            }
        }
    }

    fun refrescar() {
        viewModelScope.launch { sincronizar() }
    }

    fun filtrar(estado: Estado) {
        filtro.value = if (filtro.value == estado) null else estado
    }

    private suspend fun sincronizar() = mutex.withLock {
        actualizando.value = true
        repositorio.sincronizar()
            .onSuccess { sinConexion.value = false }
            .onFailure { sinConexion.value = true }
        actualizando.value = false
    }
}
