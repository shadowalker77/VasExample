package ir.ayantech.ayanvas.core

import android.app.Application
import com.batch.android.Batch
import com.batch.android.BatchActivityLifecycleHelper
import com.batch.android.Config
import ir.ayantech.ayanvas.R

class BatchHandler {
    companion object {
        fun initialize(application: Application) {
            Batch.setConfig(Config(application.resources.getString(R.string.batchAPIKey)))
            application.registerActivityLifecycleCallbacks(BatchActivityLifecycleHelper())
        }
    }
}