package com.example.mi_kardex_android

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class PerfilActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferencias = getSharedPreferences(
            "datos_alumno",
            Context.MODE_PRIVATE
        )

        setContent {
            var nombre by remember {
                mutableStateOf(
                    preferencias.getString("nombre", "") ?: ""
                )
            }

            var numeroControl by remember {
                mutableStateOf(
                    preferencias.getString("numero_control", "") ?: ""
                )
            }

            var mensaje by remember { mutableStateOf("") }

            MaterialTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Mi perfil",
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre del alumno") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = numeroControl,
                        onValueChange = { numeroControl = it },
                        label = { Text("Número de control") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            if (nombre.isNotBlank() &&
                                numeroControl.isNotBlank()
                            ) {
                                preferencias.edit()
                                    .putString("nombre", nombre.trim())
                                    .putString(
                                        "numero_control",
                                        numeroControl.trim()
                                    )
                                    .apply()

                                mensaje = "Datos guardados correctamente"
                            } else {
                                mensaje = "Completa ambos campos"
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Guardar datos")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = mensaje)
                }
            }
        }
    }
}
