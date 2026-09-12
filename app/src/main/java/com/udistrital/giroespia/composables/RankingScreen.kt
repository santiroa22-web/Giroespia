package com.udistrital.giroespia.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import com.udistrital.giroespia.data.RankingStore
import com.udistrital.giroespia.ui.theme.SpyAccentGold
import com.udistrital.giroespia.ui.theme.SpyDark
import com.udistrital.giroespia.ui.theme.SpySurface
import com.udistrital.giroespia.ui.theme.SpyTextSecondary

/**
 * PANTALLA 5 — RANKING (local, sin conexión a Internet)
 * Muestra el top 5 de puntuaciones guardadas en el dispositivo.
 */
@Composable
fun RankingScreen(
    onBackToMenu: () -> Unit
) {
    val context = LocalContext.current
    val scores = RankingStore.getScores(context)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
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
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "CLASIFICACIÓN",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = SpyDark
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            if (scores.isEmpty()) {
                Text(
                    text = "Todavía no hay misiones completadas. ¡Sé el primer agente en el ranking!",
                    fontSize = 14.sp,
                    color = SpyTextSecondary
                )
            } else {
                scores.forEachIndexed { index, score ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(SpySurface)
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    if (index == 0) SpyAccentGold else MaterialTheme.colorScheme.background
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (index == 0) {
                                Icon(
                                    imageVector = Icons.Default.EmojiEvents,
                                    contentDescription = "Primer lugar",
                                    tint = SpyDark,
                                    modifier = Modifier.size(18.dp)
                                )
                            } else {
                                Text(
                                    text = "${index + 1}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SpyDark
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Text(
                            text = "$score pts",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = SpyDark
                        )
                    }
                }
            }
        }
    }
}
