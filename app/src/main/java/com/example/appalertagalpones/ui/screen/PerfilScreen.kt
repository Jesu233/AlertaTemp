package com.example.alertagalpones.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.appalertagalpones.data.model.Rol
import com.example.appalertagalpones.data.model.Usuario

@Composable
fun PerfilScreen(usuario: Usuario?, onCerrarSesion: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(Icons.Default.Person, null, Modifier.size(96.dp), tint = MaterialTheme.colorScheme.primary)
        Text(usuario?.nombre ?: "", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Rol: ${usuario?.rol?.etiqueta ?: ""}")
                Text("Usuario: ${usuario?.usuario ?: ""}")
                Text("Teléfono: ${usuario?.telefono ?: ""}")
                Text("Correo: ${usuario?.correo ?: ""}")
                Spacer(Modifier.height(4.dp))
                Text(
                    when (usuario?.rol) {
                        Rol.OPERARIO -> "Ves los galpones asignados a ti y recibes notificaciones críticas."
                        Rol.SUPERVISOR -> "Ves los galpones de tu fundo y recibes notificaciones críticas."
                        Rol.JEFATURA -> "Consultas todos los galpones, alertas, mediciones y acciones (solo lectura)."
                        null -> ""
                    },
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        OutlinedButton(onClick = onCerrarSesion, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.AutoMirrored.Filled.ExitToApp, null)
            Spacer(Modifier.width(8.dp))
            Text("Cerrar sesión")
        }
    }
}
