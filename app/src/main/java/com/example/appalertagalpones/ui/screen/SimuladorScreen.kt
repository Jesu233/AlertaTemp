package com.example.appalertagalpones.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.appalertagalpones.ui.components.CampoTexto
import com.example.appalertagalpones.ui.components.SelectorDesplegable
import com.example.appalertagalpones.ui.viewmodel.SimuladorViewModel

@Composable
fun SimuladorScreen(viewModel: SimuladorViewModel) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val f = estado.form

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            "Fija valores ficticios de temperatura y humedad para probar alertas y notificaciones. " +
                "Umbrales actuales: advertencia ${estado.umbrales.tempAdvertencia} °C / ${estado.umbrales.humAdvertencia} %, " +
                "crítico ${estado.umbrales.tempCritico} °C / ${estado.umbrales.humCritico} %.",
            style = MaterialTheme.typography.bodyMedium
        )

        SelectorDesplegable(
            etiqueta = "Galpón",
            seleccion = estado.galpones.firstOrNull { it.id == f.galponId }?.nombre ?: "Selecciona",
            opciones = estado.galpones.map { it.nombre to it },
            onSeleccion = viewModel::seleccionarGalpon,
            error = f.errores.galpon
        )
        CampoTexto(f.temperatura, viewModel::onTemperatura, "Temperatura (°C)", Icons.Default.Thermostat, f.errores.temperatura, teclado = KeyboardType.Decimal)
        CampoTexto(f.humedad, viewModel::onHumedad, "Humedad relativa (%)", Icons.Default.WaterDrop, f.errores.humedad, teclado = KeyboardType.Decimal)

        Button(onClick = viewModel::aplicar, enabled = !f.aplicando, modifier = Modifier.fillMaxWidth()) {
            if (f.aplicando) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
            else Text("Aplicar lectura simulada")
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = viewModel::forzarCritica, modifier = Modifier.weight(1f)) { Text("Forzar alerta crítica") }
            OutlinedButton(onClick = viewModel::restablecer, modifier = Modifier.weight(1f)) { Text("Restablecer") }
        }

        AnimatedVisibility(
            visible = f.mensaje != null,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Card(Modifier.fillMaxWidth()) {
                Text(f.mensaje ?: "", Modifier.padding(16.dp), fontWeight = FontWeight.Medium)
            }
        }
    }
}
