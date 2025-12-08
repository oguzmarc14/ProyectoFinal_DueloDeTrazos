package com.duelodetrazos.network

import android.util.Log
import com.duelodetrazos.models.*
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.livequery.ParseLiveQueryClient
import com.parse.livequery.SubscriptionHandling
import java.net.URI
import java.util.Date

class LiveQueryManager private constructor() {

    companion object {
        private var INSTANCE: LiveQueryManager? = null

        fun getInstance(): LiveQueryManager {
            if (INSTANCE == null) INSTANCE = LiveQueryManager()
            return INSTANCE!!
        }

        private const val LIVEQUERY_URL =
            "wss://A4lODRoPJmp1awuWdNXrVDMmMmtGGEjSwtsqwVjy.back4app.io/"
    }

    private var client: ParseLiveQueryClient? = null
    private var subscription: SubscriptionHandling<ParseObject>? = null

    fun initClientIfNeeded() {
        if (client == null) {
            try {
                client = ParseLiveQueryClient.Factory.getClient(URI(LIVEQUERY_URL))
                Log.d("LiveQuery", "Cliente LiveQuery inicializado")
            } catch (e: Exception) {
                Log.e("LiveQuery", "Error inicializando LiveQueryClient", e)
            }
        }
    }

    fun subscribeToRoom(roomCode: String, listener: GameEventsListener) {
        initClientIfNeeded()

        val query = ParseQuery.getQuery<ParseObject>("GameEvent")
            .whereEqualTo("roomCode", roomCode)

        // ✅ ESTA ES LA LÍNEA CORRECTA
        subscription = client?.subscribe(query, ParseObject::class.java)

        subscription?.handleEvent(SubscriptionHandling.Event.CREATE) { _, obj ->
            try {
                val event = parseEvent(obj)
                dispatchEvent(event, listener)
            } catch (e: Exception) {
                listener.onError(e)
            }
        }

        subscription?.handleError { _, e ->
            listener.onError(e)
        }

        Log.d("LiveQuery", "Suscrito a sala: $roomCode")
    }

    fun unsubscribe() {
        try {
            subscription?.let { sub ->
                client?.unsubscribe(sub, ParseObject::class.java)
            }
            subscription = null
        } catch (_: Exception) {}
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

    private fun dispatchEvent(event: GameEvent, listener: GameEventsListener) {
        when (event.type) {
            GameEventType.SPAWN ->
                listener.onSpawn(
                    event,
                    GameEventPayload.Spawn(
                        event.payload["targetX"].toString().toFloat(),
                        event.payload["targetY"].toString().toFloat(),
                        event.payload["radius"].toString().toFloat(),
                        event.payload["round"].toString().toInt()
                    )
                )

            GameEventType.HIT ->
                listener.onHit(
                    event,
                    GameEventPayload.Hit(
                        event.playerId ?: "",
                        event.payload["hitX"].toString().toFloat(),
                        event.payload["hitY"].toString().toFloat(),
                        event.payload["hitTimeMs"].toString().toLong()
                    )
                )

            GameEventType.SCORE ->
                listener.onScore(
                    event,
                    GameEventPayload.Score(
                        event.payload["player1Score"].toString().toInt(),
                        event.payload["player2Score"].toString().toInt(),
                        event.payload["lastWinnerId"] as? String
                    )
                )

            GameEventType.END ->
                listener.onEnd(
                    event,
                    GameEventPayload.End(
                        event.payload["winnerId"] as? String,
                        event.payload["finalScoreP1"].toString().toInt(),
                        event.payload["finalScoreP2"].toString().toInt(),
                        event.payload["reason"].toString()
                    )
                )
        }
    }
}

interface GameEventsListener {
    fun onSpawn(event: GameEvent, payload: GameEventPayload.Spawn)
    fun onHit(event: GameEvent, payload: GameEventPayload.Hit)
    fun onScore(event: GameEvent, payload: GameEventPayload.Score)
    fun onEnd(event: GameEvent, payload: GameEventPayload.End)
    fun onError(e: Throwable)
}
