package com.example.mi_kardex_android
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

private val MoradoMateria = Color(0xFF6D28D9)
private val FondoMateria = Color(0xFFF7F3FF)

class MateriaActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dao = KardexDatabase
            .obtenerBaseDatos(this)
            .materiaDao()

        setContent {
            MaterialTheme {
                PantallaMateria(
                    onGuardar = { materia ->
                        lifecycleScope.launch {
                            try {
                                dao.insertar(materia)

                                Toast.makeText(
                                    this@MateriaActivity,
                                    "Materia guardada correctamente",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } catch (e: Exception) {
                                Toast.makeText(
                                    this@MateriaActivity,
                                    "No se pudo guardar la materia",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaMateria(
    onGuardar: (MateriaEntity) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var semestre by remember { mutableStateOf("") }
    var normal by remember { mutableStateOf("") }
    var repeticion by remember { mutableStateOf("") }
    var especial by remember { mutableStateOf("") }

    val contexto = LocalContext.current

    Scaffold(
        containerColor = FondoMateria,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Nueva materia",
                        color = MoradoMateria
                    )
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.Top
        ) {

            Text(
                text = "Registra los datos de tu materia",
                style = MaterialTheme.typography.titleMedium,
                color = MoradoMateria
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre de la materia") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = semestre,
                onValueChange = { semestre = it.filter { c -> c.isDigit() } },
                label = { Text("Semestre (1 al 9)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                )
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = normal,
                onValueChange = { normal = it },
                label = { Text("Calificación normal (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                )
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = repeticion,
                onValueChange = { repeticion = it },
                label = { Text("Calificación de repetición (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                )
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = especial,
                onValueChange = { especial = it },
                label = { Text("Calificación especial (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                )
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Ingresa calificaciones de 0 a 100. " +
                        "Debes registrar al menos una calificación.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.DarkGray
            )

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    val numeroSemestre = semestre.toIntOrNull()
                    val calNormal = normal.toDoubleOrNull()
                    val calRepeticion = repeticion.toDoubleOrNull()
                    val calEspecial = especial.toDoubleOrNull()

                    val normalValida =
                        normal.isBlank() ||
                                (calNormal != null && calNormal in 0.0..100.0)

                    val repeticionValida =
                        repeticion.isBlank() ||
                                (calRepeticion != null &&
                                        calRepeticion in 0.0..100.0)

                    val especialValida =
                        especial.isBlank() ||
                                (calEspecial != null &&
                                        calEspecial in 0.0..100.0)

                    val hayCalificacion =
                        normal.isNotBlank() ||
                                repeticion.isNotBlank() ||
                                especial.isNotBlank()

                    if (
                        nombre.isBlank() ||
                        numeroSemestre == null ||
                        numeroSemestre !in 1..9 ||
                        !normalValida ||
                        !repeticionValida ||
                        !especialValida ||
                        !hayCalificacion
                    ) {
                        Toast.makeText(
                            contexto,
                            "Revisa los datos e ingresa al menos una calificación válida.",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        onGuardar(
                            MateriaEntity(
                                nombre = nombre.trim(),
                                semestre = numeroSemestre,
                                calNormal = calNormal,
                                calRepeticion = calRepeticion,
                                calEspecial = calEspecial
                            )
                        )

                        nombre = ""
                        semestre = ""
                        normal = ""
                        repeticion = ""
                        especial = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar materia")
            }
        }
    }
}
