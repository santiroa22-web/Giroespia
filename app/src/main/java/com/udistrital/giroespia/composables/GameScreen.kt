package com.udistrital.giroespia.composables

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.udistrital.giroespia.R
import kotlinx.coroutines.delay
import java.util.Locale
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun GameScreen(
    maxTimeSeconds: Int = 15,
    onBackToMenu: () -> Unit,
    onRestart: () -> Unit,
    onGameFinished: (score: Int, timeUsed: Int, precisionError: Float, isVictory: Boolean) -> Unit
) {
    val context = LocalContext.current

    var countdownValue by remember { mutableIntStateOf(3) }
    val isGameActive = countdownValue == 0

    var currentAngle by remember { mutableFloatStateOf(0f) }
    var timeLeft by remember { mutableIntStateOf(maxTimeSeconds) }
    var isFiring by remember { mutableStateOf(false) }

    // Ángulo objetivo totalmente aleatorio (0° a 360°)
    val targetAngle by remember {
        mutableFloatStateOf((0..359).random().toFloat())
    }

    // Distancia fija LEJANA: 92% del borde exterior del radar
    val targetRadiusFraction = 0.92f

    val normalizedAngle = ((currentAngle % 360f) + 360f) % 360f
    val rawDiff = abs(targetAngle - normalizedAngle)
    val shortestDistance = if (rawDiff > 180f) 360f - rawDiff else rawDiff
    val proximityProgress = (1f - (shortestDistance / 180f)).coerceIn(0f, 1f)
    val currentDistanceRef = rememberUpdatedState(shortestDistance)

    // Revelar el objetivo si la luz en V pasa cerca (menos de 20° de error)
    val isTargetIlluminated = shortestDistance <= 20f

    val minutes = timeLeft / 60
    val seconds = timeLeft % 60
    val formattedTime = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)

    val currentCalculatedPoints = remember(timeLeft) {
        val baseScore = 500
        val timeBonus = timeLeft * 30
        (baseScore + timeBonus).coerceAtLeast(0)
    }

    // Cuenta regresiva inicial
    LaunchedEffect(Unit) {
        while (countdownValue > 0) {
            delay(1000L)
            countdownValue--
        }
    }

    // Temporizador de juego
    LaunchedEffect(isGameActive) {
        if (isGameActive) {
            while (timeLeft > 0 && !isFiring) {
                delay(1000L)
                timeLeft--
            }
            if (!isFiring) {
                onGameFinished(0, maxTimeSeconds, shortestDistance, false)
            }
        }
    }

    // Sonar de proximidad
    LaunchedEffect(isGameActive) {
        if (isGameActive) {
            while (!isFiring) {
                val dist = currentDistanceRef.value
                val interval = when {
                    dist < 8f -> 250L
                    dist < 20f -> 500L
                    dist < 45f -> 1000L
                    else -> 1800L
                }

                try {
                    val beep = MediaPlayer.create(context, R.raw.beep)
                    beep?.start()
                    beep?.setOnCompletionListener { it.release() }
                } catch (_: Exception) {}

                delay(interval)
            }
        }
    }

    // Giroscopio
    DisposableEffect(isGameActive) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        var timestamp = 0L

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (!isGameActive || isFiring) return
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

        if (isGameActive) {
            sensorManager.registerListener(listener, gyroSensor, SensorManager.SENSOR_DELAY_GAME)
        }

        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    val coldColor = colorResource(id = R.color.temp_cold)
    val warmColor = colorResource(id = R.color.temp_warm)
    val hotColor = colorResource(id = R.color.temp_hot)

    val stateText = when {
        shortestDistance < 15f -> "¡ENEMIGO EN MIRA!"
        shortestDistance < 45f -> "SEÑAL TIBIA..."
        else -> "RASTREANDO ZONA..."
    }

    // Radio de radar grande (125px de radio útil)
    val radarRadiusPx = 125f
    val targetAngleRad = Math.toRadians(targetAngle.toDouble())

    // Posición del objetivo aleatoria sobre la franja exterior (lejos de la nave central)
    val targetOffsetX = (radarRadiusPx * targetRadiusFraction * sin(targetAngleRad)).toFloat()
    val targetOffsetY = (-radarRadiusPx * targetRadiusFraction * cos(targetAngleRad)).toFloat()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D130E))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // CABECERA
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1E2614))
                        .clickable { onBackToMenu() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color(0xFFC5D6B4)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF1E2614))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "⏱ $formattedTime",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFC5D6B4)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1E2614))
                            .clickable { onRestart() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reiniciar",
                            tint = Color(0xFFC5D6B4)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1E2614))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Puntos", fontSize = 9.sp, color = Color(0xFF8FA87A))
                        Text(text = "$currentCalculatedPoints", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFC5D6B4))
                    }
                }
            }

            // ZONA CENTRAL: RADAR CIRCULAR DE 280.dp
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stateText,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = when {
                        shortestDistance < 15f -> Color(0xFFFF3D00)
                        shortestDistance < 45f -> Color(0xFFFFC107)
                        else -> Color(0xFF8FA87A)
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .size(280.dp)
                        .background(Color(0xFF141F15), CircleShape)
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val radius = size.width / 2f

                        // Anillos concéntricos del radar
                        drawCircle(color = Color(0xFF233820), radius = radius, style = Stroke(2f))
                        drawCircle(color = Color(0xFF233820), radius = radius * 0.66f, style = Stroke(1.5f))
                        drawCircle(color = Color(0xFF233820), radius = radius * 0.33f, style = Stroke(1.5f))

                        // Cono de luz en V dinámico
                        val beamAngleRad = Math.toRadians(normalizedAngle.toDouble())
                        val coneHalfAngleRad = Math.toRadians(16.0)

                        val p1Angle = beamAngleRad - coneHalfAngleRad
                        val p2Angle = beamAngleRad + coneHalfAngleRad

                        val x1 = center.x + (radius * sin(p1Angle)).toFloat()
                        val y1 = center.y - (radius * cos(p1Angle)).toFloat()
                        val x2 = center.x + (radius * sin(p2Angle)).toFloat()
                        val y2 = center.y - (radius * cos(p2Angle)).toFloat()

                        val vBeamPath = Path().apply {
                            moveTo(center.x, center.y)
                            lineTo(x1, y1)
                            lineTo(x2, y2)
                            close()
                        }

                        val vBeamColor = when {
                            shortestDistance < 15f -> Color(0xFFFF3D00).copy(alpha = 0.45f)
                            shortestDistance < 45f -> Color(0xFFFFC107).copy(alpha = 0.30f)
                            else -> Color(0xFF00E676).copy(alpha = 0.20f)
                        }
                        drawPath(path = vBeamPath, color = vBeamColor)

                        // Disparo láser directo al objetivo lejano
                        if (isFiring) {
                            val targetPxX = center.x + targetOffsetX
                            val targetPxY = center.y + targetOffsetY
                            drawLine(
                                color = Color(0xFFFF3D00),
                                start = center,
                                end = Offset(targetPxX, targetPxY),
                                strokeWidth = 7f
                            )
                        }
                    }

                    // NAVE DEL JUGADOR (CENTRO)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        Text(text = "🛩️", fontSize = 20.sp)
                    }

                    // OBJETIVO UBICADO EN LA ORILLA EXTERIOR (LEJOS DEL AVION)
                    if (isTargetIlluminated) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .offset {
                                    IntOffset(
                                        targetOffsetX.roundToInt(),
                                        targetOffsetY.roundToInt()
                                    )
                                }
                        ) {
                            Text(text = "🎯", fontSize = 18.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // BOTÓN DE DISPARAR
                if (shortestDistance < 15f && isGameActive && !isFiring) {
                    Button(
                        onClick = { isFiring = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF3D00)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .height(48.dp)
                    ) {
                        Text(
                            text = if (shortestDistance <= 5.0f) "🎯 ¡DISPARAR (PRECISIÓN PERFECTA)!" else "🔥 ¡DISPARAR AL OBJETIVO!",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // BARRA DE PROXIMIDAD INFERIOR
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .background(Color(0xFF19241A), shape = RoundedCornerShape(12.dp))
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "DISTANCIA APROXIMADA",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFC5D6B4)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp)
                        .clip(RoundedCornerShape(7.dp))
                        .background(Brush.horizontalGradient(listOf(coldColor, warmColor, hotColor)))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(proximityProgress)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .align(Alignment.CenterEnd)
                                .background(Color.White, CircleShape)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Frío", fontSize = 11.sp, color = coldColor)
                    Text(text = "Tibio", fontSize = 11.sp, color = warmColor)
                    Text(text = "Caliente", fontSize = 11.sp, color = hotColor)
                }
            }
        }

        // Animación de disparo
        if (isFiring) {
            LaunchedEffect(Unit) {
                delay(500L)
                val timeUsed = maxTimeSeconds - timeLeft
                val baseScore = 500
                val timeBonus = timeLeft * 30
                val precisionBonus = when {
                    shortestDistance <= 5.0f -> 600
                    shortestDistance < 15.0f -> 200
                    else -> 0
                }
                val finalScore = baseScore + timeBonus + precisionBonus
                onGameFinished(finalScore, timeUsed, shortestDistance, true)
            }
        }

        // Overlay cuenta regresiva inicial (3, 2, 1)
        if (!isGameActive) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.85f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (countdownValue > 0) "$countdownValue" else "¡ADELANTE!",
                    fontSize = 64.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }
        }
    }
}