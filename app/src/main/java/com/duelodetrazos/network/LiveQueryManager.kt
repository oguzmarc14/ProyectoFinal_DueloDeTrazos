package com.duelodetrazos.network

import android.util.Log
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import java.net.URI

object LiveQueryManager {

    private var client: WebSocketClient? = null

    fun connect(roomId: String, onStatusChange: (String) -> Unit) {
        val uri = URI("wss://A4lODRoPJmp1awuWdNXrVDMmMmtGGEjSwtsqwVjy.back4app.io/")

        client = object : WebSocketClient(uri) {

            override fun onOpen(handshakedata: ServerHandshake?) {
                Log.d("LiveQuery", "Connected")

                // 1️⃣ Mensaje CONNECT obligatorio
                val connect = JSONObject().apply {
                    put("op", "connect")
                    put("applicationId", "A4lODRoPJmp1awuWdNXrVDMmMmtGGEjSwtsqwVjy")
                    put("clientKey", "bL3c4PquKN6GfHelqKfI3jIb3lxHeRAuAnSvE1kJ")
                }
                send(connect.toString())

                // 2️⃣ Suscripción a cambios del objeto Room
                val subscribe = JSONObject().apply {
                    put("op", "subscribe")
                    put("requestId", 1)
                    put("query", JSONObject().apply {
                        put("className", "Room")
                        put("where", JSONObject().apply {
                            put("objectId", roomId)
                        })
                    })
                }
                send(subscribe.toString())
            }

            override fun onMessage(message: String?) {
                message ?: return

                try {
                    val json = JSONObject(message)

                    if (json.optString("op") == "update") {
                        val obj = json.getJSONObject("object")
                        val newStatus = obj.optString("status", "")
                        onStatusChange(newStatus)
                    }

                } catch (e: Exception) {
                    Log.e("LiveQuery", "Error parsing message: $message")
                }
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                Log.d("LiveQuery", "Closed: $reason")
            }

            override fun onError(ex: Exception?) {
                Log.e("LiveQuery", "Error: ${ex?.message}")
            }
        }

        client?.connect()
    }

    fun disconnect() {
        client?.close()
        client = null
    }
}
