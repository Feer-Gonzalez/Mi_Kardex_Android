package com.example.mi_kardex_android

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [MateriaEntity::class],
    version = 1,
    exportSchema = false
)
abstract class KardexDatabase : RoomDatabase() {

    abstract fun materiaDao(): MateriaDao

    companion object {
        @Volatile
        private var INSTANCE: KardexDatabase? = null

        fun obtenerBaseDatos(context: Context): KardexDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    KardexDatabase::class.java,
                    "cardex_database"
                ).build().also {
                    INSTANCE = it
                }
            }
        }
    }
}