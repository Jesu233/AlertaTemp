package com.example.appalertagalpones

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.appalertagalpones.util.Notificador
import com.example.appalertagalpones.data.AppDatabase
import com.example.appalertagalpones.data.FotoStorage
import com.example.appalertagalpones.data.MonitoreoRepository
import com.example.appalertagalpones.data.PreferenciasRepository
import com.example.appalertagalpones.data.remote.FakeMonitoreoApi
import com.example.appalertagalpones.ui.navigation.AppNavigation
import com.example.appalertagalpones.ui.theme.AlertaTheme
import com.example.appalertagalpones.ui.viewmodel.AppViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Igual que la Guía 14: se crean base de datos, repositorios y la fábrica de ViewModels.
        val database = AppDatabase.getDatabase(this)
        val preferencias = PreferenciasRepository(applicationContext)
        val repositorio = MonitoreoRepository(database.monitoreoDao(), FakeMonitoreoApi(), preferencias)
        val factory = AppViewModelFactory(
            repositorio = repositorio,
            preferencias = preferencias,
            fotos = FotoStorage(applicationContext),
            notificador = Notificador(applicationContext)
        )

        setContent {
            AlertaTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    AppNavigation(factory)
                }
            }
        }
    }
}
