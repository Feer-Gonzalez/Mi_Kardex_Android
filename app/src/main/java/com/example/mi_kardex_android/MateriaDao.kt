package com.example.mi_kardex_android

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MateriaDao {

    @Insert
    suspend fun insertar(materia: MateriaEntity)

    @Update
    suspend fun actualizar(materia: MateriaEntity)

    @Delete
    suspend fun eliminar(materia: MateriaEntity)

    @Query("SELECT * FROM materias ORDER BY semestre ASC, nombre ASC")
    fun obtenerTodas(): Flow<List<MateriaEntity>>

    @Query("SELECT * FROM materias WHERE id = :id LIMIT 1")
    suspend fun obtenerPorId(id: Int): MateriaEntity?

    @Query("DELETE FROM materias")
    suspend fun eliminarTodas()
}