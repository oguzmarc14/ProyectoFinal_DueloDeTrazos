package com.duelodetrazos

import android.app.Application
import com.parse.Parse
import com.parse.ParseInstallation

class DueloDeTrazosApp : Application() {

    override fun onCreate() {
        super.onCreate()

        Parse.initialize(
            Parse.Configuration.Builder(this)
                .applicationId("A4lODRoPJmp1awuWdNXrVDMmMmtGGEjSwtsqwVjy")
                .clientKey("bL3c4PquKN6GfHelqKfI3jIb3lxHeRAuAnSvE1kJ")
                .server("https://parseapi.back4app.com/")
                .build()
        )

        ParseInstallation.getCurrentInstallation().saveInBackground()
    }
}
