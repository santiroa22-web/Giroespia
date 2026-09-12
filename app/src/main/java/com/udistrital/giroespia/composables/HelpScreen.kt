package com.udistrital.giroespia.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.udistrital.giroespia.ui.theme.SpyAccentGold
import com.udistrital.giroespia.ui.theme.SpyDark
import com.udistrital.giroespia.ui.theme.SpyPrimary
import com.udistrital.giroespia.ui.theme.SpySurface
import com.udistrital.giroespia.ui.theme.SpyTextSecondary
import com.udistrital.giroespia.ui.theme.TempCold
import com.udistrital.giroespia.ui.theme.TempHot
import com.udistrital.giroespia.ui.theme.TempWarm

private data class HelpItem(
    val icon: ImageVector,
    val iconTint: Color,
    val title: String,
    val description: String
)

private val helpItems = listOf(
    HelpItem(
        icon = Icons.Default.Visibility,
        iconTint = SpyDark,
        title = "Agente oculto",
        description = "El personaje está escondido en una dirección desconocida."
    ),
    HelpItem(
        icon = Icons.Default.ScreenRotation,
        iconTint = SpyDark,
        title = "Gira el teléfono",
        description = "Gira o mueve el teléfono para buscarlo en todas direcciones."
    ),
    HelpItem(
        icon = Icons.Default.Thermostat,
        iconTint = SpyDark,
        title = "Observa la temperatura",
        description = "El indicador te dice qué tan cerca estás del agente."
    ),
    HelpItem(
        icon = Icons.Default.AcUnit,
        iconTint = TempCold,
        title = "Frío",
        description = "Significa que estás lejos del objetivo."
    ),
    HelpItem(
        icon = Icons.Default.Thermostat,
        iconTint = TempWarm,
        title = "Tibio",
        description = "Significa que te estás acercando."
    ),
    HelpItem(
        icon = Icons.Default.LocalFireDepartment,
        iconTint = TempHot,
        title = "Caliente",
        description = "Significa que estás muy cerca. ¡Confírmalo!"
    ),
    HelpItem(
        icon = Icons.Default.Timer,
        iconTint = SpyDark,
        title = "Contra el reloj",
        description = "Encuentra al personaje antes de que se acabe el tiempo."
    )
)

/**
 * PANTALLA 2 — AYUDA / INSTRUCCIONES
 * Explica la mecánica con tarjetas e iconos, evitando bloques largos de texto.
 */
@Composable
fun HelpScreen(
    onStartMission: () -> Unit,
    onBackToMenu: () -> Unit
) {
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
                    tint = SpyDark,
                    modifier = Modifier
                        .padding(4.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "INSTRUCCIONES",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = SpyDark
            )
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(helpItems) { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SpySurface)
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.background),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            tint = item.iconTint,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = item.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = SpyDark
                        )
                        Text(
                            text = item.description,
                            fontSize = 13.sp,
                            color = SpyTextSecondary
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onStartMission,
                colors = ButtonDefaults.buttonColors(containerColor = SpyPrimary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(text = "COMENZAR MISIÓN", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Volver al menú",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = SpyAccentGold,
                modifier = Modifier
                    .clickable { onBackToMenu() }
                    .padding(8.dp)
            )
        }
    }
}
