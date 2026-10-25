package com.example.appalertagalpones.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.alertagalpones.data.FotoStorage
import com.example.alertagalpones.data.MonitoreoRepository
import com.example.alertagalpones.data.PreferenciasRepository
import com.example.alertagalpones.util.Notificador
import com.example.appalertagalpones.data.FotoStorage
import com.example.appalertagalpones.data.MonitoreoRepository
import com.example.appalertagalpones.data.PreferenciasRepository

/** Fábrica de ViewModels con sus dependencias (patrón de la Guía 14). */
class AppViewModelFactory(
    private val repositorio: MonitoreoRepository,
    private val preferencias: PreferenciasRepository,
    private val fotos: FotoStorage,
    private val notificador: Notificador
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val vm: ViewModel = when {
            modelClass.isAssignableFrom(SesionViewModel::class.java) -> SesionViewModel(preferencias)
            modelClass.isAssignableFrom(GalponesViewModel::class.java) -> GalponesViewModel(repositorio, preferencias, notificador)
            modelClass.isAssignableFrom(AlertasViewModel::class.java) -> AlertasViewModel(repositorio, preferencias)
            modelClass.isAssignableFrom(DetalleViewModel::class.java) -> DetalleViewModel(repositorio, preferencias)
            modelClass.isAssignableFrom(AccionViewModel::class.java) -> AccionViewModel(repositorio, preferencias, fotos)
            modelClass.isAssignableFrom(SimuladorViewModel::class.java) -> SimuladorViewModel(repositorio, preferencias)
            modelClass.isAssignableFrom(AjustesViewModel::class.java) -> AjustesViewModel(preferencias)
            modelClass.isAssignableFrom(PanelViewModel::class.java) -> PanelViewModel(repositorio, preferencias)
            else -> throw IllegalArgumentException("ViewModel desconocido")
        }
        return vm as T
    }
}
