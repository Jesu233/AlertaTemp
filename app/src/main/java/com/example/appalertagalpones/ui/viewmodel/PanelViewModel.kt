package com.example.appalertagalpones.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appalertagalpones.data.MonitoreoRepository
import com.example.appalertagalpones.data.PreferenciasRepository
import com.example.appalertagalpones.data.model.AccionRegistrada
import com.example.appalertagalpones.data.model.AtencionAlerta
import com.example.appalertagalpones.data.model.Estado
import com.example.appalertagalpones.data.model.puedeVer
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class ResumenFundo(
    val fundo: String,
    val total: Int,
    val criticos: Int,
    val advertencias: Int,
    val normales: Int
)

data class PanelUiState(
    val fundos: List<ResumenFundo> = emptyList(),
    val totalGalpones: Int = 0,
    val criticos: Int = 0,
    val advertencias: Int = 0,
    val normales: Int = 0,
    val alertasPendientes: Int = 0,
    val ultimasAcciones: List<AccionRegistrada> = emptyList()
)

class PanelViewModel(
    repositorio: MonitoreoRepository,
    preferencias: PreferenciasRepository
) : ViewModel() {

    val uiState: StateFlow<PanelUiState> = combine(
        repositorio.galpones, preferencias.usuario, repositorio.alertas, repositorio.todasLasAcciones
    ) { galpones, usuario, alertas, acciones ->
        val visibles = galpones.filter { usuario?.puedeVer(it) == true }
        val ids = visibles.map { it.id }.toSet()
        PanelUiState(
            fundos = visibles.groupBy { it.fundo }.map { (fundo, lista) ->
                ResumenFundo(
                    fundo = fundo,
                    total = lista.size,
                    criticos = lista.count { it.estado == Estado.CRITICO.name },
                    advertencias = lista.count { it.estado == Estado.ADVERTENCIA.name },
                    normales = lista.count { it.estado == Estado.NORMAL.name }
                )
            },
            totalGalpones = visibles.size,
            criticos = visibles.count { it.estado == Estado.CRITICO.name },
            advertencias = visibles.count { it.estado == Estado.ADVERTENCIA.name },
            normales = visibles.count { it.estado == Estado.NORMAL.name },
            alertasPendientes = alertas.count { it.galponId in ids && it.atencion == AtencionAlerta.PENDIENTE.name },
            ultimasAcciones = acciones.filter { it.galponId in ids }.take(5)
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PanelUiState())
}
