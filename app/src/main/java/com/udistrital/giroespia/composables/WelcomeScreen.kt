package com.udistrital.giroespia.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WelcomeScreen(
    onStartGame: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "CALIENTE / FRÍO", fontSize = 28.sp)
        Text(text = "¡Encuéntralo!", fontSize = 20.sp)

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onStartGame,
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text(text = "NUEVA PARTIDA")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { /* TODO: Ranking */ },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text(text = "RANKING")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { /* TODO: Configuración */ },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text(text = "CONFIGURACIÓN")
        }
    }
}