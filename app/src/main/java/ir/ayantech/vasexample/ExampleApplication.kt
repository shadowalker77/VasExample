package ir.ayantech.vasexample

import android.app.Application
import ir.ayantech.ayanvas.core.AyanCore

class ExampleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AyanCore.initialize(this, "cheshmak1")
    }
}