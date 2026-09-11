package com.udistrital.giroespia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.udistrital.giroespia.composables.GameScreen
import com.udistrital.giroespia.composables.ResultScreen
import com.udistrital.giroespia.composables.WelcomeScreen
import com.udistrital.giroespia.enums.TypeScreen
import com.udistrital.giroespia.ui.theme.GiroespiaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GiroespiaTheme {
                var currentScreen by remember { mutableStateOf(TypeScreen.WELCOME) }
                var lastScore by remember { mutableIntStateOf(0) }
                var lastTimeUsed by remember { mutableIntStateOf(0) }
                var lastPrecisionError by remember { mutableFloatStateOf(0f) }
                var isVictory by remember { mutableStateOf(false) }

                when (currentScreen) {
                    TypeScreen.WELCOME -> {
                        WelcomeScreen(
                            onStartGame = { currentScreen = TypeScreen.GAME }
                        )
                    }
                    TypeScreen.GAME -> {
                        GameScreen(
                            maxTimeSeconds = 45,
                            onBackToMenu = { currentScreen = TypeScreen.WELCOME },
                            onGameFinished = { score, timeUsed, precisionError, victory ->
                                lastScore = score
                                lastTimeUsed = timeUsed
                                lastPrecisionError = precisionError
                                isVictory = victory
                                currentScreen = TypeScreen.RESULT
                            }
                        )
                    }
                    TypeScreen.RESULT -> {
                        ResultScreen(
                            score = lastScore,
                            timeUsed = lastTimeUsed,
                            precisionError = lastPrecisionError,
                            isVictory = isVictory,
                            onPlayAgain = { currentScreen = TypeScreen.GAME },
                            onBackToMenu = { currentScreen = TypeScreen.WELCOME }
                        )
                    }
                }
            }
        }
    }
}