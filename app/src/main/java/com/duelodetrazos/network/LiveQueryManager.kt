package com.duelodetrazos.network

import android.util.Log
import com.duelodetrazos.data.models.GameEvent
import com.duelodetrazos.data.models.GameEventType
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.livequery.ParseLiveQueryClient
import com.parse.livequery.SubscriptionHandling
import org.json.JSONObject
import java.net.URI
import java.util.Date

object LiveQueryManager {

    private var client: ParseLiveQueryClient? = null
    private var subscription: SubscriptionHandling<ParseObject>? = null
    private var query: ParseQuery<ParseObject>? = null

    var onSpawn: ((Float, Float) -> Unit)? = null
    var onHit: (() -> Unit)? = null
    var onScoreUpdate: ((Int, Int) -> Unit)? = null
    var onRoundUpdate: ((Int) -> Unit)? = null
    var onGameEnd: (() -> Unit)? = null
    var onError: ((Throwable) -> Unit)? = null

    fun sendEvent(roomId: String, type: String, data: JSONObject) {
        val event = ParseObject("GameEvent").apply {
            put("roomCode", roomId)
            put("type", type)
            put("payload", data)
        }
        event.saveInBackground()
    }

    fun connect(roomId: String) {
        // Inicialización manual del cliente (MÉTODO CLÁSICO)
        if (client == null) {
            try {
                client = ParseLiveQueryClient.Factory.getClient(URI("wss://A4lODRoPJmp1awuWdNXrVDMmMmtGGEjSwtsqwVjy.back4app.io/"))
            } catch (e: Exception) {
                onError?.invoke(e)
                return
            }
        }

        query = ParseQuery.getQuery<ParseObject>("GameEvent").whereEqualTo("roomCode", roomId)

        subscription = client?.subscribe(query)

        // Usar handleEvent (MÉTODO CLÁSICO)
        subscription?.handleEvent(SubscriptionHandling.Event.CREATE) { _, obj ->
            try {
                val event = parseEvent(obj)
                dispatchEvent(event)
            } catch (e: Exception) {
                onError?.invoke(e)
            }
        }

        subscription?.handleError { _, e ->
            onError?.invoke(e)
        }

        Log.d("LiveQuery", "Suscrito a la sala: $roomId")
    }

    fun disconnect() {
        query?.let { client?.unsubscribe(it) }
    }

    private fun parseEvent(obj: ParseObject): GameEvent {
        val payload = (obj.get("payload") as? Map<*, *>)?.mapNotNull { (k, v) ->
            (k as? String)?.let { it to v }
        }?.toMap() ?: emptyMap()

        return GameEvent(
            objectId = obj.objectId,
            roomCode = obj.getString("roomCode") ?: "",
            type = obj.getString("type") ?: "",
            playerId = obj.getString("playerId"),
            payload = payload,
            createdAt = obj.createdAt ?: Date()
        )
    }

    private fun dispatchEvent(event: GameEvent) {
        when (event.type) {
            GameEventType.SPAWN -> {
                val x = (event.payload["x"] as? Number)?.toFloat() ?: 0f
                val y = (event.payload["y"] as? Number)?.toFloat() ?: 0f
                onSpawn?.invoke(x, y)
            }
            GameEventType.HIT -> {
                onHit?.invoke()
            }
            GameEventType.SCORE -> {
                val p1 = (event.payload["player1Score"] as? Number)?.toInt() ?: 0
                val p2 = (event.payload["player2Score"] as? Number)?.toInt() ?: 0
                onScoreUpdate?.invoke(p1, p2)
            }
            GameEventType.END -> {
                onGameEnd?.invoke()
            }
        }
    }
}
