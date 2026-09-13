package com.udistrital.giroespia.composables

import android.media.AudioManager
import android.media.ToneGenerator
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.udistrital.giroespia.ui.theme.SpyAccentGold
import com.udistrital.giroespia.ui.theme.SpyDark
import com.udistrital.giroespia.ui.theme.SpyPrimary
import com.udistrital.giroespia.ui.theme.SpySurface
import com.udistrital.giroespia.ui.theme.SpyTextSecondary

/**
 * Emite un sonido de clic táctico corto usando ToneGenerator sin requerir archivos mp3 externos.
 */
private fun playButtonClickSound() {
    try {
        val toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 80)
        toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, 25)
    } catch (_: Exception) {}
}

/**
 * PANTALLA 1 — INICIO
 * Presenta la misión, explica brevemente el objetivo del juego y da acceso
 * a Nueva partida, Instrucciones y Ranking. Estética "agente espía", cálida
 * y coherente con el ícono de la app.
 */
@Composable
fun WelcomeScreen(
    onStartGame: () -> Unit,
    onOpenHelp: () -> Unit,
    onOpenRanking: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 28.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(4.dp))

        // --- Emblema de misión ---
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(SpyDark),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Radar,
                    contentDescription = "Emblema de misión de espionaje",
                    tint = SpyAccentGold,
                    modifier = Modifier.size(64.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "GIRO ESPÍA",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = SpyDark,
                letterSpacing = 2.sp
            )
            Text(
                text = "M A S T E R",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = SpyAccentGold,
                letterSpacing = 6.sp
            )
        }

        // --- Tarjeta de misión ---
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(SpySurface)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Encuentra al agente oculto utilizando la orientación de tu teléfono.",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = SpyDark,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "¿Estás listo para la misión?",
                fontSize = 14.sp,
                color = SpyTextSecondary,
                textAlign = TextAlign.Center
            )
        }

        // --- Acciones ---
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    playButtonClickSound()
                    onStartGame()
                },
                colors = ButtonDefaults.buttonColors(containerColor = SpyPrimary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(56.dp)
            ) {
                Text(text = "NUEVA PARTIDA", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = {
                    playButtonClickSound()
                    onOpenHelp()
                },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = SpyDark),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(52.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.HelpOutline,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "INSTRUCCIONES", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = {
                    playButtonClickSound()
                    onOpenRanking()
                },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = SpyDark),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(52.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = SpyAccentGold,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "RANKING", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}