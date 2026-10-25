package com.example.appalertagalpones.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.appalertagalpones.data.model.AccionRegistrada
import com.example.appalertagalpones.data.model.Alerta

import com.example.appalertagalpones.data.model.Galpon
import com.example.appalertagalpones.data.model.Medicion

@Database(
    entities = [Galpon::class, Medicion::class, Alerta::class, AccionRegistrada::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun monitoreoDao(): MonitoreoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "alerta_galpones_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
