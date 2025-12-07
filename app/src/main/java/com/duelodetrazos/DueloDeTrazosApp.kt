package com.duelodetrazos

import android.app.Application
import com.parse.Parse

class DueloDeTrazosApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // TODO: Reemplazar con tus credenciales reales de Back4App
        Parse.initialize(
            Parse.Configuration.Builder(this)
                .applicationId("AQUÍ_TU_APPLICATION_ID")
                .clientKey("AQUÍ_TU_CLIENT_KEY")
                .server("https://AQUÍ_TU_URL.back4app.io/")
                .build()
        )
    }
}
