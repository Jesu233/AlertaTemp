package com.example.appalertagalpones.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appalertagalpones.data.MonitoreoRepository
import com.example.appalertagalpones.data.PreferenciasRepository
import com.example.appalertagalpones.data.model.Alerta
import com.example.appalertagalpones.data.model.AtencionAlerta
import com.example.appalertagalpones.data.model.Estado
import com.example.appalertagalpones.data.model.Galpon
import com.example.appalertagalpones.data.model.Usuario
import com.example.appalertagalpones.data.model.puedeVer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class FiltroFecha(val etiqueta: String, val ms: Long?) {
    TODAS("Todas", null),
    ULTIMAS_24H("24 h", 24L * 3_600_000),
    SIETE_DIAS("7 días", 7L * 24 * 3_600_000)
}

data class FiltrosAlerta(
    val tipo: Estado? = null,
    val fecha: FiltroFecha = FiltroFecha.TODAS,
    val galponId: Int? = null
)

data class AlertasUiState(
    val alertas: List<Alerta> = emptyList(),
    val galpones: List<Galpon> = emptyList(),
    val filtros: FiltrosAlerta = FiltrosAlerta(),
    val usuario: Usuario? = null,
    val pendientes: Int = 0
)

class AlertasViewModel(
    private val repositorio: MonitoreoRepository,
    preferencias: PreferenciasRepository
) : ViewModel() {

    private val filtros = MutableStateFlow(FiltrosAlerta())

    val uiState: StateFlow<AlertasUiState> = combine(
        repositorio.alertas, repositorio.galpones, preferencias.usuario, filtros
    ) { alertas, galpones, usuario, f ->
        val visibles = galpones.filter { usuario?.puedeVer(it) == true }
        val ids = visibles.map { it.id }.toSet()
        val propias = alertas.filter { it.galponId in ids }
        val ahora = System.currentTimeMillis()
        val tipo = f.tipo
        val galponId = f.galponId
        val ventana = f.fecha.ms
        AlertasUiState(
            alertas = propias
                .filter { tipo == null || it.tipo == tipo.name }
                .filter { galponId == null || it.galponId == galponId }
                .filter { ventana == null || it.fechaMs >= ahora - ventana },
            galpones = visibles,
            filtros = f,
            usuario = usuario,
            pendientes = propias.count { it.atencion == AtencionAlerta.PENDIENTE.name }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AlertasUiState())

    fun filtrarTipo(tipo: Estado?) = filtros.update { it.copy(tipo = tipo) }
    fun filtrarFecha(fecha: FiltroFecha) = filtros.update { it.copy(fecha = fecha) }
    fun filtrarGalpon(id: Int?) = filtros.update { it.copy(galponId = id) }

    fun tomarAlerta(id: Long) {
        viewModelScope.launch { repositorio.actualizarAtencion(id, AtencionAlerta.EN_ATENCION) }
    }

    fun marcarAtendida(id: Long) {
        viewModelScope.launch { repositorio.actualizarAtencion(id, AtencionAlerta.ATENDIDA) }
    }
}
