package com.udistrital.giroespia.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.udistrital.giroespia.data.RankingStore
import com.udistrital.giroespia.ui.theme.SpyAccentGold
import com.udistrital.giroespia.ui.theme.SpyDark
import com.udistrital.giroespia.ui.theme.SpyPrimary
import com.udistrital.giroespia.ui.theme.SpySurface
import com.udistrital.giroespia.ui.theme.SpyTextSecondary
import com.udistrital.giroespia.ui.theme.TempHot

/**
 * PANTALLA 4 — RESULTADO
 * Distingue claramente Victoria ("MISIÓN CUMPLIDA") de Derrota ("MISIÓN FALLIDA"),
 * conservando exactamente los datos calculados por GameScreen.
 */
@Composable
fun ResultScreen(
    score: Int,
    timeUsed: Int,
    precisionError: Float,
    isVictory: Boolean,
    onPlayAgain: () -> Unit,
    onBackToMenu: () -> Unit,
    onViewRanking: () -> Unit
) {
    val context = LocalContext.current

    // Guarda el puntaje en el ranking local solo cuando hay victoria.
    LaunchedEffect(isVictory, score) {
        if (isVictory) {
            RankingStore.addScore(context, score)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(if (isVictory) SpyPrimary else TempHot.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isVictory) Icons.Default.EmojiEvents else Icons.Default.SentimentDissatisfied,
                contentDescription = if (isVictory) "Misión cumplida" else "Misión fallida",
                tint = if (isVictory) SpyAccentGold else TempHot,
                modifier = Modifier.size(52.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = if (isVictory) "MISIÓN CUMPLIDA" else "MISIÓN FALLIDA",
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            color = SpyDark,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = if (isVictory)
                "Excelente trabajo, agente. Localizaste el objetivo."
            else
                "Se agotó el tiempo antes de encontrar al agente oculto.",
            fontSize = 14.sp,
            color = SpyTextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Tarjeta de estadísticas
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(SpySurface)
                .padding(20.dp)
        ) {
            ResultStatRow(label = "Puntuación", value = if (isVictory) "$score pts" else "0 pts")
            Spacer(modifier = Modifier.height(10.dp))
            ResultStatRow(label = "Tiempo empleado", value = "$timeUsed seg")
            Spacer(modifier = Modifier.height(10.dp))
            ResultStatRow(
                label = "Error de precisión",
                value = String.format("%.1f°", precisionError)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onPlayAgain,
            colors = ButtonDefaults.buttonColors(containerColor = SpyPrimary),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(54.dp)
        ) {
            Text(
                text = if (isVictory) "JUGAR DE NUEVO" else "INTENTAR DE NUEVO",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = onBackToMenu,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = SpyDark),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(50.dp)
        ) {
            Text(text = "VOLVER AL MENÚ", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }

        if (isVictory) {
            TextButton(onClick = onViewRanking) {
                Text(
                    text = "VER RANKING",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SpyAccentGold
                )
            }
        }
    }
}

@Composable
private fun ResultStatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 14.sp, color = SpyTextSecondary)
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SpyDark)
    }
}
