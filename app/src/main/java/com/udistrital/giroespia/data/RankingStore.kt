package com.udistrital.giroespia.data

import android.content.Context

/**
 * Ranking local muy simple, apropiado para el alcance del taller:
 * guarda los mejores puntajes en SharedPreferences (no requiere Internet,
 * ni cuentas, ni servidor). Solo conserva el top 5.
 */
object RankingStore {
    private const val PREFS_NAME = "giroespia_prefs"
    private const val KEY_SCORES = "top_scores"
    private const val MAX_ENTRIES = 5

    fun addScore(context: Context, score: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val current = getScores(context).toMutableList()
        current.add(score)
        val topScores = current.sortedDescending().take(MAX_ENTRIES)
        prefs.edit()
            .putString(KEY_SCORES, topScores.joinToString(","))
            .apply()
    }

    fun getScores(context: Context): List<Int> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val raw = prefs.getString(KEY_SCORES, "") ?: ""
        if (raw.isBlank()) return emptyList()
        return raw.split(",").mapNotNull { it.trim().toIntOrNull() }
    }
}
