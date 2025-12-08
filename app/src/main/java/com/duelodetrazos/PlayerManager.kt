package com.duelodetrazos

import android.content.Context
import java.util.UUID

object PlayerManager {

    private const val PREFS_NAME = "duelo_prefs"
    private const val KEY_PLAYER_ID = "PLAYER_ID"

    fun getPlayerId(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val existing = prefs.getString(KEY_PLAYER_ID, null)

        if (existing != null) return existing

        val newId = UUID.randomUUID().toString()
        prefs.edit().putString(KEY_PLAYER_ID, newId).apply()
        return newId
    }
}
