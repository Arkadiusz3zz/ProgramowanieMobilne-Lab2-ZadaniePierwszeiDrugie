package com.example.programowaniemobilnezadaniedrugie

import android.app.Activity
import android.content.Context.SENSOR_SERVICE
import android.content.pm.ActivityInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.programowaniemobilnezadaniedrugie.ui.theme.ProgramowanieMobilneZadanieDrugieTheme
import kotlinx.coroutines.launch
import java.text.DecimalFormat


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProgramowanieMobilneZadanieDrugieTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavGraph(navController = navController)

                }
            }
        }
    }
}

@Composable
fun LockOrientation(orientation: Int) {
    val activity = LocalContext.current as? Activity
    activity?.requestedOrientation = orientation
}

@Composable
fun EkranGlowny(navController: NavController){

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Ekran Pierwszy",
            fontSize = 30.sp
        )
        Row(
            modifier = Modifier
        ) {
        Button(onClick = {
            navController.navigate("ekranDrugi")
        }) {
            Text(text = "Temperatura")
        }
        Button(onClick = {
            navController.navigate("ekranTrzeci")
        }) {
            Text(text = "Akcelerometr")
        }
        Button(onClick = {
            navController.navigate("ekranCzwarty")
        }) {
            Text(text = "Oświetlenia")
        }
        }
    }}

@Composable
fun EkranDrugi(navController: NavController){
    val context = LocalContext.current
    val sensorManager = context.getSystemService(SENSOR_SERVICE) as SensorManager
    val temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
    var temperature by remember {
        mutableStateOf("Niedostępny")
    }
    val sensorEventListener = remember {
        object : SensorEventListener{
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let{
                    temperature = "${it.values[0]} C"
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
    }
    DisposableEffect(Unit){
        if (temperatureSensor != null){
            sensorManager.registerListener(sensorEventListener,
            temperatureSensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
        else{
            temperature = "Czujnik niedostępny"
        }
        onDispose {
            sensorManager.unregisterListener(sensorEventListener)
        }
    }

    val iconColor = when{
        temperature.startsWith("Niedostępny") -> Color.Gray
        else -> {val temp = temperature.removeSuffix(" C").toFloat()
        when{
            temp < 20.0 -> Color.Blue
            temp in 20.0..40.0 -> Color.Green
            temp > 40.0 -> Color.Red
            else -> Color.Gray
        }}
        }




    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Temperatura otoczenia",
            fontSize = 30.sp
        )
        Text(
            text = temperature,
            fontSize = 30.sp
        )
        Image(painter = painterResource(id = R.drawable.termostat),
            contentDescription = null,
            colorFilter = ColorFilter.tint(iconColor),
        modifier = Modifier.size(100.dp))
        Button(onClick = {
            navController.navigate("ekranPierwszy")
        }) {
            Text(text = "Powrót")
        }

    }}

@Composable
fun EkranTrzeci(navController: NavController){

    val context = LocalContext.current
    val sensorManager = context.getSystemService(SENSOR_SERVICE) as SensorManager
    val gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    var gyroX by remember { mutableFloatStateOf(0f) }
    var gyroY by remember { mutableFloatStateOf(0f) }
    var gyroZ by remember { mutableFloatStateOf(0f) }

    var offsetX by remember { mutableFloatStateOf(5f) }
    var offsetY by remember { mutableFloatStateOf(5f) }
    //var offsetZ by remember { mutableFloatStateOf(0f) }

    val sensorEventListener = remember {
        object : SensorEventListener{
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let{
                    gyroX = it.values[0]
                    gyroY = it.values[1]
                    gyroZ = it.values[2]
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
    }

    DisposableEffect(Unit) {
        if (gyroscopeSensor != null){
            sensorManager.registerListener(
                sensorEventListener,
                gyroscopeSensor,
                SensorManager.SENSOR_DELAY_FASTEST
            )
        }
        onDispose {
            sensorManager.unregisterListener(sensorEventListener)
        }
    }

    LockOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

    val df = DecimalFormat("#.##")

    val ballSize = 30.dp
    val platformHeight = 16.dp
    val canvasHeight = 380.dp
    val canvasWidth = 400.dp

    val density = LocalDensity.current
    val ballSizePx = with(density) { ballSize.toPx() }
    val platformHeightPx = with(density) { platformHeight.toPx() }
    val canvasWidthPx = with(density) { canvasWidth.toPx()}
    val canvasHeightPx = with(density) { canvasHeight.toPx() }

    // Animacja pozycji piłki (Y)
    val ballPositionY = remember { Animatable(canvasHeightPx/2) }
    val ballPositionX = remember { Animatable(canvasWidthPx/2) }

    LaunchedEffect(Unit) {

        var yCooldown = false
        var xCooldown = false

        val dampingFactor = 0.98f // Damping factor to reduce speed
        val dampingFactorOffset = 0.5f

        var previousGyroZ = gyroZ
        var previousGyroX = gyroX

        while (true) {
            // Calculate offset for Y-axis
            if (ballPositionY.value <= platformHeightPx + ballSizePx / 2) {
                offsetY = -offsetY * dampingFactorOffset
                yCooldown = true
            } else if (ballPositionY.value >= canvasHeightPx - platformHeightPx - ballSizePx / 2) {
                offsetY = -offsetY * dampingFactorOffset
                yCooldown = true
            } else if (!yCooldown && gyroZ != previousGyroZ) {
                offsetY = (offsetY - gyroZ*10 - previousGyroZ * 10) * dampingFactor
            }

            // Calculate offset for X-axis
            if (ballPositionX.value <= platformHeightPx + ballSizePx / 2) {
                offsetX = -offsetX * dampingFactorOffset
                xCooldown = true
            } else if (ballPositionX.value >= canvasWidthPx - platformHeightPx - ballSizePx / 2) {
                offsetX = -offsetX * dampingFactorOffset
                xCooldown = true
            } else if (!xCooldown && gyroX != previousGyroX) {
                offsetX = (offsetX - gyroX*10 - previousGyroX * 10) * dampingFactor
            }

            // Update previous gyro values
            previousGyroZ = gyroZ
            previousGyroX = gyroX

            // Update ball position for both axes
            val targetValueY = ballPositionY.value + offsetY
            val targetValueX = ballPositionX.value + offsetX

            launch {
                ballPositionY.animateTo(
                    targetValue = targetValueY,
                    animationSpec = TweenSpec(durationMillis = 100)
                )
            }

            launch {
                ballPositionX.animateTo(
                    targetValue = targetValueX,
                    animationSpec = TweenSpec(durationMillis = 100)
                )
            }

            // Add delay to prevent blocking the main thread
            kotlinx.coroutines.delay(16L) // Approximately 60 FPS

            // Reset cooldowns after a short delay
            if (yCooldown) {
                kotlinx.coroutines.delay(25L) // 500ms cooldown
                yCooldown = false
            }
            if (xCooldown) {
                kotlinx.coroutines.delay(25L) // 500ms cooldown
                xCooldown = false
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Akcelerometr",
            fontSize = 30.sp
        )
        Row {
            Text(
            text = "Z:  ${df.format(gyroX)} rad/s ",
            fontSize = 19.sp
        )
            Text(
                text = "Y:  ${df.format(gyroY)} rad/s ",
                fontSize = 19.sp
            )
            Text(
                text = "X:  ${df.format(gyroZ)} rad/s",
                fontSize = 19.sp
            )
             }

        Canvas(
            modifier = Modifier
                .size(canvasWidth, canvasHeight)
        ) {
            drawRoundRect(
                color = Color.Gray,
                topLeft = Offset(
                    x = 0f,
                    y = 0f
                ),
                size = androidx.compose.ui.geometry.Size(
                    width = canvasWidthPx,
                    height = platformHeightPx
                ),
                cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
            )

            // Bottom platform
            drawRoundRect(
                color = Color.Gray,
                topLeft = Offset(
                    x = 0f,
                    y = canvasHeightPx - platformHeightPx
                ),
                size = androidx.compose.ui.geometry.Size(
                    width = canvasWidthPx,
                    height = platformHeightPx
                ),
                cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
            )

            // Left platform
            drawRoundRect(
                color = Color.Gray,
                topLeft = Offset(
                    x = 0f,
                    y = 0f
                ),
                size = androidx.compose.ui.geometry.Size(
                    width = platformHeightPx,
                    height = canvasHeightPx
                ),
                cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
            )

            // Right platform
            drawRoundRect(
                color = Color.Gray,
                topLeft = Offset(
                    x = canvasWidthPx - platformHeightPx,
                    y = 0f
                ),
                size = androidx.compose.ui.geometry.Size(
                    width = platformHeightPx,
                    height = canvasHeightPx
                ),
                cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
            )

            // Draw the ball
            val ballY = ballPositionY.value
            val ballx = ballPositionX.value
            drawCircle(
                color = Color.Red,
                radius = ballSizePx / 2,
                center = Offset(x = ballx, y = ballY)
            )
        }

        Button(onClick = {
            navController.navigate("ekranPierwszy")
        },
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp)) {
            Text(text = "Powrót")
        }
    }}

@Composable
fun EkranCzwarty(navController: NavController){

    val context = LocalContext.current
    val sensorManager = context.getSystemService(SENSOR_SERVICE) as SensorManager
    val temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
    var swiatlo by remember {
        mutableStateOf("Niedostępny")
    }
    val sensorEventListener = remember {
        object : SensorEventListener{
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let{
                    swiatlo = "${it.values[0]} lux"
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
    }

    DisposableEffect(Unit){
        if (temperatureSensor != null){
            sensorManager.registerListener(sensorEventListener,
                temperatureSensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
        else{
            swiatlo = "Czujnik niedostępny"
        }
        onDispose {
            sensorManager.unregisterListener(sensorEventListener)
        }
    }

    val brightness = when{
        swiatlo.startsWith("Niedostępny") -> 1f
        else -> {val temp = swiatlo.removeSuffix(" lux").toFloat()
            when{
                temp < 10000.0 -> 0.80f
                temp in 10000.0..20000.0 -> 0.50f
                temp > 20000.0 -> 0.10f
                else -> 1f
            }}
    }

    Column(
        modifier = Modifier.fillMaxSize().
        background(Color.Black.copy(alpha = brightness)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Wartość oświetlenia",
            fontSize = 30.sp,
            color = Color.White
        )
        Text(
            text = swiatlo,
            fontSize = 30.sp,
            color = Color.White
        )
        Button(onClick = {
            navController.navigate("ekranPierwszy")
        }) {
            Text(text = "Powrót")
        }
        Text(
            text = " ",
            fontSize = 30.sp
        )
    }}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "EkranPierwszy") {
        composable("ekranPierwszy") {
            EkranGlowny(navController = navController)
        }
        composable("ekranDrugi") {
            EkranDrugi(navController = navController)
        }
        composable("ekranTrzeci") {
            EkranTrzeci(navController = navController)
        }
        composable("ekranCzwarty") {
            EkranCzwarty(navController = navController)
        }
    }
}


