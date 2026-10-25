package com.example.alertagalpones.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.appalertagalpones.ui.components.CampoTexto
import com.example.appalertagalpones.ui.viewmodel.AjustesViewModel

@Composable
fun AjustesScreen(viewModel: AjustesViewModel) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val e = estado.errores

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            "Los rangos reales dependen de la edad de las aves y de las condiciones de crianza. " +
                "Estos umbrales son ficticios y deben validarse con el docente.",
            style = MaterialTheme.typography.bodyMedium
        )
        Text("Temperatura (°C)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        CampoTexto(estado.tempAdvertencia, viewModel::onTempAdvertencia, "Advertencia desde", Icons.Default.Thermostat, e.tempAdvertencia, teclado = KeyboardType.Decimal)
        CampoTexto(estado.tempCritico, viewModel::onTempCritico, "Crítico desde", Icons.Default.Thermostat, e.tempCritico, teclado = KeyboardType.Decimal)
        Text("Humedad relativa (%)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        CampoTexto(estado.humAdvertencia, viewModel::onHumAdvertencia, "Advertencia desde", Icons.Default.WaterDrop, e.humAdvertencia, teclado = KeyboardType.Decimal)
        CampoTexto(estado.humCritico, viewModel::onHumCritico, "Crítico desde", Icons.Default.WaterDrop, e.humCritico, teclado = KeyboardType.Decimal)

        Button(onClick = viewModel::guardar, modifier = Modifier.fillMaxWidth()) { Text("Guardar umbrales") }
        AnimatedVisibility(estado.guardado, enter = fadeIn(), exit = fadeOut()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CheckCircle, null, tint = MaterialTheme.colorScheme.primary)
                Text(" Umbrales guardados. Se aplican desde la próxima medición.")
            }
        }
    }
}
