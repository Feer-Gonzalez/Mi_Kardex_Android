package com.example.mi_kardex_android

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "materias")
data class MateriaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val semestre: Int,
    val calNormal: Double? = null,
    val calRepeticion: Double? = null,
    val calEspecial: Double? = null
) {
    fun calificacionAprobatoria(): Double? {
        return when {
            calEspecial != null && calEspecial >= 70 -> calEspecial
            calRepeticion != null && calRepeticion >= 70 -> calRepeticion
            calNormal != null && calNormal >= 70 -> calNormal
            else -> null
        }
    }
}