package com.example.appalertagalpones.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.example.appalertagalpones.data.model.AccionRegistrada
import com.example.appalertagalpones.data.model.Alerta
import com.example.appalertagalpones.data.model.Galpon
import com.example.appalertagalpones.data.model.Medicion
import kotlinx.coroutines.flow.Flow

@Dao
interface MonitoreoDao {
    // Galpones
    @Query("SELECT * FROM galpones ORDER BY id")
    fun observarGalpones(): Flow<List<Galpon>>

    @Query("SELECT * FROM galpones WHERE id = :id")
    fun observarGalpon(id: Int): Flow<Galpon?>

    @Query("SELECT * FROM galpones WHERE id = :id")
    suspend fun obtenerGalpon(id: Int): Galpon?

    @Upsert
    suspend fun guardarGalpon(galpon: Galpon)

    // Mediciones
    @Insert
    suspend fun insertarMedicion(medicion: Medicion)

    @Query("SELECT * FROM mediciones WHERE galponId = :id AND fechaMs >= :desde ORDER BY fechaMs ASC")
    fun observarMediciones(id: Int, desde: Long): Flow<List<Medicion>>

    // Alertas
    @Insert
    suspend fun insertarAlerta(alerta: Alerta)

    @Query("SELECT * FROM alertas ORDER BY fechaMs DESC")
    fun observarAlertas(): Flow<List<Alerta>>

    @Query("UPDATE alertas SET atencion = :atencion WHERE id = :id")
    suspend fun actualizarAtencion(id: Long, atencion: String)

    @Query("UPDATE alertas SET atencion = 'ATENDIDA' WHERE galponId = :galponId")
    suspend fun atenderAlertasDeGalpon(galponId: Int)

    // Acciones
    @Insert
    suspend fun insertarAccion(accion: AccionRegistrada)

    @Query("SELECT * FROM acciones ORDER BY fechaMs DESC")
    fun observarAcciones(): Flow<List<AccionRegistrada>>

    @Query("SELECT * FROM acciones WHERE galponId = :id ORDER BY fechaMs DESC")
    fun observarAccionesDeGalpon(id: Int): Flow<List<AccionRegistrada>>
}
