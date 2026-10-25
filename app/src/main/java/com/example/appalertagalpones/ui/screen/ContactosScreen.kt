package com.example.alertagalpones.ui.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.appalertagalpones.data.model.UsuariosDemo

/** Pregunta 8: contacto ficticio de los supervisores responsables (marcador del teléfono, sin permisos). */
@Composable
fun ContactosScreen() {
    val contexto = LocalContext.current
    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Text("Supervisores responsables (datos ficticios)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
        items(UsuariosDemo.supervisores(), key = { it.usuario }) { s ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(s.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(s.rol.etiqueta, style = MaterialTheme.typography.bodySmall)
                    Text("Teléfono: ${s.telefono}")
                    Text("Correo: ${s.correo}")
                    OutlinedButton(onClick = {
                        contexto.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + s.telefono.filter { it.isDigit() || it == '+' })))
                    }) {
                        Icon(Icons.Default.Call, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Llamar")
                    }
                }
            }
        }
    }
}
