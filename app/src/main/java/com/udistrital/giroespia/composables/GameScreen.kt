package com.udistrital.giroespia.composables

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.udistrital.giroespia.R
import com.udistrital.giroespia.ui.theme.SpyDark
import com.udistrital.giroespia.ui.theme.SpyPanel
import com.udistrital.giroespia.ui.theme.SpySurface
import kotlinx.coroutines.delay
import java.util.Locale
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun GameScreen(
    maxTimeSeconds: Int = 45,
    onBackToMenu: () -> Unit,
    onRestart: () -> Unit,
    onGameFinished: (score: Int, timeUsed: Int, precisionError: Float, isVictory: Boolean) -> Unit
) {
    val context = LocalContext.current

    // ---- LÓGICA DEL JUEGO (sin cambios) ----
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
    // ---- FIN LÓGICA DEL JUEGO ----

    val coldColor = colorResource(id = R.color.temp_cold)
    val warmColor = colorResource(id = R.color.temp_warm)
    val hotColor = colorResource(id = R.color.temp_hot)

    val stateText = when {
        shortestDistance < 15f -> "Caliente"
        shortestDistance < 45f -> "Tibio"
        else -> "Frío"
    }

    val stateColor = when {
        shortestDistance < 15f -> hotColor
        shortestDistance < 45f -> warmColor
        else -> coldColor
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // CABECERA (atrás, reiniciar, temporizador, puntuación)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(SpySurface)
                        .clickable { onBackToMenu() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver al menú",
                        tint = SpyDark
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(SpySurface)
                        .clickable { onRestart() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reiniciar misión",
                        tint = SpyDark
                    )
                }
            }

            // Tarjeta del Temporizador
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(SpySurface)
                    .padding(horizontal = 18.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "⏱ ", fontSize = 22.sp)
                    Text(
                        text = formattedTime,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = SpyDark
                    )
                }
            }

            // Tarjeta de Puntos
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(SpySurface)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Puntos",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SpyDark
                    )
                    Text(
                        text = "$currentCalculatedPoints",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = SpyDark
                    )
                }
            }
        }

        // ZONA CENTRAL: estado + radar de orientación
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stateText,
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = stateColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Radar: muestra SOLO la orientación actual del jugador (la aguja),
            // nunca la dirección objetivo. El color del anillo cambia según
            // la proximidad (frío/tibio/caliente) para reforzar la búsqueda.
            OrientationRadar(
                currentAngleDegrees = normalizedAngle,
                ringColor = stateColor,
                proximity = proximityProgress
            )

            Spacer(modifier = Modifier.height(20.dp))

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
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = hotColor)
                ) {
                    Text(text = "¡ENCONTRADO!", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // BARRA INFERIOR DE PROXIMIDAD
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .background(SpyPanel, shape = RoundedCornerShape(12.dp))
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
                    .semantics { contentDescription = "Nivel de proximidad: $stateText" }
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

/**
 * Radar circular tipo "brújula de agente": dibuja anillos concéntricos y una
 * aguja que apunta hacia donde está orientado el teléfono en este momento.
 * NUNCA dibuja ni insinúa la dirección objetivo — solo ayuda al jugador a
 * entender hacia dónde está girando mientras busca.
 */
@Composable
private fun OrientationRadar(
    currentAngleDegrees: Float,
    ringColor: Color,
    proximity: Float
) {
    Box(
        modifier = Modifier
            .size(220.dp)
            .semantics {
                contentDescription = "Radar de orientación del teléfono"
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val maxRadius = size.minDimension / 2f

            // Anillos concéntricos de fondo (estética de radar)
            val ringColors = listOf(
                SpyDark.copy(alpha = 0.10f),
                SpyDark.copy(alpha = 0.10f),
                SpyDark.copy(alpha = 0.10f)
            )
            ringColors.forEachIndexed { index, color ->
                val fraction = (index + 1) / ringColors.size.toFloat()
                drawCircle(
                    color = color,
                    radius = maxRadius * fraction,
                    center = center,
                    style = Stroke(width = 2.dp.toPx())
                )
            }

            // Anillo de proximidad: su grosor y opacidad crecen al acercarse
            drawCircle(
                color = ringColor.copy(alpha = 0.25f + 0.5f * proximity),
                radius = maxRadius * 0.92f,
                center = center,
                style = Stroke(width = (4 + 10 * proximity).dp.toPx())
            )

            // Aguja: apunta hacia la orientación ACTUAL del teléfono (0° = arriba)
            val angleRad = Math.toRadians((currentAngleDegrees - 90f).toDouble())
            val needleLength = maxRadius * 0.75f
            val tip = Offset(
                x = center.x + (needleLength * cos(angleRad)).toFloat(),
                y = center.y + (needleLength * sin(angleRad)).toFloat()
            )
            drawLine(
                color = SpyDark,
                start = center,
                end = tip,
                strokeWidth = 6.dp.toPx()
            )
            drawCircle(color = SpyDark, radius = 8.dp.toPx(), center = center)
            drawCircle(color = ringColor, radius = 10.dp.toPx(), center = tip)
        }
    }
}
