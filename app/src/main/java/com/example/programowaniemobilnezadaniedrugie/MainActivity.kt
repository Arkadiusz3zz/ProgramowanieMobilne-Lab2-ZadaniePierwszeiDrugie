package com.example.programowaniemobilnezadaniedrugie

import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.programowaniemobilnezadaniedrugie.ui.theme.ProgramowanieMobilneZadanieDrugieTheme



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
fun EkranGlowny(navController: NavController){

    var text by remember { mutableStateOf(" ") }

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
            navController.navigate("ekranDrugi/$text")
        }) {
            Text(text = "Temperatura")
        }
        Button(onClick = {
            navController.navigate("ekranTrzeci/$text")
        }) {
            Text(text = "Akcelerometr")
        }
        Button(onClick = {
            navController.navigate("ekranCzwarty/$text")
        }) {
            Text(text = "Oświetlenia")
        }
        }
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
        )
    }}

@Composable
fun EkranDrugi(navController: NavController, text: String){
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
fun EkranTrzeci(navController: NavController, text: String){
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Ekran Trzeci",
            fontSize = 30.sp
        )
        Button(onClick = {
            navController.navigate("ekranPierwszy")
        }) {
            Text(text = "Powrót")
        }
        Text(
            text = "$text",
            fontSize = 30.sp
        )
    }}

@Composable
fun EkranCzwarty(navController: NavController, text: String){
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Ekran Czwarty",
            fontSize = 30.sp
        )
        Button(onClick = {
            navController.navigate("ekranPierwszy")
        }) {
            Text(text = "Powrót")
        }
        Text(
            text = "$text",
            fontSize = 30.sp
        )
    }}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "EkranPierwszy") {
        composable("ekranPierwszy") {
            EkranGlowny(navController = navController)
        }
        composable("ekranDrugi/{text}",
            arguments = listOf(navArgument("text") { type = NavType.StringType})
        ) {
            entry ->
            val text = entry.arguments?.getString("text") ?: "Unknown"
            EkranDrugi(navController = navController, text = text)
        }
        composable("ekranTrzeci/{text}",
            arguments = listOf(navArgument("text") { type = NavType.StringType})
            ) {
            entry ->
            val text = entry.arguments?.getString("text") ?: "Unknown"
            EkranTrzeci(navController = navController, text = text)
        }
        composable("ekranCzwarty/{text}",
            arguments = listOf(navArgument("text") { type = NavType.StringType})
            ) {
            entry ->
            val text = entry.arguments?.getString("text") ?: "Unknown"
            EkranCzwarty(navController = navController, text = text)
        }
    }
}