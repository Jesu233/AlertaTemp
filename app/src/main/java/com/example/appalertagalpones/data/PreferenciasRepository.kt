package com.example.appalertagalpones.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.appalertagalpones.data.model.Umbrales
import com.example.appalertagalpones.data.model.Usuario
import com.example.appalertagalpones.data.model.UsuariosDemo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "preferencias_usuario")

class PreferenciasRepository(private val context: Context) {

    companion object {
        private val USUARIO = stringPreferencesKey("usuario_id")
        private val RECORDAR = booleanPreferencesKey("recordar_sesion")
        private val T_ADV = doublePreferencesKey("temp_advertencia")
        private val T_CRIT = doublePreferencesKey("temp_critico")
        private val H_ADV = doublePreferencesKey("hum_advertencia")
        private val H_CRIT = doublePreferencesKey("hum_critico")
    }

    val usuario: Flow<Usuario?> = context.dataStore.data.map { UsuariosDemo.buscar(it[USUARIO]) }

    val recordarSesion: Flow<Boolean> = context.dataStore.data.map { it[RECORDAR] ?: false }

    val umbrales: Flow<Umbrales> = context.dataStore.data.map { p ->
        val d = Umbrales()
        Umbrales(
            tempAdvertencia = p[T_ADV] ?: d.tempAdvertencia,
            tempCritico = p[T_CRIT] ?: d.tempCritico,
            humAdvertencia = p[H_ADV] ?: d.humAdvertencia,
            humCritico = p[H_CRIT] ?: d.humCritico
        )
    }

    suspend fun iniciarSesion(usuarioId: String, recordar: Boolean) {
        context.dataStore.edit {
            it[USUARIO] = usuarioId
            it[RECORDAR] = recordar
        }
    }

    suspend fun cerrarSesion() {
        context.dataStore.edit {
            it.remove(USUARIO)
            it.remove(RECORDAR)
        }
    }

    suspend fun guardarUmbrales(u: Umbrales) {
        context.dataStore.edit {
            it[T_ADV] = u.tempAdvertencia
            it[T_CRIT] = u.tempCritico
            it[H_ADV] = u.humAdvertencia
            it[H_CRIT] = u.humCritico
        }
    }
}
