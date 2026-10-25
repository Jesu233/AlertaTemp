package com.example.appalertagalpones.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appalertagalpones.data.MonitoreoRepository
import com.example.appalertagalpones.data.PreferenciasRepository
import com.example.appalertagalpones.data.model.AccionRegistrada
import com.example.appalertagalpones.data.model.Galpon
import com.example.appalertagalpones.data.model.Medicion
import com.example.appalertagalpones.data.model.Rol
import com.example.appalertagalpones.data.model.Usuario
import com.example.appalertagalpones.data.model.UsuariosDemo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

/** Pregunta 11: gráficos de temperatura y humedad durante períodos determinados. */
enum class PeriodoGrafico(val etiqueta: String, val ms: Long?) {
    MIN_30("30 min", 30L * 60_000),
    HORAS_2("2 h", 2L * 3_600_000),
    TODO("Todo", null)
}

data class DetalleUiState(
    val galpon: Galpon? = null,
    val mediciones: List<Medicion> = emptyList(),
    val acciones: List<AccionRegistrada> = emptyList(),
    val periodo: PeriodoGrafico = PeriodoGrafico.HORAS_2,
    val usuario: Usuario? = null,
    val operario: Usuario? = null,
    val supervisor: Usuario? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
class DetalleViewModel(
    private val repositorio: MonitoreoRepository,
    preferencias: PreferenciasRepository
) : ViewModel() {

    private val galponId = MutableStateFlow<Int?>(null)
    private val periodo = MutableStateFlow(PeriodoGrafico.HORAS_2)

    val uiState: StateFlow<DetalleUiState> = combine(galponId, periodo, preferencias.usuario) { id, p, u ->
        Triple(id, p, u)
    }.flatMapLatest { (id, p, u) -> flujoDetalle(id, p, u) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DetalleUiState())

    private fun flujoDetalle(id: Int?, p: PeriodoGrafico, u: Usuario?): Flow<DetalleUiState> {
        if (id == null) return flowOf(DetalleUiState(periodo = p, usuario = u))
        val desde = p.ms?.let { System.currentTimeMillis() - it } ?: 0L
        return combine(
            repositorio.galpon(id), repositorio.mediciones(id, desde), repositorio.accionesDe(id)
        ) { galpon, mediciones, acciones ->
            DetalleUiState(
                galpon = galpon,
                mediciones = mediciones,
                // Pregunta 12: el operario ve sus propias acciones; supervisor y jefatura ven todas.
                acciones = if (u?.rol == Rol.OPERARIO) acciones.filter { it.usuarioId == u.usuario } else acciones,
                periodo = p,
                usuario = u,
                operario = UsuariosDemo.buscar(galpon?.operarioId),
                supervisor = UsuariosDemo.buscar(galpon?.supervisorId)
            )
        }
    }

    fun seleccionarGalpon(id: Int) {
        galponId.value = id
    }

    fun cambiarPeriodo(p: PeriodoGrafico) {
        periodo.value = p
    }
}
