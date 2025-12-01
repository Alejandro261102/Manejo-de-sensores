package com.example.sensorappipn

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import com.example.sensorappipn.ui.theme.SensorAppIPNTheme

class MainActivity : ComponentActivity(), SensorEventListener {

    // Variables del Sensor Manager
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var lightSensor: Sensor? = null

    // Estados mutables para la UI (observables)
    private var accelX = mutableStateOf(0f)
    private var accelY = mutableStateOf(0f)
    private var accelZ = mutableStateOf(0f)
    private var lightValue = mutableStateOf(0f)

    // Estados de configuración
    private var isEscomTheme = mutableStateOf(false) // false = IPN (Guinda), true = ESCOM (Azul)
    private var isAccelEnabled = mutableStateOf(true)
    private var isLightEnabled = mutableStateOf(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar Sensores
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        createNotificationChannel()

        setContent {
            // Aplicamos el tema pasando nuestra variable de estado
            SensorAppIPNTheme(isEscomTheme = isEscomTheme.value) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(
                        isEscomTheme = isEscomTheme,
                        accelX = accelX.value,
                        accelY = accelY.value,
                        accelZ = accelZ.value,
                        lightVal = lightValue.value,
                        isAccelEnabled = isAccelEnabled,
                        isLightEnabled = isLightEnabled,
                        onToggleAccel = { toggleSensor(Sensor.TYPE_ACCELEROMETER, it) },
                        onToggleLight = { toggleSensor(Sensor.TYPE_LIGHT, it) }
                    )
                }
            }
        }
    }

    // Funciones del ciclo de vida para ahorrar batería (Requisito 4)
    override fun onResume() {
        super.onResume()
        if (isAccelEnabled.value) registerSensor(Sensor.TYPE_ACCELEROMETER)
        if (isLightEnabled.value) registerSensor(Sensor.TYPE_LIGHT)
    }

    override fun onPause() {
        super.onPause()
        // Desregistramos para ahorrar batería, pero mandamos notificación
        // Nota: Para mantener lectura real en segundo plano se requeriría un ForegroundService complejo.
        // Para este ejercicio, notificamos el estado antes de pausar.
        sensorManager.unregisterListener(this)
        sendBackgroundNotification()
    }

    private fun registerSensor(type: Int) {
        if (type == Sensor.TYPE_ACCELEROMETER && accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
        }
        if (type == Sensor.TYPE_LIGHT && lightSensor != null) {
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    private fun toggleSensor(type: Int, enabled: Boolean) {
        if (enabled) {
            registerSensor(type)
        } else {
            if (type == Sensor.TYPE_ACCELEROMETER) sensorManager.unregisterListener(this, accelerometer)
            if (type == Sensor.TYPE_LIGHT) sensorManager.unregisterListener(this, lightSensor)
        }

        if(type == Sensor.TYPE_ACCELEROMETER) isAccelEnabled.value = enabled
        else isLightEnabled.value = enabled
    }

    // Callbacks del SensorEventListener
    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                accelX.value = it.values[0]
                accelY.value = it.values[1]
                accelZ.value = it.values[2]
            } else if (it.sensor.type == Sensor.TYPE_LIGHT) {
                lightValue.value = it.values[0]
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No necesario para este ejercicio
    }

    // Notificaciones (Requisito 4)
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "SensorChannel"
            val descriptionText = "Notificaciones de sensores"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("SENSOR_CHANNEL_ID", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendBackgroundNotification() {
        val builder = NotificationCompat.Builder(this, "SENSOR_CHANNEL_ID")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("App Sensores en Pausa")
            .setContentText("La aplicación está en segundo plano para ahorrar batería.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, builder.build())
    }
}

// Interfaz de Usuario (Compose)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    isEscomTheme: MutableState<Boolean>,
    accelX: Float,
    accelY: Float,
    accelZ: Float,
    lightVal: Float,
    isAccelEnabled: MutableState<Boolean>,
    isLightEnabled: MutableState<Boolean>,
    onToggleAccel: (Boolean) -> Unit,
    onToggleLight: (Boolean) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sensores IPN - ESCOM") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Sección de Configuración de Tema
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Configuración de Tema", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = if (isEscomTheme.value) "Tema: ESCOM (Azul)" else "Tema: IPN (Guinda)")
                        Switch(
                            checked = isEscomTheme.value,
                            onCheckedChange = { isEscomTheme.value = it }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sección Acelerómetro
            SensorCard(
                title = "Acelerómetro",
                icon = "📐",
                isEnabled = isAccelEnabled.value,
                onEnableChange = onToggleAccel
            ) {
                if (isAccelEnabled.value) {
                    Text("X: ${String.format("%.2f", accelX)}")
                    Text("Y: ${String.format("%.2f", accelY)}")
                    Text("Z: ${String.format("%.2f", accelZ)}")
                } else {
                    Text("Sensor Desactivado", color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sección Sensor de Luz
            SensorCard(
                title = "Sensor de Luz (Ambiental)",
                icon = "💡",
                isEnabled = isLightEnabled.value,
                onEnableChange = onToggleLight
            ) {
                if (isLightEnabled.value) {
                    Text("Valor: $lightVal lux")

                    // Barra visual simple
                    LinearProgressIndicator(
                        progress = { (lightVal / 1000f).coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    )
                } else {
                    Text("Sensor Desactivado", color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Ejercicio 1: Implementación de Sensores\nDesarrollo Móvil Nativo",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun SensorCard(
    title: String,
    icon: String,
    isEnabled: Boolean,
    onEnableChange: (Boolean) -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = icon, fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }
                Switch(checked = isEnabled, onCheckedChange = onEnableChange)
            }
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            content()
        }
    }
}