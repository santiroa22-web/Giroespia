package com.udistrital.giroespia.composables

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.udistrital.giroespia.R
import kotlinx.coroutines.delay
import java.util.Locale
import kotlin.math.abs

@Composable
fun GameScreen(
    maxTimeSeconds: Int = 45,
    onBackToMenu: () -> Unit,
    onGameFinished: (score: Int, timeUsed: Int, precisionError: Float, isVictory: Boolean) -> Unit
) {
    val context = LocalContext.current

    var currentAngle by remember { mutableFloatStateOf(0f) }
    var timeLeft by remember { mutableIntStateOf(maxTimeSeconds) }

    val targetAngle by remember {
        mutableFloatStateOf(
            listOf((60..150).random(), (210..300).random()).random().toFloat()
        )
    }

    val normalizedAngle = ((currentAngle % 360f) + 360f) % 360f
    val rawDiff = abs(targetAngle - normalizedAngle)
    val shortestDistance = if (rawDiff > 180f) 360f - rawDiff else rawDiff
    val proximityProgress = (1f - (shortestDistance / 180f)).coerceIn(0f, 1f)

    val minutes = timeLeft / 60
    val seconds = timeLeft % 60
    val formattedTime = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)

    val currentCalculatedPoints = remember(timeLeft) {
        val baseScore = 500
        val timeBonus = timeLeft * 20
        (baseScore + timeBonus).coerceAtLeast(0)
    }

    LaunchedEffect(Unit) {
        while (timeLeft > 0) {
            delay(1000L)
            timeLeft--
        }
        onGameFinished(0, maxTimeSeconds, shortestDistance, false)
    }

    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        var timestamp = 0L

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    if (timestamp != 0L) {
                        val dT = (it.timestamp - timestamp) * 1.0f / 1_000_000_000.0f
                        val zRotationRate = it.values[2]
                        if (abs(zRotationRate) > 0.05f) {
                            currentAngle += Math.toDegrees((zRotationRate * dT).toDouble()).toFloat()
                        }
                    }
                    timestamp = it.timestamp
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        sensorManager.registerListener(listener, gyroSensor, SensorManager.SENSOR_DELAY_GAME)

        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    val coldColor = colorResource(id = R.color.temp_cold)
    val warmColor = colorResource(id = R.color.temp_warm)
    val hotColor = colorResource(id = R.color.temp_hot)

    val stateText = when {
        shortestDistance < 15f -> "Caliente"
        shortestDistance < 45f -> "Tibio"
        else -> "Frío"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // CABECERA AJUSTADA (Reloj más grande y posición ligeramente más arriba)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Botón Atrás
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFF3F4E8))
                    .clickable { onBackToMenu() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver al menú",
                    tint = Color(0xFF1E2614)
                )
            }

            // 2. Tarjeta del Temporizador (Reloj más grande)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF3F4E8))
                    .padding(horizontal = 18.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "⏱ ",
                        fontSize = 22.sp
                    )
                    Text(
                        text = formattedTime,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E2614)
                    )
                }
            }

            // 3. Tarjeta de Puntos
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFF3F4E8))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Puntos",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1E2614)
                    )
                    Text(
                        text = "$currentCalculatedPoints",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E2614)
                    )
                }
            }
        }

        // ZONA CENTRAL
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stateText,
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (shortestDistance < 15f) {
                Button(
                    onClick = {
                        val timeUsed = maxTimeSeconds - timeLeft
                        val baseScore = 500
                        val timeBonus = timeLeft * 20
                        val precisionBonus = when {
                            shortestDistance < 5f -> 300
                            shortestDistance < 10f -> 150
                            else -> 0
                        }
                        val finalScore = baseScore + timeBonus + precisionBonus
                        onGameFinished(finalScore, timeUsed, shortestDistance, true)
                    }
                ) {
                    Text(text = "¡ENCONTRADO!", fontSize = 18.sp)
                }
            }
        }

        // BARRA INFERIOR DE PROXIMIDAD
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .background(Color(0xFF1E1E1E), shape = RoundedCornerShape(12.dp))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "DISTANCIA APROXIMADA",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(coldColor, warmColor, hotColor)
                        )
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(proximityProgress)
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .align(Alignment.CenterEnd)
                            .background(Color.White, CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Frío", fontSize = 12.sp, color = coldColor)
                Text(text = "Tibio", fontSize = 12.sp, color = warmColor)
                Text(text = "Caliente", fontSize = 12.sp, color = hotColor)
            }
        }
    }
}