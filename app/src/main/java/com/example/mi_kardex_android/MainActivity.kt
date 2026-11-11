

package com.example.mi_kardex_android

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import java.util.Locale

// COLORES: FONDO LILA CLARO Y BOTONES PURPURA

private val Morado = Color(0xFF7B4BC4)
private val MoradoOscuro = Color(0xFF6236A8)
private val MoradoClaro = Color(0xFFE8DDF8)
private val Fondo = Color(0xFFF1E8FC)
private val TextoOscuro = Color(0xFF292438)
private val TextoSecundario = Color(0xFF777184)
private val Blanco = Color(0xFFFFFFFF)

private val KardexColores = lightColorScheme(
    primary = Morado,
    onPrimary = Blanco,
    primaryContainer = MoradoClaro,
    onPrimaryContainer = MoradoOscuro,
    background = Fondo,
    onBackground = TextoOscuro,
    surface = Blanco,
    onSurface = TextoOscuro,
    error = Color(0xFFB3261E)
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dao = KardexDatabase
            .obtenerBaseDatos(applicationContext)
            .materiaDao()

        setContent {
            MaterialTheme(colorScheme = KardexColores) {
                AplicacionKardex(dao = dao)
            }
        }
    }
}

@Composable
fun AplicacionKardex(dao: MateriaDao) {
    val contexto = LocalContext.current
    val scope = rememberCoroutineScope()

    var pantalla by remember { mutableStateOf("inicio") }
    var materiaEditar by remember { mutableStateOf<MateriaEntity?>(null) }

    var nombreAlumno by remember {
        mutableStateOf(
            contexto.getSharedPreferences("perfil_kardex", 0)
                .getString("nombre", "") ?: ""
        )
    }

    var controlAlumno by remember {
        mutableStateOf(
            contexto.getSharedPreferences("perfil_kardex", 0)
                .getString("control", "") ?: ""
        )
    }

    when (pantalla) {
        "inicio" -> {
            val materias by dao.obtenerTodas()
                .collectAsState(initial = emptyList())

            PantallaBase("Mi Kardex", { pantalla = "inicio" }) {
                PantallaInicio(
                    materias = materias,
                    nombreAlumno = nombreAlumno,
                    controlAlumno = controlAlumno,
                    onPerfil = { pantalla = "perfil" },
                    onRegistrar = {
                        materiaEditar = null
                        pantalla = "formulario"
                    },
                    onMaterias = { pantalla = "lista" },
                    onPromedios = { pantalla = "promedios" }
                )
            }
        }

        "perfil" -> PantallaBase("Mi perfil", {
            pantalla = "inicio"
        }) {
            var nombre by remember {
                mutableStateOf(nombreAlumno)
            }
            var control by remember {
                mutableStateOf(controlAlumno)
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Tus datos personales",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextoOscuro
                )

                Text(
                    text = "Guarda tu nombre y número de control.",
                    color = TextoSecundario
                )

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre del alumno") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )

                OutlinedTextField(
                    value = control,
                    onValueChange = { control = it },
                    label = { Text("Número de control") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )

                Button(
                    onClick = {
                        if (nombre.isBlank() || control.isBlank()) {
                            Toast.makeText(
                                contexto,
                                "Completa todos los datos",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            contexto.getSharedPreferences(
                                "perfil_kardex", 0
                            ).edit()
                                .putString("nombre", nombre.trim())
                                .putString("control", control.trim())
                                .apply()

                            nombreAlumno = nombre.trim()
                            controlAlumno = control.trim()

                            Toast.makeText(
                                contexto,
                                "Perfil guardado",
                                Toast.LENGTH_SHORT
                            ).show()

                            pantalla = "inicio"
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Morado,
                        contentColor = Blanco
                    )
                ) {
                    Text("Guardar perfil")
                }

                OutlinedButton(
                    onClick = { pantalla = "inicio" },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MoradoOscuro
                    )
                ) {
                    Text("Regresar")
                }
            }
        }

        "formulario" -> PantallaBase(
            if (materiaEditar == null) "Nueva materia"
            else "Editar materia",
            { pantalla = "inicio" }
        ) {
            FormularioMateria(
                materia = materiaEditar,
                onGuardar = { materia ->
                    scope.launch {
                        if (materiaEditar == null) {
                            dao.insertar(materia)
                        } else {
                            dao.actualizar(materia)
                        }

                        materiaEditar = null

                        Toast.makeText(
                            contexto,
                            "Materia guardada correctamente",
                            Toast.LENGTH_SHORT
                        ).show()

                        pantalla = "lista"
                    }
                },
                onCancelar = {
                    materiaEditar = null
                    pantalla = "inicio"
                }
            )
        }

        "lista" -> {
            val materias by dao.obtenerTodas()
                .collectAsState(initial = emptyList())

            var semestreSeleccionado by remember {
                mutableStateOf<Int?>(null)
            }
            var menuSemestresExpandido by remember {
                mutableStateOf(false)
            }

            val materiasFiltradas = if (semestreSeleccionado == null) {
                materias
            } else {
                materias.filter { it.semestre == semestreSeleccionado }
            }

            PantallaBase("Mis materias", {
                pantalla = "inicio"
            }) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Fondo)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Button(
                        onClick = {
                            materiaEditar = null
                            pantalla = "formulario"
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Morado,
                            contentColor = Blanco
                        )
                    ) {
                        Text("+ Agregar materia")
                    }

                    Spacer(Modifier.height(14.dp))

                    Text(
                        "Filtrar materias por semestre",
                        color = TextoOscuro,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(Modifier.height(6.dp))

                    Box {
                        OutlinedButton(
                            onClick = { menuSemestresExpandido = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MoradoOscuro
                            )
                        ) {
                            Text(
                                if (semestreSeleccionado == null) {
                                    "Todos los semestres"
                                } else {
                                    "Semestre $semestreSeleccionado"
                                }
                            )
                        }

                        DropdownMenu(
                            expanded = menuSemestresExpandido,
                            onDismissRequest = {
                                menuSemestresExpandido = false
                            },
                            modifier = Modifier.background(Blanco)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Todos los semestres") },
                                onClick = {
                                    semestreSeleccionado = null
                                    menuSemestresExpandido = false
                                }
                            )

                            (1..9).forEach { numeroSemestre ->
                                DropdownMenuItem(
                                    text = { Text("Semestre $numeroSemestre") },
                                    onClick = {
                                        semestreSeleccionado = numeroSemestre
                                        menuSemestresExpandido = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    if (materiasFiltradas.isEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MoradoClaro
                            ),
                            shape = RoundedCornerShape(18.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    if (materias.isEmpty()) {
                                        "Aún no tienes materias"
                                    } else {
                                        "No hay materias en este semestre"
                                    },
                                    fontWeight = FontWeight.Bold,
                                    color = TextoOscuro
                                )
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    if (materias.isEmpty()) {
                                        "Agrega tu primera materia para comenzar."
                                    } else {
                                        "Selecciona otro semestre o muestra todos."
                                    },
                                    color = TextoSecundario
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = materiasFiltradas,
                                key = { it.id }
                            ) { materia ->
                                TarjetaMateria(
                                    materia = materia,
                                    onEditar = {
                                        materiaEditar = materia
                                        pantalla = "formulario"
                                    },
                                    onEliminar = {
                                        scope.launch {
                                            dao.eliminar(materia)
                                            Toast.makeText(
                                                contexto,
                                                "Materia eliminada",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        "promedios" -> {
            val materias by dao.obtenerTodas()
                .collectAsState(initial = emptyList())

            PantallaBase("Mis promedios", {
                pantalla = "inicio"
            }) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Fondo)
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    val aprobadas = materias.mapNotNull {
                        it.calificacionAprobatoria()
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Morado
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Text(
                                "Promedio general",
                                color = Blanco.copy(alpha = 0.9f)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                if (aprobadas.isEmpty()) {
                                    "--"
                                } else {
                                    formatoPromedio(aprobadas.average())
                                },
                                color = Blanco,
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                if (aprobadas.isEmpty()) {
                                    "Aún no hay calificaciones aprobatorias"
                                } else {
                                    "${aprobadas.size} materia(s) aprobada(s)"
                                },
                                color = Blanco.copy(alpha = 0.9f)
                            )
                        }
                    }

                    Text(
                        "Promedio por semestre",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextoOscuro
                    )

                    (1..9).forEach { semestre ->
                        val calificaciones = materias
                            .filter { it.semestre == semestre }
                            .mapNotNull {
                                it.calificacionAprobatoria()
                            }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MoradoClaro
                            ),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, MoradoClaro)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        "Semestre $semestre",
                                        fontWeight = FontWeight.Bold,
                                        color = TextoOscuro
                                    )
                                    Text(
                                        if (calificaciones.isEmpty()) {
                                            "Sin materias aprobadas"
                                        } else {
                                            "${calificaciones.size} materia(s) aprobada(s)"
                                        },
                                        color = TextoSecundario,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }

                                Text(
                                    if (calificaciones.isEmpty()) {
                                        "--"
                                    } else {
                                        formatoPromedio(
                                            calificaciones.average()
                                        )
                                    },
                                    color = MoradoOscuro,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleLarge
                                )
                            }
                        }
                    }

                    Text(
                        "El promedio considera una calificación aprobatoria " +
                                "por materia: primero la especial, después la " +
                                "repetición y finalmente la normal. Las materias " +
                                "sin una calificación aprobatoria no se incluyen.",
                        color = TextoSecundario,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun PantallaInicio(
    materias: List<MateriaEntity>,
    nombreAlumno: String,
    controlAlumno: String,
    onPerfil: () -> Unit,
    onRegistrar: () -> Unit,
    onMaterias: () -> Unit,
    onPromedios: () -> Unit
) {
    val aprobadas = materias.mapNotNull {
        it.calificacionAprobatoria()
    }

    val promedioGeneral = if (aprobadas.isEmpty()) {
        "--"
    } else {
        formatoPromedio(aprobadas.average())
    }

    val hayCalificaciones = materias.any {
        it.calNormal != null ||
                it.calRepeticion != null ||
                it.calEspecial != null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Fondo)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                "Tu historial académico",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextoOscuro
            )

            Text(
                "Consulta y organiza tus calificaciones.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextoSecundario
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onPerfil),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = Blanco
            ),
            border = BorderStroke(1.dp, MoradoClaro)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    "Datos personales",
                    fontWeight = FontWeight.Bold,
                    color = MoradoOscuro,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    "Nombre: ${nombreAlumno.ifBlank { "No registrado" }}",
                    color = TextoOscuro
                )
                Text(
                    "Número de control: ${controlAlumno.ifBlank { "No registrado" }}",
                    color = TextoOscuro
                )
                Text(
                    "Toca aquí para editar tus datos",
                    color = TextoSecundario,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Morado
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "PROMEDIO GENERAL",
                    color = Blanco.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    promedioGeneral,
                    color = Blanco,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    when {
                        aprobadas.isNotEmpty() ->
                            "${aprobadas.size} materia(s) con calificación aprobatoria"
                        hayCalificaciones ->
                            "Aún no hay calificaciones aprobatorias"
                        else ->
                            "Aún no hay calificaciones registradas"
                    },
                    color = Blanco.copy(alpha = 0.95f),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Text(
            "Mi espacio académico",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = TextoOscuro
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TarjetaMenu(
                titulo = "Mis materias",
                descripcion = "Registrar y consultar materias",
                simbolo = "M",
                modifier = Modifier.weight(1f),
                onClick = onMaterias
            )

            TarjetaMenu(
                titulo = "Promedios",
                descripcion = "General y por semestre",
                simbolo = "P",
                modifier = Modifier.weight(1f),
                onClick = onPromedios
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TarjetaMenu(
                titulo = "Mi perfil",
                descripcion = "Nombre y número de control",
                simbolo = "U",
                modifier = Modifier.weight(1f),
                onClick = onPerfil
            )

            TarjetaMenu(
                titulo = "Nueva materia",
                descripcion = "Agregar calificaciones",
                simbolo = "+",
                modifier = Modifier.weight(1f),
                onClick = onRegistrar
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MoradoClaro
            ),
            shape = RoundedCornerShape(18.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier.size(42.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "✓",
                        color = MoradoOscuro,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        "Sigue tu avance",
                        fontWeight = FontWeight.Bold,
                        color = MoradoOscuro
                    )
                    Text(
                        "Lleva tus materias y calificaciones en un solo lugar.",
                        color = MoradoOscuro,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))
    }
}

@Composable
fun TarjetaMenu(
    titulo: String,
    descripcion: String,
    simbolo: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(158.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Blanco
        ),
        border = BorderStroke(1.dp, MoradoClaro),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MoradoClaro
                )
            ) {
                Box(
                    modifier = Modifier.size(38.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        simbolo,
                        color = MoradoOscuro,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                titulo,
                fontWeight = FontWeight.Bold,
                color = TextoOscuro,
                style = MaterialTheme.typography.titleSmall
            )

            Text(
                descripcion,
                color = TextoSecundario,
                style = MaterialTheme.typography.bodySmall,
                lineHeight = 16.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaBase(
    titulo: String,
    onRegresar: () -> Unit,
    contenido: @Composable () -> Unit
) {
    Scaffold(
        containerColor = Fondo,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        titulo,
                        fontWeight = FontWeight.Bold,
                        color = Blanco
                    )
                },
                navigationIcon = {
                    if (titulo != "Mi Kardex") {
                        TextButton(onClick = onRegresar) {
                            Text("Volver", color = Blanco)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MoradoOscuro,
                    titleContentColor = Blanco,
                    navigationIconContentColor = Blanco
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Fondo)
                .padding(padding)
        ) {
            contenido()
        }
    }
}

@Composable
fun FormularioMateria(
    materia: MateriaEntity?,
    onGuardar: (MateriaEntity) -> Unit,
    onCancelar: () -> Unit
) {
    var nombre by remember(materia?.id) {
        mutableStateOf(materia?.nombre ?: "")
    }
    var semestre by remember(materia?.id) {
        mutableStateOf(materia?.semestre?.toString() ?: "1")
    }
    var normal by remember(materia?.id) {
        mutableStateOf(materia?.calNormal?.toString() ?: "")
    }
    var repeticion by remember(materia?.id) {
        mutableStateOf(materia?.calRepeticion?.toString() ?: "")
    }
    var especial by remember(materia?.id) {
        mutableStateOf(materia?.calEspecial?.toString() ?: "")
    }
    var error by remember(materia?.id) {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Fondo)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Datos de la materia",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = TextoOscuro
        )

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre de la materia") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(14.dp)
        )

        OutlinedTextField(
            value = semestre,
            onValueChange = { semestre = it },
            label = { Text("Semestre (1 al 9)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(14.dp)
        )

        OutlinedTextField(
            value = normal,
            onValueChange = { normal = it },
            label = { Text("Calificación normal (opcional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(14.dp)
        )

        OutlinedTextField(
            value = repeticion,
            onValueChange = { repeticion = it },
            label = { Text("Calificación de repetición (opcional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(14.dp)
        )

        OutlinedTextField(
            value = especial,
            onValueChange = { especial = it },
            label = { Text("Calificación especial (opcional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(14.dp)
        )

        if (error.isNotBlank()) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error
            )
        }

        Button(
            onClick = {
                val numSemestre = semestre.toIntOrNull()
                val calN = normal.replace(',', '.').toDoubleOrNull()
                val calR = repeticion.replace(',', '.').toDoubleOrNull()
                val calE = especial.replace(',', '.').toDoubleOrNull()

                val notasValidas =
                    (normal.isBlank() || calN != null) &&
                            (repeticion.isBlank() || calR != null) &&
                            (especial.isBlank() || calE != null) &&
                            listOfNotNull(calN, calR, calE).all {
                                it in 0.0..100.0
                            }

                val datosValidos =
                    nombre.isNotBlank() &&
                            numSemestre != null &&
                            numSemestre in 1..9 &&
                            notasValidas

                if (!datosValidos) {
                    error = "Revisa el nombre, el semestre y las calificaciones. Las notas deben estar entre 0 y 100."
                } else {
                    error = ""
                    onGuardar(
                        MateriaEntity(
                            id = materia?.id ?: 0,
                            nombre = nombre.trim(),
                            semestre = numSemestre!!,
                            calNormal = calN,
                            calRepeticion = calR,
                            calEspecial = calE
                        )
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Morado,
                contentColor = Blanco
            )
        ) {
            Text("Guardar materia")
        }

        OutlinedButton(
            onClick = onCancelar,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MoradoOscuro
            )
        ) {
            Text("Cancelar")
        }
    }
}

@Composable
fun TarjetaMateria(
    materia: MateriaEntity,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    var confirmarEliminar by remember {
        mutableStateOf(false)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MoradoClaro
        ),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, MoradoClaro)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            Text(
                materia.nombre,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MoradoOscuro
            )

            Text(
                "Semestre: ${materia.semestre}",
                color = TextoOscuro
            )
            Text(
                "Normal: ${materia.calNormal?.toString() ?: "Sin calificación"}",
                color = TextoOscuro
            )
            Text(
                "Repetición: ${materia.calRepeticion?.toString() ?: "Sin calificación"}",
                color = TextoOscuro
            )
            Text(
                "Especial: ${materia.calEspecial?.toString() ?: "Sin calificación"}",
                color = TextoOscuro
            )

            val aprobatoria = materia.calificacionAprobatoria()

            Text(
                "Calificación aprobatoria: ${
                    aprobatoria?.toString() ?: "No aprobada"
                }",
                fontWeight = FontWeight.SemiBold,
                color = MoradoOscuro
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onEditar,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MoradoOscuro
                    )
                ) {
                    Text("Editar")
                }

                Button(
                    onClick = { confirmarEliminar = true },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Morado,
                        contentColor = Blanco
                    )
                ) {
                    Text("Eliminar")
                }
            }
        }
    }

    if (confirmarEliminar) {
        AlertDialog(
            onDismissRequest = {
                confirmarEliminar = false
            },
            title = { Text("Eliminar materia") },
            text = {
                Text("¿Deseas eliminar ${materia.nombre}?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        confirmarEliminar = false
                        onEliminar()
                    }
                ) {
                    Text("Sí, eliminar", color = MoradoOscuro)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        confirmarEliminar = false
                    }
                ) {
                    Text("Cancelar", color = TextoSecundario)
                }
            },
            containerColor = Fondo
        )
    }
}

fun formatoPromedio(promedio: Double): String {
    return String.format(Locale.getDefault(), "%.2f", promedio)
}
