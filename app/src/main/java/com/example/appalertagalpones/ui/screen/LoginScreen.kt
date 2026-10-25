package com.example.appalertagalpones.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.appalertagalpones.ui.components.CampoTexto
import com.example.appalertagalpones.ui.viewmodel.SesionViewModel
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.appalertagalpones.R

@Composable
fun LoginScreen(viewModel: SesionViewModel) {
    val estado by viewModel.login.collectAsStateWithLifecycle()

    Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.widthIn(max = 480.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.logo_login),
                contentDescription = "Logo AlertaTemp",
                modifier = Modifier.fillMaxWidth(0.85f),
                contentScale = ContentScale.FillWidth
            )
            Text("Monitoreo de temperatura y humedad", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(16.dp))

            CampoTexto(estado.usuario, viewModel::onUsuario, "Usuario", Icons.Default.Person, estado.errores.usuario)
            CampoTexto(estado.clave, viewModel::onClave, "Contraseña", Icons.Default.Lock, estado.errores.clave, esClave = true)

            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = estado.recordar, onCheckedChange = viewModel::onRecordar)
                Text("Mantener sesión iniciada")
            }

            AnimatedVisibility(
                visible = estado.errorGeneral != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, null, tint = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.width(8.dp))
                    Text(estado.errorGeneral ?: "", color = MaterialTheme.colorScheme.error)
                }
            }

            Button(
                onClick = viewModel::ingresar,
                enabled = !estado.cargando,
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                if (estado.cargando) {
                    CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Ingresar")
                }
            }

        }
    }
}
