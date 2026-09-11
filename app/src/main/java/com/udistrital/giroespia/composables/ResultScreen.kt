package com.udistrital.giroespia.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ResultScreen(
    score: Int,
    timeUsed: Int,
    precisionError: Float,
    isVictory: Boolean,
    onPlayAgain: () -> Unit,
    onBackToMenu: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isVictory) {
            Text(
                text = "¡LO ENCONTRASTE!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4CAF50)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Puntuación Final: $score pts", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(text = "Tiempo empleado: $timeUsed seg", fontSize = 16.sp)
            Text(text = "Error de precisión: ${String.format("%.1f", precisionError)}°", fontSize = 16.sp)
        } else {
            Text(
                text = "¡TIEMPO AGOTADO!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Red
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "No lograste encontrar al personaje a tiempo.",
                fontSize = 16.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(36.dp))

        Button(
            onClick = onPlayAgain,
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text(text = "JUGAR DE NUEVO")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onBackToMenu,
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text(text = "VOLVER AL MENÚ")
        }
    }
}